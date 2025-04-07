import util.TerminalUtil;
import java.util.Scanner;

public class Main {
    private static final String VERSION = "1.0";
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TerminalUtil.clearScreen();
        System.out.println("Schedule 1 Save Editor v" + VERSION);
        System.out.println("Created by ACoolerName\n");

        SaveManager.selectSaveSlot(scanner);
        scanner.close();
    }
}