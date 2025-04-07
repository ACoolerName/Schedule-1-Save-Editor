package util;

import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.io.File;


public class JsonUtil {
    public static JSONObject readJson(File file) {
        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            return new JSONObject(content);
        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON file: " + file.getPath());
        }
    }

    public static void writeJson(File file, JSONObject json) {
        try {
            Files.write(file.toPath(), json.toString(4).getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Error writing JSON file: " + file.getPath());
        }
    }
}
