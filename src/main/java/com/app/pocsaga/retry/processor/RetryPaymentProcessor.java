package com.app.pocsaga.retry.processor;

import com.app.pocsaga.machine.PaymentEvents;
import com.app.pocsaga.machine.PaymentStates;
import com.app.pocsaga.payment.Payment;
import org.springframework.statemachine.StateMachine;

import java.util.List;

public interface RetryPaymentProcessor {

    List<Payment> getPaymetns(PaymentStates state);

    StateMachine<PaymentStates, PaymentEvents> errorToCancel(Payment payment, StateMachine<PaymentStates, PaymentEvents> state );

    StateMachine<PaymentStates, PaymentEvents> erroToRetry(Payment payment );

    StateMachine<PaymentStates, PaymentEvents> retryToConfirm(Payment payment );

    StateMachine<PaymentStates, PaymentEvents> retryToPay(Payment payment );

}
