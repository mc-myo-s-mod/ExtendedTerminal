package me.myogoo.extendedterminal.mixin;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmithingTrimRecipe.class)
public interface SmithingTrimRecipeAccessor {
    @Accessor("template")
    Ingredient extendedterminal$getTemplate();

    @Accessor("base")
    Ingredient extendedterminal$getBase();

    @Accessor("addition")
    Ingredient extendedterminal$getAddition();
}
