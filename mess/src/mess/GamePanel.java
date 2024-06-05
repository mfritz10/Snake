package mess;

import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;



import javax.imageio.ImageIO;
import javax.swing.*;

import java.util.Random;


public class GamePanel extends JPanel implements ActionListener {

	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 25;
	static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT)/UNIT_SIZE;
	static final int DELAY = 75;
	
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	int bodyParts = 6;
	int applesEaten;
	int appleX;
	int appleY;
	char direction = 'R';
	
	boolean running = false;
	Timer timer;
	Random random;
	
	private Image img;
	
	
	
	
	GamePanel() {
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(new Color(0, 150, 0));
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		try {
			img = ImageIO.read(new FileInputStream("images/apple.png"));
		} catch (Exception e) {
			System.out.println("Image not found");
		}
		startGame();
		
	}
	
	public void startGame() {
		newApple();
		running = true;
		timer = new Timer(DELAY, this);
		timer.start();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.setColor(new Color(0,140,0));
		for (int x = 0; x < SCREEN_WIDTH; x += UNIT_SIZE) {
			int start;
			if (x%2 == 0) {
				start = 0;
			} else {
				start = UNIT_SIZE;
			}
			for (int y = start; y < SCREEN_HEIGHT; y += UNIT_SIZE * 2) {
				g.fillRect(x, y,  UNIT_SIZE, UNIT_SIZE);
			}
		}
		draw(g);
	}
	
	public void draw(Graphics g) {
		
		if (running) {
			//g.setColor(Color.red);
			//g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
			
				
			g.drawImage(img, appleX, appleY,
						UNIT_SIZE, UNIT_SIZE,
						null);
			
			for(int i = 0; i < bodyParts; i++) {
				if (i == 0) {
					g.setColor(Color.magenta);
					//g.fillOval(x[i] - (UNIT_SIZE/4), y[i], UNIT_SIZE, (int) (UNIT_SIZE * 0.8));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
					
				} else {
					g.setColor(new Color(200, 0, 200));
					//g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			
			g.setColor(Color.black);
			g.setFont(new Font("Ink Free", Font.BOLD, 40));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());
		
		
		} else {
			gameOver(g);
		}
	}
	
	public void newApple() {
		appleX = random.nextInt((int)SCREEN_WIDTH/UNIT_SIZE) * UNIT_SIZE;
		appleY = random.nextInt((int)SCREEN_HEIGHT/UNIT_SIZE) * UNIT_SIZE;
		
	}
	
	public void move() {
		for (int i = bodyParts; i > 0; i--) {
			x[i] = x[i-1];
			y[i] = y[i-1];
			
		}
		
		switch(direction) {
		case 'U':
			y[0] = y[0] - UNIT_SIZE;
			break;
		case 'D':
			y[0] = y[0] + UNIT_SIZE;
			break;
		case 'L':
			x[0] = x[0] - UNIT_SIZE;
			break;
		case 'R':
			x[0] = x[0] + UNIT_SIZE;
			break;
		}
	}
	
	public void checkApple() {
		if ((x[0] == appleX) && (y[0] == appleY)) {
			bodyParts++;
			applesEaten++;
			newApple();
		}
	}
	
	public void checkCollisions() {
		// checks if head collides with body
		for (int i = bodyParts; i >0; i--) {
			if ((x[0] == x[i]) && (y[0] == y[i])) {
				running = false;
			}
		}
		
		//check if head touches left border
		if (x[0] < 0) {
			running = false;
		}
		
		//check if head touches right border
		if (x[0] > SCREEN_WIDTH-1) {
			running = false;
		}
		
		//check if head touches top border
		if (y[0] < 0) {
			running = false;
		}
		
		//check if head touches bottom border
		if (y[0] > SCREEN_HEIGHT-1) {
			running = false;
		}
		
		if (!running) {
			timer.stop();
		}
	}
	
	public void gameOver(Graphics g) {
		// Score
		g.setColor(Color.black);
		g.setFont(new Font("Ink Free", Font.BOLD, 40));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());
	
		
		// Game Over text
		g.setColor(new Color(200, 0, 0));
		g.setFont(new Font("Ink Free", Font.BOLD, 75));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Width"))/2, SCREEN_HEIGHT/2);
		
		// Restart game
		g.setColor(Color.black);
		g.setFont(new Font("Ink Free", Font.BOLD, 20));
		FontMetrics metrics3 = getFontMetrics(g.getFont());
		g.drawString("Press \"r\" to restart the game", (SCREEN_WIDTH - metrics3.stringWidth("Press \"r\" to restart the game"))/2, (int) (SCREEN_HEIGHT * 0.7));
	
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (running) {
			move();
			checkApple();
			checkCollisions();
		}
		repaint();
	}
	
	public class MyKeyAdapter extends KeyAdapter {

		
		
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if (direction != 'R') {
					direction = 'L';
				}
				break;
				
			case KeyEvent.VK_RIGHT:
				if (direction != 'L') {
					direction = 'R';
				}
				break;
				
			case KeyEvent.VK_UP:
				if (direction != 'D') {
					direction = 'U';
				}
				break;
			
			case KeyEvent.VK_DOWN:
				if (direction != 'U') {
					direction = 'D';
				}
				break;
				
				
			}
		}
	}
	
	}

