package DiseaseSim.utility;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.Node;
import java.util.*;

/**
 * Modular chart panel for displaying time series data (e.g., infection progression).
 * Allows user to select variables and locations to plot.
 */
public class ChartPanel extends VBox {
    private LineChart<Number, Number> lineChart;
    private ComboBox<String> variableSelector;
    private ComboBox<String> locationSelector;
    private List<String> availableVariables;
    private List<String> availableLocations;
    private Map<String, List<Map<String, Integer>>> locationStatsHistory;

    public ChartPanel(List<String> variables, List<String> locations, Map<String, List<Map<String, Integer>>> statsHistory) {
        this.availableVariables = variables;
        this.availableLocations = locations;
        this.locationStatsHistory = statsHistory;
        setupUI();
    }

    private void setupUI() {
        setSpacing(10);
        setPadding(new javafx.geometry.Insets(10));
        variableSelector = new ComboBox<>();
        variableSelector.getItems().addAll(availableVariables);
        variableSelector.getSelectionModel().selectFirst();
        locationSelector = new ComboBox<>();
        locationSelector.getItems().addAll(availableLocations);
        locationSelector.getSelectionModel().selectFirst();
        Label varLabel = new Label("Variable:");
        Label locLabel = new Label("Location:");
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Day");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Count");
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Progression Chart");
        getChildren().addAll(varLabel, variableSelector, locLabel, locationSelector, lineChart);
        variableSelector.setOnAction(e -> updateChart());
        locationSelector.setOnAction(e -> updateChart());
        updateChart();
    }

    /**
     * Updates the chart based on selected variable and location.
     */
    public void updateChart() {
        String variable = variableSelector.getValue();
        String location = locationSelector.getValue();
        lineChart.getData().clear();
        if (variable == null || location == null) return;
        List<Map<String, Integer>> statsList = locationStatsHistory.get(location);
        if (statsList == null) return;
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(location + " - " + variable);
        for (int day = 0; day < statsList.size(); day++) {
            Map<String, Integer> stats = statsList.get(day);
            Integer value = stats.get(variable);
            if (value != null) {
                series.getData().add(new XYChart.Data<>(day, value));
            }
        }
        lineChart.getData().add(series);
    }

    /**
     * Allows updating the available variables and locations (for modularity).
     */
    public void setAvailableVariables(List<String> variables) {
        availableVariables = variables;
        variableSelector.getItems().setAll(variables);
        variableSelector.getSelectionModel().selectFirst();
        updateChart();
    }

    public void setAvailableLocations(List<String> locations) {
        availableLocations = locations;
        locationSelector.getItems().setAll(locations);
        locationSelector.getSelectionModel().selectFirst();
        updateChart();
    }

    /**
     * Allows updating the stats history data.
     */
    public void setLocationStatsHistory(Map<String, List<Map<String, Integer>>> statsHistory) {
        locationStatsHistory = statsHistory;
        updateChart();
    }

    /**
     * Returns the chart Node for embedding in other UI containers.
     */
    public Node getChartNode() {
        return lineChart;
    }
}
