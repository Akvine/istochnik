package ru.akvine.istochnik.validators.strategy;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.istochnik.controllers.dto.validation.ValidationColumnsInfo;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.Topic;
import ru.akvine.istochnik.providers.BaseTypeValidatorsProvider;

@Component
public class FakerStrategyValidator extends AbstractGenerationStrategyValidator {

    @Value("${max.faker.text.length}")
    private int maxTextLength;

    @Autowired
    public FakerStrategyValidator(BaseTypeValidatorsProvider baseTypeValidatorsProvider) {
        super(baseTypeValidatorsProvider);
    }

    @Override
    public void validate(ValidationColumnsInfo validationColumnsInfo, ColumnDto column, int rowsCount) {
        String columnName = column.getName();

        ConfigDto config = column.getConfig();
        if (config == null) {
            validationColumnsInfo.put(columnName, ErrorMessages.CONFIG_IS_NULL_ERROR);
            return;
        }

        List<String> topicNames = config.getTopics();
        if (CollectionUtils.isEmpty(topicNames)) {
            validationColumnsInfo.put(columnName, ErrorMessages.TOPICS_ARE_EMPTY_ERROR);
            return;
        }

        Set<Topic> topics = EnumSet.noneOf(Topic.class);
        for (String topicName : topicNames) {
            try {
                topics.add(Topic.safeFrom(topicName));
            } catch (IllegalArgumentException | UnsupportedOperationException exception) {
                validationColumnsInfo.put(columnName, exception.getMessage());
            }
        }

        // Длина нужна только тексту, остальные топики её не используют
        Integer length = config.getLength();
        if (topics.contains(Topic.TEXT) && length != null && (length <= 0 || length > maxTextLength)) {
            validationColumnsInfo.put(
                    columnName, String.format(ErrorMessages.TEXT_LENGTH_ERROR, Topic.TEXT, maxTextLength));
        }
    }

    @Override
    public GenerationStrategy getStrategy() {
        return GenerationStrategy.FAKER;
    }

    interface ErrorMessages {
        String CONFIG_IS_NULL_ERROR = "field [config] can't be null";
        String TOPICS_ARE_EMPTY_ERROR = "field [topics] can't be empty";
        String TEXT_LENGTH_ERROR = "field [length] for topic [%s] must be between 1 and max = [%s]";
    }
}
