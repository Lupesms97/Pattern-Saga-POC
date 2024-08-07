package com.app.pocsaga.payment;


import com.app.pocsaga.machine.PaymentStates;
import com.app.pocsaga.payment.provider.ProviderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "payment")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {
    @Id
    private String id;
    private BigDecimal amount;
    private String name;
    private ProviderType provider;
    private List<String> statesPassed;
    private PaymentStates currentState;

    public void addState(PaymentStates state) {
        if (this.statesPassed == null) {
            this.statesPassed = new ArrayList<>();
        }
        List<String> newStatesPassed = new ArrayList<>(this.statesPassed);
        newStatesPassed.add(state.name());
        this.statesPassed = newStatesPassed;
    }
}
