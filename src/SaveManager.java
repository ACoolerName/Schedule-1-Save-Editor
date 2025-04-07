import java.io.File;
import java.util.Scanner;
import util.TerminalUtil;

public class SaveManager {
    private static final String USER_HOME = System.getProperty("user.home");
    private static final String BASE_SAVE_PATH = USER_HOME + "\\AppData\\LocalLow\\TVGS\\Schedule I\\Saves\\";
    private static String steamId;
    private static String basePath;

    public static void selectSaveSlot(Scanner scanner) {
        steamId = resolveSteamId(scanner);
        basePath = BASE_SAVE_PATH + steamId + "\\";

        
        int slot;

        while (true) {
            System.out.print("Select save slot (1-5) or -1 to exit: ");
            slot = scanner.nextInt();

            if (slot == -1) {
                System.out.println("Goodbye!");
                System.exit(0);
            }

            File saveFolder = new File(basePath + "SaveGame_" + slot);
            if (saveFolder.exists()) {
                loadMainMenu(slot);
                break;
            } else {
                System.out.println("No save found in that slot.\n");
            }
        }
    }

    private static String resolveSteamId(Scanner scanner) {
        File savesDir = new File(BASE_SAVE_PATH);
        File[] steamDirs = savesDir.listFiles(File::isDirectory);

        if (steamDirs == null || steamDirs.length == 0) {
            System.out.println("No Steam save folders found.");
            System.exit(1);
        }

        if (steamDirs.length == 1) {
            return steamDirs[0].getName();
        }

        System.out.println("Multiple Steam IDs found:");
        for (int i = 0; i < steamDirs.length; i++) {
            System.out.printf("%d. %s\n", i + 1, steamDirs[i].getName());
        }

        int choice;
        while (true) {
            System.out.print("Choose Steam ID: ");
            choice = scanner.nextInt();
            if (choice >= 1 && choice <= steamDirs.length) {
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }

        return steamDirs[choice - 1].getName();
    }

    private static void loadMainMenu(int slot) {
        while (true) {
            TerminalUtil.clearScreen();
            System.out.println("Loaded SaveGame_" + slot);
            System.out.println("1. Edit Inventory");
            System.out.println("2. Edit Money");
            System.out.println("3. Edit Unlocks");
            System.out.println("-1. Go back");

            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    MenuInventory.run(scanner, slot);
                    break;
                case 2: 
                    MenuMoney.run(scanner, slot);
                    break;
                case 3: 
                    MenuUnlocks.run(scanner, slot);
                    break;

                case -1: 
                    TerminalUtil.clearScreen();
                    selectSaveSlot(scanner);
                    break;
                default:
                    System.out.println("Invalid input.");
                    break;
            }
        }
    }

    public static String getBasePath() {
        return basePath;
    }
}
