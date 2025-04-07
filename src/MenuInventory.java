import org.json.JSONArray;
import org.json.JSONObject;
import util.JsonUtil;
import util.TerminalUtil;
import java.io.File;
import java.util.Scanner;

public class MenuInventory {
    public static void run(Scanner scanner, int slot) {
        while (true) {
            TerminalUtil.clearScreen();

            File inventoryFile = new File(SaveManager.getBasePath() + "SaveGame_" + slot + "\\Players\\Player_0\\Inventory.json");
            JSONObject json = JsonUtil.readJson(inventoryFile);
            JSONArray items = json.getJSONArray("Items");

            for (int i = 0; i < 8; i++) {
                String itemStr = items.optString(i, "");
                JSONObject item = new JSONObject(itemStr);

                String id = item.optString("ID", "");
                int quantity = item.optInt("Quantity", 0);

                if (!id.isEmpty() && quantity > 0) {
                    System.out.printf("%d. %s (%d)\n", i + 1, id, quantity);
                } else {
                    System.out.printf("%d. Null\n", i + 1);
                }
            }
            System.out.println("-1. Go back");

            int slotChoice = scanner.nextInt();

            if (slotChoice == -1) return;

            if (slotChoice < 1 || slotChoice > 8) continue;

            String selectedItemStr = items.optString(slotChoice - 1, "");
            JSONObject selectedItem = new JSONObject(selectedItemStr);

            String id = selectedItem.optString("ID", "");
            if (id.isEmpty()) {
                System.out.println("Nothing in that slot. Press enter...");
                scanner.nextLine();
                scanner.nextLine();
                continue;
            }

            TerminalUtil.clearScreen();
            int currentQuantity = selectedItem.optInt("Quantity", 0);

            System.out.printf("Editing %s (%d)\n", id, currentQuantity);
            System.out.print("Enter new quantity (1-250) or -1 to go back: ");
            int newAmount = scanner.nextInt();

            if (newAmount == -1) continue;
            if (newAmount < 1) newAmount = 1;
            if (newAmount > 250) newAmount = 250;

            selectedItem.put("Quantity", newAmount);
            items.put(slotChoice - 1, selectedItem.toString());

            JsonUtil.writeJson(inventoryFile, json);

            System.out.println("Item updated. Press enter...");
            scanner.nextLine();
            scanner.nextLine();
        }
    }
}
