package ru.akvine.istochnik.services.generators.number.integer;

public class IntegerShiftRange {
    private Integer start;
    private int end;
    private int step;

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public Integer getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
