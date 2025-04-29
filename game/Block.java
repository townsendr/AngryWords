package game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Represents a block that can be knocked down by letters.
 */
public class Block extends GameObject {
    private boolean hit = false;
    private int health = 100;
    
    /**
     * Constructs a block game object.
     */
    public Block(double x, double y, double width, double height) {
        super(x, y, width, height, width * height * 0.1);
        this.color = new Color(150, 100, 50);  // Brown wooden blocks
    }
    
    /**
     * Updates the block's position and status.
     */
    @Override
    public void update(double deltaTime) {
        if (!active) return;
        
        super.update(deltaTime);
        
        // Limit maximum velocity to prevent blocks from moving too fast
        double maxVelocity = 300.0;
        if (Math.abs(velocityX) > maxVelocity) {
            velocityX = Math.signum(velocityX) * maxVelocity;
        }
        if (Math.abs(velocityY) > maxVelocity) {
            velocityY = Math.signum(velocityY) * maxVelocity;
        }
        
        // Blocks that fall off the bottom of the screen are deactivated
        if (y > 500) {
            active = false;
        }
    }
    
    /**
     * Custom collision handling for block-to-block interactions
     */
    @Override
    public boolean collidesWith(GameObject other) {
        // Standard collision check using rectangles
        if (!super.collidesWith(other)) {
            return false;
        }
        
        // For block-to-block collisions, add some additional handling
        if (other instanceof Block) {
            // Calculate penetration depth more accurately for blocks
            double overlapX = (this.width + other.getWidth()) / 2 - 
                Math.abs((this.x + this.width/2) - (other.getX() + other.getWidth()/2));
            double overlapY = (this.height + other.getHeight()) / 2 - 
                Math.abs((this.y + this.height/2) - (other.getY() + other.getHeight()/2));
            
            // If blocks are barely touching, don't consider it a collision
            // This helps prevent blocks from getting stuck together
            if (overlapX < 0.5 || overlapY < 0.5) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Renders the block on the screen.
     */
    @Override
    public void render(Graphics2D g) {
        if (!active) return;
        
        // Save original color
        Color originalColor = g.getColor();
        
        // Draw block with color dependent on health
        if (hit) {
            float healthPercent = health / 100f;
            g.setColor(new Color(
                Math.min(255, color.getRed() + (int)((255 - color.getRed()) * (1 - healthPercent))),
                Math.max(0, color.getGreen() - (int)(color.getGreen() * (1 - healthPercent))),
                Math.max(0, color.getBlue() - (int)(color.getBlue() * (1 - healthPercent)))
            ));
        } else {
            g.setColor(color);
        }
        
        g.fillRect((int)x, (int)y, (int)width, (int)height);
        
        // Draw block outline
        g.setColor(Color.BLACK);
        g.drawRect((int)x, (int)y, (int)width, (int)height);
        
        // If hit, draw cracks or damage effects
        if (hit && health < 80) {
            g.setColor(Color.BLACK);
            int cracks = 5 - (health / 20);
            for (int i = 0; i < cracks; i++) {
                int startX = (int)(x + Math.random() * width);
                int startY = (int)(y + Math.random() * height);
                int endX = (int)(startX + (Math.random() * 20 - 10));
                int endY = (int)(startY + (Math.random() * 20 - 10));
                g.drawLine(startX, startY, endX, endY);
            }
        }
        
        // Restore original color
        g.setColor(originalColor);
    }
    
    /**
     * Called when the block is hit by a letter.
     */
    public void onHit(double force) {
        hit = true;
        health -= (int)(force * 10);
        if (health <= 0) {
            health = 0;
            // Block maintains its active state but shows heavy damage
        }
    }
    
    /**
     * Gets the block's health.
     */
    public int getHealth() {
        return health;
    }
    
    /**
     * Checks if the block has been hit.
     */
    public boolean isHit() {
        return hit;
    }
}
