import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.swing.JFrame;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.awt.*;
import java.util.List;

public class plotChart {

    public static void plotChart(Map<ZonedDateTime, Double> hourlyPrices) {
        XYChart chart = new XYChartBuilder()
                .width(1000)
                .height(600)
                .title("Day-Ahead Electricity Prices")
                .xAxisTitle("Hour")
                .yAxisTitle("Price (ct/kWh)")
                .build();

        chart.getStyler().setDatePattern("HH:mm");
        chart.getStyler().setLegendVisible(true);

        Map<LocalDate, List<Double>> pricesPerDay = new LinkedHashMap<>();
        Map<LocalDate, List<Date>> hoursPerDay = new LinkedHashMap<>();

        ZoneId zone = ZoneId.of("Europe/Vienna");

        for (Map.Entry<ZonedDateTime, Double> entry : hourlyPrices.entrySet()) {
            LocalDate day = entry.getKey().toLocalDate();
            pricesPerDay.computeIfAbsent(day, k -> new ArrayList<>()).add(entry.getValue());
            hoursPerDay.computeIfAbsent(day, k -> new ArrayList<>())
                    .add(Date.from(entry.getKey()
                            .withZoneSameInstant(zone)
                            .toInstant()));
        }

        List<LocalDate> allDays = new ArrayList<>(pricesPerDay.keySet());
        if (allDays.size() > 3) {
            allDays = allDays.subList(0, 3);
        }

        String[] colors = {"#FF0000", "#00AA00", "#0000FF"};
        int colorIndex = 0;

        for (LocalDate day : allDays) {
            List<Date> hours = hoursPerDay.getOrDefault(day, List.of());
            List<Double> prices = pricesPerDay.getOrDefault(day, List.of());

            List<SeriesSegment> segments = splitByGaps(hours, prices);

            Color dayColor = Color.decode(colors[colorIndex % colors.length]);

            if (segments.isEmpty()) {
                Calendar cal = Calendar.getInstance();
                cal.set(day.getYear(), day.getMonthValue() - 1, day.getDayOfMonth(), 12, 0);
                Date noon = cal.getTime();

                XYSeries s = chart.addSeries(day.toString() + " (No data)",
                        Collections.singletonList(noon),
                        Collections.singletonList(0.0));
                s.setMarker(SeriesMarkers.CIRCLE);
                s.setMarkerColor(Color.GRAY);
                s.setLineColor(Color.GRAY);
            } else {
                boolean showLegend = true;

                for (int i = 0; i < segments.size(); i++) {
                    SeriesSegment seg = segments.get(i);

                    String seriesName = day.toString();
                    if (i > 0) seriesName += "_gap" + i;

                    XYSeries s = chart.addSeries(seriesName, seg.x, seg.y);

                    s.setLineColor(dayColor);
                    s.setMarkerColor(dayColor);
                    s.setMarker(SeriesMarkers.CIRCLE);

                    s.setShowInLegend(showLegend);
                    showLegend = false;
                }
            }

            colorIndex++;
        }


        JFrame chartFrame = new JFrame("Day-Ahead Electricity Prices");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.add(new XChartPanel<>(chart), BorderLayout.CENTER);
        chartFrame.pack();
        chartFrame.setLocationRelativeTo(null);
        chartFrame.setVisible(true);
    }

    // ---------- GAP SPLITTER ----------
    private static List<SeriesSegment> splitByGaps(List<Date> x, List<Double> y) {
        List<SeriesSegment> segments = new ArrayList<>();
        List<Date> segX = new ArrayList<>();
        List<Double> segY = new ArrayList<>();

        for (int i = 0; i < y.size(); i++) {
            Double val = y.get(i);

            if (val != null && !val.isNaN()) {
                segX.add(x.get(i));
                segY.add(val);
            } else if (!segX.isEmpty()) {
                segments.add(new SeriesSegment(segX, segY));
                segX = new ArrayList<>();
                segY = new ArrayList<>();
            }
        }

        if (!segX.isEmpty()) {
            segments.add(new SeriesSegment(segX, segY));
        }

        return segments;
    }

    static class SeriesSegment {
        List<Date> x;
        List<Double> y;

        SeriesSegment(List<Date> x, List<Double> y) {
            this.x = x;
            this.y = y;
        }
    }
}
