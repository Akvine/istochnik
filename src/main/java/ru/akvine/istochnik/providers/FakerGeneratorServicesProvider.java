package ru.akvine.istochnik.providers;

import java.util.Map;
import ru.akvine.istochnik.enums.Topic;
import ru.akvine.istochnik.services.FakerGeneratorService;

public record FakerGeneratorServicesProvider(Map<Topic, FakerGeneratorService> fakeGeneratorServices) {
    public FakerGeneratorService getByTopic(Topic topic) {
        if (fakeGeneratorServices.containsKey(topic)) {
            return fakeGeneratorServices.get(topic);
        }

        throw new UnsupportedOperationException("Topic = [" + topic + "] is not supported by app!");
    }
}
