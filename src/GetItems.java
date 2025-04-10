import org.json.JSONArray;
import org.json.JSONObject;
import util.JsonUtil;
import util.TerminalUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class GetItems {
    public static void run(Scanner scanner, int slot) {
        while (true) {
            TerminalUtil.clearScreen();
            
            System.out.println("INFO: For this to work, you need to have an active delivery that has arrived at a property");
            System.out.println("1. View item list");
            System.out.println("2. View vehicle contents");
            System.out.println("3. Add items to vehicle");
            System.out.println("-1. Go back");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    viewItemList(scanner);
                    break;
                case 2:
                    viewVehicleContents(scanner, slot);
                    break;
                case 3:
                    addItemToVehicle(scanner, slot);
                    break;
                case -1:
                    return;
                default:
                    System.out.print("Invalid input. Press enter...");
                    scanner.nextLine();
                    scanner.nextLine();
                    break;
            }
        }
    }

    private static void viewItemList(Scanner scanner) {
        TerminalUtil.clearScreen();
    
        System.out.println("What would you like to do?");
        System.out.println("1. Download list (recommended)");
        System.out.println("2. View in terminal");
        System.out.println("-1. Go back");
        scanner.nextLine();
    
        String choice = scanner.nextLine();
    
        switch (choice) {
            case "1":
                downloadItemList();
                break;
            case "2":
                viewItemListInTerminal();
                break;
            case "-1":
                return;
            default:
                System.out.println("Invalid choice. Returning...");
                break;
        }
    
        System.out.print("Press enter to go back...");
        scanner.nextLine();
    }

    private static void downloadItemList() {
        System.out.println("Downloading item list...");

        String userHome = System.getProperty("user.home");
        File downloadsFolder = new File(userHome, "Downloads");
        File outputFile = new File(downloadsFolder, "itemlist.txt");

        try (InputStream in = new URL("https://raw.githubusercontent.com/ACoolerName/Schedule-1-Save-Editor/refs/heads/main/src/itemlist.txt").openStream();
            FileOutputStream out = new FileOutputStream(outputFile)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            System.out.println("Download complete. File saved to: " + outputFile.getAbsolutePath());

        } catch (IOException e) {
            System.out.println("Failed to download file.");
            System.out.println("You can download it manually from:");
            System.out.println("https://raw.githubusercontent.com/ACoolerName/Schedule-1-Save-Editor/refs/heads/main/src/itemlist.txt");
        }
    }

    private static void viewItemListInTerminal() {
        System.out.println("Reading bundled item list...");
    
        InputStream stream = GetItems.class.getResourceAsStream("itemlist.txt");
    
        if (stream == null) {
            System.out.println("Could not find itemlist.txt inside resources.");
            return;
        }
    
        try (Scanner fileScanner = new Scanner(stream)) {
            while (fileScanner.hasNextLine()) {
                System.out.println(fileScanner.nextLine());
            }
        } catch (Exception e) {
            System.out.println("Failed to read item list.");
        }
    }

    private static void viewVehicleContents(Scanner scanner, int slot) {
        TerminalUtil.clearScreen();
    
        File vehiclesDir = new File(SaveManager.getBasePath() + "SaveGame_" + slot + "\\Deliveries\\DeliveryVehicles");
        File[] vehicles = vehiclesDir.listFiles(File::isDirectory);
    
        if (vehicles == null || vehicles.length == 0) {
            System.out.print("No active deliveries found. Press enter...");
            scanner.nextLine();
            scanner.nextLine();
            return;
        }
    
        for (int i = 0; i < vehicles.length; i++) {
            System.out.printf("%d. %s\n", i + 1, vehicles[i].getName());
        }
        System.out.println("-1. Go back");
    
        int choice = scanner.nextInt();
        if (choice == -1 || choice < 1 || choice > vehicles.length) return;
    
        File vehicleDir = vehicles[choice - 1];
        File contentsFile = new File(vehicleDir, "Contents.json");
        JSONObject json = JsonUtil.readJson(contentsFile);
        JSONArray items = json.getJSONArray("Items");
    
        TerminalUtil.clearScreen();
        for (int i = 0; i < 16; i++) {
            String slotStr = items.getString(i);
            JSONObject itemObj = new JSONObject(slotStr);
            String id = itemObj.optString("ID", "");
            int quantity = itemObj.optInt("Quantity", 0);
    
            if (id.isEmpty() || quantity == 0) {
                System.out.printf("%d. empty\n", i + 1);
            } else {
                System.out.printf("%d. %s %d\n", i + 1, id, quantity);
            }
        }
    
        System.out.println("\n1. Empty van");
        System.out.println("2. Delete items");
        System.out.println("-1. Go back");
    
        scanner.nextLine(); // clear buffer
        String option = scanner.nextLine();
    
        switch (option) {
            case "1":
                System.out.print("Are you sure you want to delete everything in this van? (y/n): ");
                String confirm = scanner.nextLine();
                if (confirm.equalsIgnoreCase("y")) {
                    deleteDirectory(vehicleDir);
                    System.out.print("Van emptied. Press enter...");
                    scanner.nextLine();
                }
                break;
            case "2":
                deleteItemsFromVan(scanner, contentsFile, json, vehicleDir);
                break;
            default:
                break;
        }
    }

    private static void deleteItemsFromVan(Scanner scanner, File contentsFile, JSONObject json, File vehicleDir) {
        JSONArray items = json.getJSONArray("Items");
    
        while (true) {
            System.out.print("Select a slot to edit (1-16) or -1 to go back: ");
            int slotChoice = scanner.nextInt();
            scanner.nextLine(); // clear buffer
    
            if (slotChoice == -1) return;
            if (slotChoice < 1 || slotChoice > 16) continue;
    
            JSONObject slotItem = new JSONObject(items.getString(slotChoice - 1));
            String id = slotItem.optString("ID", "");
            int quantity = slotItem.optInt("Quantity", 0);
    
            if (id.isEmpty() || quantity == 0) {
                System.out.println("That slot is empty. Try again.");
                continue;
            }
    
            System.out.printf("Slot %d: %s (%d)\n", slotChoice, id, quantity);
            System.out.println("1. Clear slot");
            System.out.println("2. Remove x items");
            System.out.println("-1. Go back");
    
            String action = scanner.nextLine();
            switch (action) {
                case "1":
                    items.put(slotChoice - 1, buildItemJson("", 0).toString());
                    break;
                case "2":
                    System.out.print("How many to remove? (-1 to go back): ");
                    int toRemove = scanner.nextInt();
                    scanner.nextLine();
                    if (toRemove == -1) continue;
                    if (toRemove >= quantity) {
                        items.put(slotChoice - 1, buildItemJson("", 0).toString());
                    } else if (toRemove > 0) {
                        slotItem.put("Quantity", quantity - toRemove);
                        items.put(slotChoice - 1, slotItem.toString());
                    } else {
                        System.out.println("Invalid amount.");
                    }
                    break;
                case "-1":
                    return;
                default:
                    System.out.println("Invalid input.");
                    break;
            }
    
            JsonUtil.writeJson(contentsFile, json);
    
            boolean allEmpty = true;
            for (int i = 0; i < items.length(); i++) {
                JSONObject obj = new JSONObject(items.getString(i));
                if (!obj.getString("ID").isEmpty() && obj.getInt("Quantity") > 0) {
                    allEmpty = false;
                    break;
                }
            }
    
            if (allEmpty) {
                deleteDirectory(vehicleDir);
                System.out.print("All items removed. Van deleted. Press enter...");
                scanner.nextLine();
                return;
            }
        }
    }

    private static void addItemToVehicle(Scanner scanner, int slot) {
        TerminalUtil.clearScreen();
    
        File vehiclesDir = new File(SaveManager.getBasePath() + "SaveGame_" + slot + "\\Deliveries\\DeliveryVehicles");
        File[] vehicles = vehiclesDir.listFiles(File::isDirectory);
    
        if (vehicles == null || vehicles.length == 0) {
            System.out.print("No delivery vehicles found. Press enter...");
            scanner.nextLine();
            scanner.nextLine();
            return;
        }
    
        for (int i = 0; i < vehicles.length; i++) {
            System.out.printf("%d. %s\n", i + 1, vehicles[i].getName());
        }
        System.out.println("-1. Go back");
    
        int choice = scanner.nextInt();
        if (choice == -1 || choice < 1 || choice > vehicles.length) return;
    
        File vehicleDir = vehicles[choice - 1];
        File contentsFile = new File(vehicleDir, "Contents.json");
        JSONObject json = JsonUtil.readJson(contentsFile);
        JSONArray items = json.getJSONArray("Items");
    
        Set<String> validItems = new HashSet<>();
        try (Scanner fileScanner = new Scanner(GetItems.class.getResourceAsStream("itemlist.txt"))) {
            while (fileScanner.hasNextLine()) {
                validItems.add(fileScanner.nextLine().trim());
            }
        } catch (Exception e) {
            System.out.println("Failed to read item list. Cannot validate inputs.");
        }

        scanner.nextLine();
        
        while (true) {
            int emptySlots = 0, partiallyAvailable = 0, totalSpace = 0;
            Map<Integer, Integer> partialSlots = new LinkedHashMap<>();
    
            for (int i = 0; i < items.length(); i++) {
                JSONObject obj = new JSONObject(items.getString(i));
                String id = obj.getString("ID");
                int qty = obj.getInt("Quantity");
    
                if (id.isEmpty() || qty == 0) {
                    emptySlots++;
                } else if (qty < 20) {
                    partiallyAvailable++;
                    partialSlots.put(i, qty);
                }
            }
    
            int totalAvailable = (emptySlots + partialSlots.size()) * 20 - partialSlots.values().stream().mapToInt(i -> i).sum();
    
            if (totalAvailable == 0) {
                System.out.print("This van is full and can't hold any more items. Press enter...");
                scanner.nextLine();
                scanner.nextLine();
                return;
            }
    
            String slotsText = partiallyAvailable > 0 && emptySlots == 0
                    ? String.format("has %d slots partially available", partiallyAvailable)
                    : String.format("has %d slots available", emptySlots);
    
            System.out.printf("This delivery %s and can hold up to %d more items.\n", slotsText, totalAvailable);
            System.out.print("Enter item ID to add (or -1 to go back): ");
            String itemId = scanner.nextLine();
    
            if (itemId.equals("-1")) return;
            if (!validItems.contains(itemId)) {
                System.out.println("Invalid item ID. Please consult the item list.");
                continue;
            }
    
            int max = 0;
            int existingPartialQty = partialSlots.entrySet().stream()
                .filter(e -> new JSONObject(items.getString(e.getKey())).getString("ID").equals(itemId))
                .mapToInt(e -> 20 - e.getValue())
                .sum();
    
            boolean alreadyExists = items.toList().stream()
                .map(Object::toString)
                .anyMatch(str -> new JSONObject(str).getString("ID").equals(itemId));
    
            max = alreadyExists ? existingPartialQty + emptySlots * 20 : emptySlots * 20;
    
            while (true) {
                System.out.printf("Enter quantity (max: %d, -1 to go back): ", max);
                int quantity = scanner.nextInt();
                scanner.nextLine();
    
                if (quantity == -1 || quantity == 0) break;
                if (quantity < 1 || quantity > max) {
                    System.out.println("Invalid quantity. Try again.");
                    continue;
                }
    
                // Fill existing partials first
                for (Map.Entry<Integer, Integer> entry : partialSlots.entrySet()) {
                    int index = entry.getKey();
                    JSONObject obj = new JSONObject(items.getString(index));
                    if (!obj.getString("ID").equals(itemId)) continue;
    
                    int space = 20 - obj.getInt("Quantity");
                    int toAdd = Math.min(space, quantity);
                    obj.put("Quantity", obj.getInt("Quantity") + toAdd);
                    items.put(index, obj.toString());
                    quantity -= toAdd;
                    if (quantity <= 0) break;
                }
    
                // Then fill empty slots
                for (int i = 0; i < 16 && quantity > 0; i++) {
                    JSONObject obj = new JSONObject(items.getString(i));
                    if (!obj.getString("ID").isEmpty()) continue;
    
                    int toAdd = Math.min(20, quantity);
                    items.put(i, buildItemJson(itemId, toAdd).toString());
                    quantity -= toAdd;
                }
    
                JsonUtil.writeJson(contentsFile, json);
                System.out.print("Item(s) added. Press enter...");
                scanner.nextLine();
                break;
            }
        }
    }

    private static JSONObject buildItemJson(String id, int quantity) {
        JSONObject item = new JSONObject();
        item.put("DataType", "ItemData");
        item.put("DataVersion", 0);
        item.put("GameVersion", "0.3.3f15");
        item.put("ID", id);
        item.put("Quantity", quantity);
        return item;
    }

    private static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                file.delete();
            }
        }
        dir.delete();
    }
}