package ui;

import application.PriceService;
import infrastructure.api.AwattarClient;
import infrastructure.json.GsonPriceMapper;
import util.PriceNormalizer;

import javax.swing.*;
import org.jdatepicker.impl.*;

import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Properties;
import java.util.Map;
import java.time.ZonedDateTime;

public class DatePickerFrame extends JFrame {

    private final PriceService priceService;
    private final ZoneId austriaZone = ZoneId.of("Europe/Vienna");

    public DatePickerFrame() {
        super("Select a Date");

        // Initialize service
        this.priceService = new PriceService(
                new AwattarClient(),
                new GsonPriceMapper(),
                new PriceNormalizer()
        );

        setupUI();
    }

    private void setupUI() {
        this.setSize(300, 120);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout());

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

        this.add(datePicker);
        this.add(okButton);
        this.add(exitButton);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        // Action listeners
        okButton.addActionListener(e -> onDateSelected(datePicker));
        exitButton.addActionListener(e -> {
            ChartController.closeAllCharts();
            dispose();
        });    }

    private void onDateSelected(JDatePickerImpl datePicker) {
        java.util.Date selectedDate = (java.util.Date) datePicker.getModel().getValue();
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Please select a date!");
            return;
        }

        LocalDate date = selectedDate.toInstant().atZone(austriaZone).toLocalDate();

        new Thread(() -> {
            try {
                Map<ZonedDateTime, Double> normalizedData =
                        priceService.loadNormalizedPrices(date, austriaZone);
                System.out.println(export.JsonExporter.toNormalizedJsonString(normalizedData));
                // Delegate chart rendering
                SwingUtilities.invokeLater(() ->
                        ChartController.plotChart(normalizedData)
                );

            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage())
                );
            }
        }).start();
    }

    // Formatter for JDatePicker
    public static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private final java.text.SimpleDateFormat dateFormatter =
                new java.text.SimpleDateFormat("yyyy-MM-dd");

        @Override
        public Object stringToValue(String text) throws java.text.ParseException {
            return dateFormatter.parse(text);
        }

        @Override
        public String valueToString(Object value) throws java.text.ParseException {
            if (value != null) {
                java.util.Calendar cal = (java.util.Calendar) value;
                return dateFormatter.format(cal.getTime());
            }
            return "";
        }
    }
}
