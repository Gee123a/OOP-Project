package Model;

/**
 * Base class representing any character in the game world
 */
public abstract class Character {
    protected String name;
    protected int health;
    protected boolean isSick;
    
    public Character(String name, int health, boolean isSick) {
        this.name = name;
        this.health = health;
        this.isSick = isSick;
    }
    
    // Shared methods for health management
    public int getHealth() { return health; }
    
    public void setHealth(int health) { 
        this.health = Math.max(0, Math.min(100, health)); 
    }
    
    // Check if character is alive
    public boolean isAlive() {
        return health > 0;
    }
    
    // Disease-related methods
    public boolean isSick() { return isSick; }
    
    public void setSick(boolean sick) { 
        this.isSick = sick;
        if (sick && health > 60) {
            health = 60; // Getting sick reduces health
        }
    }
    
    public void recover() {
        if (isSick) {
            isSick = false;
            health = Math.min(health + 30, 100);
        }
    }
    
    // Abstract method that subclasses must implement
    protected abstract void updateDailyStatus();
    
    // Getters and setters
    public String getName() { return name; }
}