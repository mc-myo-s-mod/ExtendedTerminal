package me.myogoo.extendedterminal.gametest;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.api.storage.StorageCells;
import appeng.menu.locator.MenuLocators;
import com.mojang.authlib.GameProfile;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import me.myogoo.extendedterminal.menu.extendedterminal.FakeAnvilMenu;
import me.myogoo.myotus.api.experience.ExperienceMath.ExperienceSource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.UUID;

@GameTestHolder(ExtendedTerminal.MODID)
@PrefixGameTestTemplate(false)
public final class OptionalExperienceGameTests {
    @GameTestGenerator
    public static List<TestFunction> tests() {
        // This explicit dev suite fails on missing dependencies instead of silently dropping fixtures.
        if (!Boolean.getBoolean("extendedterminal.test.optionalExperience")) return List.of();
        return List.of(
                new TestFunction("extendedterminal", "optional_apothic_anvil", "extendedterminal:empty",
                        100, 0, true, OptionalExperienceGameTests::apothicAnvil),
                new TestFunction("extendedterminal", "optional_applied_experienced_cell", "extendedterminal:empty",
                        100, 0, true, OptionalExperienceGameTests::experienceCell));
    }

    private static void requireMods(GameTestHelper helper) {
        for (var id : List.of("apothic_enchanting", "appex", "experiencelib", "ae2wtlib")) {
            helper.assertTrue(ModList.get().isLoaded(id), "Optional XP suite requires actual mod " + id);
        }
    }

    private static ItemStack input() {
        var stack = new ItemStack(Items.DIAMOND);
        stack.set(DataComponents.REPAIR_COST, 4);
        return stack;
    }

