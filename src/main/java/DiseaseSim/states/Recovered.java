
// DiseaseSim.states package contains all health state classes for the simulation
package DiseaseSim.states;


// Import the Person class for state logic
import DiseaseSim.model.Person;

/**
 * Represents the Recovered health state in the simulation.
 * Recovered persons have permanent immunity and cannot infect or be infected again.
 */
public class Recovered extends HealthState {
    /**
     * Updates the person's state for a new day. No action for recovered persons (permanent immunity).
     * @param person The person (unused)
     */
    @Override
    public void update(Person person) {
        // No action needed for recovered state (partial immunity, 50% chance of reinfection)
    }

    /**
     * Gets the name of this health state.
     * @return "Recovered"
     */
    @Override
    public String getStateName() {
        return "Recovered";
    }

    /**
     * Recovered individuals cannot infect others.
     * @return false
     */
    @Override
    public boolean canInfect() {
        return false; // Recovered individuals cannot infect others
    }

    /**
     * Recovered individuals cannot be infected again (permanent immunity).
     * @return false
     */
    @Override
    public boolean canBeInfected() {
        return true; // Recovered individuals can be infected again, at a lower rate
    }
}
