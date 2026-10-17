package ru.akvine.istochnik.config.limits;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

@DisplayName("Generation limits calculation settings tests")
class GenerationLimitsCalculationPropertiesTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    ConfigurationPropertiesAutoConfiguration.class, ValidationAutoConfiguration.class))
            .withUserConfiguration(GenerationLimitsCalculationProperties.class);

    @ParameterizedTest(name = "{0}")
    @ValueSource(
            strings = {
                "max.generation.calculation.memory-share=0",
                "max.generation.calculation.memory-share=1.5",
                "max.generation.calculation.bytes-per-cell=0",
                "max.generation.calculation.xlsx-bytes-per-cell=-1",
                "max.generation.calculation.users=0"
            })
    @DisplayName("Invalid setting - application doesn't start")
    void fail_start_with_invalid_setting(String setting) {
        contextRunner.withPropertyValues(setting).run(context -> assertThat(context)
                .hasFailed());
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"max.generation.calculation.memory-share=1.0", "max.generation.calculation.users=8"})
    @DisplayName("Valid setting - application starts")
    void successful_start_with_valid_setting(String setting) {
        contextRunner.withPropertyValues(setting).run(context -> assertThat(context)
                .hasNotFailed());
    }
}
