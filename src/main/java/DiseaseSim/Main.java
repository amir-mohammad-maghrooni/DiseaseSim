


package DiseaseSim;


// Import model, policy, and utility classes for simulation and UI
import DiseaseSim.model.*;
import DiseaseSim.policies.*;
import DiseaseSim.utility.PercentUtils;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;
import java.nio.file.*;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Main is the entry point for the JavaFX-based Disease Simulation application.
 * It provides a graphical user interface for running, configuring, and visualizing the simulation.
 */
public class Main extends Application {
    // List of user-defined custom policies
    private java.util.List<Policy> customPolicies = new java.util.ArrayList<>();
    // The simulation world (contains locations, disease, etc.)
    private World world;
    // Output area for displaying simulation results
    private TextArea outputArea;
    // UI containers for different control sections
    private VBox locationControls;
    private VBox diseaseControls;
    private VBox policyControls;
    private VBox autoPolicyControls;
    // Spinner for selecting number of days to simulate
    private Spinner<Integer> daysToSimulate;

    // Maps for disease parameter controls
    private Map<String, Spinner<Double>> diseaseFields = new HashMap<>();
    private Map<String, Spinner<Integer>> diseaseIntFields = new HashMap<>();
    private Map<String, TextField> diseaseTextFields = new HashMap<>();
    // List of auto-policy rules (structural representation)
    private java.util.List<AutoPolicyRule> autoPolicyRules = new java.util.ArrayList<>();

    /**
     * Helper class for storing auto-policy rules (when to add/remove policies automatically).
     */
    private static class AutoPolicyRule {
        String locationName;
        String policyName;
        int addThreshold;
        int removeThreshold;
        public AutoPolicyRule(String locationName, String policyName, int addThreshold, int removeThreshold) {
            this.locationName = locationName;
            this.policyName = policyName;
            this.addThreshold = addThreshold;
            this.removeThreshold = removeThreshold;
        }
    }


    /**
     * Main entry point for launching the JavaFX application.
     */
    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Initializes the JavaFX UI, sets up all controls, and starts the simulation.
     * @param primaryStage The main application window
     */
    @Override
    public void start(Stage primaryStage) {
        world = new World();
        
        // Create main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        
        // Create tabs for different control sections
        TabPane tabPane = new TabPane();
        
        // Simulation Controls Tab
        Tab simulationTab = new Tab("Simulation", createSimulationControls());
        simulationTab.setClosable(false);
        
        // Locations Tab
        Tab locationsTab = new Tab("Locations", createLocationControls());
        locationsTab.setClosable(false);
        
        // Disease Tab
        Tab diseaseTab = new Tab("Disease", createDiseaseControls());
        diseaseTab.setClosable(false);
        
        // Policies Tab
        Tab policiesTab = new Tab("Policies", createPolicyControls());
        policiesTab.setClosable(false);
        
        // Auto Policies Tab
        Tab autoPoliciesTab = new Tab("Auto Policies", createAutoPolicyControls());
        autoPoliciesTab.setClosable(false);
        
        tabPane.getTabs().addAll(simulationTab, locationsTab, diseaseTab, policiesTab, autoPoliciesTab);
        
        // Output area for simulation results
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        
        // Add components to root layout
        root.setTop(tabPane);
        root.setCenter(outputArea);
        
        // Set up the scene and show the window
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("Disease Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Initialize with default values and update output
        initializeDefaultValues();
        updateOutput();
    }


    /**
     * Creates the simulation controls UI (run days, reset, etc.).
     * @return VBox containing simulation controls
     */
    private VBox createSimulationControls() {
        VBox controls = new VBox(10);
        controls.setPadding(new Insets(10));
        
        // Days to simulate spinner
        HBox daysBox = new HBox(10);
        daysBox.setAlignment(Pos.CENTER_LEFT);
        daysToSimulate = new Spinner<>(1, 1000, 5);
        daysToSimulate.setEditable(true);
        daysBox.getChildren().addAll(new Label("Days to simulate:"), daysToSimulate);
        
        // Simulation buttons
        HBox buttonBox = new HBox(10);
        Button run1DayBtn = new Button("Run 1 Day");
        run1DayBtn.setOnAction(_ -> runSimulation(1));
        
        Button run5DaysBtn = new Button("Run 5 Days");
        run5DaysBtn.setOnAction(_ -> runSimulation(5));
        
        Button runCustomBtn = new Button("Run Custom Days");
        runCustomBtn.setOnAction(_ -> runSimulation(daysToSimulate.getValue()));
        
        Button resetBtn = new Button("Reset Simulation");
        resetBtn.setOnAction(_ -> resetSimulation());
        
        Button showChart = new Button("Show Chart");
        showChart.setOnAction(_ -> showChartDialog());
        buttonBox.getChildren().addAll(run1DayBtn, run5DaysBtn, runCustomBtn, resetBtn, showChart);
        controls.getChildren().addAll(daysBox, buttonBox);
        return controls;
    }
    /**
     * Prompts user for chart interval and save location, then exports a line chart as JPG.
     */
    private void showChartDialog() {
        // Show chart preview window with save button
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<Integer> intervalCombo = new ComboBox<>();
        intervalCombo.getItems().addAll(1, 5, 10, 20, 50);
        intervalCombo.getSelectionModel().selectFirst();

        ComboBox<String> variableCombo = new ComboBox<>();
        variableCombo.getItems().addAll("Infected", "Healthy", "Recovered", "Deceased");
        variableCombo.getSelectionModel().selectFirst();

        ComboBox<String> locationCombo = new ComboBox<>();
        for (Location loc : world.getLocations()) {
            locationCombo.getItems().add(loc.getName());
        }
        if (!locationCombo.getItems().isEmpty()) {
            locationCombo.getSelectionModel().selectFirst();
        }

        grid.add(new Label("Interval (days):"), 0, 0);
        grid.add(intervalCombo, 1, 0);
        grid.add(new Label("Variable:"), 0, 1);
        grid.add(variableCombo, 1, 1);
        grid.add(new Label("Location:"), 0, 2);
        grid.add(locationCombo, 1, 2);

        javafx.stage.Stage previewStage = new javafx.stage.Stage();
        previewStage.setTitle("Chart Preview");
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(10));
        vbox.getChildren().add(grid);
        javafx.scene.control.Button showChartButton = new javafx.scene.control.Button("Show Chart");
        vbox.getChildren().add(showChartButton);
        previewStage.setScene(new javafx.scene.Scene(vbox, 400, 300));
        previewStage.show();

        showChartButton.setOnAction(e -> {
            int interval = intervalCombo.getValue();
            String variable = variableCombo.getValue();
            String location = locationCombo.getValue();
            showChartPreviewWindow(variable, location, interval);
        });
    }

