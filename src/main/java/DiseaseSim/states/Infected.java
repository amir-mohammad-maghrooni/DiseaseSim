package DiseaseSim.states;


// Import the Person class for state logic
import DiseaseSim.model.Person;
// Import the Disease class for accessing disease properties
import DiseaseSim.model.Disease;

/**
 * Represents the Infected health state in the simulation.
 * Infected persons can infect others and may recover or die after a minimum number of days.
 */
public class Infected extends HealthState {


    /**
     * Updates the person's state for a new day. After a minimum number of days,
     * performs a probabilistic roll to determine if the person recovers, dies, or remains infected.
     * @param person The person being updated
     */
    @Override
    public void update(Person person) {
        person.incrementDaysInState();
        // Try to get from Disease object if available and set a fallback default
        // This allows for dynamic disease properties based on the person's location
        Disease disease = person.getLocation().getDisease();
        int minDaysInfected = (disease != null) ? disease.getMinDaysInfected() : 7; // fallback default

        // Only roll for recovery/mortality after the minimum number of days
        if (person.getDaysInCurrentState() >= minDaysInfected) {
            // Get recovery and mortality rates from the Disease object via the person's location
            double recoveryRate = (disease != null) ? disease.getRecoveryRate() : 0.1; // fallback default
            double mortalityRate = (disease != null) ? disease.getMortalityRate() : 0.02; // fallback default

            // Roll a random number to determine outcome
            double roll = Math.random();
            if  (roll < mortalityRate){
                // If the roll is less than mortality rate, person dies
                // Transition to Deceased state
                person.setState(new Deceased());
            } else if (roll < mortalityRate + recoveryRate) {
                // If the roll is less than the sum of mortality and recovery rates, person recovers
                // Transition to Recovered states
                person.setState(new Recovered());
            }
            // else: remain infected
        }
    }
    
    /**
     * Gets the name of this health state.
     * @return "Infected"
     */
    @Override
    public String getStateName(){
        return "Infected";
    }

    /**
     * Infected persons can infect others.
     * @return true
     */
    @Override
    public boolean canInfect() {
        return true;
    }

    /**
     * Infected persons cannot be infected again.
     * @return false
     */
    @Override
    public boolean canBeInfected() {
        return false; 
    }
}
