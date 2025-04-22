package game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Represents a letter in the game that can be flung at blocks.
 */
public class Letter extends GameObject {
    private char letter;
    private Font font;
    private static final double LETTER_SIZE = 30;
    
    /**
     * Constructs a letter game object.
     */
    public Letter(double x, double y, char letter) {
        super(x, y, LETTER_SIZE, LETTER_SIZE, LETTER_SIZE * 0.2);
        this.letter = letter;
        this.font = new Font("Arial", Font.BOLD, (int)LETTER_SIZE);
        
        // Assign color based on letter
        assignColor();
    }
    
    /**
     * Assigns a color to the letter based on its value.
     */
    private void assignColor() {
        // Colors based on vowels, consonants, or special characters
        if ("AEIOU".indexOf(Character.toUpperCase(letter)) >= 0) {
            // Vowels are red
            color = new Color(220, 50, 50);
        } else if (Character.isLetter(letter)) {
            // Consonants are blue
            color = new Color(50, 50, 220);
        } else {
            // Special characters are green
            color = new Color(50, 220, 50);
        }
    }
    
    /**
     * Renders the letter on the screen.
     */
    @Override
    public void render(Graphics2D g) {
        if (!active) return;
        
        // Save original color and font
        Color originalColor = g.getColor();
        Font originalFont = g.getFont();
        
        // Draw the circle background for the letter
        g.setColor(color);
        g.fillOval((int)x, (int)y, (int)width, (int)height);
        
        // Draw the letter
        g.setColor(Color.WHITE);
        g.setFont(font);
        
        // Center the letter in the circle
        FontMetrics metrics = g.getFontMetrics(font);
        int textX = (int)(x + (width - metrics.charWidth(letter)) / 2);
        int textY = (int)(y + ((height - metrics.getHeight()) / 2) + metrics.getAscent());
        
        g.drawString(String.valueOf(letter), textX, textY);
        
        // Restore original color and font
        g.setColor(originalColor);
        g.setFont(originalFont);
    }
    
    /**
     * Gets the letter character.
     */
    public char getLetter() {
        return letter;
    }
    
    /**
     * Gets accurate collision bounds for the letter (circular).
     */
    @Override
    public Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(x, y, width, height);
    }
}
