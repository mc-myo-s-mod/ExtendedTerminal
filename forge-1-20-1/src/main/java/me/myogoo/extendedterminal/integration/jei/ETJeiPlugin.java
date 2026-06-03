package me.myogoo.extendedterminal.integration.jei;

import com.mojang.logging.LogUtils;
import me.myogoo.extendedterminal.ExtendedTerminal;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.antlr.v4.runtime.misc.NotNull;
import org.slf4j.Logger;


@JeiPlugin
public class ETJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = ExtendedTerminal.makeId("jei_plugin");
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        JeiRegisterHelper.registerRecipeCatalysts(registration);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        JeiRegisterHelper.registerRecipeTransfer(registration);
    }

    @Override
    public void registerGuiHandlers(@org.jetbrains.annotations.NotNull IGuiHandlerRegistration registration) {
        JeiRegisterHelper.registerGuiHandler(registration);
    }
}
