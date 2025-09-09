package me.myogoo.extendedterminal.client;

import appeng.datagen.providers.localization.LocalizationProvider;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.DataGen;
import me.myogoo.extendedterminal.client.provider.InscriberRecipeProvider;
import me.myogoo.extendedterminal.util.SafeClass;
import me.myogoo.extendedterminal.util.mod.ModLoadHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

@Mod.EventBusSubscriber(modid = ExtendedTerminal.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ETDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        ModLoadHelper.init();
        var registryAccess = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        var registries = new LocalizationProvider(event.getGenerator());

        var generator = event.getGenerator();
        var pack = generator.getVanillaPack(true);
        pack.addProvider(InscriberRecipeProvider::new);


        ModLoadHelper
                .getScanData()
                .getAnnotations()
                .stream()
                .filter(a -> a.annotationType().equals(Type.getType(DataGen.class)))
                .map(a -> SafeClass.forType(a.clazz()))
                .filter(a -> Arrays.stream(a.getDeclaredAnnotations()).allMatch(b -> ModLoadHelper.get(b.annotationType())))
                .forEach(x -> {
                        Constructor[] constructors = x.getDeclaredConstructors();
                        for (var constructor : constructors) {

                            if (constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0] == PackOutput.class) {
                                constructor.setAccessible(true);
                                pack.addProvider(t -> {
                                    try {
                                        Object obj = constructor.newInstance(t);
                                        if(!(obj instanceof DataProvider dataProvider)) {
                                            throw new RuntimeException("DataProvider 생성 실패: " + x.getSimpleName() + " is not a DataProvider");
                                        }
                                        return dataProvider;
                                    } catch (InstantiationException | IllegalAccessException |
                                             InvocationTargetException e) {
                                        ExtendedTerminal.LOGGER.error("Failed to generate recipe : {}", x.getSimpleName(), e);
                                        throw new RuntimeException("DataProvider 생성 실패: " + x.getSimpleName(), e);
                                    }
                                });
                            } else {
                                ExtendedTerminal.LOGGER.warn("Method {} in class {} is annotated with @SubscribeLoadEvent but does not have the correct parameters.", constructor.getName(), x.getName());
                            }
                        }

                });
    }
}