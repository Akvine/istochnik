package ru.akvine.istochnik.controllers.dto.filters;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.compozit.commons.dto.SuccessfulResponse;

import java.util.List;

@Data
@Accessors(chain = true)
public class FiltersListResponse extends SuccessfulResponse {
    private List<FilterTypeDto> filters;
}
