package miguel.nu.wayStone.Classes;

import miguel.nu.wayStone.CustomModelData;
import miguel.nu.wayStone.Main;
import miguel.nu.wayStone.utils.BedrockUtil;
import miguel.nu.wayStone.utils.NamespaceKey;
import miguel.nu.wayStone.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

public class Waystone {
    // Java display entities
    private UUID itemDisplay;
    private UUID hitbox;
    private UUID pedestal;

    // Bedrock fallback entities (armor stands)
    private UUID bedrockStatue;
    private UUID bedrockPedestal;
    private UUID bedrockHitbox;

    String name;
    Location statueLocation;
    Material placeholder;

    public void spawn() {
        // ----------------------- STATUE (JAVA ItemDisplay) ----------------------
        ItemDisplay itemDisplay = (ItemDisplay) statueLocation.getWorld().spawnEntity(
                statueLocation,
                EntityType.ITEM_DISPLAY
        );

        List<Double> statueOffset = Main.config.getDoubleList("options.statue.offset");
        List<Double> statueSize   = Main.config.getDoubleList("options.statue.size");

        Transformation statueTransformation = new Transformation(
                new Vector3f(
                        statueOffset.get(0).floatValue(),
                        statueOffset.get(1).floatValue(),
                        statueOffset.get(2).floatValue()
                ),
                new Quaternionf(0, 0, 0, 1),
                new Vector3f(
                        statueSize.get(0).floatValue(),
                        statueSize.get(1).floatValue(),
                        statueSize.get(2).floatValue()
                ),
                new Quaternionf(0, 0, 0, 1)
        );

        itemDisplay.setTransformation(statueTransformation);
        itemDisplay.setItemStack(CustomModelData.addCustomModelData(
                "horsewaystone",
                new ItemStack(Material.DIAMOND_BLOCK)
        ));
        this.itemDisplay = itemDisplay.getUniqueId();

        // ----------------------- HITBOX (JAVA Interaction) ----------------------
        List<Double> interactionOffset = Main.config.getDoubleList("options.hitbox.offset");
        Location interactionLocation = statueLocation.clone().add(
                interactionOffset.get(0),
                interactionOffset.get(1),
                interactionOffset.get(2)
        );

        Interaction interaction = (Interaction) statueLocation.getWorld().spawnEntity(
                interactionLocation,
                EntityType.INTERACTION
        );

        List<Double> interactionSize = Main.config.getDoubleList("options.hitbox.size");
        interaction.setInteractionWidth(interactionSize.get(0).floatValue());
        interaction.setInteractionHeight(interactionSize.get(1).floatValue());

        interaction.getPersistentDataContainer().set(
                NamespaceKey.getNamespacedKey("IS_WAYSTONE"),
                PersistentDataType.BOOLEAN,
                true
        );
        this.hitbox = interaction.getUniqueId();

        // ----------------------- PEDESTAL (JAVA ItemDisplay) ----------------------
        ItemDisplay pedestal = (ItemDisplay) statueLocation.getWorld().spawnEntity(
                statueLocation,
                EntityType.ITEM_DISPLAY
        );

        List<Double> pedestalOffset = Main.config.getDoubleList("options.pedestal.offset");
        List<Double> pedestalSize   = Main.config.getDoubleList("options.pedestal.size");

        Transformation pedestalTransformation = new Transformation(
                new Vector3f(
                        pedestalOffset.get(0).floatValue(),
                        pedestalOffset.get(1).floatValue(),
                        pedestalOffset.get(2).floatValue()
                ),
                new Quaternionf(0, 0, 0, 1),
                new Vector3f(
                        pedestalSize.get(0).floatValue(),
                        pedestalSize.get(1).floatValue(),
                        pedestalSize.get(2).floatValue()
                ),
                new Quaternionf(0, 0, 0, 1)
        );

        pedestal.setTransformation(pedestalTransformation);
        pedestal.setItemStack(CustomModelData.addCustomModelData(
                "pedestalwaystone",
                new ItemStack(Material.DIAMOND_BLOCK)
        ));
        this.pedestal = pedestal.getUniqueId();

// ========================================================================
// ======================= BEDROCK FALLBACK ENTITIES ======================
// ========================================================================

// ----------------------- STATUE (Bedrock ArmorStand) ----------------------
        ArmorStand bedrockStatue = (ArmorStand) statueLocation.getWorld().spawnEntity(
                statueLocation.clone().add(
                        statueOffset.get(0),
                        statueOffset.get(1) - 1.1,
                        statueOffset.get(2)
                ),
                EntityType.ARMOR_STAND
        );
        bedrockStatue.setInvisible(true);
        bedrockStatue.setMarker(true);
        bedrockStatue.setGravity(false);
        bedrockStatue.setSmall(false);
        bedrockStatue.setArms(false);

        bedrockStatue.getEquipment().setHelmet(new ItemStack(Material.COPPER_BOOTS));

        this.bedrockStatue = bedrockStatue.getUniqueId();

// ----------------------- PEDESTAL (Bedrock ArmorStand) ----------------------
        ArmorStand bedrockPedestal = (ArmorStand) statueLocation.getWorld().spawnEntity(
                statueLocation.clone().add(
                        pedestalOffset.get(0),
                        pedestalOffset.get(1) - 0.5,
                        pedestalOffset.get(2)
                ),
                EntityType.ARMOR_STAND
        );
        bedrockPedestal.setInvisible(true);
        bedrockPedestal.setMarker(true);
        bedrockPedestal.setGravity(false);
        bedrockPedestal.setSmall(false);
        bedrockPedestal.setArms(false);

// Same here: just STRUCTURE_BLOCK
        bedrockPedestal.getEquipment().setHelmet(new ItemStack(Material.COPPER_HELMET));

        this.bedrockPedestal = bedrockPedestal.getUniqueId();

// ----------------------- HITBOX (Bedrock ArmorStand) ----------------------
        ArmorStand bedrockHitbox = (ArmorStand) statueLocation.getWorld().spawnEntity(
                interactionLocation,
                EntityType.ARMOR_STAND
        );
        bedrockHitbox.setInvisible(true);
        bedrockHitbox.setMarker(false);
        bedrockHitbox.setGravity(false);
        bedrockHitbox.setSmall(true);
        bedrockHitbox.setCustomNameVisible(false);
        bedrockHitbox.setCollidable(true);

        bedrockHitbox.getPersistentDataContainer().set(
                NamespaceKey.getNamespacedKey("IS_WAYSTONE"),
                PersistentDataType.BOOLEAN,
                true
        );
        this.bedrockHitbox = bedrockHitbox.getUniqueId();

        Utils.tagWaystoneEntity(itemDisplay, "WAYSTONE_ENTITY");
        Utils.tagWaystoneEntity(interaction, "WAYSTONE_ENTITY");
        Utils.tagWaystoneEntity(pedestal, "WAYSTONE_ENTITY");
        Utils.tagWaystoneEntity(bedrockHitbox, "WAYSTONE_ENTITY");
        Utils.tagWaystoneEntity(bedrockPedestal, "WAYSTONE_ENTITY");
        Utils.tagWaystoneEntity(bedrockStatue, "WAYSTONE_ENTITY");

        // ----------------------- PER-PLAYER VISIBILITY ----------------------
        updateVisibilityForAllPlayers();
    }


