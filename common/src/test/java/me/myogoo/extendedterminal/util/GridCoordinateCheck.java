package me.myogoo.extendedterminal.util;

public final class GridCoordinateCheck {
    public static void main(String[] args) {
        for (int side : new int[]{3, 5, 7, 9, 11, 13}) {
            for (int width = 0; width <= side; width++) {
                for (int height = 0; height <= side; height++) {
                    var coordinate = TableCraftingHelper.indexToCoordinate(side * side, width, height);
                    int left = Math.floorDiv(side - width, 2);
                    int top = Math.floorDiv(side - height, 2);
                    int count = 0;
                    for (int index = -1; index <= side * side; index++) {
                        int x = index % side;
                        int y = index / side;
                        boolean expected = index >= 0 && index < side * side
                                && x >= left && x < left + width && y >= top && y < top + height;
                        assert coordinate.test(index) == expected : side + ":" + width + ":" + height + ":" + index;
                        if (expected) count++;
                    }
                    assert count == width * height;
                }
            }
        }
        System.out.println("GridCoordinate boundaries/rectangles PASS (sides 3/5/7/9/11/13)");
    }
}
