import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:konwerter.db";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            throw new RuntimeException("Błąd połączenia z bazą", e);
        }
    }

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
            throw new RuntimeException("Błąd tworzenia tabeli", e);
        }
    }

    public static void saveConversion(double value, String type, double result) {
        String sql = """
            INSERT INTO conversions (value, conversion_type, result, created_at)
            VALUES (?, ?, ?, ?);
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, value);
            ps.setString(2, type);
            ps.setDouble(3, result);
            ps.setString(4, LocalDateTime.now().toString());
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Błąd zapisu do bazy", e);
        }
    }

    public static List<String[]> getAllConversions() {
        List<String[]> data = new ArrayList<>();

        String sql = """
            SELECT value, conversion_type, result, created_at
            FROM conversions
            ORDER BY id DESC;
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                data.add(new String[]{
                        rs.getString("value"),
                        rs.getString("conversion_type"),
                        rs.getString("result"),
                        rs.getString("created_at")
                });
            }

        } catch (Exception e) {
            throw new RuntimeException("Błąd odczytu danych", e);
        }

        return data;
    }
}