    private static FakePlayer player(GameTestHelper helper, int xp) {
        var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "et-optional-xp"));
        player.giveExperiencePoints(xp);
        return player;
    }

    private static void apothicAnvil(GameTestHelper helper) {
        requireMods(helper);
        var vanillaPlayer = player(helper, 173);
        var vanilla = new FakeAnvilMenu(0, vanillaPlayer.getInventory());
        vanilla.getInputSlots().setItem(0, input());
        vanilla.setItemName("Optional XP");
        helper.assertTrue(vanilla.getCost() == 5 && !vanilla.getResultItem().isEmpty(), "Real anvil costs five levels");
        vanilla.ET$OnTake(vanillaPlayer, vanilla.getResultItem().copy());
        helper.assertTrue(vanillaPlayer.totalExperience == 118, "Actual Apothic mixin debits exactly 55 XP");

        var player = player(helper, 54);
        var fixture = new Fixture(player, appeng.me.storage.NullInventory.of());
        var menu = fixture.menu(player);
        var before = fixture.getSubInventory(ETTerminalMode.ANVIL.getInventoryId()).getStackInSlot(0).copy();
        helper.assertTrue(!menu.canPayAnvilCost(player) && !menu.consumeAnvilExperience(player)
                        && player.totalExperience == 54 && player.experienceLevel == 4
                        && ItemStack.matches(before, fixture.getSubInventory(ETTerminalMode.ANVIL.getInventoryId()).getStackInSlot(0)),
                "One XP short refuses without mutating player or anvil input");
        player.giveExperiencePoints(119);
        helper.assertTrue(menu.canPayAnvilCost(player) && player.totalExperience == 173
                        && menu.consumeAnvilExperience(player) && player.totalExperience == vanillaPlayer.totalExperience,
                "ET simulation is non-mutating and debit matches the actual Apothic anvil");
        player.getAbilities().instabuild = true;
        helper.assertTrue(menu.consumeAnvilExperience(player) && player.totalExperience == 118,
                "Creative bypass preserves XP");
        helper.succeed();
    }

    private static void experienceCell(GameTestHelper helper) {
        requireMods(helper);
        final AEKey key;
        try {
            key = (AEKey) Class.forName("es.degrassi.appexp.me.key.AEExperienceKey").getField("KEY").get(null);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Actual Applied Experienced key is required", e);
        }
        var stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("appex", "experience_storage_cell_1k")));
        var cell = StorageCells.getCellInventory(stack, null);
        helper.assertTrue(cell != null && cell.getClass().getName().equals("es.degrassi.appexp.me.cell.ExperienceCellInventory"),
                "Fixture uses the actual registered Applied Experienced storage cell");
        var player = player(helper, 0);
        var source = IActionSource.ofPlayer(player);
        helper.assertTrue(cell.insert(key, 56, Actionable.MODULATE, source) == 56, "Seed actual cell with raw XP");
        var storage = new CellStorage(cell);
        var fixture = new Fixture(player, storage);
        var menu = fixture.menu(player);
        menu.cycleAnvilExperienceSourcePriority();
        helper.assertTrue(menu.getSelectedAnvilExperienceSource() == ExperienceSource.APPLIED_EXPERIENCED_AMOUNT,
                "Actual XP cell is offered and selected");
        var before = stack.copy();
        helper.assertTrue(menu.canPayAnvilCost(player) && ItemStack.matches(before, stack), "Simulation preserves cell components");
        var output = menu.getSlots(ETTerminalMode.ANVIL.getOutputSlotSemantics()).getFirst();
        helper.assertTrue(output.mayPickup(player), "Paid output may be taken");
        var result = output.remove(1);
        output.onTake(player, result);
        helper.assertTrue(result.is(Items.DIAMOND) && cell.extract(key, Long.MAX_VALUE, Actionable.SIMULATE, source) == 1
                        && player.totalExperience == 0 && player.experienceLevel == 0
                        && fixture.getSubInventory(ETTerminalMode.ANVIL.getInventoryId()).getStackInSlot(0).isEmpty(),
                "Actual output take consumes input and exactly 55 cell XP, without double charging");
        helper.assertTrue(StorageCells.getCellInventory(stack.copy(), null).extract(key, 100, Actionable.SIMULATE, source) == 1,
                "Actual cell persists the remaining XP on reopen");

        fixture.getSubInventory(ETTerminalMode.ANVIL.getInventoryId()).setItemDirect(0, input());
        menu = fixture.menu(player);
        cell.insert(key, 53, Actionable.MODULATE, source);
        before = stack.copy();
        helper.assertTrue(!menu.canPayAnvilCost(player) && !menu.consumeAnvilExperience(player)
                        && ItemStack.matches(before, stack) && player.totalExperience == 0,
                "54 raw cell XP refuses with no mutation");
        for (boolean powerDenied : new boolean[]{false, true}) {
            cell.insert(key, 2, Actionable.MODULATE, source);
            storage.allowExtraction = powerDenied;
            fixture.powered = !powerDenied;
            before = stack.copy();
            var deniedOutput = menu.getSlots(ETTerminalMode.ANVIL.getOutputSlotSemantics()).getFirst();
            var beforeOutput = deniedOutput.getItem().copy();
            helper.assertTrue(!menu.canPayAnvilCost(player) && !menu.consumeAnvilExperience(player)
                            && !deniedOutput.mayPickup(player)
                            && menu.getSelectedAnvilExperienceSource() == ExperienceSource.APPLIED_EXPERIENCED_AMOUNT
                            && ItemStack.matches(beforeOutput, deniedOutput.getItem())
                            && ItemStack.matches(before, stack) && player.totalExperience == 0
                            && ItemStack.matches(input(), fixture.getSubInventory(ETTerminalMode.ANVIL.getInventoryId()).getStackInSlot(0)),
                    "Stale cell availability cannot bypass denied extraction/power or mutate cell, input, output or player");
        }
        fixture.powered = true;
        storage.allowExtraction = true;
        cell.extract(key, Long.MAX_VALUE, Actionable.MODULATE, source);
        cell.insert(key, 35, Actionable.MODULATE, source);
        player.giveExperiencePoints(20);
        helper.assertTrue(menu.canPayAnvilCost(player) && player.totalExperience == 20
                        && menu.consumeAnvilExperience(player) && player.totalExperience == 0
                        && cell.extract(key, 100, Actionable.SIMULATE, source) == 0,
                "Mixed player/cell payment consumes exactly 20 + 35 raw XP");

        cell.insert(key, 56, Actionable.MODULATE, source);
        fixture.setRememberedAnvilExperienceSource(ExperienceSource.APPLIED_EXPERIENCED_AMOUNT);
        storage.availableStackReads = 0;
        storage.largestSimulatedExtraction = 0;
        var availabilityMenu = fixture.menu(player);
        helper.assertTrue(storage.availableStackReads == 2 && storage.largestSimulatedExtraction == 1
                        && availabilityMenu.getSelectedAnvilExperienceSource() == ExperienceSource.APPLIED_EXPERIENCED_AMOUNT,
                "Opening a menu immediately probes each source once for one XP and restores the remembered source");
        availabilityMenu.getSelectedAnvilExperienceSourceLabelKey();
        helper.assertTrue(storage.availableStackReads == 2, "Displayed source getters reuse the availability snapshot");
        availabilityMenu.broadcastChanges();
        availabilityMenu.broadcastChanges();
        helper.assertTrue(storage.availableStackReads == 4,
                "Same-tick broadcasts retain only AE2's one storage read per broadcast");

        cell.extract(key, Long.MAX_VALUE, Actionable.MODULATE, source);
        var staleOutput = availabilityMenu.getSlots(ETTerminalMode.ANVIL.getOutputSlotSemantics()).getFirst();
        var beforeOutput = staleOutput.getItem().copy();
        helper.assertTrue(availabilityMenu.getSelectedAnvilExperienceSource() == ExperienceSource.APPLIED_EXPERIENCED_AMOUNT
                        && !staleOutput.mayPickup(player) && !availabilityMenu.consumeAnvilExperience(player)
                        && ItemStack.matches(beforeOutput, staleOutput.getItem()) && player.totalExperience == 0
                        && ItemStack.matches(input(), fixture.getSubInventory(ETTerminalMode.ANVIL.getInventoryId()).getStackInSlot(0)),
                "A stale available display cannot pay after XP disappears or mutate input, output or player");
        storage.availableStackReads = 0;
        long openedAt = helper.getLevel().getGameTime();
        helper.runAfterDelay(9, () -> {
            helper.assertTrue(helper.getLevel().getGameTime() == openedAt + 9, "Advance nine actual game ticks");
            availabilityMenu.broadcastChanges();
            helper.assertTrue(storage.availableStackReads == 1
                            && availabilityMenu.getSelectedAnvilExperienceSource() == ExperienceSource.APPLIED_EXPERIENCED_AMOUNT,
                    "Nine ticks retain the snapshot, while AE2 still broadcasts");
        });
        helper.runAfterDelay(10, () -> {
            helper.assertTrue(helper.getLevel().getGameTime() == openedAt + 10, "Advance ten actual game ticks");
            availabilityMenu.broadcastChanges();
            helper.assertTrue(storage.availableStackReads == 4
                            && availabilityMenu.getSelectedAnvilExperienceSource() == ExperienceSource.PLAYER,
                    "The tenth tick refreshes both XP sources plus AE2 and removes the unavailable cell");
            availabilityMenu.broadcastChanges();
            helper.assertTrue(storage.availableStackReads == 5, "The refreshed tick does not repeat XP probes");

            cell.insert(key, 56, Actionable.MODULATE, source);
            storage.availableStackReads = 0;
            availabilityMenu.cycleAnvilExperienceSourcePriority();
            helper.assertTrue(storage.availableStackReads == 2
                            && availabilityMenu.getSelectedAnvilExperienceSource() == ExperienceSource.APPLIED_EXPERIENCED_AMOUNT
                            && fixture.getRememberedAnvilExperienceSource() == ExperienceSource.APPLIED_EXPERIENCED_AMOUNT,
                    "Cycling immediately refreshes new XP and selects/remembers it without repeated probes");
            cell.extract(key, Long.MAX_VALUE, Actionable.MODULATE, source);
            availabilityMenu.setMode(ETTerminalMode.CRAFTING);
            storage.availableStackReads = 0;
            availabilityMenu.setMode(ETTerminalMode.ANVIL);
            helper.assertTrue(storage.availableStackReads == 2
                            && availabilityMenu.getSelectedAnvilExperienceSource() == ExperienceSource.PLAYER,
                    "Entering anvil mode immediately refreshes disappearing XP");
            availabilityMenu.broadcastChanges();
            helper.assertTrue(storage.availableStackReads == 3, "Forced refresh postpones duplicate periodic probes");
            helper.succeed();
        });
    }

    private static final class Fixture extends ETWTHost {
        private final MEStorage storage;
        private boolean powered = true;
        private Fixture(FakePlayer player, MEStorage storage) {
            this(player, storage, new ItemStack(WTDefinition.of(ETMenuType.ET_TERMINAL.getWTIdAsString()).item()));
        }
        private Fixture(FakePlayer player, MEStorage storage, ItemStack stack) {
            super((ItemWT) stack.getItem(), player, MenuLocators.forStack(stack), (p, menu) -> {});
            this.storage = storage;
            setMode(ETTerminalMode.ANVIL);
            getSubInventory(ETTerminalMode.ANVIL.getInventoryId()).setItemDirect(0, input());
        }
        @Override public MEStorage getInventory() { return storage; }
        @Override public double extractAEPower(double amount, Actionable mode, PowerMultiplier multiplier) {
            return powered ? amount : 0;
        }
        private ETTerminalMenu menu(FakePlayer player) {
            var menu = new ETTerminalMenu(ETTerminalMenu.TYPE, 0, player.getInventory(), this) {
                @Override protected boolean checkCraftingOnlyActive() { return false; }
            };
            menu.setAnvilItemName("Optional XP");
            if (menu.getAnvilCost() != 5) throw new AssertionError("Fixture must cost five levels");
            return menu;
        }
    }

    // Only the network permission boundary is simulated; all cell reads/writes use the real mod inventory.
    private static final class CellStorage implements MEStorage {
        private final MEStorage cell;
        private boolean allowExtraction = true;
        private int availableStackReads;
        private long largestSimulatedExtraction;
        private CellStorage(MEStorage cell) { this.cell = cell; }
        @Override public long insert(AEKey key, long amount, Actionable mode, IActionSource source) {
            return cell.insert(key, amount, mode, source);
        }
        @Override public long extract(AEKey key, long amount, Actionable mode, IActionSource source) {
            if (mode == Actionable.SIMULATE) largestSimulatedExtraction = Math.max(largestSimulatedExtraction, amount);
            return allowExtraction ? cell.extract(key, amount, mode, source) : 0;
        }
        @Override public void getAvailableStacks(KeyCounter out) {
            availableStackReads++;
            cell.getAvailableStacks(out);
        }
        @Override public net.minecraft.network.chat.Component getDescription() { return cell.getDescription(); }
    }
}
