package me.myogoo.extendedterminal.api.host;

import appeng.api.storage.ITerminalHost;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface IETTerminalHost extends ITerminalHost {
    ETTerminalMode getMode();

    void setMode(ETTerminalMode mode);

    @Nullable
    ResourceLocation getStoneCutterRecipeId();

    void setStoneCutterRecipeId(@Nullable ResourceLocation stonecuttingRecipeId);
}
