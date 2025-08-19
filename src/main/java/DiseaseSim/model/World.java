
// DiseaseSim.model package contains all model classes for the simulation
package DiseaseSim.model;

import java.util.*;

/**
 * Represents the entire simulation world, containing multiple locations and a disease.
 * Manages simulation state, global statistics, and controls simulation flow.
 */
public class World {
    // List of all locations in the world
    private List<Location> locations;
    // The disease currently being simulated
    private Disease disease;
    // The current day of the simulation
    private int currentDay;
    // Whether the simulation is running or paused
    private boolean isRunning;

    /**
     * Constructs a new World with no locations and simulation paused.
     */
    public World(){
        this.locations = new ArrayList<>();
        this.currentDay = 0;
        this.isRunning = false;
    }

    /**
     * Initializes the world with default locations and a default disease.
     * Infects a small number of people in each location at the start.
     */
    public void initializeWorld(){
        locations.add(new Location("Metro City", 150000, 0.8));
        locations.add(new Location("Suburbs", 55000, 0.4));
        locations.add(new Location("Rural Town", 15000, 0.2));

        disease = new Disease("COVID-19", 0.3, 0.02, 0.1, 5);

        // Infect 10 people in each location at the start
        locations.get(0).introduceDisease(disease, 10);
        locations.get(1).introduceDisease(disease, 10);
        locations.get(2).introduceDisease(disease, 10);
    }

    /**
     * Simulates a single day for all locations in the world.
     * Only runs if the simulation is currently running.
     * Increments the current day counter.
     */
    public void simulateDay(){
        if (!isRunning) return;

        for (Location location : locations) {
            location.simulateDay();
        }

        currentDay++;
    }

    /**
     * Aggregates statistics from all locations to provide global health state counts.
     * @return Map with keys: "Healthy", "Infected", "Recovered", "Deceased"
     */
    public Map<String, Integer> getGlobalStatistics(){
        Map<String, Integer> globalStats = new HashMap<>();
        globalStats.put("Healthy", 0);
        globalStats.put("Infected", 0);
        globalStats.put("Recovered", 0);
        globalStats.put("Deceased", 0);

        for(Location location : locations) {
            Map<String, Integer> locationStats = location.getStatistics();
            for (String state: locationStats.keySet()){
                globalStats.put(state, globalStats.get(state) + locationStats.get(state));
            }
        }

        return globalStats;
    }

    /**
     * Starts the simulation (sets isRunning to true).
     */
    public void start(){
        isRunning = true;
    }

    /**
     * Pauses the simulation (sets isRunning to false).
     */
    public void pause(){
        isRunning = false;
    }

    /**
     * Resets the simulation to its initial state, clearing all locations and restarting the world.
     */
    public void reset(){
        currentDay = 0;
        isRunning = false;
        locations.clear();
        initializeWorld();
    }

    /**
     * Gets the list of all locations in the world.
     * @return List of Location objects
     */
    public List<Location> getLocations() { 
        return locations; 
    }

    /**
     * Adds a new location to the world.
     * @param location The Location object to add
     */
    public void addNewLocation(Location location){
        locations.add(location);
    }
    
    /**
     * Gets the current day of the simulation.
     * @return Current day (int)
     */
    public int getCurrentDay() { 
        return currentDay; 
    }
    
    /**
     * Checks if the simulation is currently running.
     * @return true if running, false if paused
     */
    public boolean isRunning() { 
        return isRunning; 
    }
    
    /**
     * Gets the Disease object currently being simulated.
     * @return Disease instance
     */
    public Disease getDisease() { 
        return disease; 
    }

    /**
     * Sets a new Disease for the world with the given parameters.
     * @param name Name of the disease
     * @param transmissionRate Transmission rate
     * @param mortalityRate Mortality rate
     * @param recoveryRate Recovery rate
     * @return The new Disease object
     */
    public Disease setDisease(String name, double transmissionRate, double mortalityRate, double recoveryRate, int minDaysInfected){
        disease = new Disease(name, transmissionRate, mortalityRate, recoveryRate, minDaysInfected);
        return disease;
    }
}
