package self.starvern.ultimateuserinterface.lib;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import self.starvern.ultimateuserinterface.UUI;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuiPage
{
    private final Gui gui;
    private final List<String> pattern;
    private final List<GuiItem> items;
    private final Inventory inventory;

    public GuiPage(Gui gui, List<String> pattern)
    {
        this.gui = gui;
        this.pattern = pattern;
        this.items = new ArrayList<>();
        this.inventory = Bukkit.createInventory(null, 9 * this.pattern.size(), this.gui.getTitle());
    }

    /**
     * @return The GUI this page is inside.
     * @since 0.1.0
     */
    public Gui getGui()
    {
        return this.gui;
    }

    /**
     * Creates a clean duplicate of the page.
     * @return The new instance of GuiPage.
     * @since 0.1.5
     */
    public GuiPage duplicate()
    {
        return new GuiPage(this.gui, this.pattern).loadItems();
    }

    /**
     * Constructs a list of GuiItems based on the pattern
     * @since 0.1.0
     */
    public GuiPage loadItems()
    {
        this.items.clear();
        int slot = 0;
        for (String line : this.pattern)
        {
            for (char character : line.toCharArray())
            {
                String letter = String.valueOf(character);
                GuiItem item = getConfigItem(letter, slot++);
                this.items.add(item);
            }
        }
        return this;
    }

    /**
     * @return The generated item from the config with the following letter.
     * @since 0.1.7
     */
    private GuiItem getConfigItem(String letter, int slot)
    {
        ConfigurationSection baseSection = this.gui.getConfig().getConfigurationSection("");

        for (String character : baseSection.getKeys(false))
        {
            if (!character.equalsIgnoreCase(letter)) return new GuiItem(this, letter, slot);
            return new GuiItem(this, letter, slot);
        }
        return new GuiItem(this, slot);
    }

    /**
     * @return The inventory constructed from the pattern.
     * @since 0.1.0
     */
    public Inventory getInventory()
    {
        for (GuiItem item : this.items)
        {
            this.inventory.setItem(item.getSlot(), item.getItem().build());
        }

        return this.inventory;
    }

    /**
     * @return A list of all items in this page.
     * @since 0.1.6
     */
    public List<GuiItem> getItems()
    {
        return this.items;
    }

    /**
     * @param item The item to compare to the GuiItem.
     * @return The GuiItem, or if the item doesn't match anything, null.
     * @since 0.1.0
     */
    @Nullable
    public GuiItem getItem(ItemStack item)
    {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null)
            return null;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (!container.has(new NamespacedKey(UUI.getSingleton(), "uui-item-id"), PersistentDataType.STRING))
            return null;

        String rawUUID = container.get(new NamespacedKey(UUI.getSingleton(), "uui-item-id"), PersistentDataType.STRING);
        UUID uuid = UUID.fromString(rawUUID);

        for (GuiItem guiItem : this.items)
        {
            if (guiItem.getUniqueId().equals(uuid))
                return guiItem;
        }
        return null;
    }

    /**
     * @param id The character assigned to the items.
     * @return All the items with the assigned to the designated character.
     * @since 0.1.7
     */
    public List<GuiItem> getItems(String id)
    {
        List<GuiItem> items = new ArrayList<>();

        for (GuiItem item : this.items)
        {
            if (item.getId().equalsIgnoreCase(id))
                items.add(item);
        }

        return items;
    }

    /**
     * @return True if the page is the first of the GUI.
     * @since 0.1.7
     */
    public boolean isFirst()
    {
        return this.gui.indexOf(this) == 0;
    }

    /**
     * @return True if the page is the last of the GUI.
     * @since 0.1.7
     */
    public boolean isLast()
    {
        return this.gui.indexOf(this) == this.gui.getPages().size()-1;
    }

    /**
     * @return The next page of the GUI, or the first page if it's last.
     * @since 0.1.7
     */
    public GuiPage next()
    {
        try
        {
            return this.gui.getPage(this.gui.indexOf(this)+1);
        }
        catch (IndexOutOfBoundsException exception)
        {
            return this.gui.getPage(0);
        }
    }

    /**
     * @return The previous page of the GUI, or the last page if it's first.
     * @since 0.1.7
     */
    public GuiPage last()
    {
        try
        {
            return this.gui.getPage(this.gui.indexOf(this)-1);
        }
        catch (IndexOutOfBoundsException exception)
        {
            return this.gui.getPage(this.gui.getPages().size()-1);
        }
    }

    /**
     * Open the GUI page for an entity.
     * @param entity The entity to open the GUI page for.
     * @since 0.1.7
     */
    public void open(HumanEntity entity)
    {
        entity.openInventory(this.getInventory());
    }

    /**
     * Open the GUI page for a player.
     * @param player The player to open the GUI page for.
     * @since 0.1.7
     */
    public void open(Player player)
    {
        player.openInventory(this.getInventory());
    }
}
