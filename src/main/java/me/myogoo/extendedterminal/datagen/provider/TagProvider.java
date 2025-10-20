package me.myogoo.extendedterminal.datagen.provider;

import appeng.datagen.providers.tags.ConventionTags;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.init.ETItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class TagProvider extends ItemTagsProvider {
    public TagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, CompletableFuture<TagLookup<Block>> blockTagsProvider, ExistingFileHelper existingFileHelper) {
        super(packOutput, registries, blockTagsProvider, ExtendedTerminal.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(ConventionTags.INSCRIBER_PRESSES)
                .add(ETItems.COMPAT_PRESS.asItem());

        tag(Tags.Items.ENDER_PEARLS)
                .add(ETItems.CHARGED_ENDER_PEARL.asItem());
    }
}
