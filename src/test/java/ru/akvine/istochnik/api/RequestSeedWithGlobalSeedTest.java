package ru.akvine.istochnik.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;

// В общих настройках тестов задан глобальный seed
@DisplayName("Request seed with global seed tests")
public class RequestSeedWithGlobalSeedTest extends ApiBaseTest {

    @Test
    @DisplayName("Global seed is more important than request seed")
    void global_seed_overrides_request_seed() {
        assertThat(generate("first")).isEqualTo(generate("second")).isEqualTo(generate(null));
    }

    private byte[] generate(String seed) {
        ColumnDto column = new ColumnDto()
                .setName("amount")
                .setType(BaseType.INTEGER.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.getType())
                        .setNotNull(true)
                        .setStart("0")
                        .setEnd("1000000000"));

        return sendGenerateRequest(new GenerateTableRequest()
                .setSize(30)
                .setFileType(FileType.CSV.name())
                .setSeed(seed)
                .setColumns(List.of(column)));
    }
}
