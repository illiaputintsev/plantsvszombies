/**
 * Represents a single cell on the game board.
 * Each tile can hold one plant.
 */
public class Tile
{
    private int row;
    private int col;
    private Plant plant;

    /**
     * Creates an empty tile at row 0, column 0.
     */
    public Tile()
    {
        this.row = 0;
        this.col = 0;
        this.plant = null;
    }

    /**
     * Creates an empty tile at the given grid position.
     * @param row the row index
     * @param col the column index
     */
    public Tile(int row, int col)
    {
        this.row = row;
        this.col = col;
        this.plant = null;
    }

    /**
     * Places a plant on this tile.
     * @param plant the plant to assign
     */
    public void setPlant(Plant plant)
    {
        this.plant = plant;
    }

    /**
     * Removes the plant from this tile.
     */
    public void removePlant()
    {
        this.plant = null;
    }

    /**
     * Checks whether this tile has a living plant.
     * @return true if a plant is present and alive
     */
    public boolean hasPlant()
    {
        return plant != null && plant.isAlive();
    }

    /**
     * Returns the plant on this tile, or null if empty.
     * @return the plant, or null
     */
    public Plant getPlant()
    {
        return plant;
    }
}
