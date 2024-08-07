package com.app.pocsaga.payment;

import com.app.pocsaga.machine.PaymentEvents;
import com.app.pocsaga.machine.PaymentStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@Service
@Deprecated
public class PaymentServiceDeprecated {

    @Autowired
    private StateMachineFactory<PaymentStates, PaymentEvents> stateMachineFactory;

    public void processEvent(Payment payment, PaymentEvents event) {
        StateMachine<PaymentStates, PaymentEvents> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put("payment", payment);
        stateMachine.sendEvent(buildMessage(event, payment));
    }

    private Message<PaymentEvents> buildMessage(PaymentEvents event, Payment payment) {
        return MessageBuilder.withPayload(event)
                .setHeader("payment", payment)
                .build();
    }

    public void addStateToPayment(Payment payment, PaymentStates state) {
        payment.addState(state);
    }
}
