package me.myogoo.extendedterminal.integration.jei.extendedcrafting;

import com.blakebr0.extendedcrafting.compat.jei.category.table.AdvancedTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.BasicTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.EliteTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.UltimateTableCategory;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.itemList.jei.JEI;
import me.myogoo.myotus.api.annotation.itemList.jei.JEIGuiHandler;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.AdvancedTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.BasicTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.EliteTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.UltimateTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.wt.ExtendedCraftingWTScreen;
import me.myogoo.extendedterminal.integration.emi.extendedcrafting.ECWorkstation;
import me.myogoo.extendedterminal.integration.jei.ETJeiPlugin;
import me.myogoo.extendedterminal.integration.jei.handler.JeiTableGuiHandler;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import me.myogoo.extendedterminal.api.annotation.ExtendedCrafting;

import java.util.Collection;
import java.util.List;

@ExtendedCrafting
@JEI
@JEIGuiHandler
public class ECGuiHandler {
    @MyotusSubscriber
    public static void init(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(BasicTerminalScreen.class, new JeiTableGuiHandler<>(BasicTableCategory.RECIPE_TYPE));
        registration.addGuiContainerHandler(AdvancedTerminalScreen.class, new JeiTableGuiHandler<>(AdvancedTableCategory.RECIPE_TYPE));
        registration.addGuiContainerHandler(EliteTerminalScreen.class, new JeiTableGuiHandler<>(EliteTableCategory.RECIPE_TYPE));
        registration.addGuiContainerHandler(UltimateTerminalScreen.class, new JeiTableGuiHandler<>(UltimateTableCategory.RECIPE_TYPE));
        if (MyotusAPI.integrations().isLoaded(AE2WTLib.class)
                && (ETMenuType.EPIC_TERMINAL.canLoad() || ETMenuType.LEGENDARY_TERMINAL.canLoad())) {
            registration.addGuiContainerHandler(ExtendedCraftingWTScreen.class, new WirelessGuiHandler());
        }
    }

    private static class WirelessGuiHandler implements IGuiContainerHandler<ExtendedCraftingWTScreen> {
        @Override
        public @NotNull List<Rect2i> getGuiExtraAreas(ExtendedCraftingWTScreen screen) {
            return screen.getExclusionZones();
        }

        @Override
        public @NotNull Collection<IGuiClickableArea> getGuiClickableAreas(
                ExtendedCraftingWTScreen screen, double guiMouseX, double guiMouseY) {
            var menu = screen.getMenu();
            var outputSlot = menu.getSlots(menu.getOutputSlotSemantic()).get(0);
            var area = new Rect2i(outputSlot.x - 4, outputSlot.y - 30, 24, 24);
            return List.of(new IGuiClickableArea() {
                @Override
                public @NotNull Rect2i getArea() {
                    return area;
                }

                @Override
                public void onClick(IFocusFactory focusFactory, IRecipesGui recipesGui) {
                    if (ModList.get().isLoaded("emi")) {
                        ECWorkstation.showRecipes(menu.getETMenuType());
                    } else {
                        var category = new ResourceLocation("extendedcrafting",
                                menu.getETMenuType() == ETMenuType.EPIC_TERMINAL
                                        ? "epic_crafting" : "legendary_crafting");
                        recipesGui.showTypes(List.of(ETJeiPlugin.recipeTypeFor(category)));
                    }
                }
            });
        }
    }
}
