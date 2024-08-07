package com.app.pocsaga.payment.repository;

import com.app.pocsaga.machine.PaymentStates;
import com.app.pocsaga.payment.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    List<Payment> findPaymentByCurrentState(String currentState);

    List<Payment> findPaymentByCurrentStateAndStatesPassedIsNotContainingIgnoreCase(PaymentStates currentState, List<String> statesPassed);
}
