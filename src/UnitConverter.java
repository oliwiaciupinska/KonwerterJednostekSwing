public class UnitConverter {

    public static double convert(double value, String conversionType) {
        return switch (conversionType) {
            case "Metry → Kilometry" -> value / 1000.0;
            case "Kilometry → Metry" -> value * 1000.0;

            case "Kilogramy → Gramy" -> value * 1000.0;
            case "Gramy → Kilogramy" -> value / 1000.0;

            case "Celsjusz → Fahrenheit" -> value * 9 / 5 + 32;
            case "Fahrenheit → Celsjusz" -> (value - 32) * 5 / 9;

            default -> throw new IllegalArgumentException("Nieznany typ konwersji");
        };
    }
}
