package miguel.nu.wayStone.utils;

import miguel.nu.wayStone.Classes.Waystone;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderSetter {
    public static String setPlaceholder(String input, Player player, Waystone waystone){
        Map<String, String> replacements = new HashMap<>();

        if(player != null){
            replacements.put("%name%", player.getName());
        }
        if(waystone != null){
            replacements.put("%waystone_name%", waystone.getName());
            replacements.put("%world%", waystone.getStatueLocation().getWorld().getName());
        }

        String result = input;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
