package com.app.pocsaga.retry.processor;


import com.app.pocsaga.machine.PaymentEvents;
import com.app.pocsaga.machine.PaymentStates;
import com.app.pocsaga.machine.interceptor.PaymentStateChanceInterceptor;
import com.app.pocsaga.payment.Payment;
import com.app.pocsaga.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
@Slf4j
public class RetryPaymentProcessorImpl implements RetryPaymentProcessor {

    private final PaymentRepository paymentRepository;
    private final StateMachineFactory<PaymentStates, PaymentEvents> stateMachineFactory;
    private final PaymentStateChanceInterceptor paymentStateChanceInterceptor;

    @Override
    public List<Payment> getPaymetns(PaymentStates state) {
        List<String> stateToExclude = List.of(state.name());
        return paymentRepository.findPaymentByCurrentStateAndStatesPassedIsNotContainingIgnoreCase(PaymentStates.BANCOOB_ERROR, stateToExclude);
    }


    @Transactional
    @Override
    public StateMachine<PaymentStates, PaymentEvents> erroToRetry(Payment payment) {
        StateMachine<PaymentStates, PaymentEvents> stateMachine = buildBlock(payment);

        try {
            boolean result = sendEvent(payment, stateMachine, PaymentEvents.BANCOOB_RETRY);
            log.info("Confirm event sent: {}, result: {}, current state: {}", PaymentEvents.BANCOOB_RETRY, result, stateMachine.getState().getId());
        } catch (Exception e) {
            log.error("Error during confirm event: {}", e.getMessage());
            errorToCancel(payment, stateMachine);
        }

        return stateMachine;
    }

    @Transactional
    @Override
    public StateMachine<PaymentStates, PaymentEvents> retryToConfirm(Payment payment) {
        StateMachine<PaymentStates, PaymentEvents> stateMachine = buildBlock(payment);
//
//        try {
//            boolean result = sendEvent(payment, stateMachine, PaymentEvents.BANCOOB_COMPLETE);
//            log.info("Complete event sent: {}, result: {}, current state: {}", PaymentEvents.BANCOOB_COMPLETE, result, stateMachine.getState().getId());
//        } catch (Exception e) {
//            log.error("Error during complete event: {}", e.getMessage());
//            errorToCancel(payment, stateMachine);
//        }

        Random random = new Random();

        int rand = random.nextInt();

        if (rand%2 == 0) {
            sendEvent(payment, stateMachine, PaymentEvents.BANCOOB_CONFIRM);
        } else {
            errorToCancel(payment, stateMachine);
        }

        return stateMachine;
    }

    @Transactional
    @Override
    public StateMachine<PaymentStates, PaymentEvents> retryToPay(Payment payment) {
        StateMachine<PaymentStates, PaymentEvents> stateMachine = buildBlock(payment);

        sendEvent(payment, stateMachine, PaymentEvents.BANCOOB_PAY);
        return null;
    }

    @Transactional
    @Override
    public StateMachine<PaymentStates, PaymentEvents> errorToCancel(Payment payment,
                                                                    StateMachine<PaymentStates, PaymentEvents> state
    ) {
        sendEvent(payment, state, PaymentEvents.BANCOOB_CANCEL);
        return null;
    }

    private boolean sendEvent(Payment payment, StateMachine<PaymentStates, PaymentEvents> stateMachine, PaymentEvents event) {
        Message<PaymentEvents> message = MessageBuilder.withPayload(event)
                .setHeader("payment", payment)
                .build();

        return stateMachine.sendEvent(message);
    }

    private Mono<StateMachine<PaymentStates, PaymentEvents>> build(Payment payment) {
        StateMachine<PaymentStates, PaymentEvents> stateMachine = stateMachineFactory.getStateMachine(payment.getId());

        return stateMachine.stopReactively()
                .then(Mono.fromRunnable(() -> {
                    stateMachine.getStateMachineAccessor()
                            .doWithAllRegions(
                                    stateMachineAccessor -> {
                                        stateMachineAccessor.addStateMachineInterceptor(paymentStateChanceInterceptor);
                                        stateMachineAccessor.resetStateMachineReactively(
                                        new DefaultStateMachineContext<>(payment.getCurrentState(), null, null, null)
                                ).subscribe();
                            });
                }))
                .then(stateMachine.startReactively())
                .then(Mono.just(stateMachine));
    }

    private StateMachine<PaymentStates, PaymentEvents> buildBlock(Payment payment) {
        StateMachine<PaymentStates, PaymentEvents> stateMachine = stateMachineFactory.getStateMachine(payment.getId());

        stateMachine.stopReactively().subscribe();

        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(stateMachineAccessor -> {
                    stateMachineAccessor.addStateMachineInterceptor(paymentStateChanceInterceptor);
                    stateMachineAccessor.resetStateMachineReactively(
                            new DefaultStateMachineContext<>(payment.getCurrentState(), null, null, null)
                    ).subscribe();
                });

        stateMachine.startReactively().subscribe();

        return stateMachine;
    }
}
