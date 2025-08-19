
// DiseaseSim.policies package contains all policy-related classes for the simulation
package DiseaseSim.policies;

/**
 * Represents a lockdown policy that can be applied to a location in the simulation.
 * Lockdown reduces disease transmission but has a significant economic impact.
 */
public class LockdownPolicy implements Policy {
    // Multiplier for disease transmission rate (0.3 means 70% reduction)
    private static final double EFFECTIVENESS = 0.3;

    /**
     * Gets the transmission modifier for this policy.
     * @return Transmission rate multiplier
     */
    @Override
    public double getTransmissionModifier() {
        return EFFECTIVENESS;
    }

    /**
     * Gets the name of this policy.
     * @return Policy name
     */
    @Override
    public String getName() {
        return "Lockdown";
    }

    /**
     * Gets the description of this policy.
     * @return Policy description
     */
    @Override
    public String getDescription() {
        return "Reduces transmission by 70%";
    }
}
