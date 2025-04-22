package game;

/**
 * A 2D vector class for physics calculations.
 */
public class Vector2D {
    private double x;
    private double y;
    
    /**
     * Constructs a vector with x and y components.
     */
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Gets the x component.
     */
    public double getX() {
        return x;
    }
    
    /**
     * Gets the y component.
     */
    public double getY() {
        return y;
    }
    
    /**
     * Sets the x component.
     */
    public void setX(double x) {
        this.x = x;
    }
    
    /**
     * Sets the y component.
     */
    public void setY(double y) {
        this.y = y;
    }
    
    /**
     * Adds another vector to this vector.
     */
    public Vector2D add(Vector2D other) {
        return new Vector2D(this.x + other.x, this.y + other.y);
    }
    
    /**
     * Subtracts another vector from this vector.
     */
    public Vector2D subtract(Vector2D other) {
        return new Vector2D(this.x - other.x, this.y - other.y);
    }
    
    /**
     * Multiplies this vector by a scalar.
     */
    public Vector2D multiply(double scalar) {
        return new Vector2D(this.x * scalar, this.y * scalar);
    }
    
    /**
     * Calculates the dot product of this vector and another vector.
     */
    public double dot(Vector2D other) {
        return this.x * other.x + this.y * other.y;
    }
    
    /**
     * Calculates the magnitude (length) of this vector.
     */
    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }
    
    /**
     * Normalizes this vector (makes it unit length).
     */
    public Vector2D normalize() {
        double mag = magnitude();
        if (mag > 0) {
            return new Vector2D(x / mag, y / mag);
        }
        return new Vector2D(0, 0);
    }
    
    /**
     * Returns a string representation of this vector.
     */
    @Override
    public String toString() {
        return "Vector2D(" + x + ", " + y + ")";
    }
}
