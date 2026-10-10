package ru.akvine.istochnik.services.generators.custom.card;

import java.util.random.RandomGenerator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.PaymentSystem;
import ru.akvine.istochnik.services.generators.Config;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class CardNumberGeneratorConfig extends Config {
    private final boolean notNull;
    private final boolean unique;
    private final boolean valid;
    private final PaymentSystem paymentSystem;

    public CardNumberGeneratorConfig(
            int size,
            boolean notNull,
            boolean unique,
            boolean valid,
            PaymentSystem paymentSystem,
            RandomGenerator randomGenerator) {
        super(size, randomGenerator);
        this.notNull = notNull;
        this.unique = unique;
        this.valid = valid;
        this.paymentSystem = paymentSystem;
    }
}
