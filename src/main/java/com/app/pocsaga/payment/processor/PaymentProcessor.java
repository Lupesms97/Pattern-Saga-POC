package com.app.pocsaga.payment.processor;

import com.app.pocsaga.machine.PaymentEvents;
import com.app.pocsaga.machine.PaymentStates;
import com.app.pocsaga.payment.Payment;
import org.springframework.statemachine.StateMachine;

public interface PaymentProcessor {

    Payment newPayment(Payment payment);

    StateMachine<PaymentStates, PaymentEvents> pay(Payment payment);

    StateMachine<PaymentStates, PaymentEvents> confirm(Payment payment);

    StateMachine<PaymentStates, PaymentEvents> complete(Payment payment);

    StateMachine<PaymentStates, PaymentEvents> cancel(Payment payment);

    StateMachine<PaymentStates, PaymentEvents> error(Payment payment,StateMachine<PaymentStates, PaymentEvents> stateMachine);
}
