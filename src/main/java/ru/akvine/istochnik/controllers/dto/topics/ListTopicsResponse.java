package ru.akvine.istochnik.controllers.dto.topics;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.controllers.dto.SuccessfulResponse;

@Data
@Accessors(chain = true)
public class ListTopicsResponse extends SuccessfulResponse {
    private List<TopicDto> topics;
}
