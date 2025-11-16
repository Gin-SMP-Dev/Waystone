package miguel.nu.wayStone.Classes;

import miguel.nu.wayStone.Main;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Waystone {
    String name;
    Location statueLocation;
    Material placeholder;

    public void spawn(){
        Bukkit.broadcast(Component.text("New waystone has been spawned"));
    }
    public void despawn(){
        Bukkit.broadcast(Component.text("A waystone has been despawned"));
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


}
