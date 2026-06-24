package me.myogoo.extendedterminal.me.host;

import appeng.api.inventories.InternalInventory;
import appeng.items.contents.StackDependentSupplier;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.util.inv.SupplierInternalInventory;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.wct.WCTMenuHost;
import me.myogoo.extendedterminal.api.host.IUnitedTerminalHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import static me.myogoo.extendedterminal.init.ETDataComponent.*;

public class UnitedWTHost extends WCTMenuHost implements IUnitedTerminalHost {
    private static final String REMEMBER_RECIPE_KIND = "rememberUnitedRecipeKind";
    private static final String SELECTED_RECIPE_KIND = "selectedUnitedRecipeKind";

    private final SupplierInternalInventory<InternalInventory> craftingGrid;

    public UnitedWTHost(ItemWT item, Player player, ItemMenuHostLocator locator, BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator, returnToMainMenu);
        this.craftingGrid = new SupplierInternalInventory<>(new StackDependentSupplier(this::getItemStack,
                stack -> createInv(player, (ItemStack) stack, CRAFTING_INV,
                        ETMenuType.UNITED_TERMINAL.getGridSize())));
    }

    @Override
    public @Nullable InternalInventory getSubInventory(net.minecraft.resources.ResourceLocation id) {
        if (id.equals(ETMenuType.UNITED_TERMINAL.getCraftingInventory())) {
            return craftingGrid;
        }
        return super.getSubInventory(id);
    }

    public boolean rememberUnitedRecipeKind() {
        var tag = getHostTag();
        return !tag.contains(REMEMBER_RECIPE_KIND, Tag.TAG_BYTE) || tag.getBoolean(REMEMBER_RECIPE_KIND);
    }

    public void setRememberUnitedRecipeKind(boolean remember) {
        var tag = getHostTag();
        tag.putBoolean(REMEMBER_RECIPE_KIND, remember);
        if (!remember) {
            tag.remove(SELECTED_RECIPE_KIND);
        }
        saveHostTag(tag);
    }

    public @Nullable UnitedTerminalMenu.UnitedRecipeKind getRememberedUnitedRecipeKind() {
        var tag = getHostTag();
        if (!tag.contains(SELECTED_RECIPE_KIND, Tag.TAG_STRING)) {
            return null;
        }
        return UnitedTerminalMenu.UnitedRecipeKind.bySerializedName(tag.getString(SELECTED_RECIPE_KIND));
    }

    public void setRememberedUnitedRecipeKind(@Nullable UnitedTerminalMenu.UnitedRecipeKind recipeKind) {
        var tag = getHostTag();
        if (recipeKind == null) {
            tag.remove(SELECTED_RECIPE_KIND);
        } else {
            tag.putString(SELECTED_RECIPE_KIND, recipeKind.serializedName());
        }
        saveHostTag(tag);
    }

    private CompoundTag getHostTag() {
        return this.getItemStack().getOrDefault(ET_TERMINAL_HOST_TAG, new CompoundTag()).copy();
    }

    private void saveHostTag(CompoundTag tag) {
        this.getItemStack().set(ET_TERMINAL_HOST_TAG, tag);
    }
}
