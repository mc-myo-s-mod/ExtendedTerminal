package me.myogoo.extendedterminal.datagen;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public record RawIngredientValue(ResourceLocation item) implements Ingredient.Value {

    @NotNull
    @Override
    public Collection<ItemStack> getItems() {
        return Collections.singleton(new ItemStack(ForgeRegistries.ITEMS.getValue(this.item)));
    }

    @NotNull
    @Override
    public JsonObject serialize() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("item", item.toString());
        return jsonobject;
    }
}
