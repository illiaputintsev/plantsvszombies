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
    private boolean collecting;
    private double scale;

    public Sun(double x, double y, int value, boolean falling) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.lifetime = 10.0;
        this.alive = true;
        this.falling = falling;
        this.fallSpeed = 40;
        this.targetY = y + 80;
        this.collecting = false;
        this.scale = 1.0;
    }

    public void update(double deltaTime) {
        if (collecting) {
            // Move toward the sun counter
            double speed = 1200;
            double dx = 20 - x;
            double dy = 27 - y;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < 10) {
                alive = false;
                return;
            }

            x += (dx / dist) * speed * deltaTime;
            y += (dy / dist) * speed * deltaTime;
            scale -= deltaTime * 2;
            if (scale < 0.3) scale = 0.3;
            return;
        }

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
        drawSunIcon(gc, this.x, this.y, this.scale);
    }

    public static void drawSunIcon(GraphicsContext gc, double cx, double cy, double scale) {
        double centerRadius = 14 * scale;
        double rayOuter = 22 * scale;
        int rayCount = 12;
        double spread = 0.32;

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

        gc.setFill(Color.GOLD);
        gc.fillOval(cx - centerRadius, cy - centerRadius, centerRadius * 2, centerRadius * 2);

        double innerHighlight = 9 * scale;
        gc.fillOval(cx - innerHighlight, cy - innerHighlight, innerHighlight * 2, innerHighlight * 2);
    }

    public boolean isClicked(double mouseX, double mouseY) {
        if (collecting) return false;
        double dx = mouseX - x;
        double dy = mouseY - y;
        return dx * dx + dy * dy <= 225;
    }

    public int collect() {
        collecting = true;
        return value;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isCollecting() {
        return collecting;
    }
}
