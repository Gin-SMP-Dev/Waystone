package miguel.nu.wayStone.Menu;

import miguel.nu.wayStone.Classes.Waystone;
import miguel.nu.wayStone.Main;
import miguel.nu.wayStone.WaystoneManager;
import miguel.nu.wayStone.utils.NamespaceKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class WaystoneMenu {
    public static void open(Player player){
        MenuHolder holder = new MenuHolder("WAYSTONE_MENU", Main.config.getInt("inventory.size"), Component.text(Main.config.getString("inventory.title")));
        Inventory inventory = holder.getInventory();

        MenuPrefab.drawBorder(inventory);

        Waystone[] waystones = WaystoneManager.getAllWaystone();

        int size = inventory.getSize();
        if (size % 9 != 0) return;

        List<Integer> fillSlots = new ArrayList<>();
        int rows = size / 9;
        for (int slot = 0; slot < size; slot++) {
            int col = slot % 9;
            int row = slot / 9;
            if (row == 0 || row == rows - 1 || col == 0 || col == 8) continue;
            fillSlots.add(slot);
        }

        int count = Math.min(fillSlots.size(), waystones.length);
        if (fillSlots.isEmpty()) return;

        for(int i = 0; i < count; i++){
            inventory.setItem(fillSlots.get(i), drawWaystone(waystones[i]));
        }

        player.openInventory(inventory);
    }

    static ItemStack drawWaystone(Waystone waystone){
        ItemStack item = new ItemStack(waystone.getPlaceholder());
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text(waystone.getName())
                .color(NamedTextColor.AQUA)
                .decorate(TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(
                Component.text("Click to travel here!")
                        .color(NamedTextColor.YELLOW)
                        .decoration(TextDecoration.ITALIC, false)
        ));

        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(NamespaceKey.getNamespacedKey("WAYSTONE_NAME"), PersistentDataType.STRING, waystone.getName());
        item.setItemMeta(meta);
        return item;
    }
}
