import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jdatepicker.impl.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.util.*;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::showDatePicker);
    }

    private static void showDatePicker() {
        JFrame frame = new JFrame("Select a date");
        frame.setSize(300, 120);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        // JDatePicker setup
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

        JButton okButton = new JButton("OK");
        JButton exitButton = new JButton("Exit");

        frame.add(datePicker);
        frame.add(okButton);
        frame.add(exitButton);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        ZoneId austriaZone = ZoneId.of("Europe/Vienna");

        okButton.addActionListener(e -> {
            Date selectedDate = (Date) datePicker.getModel().getValue();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(frame, "Please select a date!");
                return;
            }
            LocalDate date = selectedDate.toInstant().atZone(austriaZone).toLocalDate();

            // Fetch and plot data in a separate thread
            new Thread(() -> {
                long[] dates = timeConverter(date);
                APIfunction api = new APIfunction();
                String jsonResponse = api.FETCING(dates[0], dates[1]);
                APIresponse response = JSONtoData(jsonResponse);
                Map<ZonedDateTime, Double> normalizedData = DataUtils.normalizeData(response.getData(), austriaZone);
                plotChart.plotChart(normalizedData);
            }).start();
        });

        exitButton.addActionListener(e -> {
            frame.dispose();
        });
    }

    public static long[] timeConverter(LocalDate date) {
        LocalDate previousDay = date.minusDays(1);
        LocalDate nextDay = date.plusDays(2);

        ZoneId austriaZone = ZoneId.of("Europe/Vienna");
        ZonedDateTime previousMidnight = previousDay.atStartOfDay(austriaZone);
        ZonedDateTime nextMidnight = nextDay.atStartOfDay(austriaZone);

        long startUnix = previousMidnight.toInstant().toEpochMilli();
        long endUnix = nextMidnight.toInstant().toEpochMilli();
        System.out.println("Unix timestamp at midnight Austria time (ms): " + startUnix);
        return new long[]{startUnix, endUnix};
    }

    public static APIresponse JSONtoData(String data) {
        Gson gson = new Gson();
        JsonObject root = gson.fromJson(data, JsonObject.class);
        JsonArray dataArray = root.getAsJsonArray("data");

        List<APIData> list = new ArrayList<>();
        for (int i = 0; i < dataArray.size(); i++) {
            JsonObject obj = dataArray.get(i).getAsJsonObject();
            APIData apiData = new APIData();
            apiData.setStart_timestamp(new Date(obj.get("start_timestamp").getAsLong()));
            apiData.setEnd_timestamp(new Date(obj.get("end_timestamp").getAsLong()));
            apiData.setMarketprice(obj.get("marketprice").getAsDouble());
            apiData.setUnit(obj.get("unit").getAsString());
            list.add(apiData);
        }

        APIresponse response = new APIresponse();
        response.setData(list);
        return response;
    }

    // Formatter for JDatePicker
    public static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private final java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");

        @Override
        public Object stringToValue(String text) throws java.text.ParseException {
            return dateFormatter.parse(text);
        }

        @Override
        public String valueToString(Object value) throws java.text.ParseException {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }
            return "";
        }
    }
}
