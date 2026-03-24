import java.util.List;
/**
 * Write a description of class Entity here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public abstract class Entity extends Game
{
    protected int hp;
    protected int row, col;
    protected int x, y;
    protected boolean alive;
    protected static final int DAMAGE = 25;
    
    public Entity(int hp, int row, int col, boolean alive)
    {
        this.hp = hp;
        this.row = row;
        this.col = col;
        this.alive = alive;
        this.x = col * Game.CELL_W + Game.GRID_X;
        this.y = row * Game.CELL_H + Game.GRID_Y;
    }
    
    protected void takeDamage(){
        hp -= DAMAGE;
        if (hp <= 0){
            setDead();
        }
    }
    
    protected boolean isAlive(){ return alive; }
    
    protected void setDead(){ alive = false; }
    
    public int getHp() { return hp; }
    public int getRow() { return row; }
    public int getCol() { return col;}
    public int getX() { return x;}
    public int getY() { return y;}  
}