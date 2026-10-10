package ru.akvine.istochnik.services.generators.custom.card;

import java.util.random.RandomGenerator;
import lombok.Getter;
import ru.akvine.istochnik.enums.PaymentSystem;
import ru.akvine.istochnik.services.generators.custom.requisite.RequisiteGeneratorConfig;

@Getter
public class CardNumberGeneratorConfig extends RequisiteGeneratorConfig {
    private final PaymentSystem paymentSystem;

    public CardNumberGeneratorConfig(
            int size,
            boolean notNull,
            boolean unique,
            boolean valid,
            PaymentSystem paymentSystem,
            RandomGenerator randomGenerator) {
        super(size, notNull, unique, valid, randomGenerator);
        this.paymentSystem = paymentSystem;
    }
}
