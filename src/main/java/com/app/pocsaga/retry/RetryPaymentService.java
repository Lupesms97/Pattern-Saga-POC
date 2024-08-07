package com.app.pocsaga.retry;


import com.app.pocsaga.machine.PaymentStates;
import com.app.pocsaga.payment.Payment;
import com.app.pocsaga.payment.processor.PaymentProcessor;
import com.app.pocsaga.payment.processor.PaymentProcessorImpl;
import com.app.pocsaga.retry.processor.RetryPaymentProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RetryPaymentService {

    private final RetryPaymentProcessor retryPaymentProcessor;
    private final PaymentProcessor paymentProcessor;

//    NAO LEVA EM CONTA O STATUS QUE POSSAM ESTAR COM ERRO E JÀ FORAM PROCESSADOS
    public List<Payment> processRetry(PaymentStates state) {
        log.info("Processing retry payments");

        List<Payment> payments = retryPaymentProcessor.getPaymetns(state);

        if (state.equals(PaymentStates.BANCOOB_CONFIRMED)) {
            payments.forEach(paymentProcessor::pay);
        }

        if (state.equals(PaymentStates.BANCOOB_PAYED)) {
            payments.forEach(paymentProcessor::confirm);
        }

        return payments;
    }



}
