package Logic;

import Model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Logic {

    private Player player;
    private Village village;
    private Map<String, NPC> keyVillagers;
    private int currentDay = 1;
    private int TOTAL_DAYS = 30;
    private Random random = new Random();
    private String gameState = "ONGOING";
    private String VERSION = "1.2";

    private ConsoleTextInterface textInterface = new ConsoleTextInterface();
    private ArrayList<String> activeQuests = new ArrayList<>();
    private Map<String, Integer> questProgress = new HashMap<>();

    public void run() {
        // Initialize game
        initializeGame();

        textInterface.display("Plague Doctor's Day v" + VERSION);

        // Show introduction
        showIntroduction();

        // Show tutorial (optional)
        showTutorial();

        // Main Game loop
        while (currentDay <= TOTAL_DAYS && gameState.equals("ONGOING")) {
            // Display day header
            textInterface.shortPause();
            textInterface.display("\n=== DAY " + currentDay + " ===");

            // Show daily status
            displayStatus();

            // Process special events if there's any
            processSpecialEvents();

            // Process player actions
            if (gameState.equals("ONGOING")) {
                processPlayerActions();
                endDay();
                textInterface.longPause();
            }
        }

        // Show ending
        displayEnding();
        textInterface.display("\nThank you for playing!");

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
        textInterface.displayStory("The year is 1348. The Black Death ravages Europe.");
        textInterface.displayStory("You arrive at Alderbrook village as their new plague doctor.");
        textInterface.displayDramatic("Can you save the village over the next 30 days?");
        textInterface.display("============================================================");
        String username = getUsername();
        textInterface.displayNotification("Welcome, Doctor " + username + "!");
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
        textInterface.longPause();
        textInterface.display("\n=== PLAYER STATUS ===");
        textInterface.display("Your Health: " + player.getHealth() + "/100");
        textInterface.display("Cleanliness: " + player.getCleanliness() + "/100");
        textInterface.display("Energy: " + player.getEnergy() + "/100");
        textInterface.display("Doctor Skill: " + player.getDoctorSkill() + "/100");
        textInterface.longPause();
        textInterface.display("\nInventory:");
        for (Map.Entry<String, Integer> item : player.getInventory().entrySet()) {
            textInterface.display("- " + item.getKey() + ": " + item.getValue());
        }
        // In displayStatus(), update the quest display section:
        if (!activeQuests.isEmpty()) {
            textInterface.longPause();
            textInterface.display("\n=== ACTIVE QUESTS ===");
            for (String quest : activeQuests) {
                int progress = questProgress.getOrDefault(quest, 0);
                if (quest.equals("doctor_quest")) {
                    textInterface.display("- Gather herbs for Doctor Elias: " + progress + "/10 herbs");
                    if (progress >= 10) {
                        textInterface.display("  >>> READY TO TURN IN! Speak with Doctor Elias <<<");
                    }
                } else if (quest.equals("lord_quest")) {
                    textInterface.display("- Examine noble families for Lord Harwick: " + progress + "/3 families");
                    if (progress >= 3) {
                        textInterface.display("  >>> READY TO TURN IN! Speak with Lord Harwick <<<");
                    }
                } else if (quest.equals("traveler_quest")) {
                    if (keyVillagers.containsKey("traveler") && keyVillagers.get("traveler") instanceof SpecialNPC) {
                        SpecialNPC traveler = (SpecialNPC) keyVillagers.get("traveler");
                        String role = traveler.getRole();
                        if (role.equals("Royal Physician")) {
                            textInterface
                                    .display("- Gather healing water for Marcus Aurelius: " + progress + "/3 samples");
                        } else if (role.equals("Traveling Scholar")) {
                            textInterface.display(
                                    "- Educational research for Brother Benedict: " + progress + "/3 sessions");
                        } else if (role.equals("Merchant Prince")) {
                            textInterface.display(
                                    "- Treat merchant families for Lady Vivienne: " + progress + "/5 families");
                        }
                    }
                }
            }
        }
    }

    // Process events for the current day
    private void processSpecialEvents() {
        if (currentDay == 1) {
            textInterface.displayStory("The village elder, Thomas, greets you at the gate.");
            textInterface.displayDramatic("\"Thank the heavens you've come, doctor. The sickness spreads quickly.\"");
            textInterface.mediumPause();

            textInterface.display("\nWhat do you do?");
            textInterface.display("1: Introduce yourself formally to the village council");
            textInterface.display("2: Immediately ask to see the sick");
            textInterface.display("3: Survey the village layout first");

            int choice = textInterface.getChoice("Choose an action", 1, 3);

            switch (choice) {
                case 1:
                    textInterface.displayStory("\nYou make a formal introduction to the gathered elders.");
                    textInterface.displayNotification("They seem impressed by your knowledge and bearing.");
                    village.improveTrust(10);
                    break;
                case 2:
                    textInterface.displayStory("\nYou request to be taken to the sick immediately.");
                    textInterface.displayQuick("The villagers appreciate your urgency...");
                    village.improveTrust(5);
                    player.setCleanliness(player.getCleanliness() - 10);
                    break;
                case 3:
                    textInterface.displayStory("\nYou take time to understand the village layout systematically.");
                    textInterface.displayQuick("This strategic approach will help with containment planning...");

                    // Survey reveals strategic information with dramatic timing
                    textInterface.displayHeader("VILLAGE SURVEY INSIGHTS");
                    textInterface.displayStory("You identify key locations and potential risks:");

                    // Give tactical intelligence with pauses
                    if (village.getCleanliness() < 50) {
                        textInterface.displayWithDelay(
                                "- The wells show signs of contamination - water sources need immediate attention.",
                                1000);
                    }
                    if (village.getEducationLevel() < 40) {
                        textInterface.displayWithDelay(
                                "- Many villagers still believe in harmful superstitions about disease.", 1000);
                    }

                    // Reveal infection hotspots
                    textInterface.displayWithDelay("- The market area shows highest infection risk due to crowding.",
                            1000);
                    textInterface.displayWithDelay(
                            "- Noble district has better sanitation but lower trust in outsiders.", 1000);

                    // Give strategic advantage
                    textInterface.displayStory("\nYour systematic approach impresses the village council.");
                    village.improveTrust(8);
                    player.setCleanliness(player.getCleanliness() - 5);
                    break;
            }
        }

        // Day 7 - 1st random encounter event (UPDATED)
        else if (currentDay == 7) {
            if (random.nextDouble() < 0.7) // 70% chance of this event
            {
                textInterface.displayHeader("SPECIAL EVENT");
                textInterface.displayStory("A sick traveler collapses at the village gates.");
                textInterface.mediumPause();
                textInterface.displayDramatic("Despite their illness, there's something mysterious about them...");

                textInterface.display("\nWhat will you do?");
                textInterface.display("1: Help the traveler personally, despite the risk");
                textInterface.display("2: Order the traveler to be kept outside the village");
                textInterface.display("3: Examine from a distance with protective gear");

                int choice = textInterface.getChoice("Choose an action", 1, 3);
                NPC traveler;
                switch (choice) {
                    case 1: // Help personally
                        textInterface.displayStory("You help the traveler personally, risking infection.");
                        textInterface.displayQuick("Your compassion overrides your caution...");
                        player.setCleanliness(player.getCleanliness() - 30);

                        traveler = new NPC("Mysterious Traveler", "Unknown Wanderer",
                                "A sick traveler who collapsed at the gates. Their eyes hold ancient wisdom.", true);
                        keyVillagers.put("traveler", traveler);
                        village.improveTrust(15);
                        textInterface.displayNotification("The villagers are moved by your selflessness!");
                        break;

                    case 2: // Reject
                        textInterface.displayStory(
                                "You order the gates closed. The village is safer, but the traveler's fate is sealed.");
                        textInterface.displayWithDelay("Some villagers look away, troubled by this decision.", 1500);
                        village.lowerTrust(10);
                        break;

                    case 3: // Examine with protection
                        textInterface.displayStory("You examine the traveler carefully, using your protective gear.");
                        if (player.useItem("protective_gear", 1)) {
                            textInterface.displayQuick("Your precautions serve you well...");
                            player.setCleanliness(player.getCleanliness() - 10);
                            village.improveTrust(5);

                            traveler = new NPC("Mysterious Traveler", "Unknown Wanderer",
                                    "A sick traveler who collapsed at the gates. Their eyes hold ancient wisdom.",
                                    true);
                            keyVillagers.put("traveler", traveler);
                            textInterface.displayNotification("You successfully help while staying protected!");
                        } else {
                            textInterface.displayDramatic(
                                    "You don't have protective gear! You're forced to keep your distance.");
                            textInterface.displayStory("The traveler's fate hangs in the balance...");
                            village.lowerTrust(5);
                        }
                        break;
                }
            }
        }

        // Day 14 - Traveler recovery and transformation event
        else if (currentDay == 14) {
            if (keyVillagers.containsKey("traveler")) {
                NPC traveler = keyVillagers.get("traveler");

                textInterface.displayHeader("TRAVELER RECOVERY EVENT");

                if (!traveler.isSick()) {
                    // Traveler has recovered - TRANSFORM them into SpecialNPC
                    textInterface.displayStory("The mysterious traveler approaches you, now fully recovered.");
                    textInterface.mediumPause();
                    textInterface
                            .displayDramatic("\"Doctor, I owe you my life. Let me tell you who I really am...\"");

                    // Create new SpecialNPC with revealed identity
                    SpecialNPC specialTraveler = transformTravelerToSpecial(traveler);

                    // Replace the regular NPC with SpecialNPC
                    keyVillagers.put("traveler", specialTraveler);

                    textInterface.shortPause();
                    textInterface.displayNotification(
                            "You can now speak with " + specialTraveler.getName() + " for special assistance!");

                } else {
                    // Still sick - offer intensive treatment
                    textInterface.displayStory("The traveler remains gravely ill after a week.");
                    textInterface.shortPause();
                    boolean intensiveTreatment = textInterface.askYesNo("Spend extra effort treating them today?");

                    if (intensiveTreatment) {
                        textInterface.displayStory("You focus all your medical knowledge on saving them...");

                        if (player.hasItem("herbs")) {
                            player.useItem("herbs", 2);
                            player.setEnergy(player.getEnergy() - 20);

                            if (random.nextDouble() < 0.8) { // 80% success with herbs
                                traveler.recover();
                                textInterface.displayDramatic(
                                        "Your intensive treatment works! The traveler begins to recover!");
                                village.improveTrust(10);
                            } else {
                                textInterface.displayWithDelay("Despite your best efforts, they remain critical.",
                                        1500);
                            }
                        } else {
                            player.setEnergy(player.getEnergy() - 25);
                            if (random.nextDouble() < 0.4) { // 40% success without herbs
                                traveler.recover();
                                textInterface
                                        .displayDramatic("Through determination alone, you help them recover!");
                            } else {
                                textInterface.displayStory("Without proper medicine, your options are limited.");
                            }
                        }
                    }
                }
            }
        }

        // Special event on May 20 - ENHANCE WITH DELAYS
        else if (currentDay == 20) {
            textInterface.displayHeader("SPECIAL EVENT");
            textInterface
                    .displayStory("You notice today's date is May 20th, a date that feels particularly significant.");
            textInterface.mediumPause();
            textInterface.displayTypewriter(
                    "A strange sense of clarity comes over you, as if unseen forces guide your hand.", 70);
            textInterface.displayTypewriter("Ancient knowledge seems to flow through your fingertips...", 80);
            player.increaseDoctorSkill(5);
            textInterface
                    .displayNotification("Your doctor skill has increased by 5 points through divine inspiration!");
        }
    }

    // METHOD TO TRANSFORM TRAVELER TO SPECIAL NPC (POLYMORPHISM CASTING)
    private SpecialNPC transformTravelerToSpecial(NPC originalTraveler) {
        // Randomly determine their revealed identity
        String[] identities = { "Royal Physician", "Traveling Scholar", "Merchant Prince" };
        String identity = identities[random.nextInt(identities.length)];

        SpecialNPC specialTraveler;

        switch (identity) {
            case "Royal Physician":
                specialTraveler = new SpecialNPC("Marcus Aurelius", "Royal Physician",
                        "A learned doctor from the King's court, traveling in disguise.", false,
                        "Can teach advanced royal medical techniques");

                textInterface.displayTypewriter("\"I am Marcus Aurelius, physician to the King's court.\"", 80);
                textInterface.displayTypewriter("\"I was studying plague patterns in rural areas when I fell ill.\"",
                        70);
                textInterface.displayTypewriter("\"Your compassionate care has earned you a powerful ally.\"", 60);
                textInterface.displayWithDelay("\"Take this letter of recommendation to the King!\"", 1200);
                textInterface.displayStory("\"Your compassionate care has earned you a powerful ally.\"");

                // Immediate reward for helping
                player.increaseDoctorSkill(3);
                village.improveTrust(15);
                textInterface.displayNotification("He teaches you advanced royal techniques!");
                break;

            case "Traveling Scholar":
                specialTraveler = new SpecialNPC("Brother Benedict", "Traveling Scholar",
                        "A scholar studying disease patterns across different regions.", false,
                        "Can provide valuable research and education");

                textInterface.displayTypewriter("\"I am Brother Benedict from the university.\"", 80);
                textInterface.displayTypewriter("\"I've been documenting plague responses across the realm.\"", 70);
                textInterface.displayTypewriter("\"Your methods align with the latest scholarly research.\"", 60);
                textInterface.displayWithDelay("\"My complete research is now yours!\"", 1200);
                textInterface
                        .displayNotification("Brother Benedict shares his complete research with the village!");
                break;

            case "Merchant Prince":
                specialTraveler = new SpecialNPC("Lady Vivienne", "Merchant Prince",
                        "A wealthy trader with connections across the continent.", false,
                        "Can provide rare supplies and trade connections");

                textInterface.displayTypewriter("\"I am Lady Vivienne, head of the Northern Trading Guild.\"", 80);
                textInterface.displayTypewriter("\"Your care has earned you a powerful ally in commerce.\"", 70);
                textInterface.displayTypewriter("\"I can arrange for medical supplies to reach this village.\"", 60);
                textInterface.displayWithDelay("\"I hereby establish permanent trade routes!\"", 1200);
                textInterface.displayNotification("Lady Vivienne establishes permanent medical supply routes!");
                break;

            default:
                specialTraveler = new SpecialNPC("The Traveler", "Mysterious Wanderer",
                        "A recovered traveler with hidden knowledge.", false,
                        "Possesses mysterious abilities");
                break;
        }

        // Copy relationship level from original NPC
        while (specialTraveler.getRelationshipLevel() < originalTraveler.getRelationshipLevel()) {
            specialTraveler.improveRelationship(1);
        }

        return specialTraveler;
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
            textInterface.display("8: View inventory");

            // Only show advanced medicine option if player has it
            boolean hasAdvancedMedicine = player.hasItem("advanced_medicine");
            if (hasAdvancedMedicine) {
                textInterface.display("9: Use advanced medicine");
            }
            int maxChoice = hasAdvancedMedicine ? 9 : 8;
            int choice = textInterface.getChoice("Choose an action", 1, maxChoice);

            switch (choice) {
                case 1: // Treat patients
                    treatPatients();
                    actionsRemaining--;
                    break;

                case 2: // Rest
                    textInterface.displayStory("You spend time resting and recovering your strength.");
                    textInterface.displayQuick("You feel your energy slowly returning...");
                    player.rest();
                    textInterface.displayNotification("Your energy and health have improved!");
                    actionsRemaining--;
                    break;

                case 3: // Clean
                    if (player.hasItem("soap")) {
                        player.clean();
                        textInterface.displayStory("You wash yourself with soap, feeling refreshed.");
                    } else {
                        player.clean();
                        textInterface.displayStory("You clean yourself with water, but it isn't as effective.");
                    }
                    textInterface.displayQuick("The grime and corruption wash away...");
                    textInterface.displayNotification("Your cleanliness is now: " + player.getCleanliness());
                    actionsRemaining--;
                    break;

                case 4: // Gather herbs
                    textInterface
                            .displayStory("You venture out to gather medicinal herbs in the surrounding countryside.");
                    textInterface.displayQuick("You search carefully among the plants...");
                    int herbsFound = 1 + random.nextInt(3); // 1-3 herbs
                    player.addItem("herbs", herbsFound);
                    player.setEnergy(player.getEnergy() - 20);
                    textInterface.displayNotification("You found " + herbsFound + " herbs for your medicines!");

                    updateQuestProgress("doctor_quest", herbsFound);
                    actionsRemaining--;
                    break;

                case 5: // Educate
                    textInterface.display("\nYou decide to educate the villagers about disease prevention.");
                    textInterface.display("What topic would you like to focus on today?");
                    textInterface.display("1: Personal hygiene and cleanliness");
                    textInterface.display("2: Disease transmission and prevention");
                    textInterface.display("3: Herbal medicine and treatments");
                    textInterface.display("4: Quarantine and isolation practices");
                    textInterface.display("5: Nutrition and strengthening the body");

                    int educationChoice = textInterface.getChoice("Choose an educational topic", 1, 5);

                    switch (educationChoice) {
                        case 1: // Personal hygiene
                            educateAboutHygiene();
                            break;
                        case 2: // Disease transmission
                            educateAboutTransmission();
                            break;
                        case 3: // Herbal medicine
                            educateAboutHerbalMedicine();
                            break;
                        case 4: // Quarantine practices
                            educateAboutQuarantine();
                            break;
                        case 5: // Nutrition
                            educateAboutNutrition();
                            break;
                    }

                    player.setEnergy(player.getEnergy() - 15); // Slightly more energy cost for detailed teaching
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
                case 8:
                    textInterface.displayHeader("YOUR INVENTORY");

                    // Display current inventory
                    Map<String, Integer> inventory = player.getInventory();
                    if (inventory.isEmpty()) {
                        textInterface.displayStory("Your medical bag is empty.");
                    } else {
                        textInterface.displayStory("Contents of your medical bag:");
                        for (Map.Entry<String, Integer> item : inventory.entrySet()) {
                            textInterface.display("- " + item.getKey() + ": " + item.getValue());
                        }
                    }
                    actionsRemaining--;
                    break;
                case 9: // Use advanced medicine (only available if player has it)
                    if (player.hasItem("advanced_medicine")) {
                        textInterface.displayHeader("ADVANCED MEDICINE USAGE");
                        textInterface.displayStory("You examine your precious advanced medicine carefully.");

                        textInterface.display("\nHow would you like to use it?");
                        textInterface.display("1: Use on yourself to restore health");
                        textInterface.display("2: Use while treating patients for maximum effect");
                        textInterface.display("3: Save it for later");

                        int medicineChoice = textInterface.getChoice("Choose how to use advanced medicine", 1, 3);

                        switch (medicineChoice) {
                            case 1: // Use on self
                                textInterface.displayStory("You carefully apply the advanced medicine to yourself.");
                                player.useItem("advanced_medicine", 1);
                                player.setHealth(Math.min(100, player.getHealth() + 40));
                                player.setCleanliness(Math.min(100, player.getCleanliness() + 20));
                                textInterface
                                        .displayDramatic("You feel the powerful medicine coursing through your body!");
                                textInterface.displayNotification("Health +40, Cleanliness +20");
                                break;

                            case 2: // Use while treating
                                textInterface.displayStory(
                                        "You decide to use the advanced medicine while treating patients.");
                                treatPatients(); // This will ask about advanced medicine usage
                                break;

                            case 3: // Save it
                                textInterface.displayStory(
                                        "You decide to save the advanced medicine for a more critical moment.");
                                actionsRemaining++; // Don't consume action if not used
                                break;
                        }
                    } else {
                        textInterface.displayStory("You don't have any advanced medicine.");
                        actionsRemaining++; // Don't consume action
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

        int maxChoice = 4;

        // Check if traveler exists and is recovered
        if (keyVillagers.containsKey("traveler") && !keyVillagers.get("traveler").isSick()) {
            NPC traveler = keyVillagers.get("traveler");

            // Use casting to determine what to display
            if (traveler instanceof SpecialNPC) {
                SpecialNPC specialTraveler = (SpecialNPC) traveler;
                textInterface.display("5: " + specialTraveler.getName() + " (" + specialTraveler.getRole() + ")");
            } else {
                textInterface.display("5: " + traveler.getName() + " (Recovered Traveler)");
            }
            maxChoice = 5;
        }

        int choice = textInterface.getChoice("Choose a villager to speak with", 1, maxChoice);
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
            case 5:
                // Safety check
                if (keyVillagers.containsKey("traveler") && !keyVillagers.get("traveler").isSick()) {
                    npcKey = "traveler";
                } else {
                    textInterface.display("That option is not available.");
                    return;
                }
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
                            
                            // Initialize progress based on current inventory
                            int initialProgress = 0;
                            if (questKey.equals("doctor_quest")) {
                                // Give credit for herbs already in inventory
                                initialProgress = player.getItemCount("herbs");
                                textInterface.displayNotification("You already have " + initialProgress + " herbs that count toward this quest!");
                            } else if (questKey.equals("lord_quest")) {
                                // For lord quest, start at 0 since it tracks actions, not items
                                initialProgress = 0;
                            } else if (questKey.equals("traveler_quest")) {
                                // Different traveler quests might have different starting conditions
                                if (keyVillagers.containsKey("traveler") && keyVillagers.get("traveler") instanceof SpecialNPC) {
                                    SpecialNPC traveler = (SpecialNPC) keyVillagers.get("traveler");
                                    String role = traveler.getRole();
                                    if (role.equals("Royal Physician")) {
                                        // Count existing healing water samples if any
                                        initialProgress = player.getItemCount("healing_water");
                                    }
                                    // Other traveler types start at 0 since they track actions
                                }
                            }
                            
                            questProgress.put(questKey, initialProgress);
                            textInterface.display("You've accepted the quest!");
                            
                            // Check if quest is already complete
                            checkQuestCompletion(questKey, npcKey);
                            
                            textInterface.display("Check your status to track progress.");
                        } else {
                            textInterface.display("You already have this quest.");
                        }
                    } else {
                        textInterface.display("You decline the request.");
                    }
                    break;
            }
        }
        // NEW: Special case for merchant Anna
        else if (npcKey.equals("merchant")) {
            textInterface.display("\nHow would you like to interact with Anna?");
            textInterface.display("1: General conversation");
            textInterface.display("2: Browse her wares (trading)");

            int choice = textInterface.getChoice("Choose an interaction", 1, 2);

            switch (choice) {
                case 1:
                    // Regular conversation
                    if (npc.getRelationshipLevel() > 60) {
                        textInterface.display("Anna shares some useful trading gossip with you.");
                        textInterface.displayQuick("\"The supply routes are getting dangerous, doctor.\"");
                        npc.improveRelationship(2);
                    } else {
                        textInterface.display("You have a brief conversation about village trade.");
                        npc.improveRelationship(1);
                    }
                    break;
                case 2:
                    // Trading system
                    tradeWithMerchant(npc);
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
        textInterface.displayStory("You spend your day treating the sick in their homes.");
        textInterface.mediumPause();

        // Check if player has protective gear before asking
        boolean hasProtection = player.hasItem("protective_gear");
        boolean useProtection = false;

        if (hasProtection) {
            useProtection = textInterface.askYesNo("Use protective gear?");
            if (useProtection) {
                textInterface.displayStory("You put on protective gear before treating patients.");
            } else {
                textInterface.displayStory("You decide not to use protective gear.");
            }
        } else {
            textInterface.displayStory("You don't have protective gear, so you treat patients without protection.");
            useProtection = false;
        }

        // Check for advanced medicine usage
        boolean useAdvancedMedicine = false;
        if (player.hasItem("advanced_medicine")) {
            textInterface.displayDramatic("\nYou notice you have advanced medicine in your kit...");
            useAdvancedMedicine = textInterface.askYesNo("Use advanced medicine on the most critical patients?");

            if (useAdvancedMedicine) {
                textInterface
                        .displayStory("You decide to use your precious advanced medicine on the most severe cases.");
                player.useItem("advanced_medicine", 1);
                textInterface.displayQuick("You carefully prepare the rare medicine...");
            } else {
                textInterface.displayStory("You save the advanced medicine for another time.");
            }
        }

        // Apply effects based on choices
        player.treatPatient(useProtection);

        // Calculate treatment effectiveness with advanced medicine bonus
        double baseEffectiveness = 0.03 + (player.getDoctorSkill() / 200.0);

        // Advanced medicine significantly boosts effectiveness
        if (useAdvancedMedicine) {
            baseEffectiveness += 0.15; // +15% effectiveness boost
            textInterface.displayDramatic("The advanced medicine shows immediate results!");
        }

        int potentialRecoveries = (int) (village.getInfectedCount() * baseEffectiveness);

        // Add skill check - low skill means potential failure (but advanced medicine
        // helps)
        if (player.getDoctorSkill() < 15 && random.nextDouble() < 0.4) {
            if (useAdvancedMedicine) {
                textInterface.displayStory("Your inexperience shows, but the advanced medicine compensates!");
                potentialRecoveries = Math.max(0, potentialRecoveries - 1); // Less penalty with advanced medicine
            } else {
                textInterface
                        .displayStory("Your inexperience shows. Some treatments may have done more harm than good.");
                potentialRecoveries = Math.max(0, potentialRecoveries - 2);
            }
        }

        // Random variation
        potentialRecoveries = Math.max(0, potentialRecoveries + random.nextInt(2) - 1);

        // Advanced medicine can save additional patients
        if (useAdvancedMedicine && random.nextDouble() < 0.6) { // 60% chance
            potentialRecoveries += 1 + random.nextInt(2);
            textInterface.displayTypewriter("The advanced medicine works miracles on the most critical cases!", 70);
        }

        // Apply results with enhanced feedback
        if (potentialRecoveries > 0) {
            textInterface.displayStory("You work tirelessly through the day...");

            if (useAdvancedMedicine) {
                textInterface.displayDramatic(
                        "The combination of your skill and advanced medicine proves highly effective!");
                textInterface.displayNotification(
                        "You helped " + potentialRecoveries + " patients recover, including some critical cases!");

                // Advanced medicine provides additional benefits
                village.improveTrust(4); // Extra trust for using precious resources
                player.increaseDoctorSkill(2); // Learn from using advanced techniques

            } else {
                textInterface.displayNotification(
                        "You helped " + potentialRecoveries + " patients on their way to recovery!");
                village.improveTrust(2);
            }

            for (int i = 0; i < potentialRecoveries && village.getInfectedCount() > 0; i++) {
                village.recoverVillager();
            }

            // Progress lord quest if treating wealthy families
            if (random.nextDouble() < 0.3) // 30% chance of treating noble family
            {
                textInterface.displayQuick("Among your patients today was a member of a noble family.");
                updateQuestProgress("lord_quest", 1);
            }

            // Advanced medicine increases chance of treating important families
            if (useAdvancedMedicine && random.nextDouble() < 0.4) { // 40% chance with advanced medicine
                textInterface.displayQuick("Your use of advanced medicine impressed a wealthy merchant family.");
                updateQuestProgress("lord_quest", 1);
            }

            // Progress merchant prince quest if treating merchant families
            if (activeQuests.contains("traveler_quest") &&
                    keyVillagers.containsKey("traveler") &&
                    keyVillagers.get("traveler") instanceof SpecialNPC) {
                SpecialNPC traveler = (SpecialNPC) keyVillagers.get("traveler");
                if (traveler.getRole().equals("Merchant Prince")) {
                    double merchantChance = useAdvancedMedicine ? 0.4 : 0.2; // Double chance with advanced medicine
                    if (random.nextDouble() < merchantChance) {
                        textInterface.displayQuick(
                                "You treated a merchant family, furthering Lady Vivienne's trust-building goal.");
                        updateQuestProgress("traveler_quest", 1);
                    }
                }
            }

        } else {
            // Treatment was ineffective
            textInterface.displayStory("Your treatments didn't seem very effective today.");

            if (useAdvancedMedicine) {
                textInterface.displayDramatic("Even the advanced medicine couldn't help today's patients...");
                textInterface.displayStory(
                        "Perhaps the cases were too far advanced, or the medicine was applied incorrectly.");
                village.lowerTrust(1); // Less trust loss since you tried your best
            } else {
                village.lowerTrust(2);
            }

            // Apply cleanliness effects
            if (hasProtection && useProtection) {
                textInterface.displayStory("You used protective gear, but it didn't help much.");
                player.setCleanliness(player.getCleanliness() - 5);
            } else {
                textInterface.displayStory("You treated patients without protection, risking your own health.");
                player.setCleanliness(player.getCleanliness() - 15);
            }
        }

        // Special narrative for using advanced medicine
        if (useAdvancedMedicine) {
            textInterface.displayStory("\nThe villagers watch in awe as you apply the sophisticated medicine.");
            textInterface.displayQuick("Word spreads quickly about your access to rare treatments.");

            // Chance of attracting attention from special NPCs
            if (random.nextDouble() < 0.3) { // 30% chance
                textInterface.displayDramatic(
                        "Your use of advanced medicine catches the attention of influential villagers!");
                if (keyVillagers.containsKey("lord")) {
                    keyVillagers.get("lord").improveRelationship(5);
                    textInterface.displayQuick("Lord Harwick takes note of your sophisticated methods.");
                }
            }
        }

        // When villagers die (you could add this in treatPatients when treatment
        // fails):
        if (potentialRecoveries <= 0 && random.nextDouble() < 0.2) {
            textInterface.displayTypewriter("Despite your efforts, another soul slips away into the darkness...", 90);
            textInterface.displayStory("The family mourns as you document another loss.");
        }
    }

    // Update your updateQuestProgress method:
    private void updateQuestProgress(String questKey, int progress) {
        if (activeQuests.contains(questKey)) {
            int currentProgress = questProgress.getOrDefault(questKey, 0);
            questProgress.put(questKey, currentProgress + progress);

            // Check if quest is complete
            if (questKey.equals("doctor_quest") && questProgress.get(questKey) >= 10) {
                completeQuest(questKey, "doctor");
            } else if (questKey.equals("lord_quest") && questProgress.get(questKey) >= 3) {
                completeQuest(questKey, "lord");
            } else if (questKey.equals("traveler_quest")) {
                // Different completion based on traveler identity
                if (keyVillagers.containsKey("traveler") && keyVillagers.get("traveler") instanceof SpecialNPC) {
                    SpecialNPC traveler = (SpecialNPC) keyVillagers.get("traveler");
                    String role = traveler.getRole();

                    if ((role.equals("Royal Physician") && questProgress.get(questKey) >= 3) ||
                            (role.equals("Traveling Scholar") && questProgress.get(questKey) >= 3) ||
                            (role.equals("Merchant Prince") && questProgress.get(questKey) >= 5)) {
                        completeQuest(questKey, "traveler");
                    }
                }
            }
        }
    }

    private void completeQuest(String questKey, String npcKey) {
        activeQuests.remove(questKey);
        questProgress.remove(questKey);

        if (keyVillagers.containsKey(npcKey) && keyVillagers.get(npcKey) instanceof SpecialNPC) {
            SpecialNPC npc = (SpecialNPC) keyVillagers.get(npcKey);
            npc.completeQuest();

            textInterface.displayHeader("QUEST COMPLETED");
            textInterface.displayDramatic("You completed " + npc.getName() + "'s quest!");

            // Add rewards with dramatic pauses
            if (npcKey.equals("doctor")) {
                player.increaseDoctorSkill(3);
                player.addItem("advanced_medicine", 2);
                textInterface.displayStory("Doctor Elias nods approvingly...");
                textInterface
                        .displayNotification("He teaches you advanced techniques and gives you 2 special medicine!");
                textInterface.displayQuick("Your doctor skill increased by 3!");
            } else if (npcKey.equals("lord")) {
                village.improveTrust(15);
                player.addItem("coins", 20);
                player.addItem("noble_seal", 1);
                textInterface.displayStory("Lord Harwick's stern expression softens...");
                textInterface.displayNotification("He speaks highly of you and rewards you with coins and his seal!");
                textInterface.displayQuick("Village trust increased significantly!");
            } else if (npcKey.equals("traveler")) {
                // Add traveler quest rewards here with dramatic effect
                SpecialNPC traveler = (SpecialNPC) keyVillagers.get("traveler");
                String role = traveler.getRole();
                textInterface.displayStory(traveler.getName() + " smiles with deep gratitude...");
                if (role.equals("Royal Physician")) {
                    player.increaseDoctorSkill(5);
                    player.addItem("royal_medicine", 3);
                    player.addItem("king_letter", 1);
                    textInterface.displayTypewriter("\"Take this letter of recommendation to the King!\"", 75);
                    textInterface.displayNotification("Marcus Aurelius gives you royal medicine and a king's letter!");
                } else if (role.equals("Traveling Scholar")) {
                    village.improveEducation(20);
                    player.addItem("ancient_texts", 2);
                    textInterface.displayTypewriter("\"My complete research is now yours!\"", 75);
                    textInterface
                            .displayNotification("Brother Benedict shares his complete research with the village!");
                } else if (role.equals("Merchant Prince")) {
                    player.addItem("rare_supplies", 5);
                    player.addItem("protective_gear", 3);
                    village.improveTrust(15);
                    textInterface.displayTypewriter("\"I hereby establish permanent trade routes!\"", 75);
                    textInterface.displayNotification("Lady Vivienne establishes permanent medical supply routes!");
                }
            }
        }
    }

    // Add this new method to check if a quest is immediately completable:

    private void checkQuestCompletion(String questKey, String npcKey) {
        int currentProgress = questProgress.getOrDefault(questKey, 0);
        
        if (questKey.equals("doctor_quest") && currentProgress >= 10) {
            textInterface.displayDramatic("You already have enough herbs to complete this quest!");
            boolean completeNow = textInterface.askYesNo("Turn in the quest now?");
            if (completeNow) {
                // Consume the required herbs
                player.useItem("herbs", 10);
                completeQuest(questKey, npcKey);
            }
        } else if (questKey.equals("lord_quest") && currentProgress >= 3) {
            completeQuest(questKey, npcKey);
        } else if (questKey.equals("traveler_quest")) {
            if (keyVillagers.containsKey("traveler") && keyVillagers.get("traveler") instanceof SpecialNPC) {
                SpecialNPC traveler = (SpecialNPC) keyVillagers.get("traveler");
                String role = traveler.getRole();
                
                if (role.equals("Royal Physician") && currentProgress >= 3) {
                    textInterface.displayDramatic("You already have the required healing water samples!");
                    boolean completeNow = textInterface.askYesNo("Turn in the quest now?");
                    if (completeNow) {
                        player.useItem("healing_water", 3);
                        completeQuest(questKey, npcKey);
                    }
                }
                // Other traveler quest types are action-based, so can't be pre-completed
            }
        }
    }

    // End the current day
    private void endDay() {
        textInterface.displayWithDelay("\n=== END OF DAY " + currentDay + " ===", 1000);

        // Update village status
        village.updateDailyStatus();

        // Update player status
        player.updateDailyStatus();

        // Check for critical health with dramatic effect
        if (player.getHealth() < 20) {
            textInterface.displayTypewriter("Your vision blurs as fever takes hold...", 100);
            textInterface.displayWithDelay("The plague may be claiming another victim.", 2000);
            gameState = "BAD_ENDING";
            return;
        }

        // Check for branching on day 20
        if (currentDay == 20) {
            textInterface.displayHeader("MIDPOINT ASSESSMENT");
            textInterface.displayStory("Two weeks have passed since your arrival...");
            textInterface.mediumPause();

            int villageHealth = 100 - (village.getInfectedCount() * 100 / village.getPopulation());
            int trustLevel = village.getTrustLevel();
            int playerHealth = player.getHealth();

            if (villageHealth > 60 && trustLevel > 65 && playerHealth > 70) {
                gameState = "GOOD_PATH";
                textInterface.displayDramatic("The village is showing clear signs of recovery!");
                textInterface.displayStory("Hope begins to replace despair in the villagers' eyes.");
            } else if (villageHealth < 40 || trustLevel < 40 || playerHealth < 50) {
                gameState = "CRISIS_PATH";
                textInterface.displayDramatic("The village is descending into chaos...");
                textInterface.displayStory("Your methods are being questioned by desperate people.");
            } else {
                gameState = "MIXED_PATH";
                textInterface.displayDramatic("The village remains divided about your methods.");
                textInterface.displayStory("Some trust you, others whisper in the shadows.");
            }
        }

        // Advance to next day with pause
        textInterface.displayQuick("Day " + currentDay + " has ended.");
        textInterface.displayStory("You retire to your quarters, planning tomorrow's efforts...");
        currentDay++;
    }

    // Display game ending
    private void displayEnding() {
        textInterface.displayHeader("THE END");
        textInterface.mediumPause();

        switch (gameState) {
            case "GOOD_PATH":
                textInterface.displayTypewriter("You successfully led the village to recovery!", 80);
                textInterface.displayStory("The plague has been contained, with " + village.getRecoveredCount() +
                        " villagers recovered and " + village.getDeathCount() + " lost.");
                textInterface.displayTypewriter("Your methods will be remembered for generations to come.", 70);
                break;

            case "MIXED_PATH":
                textInterface.displayDramatic("The village survived, though divisions remain.");
                textInterface.displayStory(
                        "Some villagers still cling to old methods, while others embrace your teachings.");
                textInterface.displayQuick("The death toll stands at " + village.getDeathCount() +
                        ", with " + village.getRecoveredCount() + " recovered.");
                break;

            case "CRISIS_PATH":
                textInterface.displayDramatic("The plague overwhelmed the village despite your efforts.");
                textInterface.displayStory("With " + village.getDeathCount() + " dead and the social order collapsed,");
                textInterface.displayWithDelay(
                        "few remain to carry on. Your methods were sound, but fear and superstition won out.", 1500);
                break;

            case "BAD_ENDING":
                textInterface.displayTypewriter("You succumbed to the plague, unable to complete your mission.", 90);
                textInterface.displayStory("Without your guidance, the village descends into panic and chaos.");
                textInterface.displayTypewriter(
                        "Perhaps your notes will help the next plague doctor who comes to Alderbrook.", 80);
                break;
        }
    }

    private void educateAboutHygiene() {
        textInterface.displayHeader("HYGIENE AND CLEANLINESS EDUCATION");
        textInterface.displayStory("You gather the villagers in the town square to teach about personal cleanliness.");
        textInterface.mediumPause();

        textInterface.displayDramatic("\"Listen carefully,\" you begin, adjusting your plague mask.");
        textInterface.displayStory("\"The first defense against the pestilence is cleanliness of body and home.\"");

        textInterface.displayHeader("KEY LESSONS YOU TEACH");
        textInterface.displayWithDelay("• HAND WASHING: \"Wash your hands frequently with soap and clean water,", 800);
        textInterface.displayWithDelay("  especially before eating and after touching sick persons.\"", 800);
        textInterface.displayWithDelay("• BATHING: \"Bathe regularly to remove corrupt humors from the skin.\"", 800);
        textInterface.displayWithDelay(
                "• CLEAN CLOTHING: \"Change and wash your garments often - dirty cloth harbors disease.\"", 800);
        textInterface.displayWithDelay("• HOME SANITATION: \"Keep your homes clean, dispose of waste properly,", 800);
        textInterface.displayWithDelay("  and ensure good air circulation.\"", 800);

        // Show villager reactions based on education level WITH DELAYS
        if (village.getEducationLevel() < 30) {
            textInterface.displayStory("\nMany villagers look skeptical. Some mutter about 'foreign ideas.'");
            textInterface.displayDramatic(
                    "An elder speaks up: \"Our grandparents never bathed so much and lived long lives!\"");
            village.improveEducation(3);
            village.improveTrust(1);
        } else if (village.getEducationLevel() < 60) {
            textInterface.displayStory("\nSome villagers nod thoughtfully, while others still seem uncertain.");
            textInterface.displayQuick("A young mother asks: \"How often should we wash our children, doctor?\"");
            village.improveEducation(5);
            village.improveTrust(3);
        } else {
            textInterface.displayStory("\nThe villagers listen attentively and ask intelligent questions.");
            textInterface
                    .displayNotification("Several people volunteer to help spread these practices to their families.");
            village.improveEducation(7);
            village.improveTrust(4);
            village.improveCleanliness(5);
        }

        textInterface.displayQuick("\nYou see some villagers immediately checking their hands and clothes.");
        textInterface.displayStory("Your lessons about cleanliness are slowly taking root in the community.");

        // Check and update traveler quest progress
        if (activeQuests.contains("traveler_quest") &&
                keyVillagers.containsKey("traveler") &&
                keyVillagers.get("traveler") instanceof SpecialNPC) {
            SpecialNPC traveler = (SpecialNPC) keyVillagers.get("traveler");
            if (traveler.getRole().equals("Traveling Scholar")) {
                updateQuestProgress("traveler_quest", 1);
                textInterface.displayQuick("Brother Benedict takes notes on your educational methods.");
            }
        }
    }

    private void educateAboutTransmission() {
        textInterface.displayHeader("DISEASE TRANSMISSION EDUCATION");
        textInterface.displayStory("You explain how the plague spreads from person to person.");
        textInterface.mediumPause();

        textInterface.displayDramatic("\"The pestilence travels in ways both seen and unseen,\" you explain.");
        textInterface.displayStory("\"Understanding its paths helps us block its advance.\"");

        textInterface.displayHeader("TRANSMISSION METHODS YOU EXPLAIN");
        textInterface.displayWithDelay(
                "• CLOSE CONTACT: \"The disease passes through the breath and touch of the sick.\"", 900);
        textInterface.displayWithDelay(
                "• CONTAMINATED ITEMS: \"Clothing, bedding, and tools of the infected carry danger.\"", 900);
        textInterface.displayWithDelay("• POOR SANITATION: \"Waste and filth create breeding grounds for corruption.\"",
                900);
        textInterface.displayWithDelay(
                "• CROWDED SPACES: \"Markets and gatherings allow rapid spread between people.\"", 900);

        textInterface.displayHeader("PREVENTION STRATEGIES");
        textInterface.displayWithDelay("• DISTANCE: \"Maintain space from the visibly sick - arm's length minimum.\"",
                800);
        textInterface.displayWithDelay("• AVOID CROWDS: \"Limit time in busy markets and large gatherings.\"", 800);
        textInterface.displayWithDelay("• CLEAN SURFACES: \"Wash items that may have contacted the sick.\"", 800);
        textInterface.displayWithDelay(
                "• ISOLATE THE ILL: \"Keep sick family members in separate rooms when possible.\"", 800);

        if (village.getEducationLevel() < 40) {
            textInterface.displayStory("\nSeveral villagers argue: \"But we must care for our sick family members!\"");
            textInterface.displayDramatic(
                    "You respond: \"Care for them, yes, but with precautions to protect yourselves.\"");
            village.improveEducation(4);
            village.improveTrust(2);
        } else {
            textInterface.displayStory("\nThe villagers grasp these concepts quickly and begin discussing");
            textInterface.displayQuick("how to reorganize their homes and daily routines.");
            village.improveEducation(6);
            village.improveTrust(3);
            // Slightly more immediate effect on infection reduction
            if (random.nextDouble() < 0.4) {
                textInterface.displayNotification(
                        "Your education immediately helps - some villagers avoid a potential exposure!");
            }
        }

        // Check and update traveler quest progress
        if (activeQuests.contains("traveler_quest") &&
                keyVillagers.containsKey("traveler") &&
                keyVillagers.get("traveler") instanceof SpecialNPC) {
            SpecialNPC traveler = (SpecialNPC) keyVillagers.get("traveler");
            if (traveler.getRole().equals("Traveling Scholar")) {
                updateQuestProgress("traveler_quest", 1);
                textInterface.displayQuick("Brother Benedict takes notes on your educational methods.");
            }
        }
    }

    private void educateAboutHerbalMedicine() {
        textInterface.displayHeader("HERBAL MEDICINE EDUCATION");

        if (player.hasItem("herbs")) {
            textInterface.displayStory("You display your collection of herbs to teach their medicinal properties.");
            textInterface
                    .displayDramatic("\"Nature provides remedies for those who know where to look,\" you explain.");

            textInterface.displayHeader("MEDICINAL HERBS AND THEIR USES");
            textInterface.displayWithDelay("• WILLOW BARK: \"Chew this to reduce fever and ease pain in the joints.\"",
                    900);
            textInterface.displayWithDelay(
                    "• ELDERBERRY: \"Make tea from these berries to strengthen the body's defenses.\"", 900);
            textInterface.displayWithDelay("• GARLIC: \"Eat daily to purify the blood and ward off corruption.\"", 900);
            textInterface.displayWithDelay("• THYME: \"Burn as incense to cleanse the air of pestilent vapors.\"", 900);
            textInterface.displayWithDelay("• CHAMOMILE: \"Brew tea to calm the spirit and aid restful sleep.\"", 900);

            textInterface.displayHeader("PREPARATION METHODS");
            textInterface.displayWithDelay("• TEAS: \"Steep herbs in hot water for the count of 200 heartbeats.\"",
                    800);
            textInterface.displayWithDelay("• POULTICES: \"Crush fresh herbs and apply to wounds or swellings.\"", 800);
            textInterface.displayWithDelay(
                    "• TINCTURES: \"Soak herbs in wine for seven days to extract their essence.\"", 800);

            // Consume some herbs in the demonstration
            player.useItem("herbs", 1);
            textInterface.displayQuick("\nYou use one herb in your demonstration.");

            village.improveEducation(6);
            village.improveTrust(4);
            textInterface.displayNotification("Several villagers take notes and promise to search for these herbs.");

        } else {
            textInterface.displayStory("You realize you don't have herbs to demonstrate with!");
            textInterface
                    .displayQuick("You explain herbal medicine theory, but without examples, it's less effective.");
            village.improveEducation(2);
            village.improveTrust(1);
            textInterface.displayStory("The villagers seem interested but want to see actual herbs next time.");
        }

        // Check and update traveler quest progress
        if (activeQuests.contains("traveler_quest") &&
                keyVillagers.containsKey("traveler") &&
                keyVillagers.get("traveler") instanceof SpecialNPC) {
            SpecialNPC traveler = (SpecialNPC) keyVillagers.get("traveler");
            if (traveler.getRole().equals("Traveling Scholar")) {
                updateQuestProgress("traveler_quest", 1);
                textInterface.displayQuick("Brother Benedict takes notes on your educational methods.");
            }
        }
    }

    private void educateAboutQuarantine() {
        textInterface.displayHeader("QUARANTINE AND ISOLATION EDUCATION");
        textInterface.displayStory("You explain the importance of separating the sick from the healthy.");
        textInterface.mediumPause();

        textInterface.displayDramatic("\"Sometimes, harsh measures preserve the greater good,\" you begin solemnly.");
        textInterface.displayStory("\"Separation protects the many, even as we care for the few.\"");

        textInterface.displayHeader("QUARANTINE PRINCIPLES");
        textInterface.displayWithDelay(
                "• EARLY ISOLATION: \"Separate those showing first signs of sickness immediately.\"", 900);
        textInterface.displayWithDelay(
                "• DEDICATED CAREGIVERS: \"Assign specific people to tend the sick - not everyone.\"", 900);
        textInterface.displayWithDelay(
                "• BARRIER PROTECTION: \"Use separate dishes, clothing, and bedding for the ill.\"", 900);
        textInterface.displayWithDelay(
                "• SCHEDULED CARE: \"Visit the sick at set times, not constantly throughout the day.\"", 900);

        textInterface.displayHeader("COMMUNITY MEASURES");
        textInterface.displayWithDelay("• TRAVEL RESTRICTIONS: \"Limit movement between infected and clean areas.\"",
                800);
        textInterface.displayWithDelay("• MARKET CONTROLS: \"Reduce market days and limit crowd sizes.\"", 800);
        textInterface.displayWithDelay("• FAMILY PODS: \"Keep families together but separate from other families.\"",
                800);

        // Different reactions based on current quarantine status
        if (village.isQuarantineActive()) {
            textInterface
                    .displayStory("\nSince quarantine is already active, villagers understand its necessity better.");
            textInterface.displayDramatic("\"We see the wisdom in these measures,\" an elder acknowledges.");
            village.improveEducation(5);
            village.improveTrust(3);
        } else {
            textInterface.displayStory("\nSome villagers look uncomfortable with these strict measures.");
            textInterface.displayDramatic("\"You ask us to abandon our sick neighbors?\" someone challenges.");
            textInterface.displayStory("You explain: \"Not abandon - protect them AND protect yourselves.\"");
            village.improveEducation(4);

            if (village.getTrustLevel() > 60) {
                village.improveTrust(2);
                textInterface.displayQuick("Most villagers trust your judgment, even if they don't like the measures.");
            } else {
                village.lowerTrust(1);
                textInterface
                        .displayStory("Some villagers remain suspicious of your 'foreign' ideas about separation.");
            }
        }

        // Check and update traveler quest progress
        if (activeQuests.contains("traveler_quest") &&
                keyVillagers.containsKey("traveler") &&
                keyVillagers.get("traveler") instanceof SpecialNPC) {
            SpecialNPC traveler = (SpecialNPC) keyVillagers.get("traveler");
            if (traveler.getRole().equals("Traveling Scholar")) {
                updateQuestProgress("traveler_quest", 1);
                textInterface.displayQuick("Brother Benedict takes notes on your educational methods.");
            }
        }
    }

    private void educateAboutNutrition() {
        textInterface.displayHeader("NUTRITION AND BODY STRENGTHENING EDUCATION");
        textInterface.displayStory("You teach about foods and practices that strengthen the body against disease.");
        textInterface.mediumPause();

        textInterface.displayDramatic("\"A strong body resists corruption better than a weak one,\" you explain.");
        textInterface.displayStory("\"What we consume shapes our ability to fight the pestilence.\"");

        textInterface.displayHeader("STRENGTHENING FOODS");
        textInterface.displayWithDelay(
                "• FRESH FRUITS: \"Apples and berries purify the blood and provide vital humors.\"", 800);
        textInterface.displayWithDelay("• VEGETABLES: \"Onions, leeks, and cabbage build resistance to disease.\"",
                800);
        textInterface.displayWithDelay(
                "• CLEAN WATER: \"Pure water flushes corruption from the body - avoid stagnant sources.\"", 800);
        textInterface.displayWithDelay(
                "• MODERATE MEAT: \"Fresh meat provides strength, but avoid excess during plague times.\"", 800);
        textInterface.displayWithDelay("• HONEY: \"Nature's medicine - soothes throat and provides clean energy.\"",
                800);

        textInterface.displayHeader("FOODS TO AVOID");
        textInterface.displayWithDelay(
                "• SPOILED FOODS: \"Rotten meat and moldy bread invite corruption into the body.\"", 800);
        textInterface.displayWithDelay(
                "• EXCESS ALCOHOL: \"Wine in moderation cleanses, but excess weakens the spirit.\"", 800);
        textInterface.displayWithDelay(
                "• HEAVY SPICES: \"During plague, simple foods are easier for weakened bodies to process.\"", 800);

        textInterface.displayHeader("HEALTHY PRACTICES");
        textInterface.displayWithDelay("• REGULAR MEALS: \"Eat at consistent times to maintain body rhythm.\"", 800);
        textInterface.displayWithDelay(
                "• PORTION CONTROL: \"Better to eat less frequently than to overburden the stomach.\"", 800);
        textInterface.displayWithDelay("• FOOD PREPARATION: \"Cook thoroughly and keep cooking areas clean.\"", 800);

        if (village.getCleanliness() > 50) {
            textInterface.displayStory(
                    "\nThe villagers respond well - their clean environment supports your nutrition advice.");
            textInterface
                    .displayNotification("Several families volunteer to share healthy recipes with their neighbors.");
            village.improveEducation(6);
            village.improveTrust(4);
        } else {
            textInterface.displayStory("\nSome villagers point out their limited food options in these hard times.");
            textInterface.displayDramatic("\"We eat what we can find, doctor,\" a mother explains sadly.");
            textInterface
                    .displayStory("You acknowledge their struggles and focus on making the best of available foods.");
            village.improveEducation(4);
            village.improveTrust(2);
        }

        // Check and update traveler quest progress
        if (activeQuests.contains("traveler_quest") &&
                keyVillagers.containsKey("traveler") &&
                keyVillagers.get("traveler") instanceof SpecialNPC) {
            SpecialNPC traveler = (SpecialNPC) keyVillagers.get("traveler");
            if (traveler.getRole().equals("Traveling Scholar")) {
                updateQuestProgress("traveler_quest", 1);
                textInterface.displayQuick("Brother Benedict takes notes on your educational methods.");
            }
        }
    }

    // Display game tutorial
    private void showTutorial() {
        textInterface.displayHeader("TUTORIAL");

        textInterface.displayStory("Welcome to the Plague Doctor's Day tutorial!");
        boolean skipTutorial = textInterface.askYesNo("Would you like to skip the tutorial?");
        if (skipTutorial)
            return;

        textInterface.displayStory(
                "In this game, you will play as a plague doctor trying to save a village from the Black Death.");
        textInterface.displayWithDelay(
                "You will manage your HEALTH, CLEANLINESS, and ENERGY while treating patients and interacting with villagers.",
                1800);
        textInterface.displayWithDelay("Each day, you will have a limited number of actions to perform.", 1500);
        textInterface.displayWithDelay(
                "You can treat patients, gather herbs, educate villagers, and interact with key NPCs.", 1800);
        textInterface.displayWithDelay("Your choices will affect the village's trust in you and your overall success.",
                1800);
        textInterface.displayWithDelay(
                "Remember to keep an eye on your health and cleanliness, as they will impact your effectiveness.",
                1800);
        textInterface.displayWithDelay(
                "You can also accept quests from special NPCs to gain rewards and improve your skills.", 1800);
        textInterface.displayStory(
                "Use your resources wisely and make strategic decisions to lead the village to recovery.");
        textInterface.displayDramatic("Good luck, doctor! The fate of Alderbrook village is in your hands.");
        textInterface.waitForEnter("Press ENTER to continue to the game");
    }

    // NEW: Trading system with merchant Anna
    private void tradeWithMerchant(NPC merchant) {
        textInterface.displayHeader("ANNA'S TRADING POST");

        // Check relationship level for pricing
        int relationshipLevel = merchant.getRelationshipLevel();
        boolean goodRelationship = relationshipLevel > 60;
        boolean excellentRelationship = relationshipLevel > 80;

        if (goodRelationship) {
            textInterface.displayStory("Anna greets you warmly and shows you her best goods.");
            if (excellentRelationship) {
                textInterface.displayQuick("\"For you, doctor, I offer special prices!\"");
            }
        } else {
            textInterface.displayStory("Anna eyes you warily but shows you her available wares.");
            textInterface.displayQuick("\"Prices are firm. No haggling.\"");
        }

        // Calculate prices based on relationship
        int protectiveGearPrice = excellentRelationship ? 2 : goodRelationship ? 3 : 4;
        int soapPrice = excellentRelationship ? 1 : goodRelationship ? 1 : 2;
        int herbPrice = excellentRelationship ? 1 : goodRelationship ? 2 : 2;

        while (true) {
            textInterface.display("\nWhat would you like to trade for?");
            textInterface.display("1: Protective gear (" + protectiveGearPrice + " coins)");
            textInterface.display("2: Soap (" + soapPrice + " coin" + (soapPrice > 1 ? "s" : "") + ")");
            textInterface.display("3: Herbs (" + herbPrice + " coin" + (herbPrice > 1 ? "s" : "") + " each)");

            boolean bundleAvailable = excellentRelationship && currentDay > 10;
            int sellOption, leaveOption;

            if (bundleAvailable) {
                textInterface.display("4: Advanced supplies bundle (8 coins) - Limited time!");
                sellOption = 5;
                leaveOption = 6;
            } else {
                sellOption = 4;
                leaveOption = 5;
            }

            textInterface.display(sellOption + ": Sell items to Anna");
            textInterface.display(leaveOption + ": Leave");

            int choice = textInterface.getChoice("Choose an option", 1, leaveOption);

            switch (choice) {
                case 1: // Protective gear
                    if (player.getItemCount("coins") >= protectiveGearPrice) {
                        player.useItem("coins", protectiveGearPrice);
                        player.addItem("protective_gear", 1);
                        textInterface.displayNotification(
                                "You traded " + protectiveGearPrice + " coins for protective gear!");
                        merchant.improveRelationship(2);
                        if (goodRelationship) {
                            textInterface.displayQuick("Anna adds: \"This should keep you safe, doctor.\"");
                        }
                    } else {
                        textInterface.displayStory("You don't have enough coins (need " + protectiveGearPrice + ").");
                    }
                    break;

                case 2: // Soap
                    if (player.getItemCount("coins") >= soapPrice) {
                        player.useItem("coins", soapPrice);
                        player.addItem("soap", 1);
                        textInterface.displayNotification(
                                "You traded " + soapPrice + " coin" + (soapPrice > 1 ? "s" : "") + " for soap!");
                        merchant.improveRelationship(1);
                    } else {
                        textInterface.displayStory("You don't have enough coins.");
                    }
                    break;

                case 3: // Herbs
                    if (player.getItemCount("coins") >= herbPrice) {
                        textInterface.display(
                                "How many herbs would you like? (You have " + player.getItemCount("coins") + " coins)");
                        int maxHerbs = player.getItemCount("coins") / herbPrice;
                        int herbQuantity = textInterface.getChoice("Enter quantity", 1, Math.min(maxHerbs, 5));

                        int totalCost = herbQuantity * herbPrice;
                        player.useItem("coins", totalCost);
                        player.addItem("herbs", herbQuantity);
                        textInterface.displayNotification(
                                "You traded " + totalCost + " coins for " + herbQuantity + " herbs!");
                        merchant.improveRelationship(1);
                    } else {
                        textInterface.displayStory("You don't have enough coins (need " + herbPrice + ").");
                    }
                    break;

                case 4: // Either Advanced bundle OR Sell items (depending on availability)
                    if (bundleAvailable) {
                        // Advanced bundle
                        if (player.getItemCount("coins") >= 8) {
                            player.useItem("coins", 8);
                            player.addItem("protective_gear", 2);
                            player.addItem("soap", 2);
                            player.addItem("herbs", 3);
                            textInterface.displayDramatic("Anna brings out a special bundle from her private stock!");
                            textInterface.displayNotification("Advanced bundle: 2 protective gear, 2 soap, 3 herbs!");
                            merchant.improveRelationship(3);
                            textInterface.displayQuick("\"This is my best deal, doctor. Don't tell the others!\"");
                        } else {
                            textInterface.displayStory("You need 8 coins for the advanced bundle.");
                        }
                    } else {
                        // Sell items (when bundle not available)
                        sellItemsToMerchant(merchant);
                    }
                    break;

                case 5: // Sell items OR Leave (depending on availability)
                    if (bundleAvailable) {
                        sellItemsToMerchant(merchant);
                    } else {
                        textInterface.displayStory("You thank Anna and leave her shop.");
                        return;
                    }
                    break;

                case 6: // Leave (only when bundle is available)
                    if (bundleAvailable) {
                        textInterface.displayStory("You thank Anna and leave her shop.");
                        return;
                    }
                    break;
            }

            // Show updated coin count
            textInterface.displayQuick("You now have " + player.getItemCount("coins") + " coins.");
        }
    }

    // NEW: Selling system
    private void sellItemsToMerchant(NPC merchant) {
        textInterface.displayHeader("SELL ITEMS TO ANNA");
        textInterface.displayStory("Anna examines your items to see what she might buy.");

        Map<String, Integer> inventory = player.getInventory();
        boolean hasItemsToSell = false;

        // Items Anna will buy and their prices
        Map<String, Integer> sellPrices = new HashMap<>();
        sellPrices.put("herbs", 1); // Sell herbs for 1 coin each
        sellPrices.put("royal_medicine", 5); // Rare items worth more
        sellPrices.put("ancient_texts", 3);
        sellPrices.put("rare_supplies", 2);

        textInterface.display("\nItems Anna is interested in:");
        for (Map.Entry<String, Integer> sellItem : sellPrices.entrySet()) {
            String itemName = sellItem.getKey();
            int sellPrice = sellItem.getValue();
            int playerQuantity = player.getItemCount(itemName);

            if (playerQuantity > 0) {
                textInterface.display("- " + itemName + ": " + playerQuantity + " available (" + sellPrice + " coin"
                        + (sellPrice > 1 ? "s" : "") + " each)");
                hasItemsToSell = true;
            }
        }

        if (!hasItemsToSell) {
            textInterface.displayStory("\"Sorry doctor, I don't see anything I need right now.\"");
            textInterface
                    .displayQuick("Anna suggests: \"Bring me herbs, rare medicines, or ancient texts next time.\"");
            return;
        }

        // Show total potential earnings
        int totalPossibleEarnings = 0;
        for (Map.Entry<String, Integer> sellItem : sellPrices.entrySet()) {
            String itemName = sellItem.getKey();
            int sellPrice = sellItem.getValue();
            int playerQuantity = player.getItemCount(itemName);
            totalPossibleEarnings += sellPrice * playerQuantity;
        }

        if (totalPossibleEarnings > 0) {
            textInterface.displayQuick("Total possible earnings: " + totalPossibleEarnings + " coins");
        }

        textInterface.display("\nWhat would you like to sell?");
        int optionNumber = 1;
        Map<Integer, String> sellOptions = new HashMap<>();

        for (Map.Entry<String, Integer> sellItem : sellPrices.entrySet()) {
            String itemName = sellItem.getKey();
            if (player.getItemCount(itemName) > 0) {
                textInterface.display(optionNumber + ": " + itemName + " (" + sellPrices.get(itemName) + " coin"
                        + (sellPrices.get(itemName) > 1 ? "s" : "") + " each)");
                sellOptions.put(optionNumber, itemName);
                optionNumber++;
            }
        }
        textInterface.display(optionNumber + ": Nothing, go back");

        int choice = textInterface.getChoice("Choose an item to sell", 1, optionNumber);

        if (choice == optionNumber) {
            return; // Go back
        }

        String itemToSell = sellOptions.get(choice);
        int sellPrice = sellPrices.get(itemToSell);
        int playerQuantity = player.getItemCount(itemToSell);

        textInterface.display("How many " + itemToSell + " would you like to sell? (You have " + playerQuantity + ")");
        int quantity = textInterface.getChoice("Enter quantity", 1, playerQuantity);

        int totalEarned = quantity * sellPrice;
        player.useItem(itemToSell, quantity);
        player.addItem("coins", totalEarned);

        textInterface
                .displayNotification("You sold " + quantity + " " + itemToSell + " for " + totalEarned + " coins!");
        merchant.improveRelationship(1);

        if (merchant.getRelationshipLevel() > 70) {
            textInterface.displayQuick("Anna smiles: \"Always a pleasure doing business with you, doctor!\"");
        }
    }
}
