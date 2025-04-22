package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Main game panel that handles rendering and game logic.
 */
public class GamePanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
    // Game objects
    private List<GameObject> gameObjects;
    private List<Letter> letters;
    private List<Block> blocks;
    
    // Game state
    private Timer gameTimer;
    private Random random;
    private int score = 0;
    private JLabel scoreLabel;
    private boolean isDragging = false;
    private Point dragStart;
    private Point dragCurrent;
    
    // Timing
    private long lastUpdateTime;
    private double deltaTime;
    
    // Current level setup
    private int currentLevel = 1;
    
    /**
     * Constructs the game panel.
     */
    public GamePanel() {
        // Initialize lists
        gameObjects = new ArrayList<>();
        letters = new ArrayList<>();
        blocks = new ArrayList<>();
        random = new Random();
        
        // Set up panel properties
        setBackground(new Color(240, 240, 255));
        setFocusable(true);
        
        // Add event listeners
        addMouseListener(this);
        addMouseMotionListener(this);
        
        // Set up game timer (60 FPS)
        gameTimer = new Timer(16, this);
        gameTimer.start();
        
        // Initialize time tracking
        lastUpdateTime = System.nanoTime();
        
        // Set up the initial level
        setupLevel(currentLevel);
    }
    
    /**
     * Sets up blocks for the specified level.
     */
    private void setupLevel(int level) {
        // Clear existing game objects
        gameObjects.clear();
        letters.clear();
        blocks.clear();
        
        // Create different block layouts based on level
        switch (level) {
            case 1:
                // Simple pyramid structure
                createPyramid(700, 400, 5, 40, 40);
                break;
            case 2:
                // Two columns with platform
                createColumn(650, 380, 5, 40, 40);
                createColumn(800, 380, 5, 40, 40);
                createPlatform(650, 340, 4, 40, 20);
                break;
            case 3:
                // Complex structure
                createColumn(650, 400, 3, 40, 40);
                createColumn(750, 400, 3, 40, 40);
                createColumn(850, 400, 3, 40, 40);
                createPlatform(650, 280, 6, 40, 20);
                createPyramid(700, 260, 3, 40, 40);
                break;
            default:
                // Random structure for higher levels
                int numStructures = 2 + level / 2;
                for (int i = 0; i < numStructures; i++) {
                    int structType = random.nextInt(3);
                    int x = 600 + random.nextInt(300);
                    int y = 300 + random.nextInt(180);
                    int size = 2 + random.nextInt(4);
                    
                    switch (structType) {
                        case 0:
                            createPyramid(x, y, size, 30 + level, 30 + level);
                            break;
                        case 1:
                            createColumn(x, y, size, 30 + level, 30 + level);
                            break;
                        case 2:
                            createPlatform(x, y, size, 40 + level, 20 + level/2);
                            break;
                    }
                }
                break;
        }
        
        // Add all blocks to game objects list
        gameObjects.addAll(blocks);
    }
    
    /**
     * Creates a pyramid structure of blocks.
     */
    private void createPyramid(int baseX, int baseY, int rows, int blockWidth, int blockHeight) {
        for (int row = 0; row < rows; row++) {
            int blocksInRow = rows - row;
            
            for (int col = 0; col < blocksInRow; col++) {
                double x = baseX + (blockWidth * col) - (blockWidth * blocksInRow / 2.0) + (blockWidth / 2.0);
                double y = baseY - (row * blockHeight);
                
                Block block = new Block(x, y, blockWidth, blockHeight);
                blocks.add(block);
            }
        }
    }
    
    /**
     * Creates a column structure of blocks.
     */
    private void createColumn(int baseX, int baseY, int height, int blockWidth, int blockHeight) {
        for (int row = 0; row < height; row++) {
            double y = baseY - (row * blockHeight);
            Block block = new Block(baseX, y, blockWidth, blockHeight);
            blocks.add(block);
        }
    }
    
    /**
     * Creates a platform structure of blocks.
     */
    private void createPlatform(int startX, int y, int length, int blockWidth, int blockHeight) {
        for (int col = 0; col < length; col++) {
            double x = startX + (col * blockWidth);
            Block block = new Block(x, y, blockWidth, blockHeight);
            blocks.add(block);
        }
    }
    
    /**
     * Flings a word from the left side of the screen.
     */
    public void flingWord(String word) {
        if (word.isEmpty()) return;
        
        // Convert word to uppercase for consistency
        word = word.toUpperCase();
        
        // Calculate initial position and spacing
        double startX = 50;
        double startY = 250;
        double letterSpacing = 40;
        
        // Create letter objects for each character
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            
            // Calculate position with slight offset for each letter
            double x = startX + i * letterSpacing;
            double y = startY + (Math.random() * 10 - 5);
            
            // Create letter object
            Letter letter = new Letter(x, y, c);
            
            // Calculate velocity based on drag if available
            double power = 400 + (i * 20); // Base power plus increasing power for later letters
            double angle = -30 + (Math.random() * 20); // Slightly random upward angle
            
            if (isDragging) {
                // Use drag vector to determine launch direction
                Vector2D launchVelocity = PhysicsEngine.calculateLaunchVelocity(
                    dragStart.x, dragStart.y, 
                    dragCurrent.x, dragCurrent.y, 
                    power
                );
                letter.setVelocityX(launchVelocity.getX());
                letter.setVelocityY(launchVelocity.getY());
            } else {
                // Default launch if not dragging
                double radians = Math.toRadians(angle);
                letter.setVelocityX(power * Math.cos(radians));
                letter.setVelocityY(power * Math.sin(radians));
            }
            
            // Add letter to lists
            letters.add(letter);
            gameObjects.add(letter);
        }
        
        // Reset drag state
        isDragging = false;
    }
    
    /**
     * Called by the game timer to update game state.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // Calculate time since last update
        long currentTime = System.nanoTime();
        deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000.0;
        lastUpdateTime = currentTime;
        
        // Cap delta time to prevent physics issues on lag
        if (deltaTime > 0.1) deltaTime = 0.1;
        
        // Update physics
        PhysicsEngine.update(gameObjects, deltaTime);
        
        // Check for completed level
        checkLevelProgress();
        
        // Remove inactive objects
        cleanupInactiveObjects();
        
        // Redraw the panel
        repaint();
    }
    
    /**
     * Checks level progress to see if it's complete.
     */
    private void checkLevelProgress() {
        // Count active blocks
        int activeBlocks = 0;
        int damagedBlocks = 0;
        
        for (Block block : blocks) {
            if (block.isActive()) {
                activeBlocks++;
                if (block.isHit() && block.getHealth() < 50) {
                    damagedBlocks++;
                }
            }
        }
        
        // Level is complete if most blocks are damaged or inactive
        if (activeBlocks > 0 && damagedBlocks >= activeBlocks * 0.7) {
            score += 100 + (currentLevel * 50);
            currentLevel++;
            setupLevel(currentLevel);
            updateScore();
        }
    }
    
    /**
     * Removes inactive game objects from lists.
     */
    private void cleanupInactiveObjects() {
        // Create lists for objects to remove
        List<GameObject> objectsToRemove = new ArrayList<>();
        List<Letter> lettersToRemove = new ArrayList<>();
        List<Block> blocksToRemove = new ArrayList<>();
        
        // Find inactive objects
        for (GameObject obj : gameObjects) {
            if (!obj.isActive()) {
                objectsToRemove.add(obj);
                
                if (obj instanceof Letter) {
                    lettersToRemove.add((Letter)obj);
                } else if (obj instanceof Block) {
                    blocksToRemove.add((Block)obj);
                }
            }
        }
        
        // Remove inactive objects
        gameObjects.removeAll(objectsToRemove);
        letters.removeAll(lettersToRemove);
        blocks.removeAll(blocksToRemove);
    }
    
    /**
     * Updates the score display.
     */
    private void updateScore() {
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + score + " | Level: " + currentLevel);
        }
    }
    
    /**
     * Sets the score label reference.
     */
    public void setScoreLabel(JLabel label) {
        this.scoreLabel = label;
        updateScore();
    }
    
    /**
     * Resets the current level.
     */
    public void resetLevel() {
        setupLevel(currentLevel);
    }
    
    /**
     * Renders the game.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing for smoother rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw background with gradient
        GradientPaint bgGradient = new GradientPaint(
            0, 0, new Color(220, 240, 255), 
            0, getHeight(), new Color(180, 210, 240)
        );
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Draw floor
        g2d.setColor(new Color(100, 100, 100));
        g2d.fillRect(0, 500, getWidth(), 20);
        
        // Draw all game objects
        for (GameObject obj : gameObjects) {
            obj.render(g2d);
        }
        
        // Draw slingshot area on left side
        g2d.setColor(new Color(100, 70, 30));
        g2d.fillRect(30, 380, 10, 120);
        g2d.fillRect(80, 380, 10, 120);
        g2d.fillOval(25, 370, 20, 20);
        g2d.fillOval(75, 370, 20, 20);
        
        // Draw elastic band
        g2d.setColor(new Color(200, 30, 30));
        g2d.setStroke(new BasicStroke(3f));
        g2d.drawLine(35, 380, 85, 380);
        
        // Draw drag line if dragging
        if (isDragging && dragStart != null && dragCurrent != null) {
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(dragStart.x, dragStart.y, dragCurrent.x, dragCurrent.y);
            
            // Draw arrow head
            double angle = Math.atan2(dragStart.y - dragCurrent.y, dragStart.x - dragCurrent.x);
            int arrowSize = 10;
            int x1 = (int)(dragCurrent.x + arrowSize * Math.cos(angle - Math.PI/6));
            int y1 = (int)(dragCurrent.y + arrowSize * Math.sin(angle - Math.PI/6));
            int x2 = (int)(dragCurrent.x + arrowSize * Math.cos(angle + Math.PI/6));
            int y2 = (int)(dragCurrent.y + arrowSize * Math.sin(angle + Math.PI/6));
            g2d.drawLine(dragCurrent.x, dragCurrent.y, x1, y1);
            g2d.drawLine(dragCurrent.x, dragCurrent.y, x2, y2);
        }
        
        // Draw level and instructions
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Level: " + currentLevel, 20, 30);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Enter a word and click 'Fling!' or drag to aim", 20, 50);
        g2d.drawString("Goal: Knock down the blocks with letters", 20, 70);
    }
    
    /**
     * Mouse event handling for drag operations.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // Start drag operation
        dragStart = e.getPoint();
        dragCurrent = e.getPoint();
        isDragging = true;
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        // End drag operation
        isDragging = false;
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        // Update current drag position
        dragCurrent = e.getPoint();
        repaint();
    }
    
    // Unused mouse event methods
    @Override
    public void mouseClicked(MouseEvent e) {}
    
    @Override
    public void mouseEntered(MouseEvent e) {}
    
    @Override
    public void mouseExited(MouseEvent e) {}
    
    @Override
    public void mouseMoved(MouseEvent e) {}
}
