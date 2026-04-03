/**
 * Manages the game grid as a 2D array of Tiles.
 * Provides methods to place, remove, and query plants on the grid.
 */
public class Board
{
    private int rows;
    private int cols;
    private Tile[][] grid;

    /**
     * Creates a 5x9 grid of empty tiles.
     */
    public Board()
    {
        this.rows = 5;
        this.cols = 9;
        grid = new Tile[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Tile(r, c);
            }
        }
    }

    /**
     * Returns the tile at the given position, or null if out of bounds.
     * @param row the row index
     * @param col the column index
     * @return the Tile at that position, or null
     */
    public Tile getTile(int row, int col)
    {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            return grid[row][col];
        }
        return null;
    }

    /**
     * Places a plant on an empty tile at the given position.
     * @param plant the plant to place
     * @param row the row index
     * @param col the column index
     */
    public void placePlant(Plant plant, int row, int col)
    {
        if (row >= 0 && row < rows && col >= 0 && col < cols
            && !grid[row][col].hasPlant()) {
            grid[row][col].setPlant(plant);
        }
    }

    /**
     * Removes any plant from the tile at the given position.
     * @param row the row index
     * @param col the column index
     */
    public void removePlant(int row, int col)
    {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            grid[row][col].removePlant();
        }
    }

    /**
     * Checks whether a tile already has a living plant on it.
     * @param row the row index
     * @param col the column index
     * @return true if the tile contains an active plant
     */
    public boolean isTileOccupied(int row, int col)
    {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            return grid[row][col].hasPlant();
        }
        return false;
    }

    public int getRows()
    {
        return rows;
    }

    public int getCols()
    {
        return cols;
    }
}
