package com.app.pocsaga.api;

import com.app.pocsaga.machine.PaymentStates;
import com.app.pocsaga.payment.Payment;
import com.app.pocsaga.payment.PaymentDto;
import com.app.pocsaga.payment.PaymentService;
import com.app.pocsaga.retry.RetryPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentApi {

    private final PaymentService paymentService;
    private final RetryPaymentService retryPaymentService;

    @PostMapping
    public ResponseEntity<?> pay(@RequestParam String value, @RequestParam String name) {

        PaymentDto dto = new PaymentDto(new BigDecimal(value), name);

        log.info("Processing payment {}", dto);

        paymentService.processEvent(dto);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Payment>> retry(@RequestParam String stateString) {

        log.info("Processing retry payments");

        PaymentStates state = PaymentStates.valueOf(stateString);

        List<Payment> payments = retryPaymentService.processRetry(state);

        return ResponseEntity.ok(payments);
    }
}