    public void despawn() {
        // Java entities
        if (itemDisplay != null) {
            Entity itemDisplayEntity = Bukkit.getEntity(itemDisplay);
            if (itemDisplayEntity != null) {
                itemDisplayEntity.remove();
            }
        }

        if (hitbox != null) {
            Entity hitboxEntity = Bukkit.getEntity(hitbox);
            if (hitboxEntity != null) {
                hitboxEntity.remove();
            }
        }

        if (pedestal != null) {
            Entity pedestalEntity = Bukkit.getEntity(pedestal);
            if (pedestalEntity != null) {
                pedestalEntity.remove();
            }
        }

        // Bedrock entities
        if (bedrockStatue != null) {
            Entity bedrockStatueEntity = Bukkit.getEntity(bedrockStatue);
            if (bedrockStatueEntity != null) {
                bedrockStatueEntity.remove();
            }
        }

        if (bedrockPedestal != null) {
            Entity bedrockPedestalEntity = Bukkit.getEntity(bedrockPedestal);
            if (bedrockPedestalEntity != null) {
                bedrockPedestalEntity.remove();
            }
        }

        if (bedrockHitbox != null) {
            Entity bedrockHitboxEntity = Bukkit.getEntity(bedrockHitbox);
            if (bedrockHitboxEntity != null) {
                bedrockHitboxEntity.remove();
            }
        }
    }

