package ui;

import javax.swing.SwingUtilities;

/**
 * Application entry point.
 * Simply launches the DatePickerFrame.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DatePickerFrame::new);
    }
}
