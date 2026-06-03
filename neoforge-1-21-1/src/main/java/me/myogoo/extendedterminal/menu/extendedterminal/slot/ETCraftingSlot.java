package me.myogoo.extendedterminal.menu.extendedterminal.slot;

import appeng.api.inventories.InternalInventory;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.MEStorage;
import appeng.helpers.ICraftingGridMenu;
import appeng.menu.slot.CraftingTermSlot;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import me.myogoo.extendedterminal.api.annotation.Polymorph;

public class ETCraftingSlot extends CraftingTermSlot {
    public ETCraftingSlot(Player player, IActionSource mySrc, IEnergySource energySrc, MEStorage storage, InternalInventory cMatrix, InternalInventory secondMatrix, ICraftingGridMenu ccp) {
        super(player, mySrc, energySrc, storage, cMatrix, secondMatrix, ccp);
    }

    @Override
    protected NonNullList<ItemStack> getRemainingItems(CraftingInput ic, Level level) {
        if(MyotusAPI.integrations().isLoaded(Polymorph.class) && getMenu() instanceof ETTerminalMenu et) {
            var recipe = et.getCurrentRecipe();
            if(recipe != null && recipe.value().matches(ic, level)) {
                return recipe.value().getRemainingItems(ic);
            }
        }
        return super.getRemainingItems(ic,level);
    }

    @Override
    protected RecipeHolder<CraftingRecipe> findRecipe(CraftingInput ic, Level level) {
        if(MyotusAPI.integrations().isLoaded(Polymorph.class) && getMenu() instanceof ETTerminalMenu et) {
            var recipe = et.getCurrentRecipe();

            if (recipe != null && recipe.value().matches(ic, level)) {
                return et.getCurrentRecipe();
            }
        }
        return super.findRecipe(ic, level);
    }
}
