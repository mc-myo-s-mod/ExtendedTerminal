package me.myogoo.extendedterminal.util;

//TODO: Refactor
public final class TableCraftingHelper {
    private static int getCraftingGridSideLength(int gridSize) {
        int width = (int) Math.sqrt(gridSize);
        if (width * width < gridSize) {
            // may not invalid?
            width++;
        }
        return width;
    }

    public static GridCoordinate indexToCoordinate(int gridSize,int width, int height) {
        return new GridCoordinate(getCraftingGridSideLength(gridSize), width, height);
    }

    public static class GridCoordinate {
        private final int sideLength;
        private final boolean[][] matrix;

        public GridCoordinate(int sideLength, int width, int height) {
            this.sideLength = sideLength;

            matrix = new boolean[sideLength][sideLength];
            int offsetX = Math.floorDiv(sideLength - width,2);
            int offsetY = Math.floorDiv(sideLength - height,2);
            for(int y = offsetY; y < offsetY + height; y++) {
                for(int x = offsetX; x < offsetX + width; x++) {
                    matrix[x][y] = true;
                }
            }
        }

        public boolean test(int index) {
            if(index < 0 || index > sideLength * sideLength) { return false; }
            return matrix[index % sideLength][index / sideLength];
        }
    }
}