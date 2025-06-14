package ru.akvine.istochnik.controllers.dto.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.akvine.istochnik.controllers.dto.topics.ListTopicsResponse;
import ru.akvine.istochnik.controllers.dto.topics.TopicDto;
import ru.akvine.istochnik.enums.Language;
import ru.akvine.istochnik.enums.Topic;
import ru.akvine.istochnik.services.MessageResolverService;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class TopicsConverter {
    private final MessageResolverService messageResolverService;

    public ListTopicsResponse convertToListTopicsResponse(Language language) {
        return new ListTopicsResponse()
                .setTopics(Arrays.stream(Topic.values())
                        .map(topic -> buildTopicDto(topic, language))
                        .toList());
    }

    private TopicDto buildTopicDto(Topic topic, Language language) {
        return new TopicDto()
                .setName(topic.name().toLowerCase())
                .setDescription(messageResolverService.message(topic.getCode(), language));
    }
}
