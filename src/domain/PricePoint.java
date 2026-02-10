package domain;

import java.time.ZonedDateTime;

public class PricePoint {

    private final ZonedDateTime start;
    private final ZonedDateTime end;
    private final double priceCtPerKwh;

    public PricePoint(ZonedDateTime start, ZonedDateTime end, double priceCtPerKwh) {
        this.start = start;
        this.end = end;
        this.priceCtPerKwh = priceCtPerKwh;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public double getPriceCtPerKwh() {
        return priceCtPerKwh;
    }
}