package com.app.pocsaga.payment;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentDto( BigDecimal amount, String name) {

    static Payment mapToPayment(PaymentDto paymentDto) {
        String id = UUID.randomUUID().toString();
        return new Payment(id, paymentDto.amount, paymentDto.name, null, null, null);
    }
}
