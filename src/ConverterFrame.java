import javax.swing.*;
import java.awt.*;

public class ConverterFrame extends JFrame {

    private JTextField inputField;
    private JLabel resultLabel;
    private JComboBox<String> unitBox;

    public ConverterFrame() {
        setTitle("Konwerter jednostek");
        setSize(520, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();

        setVisible(true);
    }

    private void initComponents() {
        // ===== KOLORY =====
        Color backgroundColor = new Color(245, 247, 250);   // jasne tło
        Color panelColor = new Color(230, 235, 240);        // panel
        Color buttonColor = new Color(70, 130, 180);        // niebieski
        Color textColor = new Color(30, 30, 30);

        // ===== PANEL GŁÓWNY =====
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ===== TYTUŁ =====
        JLabel titleLabel = new JLabel("Konwerter jednostek", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(textColor);

        // ===== PANEL WEJŚCIA =====
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBackground(panelColor);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel valueLabel = new JLabel("Wartość:");
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel unitLabel = new JLabel("Rodzaj konwersji:");
        unitLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        unitBox = new JComboBox<>(new String[]{
                "Metry → Kilometry",
                "Kilometry → Metry",
                "Kilogramy → Gramy",
                "Gramy → Kilogramy",
                "Celsjusz → Fahrenheit",
                "Fahrenheit → Celsjusz"
        });
        unitBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        inputPanel.add(valueLabel);
        inputPanel.add(inputField);
        inputPanel.add(unitLabel);
        inputPanel.add(unitBox);

        // ===== PRZYCISK =====
        JButton convertButton = new JButton("Przelicz");
        convertButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        convertButton.setBackground(buttonColor);
        convertButton.setForeground(Color.WHITE);
        convertButton.setFocusPainted(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(convertButton);

        // ===== WYNIK =====
        resultLabel = new JLabel("Wynik: ", JLabel.CENTER);
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        resultLabel.setForeground(textColor);

        // ===== SKŁADANIE OKNA =====
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        add(resultLabel, BorderLayout.SOUTH);

        // ===== OBSŁUGA PRZYCISKU =====
        convertButton.addActionListener(e -> {
            try {
                double value = Double.parseDouble(inputField.getText());
                String conversionType = (String) unitBox.getSelectedItem();

                double result = UnitConverter.convert(value, conversionType);
                resultLabel.setText("Wynik: " + result);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Proszę podać poprawną liczbę.",
                        "Błąd danych",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
