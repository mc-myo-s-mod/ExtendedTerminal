package me.myogoo.extendedterminal.gametest;

import com.mojang.authlib.GameProfile;
import de.mari_023.ae2wtlib.AE2wtlib;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.init.wt.WTItems;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import me.myogoo.extendedterminal.menu.recipe.ETRecipeTransferSupport;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;

import java.util.List;
import java.util.UUID;

@GameTestHolder(ExtendedTerminal.MODID)
public final class PanelTransferGameTests {
    @GameTestGenerator
    public static List<TestFunction> tests() {
        return ModList.get().isLoaded("ae2wtlib") ? Tests.tests() : List.of();
    }

    private static final class Tests {
        private static List<TestFunction> tests() {
            return List.of(new TestFunction("extendedterminal", "panel_source_dirty_roundtrip",
                    "extendedterminal:empty", 100, 0, true, Tests::sourceDirtyRoundtrip));
        }

        private static void sourceDirtyRoundtrip(GameTestHelper helper) {
            for (boolean universal : new boolean[]{false, true}) {
                for (var source : ETTerminalMode.loadableValues()) {
                    for (var target : ETTerminalMode.loadableValues()) {
                        if (source == target) continue;
                        for (int count : new int[]{1, 3}) {
                            var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "panel-transfer"));
                            var stack = universal ? new ItemStack((net.minecraft.world.item.Item) AE2wtlib.UNIVERSAL_TERMINAL)
                                    : new ItemStack(WTItems.WIRELESS_ET_TERMINAL);
                            if (universal) {
                                stack.getOrCreateTag().putBoolean(ETMenuType.ET_TERMINAL.getWTIdAsString(), true);
                                stack.getOrCreateTag().putString("currentTerminal", ETMenuType.ET_TERMINAL.getWTIdAsString());
                            }
                            var host = host(player, stack);
                            host.setMode(target);
                            var original = new ItemStack(Items.DIAMOND, count);
                            original.setHoverName(Component.literal("saved panel input"));
                            host.getSubInventory(source.getInventoryId()).setItemDirect(0, original.copy());
                            var menu = new ETTerminalMenu(ETTerminalMenu.TYPE, 0, player.getInventory(), host);
                            player.containerMenu = menu;
                            for (var ingredient : new Ingredient[]{Ingredient.EMPTY, Ingredient.of(Items.EMERALD)}) {
                                helper.assertTrue(ETRecipeTransferSupport.takeIngredientFromOtherGrid(menu, ingredient).isEmpty()
                                                && ItemStack.matches(reopen(player, stack).getSubInventory(source.getInventoryId()).getStackInSlot(0), original),
                                        "Rejected extraction leaves serialized source unchanged");
                            }
                            var moved = ETRecipeTransferSupport.takeIngredientFromOtherGrid(menu, Ingredient.of(Items.DIAMOND));
                            // Reopen before destination/close can accidentally save the dirty source for us.
                            var savedSource = reopen(player, stack).getSubInventory(source.getInventoryId()).getStackInSlot(0);
                            helper.assertTrue(savedSource.getCount() + moved.getCount() == count
                                            && moved.getCount() == 1 && ItemStack.isSameItemSameTags(moved, original)
                                            && (savedSource.isEmpty() || ItemStack.isSameItemSameTags(savedSource, original)),
                                    "Source extraction persists immediately: " + source + " -> " + target + " count " + count);
                            host.getSubInventory(target.getInventoryId()).setItemDirect(0, moved);
                            menu.removed(player);
                            var reopened = reopen(player, stack);
                            helper.assertTrue(reopened.getSubInventory(source.getInventoryId()).getStackInSlot(0).getCount()
                                            + reopened.getSubInventory(target.getInventoryId()).getStackInSlot(0).getCount() == count
                                            && ItemStack.matches(reopened.getSubInventory(target.getInventoryId()).getStackInSlot(0), moved)
                                            && player.getInventory().isEmpty(),
                                    "Close/NBT/reopen conserves source plus destination without spills");
                        }
                    }
                }
            }
            packetDestinations(helper);
            helper.succeed();
        }

        private static void packetDestinations(GameTestHelper helper) {
            var node = appeng.api.networking.GridHelper.createManagedNode(helper, (owner, changed) -> {})
                    .setInWorldNode(false);
            node.create(helper.getLevel(), null);
            try {
                for (int occupied : new int[]{0, 63, 64}) {
                    var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "panel-packet"));
                    var stack = new ItemStack(WTItems.WIRELESS_ET_TERMINAL);
                    var host = host(player, stack);
                    host.setMode(ETTerminalMode.ANVIL);
                    var original = new ItemStack(Items.STONE, 3);
                    original.setHoverName(Component.literal("packet source"));
                    host.getSubInventory(ETTerminalMode.ANVIL.getInventoryId()).setItemDirect(0, original.copy());
                    if (occupied > 0) {
                        host.getSubInventory(ETTerminalMode.STONECUTTING.getInventoryId()).setItemDirect(0, new ItemStack(Items.DIAMOND, occupied));
                    }
                    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        player.getInventory().setItem(i, new ItemStack(Items.DIRT, 64));
                    }
                    player.getInventory().setItem(0, new ItemStack(Items.DIAMOND, 63));
                    var recipe = helper.getLevel().getRecipeManager().getRecipesFor(net.minecraft.world.item.crafting.RecipeType.STONECUTTING,
                            new net.minecraft.world.SimpleContainer(new ItemStack(Items.STONE)), helper.getLevel()).get(0);
                    var menu = new ETTerminalMenu(ETTerminalMenu.TYPE, 0, player.getInventory(), host) {
                        @Override public appeng.api.networking.IGridNode getNetworkNode() { return node.getNode(); }
                    };
                    player.containerMenu = menu;
                    var packetClass = me.myogoo.extendedterminal.network.serverbound.ETFillStonecutterGridFromRecipePacket.class;
                    var handle = packetClass.getDeclaredMethod("handleOnServer", net.minecraft.server.level.ServerPlayer.class);
                    handle.setAccessible(true);
                    handle.invoke(new me.myogoo.extendedterminal.network.serverbound.ETFillStonecutterGridFromRecipePacket(
                            recipe.getId(), new ItemStack(Items.STONE), false), player);
                    menu.removed(player);
                    var reopened = reopen(player, stack);
                    var source = reopened.getSubInventory(ETTerminalMode.ANVIL.getInventoryId()).getStackInSlot(0);
                    var target = reopened.getSubInventory(ETTerminalMode.STONECUTTING.getInventoryId()).getStackInSlot(0);
                    helper.assertTrue(occupied == 0
                                    ? source.getCount() == 2 && target.getCount() == 1
                                        && ItemStack.isSameItemSameTags(source, original) && ItemStack.isSameItemSameTags(target, original)
                                    : ItemStack.matches(source, original) && target.is(Items.DIAMOND)
                                        && target.getCount() == occupied - 1 && player.getInventory().getItem(0).getCount() == 64,
                            "Packet close/NBT/reopen conserves source and partial/blocked destination: " + occupied);
                }
            } catch (ReflectiveOperationException exception) {
                throw new AssertionError(exception);
            } finally {
                node.destroy();
            }
        }

        private static ETWTHost host(FakePlayer player, ItemStack stack) {
            return new ETWTHost(player, null, stack, (p, menu) -> {});
        }

        private static ETWTHost reopen(FakePlayer player, ItemStack stack) {
            return host(player, ItemStack.of(stack.save(new CompoundTag())));
        }
    }
}
