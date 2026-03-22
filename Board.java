/**
 * Manages the game grid as a 2D array of Tiles.
 * Provides methods to place, remove, and query plants on the grid.
 */
public class Board extends Tile
{
    private int rows;
    private int cols;
    private Tile[][] grid;

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
     * Returns the tile at the given row and column.
     */
    public Tile getTile(int row, int col)
    {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            return grid[row][col];
        }
        return null;
    }

    /**
     * Places a plant on the grid at the certain position
     */
    public void placePlant(Plants plant, int row, int col)
    {
        if (row >= 0 && row < rows && col >= 0 && col < cols
            && !grid[row][col].hasPlant()) {
            grid[row][col].setPlant(plant);
        }
    }

    /**
     * Removes a plant from the grid at the certain position
     */
    public void removePlant(int row, int col)
    {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            grid[row][col].removePlant();
        }
    }

    /**
     * Checks whether a tile is occupied by a plant.
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
