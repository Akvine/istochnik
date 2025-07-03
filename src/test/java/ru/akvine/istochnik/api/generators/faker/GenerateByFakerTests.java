package ru.akvine.istochnik.api.generators.faker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.istochnik.api.ApiBaseTest;
import ru.akvine.istochnik.enums.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@DisplayName("Faker tests")
public class GenerateByFakerTests extends ApiBaseTest {
    @Test
    @DisplayName("Generate russian PHONE_NUMBER topic [notNull = true, rangeType = RANDOM] - successfully")
    void successful_generate_russian_by_phone_topic_without_null_random_values() {
        BaseType type = BaseType.STRING;

        List<ColumnDto> columnsToGenerate = List.of(
                new ColumnDto()
                        .setName("int_column")
                        .setType(type.getValue())
                        .setGenerationStrategy(GenerationStrategy.FAKER.getName())
                        .setConfig(new ConfigDto()
                                .setRangeType(RangeType.RANDOM.toString())
                                .setNotNull(true)
                                .setLanguage("ru")
                                .setTopics(List.of(Topic.PHONE_NUMBER.name()))
                        )
        );

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        List<String> expected = List.of(
                "8 (381) 753-11-94", "8 (835) 254-45-10", "8 (401) 472-03-22",
                "8 (815) 772-25-38", "8 (352) 508-14-62", "8 (347) 667-68-73",
                "8 (493) 588-71-88", "8 (352) 716-07-25", "8 (415) 795-29-31",
                "8 (471) 846-37-25");
        byte[] response = sendGenerateRequest(request);

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type, response));

        List<?> result = convert(type, response);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Generate PHONE_NUMBER topic [notNull = true, rangeType = RANDOM] - successfully")
    void successful_generate_by_phone_topic_without_null_random_values() {
        BaseType type = BaseType.STRING;

        List<ColumnDto> columnsToGenerate = List.of(
                new ColumnDto()
                        .setName("int_column")
                        .setType(type.getValue())
                        .setGenerationStrategy(GenerationStrategy.FAKER.getName())
                        .setConfig(new ConfigDto()
                                .setRangeType(RangeType.RANDOM.toString())
                                .setNotNull(true)
                                .setTopics(List.of(Topic.PHONE_NUMBER.name()))
                        )
        );

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        List<String> expected = List.of(
                "(505) 625-3119",
                "(480) 654-4510",
                "(730) 647-2032",
                "(730) 205-7722",
                "(730) 235-0550",
                "(472) 462-2976",
                "(305) 287-3778",
                "(730) 687-1888",
                "(786) 407-2507",
                "(305) 212-9311");
        byte[] response = sendGenerateRequest(request);

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type, response));

        List<?> result = convert(type, response);
        assertThat(result).isEqualTo(expected);
    }
}
