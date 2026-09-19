package me.myogoo.extendedterminal.menu.extendedterminal.slot;

import appeng.api.inventories.InternalInventory;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.MEStorage;
import appeng.helpers.ICraftingGridMenu;
import appeng.menu.slot.CraftingTermSlot;
import net.minecraft.world.entity.player.Player;

public class ETCraftingSlot extends CraftingTermSlot {
    public ETCraftingSlot(Player player, IActionSource mySrc, IEnergySource energySrc, MEStorage storage, InternalInventory cMatrix, InternalInventory secondMatrix, ICraftingGridMenu ccp) {
        super(player, mySrc, energySrc, storage, cMatrix, secondMatrix, ccp);
    }
}
