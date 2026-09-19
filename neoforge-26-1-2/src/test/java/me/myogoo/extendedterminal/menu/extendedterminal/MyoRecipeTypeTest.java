package me.myogoo.extendedterminal.menu.extendedterminal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class MyoRecipeTypeTest {
    @Test
    void unknownSavedRecipeTypeFallsBackToVanilla() {
        assertSame(MyoRecipeType.VANILLA, MyoRecipeType.fromSavedName("EXTREME_MEO"));
        assertSame(MyoRecipeType.VANILLA, MyoRecipeType.fromSavedName("unknown"));
        assertSame(MyoRecipeType.BASIC, MyoRecipeType.fromSavedName("BASIC"));
    }
}
