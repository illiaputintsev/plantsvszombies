/**
 * Represents a single cell on the game board.
 * Each tile can hold one plant.
 */
public class Tile
{
    private int row;
    private int col;
    private Plants plant;

    public Tile()
    {
        this.row = 0;
        this.col = 0;
        this.plant = null;
    }

    public Tile(int row, int col)
    {
        this.row = row;
        this.col = col;
        this.plant = null;
    }

    public void setPlant(Plants plant)
    {
        this.plant = plant;
    }

    public void removePlant()
    {
        this.plant = null;
    }

    public boolean hasPlant()
    {
        return plant != null && plant.isAlive();
    }

    public Plants getPlant()
    {
        return plant;
    }
}
