package com.app.pocsaga.machine;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
@Slf4j
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<PaymentStates, PaymentEvents> {

//    @Autowired
//    private PaymentService paymentService;


    @Override
    public void configure(StateMachineStateConfigurer<PaymentStates, PaymentEvents> states) throws Exception {
        states.withStates()
                .initial(PaymentStates.BANCOOB_STARTED)
                .states(EnumSet.allOf(PaymentStates.class))
                .end(PaymentStates.BANCOOB_COMPLETED)
                .end(PaymentStates.BANCOOB_CANCELLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentStates, PaymentEvents> transitions) throws Exception {
        transitions
                .withExternal()
                .source(PaymentStates.BANCOOB_STARTED).target(PaymentStates.BANCOOB_PAYED).event(PaymentEvents.BANCOOB_PAY)
                .and()
                .withExternal()
                .source(PaymentStates.BANCOOB_PAYED).target(PaymentStates.BANCOOB_CONFIRMED).event(PaymentEvents.BANCOOB_CONFIRM)
                .and()
                .withExternal()
                .source(PaymentStates.BANCOOB_CONFIRMED).target(PaymentStates.BANCOOB_COMPLETED).event(PaymentEvents.BANCOOB_COMPLETE)
                .and()
                .withExternal()
                .source(PaymentStates.BANCOOB_PAYED).target(PaymentStates.BANCOOB_ERROR).event(PaymentEvents.BANCOOB_ERROR)
                .and()
                .withExternal()
                .source(PaymentStates.BANCOOB_CONFIRMED).target(PaymentStates.BANCOOB_ERROR).event(PaymentEvents.BANCOOB_ERROR)
                .and()
                .withExternal()
                .source(PaymentStates.BANCOOB_ERROR).target(PaymentStates.BANCOOB_RETRYED).event(PaymentEvents.BANCOOB_RETRY)
                .and()
                .withExternal()
                .source(PaymentStates.BANCOOB_RETRYED).target(PaymentStates.BANCOOB_PAYED).event(PaymentEvents.BANCOOB_PAY)
                .and()
                .withExternal()
                .source(PaymentStates.BANCOOB_RETRYED).target(PaymentStates.BANCOOB_CONFIRMED).event(PaymentEvents.BANCOOB_CONFIRM)
                .and()
                .withExternal()
                .source(PaymentStates.BANCOOB_RETRYED).target(PaymentStates.BANCOOB_CANCELLED).event(PaymentEvents.BANCOOB_CANCEL);
    }


    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentStates, PaymentEvents> config) throws Exception {
        config.withConfiguration()
                .autoStartup(true);
        StateMachineListenerAdapter<PaymentStates, PaymentEvents> adapter = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<PaymentStates, PaymentEvents> from, State<PaymentStates, PaymentEvents> to) {
                log.info(String.format("State changed from %s to %s", from == null ? "none" : from.getId(), to.getId()));
            }

            @Override
            public void eventNotAccepted(Message<PaymentEvents> event) {
                log.warn("Event not accepted: " + event.getPayload());
            }
        };

        config.withConfiguration()
                .listener(adapter);
    }


