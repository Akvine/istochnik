package ru.akvine.istochnik.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.controllers.dto.converters.ConverterDto;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.ConverterType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;

@DisplayName("String converters tests")
public class StringConvertersTest extends ApiBaseTest {
    private static final String SOURCE = "Hello World 123 Привет Мир";

    @Test
    @DisplayName("Apply INVERSION converter - case of every letter is inverted")
    void successful_apply_inversion() {
        List<String> result = generate(ConverterType.INVERSION, true);

        assertThat(result).containsOnly("hELLO wORLD 123 пРИВЕТ мИР");
    }

    @Test
    @DisplayName("Apply MD5 converter - successfully")
    void successful_apply_md5() {
        List<String> result = generate(ConverterType.MD5, true);

        // md5 от строки SOURCE в кодировке UTF-8
        assertThat(result).containsOnly("434721e420a8183c7d7012148c7bf5b7");
    }

    @Test
    @DisplayName("Apply RANDOM_CASE converter - only case of letters is changed")
    void successful_apply_random_case() {
        List<String> result = generate(ConverterType.RANDOM_CASE, true);

        assertThat(result).allSatisfy(value -> assertThat(value).isEqualToIgnoringCase(SOURCE));
        assertThat(result).anySatisfy(value -> assertThat(value).isNotEqualTo(SOURCE));
    }

    @Test
    @DisplayName("Apply REMOVE_WHITESPACES converter - all whitespaces are removed")
    void successful_apply_remove_whitespaces() {
        List<String> result = generate(ConverterType.REMOVE_WHITESPACES, true);

        assertThat(result).containsOnly("HelloWorld123ПриветМир");
    }

    @Test
    @DisplayName("Apply TRANSLIT converter - only russian letters are changed")
    void successful_apply_translit() {
        List<String> result = generate(ConverterType.TRANSLIT, true);

        assertThat(result).containsOnly("Hello World 123 Privet Mir");
    }

    @Test
    @DisplayName("Apply INVERSION, MD5 and RANDOM_CASE converters to nullable column - null values are kept")
    void successful_apply_converters_to_nullable_column() {
        for (ConverterType converterType :
                List.of(ConverterType.INVERSION, ConverterType.MD5, ConverterType.RANDOM_CASE)) {
            List<String> result = generate(converterType, false);

            assertThat(result).hasSize(20);
            // Пустая строка в CSV соответствует null
            assertThat(result).contains("");
        }
    }

    private List<String> generate(ConverterType converterType, boolean notNull) {
        ColumnDto column = new ColumnDto()
                .setName("column")
                .setType(BaseType.STRING.getValue())
                .setGenerationStrategy(GenerationStrategy.DICTIONARY.getName())
                .setConfig(new ConfigDto()
                        .setNotNull(notNull)
                        .setRangeType(RangeType.SHIFT.getType())
                        .setDictionaries(Set.of(Set.of(SOURCE))))
                .setConverters(List.of(new ConverterDto().setName(converterType.getName())));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(20)
                .setFileType(FileType.CSV.name())
                .setColumns(List.of(column));

        return convert(BaseType.STRING, sendGenerateRequest(request)).stream()
                .map(String.class::cast)
                .toList();
    }
}
