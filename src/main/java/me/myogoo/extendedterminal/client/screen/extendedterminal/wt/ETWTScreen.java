package me.myogoo.extendedterminal.client.screen.extendedterminal.wt;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.BackgroundPanel;
import appeng.client.guidebook.PageAnchor;
import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.TextConstants;
import de.mari_023.ae2wtlib.terminal.ItemButton;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMode;
import de.mari_023.ae2wtlib.wut.CycleTerminalButton;
import de.mari_023.ae2wtlib.wut.IUniversalTerminalCapable;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.client.screen.extendedterminal.ETTerminalScreen;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

public class ETWTScreen extends ETTerminalScreen<ETWTMenu> implements IUniversalTerminalCapable {
    private static final PageAnchor GUIDE_TOPIC = PageAnchor.page(ExtendedTerminal.makeId("terminals/extendedterminal.md"));
    private final ItemButton magnetCardMenuButton;

    public ETWTScreen(ETWTMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        if (menu.isWUT()) {
            addToLeftToolbar(new CycleTerminalButton(btn -> cycleTerminal()));
        }

        var magnetCardTexture = new ResourceLocation(AE2wtlib.MOD_NAME, "textures/item/magnet_card.png");
        magnetCardMenuButton = new ItemButton(btn -> getMenu().openMagnetMenu(), magnetCardTexture);
        addToLeftToolbar(magnetCardMenuButton);
        magnetCardMenuButton.setMessage(TextConstants.MAGNET_FILTER);

        widgets.add("singularityBackground", new BackgroundPanel(style.getImage("singularityBackground")));
    }

    private void updateMagnetFilterVisibility() {
        var mode = getMenu().getMagnetMode();
        magnetCardMenuButton.setVisibility(mode != MagnetMode.INVALID && mode != MagnetMode.NO_CARD);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        updateMagnetFilterVisibility();
    }

    public ETWTHost getHost() {
        return (ETWTHost) this.menu.getHost();
    }

    @Override
    protected @Nullable PageAnchor getHelpTopic() {
        if (getMenu().isWUT()) {
            return GUIDE_TOPIC;
        }
        return super.getHelpTopic();
    }
}
