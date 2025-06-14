package ru.akvine.istochnik.controllers.dto.topics;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.compozit.commons.dto.SuccessfulResponse;

import java.util.List;

@Data
@Accessors(chain = true)
public class ListTopicsResponse extends SuccessfulResponse {
    private List<TopicDto> topics;
}