    /**
     * Generates and saves the chart as a JPG file.
     */
    private void exportChartAsJPG(String variable, String location, int interval, String filePath) {
        // Prepare stats history for the selected location and variable
        List<Map<String, Integer>> statsList = getStatsHistory(location);
        if (statsList == null || statsList.isEmpty()) return;
        List<Map<String, Integer>> filteredStats = new ArrayList<>();
        for (int i = 0; i < statsList.size(); i += interval) {
            filteredStats.add(statsList.get(i));
        }
        List<String> variables = new ArrayList<>();
        variables.add(variable);
        List<String> locations = new ArrayList<>();
        locations.add(location);
        Map<String, List<Map<String, Integer>>> statsHistory = new HashMap<>();
        statsHistory.put(location, filteredStats);
        DiseaseSim.utility.ChartPanel chartPanel = new DiseaseSim.utility.ChartPanel(variables, locations, statsHistory);
        chartPanel.setPrefSize(800, 600);
        javafx.stage.Stage chartStage = new javafx.stage.Stage();
        chartStage.setTitle("Chart");
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(10));
        vbox.getChildren().add(chartPanel);
        javafx.scene.control.Button saveButton = new javafx.scene.control.Button("Save Chart as PNG");
        vbox.getChildren().add(saveButton);
        chartStage.setScene(new javafx.scene.Scene(vbox, 820, 650));
        chartStage.show();

