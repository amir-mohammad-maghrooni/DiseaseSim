
package DiseaseSim.model;


// Import all health state classes (Healthy, Infected, etc.)
import DiseaseSim.states.*;

/**
 * Represents a single person in the simulation.
 * Each person has a health state, location, and tracks days in their current state.
 */
public class Person {
    // The current health state of the person (Healthy, Infected, etc.)
    private HealthState currentState;
    // The location this person belongs to
    private Location location;
    // Number of days spent in the current health state
    private int daysInCurrentState;
    // Unique identifier for this person
    private int id;

    /**
     * Constructs a new Person with a unique ID and assigned location.
     * Starts as Healthy and with 0 days in state.
     * @param id Unique identifier
     * @param location The location this person belongs to
     */
    public Person(int id, Location location) {
        this.id = id;
        this.location = location;
        this.currentState = new Healthy();
        this.daysInCurrentState = 0;
    }

    /**
     * Updates the person's state for a new day by delegating to the current HealthState.
     * This may result in state transitions (e.g., Infected to Recovered).
     */
    public void updateDaily() {
        currentState.update(this);
    }

    /**
     * Attempts to infect this person with the given disease.
     * Only succeeds if the person can be infected and the disease roll passes.
     * @param disease The Disease object
     * @return true if infection occurred, false otherwise
     */
    public boolean attemptinfection(Disease disease) {
        Double effectiveRate = null;
        // Check if the person is in a state that can be infected
        if (currentState instanceof Recovered){
             effectiveRate = 0.5;
        }
        if (currentState.canBeInfected()){
            if (disease.rollForInfection(effectiveRate)){
                setState(new Infected());
                return true;
            }
        }
        return false;
    }

    
    // Getters and setters

    /**
     * Gets the current HealthState object for this person.
     * @return Current HealthState
     */
    public HealthState getCurrentState() { 
        return currentState; 
    }

    /**
     * Gets the name of the current health state (e.g., "Healthy", "Infected").
     * @return State name as string
     */
    public String getStateString() { 
        return currentState.getStateName(); 
    }

    /**
     * Checks if this person can infect others (depends on state).
     * @return true if can infect, false otherwise
     */
    public boolean canInfect() { 
        return currentState.canInfect(); 
    }

    /**
     * Checks if this person can be infected (depends on state).
     * @return true if can be infected, false otherwise
     */
    public boolean canBeInfected() { 
        return currentState.canBeInfected(); 
    }

    /**
     * Gets the location this person belongs to.
     * @return Location object
     */
    public Location getLocation() { 
        return location; 
    }

    /**
     * Gets the number of days spent in the current health state.
     * @return Days in current state
     */
    public int getDaysInCurrentState() { 
        return daysInCurrentState; 
    }

    /**
     * Gets the unique identifier for this person.
     * @return Person ID
     */
    public int getId() { 
        return id; 
    }

    /**
     * Sets the person's health state and resets days in state to 0.
     * @param newState New HealthState
     */
    public void setState(HealthState newState) {
        this.currentState = newState;
        this.daysInCurrentState = 0;
    }

    /**
     * Increments the number of days spent in the current health state by 1.
     */
    public void incrementDaysInState() {
        this.daysInCurrentState++;
    }
}
