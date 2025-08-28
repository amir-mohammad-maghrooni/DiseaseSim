package DiseaseSim.policies;

/**
 * Interface for all policy types in the simulation.
 * Policies affect disease transmission and provide descriptive information.
 */
public interface Policy {
    /**
     * Gets the transmission modifier for this policy.
     * @return Transmission rate multiplier (e.g., 0.7 for 30% reduction)
     */
    double getTransmissionModifier();

    /**
     * Gets the name of this policy.
     * @return Policy name
     */
    String getName();

    /**
     * Gets the description of this policy.
     * @return Policy description
     */
    String getDescription();
}
