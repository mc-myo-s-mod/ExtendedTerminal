package me.myogoo.extendedterminal.api.host;

import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import org.jetbrains.annotations.Nullable;

public interface IUnitedTerminalHost {
    boolean rememberUnitedRecipeKind();

    void setRememberUnitedRecipeKind(boolean remember);

    @Nullable
    UnitedTerminalMenu.UnitedRecipeKind getRememberedUnitedRecipeKind();

    void setRememberedUnitedRecipeKind(@Nullable UnitedTerminalMenu.UnitedRecipeKind recipeKind);
}
