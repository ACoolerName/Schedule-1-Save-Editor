import org.json.JSONObject;
import util.JsonUtil;
import util.TerminalUtil;
import java.io.File;
import java.util.Scanner;

public class MenuUnlocks {
    public static void run(Scanner scanner, int slot) {
        while (true) {
            TerminalUtil.clearScreen();
            System.out.println("1. Unlock Items & Areas");
            System.out.println("2. Toggle Business Ownership");
            System.out.println("-1. Go Back");

            String input = scanner.nextLine();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                continue;
            }

            if (choice == 1) {
                unlockItemsAndAreas(scanner, slot);
            } else if (choice == 2) {
                toggleBusinesses(scanner, slot);
            } else if (choice == -1) {
                break;
            }
        }
    }

    private static void unlockItemsAndAreas(Scanner scanner, int slot) {
        TerminalUtil.clearScreen();
        System.out.print("Are you sure you want to unlock all items & areas? (y/n): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("y")) {
            File rankFile = new File(SaveManager.getBasePath() + "SaveGame_" + slot + "\\Rank.json");
            JSONObject json = JsonUtil.readJson(rankFile);
            json.put("Rank", 10);
            json.put("Tier", 1);
            JsonUtil.writeJson(rankFile, json);

            System.out.println("Successfully unlocked all. Press enter...");
            scanner.nextLine();
        }
    }

    private static void toggleBusinesses(Scanner scanner, int slot) {
        String[] businesses = { "Car Wash", "Laundromat", "Post Office", "Taco Ticklers" };

        while (true) {
            TerminalUtil.clearScreen();
            System.out.println("Select a business:");
            for (int i = 0; i < businesses.length; i++) {
                System.out.printf("%d. %s%n", i + 1, businesses[i]);
            }
            System.out.println("-1. Go Back");

            String input = scanner.nextLine();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                continue;
            }

            if (choice == -1) {
                break;
            }

            if (choice < 1 || choice > businesses.length) {
                continue;
            }

            String businessName = businesses[choice - 1];
            String path = SaveManager.getBasePath() + "SaveGame_" + slot + "\\Businesses\\" + businessName + "\\Business.json";
            File businessFile = new File(path);

            if (!businessFile.exists()) {
                System.out.println("Business file not found. Press enter...");
                scanner.nextLine();
                continue;
            }

            JSONObject json = JsonUtil.readJson(businessFile);
            boolean isOwned = json.getBoolean("IsOwned");

            TerminalUtil.clearScreen();
            System.out.printf("%s (%s)%n", businessName, isOwned ? "Owned" : "Unowned");
            System.out.printf("Change ownership status to %s? (y/n): ", isOwned ? "Unowned" : "Owned");

            String confirm = scanner.nextLine();
            if (confirm.equalsIgnoreCase("y")) {
                json.put("IsOwned", !isOwned);
                JsonUtil.writeJson(businessFile, json);

                System.out.printf("Successfully changed ownership of %s to %s. Press enter...%n", businessName, !isOwned ? "Owned" : "Unowned");
                scanner.nextLine();
            }
        }
    }
}
