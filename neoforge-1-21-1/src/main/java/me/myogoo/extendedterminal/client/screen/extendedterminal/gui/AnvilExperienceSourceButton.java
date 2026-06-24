package me.myogoo.extendedterminal.client.screen.extendedterminal.gui;

import me.myogoo.myotus.client.gui.widgets.button.MyoCycleOverlayButton;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;

public class AnvilExperienceSourceButton extends MyoCycleOverlayButton {
    private static final ResourceLocation APPLIED_EXPERIENCED_CELL_ID =
            ResourceLocation.fromNamespaceAndPath("appex", "experience_storage_cell_1k");

    public AnvilExperienceSourceButton(ETTerminalMenu menu) {
        super(
                menu::cycleAnvilExperienceSourcePriority,
                () -> getSourceIcon(menu),
                () -> List.of(Component.literal(menu.getAnvilExperienceSourcePriorityLabel())));
    }

    private static Item getSourceIcon(ETTerminalMenu menu) {
        return switch (menu.getAnvilExperienceSourcePriority().getFirst()) {
            case FLUID_XP -> Items.EXPERIENCE_BOTTLE;
            case APPLIED_EXPERIENCED_AMOUNT -> getAppliedExperiencedCellIcon();
            case PLAYER -> Items.PLAYER_HEAD;
        };
    }

    private static Item getAppliedExperiencedCellIcon() {
        return BuiltInRegistries.ITEM.getOptional(APPLIED_EXPERIENCED_CELL_ID)
                .orElse(Items.EXPERIENCE_BOTTLE);
    }
}
