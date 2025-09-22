package me.myogoo.extendedterminal.client.screen.extendedterminal;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.TabButton;
import me.myogoo.extendedterminal.client.screen.ETTerminalBaseScreen;
import me.myogoo.extendedterminal.client.screen.extendedterminal.gui.ETTerminalModePanel;
import me.myogoo.extendedterminal.client.screen.extendedterminal.gui.panels.CraftingPanel;
import me.myogoo.extendedterminal.client.screen.extendedterminal.gui.panels.SmithingTablePanel;
import me.myogoo.extendedterminal.client.screen.extendedterminal.gui.panels.StoneCutterPanel;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.CraftingRecipe;

import java.util.EnumMap;
import java.util.Map;

public class ETTerminalScreen extends ETTerminalBaseScreen<CraftingRecipe, ETTerminalMenu> {
    private final Map<ETTerminalMode, ETTerminalModePanel> modePanels = new EnumMap<>(ETTerminalMode.class);
    private final Map<ETTerminalMode, TabButton> modeTabButtons = new EnumMap<>(ETTerminalMode.class);

    public ETTerminalScreen(ETTerminalMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        for(var mode : ETTerminalMode.loadableValues()) {
            var panel = switch (mode) {
                case CRAFTING -> new CraftingPanel(this, widgets, clearBtn, clearToPlayerInvBtn);
                case SMITHING -> new SmithingTablePanel(this, this.widgets);
                case STONECUTTING -> new StoneCutterPanel(this, widgets);
                case null, default -> null;
            };

            var tabButton = new TabButton(
                    panel.getIcon(),
                    panel.getTabTooltip(),
                    btn ->  {
                        getMenu().setMode(mode);
                    });
            tabButton.setStyle(TabButton.Style.HORIZONTAL);

            var modeIndex = modeTabButtons.size();
            widgets.add(panel.getWidgetId(), panel);
            widgets.add(panel.getModeTabButtonId(), tabButton);
            modeTabButtons.put(mode, tabButton);

            modePanels.put(mode, panel);
        }
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        for(var mode : ETTerminalMode.loadableValues()) {
            var selected = menu.getMode() == mode;
            modeTabButtons.get(mode).setSelected(selected);
            modePanels.get(mode).setVisible(selected);
        }
    }

}
