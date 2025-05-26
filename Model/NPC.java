package Model;

public class NPC extends Character {
    private String role;
    private String description;
    private int relationshipLevel;
    
    public NPC(String name, String role, String description, boolean isSick) {
        super(name, isSick ? 60 : 100, isSick); // Name, health based on sickness, isSick
        this.role = role;
        this.description = description;
        this.relationshipLevel = 50; // Neutral starting point
    }
    
    @Override
    public void updateDailyStatus() {
        // Daily health changes
        if (isSick()) {
            setHealth(getHealth() - 5); // Sick NPCs get worse
        } else if (getHealth() < 100) {
            setHealth(getHealth() + 2); // Natural healing
        }
        
        // NPCs might recover naturally (low chance)
        if (isSick() && Math.random() < 0.05) {
            recover();
        }
    }
    
    public void improveRelationship(int amount) {
        relationshipLevel = Math.min(relationshipLevel + amount, 100);
    }
    
    public void damageRelationship(int amount) {
        relationshipLevel = Math.max(relationshipLevel - amount, 0);
    }
    
    public String getInteractionDialogue() {
        if (isSick()) {
            return "Hello doctor... *cough* Please help me.";
        } else {
            if (relationshipLevel > 70) {
                return "Good day, doctor! It's always a pleasure to see you.";
            } else if (relationshipLevel < 30) {
                return "Oh... it's you. What do you want?";
            } else {
                return "Good day, doctor.";
            }
        }
    }
    
    // Getters specific to NPC
    public String getRole() { return role; }
    public String getDescription() { return description; }
    public int getRelationshipLevel() { return relationshipLevel; }
}