    public void updateVisibilityForAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateVisibilityForPlayer(player);
        }
    }

    public void updateVisibilityForPlayer(Player player) {
        boolean isBedrock = BedrockUtil.isBedrockPlayer(player.getUniqueId());

        Entity javaStatue = itemDisplay != null ? Bukkit.getEntity(itemDisplay) : null;
        Entity javaPedestal = pedestal != null ? Bukkit.getEntity(pedestal) : null;
        Entity javaHitbox = hitbox != null ? Bukkit.getEntity(hitbox) : null;

        Entity bedrockStatueEntity = bedrockStatue != null ? Bukkit.getEntity(bedrockStatue) : null;
        Entity bedrockPedestalEntity = bedrockPedestal != null ? Bukkit.getEntity(bedrockPedestal) : null;
        Entity bedrockHitboxEntity = bedrockHitbox != null ? Bukkit.getEntity(bedrockHitbox) : null;

        if (isBedrock) {
            Main.plugin.getLogger().severe("BEDROCK PLAYER");
            // Bedrock: show armor stands, hide display entities
            if (javaStatue != null) player.hideEntity(Main.plugin, javaStatue);
            if (javaPedestal != null) player.hideEntity(Main.plugin, javaPedestal);
            //if (javaHitbox != null) player.hideEntity(Main.plugin, javaHitbox);

            if (bedrockStatueEntity != null) player.showEntity(Main.plugin, bedrockStatueEntity);
            if (bedrockPedestalEntity != null) player.showEntity(Main.plugin, bedrockPedestalEntity);
            if (bedrockHitboxEntity != null) player.showEntity(Main.plugin, bedrockHitboxEntity);
        } else {
            // Java: show display entities, hide armor stands
            if (javaStatue != null) player.showEntity(Main.plugin, javaStatue);
            if (javaPedestal != null) player.showEntity(Main.plugin, javaPedestal);
            if (javaHitbox != null) player.showEntity(Main.plugin, javaHitbox);

            if (bedrockStatueEntity != null) player.hideEntity(Main.plugin, bedrockStatueEntity);
            if (bedrockPedestalEntity != null) player.hideEntity(Main.plugin, bedrockPedestalEntity);
            if (bedrockHitboxEntity != null) player.hideEntity(Main.plugin, bedrockHitboxEntity);

        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getStatueLocation() {
        return statueLocation;
    }

    public void setStatueLocation(Location statueLocation) {
        this.statueLocation = statueLocation;
    }

    public Material getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(Material placeholder) {
        this.placeholder = placeholder;
    }

    public UUID getItemDisplay() {
        return itemDisplay;
    }

    public void setItemDisplay(UUID itemDisplay) {
        this.itemDisplay = itemDisplay;
    }

    public UUID getHitbox() {
        return hitbox;
    }

    public void setHitbox(UUID hitbox) {
        this.hitbox = hitbox;
    }

    public UUID getPedestal() {
        return pedestal;
    }

    public void setPedestal(UUID pedestal) {
        this.pedestal = pedestal;
    }
}
