import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConverterFrame extends JFrame {

    private JTextField inputField;
    private JComboBox<String> conversionBox;
    private JLabel resultLabel;
    private JTable historyTable;
    private DefaultTableModel tableModel;

    public ConverterFrame() {
        setTitle("Konwerter jednostek");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadHistory();

        setVisible(true);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== PANEL GÓRNY =====
        JPanel topPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        topPanel.add(new JLabel("Wartość wejściowa:"));
        inputField = new JTextField();
        topPanel.add(inputField);

        topPanel.add(new JLabel("Rodzaj konwersji:"));
        conversionBox = new JComboBox<>(new String[]{
                "",
                "Metry → Kilometry",
                "Kilometry → Metry",
                "Gramy → Kilogramy",
                "Kilogramy → Gramy",
                "Celsjusz → Fahrenheit",
                "Fahrenheit → Celsjusz"
        });
        topPanel.add(conversionBox);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ===== PANEL PRZYCISKÓW =====
        JPanel buttonPanel = new JPanel();

        JButton convertButton = new JButton("Przelicz");
        JButton clearButton = new JButton("Wyczyść");
        JButton deleteButton = new JButton("Usuń");
        JButton exitButton = new JButton("Wyjść");

        buttonPanel.add(convertButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exitButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // ===== WYNIK =====
        resultLabel = new JLabel("Wynik: ", JLabel.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(resultLabel, BorderLayout.SOUTH);

        // ===== HISTORIA =====
        String[] columns = {"Wartość", "Konwersja", "Wynik", "Data i godzina"};
        tableModel = new DefaultTableModel(columns, 0);
        historyTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Historia konwersji"));

        add(mainPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // ===== AKCJE =====
        convertButton.addActionListener(e -> convert());
        clearButton.addActionListener(e -> clearFields());
        deleteButton.addActionListener(e -> deleteSelectedRow());
        exitButton.addActionListener(e -> System.exit(0));
    }

    private void convert() {
        try {
            double value = Double.parseDouble(inputField.getText());
            String type = (String) conversionBox.getSelectedItem();

            if (type == null || type.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Wybierz rodzaj konwersji");
                return;
            }

            double result = UnitConverter.convert(value, type);
            String unit = getResultUnit(type);

            String formatted = String.format("%.2f", result);
            resultLabel.setText("Wynik: " + formatted + " " + unit);

            String dateTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

            DatabaseManager.saveConversion(value, type, result, dateTime);
            tableModel.insertRow(0, new Object[]{value, type, formatted + " " + unit, dateTime});


        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Podaj poprawną liczbę");
        }
    }

    private void clearFields() {
        inputField.setText("");
        conversionBox.setSelectedIndex(0);
        resultLabel.setText("Wynik: ");
    }

    private void deleteSelectedRow() {
        int row = historyTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Zaznacz rekord do usunięcia");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Czy na pewno usunąć zaznaczony wpis?",
                "Potwierdzenie",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            DatabaseManager.deleteLast();
            tableModel.removeRow(row);
        }
    }

    private void loadHistory() {
        List<String[]> history = DatabaseManager.getAllConversions();
        for (String[] row : history) {
            tableModel.addRow(row);
        }
    }

    private String getResultUnit(String type) {
        if (type.contains("Metry")) return "m";
        if (type.contains("Kilometry")) return "km";
        if (type.contains("Gramy")) return "g";
        if (type.contains("Kilogramy")) return "kg";
        if (type.contains("Celsjusz")) return "°C";
        if (type.contains("Fahrenheit")) return "°F";
        return "";
    }
}
