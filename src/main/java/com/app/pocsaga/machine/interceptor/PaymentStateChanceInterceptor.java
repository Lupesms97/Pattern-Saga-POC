package com.app.pocsaga.machine.interceptor;

import com.app.pocsaga.machine.PaymentEvents;
import com.app.pocsaga.machine.PaymentStates;
import com.app.pocsaga.payment.Payment;
import com.app.pocsaga.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;


@RequiredArgsConstructor
@Component
@Slf4j
public class PaymentStateChanceInterceptor extends StateMachineInterceptorAdapter<PaymentStates, PaymentEvents>  {

    private final PaymentRepository paymentRepository;

        @Override
        public void preStateChange(State<PaymentStates, PaymentEvents> state, Message<PaymentEvents> message, Transition<PaymentStates, PaymentEvents> transition, StateMachine<PaymentStates, PaymentEvents> stateMachine, StateMachine<PaymentStates, PaymentEvents> rootStateMachine) {
            Optional.ofNullable(message).flatMap(msg -> Optional.ofNullable((Payment) msg.getHeaders().get("payment"))).ifPresent(payment -> {
                payment.addState(state.getId());
                payment.setCurrentState(state.getId());
                paymentRepository.save(payment);
                log.info("Saved payment state: {} for payment: {}", state.getId(), payment);
            });
        }
    }



