package com.app.pocsaga.payment.processor;

import com.app.pocsaga.payment.Payment;
import com.app.pocsaga.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentProcessorImplTest {

    @Autowired
    PaymentProcessorImpl paymentProcessor;
    @Autowired
    PaymentRepository paymentRepository;

    Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder()
                .amount(BigDecimal.valueOf(100))
                .name("PAGAMENTO DE TESTE")
                .build();
    }

    @Test
    void pay() {
        Payment savedPayment = paymentProcessor.newPayment(payment);

        paymentProcessor.pay(savedPayment);

        Payment paymentResult = paymentRepository.findById(savedPayment.getId()).orElse(null);

        assertEquals(1, savedPayment.getStatesPassed().size());
        System.out.println(savedPayment.getStatesPassed());
        assertNotNull(paymentResult);
    }

    @Test
    void confirm() {
    }

    @Test
    void complete() {
    }

    @Test
    void cancel() {
    }
}