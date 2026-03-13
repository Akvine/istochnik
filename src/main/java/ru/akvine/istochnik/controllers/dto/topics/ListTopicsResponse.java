package ru.akvine.istochnik.controllers.dto.topics;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.compozit.commons.dto.SuccessfulResponse;

@Data
@Accessors(chain = true)
public class ListTopicsResponse extends SuccessfulResponse {
    private List<TopicDto> topics;
}
