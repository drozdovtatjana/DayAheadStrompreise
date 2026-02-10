package domain;


import java.time.ZonedDateTime;

/**
 * Domain model representing one hourly electricity price.
 * Independent from API, JSON, and UI layers.
 */
public record PricePoint(
        ZonedDateTime start,
        ZonedDateTime end,
        double priceCtPerKwh
) {
}
