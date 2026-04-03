import java.util.List;
/**
 * Base class for all game entities (plants and zombies).
 */
public abstract class Entity
{
    protected int hp;
    protected int maxHp;
    protected int row, col;
    protected boolean alive;
    protected double x;
    protected double y;
    protected static final int DAMAGE = 25;
    
    public Entity(int hp, int row, int col, boolean alive)
    {
        this.hp = hp;
        this.maxHp = hp;
        this.row = row;
        this.col = col;
        this.alive = alive;
        // Center position in the cell (consistent for both plants and zombies)
        this.x = Game.colToPixelX(col);
        this.y = Game.rowToPixelY(row);
    }
    
    /**
     * Reduces HP by the standard damage amount. Kills the entity if HP reaches zero.
     */ 
    protected void takeDamage(){
        hp -= DAMAGE;
        if (hp <= 0){
            setDead();
        }
    }

    /**
     * Reduces HP by a specified amount. Kills the entity if HP reaches zero.
     * @param damage the amount of HP to subtract
     */
    protected void takeHit(int damage){
        hp -= damage;
        if (hp <= 0){
            setDead();
        }
    }
    
    /**
     * Checks whether this entity is still alive.
     * @return true if the entity has not been killed
     */
    protected boolean isAlive(){ return alive; }
    
    /**
     * Marks this entity as dead.
     */
    protected void setDead(){ alive = false; }
    
    public int getHp()       { 
        return hp; 
    }
    public int getMaxHp()    { 
        return maxHp; 
    }
    public int getRow()      { 
        return row; 
    }
    public int getCol()      { 
        return col; 
    }
    public double getX()     {
        return x; 
    }
    public double getY()     { 
        return y; 
    }
    public void setX(double x) { 
        this.x = x; 
    }
    public void setY(double y) { 
        this.y = y; 
    }
}