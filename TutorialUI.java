import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.shape.ArcType;

/**
 * TutorialUI - renders a slideshow overlay teaching the player how to play.
 * @author - Mark
 */
public class TutorialUI {

    private int currentSlide = 0;
    private static final int TOTAL_SLIDES = 5;
    private boolean visible = false;
    private Runnable onClose;

    // popup dimensions
    private static final double POP_W = 700;
    private static final double POP_H = 420;
    private double popX;
    private double popY;

    // nav button areas
    private static final double ARROW_SIZE = 36;
    private static final double CLOSE_SIZE = 28;

    public TutorialUI(double screenW, double screenH, Runnable onClose) {
        this.popX = (screenW - POP_W) / 2.0;
        this.popY = (screenH - POP_H) / 2.0;
        this.onClose = onClose;
    }

    public void open() {
        currentSlide = 0;
        visible = true;
    }

    public boolean isVisible() {
        return visible;
    }

    // returns true if the click was consumed by the tutorial overlay
    public boolean handleClick(double mx, double my) {
        if (!visible) return false;

        // close button (top-right corner of popup)
        double closeX = popX + POP_W - CLOSE_SIZE - 10;
        double closeY = popY + 10;
        if (mx >= closeX && mx <= closeX + CLOSE_SIZE && my >= closeY && my <= closeY + CLOSE_SIZE) {
            visible = false;
            if (onClose != null) onClose.run();
            SoundManager.playMenuBtn();
            return true;
        }

        // left arrow
        double arrowY = popY + POP_H - 55;
        if (currentSlide > 0) {
            double leftX = popX + 20;
            if (mx >= leftX && mx <= leftX + ARROW_SIZE && my >= arrowY && my <= arrowY + ARROW_SIZE) {
                currentSlide--;
                SoundManager.playMenuBtn();
                return true;
            }
        }

        // right arrow
        if (currentSlide < TOTAL_SLIDES - 1) {
            double rightX = popX + POP_W - ARROW_SIZE - 20;
            if (mx >= rightX && mx <= rightX + ARROW_SIZE && my >= arrowY && my <= arrowY + ARROW_SIZE) {
                currentSlide++;
                SoundManager.playMenuBtn();
                return true;
            }
        }

        return true; // consume all clicks when overlay is open
    }

    public void draw(GraphicsContext gc) {
        if (!visible) return;

        // dim background
        gc.setFill(Color.color(0, 0, 0, 0.55));
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // popup panel
        gc.setFill(Color.rgb(50, 90, 50));
        gc.fillRoundRect(popX, popY, POP_W, POP_H, 18, 18);
        gc.setStroke(Color.rgb(200, 200, 100));
        gc.setLineWidth(3);
        gc.strokeRoundRect(popX, popY, POP_W, POP_H, 18, 18);

        // close button (X)
        double closeX = popX + POP_W - CLOSE_SIZE - 10;
        double closeY = popY + 10;
        gc.setFill(Color.rgb(180, 50, 50));
        gc.fillRoundRect(closeX, closeY, CLOSE_SIZE, CLOSE_SIZE, 6, 6);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("X", closeX + CLOSE_SIZE / 2.0, closeY + 20);

        // slide dots
        double dotsY = popY + POP_H - 20;
        for (int i = 0; i < TOTAL_SLIDES; i++) {
            gc.setFill(i == currentSlide ? Color.YELLOW : Color.GRAY);
            gc.fillOval(popX + POP_W / 2.0 - TOTAL_SLIDES * 8 + i * 16, dotsY, 8, 8);
        }

        // navigation arrows
        double arrowY = popY + POP_H - 55;

        if (currentSlide > 0) {
            drawArrow(gc, popX + 20, arrowY, true);
        }
        if (currentSlide < TOTAL_SLIDES - 1) {
            drawArrow(gc, popX + POP_W - ARROW_SIZE - 20, arrowY, false);
        }

        // draw slide content
        gc.setTextAlign(TextAlignment.CENTER);
        switch (currentSlide) {
            case 0: drawSlide1(gc); break;
            case 1: drawSlide2(gc); break;
            case 2: drawSlide3(gc); break;
            case 3: drawSlide4(gc); break;
            case 4: drawSlide5(gc); break;
        }
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setLineWidth(1);
    }

