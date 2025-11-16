package miguel.nu.wayStone;

import miguel.nu.wayStone.Command.CommandListener;
import miguel.nu.wayStone.Menu.GuiListener;
import miguel.nu.wayStone.utils.NamespaceKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public static FileConfiguration config;
    public static Plugin plugin;
    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();
        config = getConfig();

        NamespaceKey.createNamespaceKeys();
        getServer().getPluginManager().registerEvents(new GuiListener(), this);


        new CommandListener(this);
        WaystoneManager.init();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
