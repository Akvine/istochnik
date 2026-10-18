package ru.akvine.istochnik.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;

// В общих настройках тестов задан глобальный seed, который важнее seed колонок. Здесь он отключён.
// Замер времени включён, чтобы убедиться, что запись в лог не влияет на результат
@TestPropertySource(
        properties = {
            "randomizer.seed=",
            "log.total.generation.data.benchmark.enabled=true",
            "log.column.generation.data.benchmark.enabled=true"
        })
@DisplayName("Column seed tests")
public class ColumnSeedTest extends ApiBaseTest {
    private static final int SIZE = 30;

    @Test
    @DisplayName("Generate with the same seed - values are the same in every request")
    void successful_generate_same_values_with_same_seed() {
        List<?> first = generate("orders");
        List<?> second = generate("orders");

        assertThat(first).hasSize(SIZE).isEqualTo(second);
        assertThat(isRandom(BaseType.INTEGER, first)).isTrue();
    }

    @Test
    @DisplayName("Generate with different seeds - values are different")
    void successful_generate_different_values_with_different_seeds() {
        assertThat(generate("orders")).isNotEqualTo(generate("payments"));
    }

    @Test
    @DisplayName("Generate without seed - values are different in every request")
    void successful_generate_different_values_without_seed() {
        assertThat(generate(null)).hasSize(SIZE).isNotEqualTo(generate(null));
        // Пустой seed - то же, что его отсутствие
        assertThat(generate(" ")).isNotEqualTo(generate(" "));
    }

    private List<?> generate(String seed) {
        ColumnDto column = new ColumnDto()
                .setName("column")
                .setType(BaseType.INTEGER.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.getType())
                        .setNotNull(true)
                        .setStart("0")
                        .setEnd("1000000")
                        .setSeed(seed));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(SIZE)
                .setFileType(FileType.CSV.name())
                .setColumns(List.of(column));

        return convert(BaseType.INTEGER, sendGenerateRequest(request));
    }
}
