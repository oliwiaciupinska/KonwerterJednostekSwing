import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:konwerter.db";

    // Połączenie z bazą danych
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            throw new RuntimeException("Błąd połączenia z bazą danych", e);
        }
    }

    // Tworzenie tabeli przy starcie aplikacji
    public static void initDatabase() {
        String sql = """
            CREATE TABLE IF NOT EXISTS conversions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                value REAL NOT NULL,
                conversion_type TEXT NOT NULL,
                result REAL NOT NULL,
                created_at TEXT NOT NULL
            );
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

        } catch (Exception e) {
            throw new RuntimeException("Błąd inicjalizacji bazy danych", e);
        }
    }

    // Zapis jednej konwersji do bazy
    public static void saveConversion(double value, String type, double result) {
        String sql = """
            INSERT INTO conversions (value, conversion_type, result, created_at)
            VALUES (?, ?, ?, ?);
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, value);
            pstmt.setString(2, type);
            pstmt.setDouble(3, result);
            pstmt.setString(4, LocalDateTime.now().toString());

            pstmt.executeUpdate();

            System.out.println("Zapisano do bazy");

        } catch (Exception e) {
            throw new RuntimeException("Błąd zapisu do bazy danych", e);
        }
    }
}
