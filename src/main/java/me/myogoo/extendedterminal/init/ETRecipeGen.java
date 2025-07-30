package me.myogoo.extendedterminal.init;

import appeng.core.definitions.AEParts;
import com.blakebr0.extendedcrafting.init.ModBlocks;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapedTableCraftingRecipe;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.config.ETConfig;
import me.myogoo.extendedterminal.event.RecipeManagerLoadingEvent;
import me.myogoo.extendedterminal.util.extendedcrafting.ShapedTableRecipeBuilder;
import me.myogoo.extendedterminal.util.mod.ModLoadHelper;
import net.neoforged.bus.api.SubscribeEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ETRecipeGen {
    @SubscribeEvent
    static void onRegisterRecipes(RecipeManagerLoadingEvent event) {
        Method[] methods = ETRecipeGen.class.getDeclaredMethods();
        for(Method method : methods) {
            if(method.getParameterCount() == 1 && method.getParameterTypes()[0] == RecipeManagerLoadingEvent.class) {
                if(Arrays.stream(method.getDeclaredAnnotations()).allMatch(x -> ModLoadHelper.get(x.annotationType()))) {
                    method.setAccessible(true);
                    try {
                        method.invoke(null, event);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        ExtendedTerminal.LOGGER.error("Failed to invoke recipe registration method: {}", method.getName(), e);
                    }
                }
            }
        }
    }

    @ModAccessor.ExtendedCrafting
    private static void loadExCraftingRecipe(RecipeManagerLoadingEvent event) {
        if(ETConfig.BASIC_TERMINAL_CONFIG.enableTerminal()) event.addRecipe(ShapedTableRecipeBuilder.shaped(ETParts.BASIC_TERMINAL_PART, 1)
                .pattern("AB")
                .pattern("C ")
                .define('A', AEParts.CRAFTING_TERMINAL)
                .define('B', ModBlocks.BASIC_TABLE.get())
                .define('C', ETItems.COMPAT_PROCESSOR)
                .build(ExtendedTerminal.makeId("extended_crafting/basic_terminal")));

        if(ETConfig.ADVANCED_TERMINAL_CONFIG.enableTerminal()) event.addRecipe(ShapedTableRecipeBuilder.shaped(ETParts.ADVANCED_TERMINAL_PART, 1)
                .pattern("AB")
                .pattern("C ")
                .define('A', AEParts.CRAFTING_TERMINAL)
                .define('B', ModBlocks.ADVANCED_TABLE.get())
                .define('C', ETItems.COMPAT_PROCESSOR)
                .build(ExtendedTerminal.makeId("extended_crafting/advanced_terminal")));

        if(ETConfig.ELITE_TERMINAL_CONFIG.enableTerminal()) event.addRecipe(ShapedTableRecipeBuilder.shaped(ETParts.ELITE_TERMINAL_PART,1)
                .pattern("AB")
                .pattern("C ")
                .define('A', AEParts.CRAFTING_TERMINAL)
                .define('B', ModBlocks.ELITE_TABLE.get())
                .define('C', ETItems.COMPAT_PROCESSOR)
                .build(ExtendedTerminal.makeId("extended_crafting/elite_terminal")));

        if(ETConfig.ULTIMATE_TERMINAL_CONFIG.enableTerminal()) event.addRecipe(ShapedTableRecipeBuilder.shaped(ETParts.ULTIMATE_TERMINAL_PART, 1)
                .pattern("AB")
                .pattern("C ")
                .define('A', AEParts.CRAFTING_TERMINAL)
                .define('B', ModBlocks.ULTIMATE_TABLE.get())
                .define('C', ETItems.COMPAT_PROCESSOR)
                .build(ExtendedTerminal.makeId("extended_crafting/ultimate_terminal")));
    }

    @ModAccessor.Avaritia
    private static void loadAvaritiaRecipe(RecipeManagerLoadingEvent event) {
    }
}
