import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:konwerter.db";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // ===== INICJALIZACJA BAZY =====
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

        } catch (SQLException e) {
            throw new RuntimeException("Błąd inicjalizacji bazy danych", e);
        }
    }

    // ===== ZAPIS =====
    public static void saveConversion(double value, String type, double result, String dateTime) {
        String sql = """
                INSERT INTO conversions (value, conversion_type, result, created_at)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, value);
            stmt.setString(2, type);
            stmt.setDouble(3, result);
            stmt.setString(4, dateTime);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Błąd zapisu do bazy", e);
        }
    }

    // ===== ODCZYT =====
    public static List<String[]> getAllConversions() {
        List<String[]> list = new ArrayList<>();

        String sql = """
                SELECT value, conversion_type, result, created_at
                FROM conversions
                ORDER BY id DESC
                """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getDouble("value")),
                        rs.getString("conversion_type"),
                        String.format("%.2f", rs.getDouble("result")),
                        rs.getString("created_at")
                });
            }

        } catch (SQLException e) {
            throw new RuntimeException("Błąd odczytu historii", e);
        }

        return list;
    }

    // ===== USUWANIE OSTATNIEGO =====
    public static void deleteLast() {
        String sql = """
                DELETE FROM conversions
                WHERE id = (SELECT id FROM conversions ORDER BY id DESC LIMIT 1)
                """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            throw new RuntimeException("Błąd usuwania rekordu", e);
        }
    }
}
