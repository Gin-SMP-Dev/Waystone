package miguel.nu.wayStone.utils;

import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

public class Utils {
    public static void tagWaystoneEntity(Entity entity, String type) {
        entity.getPersistentDataContainer().set(
                NamespaceKey.getNamespacedKey("WAYSTONE_ENTITY"),
                PersistentDataType.STRING,
                type
        );
    }
}
