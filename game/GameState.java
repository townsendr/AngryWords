package game;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the game state, including levels, score, and active objects.
 */
public class GameState {
    private static GameState instance;
    
    private int currentLevel;
    private int score;
    private List<GameObject> gameObjects;
    private boolean gameOver;
    private boolean levelComplete;
    
    /**
     * Private constructor for singleton pattern.
     */
    private GameState() {
        currentLevel = 1;
        score = 0;
        gameObjects = new ArrayList<>();
        gameOver = false;
        levelComplete = false;
    }
    
    /**
     * Gets the singleton instance.
     */
    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }
    
    /**
     * Gets the current level.
     */
    public int getCurrentLevel() {
        return currentLevel;
    }
    
    /**
     * Sets the current level.
     */
    public void setCurrentLevel(int level) {
        this.currentLevel = level;
    }
    
    /**
     * Gets the current score.
     */
    public int getScore() {
        return score;
    }
    
    /**
     * Adds points to the score.
     */
    public void addScore(int points) {
        this.score += points;
    }
    
    /**
     * Gets the list of game objects.
     */
    public List<GameObject> getGameObjects() {
        return gameObjects;
    }
    
    /**
     * Adds a game object to the list.
     */
    public void addGameObject(GameObject obj) {
        gameObjects.add(obj);
    }
    
    /**
     * Removes a game object from the list.
     */
    public void removeGameObject(GameObject obj) {
        gameObjects.remove(obj);
    }
    
    /**
     * Clears all game objects.
     */
    public void clearGameObjects() {
        gameObjects.clear();
    }
    
    /**
     * Checks if the game is over.
     */
    public boolean isGameOver() {
        return gameOver;
    }
    
    /**
     * Sets the game over state.
     */
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
    
    /**
     * Checks if the level is complete.
     */
    public boolean isLevelComplete() {
        return levelComplete;
    }
    
    /**
     * Sets the level complete state.
     */
    public void setLevelComplete(boolean levelComplete) {
        this.levelComplete = levelComplete;
    }
    
    /**
     * Resets the game state for a new game.
     */
    public void resetGame() {
        currentLevel = 1;
        score = 0;
        gameObjects.clear();
        gameOver = false;
        levelComplete = false;
    }
    
    /**
     * Advances to the next level.
     */
    public void nextLevel() {
        currentLevel++;
        gameObjects.clear();
        levelComplete = false;
    }
}
