package ru.akvine.istochnik.services.limits;

/**
 * Лимиты одного запроса на генерацию
 *
 * @param maxRowsSize максимальное число строк
 * @param maxColumnsSize максимальное число колонок
 * @param maxCellsSize максимальное число ячеек - строк, умноженных на колонки, - для файлов CSV и SQL
 * @param maxXlsxCellsSize максимальное число ячеек для файлов XLSX
 */
public record GenerationLimits(int maxRowsSize, int maxColumnsSize, long maxCellsSize, long maxXlsxCellsSize) {}
