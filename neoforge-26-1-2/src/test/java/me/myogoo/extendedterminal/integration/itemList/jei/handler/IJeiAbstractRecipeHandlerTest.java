package me.myogoo.extendedterminal.integration.itemList.jei.handler;

import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.tags.TagKey;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class IJeiAbstractRecipeHandlerTest {
    @Test
    void sparseGridKeysFollowNonEmptyJeiSlotsInOrder() {
        var empty = slot(true);
        var first = slot(false);
        var second = slot(false);
        var third = slot(false);
        var slots = List.of(empty, first, empty, second, empty, third);

        var mapped = IJeiAbstractRecipeHandler.getInputSlotViewsByKey(slots, Set.of(40, 10, 12));
        assertEquals(Set.of(10, 12, 40), mapped.keySet());
        assertSame(first, mapped.get(10));
        assertSame(second, mapped.get(12));
        assertSame(third, mapped.get(40));

        var indexed = IJeiAbstractRecipeHandler.getInputSlotViewsByKey(slots, Set.of());
        assertEquals(slots.size(), indexed.size());
        assertSame(empty, indexed.get(0));
        assertSame(first, indexed.get(1));
        assertSame(third, indexed.get(5));
    }

    private static IRecipeSlotView slot(boolean empty) {
        return new IRecipeSlotView() {
            @Override
            public boolean isEmpty() {
                return empty;
            }

            @Override
            public Stream<ITypedIngredient<?>> getAllIngredients() {
                throw new UnsupportedOperationException();
            }

            @Override
            public List<ITypedIngredient<?>> getAllIngredientsList() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Optional<ITypedIngredient<?>> getDisplayedIngredient() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Stream<ITypedIngredient<?>> getDisplayedIngredients() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Optional<TagKey<?>> getTagKey() {
                throw new UnsupportedOperationException();
            }

            @Override
            public RecipeIngredientRole getRole() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void drawHighlight(GuiGraphicsExtractor graphics, int color) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Optional<String> getSlotName() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
