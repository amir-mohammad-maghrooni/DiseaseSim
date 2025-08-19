
// DiseaseSim.utility package contains utility/helper classes for the simulation
package DiseaseSim.utility;


// JavaFX imports for UI controls and value conversion
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;


// Import for formatting numbers as percentages
import java.text.NumberFormat;

/**
 * Utility class for formatting and handling percentages in the simulation UI.
 * Provides methods for formatting percent values and creating percent spinners for JavaFX.
 */
public class PercentUtils {
    /**
     * Formats a double value as a percentage string (e.g., 0.25 -> "25%").
     * @param value Value between 0.0 and 1.0
     * @return Formatted percent string
     */
    public static String formatPercent(double value){
        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMinimumFractionDigits(0);
        return percentFormat.format(value);
    }

    /**
     * Creates a JavaFX Spinner for selecting percent values (0% to 100%).
     * Spinner displays and parses values as percentages.
     * @param initialValue Initial value for the spinner (0.0 to 1.0)
     * @return Configured Spinner<Double> for percent selection
     */
    public static Spinner<Double> createPercentSpinner(double initialValue){
        Spinner<Double> spinner = new Spinner<>();

        // Value factory for percent values (0.0 to 1.0, step 0.01)
        SpinnerValueFactory.DoubleSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0,1.0, initialValue, 0.01);
        
        // Converter to display values as percent strings and parse user input
        valueFactory.setConverter(new StringConverter<Double>(){
            @Override
            public String toString(Double value){
                if (value == null) return "";
                return String.format("%.0f%%", value * 100);
            }

            @Override
            public Double fromString(String text){
                try {
                    text = text.replace("%", "").trim();
                    double percent = Double.parseDouble(text);
                    return percent / 100.0;
                } catch (NumberFormatException e) {
                    return 0.0; // Default to 0 if parsing fails
                }
            }
        });

        spinner.setValueFactory(valueFactory);
        spinner.setEditable(true);
        return spinner;
    }
}