        saveButton.setOnAction(event -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Save Chart");
            fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("PNG Files", "*.png")
            );
            java.io.File file = fileChooser.showSaveDialog(chartStage);
            if (file != null) {
                String fname = file.getName().toLowerCase();
                if (!fname.endsWith(".png")) {
                    file = new java.io.File(file.getAbsolutePath() + ".png");
                }
                javafx.scene.SnapshotParameters params = new javafx.scene.SnapshotParameters();
                // Save only the chart node, not the ChartPanel container
                javafx.scene.image.WritableImage image = chartPanel.getChartNode().snapshot(params, null);
                if (image == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to capture chart image.");
                    alert.showAndWait();
                    return;
                }
                try {
                    boolean result = javax.imageio.ImageIO.write(javafx.embed.swing.SwingFXUtils.fromFXImage(image, null), "png", file);
                    StringBuilder debugInfo = new StringBuilder();
                    debugInfo.append("Attempted to save file: ").append(file.getAbsolutePath()).append("\n");
                    debugInfo.append("File exists after write: ").append(file.exists()).append("\n");
                    debugInfo.append("File length: ").append(file.length()).append("\n");
                    if (result && file.exists() && file.length() > 0) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Chart saved successfully to:\n" + file.getAbsolutePath());
                        alert.showAndWait();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save chart file.\n" + debugInfo.toString());
                        alert.showAndWait();
                    }
                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error saving chart: " + e.getMessage() + "\n" + sw.toString());
                    alert.showAndWait();
                }
            }
        });
    }

    // Helper to show chart preview window from dialog
    private void showChartPreviewWindow(String variable, String location, int interval) {
        List<Map<String, Integer>> statsList = getStatsHistory(location);
        if (statsList == null || statsList.isEmpty()) return;
        List<Map<String, Integer>> filteredStats = new ArrayList<>();
        for (int i = 0; i < statsList.size(); i += interval) {
            filteredStats.add(statsList.get(i));
        }
        List<String> variables = new ArrayList<>();
        variables.add(variable);
        List<String> locations = new ArrayList<>();
        locations.add(location);
        Map<String, List<Map<String, Integer>>> statsHistory = new HashMap<>();
        statsHistory.put(location, filteredStats);
        exportChartAsJPG(variable, location, interval, null); // null path, triggers preview only
    }

    /**
     * Returns the stats history for a location (implement as needed).
     */
    private List<Map<String, Integer>> getStatsHistory(String locationName) {
    return world.getStatsHistory(locationName);
    }

    /**
     * Creates the location controls UI (add location, set initial infected, etc.).
     * @return VBox containing location controls
     */
    private VBox createLocationControls() {
        locationControls = new VBox(10);
        locationControls.setPadding(new Insets(10));

        Button addLocationBtn = new Button("Add Location");
        addLocationBtn.setOnAction(_ -> showAddLocationDialog());

        Button setInitialInfectedBtn = new Button("Set Initial Infected");
        setInitialInfectedBtn.setOnAction(_ -> showSetInitialInfectedDialog());

        locationControls.getChildren().addAll(addLocationBtn, setInitialInfectedBtn);
        rebuildLocationControls();
        return locationControls;
    }
    
    // Helper to rebuild the location controls UI from the current locations
    /**
     * Rebuilds the location controls UI from the current list of locations.
     * Used after adding/removing locations or resetting.
     */
    private void rebuildLocationControls() {
        locationControls.getChildren().clear();
        Button addLocationBtn = new Button("Add Location");
        addLocationBtn.setOnAction(_ -> showAddLocationDialog());
        Button saveLocationsBtn = new Button("Save Locations");
        saveLocationsBtn.setOnAction(_ -> showSaveLocationsDialog());
        Button loadLocationsBtn = new Button("Load Locations");
        loadLocationsBtn.setOnAction(_ -> showLoadLocationsDialog());
        Button setInitialInfectedBtn = new Button("Set Initial Infected");
        setInitialInfectedBtn.setOnAction(_ -> showSetInitialInfectedDialog());
        locationControls.getChildren().addAll(addLocationBtn, saveLocationsBtn, loadLocationsBtn, setInitialInfectedBtn);
        for (Location loc : world.getLocations()) {
            addLocationToUI(loc);
            updateOutput();
        }
    }

    // Show file chooser to save locations
        private void showSaveLocationsDialog() {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Save Locations");
            fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("JSON Files", "*.json"));
            java.io.File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                saveLocationsConfig(file.getAbsolutePath());
                saveLastLocationsPath(file.getAbsolutePath());
            }
        }

    // Show file chooser to load locations
    private void showLoadLocationsDialog() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Load Locations");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("JSON Files", "*.json"));
        java.io.File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            loadLocationsConfig(file.getAbsolutePath());
            saveLastLocationsPath(file.getAbsolutePath());
        }
    }

    // Save locations to a user-specified file
    private void saveLocationsConfig(String filePath) {
        try {
            JSONArray locationsArr = new JSONArray();
            for (Location loc : world.getLocations()) {
                JSONObject locObj = new JSONObject();
                locObj.put("name", loc.getName());
                locObj.put("population", loc.getPopulationSize());
                locObj.put("density", loc.getPopulationDensity());
                locationsArr.put(locObj);
            }
            JSONObject root = new JSONObject();
            root.put("locations", locationsArr);
            java.nio.file.Files.write(java.nio.file.Paths.get(filePath), root.toString(2).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load locations from a user-specified file
    private void loadLocationsConfig(String filePath) {
        try {
            java.nio.file.Path configPath = java.nio.file.Paths.get(filePath);
            if (!java.nio.file.Files.exists(configPath)) return;
            String jsonStr = new String(java.nio.file.Files.readAllBytes(configPath));
            JSONObject root = new JSONObject(jsonStr);
            JSONArray locationsArr = root.getJSONArray("locations");
            world.getLocations().clear();
            for (int i = 0; i < locationsArr.length(); i++) {
                JSONObject locObj = locationsArr.getJSONObject(i);
                String name = locObj.getString("name");
                int pop = locObj.getInt("population");
                double dens = locObj.getDouble("density");
                Location loc = new Location(name, pop, dens);
                world.addNewLocation(loc);
            }
            rebuildLocationControls();
            updateOutput();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Save last-used locations file path in appconfig.json
    private void saveLastLocationsPath(String filePath) {
        try {
            JSONObject config = new JSONObject();
            config.put("lastLocationsPath", filePath);
            java.nio.file.Path configPath = getJarDirectory().resolve("appconfig.json");
            java.nio.file.Files.write(configPath, config.toString(2).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load last-used locations file path from appconfig.json
    private String loadLastLocationsPath() {
        try {
            java.nio.file.Path configPath = getJarDirectory().resolve("appconfig.json");
            if (!java.nio.file.Files.exists(configPath)) return null;
            String jsonStr = new String(java.nio.file.Files.readAllBytes(configPath));
            JSONObject config = new JSONObject(jsonStr);
            return config.optString("lastLocationsPath", null);
        } catch (Exception e) {
            return null;
        }
    }
    // Call this on startup to auto-load last locations file
    private void autoLoadLastLocations() {
        String lastPath = loadLastLocationsPath();
        if (lastPath != null) {
            loadLocationsConfig(lastPath);
        }
    }
    // Dialog to set initial infected for a location
    /**
     * Shows a dialog to set the number of initially infected people for a location.
     */
    private void showSetInitialInfectedDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Set Initial Infected");
        dialog.setHeaderText("Select a location and set the number of initial infected");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<String> locationCombo = new ComboBox<>();
        // Always rebuild the ComboBox from the current locations when dialog is shown
        locationCombo.getItems().clear();
        for (Location loc : world.getLocations()) {
            locationCombo.getItems().add(loc.getName());
        }
        if (!locationCombo.getItems().isEmpty()) {
            locationCombo.getSelectionModel().selectFirst();
        }

        Spinner<Integer> infectedSpinner = new Spinner<>(0, 1000000, 0, 1);

        grid.add(new Label("Location:"), 0, 0);
        grid.add(locationCombo, 1, 0);
        grid.add(new Label("Initial Infected:"), 0, 1);
        grid.add(infectedSpinner, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Refresh ComboBox items every time dialog is shown
        dialog.setOnShowing(_ -> {
            locationCombo.getItems().clear();
            for (Location loc : world.getLocations()) {
                locationCombo.getItems().add(loc.getName());
            }
            if (!locationCombo.getItems().isEmpty()) {
                locationCombo.getSelectionModel().selectFirst();
            }
        });

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK && locationCombo.getValue() != null) {
                String locName = locationCombo.getValue();
                int infected = infectedSpinner.getValue();
                for (Location loc : world.getLocations()) {
                    if (loc.getName().equals(locName)) {
                        loc.setInitialInfected(infected);
                        if (world.getDisease() != null){
                            loc.introduceDisease(world.getDisease(), infected);
                        }
                        break;
                    }
                }
                
            }
        });
        updateOutput();
    }

    /**
     * Creates the disease controls UI (edit disease parameters).
     * @return VBox containing disease controls
     */
    private VBox createDiseaseControls() {
        diseaseControls = new VBox(10);
        diseaseControls.setPadding(new Insets(10));
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(6);
        // Disease name
        TextField diseaseName = new TextField("COVID-19");
        diseaseTextFields.put("name", diseaseName);
        grid.add(new Label("Disease Name:"), 0, 0);
        grid.add(diseaseName, 1, 0);
        
        // Transmission rate
        Spinner<Double> transmissionRate = PercentUtils.createPercentSpinner(0.3);
        diseaseFields.put("transmission", transmissionRate);
        grid.add(new Label("Transmission Rate:"), 0, 1);
        grid.add(transmissionRate, 1, 1);
        
        // Mortality rate
        Spinner<Double> mortalityRate = PercentUtils.createPercentSpinner(0.02);
        diseaseFields.put("mortality", mortalityRate);
        grid.add(new Label("Mortality Rate:"), 0, 2);
        grid.add(mortalityRate, 1, 2);
        
        // Recovery rate
        Spinner<Double> recoveryRate = PercentUtils.createPercentSpinner(0.1);
        diseaseFields.put("recovery", recoveryRate);
        grid.add(new Label("Recovery Rate:"), 0, 3);
        grid.add(recoveryRate, 1, 3);

        // Minimum days infected
        Spinner<Integer> minDaysInfected = new Spinner<>(1, 999, 5);
        minDaysInfected.setEditable(true);
        diseaseIntFields.put("minDaysInfected", minDaysInfected);
        grid.add(new Label("Min Days Infected:"), 0, 4);
        grid.add(minDaysInfected, 1, 4);
        
        Button updateDiseaseBtn = new Button("Update Disease");
        updateDiseaseBtn.setOnAction(_ -> updateDisease());
        
        diseaseControls.getChildren().addAll(grid, updateDiseaseBtn);
        return diseaseControls;
    }

    /**
     * Creates the policy controls UI (add custom policy).
     * @return VBox containing policy controls
     */
    private VBox createPolicyControls() {
        policyControls = new VBox(10);
        policyControls.setPadding(new Insets(10));
        
        Button addPolicyBtn = new Button("Add Custom Policy");
        addPolicyBtn.setOnAction(_ -> showAddPolicyDialog());
        
        policyControls.getChildren().add(addPolicyBtn);
        return policyControls;
    }

    /**
     * Creates the auto-policy controls UI (add auto-policy rules).
     * @return VBox containing auto-policy controls
     */
    private VBox createAutoPolicyControls() {
        autoPolicyControls = new VBox(10);
        autoPolicyControls.setPadding(new Insets(10));
        
        Button addAutoPolicyBtn = new Button("Add Auto Policy Rule");
        addAutoPolicyBtn.setOnAction(_ -> showAddAutoPolicyDialog());
        
        autoPolicyControls.getChildren().add(addAutoPolicyBtn);
        return autoPolicyControls;
    }

    /**
     * Initializes the simulation with default locations and disease parameters.
     */
    private void initializeDefaultValues() {
        // Initialize with default locations
        addLocation("Metro City", 150000, 0.8);
        addLocation("Suburbs", 55000, 0.4);
        addLocation("Rural Town", 15000, 0.2);

        // Ensure Disease is set before updating
        if (world.getDisease() == null) {
            world.setDisease("COVID-19", 0.3, 0.02, 0.1, 5);
        }
        updateDisease();

        world.start();
    }

    /**
     * Shows a dialog to add a new location to the simulation.
     */
    private void showAddLocationDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Location");
    dialog.setHeaderText("Enter location details");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        Spinner<Integer> populationSpinner = new Spinner<>(100, 1000000, 100000, 1000);
        Spinner<Double> densitySpinner = PercentUtils.createPercentSpinner(0.5);

    // ...existing code...

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Population:"), 0, 1);
        grid.add(populationSpinner, 1, 1);
        grid.add(new Label("Density:"), 0, 2);
        grid.add(densitySpinner, 1, 2);
    // ...existing code...

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String name = nameField.getText();
                int pop = populationSpinner.getValue();
                double dens = densitySpinner.getValue();
                Location newLoc = new Location(name, pop, dens);
                world.addNewLocation(newLoc);
                // ...existing code...
                saveLocationsConfig();
                rebuildLocationControls();
            }
        });
    }

    /**
     * Adds a new location to the simulation, checking for valid/unique names.
     * @param name Location name
     * @param population Population size
     * @param density Population density (0.0-1.0)
     */
    private void addLocation(String name, int population, double density) {
        // Prevent empty or whitespace-only names
        if (name == null || name.trim().isEmpty()) {
            System.out.println("[DEBUG] Attempted to add location with empty or whitespace name: '" + name + "'");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Location name cannot be empty.");
            alert.showAndWait();
            return;
        }
        // Prevent duplicate location names
        for (Location loc : world.getLocations()) {
            if (loc.getName().equalsIgnoreCase(name.trim())) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Location with this name already exists.");
                alert.showAndWait();
                return;
            }
        }
        Location location = new Location(name.trim(), population, density);
        world.addNewLocation(location);
        System.out.println("[DEBUG] Locations after add: " + world.getLocations().size());
        for (Location loc : world.getLocations()) {
            System.out.println("[DEBUG] Location: " + loc.getName());
        }
        rebuildLocationControls();
        updateOutput();
    }

    /**
     * Shows a dialog to manage policies for a specific location (add/remove).
     * @param location The location to manage
     */
    private void showLocationPoliciesDialog(Location location) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Manage Policies for " + location.getName());
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        // Current policies
        Label currentLabel = new Label("Current Policies:");
        VBox currentPoliciesBox = new VBox(5);
        
        for (Policy policy : location.getActivePolicies()) {
            HBox policyBox = new HBox(10);
            policyBox.setAlignment(Pos.CENTER_LEFT);
            
            Label policyLabel = new Label(policy.getName() + " (" + policy.getDescription() + ")");
            Button removeBtn = new Button("Remove");
            removeBtn.setOnAction(_ -> {
                location.removePolicy(policy.getClass());
                currentPoliciesBox.getChildren().remove(policyBox);
            });
            
            policyBox.getChildren().addAll(policyLabel, removeBtn);
            currentPoliciesBox.getChildren().add(policyBox);
        }
        
        if (location.getActivePolicies().isEmpty()) {
            currentPoliciesBox.getChildren().add(new Label("No active policies"));
        }
        
        // Add policy buttons
        Label addLabel = new Label("Add Policy:");
        HBox addButtons = new HBox(10);
        
        Button addMaskBtn = new Button("Mask Mandate");
        addMaskBtn.setOnAction(_ -> {
            location.addPolicy(new MaskPolicy());
            updateLocationPoliciesDialog(location, currentPoliciesBox);
        });
        
        Button addDistancingBtn = new Button("Social Distancing");
        addDistancingBtn.setOnAction(_ -> {
            location.addPolicy(new SocialDistancingPolicy());
            updateLocationPoliciesDialog(location, currentPoliciesBox);
        });
        
        Button addLockdownBtn = new Button("Lockdown");
        addLockdownBtn.setOnAction(_ -> {
            location.addPolicy(new LockdownPolicy());
            updateLocationPoliciesDialog(location, currentPoliciesBox);
        });
        
        addButtons.getChildren().addAll(addMaskBtn, addDistancingBtn, addLockdownBtn);
        
        content.getChildren().addAll(currentLabel, currentPoliciesBox, addLabel, addButtons);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        
        dialog.showAndWait();
    }

    /**
     * Updates the current policies section of the location policies dialog.
     * @param location The location being managed
     * @param currentPoliciesBox The UI container for current policies
     */
    private void updateLocationPoliciesDialog(Location location, VBox currentPoliciesBox) {
        currentPoliciesBox.getChildren().clear();
        
        for (Policy policy : location.getActivePolicies()) {
            HBox policyBox = new HBox(10);
            policyBox.setAlignment(Pos.CENTER_LEFT);
            
            Label policyLabel = new Label(policy.getName() + " (" + policy.getDescription() + ")");
            Button removeBtn = new Button("Remove");
            removeBtn.setOnAction(_ -> {
                location.removePolicy(policy.getClass());
                currentPoliciesBox.getChildren().remove(policyBox);
            });
            
            policyBox.getChildren().addAll(policyLabel, removeBtn);
            currentPoliciesBox.getChildren().add(policyBox);
        }
        
        if (location.getActivePolicies().isEmpty()) {
            currentPoliciesBox.getChildren().add(new Label("No active policies"));
        }
    }
    /**
     * Updates the disease parameters in the simulation from the UI controls.
     */
    private void updateDisease() {
        String name = diseaseTextFields.get("name").getText().trim();
        if (name.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Disease name cannot be empty.");
            alert.showAndWait();
            return;
        }
        world.getDisease().setName(name);

        double transmission = diseaseFields.get("transmission").getValue();
        world.getDisease().setBaseTransmissionRate(transmission);

        double mortality = diseaseFields.get("mortality").getValue();
        world.getDisease().setMortalityRate(mortality);

        double recovery = diseaseFields.get("recovery").getValue();
        world.getDisease().setRecoveryRate(recovery);

        int minDays = diseaseIntFields.get("minDaysInfected").getValue();
        world.getDisease().setMinDaysInfected(minDays);

    }
    

    /**
     * Shows a dialog to create a new custom policy.
     */
    private void showAddPolicyDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Custom Policy");
        dialog.setHeaderText("Create a new custom policy");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField();
        nameField.setPromptText("Policy name");
        
        TextField descField = new TextField();
        descField.setPromptText("Description");
        
        Spinner<Double> modifierSpinner = PercentUtils.createPercentSpinner(0.5);
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Transmission Modifier:"), 0, 2);
        grid.add(modifierSpinner, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                createCustomPolicy(nameField.getText(), descField.getText(), modifierSpinner.getValue());
            }
        });
    }

    /**
     * Creates a new custom policy and adds it to the UI and internal list.
     * @param name Policy name
     * @param description Policy description
     * @param modifier Transmission modifier
     */
    private void createCustomPolicy(String name, String description, double modifier) {
        Policy customPolicy = new CustomPolicy(name, description, modifier);
        customPolicies.add(customPolicy);

        // Add to policy controls
        HBox policyBox = new HBox(10);
        policyBox.setAlignment(Pos.CENTER_LEFT);

        Label policyLabel = new Label(name + ": " + description + " (Modifier: " + PercentUtils.formatPercent(modifier) + ")");
        Button applyBtn = new Button("Apply to Location");
        applyBtn.setOnAction(_ -> showApplyPolicyDialog(customPolicy));

        policyBox.getChildren().addAll(policyLabel, applyBtn);
        policyControls.getChildren().add(policyBox);
    }

    /**
     * Shows a dialog to apply a policy to selected locations.
     * @param policy The policy to apply
     */
    private void showApplyPolicyDialog(Policy policy) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Apply Policy");
        dialog.setHeaderText("Select locations to apply " + policy.getName());
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        for (Location location : world.getLocations()) {
            CheckBox checkBox = new CheckBox(location.getName());
            content.getChildren().add(checkBox);
        }
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                for (int i = 0; i < world.getLocations().size(); i++) {
                    CheckBox checkBox = (CheckBox) content.getChildren().get(i);
                    if (checkBox.isSelected()) {
                        world.getLocations().get(i).addPolicy(policy);
                    }
                }
                updateOutput();
            }
        });
    }

    /**
     * Shows a dialog to add a new auto-policy rule.
     */
    private void showAddAutoPolicyDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Auto Policy Rule");
        dialog.setHeaderText("Create a rule to automatically add/remove policies based on infection levels");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Location selection
        ComboBox<String> locationCombo = new ComboBox<>();
        locationCombo.getItems().clear();
        for (Location loc : world.getLocations()) {
            locationCombo.getItems().add(loc.getName());
        }
        if (!locationCombo.getItems().isEmpty()) {
            locationCombo.getSelectionModel().selectFirst();
        }

        // Policy selection
        ComboBox<String> policyCombo = new ComboBox<>();
        policyCombo.getItems().addAll("Mask Mandate", "Social Distancing", "Lockdown");
        for (Policy p : customPolicies) {
            policyCombo.getItems().add(p.getName());
        }
        if (!policyCombo.getItems().isEmpty()) {
            policyCombo.getSelectionModel().selectFirst();
        }
        
        // Thresholds
        Spinner<Integer> addThreshold = new Spinner<>(1, 1000000, 1000, 100);
        Spinner<Integer> removeThreshold = new Spinner<>(1, 1000000, 100, 10);
        
        grid.add(new Label("Location:"), 0, 0);
        grid.add(locationCombo, 1, 0);
        grid.add(new Label("Policy:"), 0, 1);
        grid.add(policyCombo, 1, 1);
        grid.add(new Label("Add when infections >:"), 0, 2);
        grid.add(addThreshold, 1, 2);
        grid.add(new Label("Remove when infections <:"), 0, 3);
        grid.add(removeThreshold, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Refresh ComboBox items every time dialog is shown
        dialog.setOnShowing(_ -> {
            locationCombo.getItems().clear();
            for (Location loc : world.getLocations()) {
                locationCombo.getItems().add(loc.getName());
            }
            if (!locationCombo.getItems().isEmpty()) {
                locationCombo.getSelectionModel().selectFirst();
            }
        });

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK && locationCombo.getValue() != null && policyCombo.getValue() != null) {
                String locationName = locationCombo.getValue();
                String policyName = policyCombo.getValue();
                int addThresh = addThreshold.getValue();
                int removeThresh = removeThreshold.getValue();
                addAutoPolicyRule(locationName, policyName, addThresh, removeThresh);
            }
        });
    }

    /**
     * Adds a new auto-policy rule to the UI and internal list.
     * @param locationName Name of the location
     * @param policyName Name of the policy
     * @param addThreshold Infected threshold to add policy
     * @param removeThreshold Infected threshold to remove policy
     */
    private void addAutoPolicyRule(String locationName, String policyName, int addThreshold, int removeThreshold) {
        HBox ruleBox = new HBox(10);
        ruleBox.setAlignment(Pos.CENTER_LEFT);

        String ruleText = String.format("%s: Add %s when >%d infected, remove when <%d",
            locationName, policyName, addThreshold, removeThreshold);

        Label ruleLabel = new Label(ruleText);
        Button removeBtn = new Button("Remove Rule");
        removeBtn.setOnAction(_ -> {
            autoPolicyControls.getChildren().remove(ruleBox);
            // Remove from the list as well
            autoPolicyRules.removeIf(r -> r.locationName.equals(locationName) && r.policyName.equals(policyName)
                && r.addThreshold == addThreshold && r.removeThreshold == removeThreshold);
        });

        ruleBox.getChildren().addAll(ruleLabel, removeBtn);
        autoPolicyControls.getChildren().add(ruleBox);

        // Store the rule in the structured list
        autoPolicyRules.add(new AutoPolicyRule(locationName, policyName, addThreshold, removeThreshold));

        System.out.println("[DEBUG] Added Auto Policy Rule : " + ruleText);
    }

    /**
     * Checks all auto-policy rules and applies/removes policies as needed based on infection counts.
     */
    private void checkAutoPolicyRules() {
        // Use the structured list of rules instead of parsing UI labels
        for (AutoPolicyRule rule : autoPolicyRules) {
            for (Location location : world.getLocations()) {
                if (location.getName().equals(rule.locationName)) {
                    Map<String, Integer> stats = location.getStatistics();
                    int currentInfected = stats.get("Infected");
                    // Add policy if needed
                    if (currentInfected > rule.addThreshold && !hasPolicyActive(location, rule.policyName)) {
                        addPolicyToLocation(location, rule.policyName);
                        System.out.println("[AUTO-POLICY] Added " + rule.policyName + " to " + rule.locationName + " (Infected: " + currentInfected + ")");
                    }
                    // Remove policy if needed
                    else if (currentInfected < rule.removeThreshold && hasPolicyActive(location, rule.policyName)) {
                        removePolicyFromLocation(location, rule.policyName);
                        System.out.println("[AUTO-POLICY] Removed " + rule.policyName + " from " + rule.locationName + " (Infected: " + currentInfected + ")");
                    }
                    break;
                }
            }
        }
    }
                
    /**
     * Checks if a location has a policy with the given name active.
     * @param location The location
     * @param policyName Policy name
     * @return true if active, false otherwise
     */
    private boolean hasPolicyActive(Location location, String policyName) {
        for (Policy policy : location.getActivePolicies()) {
            if (policy.getName().equals(policyName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a policy to a location by name (built-in or custom).
     * @param location The location
     * @param policyName Policy name
     */
    private void addPolicyToLocation(Location location, String policyName) {
        Policy policyToAdd = null;
        switch (policyName) {
            case "Mask Mandate":
                policyToAdd = new MaskPolicy();
                break;
            case "Social Distancing":
                policyToAdd = new SocialDistancingPolicy();
                break;
            case "Lockdown":
                policyToAdd = new LockdownPolicy();
                break;
            default:
                for (Policy customPolicy : customPolicies) {
                    if (customPolicy.getName().equals(policyName)) {
                        policyToAdd = customPolicy;
                        break;
                    }
                }
        }
        if (policyToAdd != null) {
            location.addPolicy(policyToAdd);
        }
    }

    /**
     * Removes a policy from a location by name.
     * @param location The location
     * @param policyName Policy name
     */
    private void removePolicyFromLocation(Location location, String policyName) {
        Policy policyToRemove = null;
        for (Policy policy : location.getActivePolicies()) {
            if (policy.getName().equals(policyName)) {
                policyToRemove = policy;
                break;
            }
        }
        if (policyToRemove != null) {
            location.removePolicy(policyToRemove.getClass());
        }
    }

    /**
     * Resets the simulation, either removing all locations or resetting states.
     */
    private void resetSimulation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset Simulation");
        alert.setHeaderText("How do you want to reset?");
        alert.setContentText("Choose your option:");

        ButtonType removeAll = new ButtonType("Remove All Locations");
        ButtonType resetStates = new ButtonType("Reset Locations to Initial State");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(removeAll, resetStates, cancel);

        alert.showAndWait().ifPresent(type -> {
            if (type == removeAll) {
                world.getLocations().clear();
                rebuildLocationControls();
                world.reset();
                initializeDefaultValues();
                updateOutput();
            } else if (type == resetStates) {
                // Only reset people and policies, do NOT remove locations
                for (Location loc : world.getLocations()) {
                    resetLocationToInitialState(loc);
                }
                rebuildLocationControls();
                updateOutput();
            }
        });
    }

    /**
     * Resets a location's people to healthy and clears all policies.
     * @param location The location to reset
     */
    private void resetLocationToInitialState(Location location) {
        location.resetToInitialState();
    }

    /**
     * Updates the output area with the latest simulation statistics and policy status.
     */
    private void updateOutput() {
        StringBuilder output = new StringBuilder();
        output.append("=== Simulation Day ").append(world.getCurrentDay()).append(" ===\n");

        // Global stats
        Map<String, Integer> globalStats = world.getGlobalStatistics();
        output.append("Global Statistics:\n");
        output.append(String.format("  Healthy: %d, Infected: %d, Recovered: %d, Deceased:  %d\n",
            globalStats.get("Healthy"), globalStats.get("Infected"), globalStats.get("Recovered"), globalStats.get("Deceased")));

        // Location stats
        output.append("\nLocation Statistics:\n");
        for (Location location : world.getLocations()) {
            Map<String, Integer> stats = location.getStatistics();
            output.append(String.format("  %s: Healthy: %d, Infected: %d, Recovered: %d, Deceased: %d\n",
                location.getName(), stats.get("Healthy"), stats.get("Infected"), stats.get("Recovered"), stats.get("Deceased")));

            // Active policies
            if (!location.getActivePolicies().isEmpty()) {
                output.append("    Active Policies: ");
                for (Policy policy : location.getActivePolicies()) {
                    output.append(policy.getName()).append(" ");
                }
                output.append("\n");
            }
        }

        outputArea.setText(output.toString());
    }
    /**
     * Adds a location to the UI controls (used after reset or add).
     * @param location The location to add
    */
    private void addLocationToUI(Location location) {
        HBox locationBox = new HBox(10);
        locationBox.setAlignment(Pos.CENTER_LEFT);

        Label locationLabel = new Label(location.getName() + ": " + location.getPopulationSize() + " people, density " + PercentUtils.formatPercent(location.getPopulationDensity()));

        Button removeBtn = new Button("Remove");
        removeBtn.setOnAction(_ -> {
            world.getLocations().remove(location);
            saveLocationsConfig();
            rebuildLocationControls();
            updateOutput();
        });

        Button policiesBtn = new Button("Manage Policies");
        policiesBtn.setOnAction(_ -> showLocationPoliciesDialog(location));

        Button editBtn = new Button("Edit");
        editBtn.setOnAction(_ -> showEditLocationDialog(location));

        locationBox.getChildren().addAll(locationLabel, removeBtn, policiesBtn, editBtn);
        locationControls.getChildren().add(locationBox);
    }
    private void showEditLocationDialog(Location location) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Location");
    dialog.setHeaderText("Edit location details");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(location.getName());
        Spinner<Integer> populationSpinner = new Spinner<>(1, 1000000, location.getPopulationSize());
        Spinner<Double> densitySpinner = PercentUtils.createPercentSpinner(location.getPopulationDensity());

    grid.add(new Label("Name:"), 0, 0);
    grid.add(nameField, 1, 0);
    grid.add(new Label("Population:"), 0, 1);
    grid.add(populationSpinner, 1, 1);
    grid.add(new Label("Density:"), 0, 2);
    grid.add(densitySpinner, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // No connection logic
                saveLocationsConfig();
                rebuildLocationControls();
                updateOutput();
            }
        });
    }
    // Save locations and connections to config file in same folder as JAR
    private void saveLocationsConfig() {
        try {
            JSONArray locationsArr = new JSONArray();
            for (Location loc : world.getLocations()) {
                JSONObject locObj = new JSONObject();
                locObj.put("name", loc.getName());
                locObj.put("population", loc.getPopulationSize());
                locObj.put("density", loc.getPopulationDensity());
                // No connections saved
                locationsArr.put(locObj);
            }
            JSONObject root = new JSONObject();
            root.put("locations", locationsArr);
            Path jarDir = getJarDirectory();
            Files.write(jarDir.resolve("locations.json"), root.toString(2).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load locations and connections from config file in same folder as JAR
    private void loadLocationsConfig() {
        try {
            Path jarDir = getJarDirectory();
            Path configPath = jarDir.resolve("locations.json");
            if (!Files.exists(configPath)) return;
            String jsonStr = new String(Files.readAllBytes(configPath));
            JSONObject root = new JSONObject(jsonStr);
            JSONArray locationsArr = root.getJSONArray("locations");
            Map<String, Location> nameToLoc = new HashMap<>();
            world.getLocations().clear();
            // First pass: create locations
            for (int i = 0; i < locationsArr.length(); i++) {
                JSONObject locObj = locationsArr.getJSONObject(i);
                String name = locObj.getString("name");
                int pop = locObj.getInt("population");
                double dens = locObj.getDouble("density");
                Location loc = new Location(name, pop, dens);
                world.addNewLocation(loc);
                nameToLoc.put(name, loc);
            }
            // No connection logic
            rebuildLocationControls();
            updateOutput();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper to get the directory of the running JAR or class
    private Path getJarDirectory() {
        try {
            String path = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File jarFile = new File(path);
            if (jarFile.isFile()) {
                return jarFile.getParentFile().toPath();
            } else {
                return jarFile.toPath();
            }
        } catch (Exception e) {
            return Paths.get("");
        }
    }

    /**
     * Runs the simulation for the specified number of days, checking auto-policy rules each day.
     * @param days Number of days to simulate
     */
    private void runSimulation(int days) {
        for (int i = 0; i < days; i++) {
            checkAutoPolicyRules();
            world.simulateDay();
        }
        updateOutput();
    }
}