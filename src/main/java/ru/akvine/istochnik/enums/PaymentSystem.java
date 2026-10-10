package ru.akvine.istochnik.enums;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentSystem {
    MIR(List.of(new PrefixRange(2200, 2204))),
    VISA(List.of(new PrefixRange(4, 4))),
    // Диапазоны 51-55 и 2221-2720 содержат одинаковое количество номеров, поэтому выбираются равновероятно
    MASTERCARD(List.of(new PrefixRange(51, 55), new PrefixRange(2221, 2720)));

    private final List<PrefixRange> prefixRanges;

    /**
     * Диапазон первых цифр номера карты, обе границы включаются
     */
    public record PrefixRange(int start, int end) {}
}
