package self.starvern.ultimateuserinterface;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.ServicePriority;
import self.starvern.ultimateuserinterface.managers.CommandManager;
import self.starvern.ultimateuserinterface.managers.GuiManager;
import self.starvern.ultimateuserinterface.managers.ItemInputManager;
import self.starvern.ultimateuserinterface.managers.MacroManager;

import java.util.logging.Logger;

public class UUI
{
    private final UUIPlugin plugin;

    private final GuiManager guiManager;
    private final ItemInputManager itemInputManager;
    private final MacroManager macroManager;
    private final CommandManager commandManager;
    private final Logger logger;

    protected UUI(UUIPlugin plugin)
    {
        this.plugin = plugin;
        this.guiManager = new GuiManager(this);
        this.itemInputManager = new ItemInputManager();
        this.macroManager = new MacroManager();
        this.commandManager = new CommandManager(this);
        this.logger = Logger.getLogger("UUI");

        Bukkit.getServicesManager().register(UUI.class, this, this.plugin, ServicePriority.Normal);
    }

    public GuiManager getGuiManager()
    {
        return guiManager;
    }

    public UUIPlugin getPlugin()
    {
        return plugin;
    }

    public ItemInputManager getItemInputManager()
    {
        return itemInputManager;
    }

    public MacroManager getMacroManager()
    {
        return macroManager;
    }

    public CommandManager getCommandManager()
    {
        return commandManager;
    }

    public NamespacedKey getKey()
    {
        return new NamespacedKey(this.plugin, "uui-uuid");
    }

    public NamespacedKey getItemKey()
    {
        return new NamespacedKey(this.plugin, "uui-item-config");
    }

    public Logger getLogger()
    {
        return this.logger;
    }
}
