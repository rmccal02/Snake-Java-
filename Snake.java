package SnakeGood;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;





class SnakePart {
	
    public int x, y;  // The x and y coordinates of this part of the snake on the screen
    public static final int SIZE = 25;  

   
    public SnakePart(int x, int y) {
        this.x = x;
        this.y = y;
    }

    
    public Rectangle getBounds() {
        return new Rectangle(x, y, SIZE, SIZE);
    }

    // An enumeration that lists the possible directions in which the snake can move
    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}





//snakebody is essentially an arraylist of snakepart
class SnakeBody {
    private ArrayList<SnakePart> body;  // List storing all the segments/parts of the snake
    public SnakePart.Direction direction = SnakePart.Direction.RIGHT;  // Initial direction the snake moves in

    // Constructor initializes the snake body with a single segment at a given x and y position
    public SnakeBody(int initialX, int initialY) {
        body = new ArrayList<>();
        body.add(new SnakePart(initialX, initialY));
    }

    // Moves the snake in its current direction by adding a new segment in front and removing the last segment
    public void move() {
        SnakePart head = body.get(0);

        int newX = head.x, newY = head.y;

        switch (direction) {
            case UP: newY -= SnakePart.SIZE; break;
            case DOWN: newY += SnakePart.SIZE; break;
            case LEFT: newX -= SnakePart.SIZE; break;
            case RIGHT: newX += SnakePart.SIZE; break;
        }

        body.add(0, new SnakePart(newX, newY));
        body.remove(body.size() - 1);  // remove the tail
    }

    // Increases the size of the snake by adding a new segment to it
    public void grow() {
        // Improved growth logic based on the direction
        SnakePart tail = body.get(body.size() - 1);
        int newX = tail.x, newY = tail.y;

        switch (direction) {
            case UP: newY += SnakePart.SIZE; break;
            case DOWN: newY -= SnakePart.SIZE; break;
            case LEFT: newX += SnakePart.SIZE; break;
            case RIGHT: newX -= SnakePart.SIZE; break;
        }

        body.add(new SnakePart(newX, newY));
    }

    // Returns a list of all the segments/parts that make up the snake's body
    public ArrayList<SnakePart> getParts() {
        return body;
    }

    // returns first segment of snake 
    public SnakePart getHead() {
        return body.get(0);
    }
}








public class Snake {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        GamePanel gamePanel = new GamePanel();  
        gamePanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        gamePanel.setBackground(Color.BLACK); 
        
        
        SidePanel sidePanel = new SidePanel();
        
        frame.setLayout(new BorderLayout());
        frame.add(gamePanel, BorderLayout.CENTER);
        frame.add(sidePanel, BorderLayout.EAST);
        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}







class GamePanel extends JPanel implements ActionListener, KeyListener {

    private static final int SQUARE_SIZE = 25;
    private SnakeBody snake;

    private Timer timer;
    private Food food;
    
    public void startTimer() {
        timer.start();
    }

    public void stopTimer() {
        timer.stop();
    }

