package Model;

import java.util.ArrayList;
import java.util.List;

public class SpecialNPC extends NPC {
    private String specialAbility;
    private List<String> uniqueDialogues; // Using ArrayList for ordered dialogues
    private boolean hasQuestAvailable;
    private boolean questCompleted;

    public SpecialNPC(String name, String role, String description,
            boolean isSick, String specialAbility) {
        super(name, role, description, isSick);
        this.specialAbility = specialAbility;
        this.uniqueDialogues = new ArrayList<>();
        this.hasQuestAvailable = true;
        this.questCompleted = false;

        // Add some default unique dialogues based on role
        addDefaultDialogues();
    }

    private void addDefaultDialogues() {
        switch (getRole()) {
            case "Village Head":
                addUniqueDialogue("The council is divided on these health measures. I need results to convince them.");
                addUniqueDialogue("My position allows me certain liberties in directing village resources.");
                addUniqueDialogue(
                        "The noble families hold significant influence. Their support could change everything.");
                break;
            case "Local Physician":
                addUniqueDialogue("I've studied traditional medicine for years. Your methods seem... unorthodox.");
                addUniqueDialogue("My patients trust me. Perhaps we could collaborate rather than compete.");
                addUniqueDialogue("There are herbs in these parts that have remarkable healing properties.");
                break;
            default:
                addUniqueDialogue("These are troubling times for the village.");
                break;
        }
    }

    @Override
    public String getInteractionDialogue() {
        if (questCompleted) {
            return getName() + ": Doctor, thank you again for your help. You have my complete trust.";
        } else if (hasQuestAvailable) {
            return getName() + ": Doctor, I have something important to discuss with you.";
        } else if (getRelationshipLevel() > 75) {
            return getName() + ": Ah, my trusted friend. What can I do for you?";
        } else {
            return getName() + ": Doctor, what news do you bring?";
        }
    }

    public void addUniqueDialogue(String dialogue) {
        uniqueDialogues.add(dialogue);
    }

    public String getRandomDialogue() {
        if (questCompleted) {
            switch (getRole()) {
                case "Village Head":
                    return getName()
                            + ": \"Your work with the noble families has strengthened our position immensely.\"";
                case "Local Physician":
                    return getName() + ": \"Those herbs you gathered have proven invaluable for our treatments.\"";
                default:
                    return getName() + ": \"Thank you for all you've done for our village.\"";
            }
        }

        if (uniqueDialogues.isEmpty()) {
            return "I have nothing special to report.";
        }
        int index = (int) (Math.random() * uniqueDialogues.size());
        return getName() + ": \"" + uniqueDialogues.get(index) + "\"";
    }

    // Quest-related methods
    public void completeQuest() {
        hasQuestAvailable = false;
        questCompleted = true;
        improveRelationship(10);
    }

    // Special ability usage
    public String useSpecialAbility(Player player, Village village) {
        switch (getRole()) {
            case "Village Head":
                // Lord can authorize quarantine
                village.setQuarantine(!village.isQuarantineActive());
                return "Lord Harwick "
                        + (village.isQuarantineActive() ? "authorizes a village quarantine!" : "lifts the quarantine.");

            case "Local Physician":
                // Rival doctor can provide insight
                player.increaseDoctorSkill(2);
                return "Doctor Elias shares some medical techniques with you. Your skills improve!";

            case "Royal Physician":
                // Royal physician can teach advanced techniques
                player.increaseDoctorSkill(2);
                player.addItem("royal_medicine", 1);
                return "Marcus Aurelius teaches you a royal healing technique and provides rare medicine!";

            case "Traveling Scholar":
                // Scholar can boost village education
                village.improveEducation(8);
                return "Brother Benedict shares crucial research that greatly improves village knowledge!";

            case "Merchant Prince":
                // Merchant can provide supplies
                player.addItem("protective_gear", 1);
                player.addItem("herbs", 2);
                return "Lady Vivienne uses her trade connections to acquire medical supplies for you!";

            default:
                return getName() + " uses their influence to help your cause.";
        }
    }

    // Getters
    public String getSpecialAbility() {
        return specialAbility;
    }

    public boolean hasQuestAvailable() {
        return hasQuestAvailable && !questCompleted; // This line could be more explicit
    }

    public boolean isQuestCompleted() {
        return questCompleted;
    }

    // Add a quest-giving method
    public String offerQuest() {
        if (!hasQuestAvailable) {
            return "I have no tasks for you at the moment.";
        }

        switch (getRole()) {
            case "Village Head":
                return "I need you to examine three noble families today. Their support is crucial.";

            case "Local Physician":
                return "I've been researching an herbal remedy. Gather 10 herbs for me to test it.";

            case "Royal Physician":
                return "I know of a sacred spring with healing waters. Help me gather 3 special water samples.";

            case "Traveling Scholar":
                return "Help me complete my research by educating villagers 3 times on different topics.";

            case "Merchant Prince":
                return "I need you to establish trust by successfully treating 5 merchant families.";

            default:
                return "I have a task that requires your expertise, doctor.";
        }
    }
}