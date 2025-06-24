package ru.akvine.istochnik.api.generators.dictionary;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.istochnik.api.ApiBaseTest;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@DisplayName("Dictionary strategy tests")
public class GenerateByDictionaryTests extends ApiBaseTest {

    @Test
    @DisplayName("Generate dictionary values randomly")
    void successful_generate_random_dictionary_values() {
        int size = 10;
        Set<Set<String>> singleDictionary = Set.of(Set.of(
                "Value 1",
                "Value 2",
                "Value 3",
                "Value 4"
        ));
        List<ColumnDto> columnsToGenerate = List.of(
                new ColumnDto()
                        .setName("string_column")
                        .setType(BaseType.STRING.getValue())
                        .setGenerationStrategy(GenerationStrategy.DICTIONARY.getName())
                        .setConfig(new ConfigDto()
                                .setDictionaries(singleDictionary)
                        )
        );

        List<String> expected = List.of(
                "Value 3", "Value 4", "Value 1", "Value 2", "Value 3",
                "Value 4", "Value 1", "Value 2", "Value 3", "Value 4"
        );
        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(size)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);
        byte[] response = sendGenerateRequest(request);

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(BaseType.STRING, response));

        List<?> result = convert(BaseType.STRING, response);

        assertThat(result).isEqualTo(expected);
    }


    @Test
    @DisplayName("Generate dictionary values shiftly")
    void successful_generate_shifted_dictionary_values() {
        int size = 10;
        Set<Set<String>> singleDictionary = Set.of(Set.of(
                "Value 1",
                "Value 2",
                "Value 3",
                "Value 4",
                "Value 5"
        ));
        List<ColumnDto> columnsToGenerate = List.of(
                new ColumnDto()
                        .setName("string_column")
                        .setType(BaseType.STRING.getValue())
                        .setGenerationStrategy(GenerationStrategy.DICTIONARY.getName())
                        .setConfig(new ConfigDto()
                                .setDictionaries(singleDictionary)
                                .setRangeType(RangeType.SHIFT.getType())
                        )
        );

        List<String> expected = List.of(
                "Value 5", "Value 3", "Value 4", "Value 1", "Value 2",
                "Value 5", "Value 3", "Value 4", "Value 1", "Value 2"
        );
        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(size)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);
        byte[] response = sendGenerateRequest(request);

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(BaseType.STRING, response));

        List<?> result = convert(BaseType.STRING, response);

        assertThat(result).isEqualTo(expected);
    }
}
