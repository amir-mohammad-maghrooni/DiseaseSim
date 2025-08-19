
// DiseaseSim.model package contains all model classes for the simulation
package DiseaseSim.model;

/**
 * Represents a disease in the simulation, with parameters for transmission, mortality, and recovery.
 */
public class Disease {
    // Name of the disease (e.g., "COVID-19")
    private String name;
    // Probability (0.0-1.0) that the disease is transmitted per contact
    private double transmissionRate;
    // Probability (0.0-1.0) that an infected person dies after the minimum infection period
    private double mortalityRate;
    // Probability (0.0-1.0) that an infected person recovers after the minimum infection period
    private double recoveryRate;
    // Minimum number of days a person must be infected before recovery/mortality rolls
    private int minDaysInfected;
    
    /**
     * Constructs a Disease with the given parameters.
     * @param name Name of the disease
     * @param transmissionRate Probability of transmission per contact (0.0-1.0)
     * @param mortalityRate Probability of death after infection (0.0-1.0)
     * @param recoveryRate Probability of recovery after infection (0.0-1.0)
     */
    public Disease(String name, double transmissionRate, double mortalityRate, double recoveryRate, int minDaysInfected) {
        this.name = name;
        this.transmissionRate = transmissionRate;
        this.mortalityRate = mortalityRate;
        this.recoveryRate = recoveryRate;
        this.minDaysInfected = minDaysInfected;
    }

    /**
     * Determines if a contact results in infection, based on the transmission rate.
     * @return true if infection occurs, false otherwise
     */
    public boolean rollForInfection(Double Modifier) {
        if (Modifier == null) {
            Modifier = 1.0; // Default to no modifier if null
        }
        return Math.random() * Modifier < transmissionRate;
    }

    /**
     * Returns the effective transmission rate after applying policy modifiers.
     * @param policyModifier Multiplier for transmission rate (e.g., from mask or distancing policies)
     * @return Effective transmission rate
     */
    public double getEffectiveTransmissionRate(double policyModifier) {
        return transmissionRate * policyModifier;
    }

    // Getters and setters

    /**
     * Gets the base transmission rate (before policy modifiers).
     * @return Transmission rate (0.0-1.0)
     */
    public double getTransmissionRate() { 
        return transmissionRate; 
    }

    /**
     * Gets the name of the disease.
     * @return Disease name
     */
    public String getName() { 
        return name; 
    }

    /**
    * Gets the probability of recovery after infection.
    * @return Recovery rate (0.0-1.0)
    */
    public double getRecoveryRate() {
        return recoveryRate;
    }

    /**
     * Gets the probability of death after infection.
     * @return Mortality rate (0.0-1.0)
    */
    public double getMortalityRate() {
        return mortalityRate;
    }
    /**
     * Gets the minimum number of days a person must be infected before recovery/mortality rolls.
     * @return Minimum days infected
    */
    public int getMinDaysInfected() {
        return minDaysInfected;
    }

    /**
     * Sets the name of the disease.
     * @param name New disease name
    */
    public void setName(String name) { 
        this.name = name;
    }

    /**
     * Sets the base transmission rate (used for updating via UI).
     * @param rate New transmission rate (0.0-1.0)
    */
    public void setBaseTransmissionRate(double rate) { 
        this.transmissionRate = rate; 
    }


    /**
     * Sets the mortality rate for the disease.
     * @param rate New mortality rate (0.0-1.0)
    */
    public void setMortalityRate(double rate) { 
        this.mortalityRate = rate;
    }

    /**
     * Sets the recovery rate for the disease.
     * @param rate New recovery rate (0.0-1.0)
    */
    public void setRecoveryRate(double rate) { 
        this.recoveryRate = rate;
    }

    /**
     * Sets the minimum number of days a person must be infected before recovery/mortality rolls.
     * @param minDays New minimum days infected
    */
    public void setMinDaysInfected(int minDays) { 
        this.minDaysInfected = minDays;
    }



   
}
