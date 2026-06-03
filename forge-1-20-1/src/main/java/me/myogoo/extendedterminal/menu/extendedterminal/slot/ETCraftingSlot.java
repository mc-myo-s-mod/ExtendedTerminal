package me.myogoo.extendedterminal.menu.extendedterminal.slot;

import appeng.api.inventories.InternalInventory;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.MEStorage;
import appeng.helpers.IMenuCraftingPacket;
import appeng.menu.slot.CraftingTermSlot;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import me.myogoo.extendedterminal.api.annotation.Polymorph;

public class ETCraftingSlot extends CraftingTermSlot {
    public ETCraftingSlot(Player player, IActionSource mySrc, IEnergySource energySrc, MEStorage storage,
                          InternalInventory cMatrix, InternalInventory secondMatrix, IMenuCraftingPacket ccp) {
        super(player, mySrc, energySrc, storage, cMatrix, secondMatrix, ccp);
    }

    @Override
    protected Recipe<CraftingContainer> findRecipe(CraftingContainer ic, Level level) {
        if (MyotusAPI.integrations().isLoaded(Polymorph.class)
                && getMenu() instanceof ETTerminalMenu menu) {
            var recipe = menu.getCurrentRecipe();
            if (recipe != null && recipe.matches(ic, level)) {
                return recipe;
            }
        }

        return super.findRecipe(ic, level);
    }

    @Override
    protected NonNullList<ItemStack> getRemainingItems(CraftingContainer ic, Level level) {
        if (MyotusAPI.integrations().isLoaded(Polymorph.class)
                && getMenu() instanceof ETTerminalMenu menu) {
            var recipe = menu.getCurrentRecipe();
            if (recipe != null && recipe.matches(ic, level)) {
                return recipe.getRemainingItems(ic);
            }
        }

        return super.getRemainingItems(ic, level);
    }
}
