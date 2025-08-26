

// DiseaseSim.model package contains all model classes for the simulation
package DiseaseSim.model;

import DiseaseSim.policies.Policy;
import DiseaseSim.states.Infected;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a physical location (city, town, etc.) in the simulation.
 * Each location has a population, policies, and a disease state.
 */
public class Location {
    /**
     * Gets the Disease object currently affecting this location.
     * @return Disease instance or null if none
     */
    public Disease getDisease() {
        return disease;
    }
    /**
     * Resets all people in this location to healthy and clears all active policies.
     * Used when resetting the simulation or location.
     */
    public void resetToInitialState() {
        for (Person p : population) {
            p.setState(new DiseaseSim.states.Healthy());
        }
        activePolicies.clear();
    }
    /**
     * Sets the number of initially infected people in this location.
     * @param initialInfected Number of people to infect at the start
     */
    public void setInitialInfected(int initialInfected) {
        if (disease != null) {
            introduceDisease(disease, initialInfected);
        }
    }
    // Name of the location (e.g., "Metro City")
    private String name;
    // List of all people in this location
    private List<Person> population;
    // Set of currently active policies (e.g., mask mandate)
    private Set<Policy> activePolicies;
    // The disease currently affecting this location
    private Disease disease;
    // Population density (0.0-1.0, where 1.0 is max density)
    private double populationDensity;
    private Set<Location> connections = new HashSet<>();

    /**
     * Constructs a Location with the given name, population size, and density.
     * Initializes the population as healthy people.
     * @param name Name of the location
     * @param populationSize Number of people in the location
     * @param populationDensity Density (0.0-1.0)
     */
    public Location(String name, int populationSize, double populationDensity){
        this.name = name;
        this.populationDensity = populationDensity;
        this.population = new ArrayList<>();
        this.activePolicies = new HashSet<>();

        // Initialize population with healthy people
        for (int i = 0; i < populationSize; i++){
            population.add(new Person(i, this));
        }
    }

    /**
     * Simulates a single day in this location:
     * - Infectious people interact with susceptibles
     * - All people update their health state
     */
    public void simulateDay() {
        List<Person> infectedPeople = population.stream()
                .filter(Person::canInfect)
                .collect(Collectors.toList());

        List<Person> susceptiblePeople = population.stream()
                .filter(Person:: canBeInfected)
                .collect(Collectors.toList());
        
        double policyModifier = calculatePolicyModifier();

        for (Person infected : infectedPeople) {
            simulateInteractions(infected, susceptiblePeople, policyModifier);
        }

        for (Person person : population) {
            person.updateDaily();
        }
    }

    /**
     * Simulates interactions between an infected person and susceptibles.
     * Each infected person interacts with a number of susceptibles based on density and policy.
     * @param infected The infected person
     * @param susceptiple List of susceptible people
     * @param policyModifier Modifier for transmission rate
     */
    public void simulateInteractions(Person infected, List<Person> susceptiple, double policyModifier) {
        int contactsPerDay = (int) (populationDensity * 10 * policyModifier);

        for (int i = 0; i < contactsPerDay && i < susceptiple.size(); i++) {
            Person target = susceptiple.get((int) (Math.random() * susceptiple.size()));

            if (Math.random() < disease.getEffectiveTransmissionRate(policyModifier)){
                target.attemptinfection(disease);
            }
        }
    }

    /**
     * Calculates the combined effect of all active policies on transmission rate.
     * @return Policy modifier (multiplier for transmission rate)
     */
    public double calculatePolicyModifier() {
        double modifier = 1.0;
        for (Policy policy : activePolicies) {
            modifier *= policy.getTransmissionModifier();
        }
        return modifier;
    }

    /**
     * Introduces a disease to this location and infects a number of people.
     * @param disease The Disease object
     * @param initialInfected Number of people to infect
     */
    public void introduceDisease(Disease disease, int initialInfected){
        this.disease = disease;

        Collections.shuffle(population);
        int infectedCount = 0;
        for (Person p : population) {
            if (infectedCount >= initialInfected) break;
            if (p.canBeInfected()) {
                p.setState(new Infected());
                infectedCount++;
            }
        }
    }

    /**
     * Returns a map of health state names to counts for this location's population.
     * Used for statistics and UI display.
     * @return Map with keys: "Healthy", "Infected", "Recovered", "Deceased"
     */
    public Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("Healthy", 0);
        stats.put("Infected", 0);
        stats.put("Recovered", 0);
        stats.put("Deceased", 0);

        for (Person person : population) {
            String state = person.getStateString();
            stats.put(state, stats.get(state) +1 );
        }

        return stats;
    }
    
    /**
     * Checks if this location is connected to another location.
     * @param other The other Location
     * @return true if connected, false otherwise
     */
    /**
     * Connects this location to another location (modular, for GUI).
     */
    public void connectTo(Location other) {
        connections.add(other);
    }

    /**
     * Disconnects this location from another location.
     */
    public void disconnectFrom(Location other) {
        connections.remove(other);
    }

    /**
     * Checks if this location is connected to another location.
     */
    public boolean isConnectedTo(Location other) {
        return connections.contains(other);
    }
    /**
     * Adds a list of people to this location's population.
     * @param newPeople List of Person objects to add
     */
    /**
     * Adds a list of people to this location's population.
     */
    public void addPeople(List<Person> peopleToAdd){
        this.population.addAll(peopleToAdd);
    }
    /**
     * Removes a list of people from this location's population.
     * @param peopleToRemove List of Person objects to remove
     */
    /**
     * Removes a list of people from this location's population.
     */
    public void removePeople(List<Person> peopleToRemove){
        this.population.removeAll(peopleToRemove);
    }
    /**
     * Returns the list of people currently in this location.
     */
    public List<Person> getPeople() {
        return population;
    }

    /**
     * Adds a policy to this location's set of active policies.
     * @param policy Policy to add
     */
    public void addPolicy(Policy policy) {
        activePolicies.add(policy);
    }

    /**
     * Removes a policy of the given type from the set of active policies.
     * @param policyType Class of the policy to remove
     */
    public void removePolicy(Class<? extends Policy> policyType) {
        activePolicies.removeIf(p -> p.getClass().equals(policyType));
    }

    /**
     * Checks if a policy of the given type is currently active in this location.
     * @param policyType Class of the policy
     * @return true if active, false otherwise
     */
    public boolean hasPolicyActive(Class<? extends Policy> policyType) {
        return activePolicies.stream()
        .anyMatch(p -> p.getClass().equals(policyType));
    }

    // Getters

    /**
     * Gets the name of this location.
     * @return Location name
     */
    public String getName() { 
        return name; 
    }

    /**
     * Gets the number of people in this location.
     * @return Population size
     */
    public int getPopulationSize() { 
        return population.size(); 
    }

    /**
     * Gets a copy of the set of active policies for this location.
     * @return Set of active policies
     */
    public Set<Policy> getActivePolicies() { 
        return new HashSet<>(activePolicies); 
    }

    /**
     * Gets the population density (0.0-1.0) for this location.
     * @return Population density
     */
    public double getPopulationDensity() {
        return populationDensity;
    }
}
