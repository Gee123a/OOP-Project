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
    private String VERSION = "1.1";
    private String LAST_UPDATED = "2025-06-03";
    private ConsoleTextInterface textInterface = new ConsoleTextInterface();
    private ArrayList<String> activeQuests = new ArrayList<>();
    private Map<String, Integer> questProgress = new HashMap<>();

    public void run() {
        // Initialize game
        initializeGame();

        textInterface.display("Plague Doctor's Day v" + VERSION);
        textInterface.display("Last updated: " + LAST_UPDATED);

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
        return java.time.LocalDateTime.now().toString();
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
                    restAndRecover();
                    actionsRemaining--;
                    break;

                case 3: // Clean
                    cleanYourself();
                    actionsRemaining--;
                    break;

                case 4: // Gather herbs
                    gatherHerbs();
                    actionsRemaining--;
                    break;

                case 5: // Educate villagers
                    educateVillagers();
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

    // ACTION CHOICE 1 - Treat Patients
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

    // ACTION CHOICE 2 - Rest and Recover
    private void restAndRecover() {
        textInterface.display("You spend time resting and recovering your strength.");
        player.rest();
    }

    // ACTION CHOICE 3 - Clean Yourself
    private void cleanYourself() {
        textInterface.display("You clean yourself thoroughly.");
        player.clean();
        textInterface.display("Your cleanliness is now: " + player.getCleanliness());
    }

    // ACTION CHOICE 4 - Gather Herbs
    private void gatherHerbs() {
        textInterface.display("You venture out to gather medicinal herbs.");
        int herbsFound = 1 + random.nextInt(3); // 1-3 herbs
        player.addItem("herbs", herbsFound);
        player.setEnergy(player.getEnergy() - 20);
        textInterface.display("You found " + herbsFound + " herbs for your medicines!");

        updateQuestProgress("doctor_quest", herbsFound);
    }

    // ACTION CHOICE 5 - Educate Villagers
    private void educateVillagers() {
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
    }

    private void educateAboutHygiene() {
        textInterface.display("\n=== HYGIENE AND CLEANLINESS EDUCATION ===");
        textInterface.display("You gather the villagers in the town square to teach about personal cleanliness.");

        textInterface.display("\n\"Listen carefully,\" you begin, adjusting your plague mask.");
        textInterface.display("\"The first defense against the pestilence is cleanliness of body and home.\"");

        textInterface.display("\n=== KEY LESSONS YOU TEACH ===");
        textInterface.display("• HAND WASHING: \"Wash your hands frequently with soap and clean water,");
        textInterface.display("  especially before eating and after touching sick persons.\"");
        textInterface.display("• BATHING: \"Bathe regularly to remove corrupt humors from the skin.\"");
        textInterface
                .display("• CLEAN CLOTHING: \"Change and wash your garments often - dirty cloth harbors disease.\"");
        textInterface.display("• HOME SANITATION: \"Keep your homes clean, dispose of waste properly,");
        textInterface.display("  and ensure good air circulation.\"");

        // Show villager reactions based on education level
        if (village.getEducationLevel() < 30) {
            textInterface.display("\nMany villagers look skeptical. Some mutter about 'foreign ideas.'");
            textInterface
                    .display("An elder speaks up: \"Our grandparents never bathed so much and lived long lives!\"");
            village.improveEducation(3);
            village.improveTrust(1);
        } else if (village.getEducationLevel() < 60) {
            textInterface.display("\nSome villagers nod thoughtfully, while others still seem uncertain.");
            textInterface.display("A young mother asks: \"How often should we wash our children, doctor?\"");
            village.improveEducation(5);
            village.improveTrust(3);
        } else {
            textInterface.display("\nThe villagers listen attentively and ask intelligent questions.");
            textInterface.display("Several people volunteer to help spread these practices to their families.");
            village.improveEducation(7);
            village.improveTrust(4);
            village.improveCleanliness(5);
        }

        textInterface.display("\nYou see some villagers immediately checking their hands and clothes.");
        textInterface.display("Your lessons about cleanliness are slowly taking root in the community.");
    }

    private void educateAboutTransmission() {
        textInterface.display("\n=== DISEASE TRANSMISSION EDUCATION ===");
        textInterface.display("You explain how the plague spreads from person to person.");

        textInterface.display("\n\"The pestilence travels in ways both seen and unseen,\" you explain.");
        textInterface.display("\"Understanding its paths helps us block its advance.\"");

        textInterface.display("\n=== TRANSMISSION METHODS YOU EXPLAIN ===");
        textInterface.display("• CLOSE CONTACT: \"The disease passes through the breath and touch of the sick.\"");
        textInterface.display("• CONTAMINATED ITEMS: \"Clothing, bedding, and tools of the infected carry danger.\"");
        textInterface.display("• POOR SANITATION: \"Waste and filth create breeding grounds for corruption.\"");
        textInterface.display("• CROWDED SPACES: \"Markets and gatherings allow rapid spread between people.\"");

        textInterface.display("\n=== PREVENTION STRATEGIES ===");
        textInterface.display("• DISTANCE: \"Maintain space from the visibly sick - arm's length minimum.\"");
        textInterface.display("• AVOID CROWDS: \"Limit time in busy markets and large gatherings.\"");
        textInterface.display("• CLEAN SURFACES: \"Wash items that may have contacted the sick.\"");
        textInterface.display("• ISOLATE THE ILL: \"Keep sick family members in separate rooms when possible.\"");

        if (village.getEducationLevel() < 40) {
            textInterface.display("\nSeveral villagers argue: \"But we must care for our sick family members!\"");
            textInterface.display("You respond: \"Care for them, yes, but with precautions to protect yourselves.\"");
            village.improveEducation(4);
            village.improveTrust(2);
        } else {
            textInterface.display("\nThe villagers grasp these concepts quickly and begin discussing");
            textInterface.display("how to reorganize their homes and daily routines.");
            village.improveEducation(6);
            village.improveTrust(3);
            // Slight immediate effect on infection reduction
            if (random.nextDouble() < 0.3) {
                textInterface.display("Your education immediately helps - some villagers avoid a potential exposure!");
            }
        }
    }

    private void educateAboutHerbalMedicine() {
        textInterface.display("\n=== HERBAL MEDICINE EDUCATION ===");

        if (player.hasItem("herbs")) {
            textInterface.display("You display your collection of herbs to teach their medicinal properties.");
            textInterface.display("\n\"Nature provides remedies for those who know where to look,\" you explain.");

            textInterface.display("\n=== MEDICINAL HERBS AND THEIR USES ===");
            textInterface.display("• WILLOW BARK: \"Chew this to reduce fever and ease pain in the joints.\"");
            textInterface.display("• ELDERBERRY: \"Make tea from these berries to strengthen the body's defenses.\"");
            textInterface.display("• GARLIC: \"Eat daily to purify the blood and ward off corruption.\"");
            textInterface.display("• THYME: \"Burn as incense to cleanse the air of pestilent vapors.\"");
            textInterface.display("• CHAMOMILE: \"Brew tea to calm the spirit and aid restful sleep.\"");

            textInterface.display("\n=== PREPARATION METHODS ===");
            textInterface.display("• TEAS: \"Steep herbs in hot water for the count of 200 heartbeats.\"");
            textInterface.display("• POULTICES: \"Crush fresh herbs and apply to wounds or swellings.\"");
            textInterface.display("• TINCTURES: \"Soak herbs in wine for seven days to extract their essence.\"");

            // Consume some herbs in the demonstration
            player.useItem("herbs", 1);
            textInterface.display("\nYou use one herb in your demonstration.");

            village.improveEducation(6);
            village.improveTrust(4);
            textInterface.display("Several villagers take notes and promise to search for these herbs.");

        } else {
            textInterface.display("You realize you don't have herbs to demonstrate with!");
            textInterface.display("You explain herbal medicine theory, but without examples, it's less effective.");
            village.improveEducation(2);
            village.improveTrust(1);
            textInterface.display("The villagers seem interested but want to see actual herbs next time.");
        }
    }

    private void educateAboutQuarantine() {
        textInterface.display("\n=== QUARANTINE AND ISOLATION EDUCATION ===");
        textInterface.display("You explain the importance of separating the sick from the healthy.");

        textInterface.display("\n\"Sometimes, harsh measures preserve the greater good,\" you begin solemnly.");
        textInterface.display("\"Separation protects the many, even as we care for the few.\"");

        textInterface.display("\n=== QUARANTINE PRINCIPLES ===");
        textInterface.display("• EARLY ISOLATION: \"Separate those showing first signs of sickness immediately.\"");
        textInterface.display("• DEDICATED CAREGIVERS: \"Assign specific people to tend the sick - not everyone.\"");
        textInterface.display("• BARRIER PROTECTION: \"Use separate dishes, clothing, and bedding for the ill.\"");
        textInterface.display("• SCHEDULED CARE: \"Visit the sick at set times, not constantly throughout the day.\"");

        textInterface.display("\n=== COMMUNITY MEASURES ===");
        textInterface.display("• TRAVEL RESTRICTIONS: \"Limit movement between infected and clean areas.\"");
        textInterface.display("• MARKET CONTROLS: \"Reduce market days and limit crowd sizes.\"");
        textInterface.display("• FAMILY PODS: \"Keep families together but separate from other families.\"");

        // Different reactions based on current quarantine status
        if (village.isQuarantineActive()) {
            textInterface.display("\nSince quarantine is already active, villagers understand its necessity better.");
            textInterface.display("\"We see the wisdom in these measures,\" an elder acknowledges.");
            village.improveEducation(5);
            village.improveTrust(3);
        } else {
            textInterface.display("\nSome villagers look uncomfortable with these strict measures.");
            textInterface.display("\"You ask us to abandon our sick neighbors?\" someone challenges.");
            textInterface.display("You explain: \"Not abandon - protect them AND protect yourselves.\"");
            village.improveEducation(4);

            if (village.getTrustLevel() > 60) {
                village.improveTrust(2);
                textInterface.display("Most villagers trust your judgment, even if they don't like the measures.");
            } else {
                village.lowerTrust(1);
                textInterface.display("Some villagers remain suspicious of your 'foreign' ideas about separation.");
            }
        }
    }

    private void educateAboutNutrition() {
        textInterface.display("\n=== NUTRITION AND BODY STRENGTHENING EDUCATION ===");
        textInterface.display("You teach about foods and practices that strengthen the body against disease.");

        textInterface.display("\n\"A strong body resists corruption better than a weak one,\" you explain.");
        textInterface.display("\"What we consume shapes our ability to fight the pestilence.\"");

        textInterface.display("\n=== STRENGTHENING FOODS ===");
        textInterface.display("• FRESH FRUITS: \"Apples and berries purify the blood and provide vital humors.\"");
        textInterface.display("• VEGETABLES: \"Onions, leeks, and cabbage build resistance to disease.\"");
        textInterface
                .display("• CLEAN WATER: \"Pure water flushes corruption from the body - avoid stagnant sources.\"");
        textInterface
                .display("• MODERATE MEAT: \"Fresh meat provides strength, but avoid excess during plague times.\"");
        textInterface.display("• HONEY: \"Nature's medicine - soothes throat and provides clean energy.\"");

        textInterface.display("\n=== FOODS TO AVOID ===");
        textInterface.display("• SPOILED FOODS: \"Rotten meat and moldy bread invite corruption into the body.\"");
        textInterface.display("• EXCESS ALCOHOL: \"Wine in moderation cleanses, but excess weakens the spirit.\"");
        textInterface
                .display("• HEAVY SPICES: \"During plague, simple foods are easier for weakened bodies to process.\"");

        textInterface.display("\n=== HEALTHY PRACTICES ===");
        textInterface.display("• REGULAR MEALS: \"Eat at consistent times to maintain body rhythm.\"");
        textInterface.display("• PORTION CONTROL: \"Better to eat less frequently than to overburden the stomach.\"");
        textInterface.display("• FOOD PREPARATION: \"Cook thoroughly and keep cooking areas clean.\"");

        if (village.getCleanliness() > 50) {
            textInterface
                    .display("\nThe villagers respond well - their clean environment supports your nutrition advice.");
            village.improveEducation(6);
            village.improveTrust(4);
            textInterface.display("Several families volunteer to share healthy recipes with their neighbors.");
        } else {
            textInterface.display("\nSome villagers point out their limited food options in these hard times.");
            textInterface.display("\"We eat what we can find, doctor,\" a mother explains sadly.");
            village.improveEducation(4);
            village.improveTrust(2);
            textInterface.display("You acknowledge their struggles and focus on making the best of available foods.");
        }
    }

    // ACTION CHOICE 6 - Speak with a Villager
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

    // FOLLOWING ACTION OF CHOICE 6
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
        textInterface.display(
                "In this game, you will play as a plague doctor trying to save a village from the Black Death.");
        textInterface.display(
                "You will manage your HEALTH, CLEANLINESS, and ENERGY while treating patients and interacting with villagers.");
        textInterface.display("Each day, you will have a limited number of actions to perform.");
        textInterface.display("You can treat patients, gather herbs, educate villagers, and interact with key NPCs.");
        textInterface.display("Your choices will affect the village's trust in you and your overall success.");
        textInterface.display(
                "Remember to keep an eye on your health and cleanliness, as they will impact your effectiveness.");
        textInterface.display("You can also accept quests from special NPCs to gain rewards and improve your skills.");
        textInterface
                .display("Use your resources wisely and make strategic decisions to lead the village to recovery.");
        textInterface.display("Good luck, doctor! The fate of Alderbrook village is in your hands.");
        textInterface.display("Press ENTER to continue to the game...");
        scanner.nextLine();

    }
}
