package DiseaseSim.states;


// Import the Person class for state logic
import DiseaseSim.model.Person;

/**
 * Represents the Healthy health state in the simulation.
 * Healthy persons cannot infect others but can become infected.
 */
public class Healthy extends HealthState {
    /**
     * Updates the person's state for a new day. No action for healthy persons.
     * @param person The person (unused)
     */
    @Override
    public void update(Person person){
        // No action for healthy persons
    }

    /**
     * Gets the name of this health state.
     * @return "Healthy"
     */
    @Override
    public String getStateName(){
        return "Healthy";
    }

    /**
     * Healthy persons cannot infect others.
     * @return false
     */
    @Override
    public boolean canInfect() {
        return false;
    }

    /**
     * Healthy persons can be infected.
     * @return true
     */
    @Override
    public boolean canBeInfected() {
        return true;
    }
}
