package main;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;

public class Menu {
    private static final Color BG_TOP_COLOR = new Color(0, 0, 0, 255);
    private static final Color BG_BOTTOM_COLOR = new Color(50, 50, 100, 255);
    private static final Color PLAY_BUTTON_TOP_COLOR = new Color(100, 255, 100);
    private static final Color PLAY_BUTTON_BOTTOM_COLOR = new Color(50, 200, 50);
    private static final Color QUIT_BUTTON_TOP_COLOR = new Color(255, 100, 100);
    private static final Color QUIT_BUTTON_BOTTOM_COLOR = new Color(200, 50, 50);

    private final RoundRectangle2D playButton = new RoundRectangle2D.Double(450, 300, 200, 50, 20, 20);
    private final RoundRectangle2D quitButton = new RoundRectangle2D.Double(450, 400, 200, 50, 20, 20);
    private BufferedImage gameBg;

    private float scrollX = 0;
    private float scrollY = 0;
    private final float SCROLL_SPEED_X = 0.5f;
    private final float SCROLL_SPEED_Y = 0.3f;

    private float buttonHoverAnimation = 0f;
    private int currentlyHovered = 0;
    private final long startTime = System.currentTimeMillis();


    public void draw(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Always draw the gradient background
        GradientPaint bgGradient = new GradientPaint(0, 0, BG_TOP_COLOR, 0, GamePanel.HEIGHT, BG_BOTTOM_COLOR);
        g2.setPaint(bgGradient);
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        // 2. Draw the scrolling image (if loaded) over the gradient
        if (gameBg != null) {
            int imgW = gameBg.getWidth();
            int imgH = gameBg.getHeight();
            for (int x = -imgW; x < GamePanel.WIDTH + imgW; x += imgW) {
                for (int y = -imgH; y < GamePanel.HEIGHT + imgH; y += imgH) {
                    g2.drawImage(gameBg, (int) (x - scrollX), (int) (y - scrollY), null);
                }
            }
        }

        drawAnimatedTitle(g2);
        drawButton(g2, playButton, "PLAY", PLAY_BUTTON_TOP_COLOR, PLAY_BUTTON_BOTTOM_COLOR, currentlyHovered == 1 ? buttonHoverAnimation : 0f);
        drawButton(g2, quitButton, "QUIT", QUIT_BUTTON_TOP_COLOR, QUIT_BUTTON_BOTTOM_COLOR, currentlyHovered == 2 ? buttonHoverAnimation : 0f);
    }

    private void drawAnimatedTitle(Graphics2D g2) {
        int titleX = 368;
        int titleBaseY = 150;
        double time = (System.currentTimeMillis() - startTime) / 1000.0;
        int offsetY = (int) (Math.sin(time * 1.5) * 5);
        g2.setFont(new Font("Impact", Font.BOLD, 55));

        for (int i = 8; i > 0; i--) {
            float alpha = i / 16f;
            g2.setColor(new Color(255, 255, 255, (int) (alpha * 50)));
            g2.drawString("SWING & STACK", titleX - i / 2, titleBaseY + offsetY + i / 2);
        }

        g2.setColor(new Color(50, 50, 50, 200));
        g2.drawString("SWING & STACK", titleX + 3, titleBaseY + offsetY + 3);

        GradientPaint gradient = new GradientPaint(titleX, titleBaseY - 40, new Color(255, 50, 50), titleX, titleBaseY + 40, new Color(255, 150, 50));
        g2.setPaint(gradient);
        g2.drawString("SWING & STACK", titleX, titleBaseY + offsetY);
        g2.setPaint(null);
    }

    private void drawButton(Graphics2D g2, RoundRectangle2D button, String text, Color topColor, Color bottomColor, float hoverIntensity) {
        Color hoverTop = brighter(topColor, 0.2f * hoverIntensity);
        Color hoverBottom = brighter(bottomColor, 0.2f * hoverIntensity);

        GradientPaint buttonGradient = new GradientPaint((float) button.getX(), (float) button.getY(), hoverTop, (float) button.getX(), (float) (button.getY() + button.getHeight()), hoverBottom);
        g2.setPaint(buttonGradient);
        g2.fill(button);

        g2.setColor(new Color(255, 255, 255, (int) (80 + 100 * hoverIntensity)));
        g2.setStroke(new BasicStroke(2 + 2 * hoverIntensity));
        g2.draw(button);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 36));
        int textX = (int) (button.getX() + (button.getWidth() - g2.getFontMetrics().stringWidth(text)) / 2);
        int textY = (int) (button.getY() + (button.getHeight() + g2.getFontMetrics().getAscent() - g2.getFontMetrics().getDescent()) / 2);
        g2.drawString(text, textX, textY);
    }

    private Color brighter(Color color, float factor) {
        int r = Math.min(255, (int) (color.getRed() + 255 * factor));
        int g = Math.min(255, (int) (color.getGreen() + 255 * factor));
        int b = Math.min(255, (int) (color.getBlue() + 255 * factor));
        return new Color(r, g, b, color.getAlpha());
    }

     public void update() {
        // Hover animation logic
        long elapsed = System.currentTimeMillis() - startTime;
        float target = (currentlyHovered != 0) ? 1f : 0f;
        buttonHoverAnimation += (target - buttonHoverAnimation) * 0.1f;

        // Scrolling logic (only if gameBg is loaded)
        if (gameBg != null) {
            scrollX += SCROLL_SPEED_X;
            scrollY += SCROLL_SPEED_Y;

            // Reset scroll position to create a seamless loop
            if (scrollX > gameBg.getWidth()) scrollX -= gameBg.getWidth();
            if (scrollY > gameBg.getHeight()) scrollY -= gameBg.getHeight();
        }
    }

    public boolean handleClick(int x, int y) {
        if (playButton.contains(x, y)) {
            return true;
        } else if (quitButton.contains(x, y)) {
            System.exit(0);
        }
        return false;
    }

    public void handleHover(int x, int y) {
        if (playButton.contains(x, y)) {
            currentlyHovered = 1;
        } else if (quitButton.contains(x, y)) {
            currentlyHovered = 2;
        } else {
            currentlyHovered = 0;
        }
    }   

    public Menu() {
        try {
            gameBg = javax.imageio.ImageIO.read(getClass().getResource("/img/gameBG.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
