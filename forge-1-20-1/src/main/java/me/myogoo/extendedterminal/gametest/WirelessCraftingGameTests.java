package me.myogoo.extendedterminal.gametest;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.implementations.menuobjects.IPortableTerminal;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.core.definitions.AEItems;
import appeng.helpers.InventoryAction;
import appeng.menu.ISubMenu;
import appeng.util.ConfigManager;
import appeng.util.inv.AppEngInternalInventory;
import com.mojang.authlib.GameProfile;
import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.terminal.ItemWT;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;
import de.mari_023.ae2wtlib.wut.WUTHandler;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.config.IETTerminalConfig;
import me.myogoo.extendedterminal.init.wt.WTItems;
import me.myogoo.extendedterminal.me.host.ExtendedCraftingWTHost;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.BasicTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.ExtendedTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.wt.ExtendedCraftingWTMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import me.myogoo.extendedterminal.part.extendedterminal.ETTerminalPart;
import me.myogoo.myotus.api.wt.AddTerminalEvent;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@GameTestHolder(ExtendedTerminal.MODID)
public final class WirelessCraftingGameTests {
    private WirelessCraftingGameTests() {
    }

    @GameTestGenerator
    public static List<TestFunction> generateTests() {
        if (!ModList.get().isLoaded("extendedcrafting")) {
            return List.of();
        }
        var tests = new ArrayList<TestFunction>();
        for (var type : ETMenuType.values()) {
            tests.add(new TestFunction("extendedterminal", "wirelesscraftinggametests.gridstoretake_" + type.getIdAsString(),
                    "extendedterminal:empty", 20, 0, true, helper -> gridStoreTake(helper, type)));
        }
        if (ModList.get().isLoaded("ae2wtlib")
                && ETMenuType.EPIC_TERMINAL.canLoad() && ETMenuType.LEGENDARY_TERMINAL.canLoad()) {
            tests.add(new TestFunction("extendedterminal", "wirelesscraftinggametests.inventoryroundtrip",
                    "extendedterminal:empty", 20, 0, true, WirelessCraftingGameTests::inventoryRoundTrip));
        }
        if (ModList.get().isLoaded("ae2wtlib") && !ModList.get().isLoaded("apotheosis")
                && !ModList.get().isLoaded("apothic_enchanting")) {
            tests.add(new TestFunction("extendedterminal", "wirelesscraftinggametests.anvilexperiencecost",
                    "extendedterminal:empty", 20, 0, true, WirelessCraftingGameTests::anvilExperienceCost));
        }
        return tests;
    }

