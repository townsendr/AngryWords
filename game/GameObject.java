package game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Base class for all game objects such as letters and blocks.
 */
public abstract class GameObject {
    protected double x, y;
    protected double width, height;
    protected double velocityX, velocityY;
    protected double mass;
    protected boolean active = true;
    protected Color color;
    
    /**
     * Constructs a game object with specified position and dimensions.
     */
    public GameObject(double x, double y, double width, double height, double mass) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.mass = mass;
        this.velocityX = 0;
        this.velocityY = 0;
        this.color = Color.GRAY;
    }
    
    /**
     * Updates the object's position based on its velocity.
     */
    public void update(double deltaTime) {
        if (!active) return;
        
        // Update position based on velocity
        x += velocityX * deltaTime;
        y += velocityY * deltaTime;
        
        // Apply gravity
        velocityY += PhysicsEngine.GRAVITY * deltaTime;
        
        // Apply drag/friction
        velocityX *= 0.99;
        velocityY *= 0.99;
        
        // If object is out of bounds, deactivate it
        if (y > 500) {
            active = false;
        }
    }
    
    /**
     * Renders the object on the screen.
     */
    public abstract void render(Graphics2D g);
    
    /**
     * Gets the bounding rectangle for collision detection.
     */
    public Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(x, y, width, height);
    }
    
    /**
     * Applies a force to the object.
     */
    public void applyForce(double forceX, double forceY) {
        velocityX += forceX / mass;
        velocityY += forceY / mass;
    }
    
    /**
     * Applies an impulse to the object (instantaneous change in velocity).
     */
    public void applyImpulse(double impulseX, double impulseY) {
        velocityX += impulseX / mass;
        velocityY += impulseY / mass;
    }
    
    /**
     * Checks if this object collides with another object.
     */
    public boolean collidesWith(GameObject other) {
        // Standard AABB (Axis-Aligned Bounding Box) collision detection
        if (!getBounds().intersects(other.getBounds())) {
            return false;
        }
        
        // For more precise collision detection, check if the penetration depth is significant
        double overlapX = (this.width + other.getWidth()) / 2 - 
                Math.abs((this.x + this.width/2) - (other.getX() + other.getWidth()/2));
        double overlapY = (this.height + other.getHeight()) / 2 - 
                Math.abs((this.y + this.height/2) - (other.getY() + other.getHeight()/2));
                
        // If overlap is very small, don't consider it a collision - this prevents jittering
        if (overlapX < 0.1 || overlapY < 0.1) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Gets the object's x position.
     */
    public double getX() {
        return x;
    }
    
    /**
     * Gets the object's y position.
     */
    public double getY() {
        return y;
    }
    
    /**
     * Gets the object's width.
     */
    public double getWidth() {
        return width;
    }
    
    /**
     * Gets the object's height.
     */
    public double getHeight() {
        return height;
    }
    
    /**
     * Gets the object's x velocity.
     */
    public double getVelocityX() {
        return velocityX;
    }
    
    /**
     * Gets the object's y velocity.
     */
    public double getVelocityY() {
        return velocityY;
    }
    
    /**
     * Sets the object's x velocity.
     */
    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }
    
    /**
     * Sets the object's y velocity.
     */
    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }
    
    /**
     * Gets the object's mass.
     */
    public double getMass() {
        return mass;
    }
    
    /**
     * Checks if the object is active.
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Sets the object's active state.
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * Sets the object's color.
     */
    public void setColor(Color color) {
        this.color = color;
    }
    
    /**
     * Gets the object's color.
     */
    public Color getColor() {
        return color;
    }
}