    private void drawArrow(GraphicsContext gc, double ax, double ay, boolean left) {
        gc.setFill(Color.rgb(80, 80, 80, 0.7));
        gc.fillRoundRect(ax, ay, ARROW_SIZE, ARROW_SIZE, 8, 8);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(left ? "<" : ">", ax + ARROW_SIZE / 2.0, ay + 26);
    }

    private void drawSlideText(GraphicsContext gc, String text) {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        gc.setTextAlign(TextAlignment.CENTER);

        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        double ty = popY + 50;
        for (String w : words) {
            if (line.length() + w.length() + 1 > 55 && line.length() > 0) {
                gc.fillText(line.toString(), popX + POP_W / 2.0, ty);
                ty += 24;
                line = new StringBuilder();
            }
            if (line.length() > 0) line.append(" ");
            line.append(w);
        }
        if (line.length() > 0) gc.fillText(line.toString(), popX + POP_W / 2.0, ty);
    }

    private void drawSlide1(GraphicsContext gc) {
        drawSlideText(gc, "Zombies have discovered where you live! Do not let them inside your house!");

        double baseX = popX + 60;
        double baseY = popY + 110;

        // grass ground
        gc.setFill(Color.rgb(100, 180, 80));
        gc.fillRect(popX + 20, baseY + 130, POP_W - 40, 75);

        // house
        drawMiniHouse(gc, baseX, baseY);

        // zombies walking toward the house
        drawMiniZombie(gc, baseX + 350, baseY + 140);
        drawMiniZombie(gc, baseX + 440, baseY + 170);
        drawMiniZombie(gc, baseX + 520, baseY + 130);
    }

