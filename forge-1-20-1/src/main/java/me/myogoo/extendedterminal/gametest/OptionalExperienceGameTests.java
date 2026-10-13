package me.myogoo.extendedterminal.gametest;

import com.mojang.authlib.GameProfile;
import me.myogoo.extendedterminal.init.wt.WTItems;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import me.myogoo.extendedterminal.menu.extendedterminal.FakeAnvilMenu;
import me.myogoo.extendedterminal.part.extendedterminal.ETTerminalPart;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.UUID;

@GameTestHolder("extendedterminal")
@PrefixGameTestTemplate(false)
public final class OptionalExperienceGameTests {
    @GameTestGenerator
    public static List<TestFunction> tests() {
        if (!Boolean.getBoolean("extendedterminal.test.optionalExperience")) return List.of();
        return List.of(new TestFunction("extendedterminal", "optional_apotheosis_anvil", "extendedterminal:empty",
                100, 0, true, OptionalExperienceGameTests::anvil));
    }

    private static ItemStack input() {
        var stack = new ItemStack(Items.DIAMOND);
        stack.setRepairCost(4);
        return stack;
    }

    private static void anvil(GameTestHelper helper) {
        helper.assertTrue(ModList.get().isLoaded("apotheosis") && ModList.get().isLoaded("ae2wtlib"),
                "Optional suite requires actual Apotheosis and WTLib");
        var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "et-optional-xp"));
        player.giveExperiencePoints(173);
        var vanilla = new FakeAnvilMenu(0, player.getInventory());
        vanilla.getInputSlots().setItem(0, input());
        vanilla.setItemName("Optional XP");
        helper.assertTrue(vanilla.getCost() == 5 && !vanilla.getResultItem().isEmpty(), "Real anvil costs five levels");
        vanilla.et$onTake(player, vanilla.getResultItem().copy());
        helper.assertTrue(player.totalExperience == 118, "Actual Apotheosis mixin debits exactly 55 XP");

        player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "et-optional-xp"));
        player.giveExperiencePoints(54);
        var host = new ETWTHost(player, null, new ItemStack(WTItems.WIRELESS_ET_TERMINAL), (p, menu) -> {});
        host.setMode(ETTerminalMode.ANVIL);
        var inventory = host.getSubInventory(ETTerminalPart.ANVIL_INVENTORY);
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
                "ET simulation is non-mutating and debit matches the actual Apotheosis anvil");
        player.getAbilities().instabuild = true;
        helper.assertTrue(menu.consumeAnvilExperience(player) && player.totalExperience == 118,
                "Creative bypass preserves XP");
        helper.succeed();
    }
}
