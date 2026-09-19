package me.myogoo.extendedterminal.part.extendedterminal;

import appeng.api.parts.IPartItem;
import me.myogoo.extendedterminal.api.host.IUnitedTerminalHost;
import me.myogoo.extendedterminal.config.extendedcrafting.ExtendedCraftingConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType;
import me.myogoo.extendedterminal.part.ETTerminalBasePart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class UnitedTerminalPart extends ETTerminalBasePart implements IUnitedTerminalHost {
    private static final String REMEMBER_RECIPE_TYPE = "rememberUnitedRecipeType";
    private static final String SELECTED_RECIPE_TYPE = "selectedUnitedRecipeType";

    private boolean rememberUnitedRecipeType = true;
    @Nullable
    private MyoRecipeType lastRecipeType;

    public UnitedTerminalPart(IPartItem<?> partItem) {
        super(partItem, ETMenuType.UNITED_TERMINAL);
        this.getMainNode().setIdlePowerUsage(ExtendedCraftingConfig.INSTANCE.getUltimateConfig().passiveDrainAE());
    }

    @Override
    public MenuType<?> getMenuType(Player p) {
        return UnitedTerminalMenu.TYPE;
    }

    @Override
    public void readFromNBT(ValueInput input) {
        super.readFromNBT(input);
        this.rememberUnitedRecipeType = input.getBooleanOr(REMEMBER_RECIPE_TYPE, true);
        var selectedRecipeType = input.getStringOr(SELECTED_RECIPE_TYPE, null);
        if (selectedRecipeType != null) {
            this.lastRecipeType = MyoRecipeType.fromSavedName(selectedRecipeType);
        } else {
            this.lastRecipeType = null;
        }
    }

    @Override
    public void writeToNBT(ValueOutput output) {
        super.writeToNBT(output);
        output.putBoolean(REMEMBER_RECIPE_TYPE, this.rememberUnitedRecipeType);
        if (this.lastRecipeType != null) {
            output.putString(SELECTED_RECIPE_TYPE, this.lastRecipeType.name());
        }
    }

    @Override
    public boolean shouldRememberRecipeType() {
        return rememberUnitedRecipeType;
    }

    @Override
    public void setRememberRecipeType(boolean remember) {
        this.rememberUnitedRecipeType = remember;
        if (!remember) {
            this.lastRecipeType = null;
        }
        getHost().markForSave();
    }

    @Override
    public @Nullable MyoRecipeType getLastRecipeType() {
        return lastRecipeType;
    }

    @Override
    public void setLastRecipeType(@Nullable MyoRecipeType recipeType) {
        this.lastRecipeType = recipeType;
        getHost().markForSave();
    }
}
