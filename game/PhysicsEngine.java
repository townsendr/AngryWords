package game;

import java.util.List;

/**
 * Handles physics calculations for the game.
 */
public class PhysicsEngine {
    // Physics constants
    public static final double GRAVITY = 9.8 * 30;  // Gravity force (adjusted for game scale)
    public static final double RESTITUTION = 0.7;   // Bounciness factor
    
    /**
     * Updates all game objects according to physics rules.
     */
    public static void update(List<GameObject> gameObjects, double deltaTime) {
        // Update positions of all objects
        for (GameObject obj : gameObjects) {
            if (obj.isActive()) {
                obj.update(deltaTime);
            }
        }
        
        // Check for and resolve collisions
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject obj1 = gameObjects.get(i);
            if (!obj1.isActive()) continue;
            
            // Check for collisions with other objects
            for (int j = i + 1; j < gameObjects.size(); j++) {
                GameObject obj2 = gameObjects.get(j);
                if (!obj2.isActive()) continue;
                
                if (obj1.collidesWith(obj2)) {
                    resolveCollision(obj1, obj2);
                    
                    // Special case for Letter hitting Block
                    if (obj1 instanceof Letter && obj2 instanceof Block) {
                        handleLetterBlockCollision((Letter)obj1, (Block)obj2);
                    } else if (obj1 instanceof Block && obj2 instanceof Letter) {
                        handleLetterBlockCollision((Letter)obj2, (Block)obj1);
                    }
                }
            }
            
            // Check for collisions with walls
            checkWallCollisions(obj1);
        }
    }
    
    /**
     * Resolves collision between two game objects.
     */
    private static void resolveCollision(GameObject obj1, GameObject obj2) {
        // Calculate collision normal and relative velocity
        double nx = obj2.getX() + obj2.getWidth()/2 - (obj1.getX() + obj1.getWidth()/2);
        double ny = obj2.getY() + obj2.getHeight()/2 - (obj1.getY() + obj1.getHeight()/2);
        double len = Math.sqrt(nx * nx + ny * ny);
        
        // If objects are exactly on top of each other, use a default normal
        if (len < 0.0001) {
            nx = 0;
            ny = 1;
            len = 1;
        } else {
            // Normalize normal vector
            nx /= len;
            ny /= len;
        }
        
        // Calculate relative velocity along normal
        double velX1 = obj1.getVelocityX();
        double velY1 = obj1.getVelocityY();
        double velX2 = obj2.getVelocityX();
        double velY2 = obj2.getVelocityY();
        double relVelX = velX1 - velX2;
        double relVelY = velY1 - velY2;
        double relVelDotNormal = relVelX * nx + relVelY * ny;
        
        // Do not resolve if objects are moving away from each other
        if (relVelDotNormal > 0) return;
        
        // Calculate impulse scalar
        double e = RESTITUTION;  // coefficient of restitution
        double j = -(1 + e) * relVelDotNormal;
        j /= (1 / obj1.getMass() + 1 / obj2.getMass());
        
        // Apply impulse
        double impulseX = j * nx;
        double impulseY = j * ny;
        
        obj1.setVelocityX(velX1 + impulseX / obj1.getMass());
        obj1.setVelocityY(velY1 + impulseY / obj1.getMass());
        obj2.setVelocityX(velX2 - impulseX / obj2.getMass());
        obj2.setVelocityY(velY2 - impulseY / obj2.getMass());
        
        // Separate the objects to prevent sinking
        float percent = 0.2f;  // usually 20% to 80%
        double penetration = 0.5; // A small value to ensure separation
        double separationX = percent * nx * penetration;
        double separationY = percent * ny * penetration;
        
        double totalMass = obj1.getMass() + obj2.getMass();
        double weight1 = obj2.getMass() / totalMass;
        double weight2 = obj1.getMass() / totalMass;
        
        // Adjust positions
        double newX1 = obj1.getX() - separationX * weight1;
        double newY1 = obj1.getY() - separationY * weight1;
        double newX2 = obj2.getX() + separationX * weight2;
        double newY2 = obj2.getY() + separationY * weight2;
        
        // Set new positions while keeping objects on screen
        obj1.x = Math.max(0, Math.min(1000 - obj1.getWidth(), newX1));
        obj1.y = Math.max(0, Math.min(500 - obj1.getHeight(), newY1));
        obj2.x = Math.max(0, Math.min(1000 - obj2.getWidth(), newX2));
        obj2.y = Math.max(0, Math.min(500 - obj2.getHeight(), newY2));
    }
    
    /**
     * Checks and resolves collisions with walls.
     */
    private static void checkWallCollisions(GameObject obj) {
        double newX = obj.getX();
        double newY = obj.getY();
        boolean collided = false;
        
        // Left wall
        if (newX < 0) {
            newX = 0;
            obj.setVelocityX(-obj.getVelocityX() * RESTITUTION);
            collided = true;
        }
        
        // Right wall
        if (newX + obj.getWidth() > 1000) {
            newX = 1000 - obj.getWidth();
            obj.setVelocityX(-obj.getVelocityX() * RESTITUTION);
            collided = true;
        }
        
        // Top wall (ceiling)
        if (newY < 0) {
            newY = 0;
            obj.setVelocityY(-obj.getVelocityY() * RESTITUTION);
            collided = true;
        }
        
        // Bottom wall (floor)
        if (newY + obj.getHeight() > 500) {
            newY = 500 - obj.getHeight();
            
            // Only bounce if coming in fast enough
            if (Math.abs(obj.getVelocityY()) > 1.0) {
                obj.setVelocityY(-obj.getVelocityY() * RESTITUTION);
            } else {
                obj.setVelocityY(0);
            }
            
            // Apply friction on the floor
            obj.setVelocityX(obj.getVelocityX() * 0.9);
            collided = true;
        }
        
        if (collided) {
            // Update position after collision
            obj.x = newX;
            obj.y = newY;
        }
    }
    
    /**
     * Handles special case for letters hitting blocks.
     */
    private static void handleLetterBlockCollision(Letter letter, Block block) {
        // Calculate collision force based on letter's velocity
        double velocityMagnitude = Math.sqrt(
            letter.getVelocityX() * letter.getVelocityX() + 
            letter.getVelocityY() * letter.getVelocityY()
        );
        
        // Apply damage to the block based on collision force
        block.onHit(velocityMagnitude * letter.getMass() / 20.0);
    }
    
    /**
     * Calculates the launch velocity for flinging a letter.
     */
    public static Vector2D calculateLaunchVelocity(double startX, double startY, 
                                                 double targetX, double targetY, 
                                                 double power) {
        // Calculate direction vector
        double dx = targetX - startX;
        double dy = targetY - startY;
        
        // Normalize and scale by power
        double length = Math.sqrt(dx * dx + dy * dy);
        double normalizedX = dx / length;
        double normalizedY = dy / length;
        
        return new Vector2D(normalizedX * power, normalizedY * power);
    }
}
