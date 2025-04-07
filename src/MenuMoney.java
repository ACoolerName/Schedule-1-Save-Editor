import org.json.JSONArray;
import org.json.JSONObject;
import util.JsonUtil;
import util.TerminalUtil;
import java.io.File;
import java.util.Scanner;

public class MenuMoney {
    public static void run(Scanner scanner, int slot) {
        while (true) {
            TerminalUtil.clearScreen();
            System.out.println("Edit Money");
            System.out.println("1. Cash");
            System.out.println("2. Card");
            System.out.println("3. Reset Deposit Limit");
            System.out.println("-1. Go back");

            int choice = scanner.nextInt();

            if (choice == -1) return;

            if (choice == 3) {
                File moneyFile = new File(SaveManager.getBasePath() + "SaveGame_" + slot + "\\Money.json");
                JSONObject json = JsonUtil.readJson(moneyFile);
                json.put("WeeklyDepositSum", 0.0);
                JsonUtil.writeJson(moneyFile, json);

                System.out.println("Deposit limit reset. Press enter...");
                scanner.nextLine();
                scanner.nextLine();
                continue;
            }

            System.out.print("Enter new amount (0-99999999), -1 to go back: ");
            int amount = scanner.nextInt();

            if (amount == -1) continue;
            if (amount < 0) amount = 0;
            if (amount > 99999999) amount = 99999999;

            if (choice == 1) {
                File invFile = new File(SaveManager.getBasePath() + "SaveGame_" + slot + "\\Players\\Player_0\\Inventory.json");
                JSONObject json = JsonUtil.readJson(invFile);
                JSONArray items = json.getJSONArray("Items");

                for (int i = 0; i < items.length(); i++) {
                    String itemStr = items.getString(i);
                    JSONObject itemObj = new JSONObject(itemStr);

                    if (itemObj.has("DataType") && itemObj.getString("DataType").equals("CashData")) {
                        itemObj.put("CashBalance", amount);
                        items.put(i, itemObj.toString());
                        break;
                    }
                }
                JsonUtil.writeJson(invFile, json);
            } else if (choice == 2) {
                File moneyFile = new File(SaveManager.getBasePath() + "SaveGame_" + slot + "\\Money.json");
                JSONObject json = JsonUtil.readJson(moneyFile);
                json.put("OnlineBalance", amount);
                JsonUtil.writeJson(moneyFile, json);
            }

            System.out.println("Money updated. Press enter...");
            scanner.nextLine();
            scanner.nextLine();
        }
    }
}