//
//    @Bean
//    public IS2B is2b() {
//        return new IS2B();
//    }
//
//    @Bean
//    public BANCOOB bancoob() {
//        return new BANCOOB();
//    }
//
//    protected class BANCOOB {
//        public Action<PaymentStates, PaymentEvents> errorToCancelAction( ) {
//            return context -> {
//               Payment payment = (Payment) context.getMessageHeader("payment");
//
//                paymentService.addStateToPayment(payment, PaymentStates.BANCOOB_CANCELLED);
//
//               log.info("Canceling payment: " + payment);
//            };
//        }
//
//        public Action<PaymentStates, PaymentEvents> confirmToErrorAction( ) {
//            return context -> {
//                Payment payment = (Payment) context.getMessageHeader("payment");
//
//                paymentService.addStateToPayment(payment, PaymentStates.BANCOOB_ERROR);
//
//                log.info("Error on payment confirmation: " + payment);
//            };
//        }
//
//        public Action<PaymentStates, PaymentEvents> payToErrorAction( ) {
//            return context -> {
//                Payment payment = (Payment) context.getMessageHeader("payment");
//
//                paymentService.addStateToPayment(payment, PaymentStates.BANCOOB_ERROR);
//
//                log.info("Error on payment: " + payment);
//            };
//        }
//
//        public Action<PaymentStates, PaymentEvents> confirmToCompleteAction( ) {
//            return context -> {
//                Payment payment = (Payment) context.getMessageHeader("payment");
//
//                paymentService.addStateToPayment(payment, PaymentStates.BANCOOB_COMPLETED);
//
//                log.info("Payment confirmed: " + payment);
//            };
//        }
//
//        public Action<PaymentStates, PaymentEvents> payToConfirmAction( ) {
//            return context -> {
//                Payment payment = (Payment) context.getMessageHeader("payment");
//
//                paymentService.addStateToPayment(payment, PaymentStates.BANCOOB_CONFIRMED);
//
//                log.info("Payment payed: " + payment);
//            };
//        }
//
//        public Action<PaymentStates, PaymentEvents> startToPayAction( ) {
//            return context -> {
//
//
//                Payment payment = (Payment) context.getMessageHeader("payment");
//
//                paymentService.addStateToPayment(payment, PaymentStates.BANCOOB_PAYED);
//
//                log.info("Starting payment: " + payment);
//            };
//        }
//    }
//
//    protected class IS2B {
//        public Action<PaymentStates, PaymentEvents> errorToCancelAction( ) {
//            return context -> {
//                Payment payment = (Payment) context.getMessageHeader("payment");
//
//                paymentService.addStateToPayment(payment, PaymentStates.IS2B_CANCELLED);
//
//                log.info("Canceling payment: " + payment);
//            };
//        }
//
//        public Action<PaymentStates, PaymentEvents> confirmToErrorAction( ) {
//            return context -> {
//
//                Payment payment = (Payment) context.getMessageHeader("payment");
//
//                paymentService.addStateToPayment(payment, PaymentStates.IS2B_ERROR);
//
//                log.info("Error on payment confirmation: " + payment);
//            };
//        }
//
//        public Action<PaymentStates, PaymentEvents> payOnProviderToErrorAction( ) {
//            return context -> {
//
//                Payment payment = (Payment) context.getMessageHeader("payment");
//
//                paymentService.addStateToPayment(payment, PaymentStates.IS2B_ERROR);
//
//                log.info("Error on payment on provider: " + payment);
//            };
//        }
//
//        public Action<PaymentStates, PaymentEvents> payToErrorAction( ) {
//            return context -> {
//
//                    Payment payment = (Payment) context.getMessageHeader("payment");
//
//                    paymentService.addStateToPayment(payment, PaymentStates.IS2B_ERROR);
//
//                    log.info("Error on payment: " + payment);
//            };
//        }
//
//        public Action<PaymentStates, PaymentEvents> authorizeToErrorAction( ) {
//            return context -> {
//
//                    Payment payment = (Payment) context.getMessageHeader("payment");
//
//                    paymentService.addStateToPayment(payment, PaymentStates.IS2B_ERROR);
//
//                    log.info("Error on payment authorization: " + payment);
//            };
//        }
//
//        public Action<PaymentStates, PaymentEvents> confirmToCompleteAction( ) {
//            return context -> {
//
//                    Payment payment = (Payment) context.getMessageHeader("payment");
//
//                    paymentService.addStateToPayment(payment, PaymentStates.IS2B_COMPLETED);
//
//                    log.info("Payment confirmed: " + payment);
//            };
//        }
//
//        public Action<PaymentStates, PaymentEvents> payOnProviderToConfirmAction( ) {
//            return context -> {
//
//                        Payment payment = (Payment) context.getMessageHeader("payment");
//
//                        paymentService.addStateToPayment(payment, PaymentStates.IS2B_CONFIRMED);
//
//                        log.info("Payment on provider confirmed: " + payment);
//            };
//        }
//
//        public Action<PaymentStates, PaymentEvents> payToPayOnProviderAction( ) {
//            return context -> {
//
//            Payment payment = (Payment) context.getMessageHeader("payment");
//
//            paymentService.addStateToPayment(payment, PaymentStates.IS2B_PAYED_ON_PROVIDER);
//
//            log.info("Payment payed on provider: " + payment);
//            };
//        }
//
//        public Action<PaymentStates, PaymentEvents> authorizeToPayAction( ) {
//            return context -> {
//
//            Payment payment = (Payment) context.getMessageHeader("payment");
//
//            paymentService.addStateToPayment(payment, PaymentStates.IS2B_PAYED);
//
//            log.info("Payment authorized: " + payment);
//            };
//        }
//
//        public Action<PaymentStates, PaymentEvents> startToAuthorizeAction( ) {
//            return context -> {
//
//             Payment payment = (Payment) context.getMessageHeader("payment");
//
//             paymentService.addStateToPayment(payment, PaymentStates.IS2B_AUTORIZED);
//
//            log.info("Starting payment authorization: " + payment);
//            };
//        }
//    }

    @Bean
    public Guard<PaymentStates, PaymentEvents> onlyWorkingDays() {
        return context -> !EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(
                ((LocalDate) context.getMessage().getHeaders().get("day")).getDayOfWeek());
    }



}

