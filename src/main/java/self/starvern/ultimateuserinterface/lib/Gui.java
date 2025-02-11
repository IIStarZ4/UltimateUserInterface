package self.starvern.ultimateuserinterface.lib;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import self.starvern.ultimateuserinterface.UUI;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.logging.Logger;

/**
 * Represents a list of GuiPages.
 * @since 0.1.0
 */
public class Gui
{
    private final UUI api;
    private final File file;
    private final FileConfiguration config;
    private final String id;
    private final String title;
    private final String permission;
    private final List<String> patterns;
    private final List<GuiPage> pages;
    private final Set<GuiSession> sessions;
    private final Logger logger;
    private final boolean registerAlias;

    public Gui(UUI api, File file)
    {
        this.api = api;
        this.file = file;
        this.config = YamlConfiguration.loadConfiguration(file);
        this.id = file.getName().replace(".yml", "");
        this.title = this.config.getString("title", "Gui");
        this.permission = this.config.getString("permission");
        this.patterns = this.config.getStringList("patterns");
        this.pages = new ArrayList<>();
        this.sessions = new HashSet<>();
        this.logger = Logger.getLogger("UUI::" + this.id + ".yml");
        this.registerAlias = this.config.getBoolean("register_alias", false);
    }

    /**
     * @return True, to register the GUI's alias.
     * @since 0.6.0
     */
    public boolean registerAlias()
    {
        return this.registerAlias;
    }

    /**
     * @return The logger for this GUI (includes GUI ID for better info).
     * @since 0.5.0
     */
    public Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @return Instance of UUI api.
     * @since 0.5.0
     */
    public UUI getApi()
    {
        return this.api;
    }

    @Nullable
    public String getPermission()
    {
        return this.permission;
    }

    /**
     * @return A clean duplicated version of this Gui.
     * @since 0.2.3
     */
    public Gui duplicate()
    {
        return new Gui(this.api, this.file).loadPages();
    }

    /**
     * Builds List<GuiPage> from the configuration.
     * @return Instance of Gui
     * @since 0.1.0
     */
    public Gui loadPages()
    {
        this.pages.clear();
        for (String patternName : patterns)
        {
            List<String> pattern = this.config.getStringList(patternName);
            GuiPage page = new GuiPage(this.api, this, pattern);
            this.pages.add(page);
        }
        return this;
    }

    /**
     * Generates the required pages to fix the amount provided.
     * @since 0.4.0
     */
    public void ensureSize(GuiPage page, String character, int size)
    {
        int itemsPerPage = page.getSlottedItems(character).size();

        if (itemsPerPage == 0 || itemsPerPage >= size || this.getAllSlottedItems(character).size() >= size)
            return;

        int pageIndex = this.pages.indexOf(page)+1;

        for (int i = itemsPerPage; i < size; i+=itemsPerPage)
            this.pages.add(pageIndex, page.duplicate());
    }

    /**
     * @return All GuiItems from every GuiPage.
     * @since 0.5.0
     */
    public List<GuiItem> getAllItems()
    {
        List<GuiItem> items = new ArrayList<>();

        for (GuiPage page : this.pages)
            items.addAll(page.getItems());

        return items;
    }

    /**
     * @return All GuiItems from every GuiPage with the given id.
     * @since 0.5.0
     */
    public List<GuiItem> getAllItems(String character)
    {
        List<GuiItem> items = new ArrayList<>();

        for (GuiPage page : this.pages)
            page.getItem(character).ifPresent(items::add);

        return items;
    }

    /**
     * @return All SlottedGuiItems from every GuiPage.
     * @since 0.5.0
     */
    public List<SlottedGuiItem> getAllSlottedItems()
    {
        List<SlottedGuiItem> items = new ArrayList<>();

        for (GuiPage page : this.pages)
            items.addAll(page.getSlottedItems());

        return items;
    }

    /**
     * @return All SlottedGuiItems from every GuiPage with the given id.
     * @since 0.5.0
     */
    public List<SlottedGuiItem> getAllSlottedItems(String character)
    {
        List<SlottedGuiItem> items = new ArrayList<>();

        for (GuiPage page : this.pages)
            items.addAll(page.getSlottedItems(character));

        return items;
    }

    /**
     * @return The file this crate is found.
     * @since 0.1.0
     */
    public File getFile()
    {
        return this.file;
    }

    /**
     * @return The config of the GUI file.
     * @since 0.1.0
     */
    public FileConfiguration getConfig()
    {
        return this.config;
    }

    /**
     * @return The name of the GUI as defined in the filename
     * @since 0.1.0
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * @return The title of the GUI
     * @since 0.1.0
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * @return The title of the GUI
     * @since 0.1.0
     */
    public List<String> getPatterns()
    {
        return this.patterns;
    }

    /**
     * Gets a specific page from the GUI.
     * @param page The index of the page to get (page < 0 = 0, page > pages = pages).
     * @return The GuiPage with the given page number.
     * @since 0.1.0
     */
    public GuiPage getPage(int page)
    {
        if (page < 0)
            return this.pages.get(0);
        if (page >= this.pages.size())
            return this.pages.get(this.pages.size()-1);
        return this.pages.get(page);
    }

    /**
     * @return A list of all the pages this GUI has.
     * @since 0.1.0
     */
    public List<GuiPage> getPages()
    {
        return this.pages;
    }

    /**
     * @param page The page to get the index of.
     * @return The index of the page inside the GUI pages list.
     * @since 0.1.2
     */
    public int indexOf(GuiPage page)
    {
        return this.pages.indexOf(page);
    }

    /**
     * @return All active sessions from this GUI.
     * @since 0.4.2
     */
    public Set<GuiSession> getSessions()
    {
        return this.sessions;
    }

    /**
     * Adds a session
     * @param session The session to add
     * @since 0.4.2
     */
    public void addSession(GuiSession session)
    {
        this.sessions.add(session);
    }

    /**
     * Removes a session
     * @param session The session to remove
     * @since 0.4.2
     */
    public void removeSession(GuiSession session)
    {
        this.sessions.remove(session);
    }

    /**
     * Open the GUI page for an entity.
     * @param entity The entity to open the GUI page for.
     * @since 0.1.7
     */
    public void open(HumanEntity entity)
    {
        this.getPage(0).open(entity);
    }
}

