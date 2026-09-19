package me.myogoo.extendedterminal.api.host;

import appeng.api.storage.ITerminalHost;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import org.jetbrains.annotations.Nullable;

public interface IUnitedTerminalHost extends ITerminalHost {
    boolean shouldRememberRecipeType();

    void setRememberRecipeType(boolean remember);

    @Nullable
    UnitedTerminalMenu.UnitedRecipeKind getLastRecipeKind();

    void setLastRecipeKind(@Nullable UnitedTerminalMenu.UnitedRecipeKind recipeKind);
}
