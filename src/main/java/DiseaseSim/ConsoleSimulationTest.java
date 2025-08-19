
// DiseaseSim package contains the main simulation entry points
package DiseaseSim;



// Import model and policy classes for simulation
import DiseaseSim.model.*;
import DiseaseSim.policies.*;

import java.util.Map;


/**
 * ConsoleSimulationTest runs a text-based simulation of disease spread and policy effects.
 * It demonstrates the simulation logic, policy application, and outputs statistics to the console.
 */
public class ConsoleSimulationTest{
    /**
     * Main entry point for the console simulation.
     * Initializes the world, applies initial policies, runs the simulation loop, and prints results.
     * @param args Command-line arguments (unused)
     */
    public static void main(String[] args){
        // Create and initialize the simulation world
        World world = new World();
        world.initializeWorld();

        System.out.println("=== Disease Simulation Test ===");
        System.out.println("Day 0: Initial State");
        printStatistics(world);

        // Apply initial policies to locations
        world.getLocations().get(0).addPolicy(new MaskPolicy());
        System.out.println("\nApplying Mask Policy to Metro City");

        world.getLocations().get(1).addPolicy(new SocialDistancingPolicy());
        System.out.println("\nApplying Social Distancing Policy to Suburbs");

        // Start the simulation and run for 100 days
        world.start();
        for (int day = 1; day <= 100; day++){
            world.simulateDay();

            // Every 10 days, check infection counts and add/remove lockdowns as needed
            if (day % 10 == 0) {
                for (Location location : world.getLocations()) {
                    Map<String, Integer> stats = location.getStatistics();
                    int infectedCount = stats.get("Infected");
                    // Dynamically apply or remove lockdowns based on infection thresholds
                    switch (location.getName()) {
                        case "Metro City":
                            if (infectedCount > 3000 && !location.hasPolicyActive(LockdownPolicy.class)) {
                                location.addPolicy(new LockdownPolicy());
                                System.out.println("\nDay " + day + ": Added Lockdown to Metro City due to high infection.");
                            } else if (infectedCount < 500 && location.hasPolicyActive(LockdownPolicy.class)) {
                                location.removePolicy(LockdownPolicy.class);
                                System.out.println("\nDay " + day + ": Removed Lockdown from Metro City as infections dropped.");
                            }
                            break;
                        case "Suburbs":
                            if (infectedCount > 2000 && !location.hasPolicyActive(LockdownPolicy.class)) {
                                location.addPolicy(new LockdownPolicy());
                                System.out.println("\nDay " + day + ": Added Lockdown to Suburbs due to high infection.");
                            } else if (infectedCount < 300 && location.hasPolicyActive(LockdownPolicy.class)) {
                                location.removePolicy(LockdownPolicy.class);
                                System.out.println("\nDay " + day + ": Removed Lockdown from Suburbs as infections dropped.");
                            }
                            break;
                        case "Rural Town":
                            if (infectedCount > 1000 && !location.hasPolicyActive(LockdownPolicy.class)) {
                                location.addPolicy(new LockdownPolicy());
                                System.out.println("\nDay " + day + ": Added Lockdown to Rural Town due to high infection.");
                            } else if (infectedCount < 50 && location.hasPolicyActive(LockdownPolicy.class)) {
                                location.removePolicy(LockdownPolicy.class);
                                System.out.println("\nDay " + day + ": Removed Lockdown from Rural Town as infections dropped.");
                            }
                            break;
                    }
                }
            }

            // Every 5 days, print statistics and policy status
            if (day % 5 == 0) {
                System.out.println("\nDay " + day + ":");
                printStatistics(world);
                printPolicyStatus(world);
            }
        }

        // Print final results
        System.out.println("\n=== Final Results ===");
        printStatistics(world);
        printPolicyStatus(world);
    }


    /**
     * Prints global and per-location statistics to the console.
     * @param world The simulation world
     */
    private static void printStatistics(World world){
        Map<String, Integer> stats = world.getGlobalStatistics();
        System.out.println("Global: Healthy: " + stats.get("Healthy") +
                ", Infected: " + stats.get("Infected") +
                ", Recovered: " + stats.get("Recovered"));
        
        for (Location location : world.getLocations()) {
            Map<String, Integer> locStats = location.getStatistics();
            System.out.println("Location: " + location.getName() +
                    " - Healthy: " + locStats.get("Healthy") +
                    ", Infected: " + locStats.get("Infected") +
                    ", Recovered: " + locStats.get("Recovered"));
        }
    }

    /**
     * Prints the active policies for each location in the world.
     * @param world The simulation world
     */
    private static void printPolicyStatus(World world) {
        System.out.println("Active Policies:");
        for (Location location : world.getLocations()){
            System.out.print(" " + location.getName() + ": ");
            if (location.getActivePolicies().isEmpty()){
                System.out.println("None");
            } else {
                for (Policy policy : location.getActivePolicies()){
                    System.out.print(policy.getName() + " ");
                }
                System.out.println();
            }
        }
    }
}