    public GamePanel() {
    	snake = new SnakeBody(5 * SQUARE_SIZE, 7 * SQUARE_SIZE);
    	this.addKeyListener(this);
    	this.setFocusable(true);
    	this.requestFocusInWindow();

        timer = new Timer(100, this);  
        timer.start();
        this.addKeyListener(this);
        this.setFocusable(true);
        food = new Food(getRandomCoordinate(), getRandomCoordinate());

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int x = 0; x < getWidth(); x += SQUARE_SIZE) {
            for (int y = 0; y < getHeight(); y += SQUARE_SIZE) {
                g.drawRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
        g.setColor(Color.GREEN);
     // Draw the snake's body
        g.setColor(Color.GREEN);
        for (SnakePart part : snake.getParts()) {
            g.fillRect(part.x, part.y, SnakePart.SIZE, SnakePart.SIZE);
        }

        g.setColor(Color.RED);
        g.fillRect(food.x, food.y, Food.SIZE, Food.SIZE);

    }
    

    @Override
    public void actionPerformed(ActionEvent e) {
        snake.move();
        repaint();

        // Check collision with self or wall
        if (checkCollisionWithSelf() || checkCollisionWithWall()) {
            gameOver();
            return;
        }

        if (snake.getHead().getBounds().intersects(food.getBounds())) {
            food.setNewLocation(snake);  // Note the modified method signature
            snake.grow();
        }
    }
    

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
        	case KeyEvent.VK_UP:
        		if (snake.direction != SnakePart.Direction.DOWN) snake.direction = SnakePart.Direction.UP;
        		break;
        	case KeyEvent.VK_DOWN:
        		if (snake.direction != SnakePart.Direction.UP) snake.direction = SnakePart.Direction.DOWN;
        		break;
        	case KeyEvent.VK_LEFT:
        		if (snake.direction != SnakePart.Direction.RIGHT) snake.direction = SnakePart.Direction.LEFT;
        		break;
        	case KeyEvent.VK_RIGHT:
        		if (snake.direction != SnakePart.Direction.LEFT) snake.direction = SnakePart.Direction.RIGHT;
            	break;
        }
    }
    public static int getRandomCoordinate() {
        int maxSquares = Snake.WIDTH / SQUARE_SIZE;
        return (int) (Math.random() * maxSquares) * SQUARE_SIZE;
    }

    
    

    private boolean checkCollisionWithSelf() {
        if(snake.getParts().size() <= 1) return false;  // Avoid checking when the snake is of length 1

        SnakePart head = snake.getHead();
        for (int i = 1; i < snake.getParts().size(); i++) {
            if (head.getBounds().intersects(snake.getParts().get(i).getBounds())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkCollisionWithWall() {
        SnakePart head = snake.getHead();
        return head.x < 0 || head.x >= Snake.WIDTH || head.y < 0 || head.y >= Snake.HEIGHT;
    }

    private void gameOver() {
        timer.stop();
        JOptionPane.showMessageDialog(this, "Game Over!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }


    @Override
    public void keyReleased(KeyEvent e) {}
}
//end game panel






class Food {
    public int x, y;
    public static final int SIZE = 25;

    public Food(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, SIZE, SIZE);
    }

    public void setNewLocation(SnakeBody snake) {
        int attempts = 0;
        do {
            this.x = GamePanel.getRandomCoordinate();
            this.y = GamePanel.getRandomCoordinate();
            attempts++;
        } while (isOnSnake(snake) && attempts < 100);  // Limiting the number of attempts
    }

    private boolean isOnSnake(SnakeBody snake) {
        for (SnakePart part : snake.getParts()) {
            if (getBounds().intersects(part.getBounds())) {
                return true;
            }
        }
        return false;
    }

    
}




class SidePanel extends JPanel {
    private JButton pauseButton;
    private JButton unpauseButton;
    private JLabel currentScoreLabel;
    private JLabel highScoreLabel;
    private int currentScore = 0;
    private int highScore = 0;
    public void pauseGame() {
        GamePanel gamePanel = (GamePanel) getParent().getComponent(0);
        gamePanel.stopTimer();
        pauseButton.setEnabled(false);
        unpauseButton.setEnabled(true);
    }

    public void unpauseGame() {
        GamePanel gamePanel = (GamePanel) getParent().getComponent(0);
        gamePanel.startTimer();
        pauseButton.setEnabled(true);
        unpauseButton.setEnabled(false);
    }

    public SidePanel() {
        this.setLayout(new GridLayout(6, 1));
        
        

        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(e -> {
            pauseGame();
            pauseButton.setEnabled(false);
            unpauseButton.setEnabled(true);
        });

        unpauseButton = new JButton("Unpause");
        unpauseButton.addActionListener(e -> {
            unpauseGame();
            pauseButton.setEnabled(true);
            unpauseButton.setEnabled(false);
        });

        currentScoreLabel = new JLabel("Current Score: 0");
        highScoreLabel = new JLabel("High Score: 0");

        this.add(pauseButton);
        this.add(unpauseButton);
        this.add(currentScoreLabel);
        this.add(highScoreLabel);
    }

    public void updateScore(int score) {
        currentScore = score;
        currentScoreLabel.setText("Current Score: " + currentScore);
        if (currentScore > highScore) {
            highScore = currentScore;
            highScoreLabel.setText("High Score: " + highScore);
        }
    }

    public void reset() {
        currentScore = 0;
        currentScoreLabel.setText("Current Score: " + currentScore);
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int score) {
        highScore = score;
        highScoreLabel.setText("High Score: " + highScore);
    }
}
