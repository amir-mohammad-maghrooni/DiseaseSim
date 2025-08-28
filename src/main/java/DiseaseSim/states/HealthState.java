package DiseaseSim.states;


// Import the Person class for state transitions and updates
import DiseaseSim.model.Person;

/**
 * Abstract base class for all health states in the simulation (e.g., Healthy, Infected, Recovered).
 * Defines the interface for state transitions and infection logic.
 */
public abstract class HealthState {
    /**
     * Updates the person's state for a new day. Handles state-specific logic and transitions.
     * @param person The person whose state is being updated
     */
    public abstract void update(Person person);

    /**
     * Gets the name of this health state (e.g., "Healthy", "Infected").
     * @return State name as string
     */
    public abstract String getStateName();

    /**
     * Checks if a person in this state can infect others.
     * @return true if can infect, false otherwise
     */
    public abstract boolean canInfect();

    /**
     * Checks if a person in this state can be infected.
     * @return true if can be infected, false otherwise
     */
    public abstract boolean canBeInfected();
}
