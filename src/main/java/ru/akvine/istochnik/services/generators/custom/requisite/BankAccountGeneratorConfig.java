package ru.akvine.istochnik.services.generators.custom.requisite;

import jakarta.annotation.Nullable;
import java.util.random.RandomGenerator;
import lombok.Getter;

@Getter
public class BankAccountGeneratorConfig extends RequisiteGeneratorConfig {
    /**
     * БИК банка, под который рассчитывается контрольный ключ счёта
     */
    @Nullable
    private final String bik;

    public BankAccountGeneratorConfig(
            int size, boolean notNull, boolean unique, boolean valid, String bik, RandomGenerator randomGenerator) {
        super(size, notNull, unique, valid, randomGenerator);
        this.bik = bik;
    }
}
