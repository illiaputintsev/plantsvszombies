import java.util.List;
/**
 * Write a description of class Entity here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public abstract class Entity extends Game
{
    int hp;
    int row;
    int col;
    boolean alive;
    int DAMAGE;
    
    public Entity(int hp, int cost, boolean alive)
    {
        DAMAGE = 25;
    }
    
    protected int takeDamage(){
        return DAMAGE;
    }
    
    protected boolean isAlvie(){
        return alive;
    }
    
    protected void setDead(){
        alive = false;
    }
}