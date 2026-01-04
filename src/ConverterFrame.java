import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ConverterFrame extends JFrame {

    private JTextField inputField;
    private JLabel resultLabel;
    private JComboBox<String> unitBox;

    private JTable historyTable;
    private DefaultTableModel tableModel;

    public ConverterFrame() {
        setTitle("Konwerter jednostek");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadHistory();

        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // ===== PANEL GÓRNY (FORMULARZ) =====
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        inputField = new JTextField();
        unitBox = new JComboBox<>(new String[]{
                "Metry → Kilometry",
                "Kilometry → Metry",
                "Kilogramy → Gramy",
                "Gramy → Kilogramy",
                "Celsjusz → Fahrenheit",
                "Fahrenheit → Celsjusz"
        });

        inputPanel.add(new JLabel("Wartość wejściowa:"));
        inputPanel.add(inputField);
        inputPanel.add(new JLabel("Rodzaj konwersji:"));
        inputPanel.add(unitBox);

        JButton convertButton = new JButton("Przelicz");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(convertButton);

        resultLabel = new JLabel("Wynik: ", JLabel.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(resultLabel, BorderLayout.SOUTH);

        // ===== PANEL HISTORII =====
        JLabel historyLabel = new JLabel("Historia konwersji", JLabel.CENTER);
        historyLabel.setFont(new Font("Arial", Font.BOLD, 14));

        String[] columns = {
                "Wartość wejściowa",
                "Rodzaj konwersji",
                "Wynik",
                "Data wykonania"
        };

        tableModel = new DefaultTableModel(columns, 0);
        historyTable = new JTable(tableModel);
        historyTable.setEnabled(false);

        JScrollPane tableScrollPane = new JScrollPane(historyTable);
        tableScrollPane.setPreferredSize(new Dimension(560, 180));

        JPanel historyPanel = new JPanel(new BorderLayout(5, 5));
        historyPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        historyPanel.add(historyLabel, BorderLayout.NORTH);
        historyPanel.add(tableScrollPane, BorderLayout.CENTER);

        // ===== DODAWANIE DO OKNA =====
        add(mainPanel, BorderLayout.NORTH);
        add(historyPanel, BorderLayout.CENTER);

        // ===== OBSŁUGA PRZYCISKU =====
        convertButton.addActionListener(e -> {
            try {
                double value = Double.parseDouble(inputField.getText());
                String type = (String) unitBox.getSelectedItem();

                double result = UnitConverter.convert(value, type);
                resultLabel.setText("Wynik: " + result);

                DatabaseManager.saveConversion(value, type, result);
                loadHistory();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Podaj poprawną liczbę",
                        "Błąd",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }

    private void loadHistory() {
        tableModel.setRowCount(0);
        for (String[] row : DatabaseManager.getAllConversions()) {
            tableModel.addRow(row);
        }
    }
}
