package ru.akvine.istochnik.services.limits;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.akvine.istochnik.config.limits.GenerationLimitsCalculationProperties;

@DisplayName("Generation limits calculator tests")
class GenerationLimitsCalculatorTest {
    private static final long GIGABYTE = 1024L * 1024 * 1024;

    private final GenerationLimitsCalculator calculator = new GenerationLimitsCalculator();

    @Test
    @DisplayName("Cells are limited by memory, columns by executor, rows by cells")
    void calculate_limits_for_one_user() {
        GenerationLimits limits = calculator.calculate(GIGABYTE, 1012, settings(0.5, 256, 2048, 1));

        // Половина гигабайта: по 256 байт на ячейку CSV и SQL, по 2048 байт на ячейку XLSX
        assertThat(limits.maxCellsSize()).isEqualTo(2_097_152);
        assertThat(limits.maxXlsxCellsSize()).isEqualTo(262_144);
        assertThat(limits.maxColumnsSize()).isEqualTo(1012);
        assertThat(limits.maxRowsSize()).isEqualTo(2_097_152);
    }

    @Test
    @DisplayName("Several users - memory and executor are shared between them")
    void calculate_limits_for_several_users() {
        GenerationLimits limits = calculator.calculate(GIGABYTE, 1012, settings(0.5, 256, 2048, 4));

        assertThat(limits).isEqualTo(new GenerationLimits(524_288, 253, 524_288, 65_536));
    }

    @Test
    @DisplayName("More memory - proportionally greater limits of cells and rows, columns are the same")
    void calculate_limits_proportionally_to_memory() {
        GenerationLimits small = calculator.calculate(GIGABYTE, 100, settings(0.5, 256, 2048, 1));
        GenerationLimits large = calculator.calculate(4 * GIGABYTE, 100, settings(0.5, 256, 2048, 1));

        assertThat(large.maxCellsSize()).isEqualTo(small.maxCellsSize() * 4);
        assertThat(large.maxXlsxCellsSize()).isEqualTo(small.maxXlsxCellsSize() * 4);
        assertThat(large.maxRowsSize()).isEqualTo(small.maxRowsSize() * 4);
        assertThat(large.maxColumnsSize()).isEqualTo(small.maxColumnsSize()).isEqualTo(100);
    }

    @Test
    @DisplayName("Too small resources - at least one row, one column and one cell are allowed")
    void calculate_limits_for_tiny_resources() {
        GenerationLimits limits = calculator.calculate(1000, 1, settings(0.1, 300, 3000, 10));

        assertThat(limits).isEqualTo(new GenerationLimits(1, 1, 1, 1));
    }

    @Test
    @DisplayName("Columns can't be greater than cells")
    void columns_are_not_greater_than_cells() {
        GenerationLimits limits = calculator.calculate(100_000, 1012, settings(1.0, 1000, 10_000, 1));

        assertThat(limits).isEqualTo(new GenerationLimits(100, 100, 100, 10));
    }

    @Test
    @DisplayName("Huge heap - rows are limited by the maximum request size")
    void rows_are_limited_by_request_size() {
        GenerationLimits limits = calculator.calculate(4096 * GIGABYTE, 1012, settings(1.0, 1, 1, 1));

        assertThat(limits.maxCellsSize()).isEqualTo(4096 * GIGABYTE);
        assertThat(limits.maxRowsSize()).isEqualTo(Integer.MAX_VALUE);
    }

    private GenerationLimitsCalculationProperties settings(
            double memoryShare, int bytesPerCell, int xlsxBytesPerCell, int users) {
        GenerationLimitsCalculationProperties settings = new GenerationLimitsCalculationProperties();
        settings.setEnabled(true);
        settings.setMemoryShare(memoryShare);
        settings.setBytesPerCell(bytesPerCell);
        settings.setXlsxBytesPerCell(xlsxBytesPerCell);
        settings.setUsers(users);
        return settings;
    }
}
