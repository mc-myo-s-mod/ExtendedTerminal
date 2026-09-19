package me.myogoo.extendedterminal.api.host;

import appeng.api.storage.ITerminalHost;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import me.myogoo.myotus.api.experience.ExperienceMath;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public interface IETTerminalHost extends ITerminalHost {
    ETTerminalMode getMode();
    void setMode(ETTerminalMode mode);

    @Nullable Identifier getStoneCutterRecipeId();
    void setStoneCutterRecipeId(@Nullable Identifier stonecuttingRecipeId);

    @Nullable ExperienceMath.ExperienceSource getRememberedAnvilExperienceSource();
    void setRememberedAnvilExperienceSource(@Nullable ExperienceMath.ExperienceSource source);

}
