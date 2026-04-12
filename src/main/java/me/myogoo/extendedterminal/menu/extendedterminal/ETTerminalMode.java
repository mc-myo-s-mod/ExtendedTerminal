package me.myogoo.extendedterminal.menu.extendedterminal;

import appeng.menu.SlotSemantic;
import me.myogoo.extendedterminal.config.ExtendedTerminalConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import me.myogoo.extendedterminal.part.extendedterminal.ETTerminalPart;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;

public enum ETTerminalMode {
    CRAFTING(ExtendedTerminalConfig.INSTANCE.getExtendedTerminalConfig().enableCraftingPanel()),
    STONECUTTING(ExtendedTerminalConfig.INSTANCE.getExtendedTerminalConfig().enableStonecutterPanel()),
    SMITHING(ExtendedTerminalConfig.INSTANCE.getExtendedTerminalConfig().enableSmithingPanel()),
    ANVIL(ExtendedTerminalConfig.INSTANCE.getExtendedTerminalConfig().enableAnvilPanel());

    private final boolean enabled;

    ETTerminalMode(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean canLoad() {
        return this.enabled;
    }

    public ResourceLocation getInventoryId() {
        return switch (this) {
            case CRAFTING -> ETMenuType.ET_TERMINAL.getCraftingInventory();
            case STONECUTTING -> ETTerminalPart.STONECUTTER_INVENTORY;
            case SMITHING -> ETTerminalPart.SMITHING_INVENTORY;
            case ANVIL -> ETTerminalPart.ANVIL_INVENTORY;
        };
    }

    public List<SlotSemantic> getSlotSemantics() {
        return switch (this) {
            case CRAFTING -> List.of(ETMenuType.ET_TERMINAL.getSlotSemanticGrid());
            case STONECUTTING -> List.of(ETSlotSemantics.STONECUTTING_INPUT);
            case SMITHING -> List.of(
                    ETSlotSemantics.SMITHING_TABLE_TEMPLATE,
                    ETSlotSemantics.SMITHING_TABLE_BASE,
                    ETSlotSemantics.SMITHING_TABLE_ADDITION);
            case ANVIL -> List.of(ETSlotSemantics.ANVIL_LEFT_INPUT, ETSlotSemantics.ANVIL_RIGHT_INPUT);
        };
    }

    public static List<ETTerminalMode> loadableValues() {
        var list = Arrays.stream(values()).filter(ETTerminalMode::canLoad).toList();
        if (list.isEmpty()) {
            throw new IllegalStateException("No ETTerminalModes are loadable");
        }
        return list;
    }
}
