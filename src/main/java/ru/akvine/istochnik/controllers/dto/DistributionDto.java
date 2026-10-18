package ru.akvine.istochnik.controllers.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DistributionDto {
    private String type;

    private String mean;

    private String stddev;

    private String exponent;

    private String peak;
}
