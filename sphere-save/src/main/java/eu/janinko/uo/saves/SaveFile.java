package eu.janinko.uo.saves;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Data;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
@Data
public class SaveFile {

    private String title;
    private String version;
    private long time;
    private int savecount;

    private Set<Sector> sectors = new HashSet<>();
    private Map<Long, Item> items = new HashMap();
    private Map<Long, Character> characters = new HashMap<>();
    private Map<String, String> globals = new HashMap<>();

    public void put(Item i) {
        items.put(i.getSerial(), i);
    }

    public void put(Character i) {
        characters.put(i.getSerial(), i);
    }

    public void put(Sector i) {
        sectors.add(i);
    }
}
