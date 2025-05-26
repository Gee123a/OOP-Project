package Logic;

import Model.*;

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
    private String LAST_UPDATED = "2025-05-20";
    private ConsoleTextInterface textInterface;
    private List<String> activeQuests = new ArrayList<>();
    private Map<String, Integer> questProgress = new HashMap<>();

    public void run() {
        System.out.println("Plague Doctor's Day v" + VERSION);
        System.out.println("Starting game session: 2025-05-20 04:44:26");

        // Initialize game
        initializeGame();

        // Show introduction
        showIntroduction();

        // Main game loop
        while (currentDay <= TOTAL_DAYS && gameState.equals("ONGOING")) {
            // Display day header
            System.out.println("\n====== DAY " + currentDay + " ======\n");

            // Show daily status
            displayStatus();

            // Process day's events
            processDailyEvents();

            // Process player actions
            if (gameState.equals("ONGOING")) {
                processPlayerActions();
                endDay();
            }
        }

        // Show ending
        displayEnding();

        System.out.println("\nGame session ended: " + getCurrentDateTime());
        System.out.println("Thanks for playing, " + getUsername() + "!");
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
        System.out.println("===============================");
        System.out.println("    PLAGUE DOCTOR'S DAY");
        System.out.println("===============================");
        System.out.println("The year is 1348. The Black Death ravages Europe.");
        System.out.println("You arrive at Alderbrook village as their new plague doctor.");
        System.out.println("Can you save the village over the next 30 days?");
        System.out.println("===============================");
        String username = getUsername();
        System.out.println("Welcome, Doctor " + username + "!");
        System.out.print("Press ENTER to begin your journey...");
        scanner.nextLine();
    }

    // Display player and village status
    private void displayStatus() {
        System.out.println("\n=== STATUS ===");
        System.out.println("Current Day: " + currentDay + "/" + TOTAL_DAYS);
        System.out.println("Village Population: " + village.getPopulation());
        System.out.println("Infected: " + village.getInfectedCount() +
                " | Recovered: " + village.getRecoveredCount() +
                " | Deaths: " + village.getDeathCount());
        System.out.println("Village Trust Level: " + village.getTrustLevel() + "/100");
        if (village.isQuarantineActive()) {
            System.out.println("*** QUARANTINE ACTIVE ***");
        }
        System.out.println();
        System.out.println("=== PLAYER STATUS ===");
        System.out.println("Your Health: " + player.getHealth() + "/100");
        System.out.println("Cleanliness: " + player.getCleanliness() + "/100");
        System.out.println("Energy: " + player.getEnergy() + "/100");

        System.out.println("\nInventory:");
        for (Map.Entry<String, Integer> item : player.getInventory().entrySet()) {
            System.out.println("- " + item.getKey() + ": " + item.getValue());
        }
        if (!activeQuests.isEmpty()) {
            System.out.println("\n=== ACTIVE QUESTS ===");
            for (String quest : activeQuests) {
                int progress = questProgress.getOrDefault(quest, 0);
                if (quest.equals("doctor_quest")) {
                    System.out.println("- Gather herbs for Doctor Elias: " + progress + "/10 herbs");
                } else if (quest.equals("lord_quest")) {
                    System.out.println("- Examine noble families for Lord Harwick: " + progress + "/3 families");
                }
            }
        }
    }

    // Process events for the current day
    private void processDailyEvents() {
        // Day 1 introduction event
        if (currentDay == 1) {
            System.out.println("The village elder, Thomas, greets you at the gate.");
            System.out.println("\"Thank the heavens you've come, doctor. The sickness spreads quickly.\"");

            System.out.println("\nWhat do you do?");
            System.out.println("1: Introduce yourself formally to the village council");
            System.out.println("2: Immediately ask to see the sick");
            System.out.println("3: Survey the village layout first");

            int choice = textInterface.getChoice("Choose an action", 1, 3);

            switch (choice) {
                case 1:
                    System.out.println(
                            "\nYou make a formal introduction to the gathered elders. They seem impressed by your knowledge.");
                    village.improveTrust(10);
                    break;
                case 2:
                    System.out.println(
                            "\nYou request to be taken to the sick immediately. The villagers appreciate your urgency.");
                    village.improveTrust(5);
                    player.setCleanliness(player.getCleanliness() - 10);
                    break;
                case 3:
                    System.out.println(
                            "\nYou take time to understand the village layout. This will help with planning containment.");

                    // Survey reveals strategic information not shown in regular status
                    System.out.println("\n=== VILLAGE SURVEY INSIGHTS ===");
                    System.out.println("You identify key locations and potential risks:");

                    // Give tactical intelligence
                    if (village.getCleanliness() < 50) {
                        System.out.println(
                                "- The wells show signs of contamination - water sources need immediate attention.");
                    }
                    if (village.getEducationLevel() < 40) {
                        System.out.println("- Many villagers still believe in harmful superstitions about disease.");
                    }

                    // Reveal infection hotspots
                    System.out.println("- The market area shows highest infection risk due to crowding.");
                    System.out.println("- Noble district has better sanitation but lower trust in outsiders.");

                    // Give strategic advantage
                    System.out.println("\nYour systematic approach impresses the village council.");
                    village.improveTrust(8);
                    player.setCleanliness(player.getCleanliness() - 5);
                    break;
            }
        }

        // Day 7 - sick traveler event
        else if (currentDay == 7) {
            if (random.nextDouble() < 0.7) { // 70% chance of this event
                System.out.println("\n=== SPECIAL EVENT ===");
                System.out.println("A sick traveler collapses at the village gates.");

                System.out.println("\nWhat will you do?");
                System.out.println("1: Help the traveler, despite the risk");
                System.out.println("2: Order the traveler to be kept outside the village");
                System.out.println("3: Examine from a distance with protective gear");

                int choice = textInterface.getChoice("Choose an action", 1, 3);

                switch (choice) {
                    case 1:
                        System.out.println("You help the traveler personally, risking infection.");
                        player.setCleanliness(player.getCleanliness() - 30);
                        village.improveTrust(15);
                        break;
                    case 2:
                        System.out.println(
                                "You order the gates closed. The village is safer, but the traveler's fate is sealed.");
                        village.lowerTrust(10);
                        break;
                    case 3:
                        System.out.println("You examine the traveler carefully, using your protective gear.");
                        if (player.useItem("protective_gear", 1)) {
                            player.setCleanliness(player.getCleanliness() - 10);
                            village.improveTrust(5);
                        } else {
                            System.out.println("You don't have protective gear! You're forced to keep your distance.");
                            village.lowerTrust(5);
                        }
                        break;
                }
            }
        }

        // Special event on May 20 (today's date)
        else if (currentDay == 20) {
            System.out.println("\n=== SPECIAL EVENT ===");
            System.out.println("You notice today's date is May 20th, a date that feels particularly significant.");
            System.out.println("A strange sense of clarity comes over you, as if unseen forces guide your hand.");
            player.increaseDoctorSkill(5); // Bonus skill points
        }
    }

    // Process player actions for the day
    private void processPlayerActions() {
        int actionsRemaining = 3; // Player gets 3 actions per day

        while (actionsRemaining > 0 && player.getEnergy() > 0) {

            System.out.println("\nActions remaining today: " + actionsRemaining);
            System.out.println("Your energy: " + player.getEnergy());

            System.out.println("\nWhat would you like to do?");
            System.out.println("1: Treat patients");
            System.out.println("2: Rest and recover");
            System.out.println("3: Clean yourself");
            System.out.println("4: Gather herbs");
            System.out.println("5: Educate villagers");
            System.out.println("6: Speak with a villager");
            System.out.println("7: End day early");

            // Only show advanced medicine option if player has it
            boolean hasAdvancedMedicine = player.hasItem("advanced_medicine");
            if (hasAdvancedMedicine) {
                System.out.println("8: Use advanced medicine");
            }
            int maxChoice = hasAdvancedMedicine ? 8 : 7;
            int choice = textInterface.getChoice("Choose an action", 1, maxChoice);

            switch (choice) {
                case 1: // Treat patients
                    treatPatients();
                    actionsRemaining--;
                    break;

                case 2: // Rest
                    System.out.println("You spend time resting and recovering your strength.");
                    player.rest();
                    actionsRemaining--;
                    break;

                case 3: // Clean
                    System.out.println("You clean yourself thoroughly.");
                    player.clean();
                    System.out.println("Your cleanliness is now: " + player.getCleanliness());
                    actionsRemaining--;
                    break;

                case 4: // Gather herbs
                    System.out.println("You venture out to gather medicinal herbs.");
                    int herbsFound = 1 + random.nextInt(3); // 1-3 herbs
                    player.addItem("herbs", herbsFound);
                    player.setEnergy(player.getEnergy() - 20);
                    System.out.println("You found " + herbsFound + " herbs for your medicines!");
                    actionsRemaining--;

                    updateQuestProgress("doctor_quest", herbsFound);
                    break;

                case 5: // Educate
                    System.out.println("You spend time educating villagers about disease prevention.");
                    player.setEnergy(player.getEnergy() - 10);
                    village.improveEducation(5);
                    village.improveTrust(2);
                    System.out.println("The villagers listen attentively. Village education level is now: " +
                            village.getEducationLevel());
                    actionsRemaining--;
                    break;

                case 6: // Speak with villager
                    speakWithVillager();
                    actionsRemaining--;
                    break;

                case 7: // End day
                    System.out.println("You decide to end your day early.");
                    actionsRemaining = 0;
                    break;
                case 8: // Use advanced medicine (only available if player has it)
                    if (player.hasItem("advanced_medicine")) {
                        player.useAdvancedMedicine();
                    } else {
                        System.out.println("You don't have any advanced medicine.");
                    }
                    actionsRemaining--;
                    break;
            }

        }

        if (player.getEnergy() <= 0) {
            System.out.println("\nYou're too exhausted to do anything else today.");
        }
    }

    // Helper method for speaking with villagers
    private void speakWithVillager() {
        System.out.println("\nWho would you like to speak with?");
        System.out.println("1: Village Elder (Thomas)");
        System.out.println("2: Lord Harwick");
        System.out.println("3: Doctor Elias");
        System.out.println("4: Merchant Anna");

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
            System.out.println("That person isn't available.");
            return;
        }

        NPC npc = keyVillagers.get(npcKey);
        System.out.println(npc.getInteractionDialogue());

        // If this is a special NPC, offer special interactions
        if (npc instanceof SpecialNPC) {
            SpecialNPC specialNPC = (SpecialNPC) npc;

            System.out.println("\nHow would you like to interact?");
            System.out.println("1: General conversation");
            System.out.println("2: Ask for assistance");
            if (specialNPC.hasQuestAvailable()) {
                System.out.println("3: Ask if they need help");
            }
            int max = specialNPC.hasQuestAvailable() ? 3 : 2;
            int choice = textInterface.getChoice("Choose an interaction", 1, max);

            switch (choice) {
                case 1:
                    System.out.println(specialNPC.getRandomDialogue());
                    break;
                case 2:
                    System.out.println(specialNPC.useSpecialAbility(player, village));
                    break;
                case 3:
                    String questDescription = specialNPC.offerQuest();
                    System.out.println(questDescription);
                    boolean accept = textInterface.askYesNo("Will you accept this quest?");
                    if (accept) {
                        String questKey = npcKey + "_quest";
                        if (!activeQuests.contains(questKey)) {
                            activeQuests.add(questKey);
                            questProgress.put(questKey, 0);
                            System.out.println("You've accepted the quest!");
                            System.out.println("Check your status to track progress.");
                        } else {
                            System.out.println("You already have this quest.");
                        }
                    } else {
                        System.out.println("You decline the request.");
                    }
                    break;
            }
        } else {
            // Regular NPC conversation
            if (npc.getRelationshipLevel() > 60) {
                System.out.println(npc.getName() + " shares village gossip with you.");
                npc.improveRelationship(2);
            } else {
                System.out.println("You have a brief, formal conversation.");
                npc.improveRelationship(1);
            }
        }
    }

    // Helper method for treating patients
    private void treatPatients() {
        System.out.println("You spend your day treating the sick in their homes.");
        boolean useProtection = textInterface.askYesNo("Use protective gear? (y/n)");

        // Apply effects
        player.treatPatient(useProtection);

        // Calculate treatment effectiveness
        double baseEffectiveness = 0.05 + (player.getDoctorSkill() / 200.0);
        int potentialRecoveries = (int) (village.getInfectedCount() * baseEffectiveness);
        potentialRecoveries = Math.max(0, potentialRecoveries + random.nextInt(3) - 1);

        if (potentialRecoveries > 0) {
            System.out.println("You helped " + potentialRecoveries + " patients on their way to recovery!");
            for (int i = 0; i < potentialRecoveries && village.getInfectedCount() > 0; i++) {
                village.recoverVillager();
            }
            village.improveTrust(3);
            // Progress lord quest if treating wealthy families
            if (random.nextDouble() < 0.3) { // 30% chance of treating noble family
                System.out.println("Among your patients today was a member of a noble family.");
                updateQuestProgress("lord_quest", 1);
            }
        } else {
            System.out.println("Your treatments didn't seem very effective today.");
            village.lowerTrust(2);
            if (useProtection) {
                System.out.println("You used protective gear, but it didn't help much.");
                player.setCleanliness(player.getCleanliness() - 5);
            } else {
                System.out.println("You treated patients without protection, risking your own health.");
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

            System.out.println("\n=== QUEST COMPLETED ===");
            System.out.println("You completed " + npc.getName() + "'s quest!");

            // Add rewards
            if (npcKey.equals("doctor")) {
                player.increaseDoctorSkill(3);
                player.addItem("advanced_medicine", 2);
                System.out.println("Doctor Elias teaches you advanced techniques and gives you special medicine!");
                System.out.println("Your doctor skill increased by 3!");
            } else if (npcKey.equals("lord")) {
                village.improveTrust(15);
                player.addItem("coins", 20);
                player.addItem("noble_seal", 1);
                System.out.println("Lord Harwick speaks highly of you and rewards you with coins and his seal!");
                System.out.println("Village trust increased significantly!");
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
                System.out.println("The village is showing clear signs of recovery!");
            } else if (villageHealth < 40 || trustLevel < 40 || playerHealth < 50) {
                gameState = "CRISIS_PATH";
                System.out.println("The village is descending into chaos...");
            } else {
                gameState = "MIXED_PATH";
                System.out.println("The village remains divided about your methods.");
            }
        }

        // Advance to next day
        currentDay++;
        System.out.println("\nDay " + currentDay + " has ended.");
    }

    // Display game ending
    private void displayEnding() {
        System.out.println("\n===============================");
        System.out.println("       THE END");
        System.out.println("===============================");

        switch (gameState) {
            case "GOOD_PATH":
                System.out.println("You successfully led the village to recovery!");
                System.out.println("The plague has been contained, with " + village.getRecoveredCount() +
                        " villagers recovered and " + village.getDeathCount() + " lost.");
                System.out.println("Your methods will be remembered for generations to come.");
                break;

            case "MIXED_PATH":
                System.out.println("The village survived, though divisions remain.");
                System.out.println("Some villagers still cling to old methods, while others embrace your teachings.");
                System.out.println("The death toll stands at " + village.getDeathCount() +
                        ", with " + village.getRecoveredCount() + " recovered.");
                break;

            case "CRISIS_PATH":
                System.out.println("The plague overwhelmed the village despite your efforts.");
                System.out.println("With " + village.getDeathCount() + " dead and the social order collapsed,");
                System.out
                        .println("few remain to carry on. Your methods were sound, but fear and superstition won out.");
                break;

            case "BAD_ENDING":
                System.out.println("You succumbed to the plague, unable to complete your mission.");
                System.out.println("Without your guidance, the village descends into panic and chaos.");
                System.out.println("Perhaps your notes will help the next plague doctor who comes to Alderbrook.");
                break;
        }
    }
}