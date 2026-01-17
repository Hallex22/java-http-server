package fakeDB;

import java.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class CatsDB {

    private List<Map<String, Object>> cats = new ArrayList<>();
    private final Gson gson = new Gson();
    private final String filePath;

    public CatsDB(String filePath) {
        this.filePath = filePath;
        loadFromFile();
    }

    // Citește fișierul JSON la start
    private void loadFromFile() {
        try {
            String json = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            cats = gson.fromJson(json, new TypeToken<List<Map<String,Object>>>(){}.getType());
        } catch (Exception e) {
            cats = new ArrayList<>();
        }
    }

    // Returnează toate pisicile
    public List<Map<String,Object>> getAll() {
        return cats;
    }

    // Returnează o pisică după id
    public Map<String,Object> get(int id) {
        return cats.stream()
                   .filter(c -> ((Double)c.get("id")).intValue() == id)
                   .findFirst()
                   .orElse(null);
    }

    // Creează o pisică nouă și dă auto-increment la id
    public Map<String,Object> create(Map<String,Object> cat) {
        int maxId = cats.stream().mapToInt(c -> ((Double)c.get("id")).intValue()).max().orElse(0);
        cat.put("id", maxId + 1);
        cats.add(cat);
        return cat;
    }

    // Actualizează o pisică după id
    public Map<String,Object> update(int id, Map<String,Object> data) {
        Map<String,Object> cat = get(id);
        if(cat != null) {
            // păstrează id-ul, suprascrie restul
            data.remove("id");
            cat.putAll(data);
        }
        return cat;
    }

    // Șterge o pisică după id
    public boolean delete(int id) {
        return cats.removeIf(c -> ((Double)c.get("id")).intValue() == id);
    }

}

