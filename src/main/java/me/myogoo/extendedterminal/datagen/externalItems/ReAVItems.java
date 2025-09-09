package me.myogoo.extendedterminal.datagen.externalItems;

import me.myogoo.extendedterminal.datagen.RawIngredientValue;
import me.myogoo.extendedterminal.util.mod.ModLoadHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.stream.Stream;

public final class ReAVItems {
    public static final Ingredient SCULK_TABLE = Ingredient.fromValues(Stream.of(new RawIngredientValue(new ResourceLocation(ModLoadHelper.Avaritia_ID, "sculk_crafting_table"))));
    public static final Ingredient NETHER_TABLE = Ingredient.fromValues(Stream.of(new RawIngredientValue(new ResourceLocation(ModLoadHelper.Avaritia_ID, "nether_crafting_table"))));
    public static final Ingredient END_TABLE = Ingredient.fromValues(Stream.of(new RawIngredientValue(new ResourceLocation(ModLoadHelper.Avaritia_ID, "end_crafting_table"))));
    public static final Ingredient EXTREME_TABLE = Ingredient.fromValues(Stream.of(new RawIngredientValue(new ResourceLocation(ModLoadHelper.Avaritia_ID, "extreme_crafting_table"))));
}
