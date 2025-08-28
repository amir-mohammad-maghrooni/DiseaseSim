package DiseaseSim.policies;

/**
 * Represents a user-defined policy that can be applied to a location in the simulation.
 * Custom policies can have a name, description, and a transmission modifier.
 */
public class CustomPolicy implements Policy {
    // Name of the policy (e.g., "Curfew")
    private final String name;
    // Description of what the policy does
    private final String description;
    // Multiplier for disease transmission rate (e.g., 0.5 for 50% reduction)
    private final double transmissionModifier;

    /**
     * Constructs a new CustomPolicy with the given parameters.
     * @param name Name of the policy
     * @param description Description of the policy
     * @param transmissionModifier Multiplier for disease transmission rate
     */
    public CustomPolicy(String name, String description, double transmissionModifier) {
        this.name = name;
        this.description = description;
        this.transmissionModifier = transmissionModifier;
    }

    /**
     * Gets the transmission modifier for this policy.
     * @return Transmission rate multiplier
     */
    @Override
    public double getTransmissionModifier() {
        return transmissionModifier;
    }

    /**
     * Gets the name of this policy.
     * @return Policy name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the description of this policy.
     * @return Policy description
     */
    @Override
    public String getDescription() {
        return description;
    }
}
