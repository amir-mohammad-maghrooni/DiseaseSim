package DiseaseSim.policies;

/**
 * Represents a social distancing policy that can be applied to a location in the simulation.
 * Social distancing reduces disease transmission by a fixed percentage.
 */
public class SocialDistancingPolicy implements Policy {
    // Multiplier for disease transmission rate (0.5 means 50% reduction)
    private static final double EFFECTIVENESS = 0.5;

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
        return "Social Distancing";
    }

    /**
     * Gets the description of this policy.
     * @return Policy description
     */
    @Override
    public String getDescription() {
        return "Reduce Transmission by 50%";
    }
    
}
