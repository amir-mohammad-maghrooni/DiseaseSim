
// DiseaseSim.states package contains all health state classes for the simulation
package DiseaseSim.states;


// Import the Person class for state logic
import DiseaseSim.model.Person;

/**
 * Represents the Deceased health state in the simulation.
 * Deceased persons cannot infect or be infected and do not change state.
 */
public class Deceased extends HealthState {
    /**
     * No action is taken for deceased persons; their state does not change.
     * @param person The person (unused)
     */
    @Override
    public void update(Person person) {
        // No action for deceased
    }

    /**
     * Gets the name of this health state.
     * @return "Deceased"
     */
    @Override
    public String getStateName() {
        return "Deceased";
    }

    /**
     * Deceased persons cannot infect others.
     * @return false
     */
    @Override
    public boolean canInfect() {
        return false;
    }

    /**
     * Deceased persons cannot be infected.
     * @return false
     */
    @Override
    public boolean canBeInfected() {
        return false;
    }
}
