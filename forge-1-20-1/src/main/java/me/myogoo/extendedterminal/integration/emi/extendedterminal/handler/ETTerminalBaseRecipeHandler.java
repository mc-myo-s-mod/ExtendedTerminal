package me.myogoo.extendedterminal.integration.emi.extendedterminal.handler;

import me.myogoo.extendedterminal.integration.emi.handler.AbstractEmiTableRecipeHandler;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public abstract class ETTerminalBaseRecipeHandler<T extends ETTerminalMenu> extends AbstractEmiTableRecipeHandler<T> {
    public ETTerminalBaseRecipeHandler(Class<T> containerClass) {
        super(containerClass);
    }

    @Override
    public List<Slot> getInputSources(T menu) {
        var slots = super.getInputSources(menu);
        slots.addAll(menu.getSlots(ETSlotSemantics.STONECUTTING_INPUT));
        slots.addAll(menu.getSlots(ETSlotSemantics.SMITHING_TABLE_BASE));
        slots.addAll(menu.getSlots(ETSlotSemantics.SMITHING_TABLE_TEMPLATE));
        slots.addAll(menu.getSlots(ETSlotSemantics.SMITHING_TABLE_ADDITION));
        return slots;
    }

}