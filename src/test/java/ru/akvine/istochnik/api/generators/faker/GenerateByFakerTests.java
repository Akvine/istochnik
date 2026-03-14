package ru.akvine.istochnik.api.generators.faker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.istochnik.api.ApiBaseTest;
import ru.akvine.istochnik.enums.*;

@DisplayName("Faker tests")
public class GenerateByFakerTests extends ApiBaseTest {
    @Test
    @DisplayName("Generate russian PHONE_NUMBER topic [notNull = true, rangeType = RANDOM] - successfully")
    void successful_generate_russian_by_phone_topic_without_null_random_values() {
        BaseType type = BaseType.STRING;

        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("int_column")
                .setType(type.getValue())
                .setGenerationStrategy(GenerationStrategy.FAKER.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.toString())
                        .setNotNull(true)
                        .setLanguage("ru")
                        .setSeed(SEED)
                        .setTopics(List.of(Topic.PHONE_NUMBER.name()))));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        List<String> expected = List.of(
                "8 (863) 786-40-27",
                "8 (485) 371-06-63",
                "8 (472) 906-11-42",
                "8 (861) 438-14-74",
                "8 (855) 141-72-84",
                "8 (383) 181-47-07",
                "8 (390) 432-48-08",
                "8 (492) 515-22-14",
                "8 (848) 771-28-27",
                "8 (395) 540-86-64");
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

        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("int_column")
                .setType(type.getValue())
                .setGenerationStrategy(GenerationStrategy.FAKER.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.toString())
                        .setNotNull(true)
                        .setSeed(SEED)
                        .setTopics(List.of(Topic.PHONE_NUMBER.name()))));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        List<String> expected = List.of(
                "(916) 364-0278",
                "(505) 771-0663",
                "(730) 669-0611",
                "(505) 228-4381",
                "(505) 282-1141",
                "(730) 234-7611",
                "(915) 876-3643",
                "(983) 808-9125",
                "(305) 721-4195",
                "(305) 268-2799");
        byte[] response = sendGenerateRequest(request);

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type, response));

        List<?> result = convert(type, response);
        assertThat(result).isEqualTo(expected);
    }
}
