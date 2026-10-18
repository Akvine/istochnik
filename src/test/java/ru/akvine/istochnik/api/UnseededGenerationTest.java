package ru.akvine.istochnik.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;
import ru.akvine.istochnik.config.async.TaskExecutor;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;

// В общих настройках тестов задан глобальный seed и пул из одного потока. Здесь seed отключён,
// а размер пула не задан, как и в настройках приложения по умолчанию
@TestPropertySource(
        properties = {"randomizer.seed=", "parallel.executor.threads-count=0", "parallel.executor.queue-capacity=100"})
@DisplayName("Generation without seed tests")
public class UnseededGenerationTest extends ApiBaseTest {
    private static final int SIZE = 500;
    private static final Pattern UUID_VERSION_4 =
            Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");

    @Autowired
    private TaskExecutor taskExecutor;

    @Test
    @DisplayName("Threads count is not set - pool size equals to the count of CPU cores")
    void pool_size_equals_to_cpu_cores_count() {
        int cores = Runtime.getRuntime().availableProcessors();

        assertThat(taskExecutor.executor().getCorePoolSize()).isEqualTo(cores);
        assertThat(taskExecutor.executor().getMaximumPoolSize()).isEqualTo(cores);
    }

    @Test
    @DisplayName("Columns with the same settings in one request - every column has its own values")
    void successful_generate_different_values_in_same_columns() {
        List<List<String>> columns =
                generate(uuidColumn("first"), uuidColumn("second"), intColumn("third"), intColumn("fourth"));

        List<String> uuids = new ArrayList<>(columns.get(0));
        uuids.addAll(columns.get(1));
        assertThat(uuids).hasSize(SIZE * 2).doesNotHaveDuplicates().allMatch(value -> UUID_VERSION_4
                .matcher(value)
                .matches());
        assertThat(columns.get(2)).hasSize(SIZE).isNotEqualTo(columns.get(3));
    }

    @Test
    @DisplayName("Same request twice - values are different")
    void successful_generate_different_values_in_every_request() {
        assertThat(generate(uuidColumn("id"), intColumn("number")))
                .isNotEqualTo(generate(uuidColumn("id"), intColumn("number")));
    }

    @Test
    @DisplayName("Strategies with their own random values work without seed")
    void successful_generate_by_other_strategies() {
        ColumnDto faker = column("name", GenerationStrategy.FAKER)
                .setConfig(randomConfig().setLanguage("ru").setTopics(List.of("FULL_NAME")));
        ColumnDto regexp = column("code", GenerationStrategy.REGEXP)
                .setConfig(randomConfig().setRegexps(Set.of("[A-Z]{3}-[0-9]{4}")));
        ColumnDto dictionary = column("status", GenerationStrategy.DICTIONARY)
                .setConfig(randomConfig().setDictionaries(Set.of(Set.of("NEW", "ACTIVE", "BLOCKED"))));

        List<List<String>> columns = generate(faker, regexp, dictionary);

        assertThat(columns.get(0)).hasSize(SIZE).doesNotContain("");
        assertThat(columns.get(1)).allMatch(value -> value.matches("[A-Z]{3}-[0-9]{4}"));
        assertThat(columns.get(2)).containsOnly("NEW", "ACTIVE", "BLOCKED");
        // Случайные значения, а не одно и то же во всех строках
        assertThat(Set.copyOf(columns.get(0))).hasSizeGreaterThan(SIZE / 2);
        assertThat(Set.copyOf(columns.get(1))).hasSizeGreaterThan(SIZE / 2);
        assertThat(Set.copyOf(columns.get(2))).hasSize(3);
    }

    // Значения по колонкам: первый список - первая колонка запроса
    private List<List<String>> generate(ColumnDto... columns) {
        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(SIZE)
                .setFileType(FileType.CSV.name())
                .setColumns(List.of(columns));

        List<List<String>> values = new ArrayList<>();
        for (int i = 0; i < columns.length; ++i) {
            values.add(new ArrayList<>());
        }
        new String(sendGenerateRequest(request)).lines().skip(1).forEach(line -> {
            String[] cells = line.split(";", -1);
            for (int i = 0; i < columns.length; ++i) {
                values.get(i).add(cells[i]);
            }
        });

        return values;
    }

    private ColumnDto uuidColumn(String name) {
        return column(name, GenerationStrategy.ALGORITHM)
                .setType(CustomType.UUID.getName())
                .setConfig(new ConfigDto());
    }

    private ColumnDto intColumn(String name) {
        return column(name, GenerationStrategy.ALGORITHM)
                .setType(BaseType.INTEGER.getValue())
                .setConfig(randomConfig().setStart("0").setEnd("1000000000"));
    }

    private ColumnDto column(String name, GenerationStrategy strategy) {
        return new ColumnDto()
                .setName(name)
                .setType(BaseType.STRING.getValue())
                .setGenerationStrategy(strategy.getName());
    }

    private ConfigDto randomConfig() {
        return new ConfigDto().setRangeType(RangeType.RANDOM.getType()).setNotNull(true);
    }
}
