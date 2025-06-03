package Logic;

import Model.*;
import Model.ConsoleTextInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Logic {

    private Player player;
    private Village village;
    private Map<String, NPC> keyVillagers;
    private int currentDay = 1;
    private int TOTAL_DAYS = 30;
    private Scanner scanner = new Scanner(System.in);
    private Random random = new Random();
    private String gameState = "ONGOING";
    private String VERSION = "1.0";
    private String LAST_UPDATED = "2025-05-25";
    private ConsoleTextInterface textInterface = new ConsoleTextInterface();
    private List<String> activeQuests = new ArrayList<>();
    private Map<String, Integer> questProgress = new HashMap<>();

    public void run() {
        textInterface.display("Plague Doctor's Day v" + VERSION);
        textInterface.display("Last updated: " + LAST_UPDATED);

        // Initialize game
        initializeGame();

        // Show introduction
        showIntroduction();

        // Show tutorial (optional)
        showTutorial();

        // Main Game loop
        while (currentDay <= TOTAL_DAYS && gameState.equals("ONGOING")) {
            // Display day header
            textInterface.display("\n=== DAY " + currentDay + " ===");

            // Show daily status
            displayStatus();

            // Process special events it there's any
            processSpecialEvents();

            // Process player actions
            if (gameState.equals("ONGOING")) {
                processPlayerActions();
                endDay();
            }
        }

        // Show ending
        displayEnding();

        textInterface.display("\nThank you for playing!");

    }

    // Get current date and time formatted
    private String getCurrentDateTime() {
        return "2025-05-20 04:44:26";
    }

    // Get username for personalization
    private String getUsername() {
        textInterface.display("Please enter your name");
        return textInterface.getInput("Name: ");
    }

    // Initialize the game world
    private void initializeGame() {
        player = new Player();
        village = new Village();
        keyVillagers = new HashMap<>();
        textInterface = new ConsoleTextInterface();

        // Add key villagers
        keyVillagers.put("elder", new NPC("Thomas", "Village Elder",
                "A wise, elderly man who commands respect in the village.", false));

        // Use SpecialNPC for important characters
        keyVillagers.put("lord", new SpecialNPC("Lord Harwick", "Village Head",
                "The nobleman who technically rules the village.", false,
                "Can authorize village-wide actions"));

        keyVillagers.put("doctor", new SpecialNPC("Doctor Elias", "Local Physician",
                "A stern-looking man with traditional medical views.", false,
                "Can provide alternative medical insights"));

        keyVillagers.put("merchant", new NPC("Anna", "Market Owner",
                "A shrewd businesswoman who controls much of the village trade.", false));
    }

    // Show game introduction
    private void showIntroduction() {
        textInterface.display("\n============================================================");
        textInterface.display("                  PLAGUE DOCTOR'S DAY");
        textInterface.display("============================================================");
        textInterface.display("The year is 1348. The Black Death ravages Europe.");
        textInterface.display("You arrive at Alderbrook village as their new plague doctor.");
        textInterface.display("Can you save the village over the next 30 days?");
        textInterface.display("============================================================");
        String username = getUsername();
        textInterface.display("Welcome, Doctor " + username + "!");
        textInterface.display("Press ENTER to begin your journey...");
        scanner.nextLine();
    }

    // Display player and village status
    private void displayStatus() {
        textInterface.display("\n=== STATUS ===");
        textInterface.display("Current Day: " + currentDay + "/" + TOTAL_DAYS);
        textInterface.display("Village Population: " + village.getPopulation());
        textInterface.display("Infected: " + village.getInfectedCount() +
                " | Recovered: " + village.getRecoveredCount() +
                " | Deaths: " + village.getDeathCount());
        textInterface.display("Village Trust Level: " + village.getTrustLevel() + "/100");
        if (village.isQuarantineActive()) {
            textInterface.display("*** QUARANTINE ACTIVE ***");
        }
        textInterface.display("\n=== PLAYER STATUS ===");
        textInterface.display("Your Health: " + player.getHealth() + "/100");
        textInterface.display("Cleanliness: " + player.getCleanliness() + "/100");
        textInterface.display("Energy: " + player.getEnergy() + "/100");
        textInterface.display("Doctor Skill: " + player.getDoctorSkill() + "/100");

        textInterface.display("\nInventory:");
        for (Map.Entry<String, Integer> item : player.getInventory().entrySet()) {
            textInterface.display("- " + item.getKey() + ": " + item.getValue());
        }
        if (!activeQuests.isEmpty()) {
            textInterface.display("\n=== ACTIVE QUESTS ===");
            for (String quest : activeQuests) {
                int progress = questProgress.getOrDefault(quest, 0);
                if (quest.equals("doctor_quest")) {
                    textInterface.display("- Gather herbs for Doctor Elias: " + progress + "/10 herbs");
                } else if (quest.equals("lord_quest")) {
                    textInterface.display("- Examine noble families for Lord Harwick: " + progress + "/3 families");
                }
            }
        }
    }

    // Process events for the current day
    private void processSpecialEvents() {
        // Day 1 introduction event
        if (currentDay == 1) {
            textInterface.display("The village elder, Thomas, greets you at the gate.");
            textInterface.display("\"Thank the heavens you've come, doctor. The sickness spreads quickly.\"");

            textInterface.display("\nWhat do you do?");
            textInterface.display("1: Introduce yourself formally to the village council");
            textInterface.display("2: Immediately ask to see the sick");
            textInterface.display("3: Survey the village layout first");

            int choice = textInterface.getChoice("Choose an action", 1, 3);

            switch (choice) {
                case 1:
                    textInterface.display(
                            "\nYou make a formal introduction to the gathered elders. They seem impressed by your knowledge.");
                    village.improveTrust(10);
                    break;
                case 2:
                    textInterface.display(
                            "\nYou request to be taken to the sick immediately. The villagers appreciate your urgency.");
                    village.improveTrust(5);
                    player.setCleanliness(player.getCleanliness() - 10);
                    break;
                case 3:
                    textInterface.display(
                            "\nYou take time to understand the village layout. This will help with planning containment.");

                    // Survey reveals strategic information not shown in regular status
                    textInterface.display("\n=== VILLAGE SURVEY INSIGHTS ===");
                    textInterface.display("You identify key locations and potential risks:");

                    // Give tactical intelligence
                    if (village.getCleanliness() < 50) {
                        textInterface.display(
                                "- The wells show signs of contamination - water sources need immediate attention.");
                    }
                    if (village.getEducationLevel() < 40) {
                        textInterface.display("- Many villagers still believe in harmful superstitions about disease.");
                    }

                    // Reveal infection hotspots
                    textInterface.display("- The market area shows highest infection risk due to crowding.");
                    textInterface.display("- Noble district has better sanitation but lower trust in outsiders.");

                    // Give strategic advantage
                    textInterface.display("\nYour systematic approach impresses the village council.");
                    village.improveTrust(8);
                    player.setCleanliness(player.getCleanliness() - 5);
                    break;
            }
        }

        // Day 7 - 1st random encounter event
        else if (currentDay == 7) {
            if (random.nextDouble() < 0.7) { // 70% chance of this event
                textInterface.display("\n=== SPECIAL EVENT ===");
                textInterface.display("A sick traveler collapses at the village gates.");

                textInterface.display("\nWhat will you do?");
                textInterface.display("1: Help the traveler, despite the risk");
                textInterface.display("2: Order the traveler to be kept outside the village");
                textInterface.display("3: Examine from a distance with protective gear");

                int choice = textInterface.getChoice("Choose an action", 1, 3);

                switch (choice) {
                    case 1:
                        textInterface.display("You help the traveler personally, risking infection.");
                        player.setCleanliness(player.getCleanliness() - 30);
                        village.improveTrust(15);
                        break;
                    case 2:
                        textInterface.display(
                                "You order the gates closed. The village is safer, but the traveler's fate is sealed.");
                        village.lowerTrust(10);
                        break;
                    case 3:
                        textInterface.display("You examine the traveler carefully, using your protective gear.");
                        if (player.useItem("protective_gear", 1)) {
                            player.setCleanliness(player.getCleanliness() - 10);
                            village.improveTrust(5);
                        } else {
                            textInterface
                                    .display("You don't have protective gear! You're forced to keep your distance.");
                            village.lowerTrust(5);
                        }
                        break;
                }
            }
        }

        // Special event on May 20 (today's date)
        else if (currentDay == 20) {
            textInterface.display("\n=== SPECIAL EVENT ===");
            textInterface.display("You notice today's date is May 20th, a date that feels particularly significant.");
            textInterface.display("A strange sense of clarity comes over you, as if unseen forces guide your hand.");
            player.increaseDoctorSkill(5); // Bonus skill points
        }
    }

    // Process player actions for the day
    private void processPlayerActions() {
        int actionsRemaining = 3; // Player gets 3 actions per day

        while (actionsRemaining > 0 && player.getEnergy() > 0) {

            textInterface.display("\nActions remaining today: " + actionsRemaining);
            textInterface.display("Your energy: " + player.getEnergy());

            textInterface.display("\nWhat would you like to do?");
            textInterface.display("1: Treat patients");
            textInterface.display("2: Rest and recover");
            textInterface.display("3: Clean yourself");
            textInterface.display("4: Gather herbs");
            textInterface.display("5: Educate villagers");
            textInterface.display("6: Speak with a villager");
            textInterface.display("7: End day early");

            // Only show advanced medicine option if player has it
            boolean hasAdvancedMedicine = player.hasItem("advanced_medicine");
            if (hasAdvancedMedicine) {
                textInterface.display("8: Use advanced medicine");
            }
            int maxChoice = hasAdvancedMedicine ? 8 : 7;
            int choice = textInterface.getChoice("Choose an action", 1, maxChoice);

            switch (choice) {
                case 1: // Treat patients
                    treatPatients();
                    actionsRemaining--;
                    break;

                case 2: // Rest
                    textInterface.display("You spend time resting and recovering your strength.");
                    player.rest();
                    actionsRemaining--;
                    break;

                case 3: // Clean
                    textInterface.display("You clean yourself thoroughly.");
                    player.clean();
                    textInterface.display("Your cleanliness is now: " + player.getCleanliness());
                    actionsRemaining--;
                    break;

                case 4: // Gather herbs
                    textInterface.display("You venture out to gather medicinal herbs.");
                    int herbsFound = 1 + random.nextInt(3); // 1-3 herbs
                    player.addItem("herbs", herbsFound);
                    player.setEnergy(player.getEnergy() - 20);
                    textInterface.display("You found " + herbsFound + " herbs for your medicines!");
                    actionsRemaining--;

                    updateQuestProgress("doctor_quest", herbsFound);
                    actionsRemaining--;
                    break;

                case 5: // Educate
                    textInterface.display("You spend time educating villagers about disease prevention.");
                    player.setEnergy(player.getEnergy() - 10);
                    village.improveEducation(5);
                    village.improveTrust(2);
                    textInterface.display("The villagers listen attentively. Village education level is now: " +
                            village.getEducationLevel());
                    actionsRemaining--;
                    break;

                case 6: // Speak with villager
                    speakWithVillager();
                    actionsRemaining--;
                    break;

                case 7: // End day
                    textInterface.display("You decide to end your day early.");
                    actionsRemaining = 0;
                    break;
                case 8: // Use advanced medicine (only available if player has it)
                    if (player.hasItem("advanced_medicine")) {
                        player.useAdvancedMedicine();
                    } else {
                        textInterface.display("You don't have any advanced medicine.");
                    }
                    actionsRemaining--;
                    break;
            }

        }

        if (player.getEnergy() <= 0) {
            textInterface.display("\nYou're too exhausted to do anything else today.");
        }
    }

    // Helper method for speaking with villagers
    private void speakWithVillager() {
        textInterface.display("\nWho would you like to speak with?");
        textInterface.display("1: Village Elder (Thomas)");
        textInterface.display("2: Lord Harwick");
        textInterface.display("3: Doctor Elias");
        textInterface.display("4: Merchant Anna");

        int choice = textInterface.getChoice("Choose a villager to speak with", 1, 4);
        String npcKey;

        switch (choice) {
            case 1:
                npcKey = "elder";
                break;
            case 2:
                npcKey = "lord";
                break;
            case 3:
                npcKey = "doctor";
                break;
            case 4:
                npcKey = "merchant";
                break;
            default:
                return;
        }

        interactWithVillager(npcKey);
    }

    // Helper method for interacting with a specific villager
    private void interactWithVillager(String npcKey) {
        if (!keyVillagers.containsKey(npcKey)) {
            textInterface.display("That person isn't available.");
            return;
        }

        NPC npc = keyVillagers.get(npcKey);
        textInterface.display(npc.getInteractionDialogue());

        // If this is a special NPC, offer special interactions
        if (npc instanceof SpecialNPC) {
            SpecialNPC specialNPC = (SpecialNPC) npc;

            textInterface.display("\nHow would you like to interact?");
            textInterface.display("1: General conversation");
            textInterface.display("2: Ask for assistance");
            if (specialNPC.hasQuestAvailable()) {
                textInterface.display("3: Ask if they need help");
            }
            int max = specialNPC.hasQuestAvailable() ? 3 : 2;
            int choice = textInterface.getChoice("Choose an interaction", 1, max);

            switch (choice) {
                case 1:
                    textInterface.display(specialNPC.getRandomDialogue());
                    break;
                case 2:
                    textInterface.display(specialNPC.useSpecialAbility(player, village));
                    break;
                case 3:
                    String questDescription = specialNPC.offerQuest();
                    textInterface.display(questDescription);
                    boolean accept = textInterface.askYesNo("Will you accept this quest?");
                    if (accept) {
                        String questKey = npcKey + "_quest";
                        if (!activeQuests.contains(questKey)) {
                            activeQuests.add(questKey);
                            questProgress.put(questKey, 0);
                            textInterface.display("You've accepted the quest!");
                            textInterface.display("Check your status to track progress.");
                        } else {
                            textInterface.display("You already have this quest.");
                        }
                    } else {
                        textInterface.display("You decline the request.");
                    }
                    break;
            }
        } else {
            // Regular NPC conversation
            if (npc.getRelationshipLevel() > 60) {
                textInterface.display(npc.getName() + " shares village gossip with you.");
                npc.improveRelationship(2);
            } else {
                textInterface.display("You have a brief, formal conversation.");
                npc.improveRelationship(1);
            }
        }
    }

    // Helper method for treating patients
    private void treatPatients() {
        textInterface.display("You spend your day treating the sick in their homes.");
        boolean useProtection = textInterface.askYesNo("Use protective gear? (y/n)");

        // Apply effects
        player.treatPatient(useProtection);

        // Calculate treatment effectiveness
        double baseEffectiveness = 0.03 + (player.getDoctorSkill() / 200.0);
        int potentialRecoveries = (int) (village.getInfectedCount() * baseEffectiveness);

        // Add skill check - low skill means potential failure
        if (player.getDoctorSkill() < 15 && random.nextDouble() < 0.4) {
            textInterface.display("Your inexperience shows. Some treatments may have done more harm than good.");
            potentialRecoveries = Math.max(0, potentialRecoveries - 2);
        }

        potentialRecoveries = Math.max(0, potentialRecoveries + random.nextInt(2) - 1);

        if (potentialRecoveries > 0) {
            textInterface.display("You helped " + potentialRecoveries + " patients on their way to recovery!");
            for (int i = 0; i < potentialRecoveries && village.getInfectedCount() > 0; i++) {
                village.recoverVillager();
            }
            village.improveTrust(2); // Reduced trust gain (was 3)

            // Progress lord quest if treating wealthy families
            if (random.nextDouble() < 0.3) { // 30% chance of treating noble family
                textInterface.display("Among your patients today was a member of a noble family.");
                updateQuestProgress("lord_quest", 1);
            }
        } else {
            textInterface.display("Your treatments didn't seem very effective today.");
            village.lowerTrust(2);
            if (useProtection) {
                textInterface.display("You used protective gear, but it didn't help much.");
                player.setCleanliness(player.getCleanliness() - 5);
            } else {
                textInterface.display("You treated patients without protection, risking your own health.");
                player.setCleanliness(player.getCleanliness() - 15);
            }
        }
    }

    private void updateQuestProgress(String questKey, int progress) {
        if (activeQuests.contains(questKey)) {
            int currentProgress = questProgress.getOrDefault(questKey, 0);
            questProgress.put(questKey, currentProgress + progress);

            // Check if quest is complete
            if (questKey.equals("doctor_quest") && questProgress.get(questKey) >= 10) {
                completeQuest(questKey, "doctor");
            } else if (questKey.equals("lord_quest") && questProgress.get(questKey) >= 3) {
                completeQuest(questKey, "lord");
            }
        }
    }

    private void completeQuest(String questKey, String npcKey) {
        activeQuests.remove(questKey);
        questProgress.remove(questKey);

        if (keyVillagers.containsKey(npcKey) && keyVillagers.get(npcKey) instanceof SpecialNPC) {
            SpecialNPC npc = (SpecialNPC) keyVillagers.get(npcKey);
            npc.completeQuest();

            textInterface.display("\n=== QUEST COMPLETED ===");
            textInterface.display("You completed " + npc.getName() + "'s quest!");

            // Add rewards
            if (npcKey.equals("doctor")) {
                player.increaseDoctorSkill(3);
                player.addItem("advanced_medicine", 2);
                textInterface.display("Doctor Elias teaches you advanced techniques and gives you special medicine!");
                textInterface.display("Your doctor skill increased by 3!");
            } else if (npcKey.equals("lord")) {
                village.improveTrust(15);
                player.addItem("coins", 20);
                player.addItem("noble_seal", 1);
                textInterface.display("Lord Harwick speaks highly of you and rewards you with coins and his seal!");
                textInterface.display("Village trust increased significantly!");
            }
        }
    }

    // End the current day
    private void endDay() {
        // Update village status
        village.updateDailyStatus();

        // Update player status
        player.updateDailyStatus();

        // Check for critical health
        if (player.getHealth() < 20) {
            gameState = "BAD_ENDING";
        }

        // Check for branching on day 20
        if (currentDay == 20) {
            int villageHealth = 100 - (village.getInfectedCount() * 100 / village.getPopulation());
            int trustLevel = village.getTrustLevel();
            int playerHealth = player.getHealth();

            if (villageHealth > 60 && trustLevel > 65 && playerHealth > 70) {
                gameState = "GOOD_PATH";
                textInterface.display("The village is showing clear signs of recovery!");
            } else if (villageHealth < 40 || trustLevel < 40 || playerHealth < 50) {
                gameState = "CRISIS_PATH";
                textInterface.display("The village is descending into chaos...");
            } else {
                gameState = "MIXED_PATH";
                textInterface.display("The village remains divided about your methods.");
            }
        }

        // Advance to next day
        textInterface.display("\nDay " + currentDay + " has ended.");
        currentDay++;
    }

    // Display game ending
    private void displayEnding() {
        textInterface.display("\n===============================");
        textInterface.display("       THE END");
        textInterface.display("===============================");

        switch (gameState) {
            case "GOOD_PATH":
                textInterface.display("You successfully led the village to recovery!");
                textInterface.display("The plague has been contained, with " + village.getRecoveredCount() +
                        " villagers recovered and " + village.getDeathCount() + " lost.");
                textInterface.display("Your methods will be remembered for generations to come.");
                break;

            case "MIXED_PATH":
                textInterface.display("The village survived, though divisions remain.");
                textInterface
                        .display("Some villagers still cling to old methods, while others embrace your teachings.");
                textInterface.display("The death toll stands at " + village.getDeathCount() +
                        ", with " + village.getRecoveredCount() + " recovered.");
                break;

            case "CRISIS_PATH":
                textInterface.display("The plague overwhelmed the village despite your efforts.");
                textInterface.display("With " + village.getDeathCount() + " dead and the social order collapsed,");
                textInterface
                        .display("few remain to carry on. Your methods were sound, but fear and superstition won out.");
                break;

            case "BAD_ENDING":
                textInterface.display("You succumbed to the plague, unable to complete your mission.");
                textInterface.display("Without your guidance, the village descends into panic and chaos.");
                textInterface.display("Perhaps your notes will help the next plague doctor who comes to Alderbrook.");
                break;
        }
    }

    // Display game tutorial
    private void showTutorial() {
        textInterface.display("\n=== TUTORIAL ===");

        textInterface.display("Welcome to the Plague Doctor's Day tutorial!");
        boolean skipTutorial = textInterface.askYesNo("Would you like to skip the tutorial?");
        if (skipTutorial)
            return;
        textInterface.display("In this game, you will play as a plague doctor trying to save a village from the Black Death.");
        textInterface.display("You will manage your HEALTH, CLEANLINESS, and ENERGY while treating patients and interacting with villagers.");
        textInterface.display("Each day, you will have a limited number of actions to perform.");
        textInterface.display("You can treat patients, gather herbs, educate villagers, and interact with key NPCs.");
        textInterface.display("Your choices will affect the village's trust in you and your overall success.");
        textInterface.display("Remember to keep an eye on your health and cleanliness, as they will impact your effectiveness.");
        textInterface.display("You can also accept quests from special NPCs to gain rewards and improve your skills.");
        textInterface
                .display("Use your resources wisely and make strategic decisions to lead the village to recovery.");
        textInterface.display("Good luck, doctor! The fate of Alderbrook village is in your hands.");
        textInterface.display("Press ENTER to continue to the game...");
        scanner.nextLine();

    }
}
