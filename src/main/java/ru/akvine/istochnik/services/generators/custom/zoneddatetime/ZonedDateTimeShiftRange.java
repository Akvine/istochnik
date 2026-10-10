package ru.akvine.istochnik.services.generators.custom.zoneddatetime;

import jakarta.annotation.Nullable;
import java.time.ZonedDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ZonedDateTimeShiftRange {
    /**
     * Зона значения start используется для всех генерируемых значений
     */
    private ZonedDateTime start;

    @Nullable
    private ZonedDateTime end;

    /**
     * Шаг в секундах
     */
    private long shiftCount;
}
