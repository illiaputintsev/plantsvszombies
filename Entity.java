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
    protected static final int DAMAGE = 19;
    
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
    
    protected void takeDamage(){
        hp -= DAMAGE;
        if (hp <= 0){
            setDead();
        }
    }

    protected void takeHit(int damage){
        hp -= damage;
        if (hp <= 0){
            setDead();
        }
    }
    
    protected boolean isAlive(){ return alive; }
    
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