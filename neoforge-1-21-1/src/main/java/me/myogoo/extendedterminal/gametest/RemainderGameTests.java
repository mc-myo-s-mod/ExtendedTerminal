package me.myogoo.extendedterminal.gametest;

import appeng.api.networking.security.IActionSource;
import appeng.me.storage.NullInventory;
import appeng.util.inv.AppEngInternalInventory;
import com.mojang.authlib.GameProfile;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.gametest.GameTestHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@GameTestHolder(ExtendedTerminal.MODID)
public final class RemainderGameTests {
    @GameTestGenerator
    public static List<TestFunction> tests() {
        var tests = new ArrayList<TestFunction>();
        if (me.myogoo.myotus.api.MyotusAPI.integrations().isLoaded(me.myogoo.extendedterminal.api.annotation.ExtendedCrafting.class)) {
            tests.add(new TestFunction("extendedterminal", "ec_compact_remainders", "extendedterminal:empty", 100, 0, true,
                    helper -> remainders(helper, false)));
        }
        if (me.myogoo.myotus.api.MyotusAPI.integrations().isLoaded(me.myogoo.extendedterminal.api.annotation.ReAvaritia.class)) {
            tests.add(new TestFunction("extendedterminal", "re_compact_remainders", "extendedterminal:empty", 100, 0, true,
                    helper -> remainders(helper, true)));
        }
        return tests;
    }

    private static void remainders(GameTestHelper helper, boolean avaritia) {
        for (var size : new int[][]{{2, 2}, {2, 3}, {3, 2}}) {
            int width = size[0], height = size[1];
            for (int top : new int[]{0, 3, 9 - height}) {
                for (int left : new int[]{0, 3, 9 - width}) {
                    var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "remainder-test"));
                    var grid = new AppEngInternalInventory(81);
                    var remaining = NonNullList.withSize(width * height, ItemStack.EMPTY);
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            grid.setItemDirect((y + top) * 9 + x + left, new ItemStack(Items.HONEY_BOTTLE));
                            var bottle = new ItemStack(Items.GLASS_BOTTLE);
                            bottle.set(DataComponents.CUSTOM_NAME, Component.literal("remainder-" + (y * width + x)));
                            remaining.set(y * width + x, bottle);
                        }
                    }
                    if (avaritia) Re.consume(helper, player, grid, remaining, width, height);
                    else Ec.consume(helper, player, grid, remaining, width, height);
                    for (int i = 0; i < 81; i++) {
                        int x = i % 9 - left, y = i / 9 - top;
                        var expected = x >= 0 && x < width && y >= 0 && y < height
                                ? remaining.get(y * width + x) : ItemStack.EMPTY;
                        helper.assertTrue(ItemStack.matches(grid.getStackInSlot(i), expected),
                                "Compact remainder count/component/position at " + left + "," + top + " slot " + i);
                    }
                    helper.assertTrue(player.getInventory().isEmpty(), "No remainders leak or duplicate into player inventory");
                }
            }
        }
        helper.succeed();
    }

    // Supply a deterministic recipe remainder; exercise the production slot's actual consumption and placement.
    private static final class Ec {
        private static void consume(GameTestHelper helper, FakePlayer player, AppEngInternalInventory grid,
                                    NonNullList<ItemStack> remaining, int width, int height) {
            var slot = new me.myogoo.extendedterminal.menu.extendedcrafting.slot.ExCraftingTerminalSlot(
                    player, IActionSource.ofPlayer(player), (amount, action, multiplier) -> amount,
                    NullInventory.of(), grid, grid, null, ETMenuType.ULTIMATE_TERMINAL) {
                @Override protected NonNullList<ItemStack> getETRemainingItems(com.blakebr0.extendedcrafting.api.TableCraftingInput input, Level level) {
                    helper.assertTrue(input.width() == width && input.height() == height, "EC compacts to rectangular dimensions");
                    return remaining;
                }
                private void consume() { makeItem(player, new ItemStack(Items.HONEY_BLOCK)); }
            };
            slot.consume();
        }
    }

    private static final class Re {
        private static void consume(GameTestHelper helper, FakePlayer player, AppEngInternalInventory grid,
                                    NonNullList<ItemStack> remaining, int width, int height) {
            var slot = new me.myogoo.extendedterminal.menu.avaritiaRe.slot.AvaritiaCraftingTerminalSlot(
                    player, IActionSource.ofPlayer(player), (amount, action, multiplier) -> amount,
                    NullInventory.of(), grid, grid, null, ETMenuType.EXTREME_TERMINAL) {
                @Override protected NonNullList<ItemStack> getETRemainingItems(committee.nova.mods.avaritia.api.common.crafting.TierInput input, Level level) {
                    helper.assertTrue(input.width() == width && input.height() == height, "Re compacts to rectangular dimensions");
                    return remaining;
                }
                private void consume() { makeItem(player, new ItemStack(Items.HONEY_BLOCK)); }
            };
            slot.consume();
        }
    }
}