    private void drawSlide2(GraphicsContext gc) {
        drawSlideText(gc, "Catch falling suns from the sky. Plant sunflowers to generate more suns!");

        double fieldX = popX + 50;
        double fieldY = popY + 170;

        // mini field (2 rows x 4 cols)
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 4; c++) {
                gc.setFill((r + c) % 2 == 0 ? Color.PALEGREEN : Color.SEAGREEN);
                gc.fillRect(fieldX + c * 70, fieldY + r * 70, 70, 70);
            }
        }

        // sunflowers
        drawMiniSunflower(gc, fieldX + 35, fieldY + 40);
        drawMiniSunflower(gc, fieldX + 105, fieldY + 110);

        // sun counter
        double boxX = popX + 410;
        double boxY = popY + 186;
        gc.setFill(Color.GOLDENROD);
        gc.fillRoundRect(boxX, boxY, 240, 108, 24, 24);
        Sun.drawSunIcon(gc, boxX + 54, boxY + 54, 1.8);
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(45));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("150", boxX + 114, boxY + 69);
    }

    private void drawSlide3(GraphicsContext gc) {
        drawSlideText(gc, "Use suns to buy defensive plants. They will kill all approaching zombies on their lane!");

        double fieldX = popX + 80;
        double fieldY = popY + 150;

        // two lane strips
        for (int c = 0; c < 6; c++) {
            gc.setFill(c % 2 == 0 ? Color.PALEGREEN : Color.SEAGREEN);
            gc.fillRect(fieldX + c * 90, fieldY, 90, 90);
            gc.setFill(c % 2 == 0 ? Color.SEAGREEN : Color.PALEGREEN);
            gc.fillRect(fieldX + c * 90, fieldY + 90, 90, 90);
        }

        // repeater on lane 1
        drawMiniRepeater(gc, fieldX + 45, fieldY + 50);
        // bullets from repeater
        drawMiniBullet(gc, fieldX + 195, fieldY + 48);
        drawMiniBullet(gc, fieldX + 235, fieldY + 40);
        // zombie on lane 1
        drawMiniZombie(gc, fieldX + 420, fieldY + 50);

        // peashooter on lane 2
        drawMiniPeashooter(gc, fieldX + 45, fieldY + 140);
        // bullet from peashooter
        drawMiniBullet(gc, fieldX + 235, fieldY + 135);
        // zombie on lane 2
        drawMiniZombie(gc, fieldX + 450, fieldY + 140);
    }

    private void drawSlide4(GraphicsContext gc) {
        drawSlideText(gc, "Walnuts have more health. Use them to win some time while gathering more suns!");

        double cx = popX + POP_W / 2.0;
        double fieldY = popY + 180;

        // single lane
        for (int c = 0; c < 5; c++) {
            gc.setFill(c % 2 == 0 ? Color.PALEGREEN : Color.SEAGREEN);
            gc.fillRect(cx - 225 + c * 90, fieldY, 90, 90);
        }

        // walnut
        drawMiniWalnut(gc, cx - 90, fieldY + 50);
        // zombie right next to walnut
        drawMiniZombie(gc, cx + 10, fieldY + 50);

        // small cracks on walnut to show it's taking damage
        gc.setStroke(Color.rgb(110, 80, 35));
        gc.setLineWidth(1.5);
        gc.strokeLine(cx - 100, fieldY + 32, cx - 96, fieldY + 40);
        gc.strokeLine(cx - 96, fieldY + 40, cx - 100, fieldY + 48);
        gc.setLineWidth(1);
    }

    private void drawSlide5(GraphicsContext gc) {
        drawSlideText(gc, "Complete all levels to win!");

        double cx = popX + POP_W / 2.0;
        double cy = popY + POP_H / 2.0 + 20;

        // trophy handles 
        gc.setStroke(Color.GOLD);
        gc.setLineWidth(4);
        gc.strokeArc(cx - 46, cy - 30, 30, 30, 90, 180, ArcType.OPEN);
        gc.strokeArc(cx + 16, cy - 30, 30, 30, -90, 180, ArcType.OPEN);
        gc.setLineWidth(1);

        // trophy
        gc.setFill(Color.GOLD);
        gc.fillRoundRect(cx - 30, cy - 40, 60, 50, 12, 12);
        gc.setFill(Color.GOLDENROD);
        gc.fillRect(cx - 20, cy + 10, 40, 12);
        gc.fillRect(cx - 25, cy + 22, 50, 8);
        
        // star on trophy 
        drawStar(gc, cx, cy - 15, 12, Color.WHITE);

        // confetti
        Color[] confettiColors = {Color.RED, Color.BLUE, Color.YELLOW, Color.LIMEGREEN, Color.MAGENTA, Color.CYAN, Color.ORANGE};
        double[][] confettiPos = {
            {-180, -80}, {-120, -50}, {-60, -90}, {60, -70}, {120, -85}, {180, -55},
            {-150, 0}, {-90, 30}, {-30, 50}, {30, 60}, {90, 20}, {150, 45},
            {-200, 70}, {-100, 80}, {0, 90}, {100, 75}, {200, 60}
        };
        for (int i = 0; i < confettiPos.length; i++) {
            gc.setFill(confettiColors[i % confettiColors.length]);
            double rx = cx + confettiPos[i][0];
            double ry = cy + confettiPos[i][1];
            if (i % 3 == 0) {
                gc.fillRect(rx, ry, 8, 12);
            } else if (i % 3 == 1) {
                gc.fillOval(rx, ry, 10, 6);
            } else {
                gc.save();
                gc.translate(rx, ry);
                gc.rotate(45);
                gc.fillRect(-4, -6, 8, 12);
                gc.restore();
            }
        }
    }

    private void drawStar(GraphicsContext gc, double cx, double cy, double r, Color color) {
        gc.setFill(color);
        double[] xs = new double[10];
        double[] ys = new double[10];
        for (int i = 0; i < 10; i++) {
            double angle = Math.PI / 2.0 + i * Math.PI / 5.0;
            double rad = (i % 2 == 0) ? r : r * 0.4;
            xs[i] = cx + Math.cos(angle) * rad;
            ys[i] = cy - Math.sin(angle) * rad;
        }
        gc.fillPolygon(xs, ys, 10);
    }

    private void drawMiniHouse(GraphicsContext gc, double bx, double by) {
        // main wall 
        gc.setFill(Color.web("#F5F5DC"));
        gc.fillRect(bx, by + 60, 130, 100);
 
        // pitched roof
        gc.setFill(Color.web("#C4736A"));
        gc.fillPolygon(
            new double[]{bx - 8, bx + 65, bx + 138},
            new double[]{by + 62, by + 14, by + 62}, 3);
        // roof edge trim
        gc.setStroke(Color.web("#A05A50"));
        gc.setLineWidth(3);
        gc.strokeLine(bx - 8, by + 62, bx + 138, by + 62);
        gc.setLineWidth(1);
 
        // door
        gc.setFill(Color.web("#8B4513"));
        gc.fillRoundRect(bx + 52, by + 110, 28, 50, 5, 5);
        gc.setFill(Color.GOLDENROD);
        gc.fillOval(bx + 72, by + 136, 4, 4);
 
        // windows
        gc.setFill(Color.web("#87CEEB"));
        gc.fillRect(bx + 14, by + 90, 26, 22);
        gc.fillRect(bx + 92, by + 90, 26, 22);
        // window frames
        gc.setStroke(Color.web("#5C3A1E"));
        gc.setLineWidth(2);
        gc.strokeRect(bx + 14, by + 90, 26, 22);
        gc.strokeRect(bx + 92, by + 90, 26, 22);
        // window cross-bars
        gc.strokeLine(bx + 27, by + 90, bx + 27, by + 112);
        gc.strokeLine(bx + 14, by + 101, bx + 40, by + 101);
        gc.strokeLine(bx + 105, by + 90, bx + 105, by + 112);
        gc.strokeLine(bx + 92, by + 101, bx + 118, by + 101);
        gc.setLineWidth(1);
 
        // foundation line
        gc.setFill(Color.rgb(140, 130, 110));
        gc.fillRect(bx - 2, by + 158, 134, 6);
    }


    private void drawMiniZombie(GraphicsContext gc, double zx, double zy) {
        // legs
        gc.setFill(Color.DARKSLATEGRAY);
        gc.fillRect(zx - 7.2, zy + 7.2, 6.3, 14.4);
        gc.fillRect(zx + 3.6, zy + 7.2, 6.3, 14.4);
    
        // body
        gc.setFill(Color.DARKKHAKI);
        gc.fillRect(zx - 9.0, zy - 14.4, 21.6, 23.4);
    
        // arms
        gc.setFill(Color.DARKSEAGREEN);
        gc.fillRect(zx - 16.2, zy - 7.2, 9.0, 5.4);
        gc.fillRect(zx + 12.6, zy - 10.8, 9.0, 5.4);
    
        // head
        gc.setFill(Color.YELLOWGREEN);
        gc.fillOval(zx - 9.0, zy - 30.6, 19.8, 19.8);
    
        // eyes
        gc.setFill(Color.RED);
        gc.fillOval(zx - 4.5, zy - 25.2, 3.6, 3.6);
        gc.fillOval(zx + 3.6, zy - 25.2, 3.6, 3.6);
    }

    private void drawMiniSunflower(GraphicsContext gc, double sx, double sy) {
        // stem
        gc.setFill(Color.FORESTGREEN);
        gc.fillRoundRect(sx - 2, sy + 4, 5, 20, 4, 4);
        // leaves
        gc.setFill(Color.LIMEGREEN);
        gc.fillOval(sx - 12, sy + 10, 12, 7);
        gc.fillOval(sx + 1, sy + 10, 12, 7);
        // petals
        gc.setFill(Color.GOLD);
        for (int i = 0; i < 10; i++) {
            double a = i * (Math.PI * 2 / 10.0);
            double px = sx + Math.cos(a) * 14;
            double py = sy - 6 + Math.sin(a) * 14;
            gc.fillOval(px - 5, py - 5, 10, 10);
        }
        // face
        gc.setFill(Color.rgb(170, 120, 60));
        gc.fillOval(sx - 11, sy - 16, 22, 20);
        // eyes
        gc.setFill(Color.BLACK);
        gc.fillOval(sx - 5, sy - 8, 3, 4);
        gc.fillOval(sx + 2, sy - 8, 3, 4);
        // smile
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeArc(sx - 5, sy - 3, 10, 5, 180, 180, ArcType.OPEN);
    }

    private void drawMiniPeashooter(GraphicsContext gc, double px, double py) {
        // stem
        gc.setFill(Color.FORESTGREEN);
        gc.fillRoundRect(px - 3, py + 2, 6, 20, 4, 4);
        // leaves
        gc.setFill(Color.LIMEGREEN);
        gc.fillOval(px - 12, py + 8, 12, 7);
        gc.fillOval(px + 1, py + 8, 12, 7);
        // head
        gc.setFill(Color.YELLOWGREEN);
        gc.fillOval(px - 13, py - 18, 22, 22);
        // snout
        gc.fillRoundRect(px + 3, py - 12, 12, 9, 4, 4);
        gc.fillOval(px + 11, py - 13, 7, 10);
        gc.setFill(Color.BLACK);
        gc.fillOval(px + 14, py - 11, 4, 7);
        // eye
        gc.setFill(Color.WHITE);
        gc.fillOval(px - 5, py - 13, 7, 9);
        gc.setFill(Color.BLACK);
        gc.fillOval(px - 2, py - 9, 3, 4);
    }

    private void drawMiniRepeater(GraphicsContext gc, double rx, double ry) {
        // stem
        gc.setFill(Color.FORESTGREEN);
        gc.fillRoundRect(rx - 2, ry + 6, 5, 16, 4, 4);
        // leaves
        gc.setFill(Color.LIMEGREEN);
        gc.fillOval(rx - 12, ry + 10, 12, 7);
        gc.fillOval(rx + 1, ry + 10, 12, 7);

        // back head
        double bx = rx - 5, by = ry - 8;
        gc.setFill(Color.YELLOWGREEN);
        gc.fillOval(bx - 11, by - 14, 20, 20);
        gc.fillRoundRect(bx + 3, by - 8, 10, 7, 4, 4);
        gc.fillOval(bx + 10, by - 10, 6, 9);
        gc.setFill(Color.BLACK);
        gc.fillOval(bx + 13, by - 8, 3, 6);
        gc.setFill(Color.WHITE);
        gc.fillOval(bx - 4, by - 9, 6, 7);
        gc.setFill(Color.BLACK);
        gc.fillOval(bx - 1, by - 6, 2, 3);

        // front head
        double fx = rx + 4, fy = ry + 1;
        gc.setFill(Color.YELLOWGREEN);
        gc.fillOval(fx - 11, fy - 14, 20, 20);
        gc.fillRoundRect(fx + 3, fy - 8, 10, 7, 4, 4);
        gc.fillOval(fx + 10, fy - 10, 6, 9);
        gc.setFill(Color.BLACK);
        gc.fillOval(fx + 13, fy - 8, 3, 6);
        gc.setFill(Color.WHITE);
        gc.fillOval(fx - 4, fy - 9, 6, 7);
        gc.setFill(Color.BLACK);
        gc.fillOval(fx - 1, fy - 6, 2, 3);
    }

    private void drawMiniBullet(GraphicsContext gc, double bx, double by) {
        gc.setFill(Color.GREEN);
        gc.fillOval(bx - 5, by - 5, 10, 10);
    }

    private void drawMiniWalnut(GraphicsContext gc, double wx, double wy) {
        // body
        gc.setFill(Color.rgb(190, 150, 70));
        gc.fillOval(wx - 16, wy - 24, 32, 44);
        // inner face
        gc.setFill(Color.rgb(210, 170, 90));
        gc.fillOval(wx - 10, wy - 10, 20, 24);
        // eyes
        gc.setFill(Color.rgb(250, 245, 220));
        gc.fillOval(wx - 9, wy - 10, 9, 12);
        gc.fillOval(wx + 1, wy - 10, 9, 12);
        // pupils
        gc.setFill(Color.BLACK);
        gc.fillOval(wx - 4, wy - 6, 4, 4);
        gc.fillOval(wx + 5, wy - 6, 4, 4);
        // determined expression
        gc.setStroke(Color.rgb(90, 70, 30));
        gc.setLineWidth(1.2);
        gc.strokeLine(wx - 3, wy + 5, wx + 3, wy + 5);
        gc.setLineWidth(1);
    }
}