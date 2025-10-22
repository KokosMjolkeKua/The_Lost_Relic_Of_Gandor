import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Room {
    protected String description;
    protected final Map<String, Room> exits = new HashMap<>();
    protected final List<Item> items = new ArrayList<>();

    public Room(String description) { this.description = description; }

    public String getDescription() {
        StringBuilder sb = new StringBuilder(description);
        if (!items.isEmpty()) {
            sb.append("\nItems here: ");
            for (Item i : items) sb.append(i.getName()).append(", ");
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }

    public void addItem(Item it) { items.add(it); }
    public List<Item> getItems() { return items; }
    public void setExit(String dir, Room room) { exits.put(dir, room); }
    public Room getExit(String dir) { return exits.get(dir); }
}
