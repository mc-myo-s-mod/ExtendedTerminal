package me.myogoo.extendedterminal.gametest;

import appeng.menu.locator.MenuLocators;
import com.mojang.authlib.GameProfile;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import me.myogoo.extendedterminal.menu.extendedterminal.FakeAnvilMenu;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.UUID;
import java.util.function.Consumer;

@EventBusSubscriber(modid = ExtendedTerminal.MODID)
public final class OptionalExperienceGameTests {
    private static final Identifier ID = ExtendedTerminal.makeId("optional_apothic_anvil");

    @SubscribeEvent
    public static void functions(RegisterEvent event) {
        if (Boolean.getBoolean("extendedterminal.test.optionalExperience") && event.getRegistryKey() == Registries.TEST_FUNCTION) {
            event.register(Registries.TEST_FUNCTION, ID, () -> (Consumer<GameTestHelper>) OptionalExperienceGameTests::anvil);
        }
    }

    @SubscribeEvent
    public static void tests(RegisterGameTestsEvent event) {
        if (!Boolean.getBoolean("extendedterminal.test.optionalExperience")) return;
        var environment = event.registerEnvironment(ID);
        event.registerTest(ID, new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION, ID),
                new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true)));
    }

    private static ItemStack input() {
        var stack = new ItemStack(Items.DIAMOND);
        stack.set(DataComponents.REPAIR_COST, 4);
        return stack;
    }

    private static void anvil(GameTestHelper helper) {
        helper.assertTrue(ModList.get().isLoaded("apothic_enchanting") && ModList.get().isLoaded("ae2wtlib"),
                "Optional suite requires actual Apothic Enchanting and WTLib");
        var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "et-optional-xp"));
        player.giveExperiencePoints(173);
        var vanilla = new FakeAnvilMenu(0, player.getInventory());
        vanilla.getInputSlots().setItem(0, input());
        vanilla.setItemName("Optional XP");
        helper.assertTrue(vanilla.getCost() == 5 && !vanilla.getResultItem().isEmpty(), "Real anvil costs five levels");
        vanilla.ET$OnTake(player, vanilla.getResultItem().copy());
        helper.assertTrue(player.totalExperience == 118, "Actual Apothic mixin debits exactly 55 XP");

        player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "et-optional-xp"));
        player.giveExperiencePoints(54);
        var stack = new ItemStack(WTDefinition.of(ETMenuType.ET_TERMINAL.getWTIdAsString()).item());
        var host = new ETWTHost((ItemWT) stack.getItem(), player, MenuLocators.forStack(stack), (p, menu) -> {});
        host.setMode(ETTerminalMode.ANVIL);
        var inventory = host.getSubInventory(ETTerminalMode.ANVIL.getInventoryId());
        inventory.setItemDirect(0, input());
        var menu = new ETTerminalMenu(ETTerminalMenu.TYPE, 0, player.getInventory(), host);
        menu.setAnvilItemName("Optional XP");
        helper.assertTrue(menu.getAnvilCost() == 5, "ET fixture costs five levels");
        helper.assertTrue(!menu.canPayAnvilCost(player) && !menu.consumeAnvilExperience(player)
                        && player.totalExperience == 54 && player.experienceLevel == 4
                        && ItemStack.matches(input(), inventory.getStackInSlot(0)),
                "One XP short refuses without mutating player or anvil input");
        player.giveExperiencePoints(119);
        helper.assertTrue(menu.canPayAnvilCost(player) && player.totalExperience == 173
                        && menu.consumeAnvilExperience(player) && player.totalExperience == 118,
                "ET simulation is non-mutating and debit matches the actual Apothic anvil");
        player.getAbilities().instabuild = true;
        helper.assertTrue(menu.consumeAnvilExperience(player) && player.totalExperience == 118,
                "Creative bypass preserves XP");
        helper.succeed();
    }
}
