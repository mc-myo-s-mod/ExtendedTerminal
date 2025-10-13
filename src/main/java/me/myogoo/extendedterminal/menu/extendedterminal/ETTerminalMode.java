package me.myogoo.extendedterminal.menu.extendedterminal;

import appeng.api.inventories.InternalInventory;
import appeng.menu.SlotSemantic;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import me.myogoo.extendedterminal.part.extendedterminal.ETTerminalPart;
import me.myogoo.extendedterminal.util.mod.ModIntegrationManager;
import net.minecraft.resources.ResourceLocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public enum ETTerminalMode {
    CRAFTING,
    STONECUTTING,
    SMITHING,
    ANVIL;

    public boolean canLoad() {
        try {
            Field field = ETTerminalMode.class.getField(this.name());
            if(field.getDeclaredAnnotations().length == 0) {
                return true;
            }
            return Arrays.stream(field.getDeclaredAnnotations())
                    .map(Annotation::annotationType)
                    .allMatch(ModIntegrationManager::isLoaded);

        } catch (NoSuchFieldException e) {
            ExtendedTerminal.LOGGER.error("Terminal Mode {} is not loaded due to missing field in ETTerminalMode", this.name());
        }
        return false;
    }

    public ResourceLocation getInventoryId() {
        return switch (this) {
            case CRAFTING -> ETMenuType.ET_TERMINAL.getCraftingInventory();
            case STONECUTTING -> ETTerminalPart.StoneCutterInventory;
            case SMITHING -> ETTerminalPart.SmithingInventory;
            case ANVIL -> ETTerminalPart.AnvilInventory;
        };
    }

    public List<SlotSemantic> getSlotSemantics() {
        return switch (this) {
            case CRAFTING -> List.of(ETMenuType.ET_TERMINAL.getSlotSemanticGrid());
            case STONECUTTING -> List.of(ETSlotSemantics.STONECUTTING_INPUT);
            case SMITHING -> List.of(ETSlotSemantics.SMITHING_TABLE_BASE, ETSlotSemantics.SMITHING_TABLE_TEMPLATE, ETSlotSemantics.SMITHING_TABLE_ADDITION);
            case ANVIL -> List.of(ETSlotSemantics.ANVIL_LEFT_INPUT, ETSlotSemantics.ANVIL_RIGHT_INPUT);
        };
    }

    public static List<ETTerminalMode> loadableValues() {
        return Arrays.stream(values()).filter(ETTerminalMode::canLoad).toList();
    }
}