    private static void anvilExperienceCost(GameTestHelper helper) {
        var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "et-anvil-test"));
        player.getAbilities().instabuild = false;
        var host = new ETWTHost(player, null, new ItemStack(WTItems.WIRELESS_ET_TERMINAL), (p, menu) -> { });
        host.setMode(ETTerminalMode.ANVIL);
        var input = new ItemStack(Items.DIAMOND);
        input.setRepairCost(4);
        host.getSubInventory(ETTerminalPart.ANVIL_INVENTORY).setItemDirect(0, input);
        var menu = new ETTerminalMenu(ETTerminalMenu.TYPE, 1, player.getInventory(), host);
        menu.setAnvilItemName("XP regression");
        helper.assertTrue(menu.getAnvilCost() == 5, "rename fixture must cost five levels");
        helper.assertTrue(!menu.canPayAnvilCost(player) && !menu.consumeAnvilExperience(player),
                "zero-level players without stored XP must not get a free anvil operation");

        player.giveExperiencePoints(7);
        helper.assertTrue(!menu.canPayAnvilCost(player) && !menu.consumeAnvilExperience(player)
                        && player.experienceLevel == 1,
                "insufficient XP must not underpay the displayed anvil cost or debit the player");

        player.giveExperiencePoints(48);
        helper.assertTrue(player.experienceLevel == 5 && menu.canPayAnvilCost(player)
                        && menu.consumeAnvilExperience(player) && player.experienceLevel == 0,
                "exactly five levels must pay the anvil cost");

        player.giveExperiencePoints(173); // Level 10 plus 13 of the next 27 XP.
        helper.assertTrue(menu.consumeAnvilExperience(player) && player.experienceLevel == 5
                        && player.totalExperience == 63,
                "vanilla level removal must account for the progress fraction at the target level");
        helper.succeed();
    }

    private static void gridStoreTake(GameTestHelper helper, ETMenuType type) {
        var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "et-store-test"));
        var host = new TestHost(player, type, 100);
        var menu = new ExtendedTerminalBaseMenu(BasicTerminalMenu.TYPE, 1, player.getInventory(), host, type, TEST_CONFIG);
        menu.broadcastChanges();
        var grid = host.grid;

        grid.setItemDirect(0, new ItemStack(Items.DIAMOND, 2));
        grid.setItemDirect(grid.size() - 1, new ItemStack(Items.EMERALD, 3));
        menu.doAction(player, InventoryAction.MOVE_REGION,
                menu.getSlots(type.getSlotSemanticGrid()).get(0).index, 0);
        helper.assertTrue(host.storage.count(Items.DIAMOND) == 2 && host.storage.count(Items.EMERALD) == 3,
                "store items must move the whole custom crafting grid into ME storage");
        helper.assertTrue(countPlayer(player, Items.DIAMOND) == 0 && countPlayer(player, Items.EMERALD) == 0,
                "store items must not move custom grid items into the player inventory");
        helper.assertTrue(grid.getStackInSlot(0).isEmpty() && grid.getStackInSlot(grid.size() - 1).isEmpty(),
                "store items must clear grid slots inserted into ME storage");

        grid.setItemDirect(0, new ItemStack(Items.GOLD_INGOT, 4));
        menu.clearToPlayerInventory(type.getCraftingInventory());
        helper.assertTrue(countPlayer(player, Items.GOLD_INGOT) == 4 && host.storage.count(Items.GOLD_INGOT) == 0,
                "take items must move the grid into the player inventory only");

        var fullHost = new TestHost(player, type, 0);
        var fullMenu = new ExtendedTerminalBaseMenu(BasicTerminalMenu.TYPE, 2, player.getInventory(), fullHost, type, TEST_CONFIG);
        fullMenu.broadcastChanges();
        fullHost.grid.setItemDirect(0, new ItemStack(Items.REDSTONE, 5));
        int redstoneBefore = countPlayer(player, Items.REDSTONE);
        fullMenu.doAction(player, InventoryAction.MOVE_REGION,
                fullMenu.getSlots(type.getSlotSemanticGrid()).get(0).index, 0);
        helper.assertTrue(fullHost.grid.getStackInSlot(0).getCount() == 5
                        && countPlayer(player, Items.REDSTONE) == redstoneBefore,
                "full ME storage must leave store items in the custom grid");
        helper.succeed();
    }

    private static void inventoryRoundTrip(GameTestHelper helper) {
        helper.assertTrue(AddTerminalEvent.didRun(), "Myotus wireless registration hook did not run");
        var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "et-wireless-test"));
        helper.assertTrue(CraftingTerminalHandler.getCraftingTerminalHandler(player).getTargetGrid() == null,
                "wireless handler without a terminal must not resolve a grid");
        var types = List.of(ETMenuType.EPIC_TERMINAL, ETMenuType.LEGENDARY_TERMINAL);
        var items = List.of((ItemWT) WTItems.WIRELESS_EPIC_TERMINAL.asItem(),
                (ItemWT) WTItems.WIRELESS_LEGENDARY_TERMINAL.asItem());
        var menus = List.of(ExtendedCraftingWTMenu.EPIC_TYPE, ExtendedCraftingWTMenu.LEGENDARY_TYPE);
        var markers = List.of(Items.DIAMOND, Items.EMERALD);
        var universal = new ItemStack(ForgeRegistries.ITEMS.getValue(
                new ResourceLocation(AE2wtlib.MOD_NAME, "wireless_universal_terminal")));

        for (int i = 0; i < types.size(); i++) {
            var type = types.get(i);
            var definition = WUTHandler.wirelessTerminals.get(type.getWTIdAsString());
            helper.assertTrue(definition != null, type + " missing from Myotus WUT registration");
            helper.assertTrue(definition.item() == items.get(i) && definition.menuType() == menus.get(i),
                    type + " WUT item/menu mapping");
            var standalone = new ItemStack(items.get(i));
            helper.assertTrue(items.get(i).getMenuType(standalone) == menus.get(i), type + " standalone menu mapping");
            universal.getOrCreateTag().putBoolean(type.getWTIdAsString(), true);

            for (var stack : List.of(standalone, universal)) {
                var host = definition.wTMenuHostFactory().create(player, null, stack, (p, menu) -> { });
                helper.assertTrue(host instanceof ExtendedCraftingWTHost craftingHost
                        && craftingHost.getTerminalType() == type, type + " WUT host factory");
                var grid = host.getSubInventory(type.getCraftingInventory());
                int size = i == 0 ? 121 : 169;
                helper.assertTrue(grid != null && grid.size() == size, type + " grid size");
                grid.setItemDirect(0, new ItemStack(markers.get(i), 1));
                grid.setItemDirect(size - 1, new ItemStack(markers.get(i), 3));
                host.getViewCellStorage().setItemDirect(4, new ItemStack(AEItems.VIEW_CELL));
                host.getSubInventory(WTMenuHost.INV_SINGULARITY).setItemDirect(0,
                        new ItemStack(AEItems.QUANTUM_ENTANGLED_SINGULARITY));

                var restoredStack = ItemStack.of(stack.save(new CompoundTag()));
                var restored = definition.wTMenuHostFactory().create(player, null, restoredStack, (p, menu) -> { });
                var restoredGrid = restored.getSubInventory(type.getCraftingInventory());
                helper.assertTrue(ItemStack.matches(grid.getStackInSlot(size - 1), restoredGrid.getStackInSlot(size - 1)),
                        type + " last grid slot must survive item NBT roundtrip");
                helper.assertTrue(restored.getViewCellStorage().getStackInSlot(4).is(AEItems.VIEW_CELL.asItem()),
                        type + " view cells must survive item NBT roundtrip");
                helper.assertTrue(restored.getSubInventory(WTMenuHost.INV_SINGULARITY).getStackInSlot(0)
                        .is(AEItems.QUANTUM_ENTANGLED_SINGULARITY.asItem()),
                        type + " singularity must survive item NBT roundtrip");
                var menu = new ExtendedCraftingWTMenu(menus.get(i), i, player.getInventory(),
                        (ExtendedCraftingWTHost) restored);
                helper.assertTrue(menu.getType() == menus.get(i)
                                && menu.getSlots(type.getSlotSemanticGrid()).size() == size
                                && menu.getCraftingMatrix() == restoredGrid,
                        type + " menu must expose every crafting slot from the wireless host");
            }
        }

        var restoredUniversal = ItemStack.of(universal.save(new CompoundTag()));
        for (int i = 0; i < types.size(); i++) {
            var type = types.get(i);
            var host = WUTHandler.wirelessTerminals.get(type.getWTIdAsString()).wTMenuHostFactory()
                    .create(player, null, restoredUniversal, (p, menu) -> { });
            var grid = host.getSubInventory(type.getCraftingInventory());
            helper.assertTrue(grid.getStackInSlot(0).is(markers.get(i))
                            && grid.getStackInSlot(grid.size() - 1).is(markers.get(i))
                            && grid.getStackInSlot(grid.size() - 1).getCount() == 3,
                    type + " grid must remain independent when both terminals share a WUT");
        }
        helper.succeed();
    }

    private static int countPlayer(Player player, net.minecraft.world.item.Item item) {
        int count = 0;
        for (var stack : player.getInventory().items) {
            if (stack.is(item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static final IETTerminalConfig TEST_CONFIG = new IETTerminalConfig() {
        @Override
        public boolean enableTerminal() {
            return true;
        }

        @Override
        public boolean enableCraftOnlyPowered() {
            return false;
        }

        @Override
        public double passiveDrainAE() {
            return 0;
        }
    };

    private static final class TestHost extends ItemMenuHost implements IPortableTerminal, ISegmentedInventory {
        private final ETMenuType type;
        private final TestStorage storage;
        private final AppEngInternalInventory grid;
        private final ConfigManager config = new ConfigManager(() -> { });

        private TestHost(Player player, ETMenuType type, long capacity) {
            super(player, null, new ItemStack(Items.CRAFTING_TABLE));
            this.type = type;
            this.storage = new TestStorage(capacity);
            this.grid = new AppEngInternalInventory(null, type.getGridSize());
        }

        @Override
        public MEStorage getInventory() {
            return storage;
        }

        @Override
        public InternalInventory getSubInventory(ResourceLocation id) {
            return type.getCraftingInventory().equals(id) ? grid : InternalInventory.empty();
        }

        @Override
        public double extractAEPower(double amt, Actionable mode, PowerMultiplier usePowerMultiplier) {
            return amt;
        }

        @Override
        public ConfigManager getConfigManager() {
            return config;
        }

        @Override
        public void returnToMainMenu(Player player, ISubMenu subMenu) {
        }

        @Override
        public ItemStack getMainMenuIcon() {
            return new ItemStack(Items.CRAFTING_TABLE);
        }
    }

    private static final class TestStorage implements MEStorage {
        private final long capacity;
        private final Map<AEKey, Long> amounts = new HashMap<>();

        private TestStorage(long capacity) {
            this.capacity = capacity;
        }

        @Override
        public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
            long accepted = Math.min(amount, capacity - amounts.values().stream().mapToLong(Long::longValue).sum());
            if (accepted > 0 && mode == Actionable.MODULATE) {
                amounts.merge(what, accepted, Long::sum);
            }
            return Math.max(accepted, 0);
        }

        @Override
        public void getAvailableStacks(KeyCounter out) {
            amounts.forEach(out::add);
        }

        private long count(net.minecraft.world.item.Item item) {
            return amounts.getOrDefault(AEItemKey.of(item), 0L);
        }

        @Override
        public Component getDescription() {
            return Component.literal("test");
        }
    }
}
