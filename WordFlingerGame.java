import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import game.GamePanel;

/**
 * Main class for the Word Flinger Physics Game.
 * This class sets up the game window and initializes the game components.
 */
public class WordFlingerGame {
    private JFrame frame;
    private GamePanel gamePanel;
    private JTextField wordInput;
    private JButton flingButton;
    private JLabel scoreLabel;
    private JPanel controlPanel;
    
    /**
     * Constructs the game window and initializes components.
     */
    public WordFlingerGame() {
        // Create and set up the window
        frame = new JFrame("Word Flinger Physics Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setResizable(false);
        
        // Create the game panel
        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(1000, 500));
        
        // Create controls panel
        createControlPanel();
        
        // Add components to the frame
        frame.getContentPane().add(gamePanel, BorderLayout.CENTER);
        frame.getContentPane().add(controlPanel, BorderLayout.SOUTH);
        
        // Center the frame on screen
        frame.setLocationRelativeTo(null);
    }
    
    /**
     * Creates the control panel with input field, buttons, and score display.
     */
    private void createControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        
        // Word input field
        JLabel inputLabel = new JLabel("Enter Word: ");
        wordInput = new JTextField(15);
        
        // Fling button
        flingButton = new JButton("Fling!");
        flingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String word = wordInput.getText().trim();
                if (!word.isEmpty()) {
                    gamePanel.flingWord(word);
                    wordInput.setText("");
                    wordInput.requestFocus();
                }
            }
        });
        
        // Score display
        scoreLabel = new JLabel("Score: 0");
        gamePanel.setScoreLabel(scoreLabel);
        
        // Reset button
        JButton resetButton = new JButton("Reset Level");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePanel.resetLevel();
            }
        });
        
        // Add components to control panel
        controlPanel.add(inputLabel);
        controlPanel.add(wordInput);
        controlPanel.add(flingButton);
        controlPanel.add(scoreLabel);
        controlPanel.add(resetButton);
        
        // Add key listener to the word input field
        wordInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    flingButton.doClick();
                }
            }
        });
    }
    
    /**
     * Displays the game window.
     */
    public void show() {
        frame.setVisible(true);
        wordInput.requestFocus();
    }
    
    /**
     * Main method to start the application.
     */
    public static void main(String[] args) {
        // Use the Event Dispatch Thread for Swing applications
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                WordFlingerGame game = new WordFlingerGame();
                game.show();
            }
        });
    }
}
