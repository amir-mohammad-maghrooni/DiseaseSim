
// DiseaseSim.policies package contains all policy-related classes for the simulation
package DiseaseSim.policies;

/**
 * Represents a mask mandate policy that can be applied to a location in the simulation.
 * Mask mandates reduce disease transmission by a fixed percentage.
 */
public class MaskPolicy implements Policy {
    // Multiplier for disease transmission rate (0.7 means 30% reduction)
    private static final double EFFECTIVENESS = 0.7;

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
        return "Mask Mandate";
    }

    /**
     * Gets the description of this policy.
     * @return Policy description
     */
    @Override
    public String getDescription() {
        return "Reduces transmission by 30%";
    }
    
    
}
