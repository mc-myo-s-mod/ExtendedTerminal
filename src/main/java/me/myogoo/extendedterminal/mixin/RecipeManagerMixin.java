package me.myogoo.extendedterminal.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.sugar.Local;
import me.myogoo.extendedterminal.event.RecipeManagerLoadingEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Lcom/google/common/collect/ImmutableMap;builder()Lcom/google/common/collect/ImmutableMap$Builder;", ordinal = 0))
    public void et$apply(
            Map<ResourceLocation, JsonElement> object,
            ResourceManager resourceManager,
            ProfilerFiller profiler,
            CallbackInfo ci,
            @Local(ordinal = 1) Map<RecipeType<?>, ImmutableMap.Builder<ResourceLocation, Recipe<?>>> map, // recipes
            @Local(name = "builder") ImmutableMap.Builder<ResourceLocation, Recipe<?>> builder) {
        RecipeManagerLoadingEvent.fireRecipeManagerLoadingEvent(
                (RecipeManager) (Object) this,
                (Map<RecipeType<?>, Object>) (Object) map,
                builder
        );
    }
}
