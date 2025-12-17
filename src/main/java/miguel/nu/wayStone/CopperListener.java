package miguel.nu.wayStone;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class CopperListener implements Listener {

    private boolean isCopperArmor(ItemStack item) {
        if (item == null) return false;
        Material type = item.getType();
        return type == Material.COPPER_HELMET || type == Material.COPPER_BOOTS;
    }

    @EventHandler
    public void onBlockDispenseArmor(BlockDispenseArmorEvent event) {
        if (!isCopperArmor(event.getItem())) {
            return;
        }

        if (event.getTargetEntity() instanceof ArmorStand) {
            event.setCancelled(true);
        }
    }

    // 1) Prevent putting copper helmet/boots on armor stands
    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        ArmorStand armorStand = event.getRightClicked();
        Player player = event.getPlayer();
        ItemStack playerItem = event.getPlayerItem();
        ItemStack armorStandItem = event.getArmorStandItem();
        EquipmentSlot slot = event.getSlot();

        // If the player is trying to put copper armor on the armor stand
        if (isCopperArmor(playerItem)) {
            // Only care about helmet/boots slots (just in case)
            if (slot == EquipmentSlot.HEAD || slot == EquipmentSlot.FEET) {
                event.setCancelled(true);
                return;
            }
        }

        // If the armor stand already has copper armor and player is trying to interact with it
        if (isCopperArmor(armorStandItem)) {
            // Optional: allow taking it off or also block that
            // Here we allow taking it off, but not putting it on.
            // If you want to prevent interaction with copper entirely, uncomment below:
            // event.setCancelled(true);
        }
    }

    // 2) Prevent mobs from picking up copper helmet/boots
    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();

        // Only care about non-player mobs
        if (entity instanceof Player) {
            return;
        }

        ItemStack item = event.getItem().getItemStack();
        if (isCopperArmor(item)) {
            event.setCancelled(true);
        }
    }

    // 3) Prevent mobs from spawning with copper helmet/boots
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        EntityEquipment equipment = entity.getEquipment();

        if (equipment == null) return;

        ItemStack helmet = equipment.getHelmet();
        ItemStack boots = equipment.getBoots();

        if (isCopperArmor(helmet)) {
            equipment.setHelmet(null);
        }
        if (isCopperArmor(boots)) {
            equipment.setBoots(null);
        }
    }
}
