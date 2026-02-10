package infrastructure.json;

import domain.PricePoint;
import infrastructure.api.AwattarPriceDto;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;


public class GsonPriceMapper {


    public List<PricePoint> toDomain(
            List<AwattarPriceDto> dtoList,
            ZoneId targetZone
    ) {
        return dtoList.stream()
                .map(dto -> mapSingle(dto, targetZone))
                .collect(Collectors.toList());
    }

    private PricePoint mapSingle(
            AwattarPriceDto dto,
            ZoneId targetZone
    ) {
        ZonedDateTime start = Instant
                .ofEpochMilli(dto.start_timestamp)
                .atZone(targetZone);

        ZonedDateTime end = Instant
                .ofEpochMilli(dto.end_timestamp)
                .atZone(targetZone);

        // EUR/MWh → ct/kWh
        double priceCtPerKwh = dto.marketprice / 10.0;

        return new PricePoint(start, end, priceCtPerKwh);
    }
}
