package ru.akvine.istochnik.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;

// В общих настройках тестов задан глобальный seed, который важнее seed запроса, и пул из одного потока.
// Здесь глобальный seed отключён, а пул вмещает запросы из нескольких колонок
@TestPropertySource(
        properties = {"randomizer.seed=", "parallel.executor.threads-count=4", "parallel.executor.queue-capacity=100"})
@DisplayName("Request seed tests")
public class RequestSeedTest extends ApiBaseTest {
    private static final int SIZE = 50;

    @Test
    @DisplayName("Same request seed - the same file for columns of every strategy")
    void successful_generate_same_file_with_same_seed() {
        GenerateTableRequest request = request("orders-2025", allStrategies());

        String first = generate(request);

        assertThat(generate(request)).isEqualTo(first);
        assertThat(first.lines()).hasSize(SIZE + 1);
    }

    @Test
    @DisplayName("Different request seeds or no seed - different files")
    void successful_generate_different_files() {
        assertThat(generate(request("first", allStrategies())))
                .isNotEqualTo(generate(request("second", allStrategies())));
        assertThat(generate(request(null, allStrategies()))).isNotEqualTo(generate(request(null, allStrategies())));
        // Пустой seed - то же, что его отсутствие
        assertThat(generate(request(" ", allStrategies()))).isNotEqualTo(generate(request(" ", allStrategies())));
    }

    @Test
    @DisplayName("Columns with the same settings get different values")
    void columns_with_same_settings_differ() {
        Map<String, List<String>> columns = columns(
                generate(request("seed", List.of(uuid("first"), uuid("second"), integer("third"), integer("fourth")))));

        assertThat(columns.get("first")).isNotEqualTo(columns.get("second"));
        assertThat(columns.get("third")).isNotEqualTo(columns.get("fourth"));
    }

    @Test
    @DisplayName("Column values don't depend on other columns of the request")
    void column_values_do_not_depend_on_other_columns() {
        Map<String, List<String>> original = columns(generate(request("seed", List.of(uuid("id"), integer("amount")))));
        // Колонки переставлены, и добавлена новая
        Map<String, List<String>> changed =
                columns(generate(request("seed", List.of(integer("added"), integer("amount"), uuid("id")))));

        assertThat(changed.get("id")).isEqualTo(original.get("id"));
        assertThat(changed.get("amount")).isEqualTo(original.get("amount"));
    }

    @Test
    @DisplayName("Columns with the same name get different values")
    void columns_with_same_name_differ() {
        String file = generate(request("seed", List.of(uuid("id"), uuid("id"))));

        List<String> first = new ArrayList<>();
        List<String> second = new ArrayList<>();
        file.lines().skip(1).forEach(line -> {
            String[] cells = line.split(";");
            first.add(cells[0]);
            second.add(cells[1]);
        });
        assertThat(first).isNotEqualTo(second);
    }

    @Test
    @DisplayName("Column seed is more important than request seed")
    void column_seed_overrides_request_seed() {
        ColumnDto ownSeed = integer("own");
        ownSeed.getConfig().setSeed("column-seed");

        List<String> alone = columns(generate(request(null, List.of(ownSeed)))).get("own");
        List<String> withRequestSeed = columns(generate(request("request-seed", List.of(ownSeed, integer("other")))))
                .get("own");
        List<String> withAnotherRequestSeed =
                columns(generate(request("another-seed", List.of(ownSeed)))).get("own");

        assertThat(withRequestSeed).isEqualTo(alone).isEqualTo(withAnotherRequestSeed);
    }

    // Значения по колонкам: имя колонки - её значения по строкам
    private Map<String, List<String>> columns(String file) {
        List<String> lines = file.lines().toList();
        String[] names = lines.get(0).split(";");

        Map<String, List<String>> columns = new LinkedHashMap<>();
        for (String name : names) {
            columns.put(name, new ArrayList<>());
        }
        lines.stream().skip(1).forEach(line -> {
            String[] cells = line.split(";", -1);
            for (int i = 0; i < names.length; ++i) {
                columns.get(names[i]).add(cells[i]);
            }
        });

        return columns;
    }

    private List<ColumnDto> allStrategies() {
        return List.of(
                uuid("id"),
                integer("amount"),
                new ColumnDto()
                        .setName("customer")
                        .setType(BaseType.STRING.getValue())
                        .setGenerationStrategy(GenerationStrategy.FAKER.getName())
                        .setConfig(randomConfig().setLanguage("ru").setTopics(List.of("FULL_NAME"))),
                new ColumnDto()
                        .setName("code")
                        .setType(BaseType.STRING.getValue())
                        .setGenerationStrategy(GenerationStrategy.REGEXP.getName())
                        .setConfig(randomConfig().setRegexps(Set.of("[A-Z]{3}-[0-9]{4}"))),
                new ColumnDto()
                        .setName("status")
                        .setType(BaseType.STRING.getValue())
                        .setGenerationStrategy(GenerationStrategy.DICTIONARY.getName())
                        .setConfig(randomConfig()
                                .setDictionaries(Set.of(Set.of("NEW", "ACTIVE", "BLOCKED")))
                                .setWeights(Map.of("NEW", 5.0))),
                new ColumnDto()
                        .setName("created_on")
                        .setType(CustomType.DATE.getName())
                        .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                        .setConfig(randomConfig().setStart("2025-01-01").setEnd("2025-12-31")));
    }

    private ColumnDto uuid(String name) {
        return new ColumnDto()
                .setName(name)
                .setType(CustomType.UUID.getName())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto());
    }

    private ColumnDto integer(String name) {
        return new ColumnDto()
                .setName(name)
                .setType(BaseType.INTEGER.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(randomConfig().setStart("0").setEnd("1000000000"));
    }

    private ConfigDto randomConfig() {
        return new ConfigDto().setRangeType(RangeType.RANDOM.getType()).setNotNull(true);
    }

    private GenerateTableRequest request(String seed, List<ColumnDto> columns) {
        return new GenerateTableRequest()
                .setSize(SIZE)
                .setFileType(FileType.CSV.name())
                .setSeed(seed)
                .setColumns(columns);
    }

    private String generate(GenerateTableRequest request) {
        return new String(sendGenerateRequest(request), StandardCharsets.UTF_8);
    }
}
