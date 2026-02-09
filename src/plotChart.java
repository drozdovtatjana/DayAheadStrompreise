import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.swing.JFrame;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

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

        // Separate by day
        Map<LocalDate, List<Double>> pricesPerDay = new LinkedHashMap<>();
        Map<LocalDate, List<Date>> hoursPerDay = new LinkedHashMap<>();

        for (Map.Entry<ZonedDateTime, Double> entry : hourlyPrices.entrySet()) {
            LocalDate day = entry.getKey().toLocalDate();
            pricesPerDay.computeIfAbsent(day, k -> new ArrayList<>()).add(entry.getValue());
            hoursPerDay.computeIfAbsent(day, k -> new ArrayList<>()).add(Date.from(entry.getKey().toInstant()));
        }

        // Ensure exactly 3 days
        List<LocalDate> allDays = new ArrayList<>(pricesPerDay.keySet());
        LocalDate lastDay = allDays.isEmpty() ? LocalDate.now() : allDays.get(allDays.size() - 1);
        while (allDays.size() < 3) {
            lastDay = lastDay.plusDays(1);
            allDays.add(lastDay);
        }
        if (allDays.size() > 3) {
            allDays = allDays.subList(0, 3);
        }

        int colorIndex = 0;
        String[] colors = {"#FF0000", "#00AA00", "#0000FF"};

        for (LocalDate day : allDays) {
            List<Date> hours = hoursPerDay.getOrDefault(day, new ArrayList<>());
            List<Double> prices = pricesPerDay.getOrDefault(day, new ArrayList<>());

            XYSeries series;

            if (!prices.isEmpty()) {
                // Normal data
                series = chart.addSeries(day.toString(), hours, prices);
                series.setMarker(SeriesMarkers.CIRCLE);           // ← точки на линии
                 series.setLineColor(java.awt.Color.decode(colors[colorIndex % colors.length]));

            } else {
                // No data: create a dummy point at noon
                Calendar cal = Calendar.getInstance();
                cal.set(day.getYear(), day.getMonthValue() - 1, day.getDayOfMonth(), 12, 0); // noon
                Date noon = cal.getTime();

                series = chart.addSeries(day.toString() + " (No data)",
                        Collections.singletonList(noon),
                        Collections.singletonList(0.0));
                series.setMarker(SeriesMarkers.CIRCLE); // show a single gray dot
                series.setMarkerColor(java.awt.Color.GRAY);
                // No need to disable lines — single point means no line
            }

            colorIndex++;
        }

        SwingWrapper<XYChart> sw = new SwingWrapper<>(chart);
        JFrame chartFrame = sw.displayChart();
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
