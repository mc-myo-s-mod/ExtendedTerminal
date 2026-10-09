package me.myogoo.extendedterminal.menu.extendedterminal.slot;

import appeng.api.inventories.InternalInventory;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.MEStorage;
import appeng.helpers.ICraftingGridMenu;
import appeng.menu.slot.CraftingTermSlot;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ETCraftingSlot extends CraftingTermSlot {
    public ETCraftingSlot(Player player, IActionSource mySrc, IEnergySource energySrc, MEStorage storage, InternalInventory cMatrix, InternalInventory secondMatrix, ICraftingGridMenu ccp) {
        super(player, mySrc, energySrc, storage, cMatrix, secondMatrix, ccp);
    }

    @Override
    protected RecipeHolder<CraftingRecipe> findRecipe(CraftingInput input, ServerLevel level) {
        if (getMenu() instanceof ETTerminalMenu menu && !menu.canCraftInMode(ETTerminalMode.CRAFTING)) {
            return null;
        }
        return super.findRecipe(input, level);
    }
}
