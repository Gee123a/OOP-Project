package Model;

import java.util.HashMap;
import java.util.Map;

public class Player extends Character {
    private int cleanliness;
    private int energy;
    private Map<String, Integer> inventory; // Using HashMap for efficient lookups
    private int doctorSkill;

    public Player() {
        super("Plague Doctor", 100, false); // Name, health, isSick
        this.cleanliness = 80;
        this.energy = 100;
        this.doctorSkill = 5;
        this.inventory = initializeInventory();
    }

    private Map<String, Integer> initializeInventory() {
        Map<String, Integer> items = new HashMap<>();
        items.put("herbs", 2);
        items.put("protective_gear", 1);
        items.put("soap", 1);
        items.put("coins", 5);
        return items;
    }

    // Add a method to handle special quest items
    public boolean hasQuestItem(String itemName) {
        return hasItem(itemName);
    }

    // Add method to use advanced medicine (quest reward)
    public void useAdvancedMedicine() {
        if (hasItem("advanced_medicine")) {
            useItem("advanced_medicine", 1);
            setHealth(Math.min(getHealth() + 25, 100));
            System.out.println("You use advanced medicine. Your health improves significantly!");
        }
    }

    @Override
    public void updateDailyStatus() {
        // Health decreases if cleanliness is low
        if (cleanliness < 30) {
            setHealth(getHealth() - 15);
        } else if (cleanliness < 60) {
            setHealth(getHealth() - 5);
        }

        // Exhaustion affects health
        if (energy < 20) {
            setHealth(getHealth() - 10);
        }

        // Natural recovery if rested
        if (energy > 70) {
            setHealth(getHealth() + 3);
        }

        // Natural energy recovery overnight
        energy = Math.min(energy + 30, 100);

        // Ensure cleanliness stays within bounds
        cleanliness = Math.max(0, Math.min(100, cleanliness));

        // Check if player gets sick based on cleanliness
        if (!isSick() && Math.random() < Math.min(0.5,
                (cleanliness < 20 ? 0.3 : cleanliness < 40 ? 0.15 : cleanliness < 60 ? 0.05 : 0.0) +
                        (energy < 30 ? 0.1 : energy < 50 ? 0.05 : 0.0) +
                        (getHealth() < 40 ? 0.15 : getHealth() < 70 ? 0.08 : 0.0))) {
            setSick(true);
            System.out.println("You feel feverish and weak... you may have contracted the plague!");
        }
    }

    // Inventory methods
    public boolean hasItem(String itemName) {
        return inventory.containsKey(itemName) && inventory.get(itemName) > 0;
    }

    public int getItemCount(String itemName) {
        return inventory.getOrDefault(itemName, 0);
    }

    public void addItem(String itemName, int quantity) {
        inventory.put(itemName, getItemCount(itemName) + quantity);
    }

    public boolean useItem(String itemName, int quantity) {
        if (getItemCount(itemName) >= quantity) {
            int newCount = getItemCount(itemName) - quantity;
            if (newCount > 0) {
                inventory.put(itemName, newCount);
            } else {
                inventory.remove(itemName);
            }
            return true;
        }
        return false;
    }

    // Action methods
    public void rest() {
        energy = Math.min(energy + 40, 100);
        setHealth(getHealth() + 5);
    }

    public void clean() {
        if (hasItem("soap")) {
            cleanliness = Math.min(cleanliness + 20, 100);
            useItem("soap", 1);
        } else {
            cleanliness = Math.min(cleanliness + 10, 100);
        }
        energy -= 5;
    }

    public void treatPatient(boolean useProtection) {
        if (useProtection && hasItem("protective_gear")) {
            cleanliness -= 5;
            useItem("protective_gear", 1);
        } else {
            cleanliness -= 20;
        }
        energy -= 15;
        doctorSkill += 1;
    }

    // Added method to increase doctor skill
    public void increaseDoctorSkill(int amount) {
        this.doctorSkill += amount;
    }

    // Getters and setters unique to Player
    public int getCleanliness() {
        return cleanliness;
    }

    public void setCleanliness(int cleanliness) {
        this.cleanliness = Math.max(0, Math.min(100, cleanliness));
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(100, energy));
    }

    public int getDoctorSkill() {
        return doctorSkill;
    }

    public Map<String, Integer> getInventory() {
        return new HashMap<>(inventory);
    }
}