package com.app.pocsaga.payment;


import com.app.pocsaga.machine.PaymentEvents;
import com.app.pocsaga.machine.PaymentStates;
import com.app.pocsaga.payment.processor.PaymentProcessorImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentProcessorImpl paymentProcessor;

    public void processEvent(PaymentDto paymentDto) {
        log.info("Processing payment {}", paymentDto);

        Payment payment = PaymentDto.mapToPayment(paymentDto);

        Payment newPayment = paymentProcessor.newPayment(payment);

        log.info("Payment created with state: {} for payment {}", newPayment.getCurrentState(), newPayment);

        StateMachine<PaymentStates, PaymentEvents> stateMachinePay = paymentProcessor.pay(newPayment);
        logStateMachine(stateMachinePay);

        StateMachine<PaymentStates, PaymentEvents> stateMachineConfirm = paymentProcessor.confirm(newPayment);
        logStateMachine(stateMachineConfirm);

        StateMachine<PaymentStates, PaymentEvents> stateMachineComplete = paymentProcessor.complete(newPayment);
        logStateMachine(stateMachineComplete);
    }

    private void logStateMachine(StateMachine<PaymentStates, PaymentEvents> stateMachine) {
        if (stateMachine != null) {
            log.info("Current state: {}", stateMachine.getState().getId());
        } else {
            log.warn("State machine is null");
        }
    }
}






