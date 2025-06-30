package com.myogoo.extendedterminal.integration.jei;

import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.compat.jei.category.table.AdvancedTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.BasicTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.EliteTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.UltimateTableCategory;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import com.mojang.logging.LogUtils;
import com.myogoo.extendedterminal.ExtendedTerminal;
import com.myogoo.extendedterminal.init.ETMenus;
import com.myogoo.extendedterminal.init.ETParts;
import com.myogoo.extendedterminal.integration.jei.handler.JeiTableRecipeTransferHandler;
import com.myogoo.extendedterminal.menu.extendedcrafting.AdvancedTerminalMenu;
import com.myogoo.extendedterminal.menu.extendedcrafting.BasicTerminalMenu;
import com.myogoo.extendedterminal.menu.extendedcrafting.EliteTerminalMenu;
import com.myogoo.extendedterminal.menu.extendedcrafting.UltimateTerminalMenu;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JeiPlugin
public class ETJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = ExtendedTerminal.makeId("jei_plugin");
    private static final Logger LOGGER = LogUtils.getLogger();

    private Level level;

    public ETJeiPlugin() {
        this.level = Minecraft.getInstance().level;
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ETParts.BASIC_TERMINAL_PART, BasicTableCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(ETParts.ADVANCED_TERMINAL_PART, AdvancedTableCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(ETParts.ELITE_TERMINAL_PART, EliteTableCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(ETParts.ULTIMATE_TERMINAL_PART, UltimateTableCategory.RECIPE_TYPE);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        var helper = registration.getTransferHelper();

        registration.addRecipeTransferHandler(new JeiTableRecipeTransferHandler<>(BasicTerminalMenu.class, BasicTerminalMenu.TYPE, BasicTableCategory.RECIPE_TYPE, helper),BasicTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(new JeiTableRecipeTransferHandler<>(AdvancedTerminalMenu.class, AdvancedTerminalMenu.TYPE, AdvancedTableCategory.RECIPE_TYPE, helper),AdvancedTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(new JeiTableRecipeTransferHandler<>(EliteTerminalMenu.class, EliteTerminalMenu.TYPE, EliteTableCategory.RECIPE_TYPE, helper),EliteTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(new JeiTableRecipeTransferHandler<>(UltimateTerminalMenu.class, UltimateTerminalMenu.TYPE, UltimateTableCategory.RECIPE_TYPE, helper), UltimateTableCategory.RECIPE_TYPE);
    }
}
