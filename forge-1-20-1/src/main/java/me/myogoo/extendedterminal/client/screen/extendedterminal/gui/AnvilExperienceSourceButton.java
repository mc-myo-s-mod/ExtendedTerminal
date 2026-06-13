package me.myogoo.extendedterminal.client.screen.extendedterminal.gui;

import appeng.api.config.ActionItems;
import appeng.client.gui.Icon;
import appeng.client.gui.widgets.ActionButton;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AnvilExperienceSourceButton extends ActionButton {
    private static final ResourceLocation APPLIED_EXPERIENCED_CELL_ID =
            new ResourceLocation("appex", "experience_storage_cell_1k");

    private final ETTerminalMenu menu;

    public AnvilExperienceSourceButton(ETTerminalMenu menu) {
        super(ActionItems.CYCLE_PROCESSING_OUTPUT, menu::cycleAnvilExperienceSourcePriority);
        this.menu = menu;
    }

    @Override
    protected Icon getIcon() {
        return null;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        setMessage(Component.literal(menu.getAnvilExperienceSourcePriorityLabel()));
        super.renderWidget(guiGraphics, mouseX, mouseY, partial);

        if (!visible) {
            return;
        }

        ItemStack icon = getSourceIcon();
        if (!icon.isEmpty()) {
            int yOffset = isHovered() ? 1 : 0;
            guiGraphics.renderItem(icon, getX(), getY() + 1 + yOffset);
        }
    }

    private ItemStack getSourceIcon() {
        return switch (this.menu.getAnvilExperienceSourcePriority().get(0)) {
            case FLUID_XP -> Items.EXPERIENCE_BOTTLE.getDefaultInstance();
            case APPLIED_EXPERIENCED_AMOUNT -> getAppliedExperiencedCellIcon();
            case PLAYER -> getClientPlayerHeadIcon();
        };
    }

    private static ItemStack getAppliedExperiencedCellIcon() {
        return BuiltInRegistries.ITEM.getOptional(APPLIED_EXPERIENCED_CELL_ID)
                .map(Item::getDefaultInstance)
                .orElse(ItemStack.EMPTY);
    }

    private static ItemStack getClientPlayerHeadIcon() {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return Items.PLAYER_HEAD.getDefaultInstance();
        }

        var stack = Items.PLAYER_HEAD.getDefaultInstance();
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("SkullOwner", player.getGameProfile().getName());
        return stack;
    }
}
