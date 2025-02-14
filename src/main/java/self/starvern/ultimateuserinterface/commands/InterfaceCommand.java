package self.starvern.ultimateuserinterface.commands;

import com.google.common.collect.MultimapBuilder;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import self.starvern.ultimateuserinterface.UUIPlugin;
import self.starvern.ultimateuserinterface.hooks.PlaceholderAPIHook;
import self.starvern.ultimateuserinterface.lib.Gui;
import self.starvern.ultimateuserinterface.lib.GuiArgument;
import self.starvern.ultimateuserinterface.lib.GuiPage;
import self.starvern.ultimateuserinterface.macros.Macro;
import self.starvern.ultimateuserinterface.properties.GuiProperty;

import java.util.Optional;

public class InterfaceCommand implements CommandExecutor
{
    private final UUIPlugin plugin;

    public InterfaceCommand(UUIPlugin plugin)
    {
        this.plugin = plugin;
        PluginCommand command = plugin.getCommand("interface");
        if (command == null)
        {
            plugin.getLogger().severe("Invalid plugin.yml. Please re-install the plugin.");
            return;
        }
        command.setTabCompleter(new InterfaceCommandCompleter(this.plugin));
        command.setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args)
    {
        if (!sender.hasPermission("uui.command.interface"))
        {
            sender.sendMessage("Insufficient permission.");
            return false;
        }

        if (args.length == 0)
        {
            sender.sendMessage("You need to specify a gui name");
            return false;
        }

        if (args[0].equalsIgnoreCase("reload"))
        {
            if (!sender.hasPermission("uui.command.interface.reload"))
            {
                sender.sendMessage("Insufficient permission.");
                return false;
            }

            sender.sendMessage("Reloaded UltimateUserInterface.");
            plugin.load();
            return true;
        }

        if (args[0].equalsIgnoreCase("list"))
        {
            for (Macro macro : this.plugin.getApi().getMacroManager().getMacros())
            {
                sender.sendMessage(macro.toString());
            }
        }

        if (!(sender instanceof Player player))
        {
            sender.sendMessage("You need to be a player to open a gui.");
            return false;
        }

        Optional<Gui> guiOptional = plugin.getApi().getGuiManager().getGui(args[0]);
        if (guiOptional.isEmpty())
        {
            player.sendMessage("Gui not found");
            return false;
        }

        if (guiOptional.get().getPermission() != null && !player.hasPermission(guiOptional.get().getPermission()))
        {
            player.sendMessage("No permission.");
            return false;
        }

        GuiPage page = guiOptional.get().getPage(0);
        page.loadArguments();

        for (int i = 0; i < page.getArguments().size(); i++)
        {
            GuiArgument argument = page.getArguments().get(i);

            String value = argument.getDefaultValue();

            if (args.length <= i+1 && argument.isRequired())
            {
                player.sendMessage("Specify argument: " + argument.getId());
                return false;
            }

            if (args.length > i+1)
                value = args[i+1];

            argument.setValue(PlaceholderAPIHook.parse(player, page.getProperties().parsePropertyPlaceholders(value)));

            try
            {
                GuiProperty<?> property = argument.asProperty();
                page.getProperties().setProperty(property, true);
            }
            catch (NumberFormatException exception)
            {
                player.sendMessage("Invalid argument: " + argument.getId() + ", required type: " + argument.getType());
                return false;
            }
        }


        page.open(player);
        return true;
    }
}
