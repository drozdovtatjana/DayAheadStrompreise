package infrastructure.api;

/**
 * Data Transfer Object (DTO) representing a single price entry
 * exactly as returned by the aWATTar API.
 *
 * This class should NOT contain business logic.
 */
public class AwattarPriceDto {

    public long start_timestamp;
    public long end_timestamp;
    public double marketprice;
    public String unit;
}
