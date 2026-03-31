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

    // Inside Sun.java

    public void draw(GraphicsContext gc) {
        // Call the new static method using this sun's coordinates
        drawSunIcon(gc, this.x, this.y, 1.0);
    }
    
    // New static method that can be called from anywhere
    public static void drawSunIcon(GraphicsContext gc, double cx, double cy, double scale) {
        double centerRadius = 14 * scale;
        double rayOuter = 22 * scale;   // only a bit past the circle
        int rayCount = 12;      // more rays = softer shape
        double spread = 0.32;   // wider triangles = less pointy
    
        // outer soft rays
        gc.setFill(Color.GOLD);
    
        for (int i = 0; i < rayCount; i++) {
            double angle = 2 * Math.PI * i / rayCount;
    
            double tipX = cx + Math.cos(angle) * rayOuter;
            double tipY = cy + Math.sin(angle) * rayOuter;
    
            double leftX = cx + Math.cos(angle - spread) * centerRadius;
            double leftY = cy + Math.sin(angle - spread) * centerRadius;
    
            double rightX = cx + Math.cos(angle + spread) * centerRadius;
            double rightY = cy + Math.sin(angle + spread) * centerRadius;
    
            gc.fillPolygon(
                new double[]{tipX, leftX, rightX},
                new double[]{tipY, leftY, rightY},
                3
            );
        }
    
        // main body
        gc.setFill(Color.GOLD);
        gc.fillOval(cx - centerRadius, cy - centerRadius, centerRadius * 2, centerRadius * 2);
    
        // Scale the inner highlight too
        double innerHighlight = 9 * scale;
        gc.fillOval(cx - innerHighlight, cy - innerHighlight, innerHighlight * 2, innerHighlight * 2);  
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