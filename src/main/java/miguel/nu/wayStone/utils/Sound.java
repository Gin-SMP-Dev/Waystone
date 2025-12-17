package miguel.nu.wayStone.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class Sound {
    public static void playSound(Player player, String key) {
        if (key == null || key.isEmpty()) return;
        NamespacedKey ns = NamespacedKey.fromString(key);
        if (ns != null) {
            player.playSound(player.getLocation(), String.valueOf(ns), SoundCategory.MASTER, 1f, 1f);
        }
    }
}
