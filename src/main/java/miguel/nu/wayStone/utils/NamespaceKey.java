package miguel.nu.wayStone.utils;

import miguel.nu.wayStone.Main;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NamespaceKey {
    private static List<NamespacedKey> namespaces = new ArrayList<>();

    @NotNull
    public static NamespacedKey getNamespacedKey(String key){
        for (NamespacedKey namespace : namespaces) {
            if (namespace.getKey().equals(key.toLowerCase())) return namespace;
        }
        throw new RuntimeException("Couldnt not find the correct namespace key for: " + key);
    }

    public static void createNamespaceKeys(){
        namespaces.add(new NamespacedKey(Main.plugin, "WAYSTONE_NAME"));
    }
}
