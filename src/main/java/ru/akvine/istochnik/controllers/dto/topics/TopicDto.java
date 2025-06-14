package ru.akvine.istochnik.controllers.dto.topics;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TopicDto {
    private String name;
    private String description;
}
