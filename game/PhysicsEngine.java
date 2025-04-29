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
        
        // Multiple iterations for more stable physics
        // More iterations = more stable physics (but more CPU intensive)
        final int ITERATIONS = 3;
        
        for (int iteration = 0; iteration < ITERATIONS; iteration++) {
            // Check for and resolve collisions
            for (int i = 0; i < gameObjects.size(); i++) {
                GameObject obj1 = gameObjects.get(i);
                if (!obj1.isActive()) continue;
                
                // Check for collisions with walls first
                checkWallCollisions(obj1);
                
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
            }
        }
    }
    
    /**
     * Resolves collision between two game objects.
     */
    private static void resolveCollision(GameObject obj1, GameObject obj2) {
        // Find actual overlap between objects
        double overlapX = (obj1.getWidth() + obj2.getWidth()) / 2 - Math.abs((obj1.getX() + obj1.getWidth()/2) - (obj2.getX() + obj2.getWidth()/2));
        double overlapY = (obj1.getHeight() + obj2.getHeight()) / 2 - Math.abs((obj1.getY() + obj1.getHeight()/2) - (obj2.getY() + obj2.getHeight()/2));
        
        // Calculate centers of objects
        double center1X = obj1.getX() + obj1.getWidth()/2;
        double center1Y = obj1.getY() + obj1.getHeight()/2;
        double center2X = obj2.getX() + obj2.getWidth()/2;
        double center2Y = obj2.getY() + obj2.getHeight()/2;
        
        // Calculate collision normal
        double nx = center2X - center1X;
        double ny = center2Y - center1Y;
        double len = Math.sqrt(nx * nx + ny * ny);
        
        // If objects are exactly on top of each other, use a random normal
        if (len < 0.0001) {
            double angle = Math.random() * Math.PI * 2;
            nx = Math.cos(angle);
            ny = Math.sin(angle);
        } else {
            // Normalize normal vector
            nx /= len;
            ny /= len;
        }
        
        // Calculate minimum translation distance to separate objects
        double mtdX, mtdY;
        if (overlapX < overlapY) {
            // Collision along x-axis
            mtdX = nx * overlapX * 1.01; // Add 1% extra space
            mtdY = 0;
        } else {
            // Collision along y-axis
            mtdX = 0;
            mtdY = ny * overlapY * 1.01; // Add 1% extra space
        }
        
        // If MTD is zero, use normal based MTD
        if (Math.abs(mtdX) < 0.01 && Math.abs(mtdY) < 0.01) {
            mtdX = nx * Math.min(obj1.getWidth(), obj2.getWidth()) * 0.5;
            mtdY = ny * Math.min(obj1.getHeight(), obj2.getHeight()) * 0.5;
        }
        
        // Calculate relative velocity
        double velX1 = obj1.getVelocityX();
        double velY1 = obj1.getVelocityY();
        double velX2 = obj2.getVelocityX();
        double velY2 = obj2.getVelocityY();
        double relVelX = velX1 - velX2;
        double relVelY = velY1 - velY2;
        
        // Calculate relative velocity in terms of normal direction
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
        
        // Apply mass-weighted position corrections to prevent overlap
        double totalMass = obj1.getMass() + obj2.getMass();
        double obj1Ratio = obj2.getMass() / totalMass;
        double obj2Ratio = obj1.getMass() / totalMass;
        
        // Separate objects based on their mass
        double newX1 = obj1.getX() - mtdX * obj1Ratio;
        double newY1 = obj1.getY() - mtdY * obj1Ratio;
        double newX2 = obj2.getX() + mtdX * obj2Ratio;
        double newY2 = obj2.getY() + mtdY * obj2Ratio;
        
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
