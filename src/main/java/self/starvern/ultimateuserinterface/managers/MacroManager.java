package self.starvern.ultimateuserinterface.managers;

import org.bukkit.plugin.Plugin;
import self.starvern.ultimateuserinterface.macros.Macro;

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MacroManager
{
    private final Set<Macro> macros = new HashSet<>();

    public void register(Macro macro)
    {
        this.macros.add(macro);
    }

    public Optional<Macro> getMacro(String action)
    {
        return this.macros.stream()
                .filter(macro -> action.toLowerCase(Locale.ROOT)
                        .startsWith(macro.toString().toLowerCase(Locale.ROOT)))
                .findFirst();
    }

    public Set<Macro> getMacros(Plugin plugin)
    {
        return this.macros.stream()
                .filter(macro -> macro.getPlugin().equals(plugin))
                .collect(Collectors.toSet());
    }

    public Set<Macro> getMacros() {
        return macros;
    }

    public void removeMacro(Macro macro)
    {
        this.macros.remove(macro);
    }
}
