package ru.akvine.istochnik.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.compozit.commons.istochnik.ConverterDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.istochnik.enums.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@DisplayName("Converters tests")
public class ConvertersTest extends ApiBaseTest {
    @Test
    @DisplayName("Apply ABS converter to INTEGER - successfully")
    void successful_apply_abs_at_random_int_values() {
        BaseType type = BaseType.INTEGER;

        List<ColumnDto> columnsToGenerate = List.of(
                new ColumnDto()
                        .setName("int_column")
                        .setType(type.getValue())
                        .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                        .setConfig(new ConfigDto()
                                .setRangeType(RangeType.RANDOM.toString())
                                .setStart("-10")
                                .setNotNull(true)
                                .setEnd("0")
                        )
                        .setConverters(List.of(
                                new ConverterDto()
                                        .setName(ConverterType.ABS.getName())
                        ))
        );

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = sendGenerateRequest(request);

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type, response));

        List<?> result = convert(type, response);
        List<Long> casted = result.stream()
                .map(Long.class::cast)
                .toList();
        assertThat(casted.stream().allMatch(value -> value >= 0)).isTrue();
        assertThat(isRandom(type, result)).isTrue();
    }

    @Test
    @DisplayName("Apply DIVIDE converter to INTEGER - successfully")
    void successful_apply_divide_at_int_values() {
        BaseType type = BaseType.INTEGER;

        List<ColumnDto> columnsToGenerate = List.of(
                new ColumnDto()
                        .setName("int_column")
                        .setType(type.getValue())
                        .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                        .setConfig(new ConfigDto()
                                .setRangeType(RangeType.RANDOM.toString())
                                .setStart("0")
                                .setNotNull(true)
                                .setEnd("10")
                        )
                        .setConverters(List.of(
                                new ConverterDto()
                                        .setName(ConverterType.DIVIDE.getName())
                                        .setArguments(new Object[]{"2.0D"})
                        ))
        );

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        List<Long> expected = List.of(0L, 1L, 1L, 2L, 0L, 3L, 0L, 2L, 4L, 3L);
        byte[] response = sendGenerateRequest(request);

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type, response));

        List<?> result = convert(type, response);
        assertThat(result).isEqualTo(expected);
        assertThat(isRandom(type, result)).isTrue();
    }

}
