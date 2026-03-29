import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Sun {
    private double x, y;
    private final int value;
    private double lifetime;
    private boolean alive;
    private final boolean falling;
    private final double fallSpeed;
    private final double targetY;

    public Sun(double x, double y, int value, boolean falling) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.lifetime = 10.0;
        this.alive = true;
        this.falling = falling;
        this.fallSpeed = 40;
        this.targetY = y + 80;
    }

    public void update(double deltaTime) {
        lifetime -= deltaTime;
        if (lifetime <= 0) {
            alive = false;
        }

        if (falling && y < targetY) {
            y += fallSpeed * deltaTime;
            if (y > targetY) {
                y = targetY;
            }
        }
    }

    public void draw(GraphicsContext gc) {
        double centerRadius = 14;
        double rayOuter = 22;   // only a bit past the circle
        int rayCount = 12;      // more rays = softer shape
        double spread = 0.32;   // wider triangles = less pointy
    
        // outer soft rays
        gc.setFill(Color.GOLD);
    
        for (int i = 0; i < rayCount; i++) {
            double angle = 2 * Math.PI * i / rayCount;
    
            double tipX = x + Math.cos(angle) * rayOuter;
            double tipY = y + Math.sin(angle) * rayOuter;
    
            double leftX = x + Math.cos(angle - spread) * centerRadius;
            double leftY = y + Math.sin(angle - spread) * centerRadius;
    
            double rightX = x + Math.cos(angle + spread) * centerRadius;
            double rightY = y + Math.sin(angle + spread) * centerRadius;
    
            gc.fillPolygon(
                new double[]{tipX, leftX, rightX},
                new double[]{tipY, leftY, rightY},
                3
            );
        }
    
        // main body
        gc.setFill(Color.GOLD);
        gc.fillOval(x - centerRadius, y - centerRadius, centerRadius * 2, centerRadius * 2);
    
        // soft inner highlight
        gc.setFill(Color.GOLD);
        gc.fillOval(x - 9, y - 9, 18, 18);
    }

    public boolean isClicked(double mouseX, double mouseY) {
        double dx = mouseX - x;
        double dy = mouseY - y;
        return dx * dx + dy * dy <= 225;
    }

    public int collect() {
        alive = false;
        return value;
    }

    public boolean isAlive() {
        return alive;
    }
}