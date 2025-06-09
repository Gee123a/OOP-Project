package Model;

import java.util.Random;

public class Village {
    private int population;
    private int infectedCount;
    private int recoveredCount;
    private int deathCount;
    private int playerRecoveries; // NEW: Track player-caused recoveries
    private int naturalRecoveries; // NEW: Track automatic recoveries
    private int trustLevel;
    private int cleanliness;
    private boolean quarantineActive;
    private int educationLevel;
    private Random random;

    // Constants
    private double BASE_INFECTION_RATE = 0.25;
    private double BASE_RECOVERY_RATE = 0.08;
    private double BASE_MORTALITY_RATE = 0.08;

    public Village() {
        this.population = 200;
        this.infectedCount = 25;
        this.recoveredCount = 0;
        this.deathCount = 0;
        this.playerRecoveries = 0;
        this.naturalRecoveries = 0;
        this.trustLevel = 30;
        this.cleanliness = 25;
        this.quarantineActive = false;
        this.educationLevel = 5;
        this.random = new Random();
    }

    public void updateDailyStatus() {
        // Calculate new infections, recoveries, and deaths
        int newInfections = calculateNewInfections();
        int newNaturalRecoveries = calculateNewRecoveries(); // Only natural recoveries
        int deaths = calculateDeaths();

        // Update counts - now accounting for both types of recoveries
        infectedCount = Math.max(0, infectedCount + newInfections - newNaturalRecoveries - deaths);
        naturalRecoveries += newNaturalRecoveries;
        recoveredCount = playerRecoveries + naturalRecoveries; // Total recoveries
        deathCount += deaths;
        population -= deaths;

        // Update village cleanliness based on education
        if (educationLevel > 50) {
            cleanliness += 2;
        } else {
            cleanliness -= 1;
        }
        cleanliness = Math.max(0, Math.min(100, cleanliness));
    }

    private int calculateNewInfections() {
        // Base infection calculation
        double infectionRate = BASE_INFECTION_RATE;

        // Apply modifiers
        if (quarantineActive)
            infectionRate *= 0.7;
        infectionRate *= (1.0 - (educationLevel / 200.0)); // Education reduces spread
        infectionRate *= (1.0 - (cleanliness / 200.0)); // Cleanliness reduces spread

        // Calculate infections
        int healthyPopulation = population - infectedCount - recoveredCount;
        int potentialNewInfections = (int) (healthyPopulation * infectionRate * (infectedCount / 20.0));

        // Add some randomness
        potentialNewInfections = Math.max(0, potentialNewInfections + random.nextInt(5) - 2);

        // Cannot infect more than there are healthy people
        return Math.min(potentialNewInfections, healthyPopulation);
    }

    private int calculateNewRecoveries() {
        // Base recovery calculation
        double recoveryRate = BASE_RECOVERY_RATE;

        // Apply modifiers
        recoveryRate += (cleanliness / 300.0); // Cleaner village = better recovery
        recoveryRate += (trustLevel / 400.0); // Trust in doctor = better compliance

        // Calculate recoveries
        int potentialRecoveries = (int) (infectedCount * recoveryRate);

        // Add randomness
        potentialRecoveries = Math.max(0, potentialRecoveries + random.nextInt(3) - 1);

        return Math.min(potentialRecoveries, infectedCount);
    }

    private int calculateDeaths() {
        // Base death calculation
        double mortalityRate = BASE_MORTALITY_RATE;

        // Apply modifiers
        mortalityRate *= (1.0 - (trustLevel / 300.0)); // Trust reduces deaths

        // Calculate deaths
        int potentialDeaths = (int) (infectedCount * mortalityRate);

        // Add randomness
        potentialDeaths = Math.max(0, potentialDeaths + random.nextInt(2) - 1);

        return potentialDeaths;
    }

    // Player-induced recovery (called from treatPatients)
    public void recoverVillager() {
        if (infectedCount > 0) {
            infectedCount--;
            playerRecoveries++;
            recoveredCount = playerRecoveries + naturalRecoveries; // Update total
        }
    }

    // Village modification methods
    public void improveTrust(int amount) {
        trustLevel = Math.min(trustLevel + amount, 100);
    }

    public void lowerTrust(int amount) {
        trustLevel = Math.max(trustLevel - amount, 0);
    }

    public void setQuarantine(boolean active) {
        this.quarantineActive = active;
    }

    public void improveEducation(int amount) {
        educationLevel = Math.min(educationLevel + amount, 100);
    }

    public void improveCleanliness(int amount) {
        cleanliness = Math.min(cleanliness + amount, 100);
    }

    // Getters
    public int getPopulation() {
        return population;
    }

    public int getInfectedCount() {
        return infectedCount;
    }

    public int getRecoveredCount() {
        return recoveredCount;
    }

    public int getDeathCount() {
        return deathCount;
    }

    public int getTrustLevel() {
        return trustLevel;
    }

    public int getCleanliness() {
        return cleanliness;
    }

    public boolean isQuarantineActive() {
        return quarantineActive;
    }

    public int getEducationLevel() {
        return educationLevel;
    }

    // NEW: Getters for detailed statistics
    public int getPlayerRecoveries() {
        return playerRecoveries;
    }

    public int getNaturalRecoveries() {
        return naturalRecoveries;
    }
}