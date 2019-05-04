/*
 * File: Breakout.java
 * -------------------
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;


public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
	// These should be used when setting up the initial size of the game,
	// but in later calculations you should use getWidth() and getHeight()
	// rather than these constants for accurate size information.
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 10;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 10;

	// Separation between neighboring bricks, in pixels
	public static final double BRICK_SEP = 4;

	// Width of each brick, in pixels
	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	// Height of each brick, in pixels
	public static final double BRICK_HEIGHT = 8;

	// Offset of the top brick row from the top, in pixels
	public static final double BRICK_Y_OFFSET = 70;

	// Dimensions of the paddle
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;

	// Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30; //30

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 150.0; //60

	// Number of turns 
	public static final int NTURNS = 3;
	
	private GRect paddle = null;
	
	private GOval ball = null;
	
	private double vx, vy;
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	private int counter = 100;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

		/* You fill this in, along with any subsidiary methods */
		setupBricks();		
		setupPaddle();
		setupBall();	
		
		addMouseListeners();		
	}
	
	/**
	 * Method: setupBricks
	 * -------------------
	 * This method will setup the bricks. The row consist of 10 bricks and
	 * the column consist of 10 bricks. The color of the bricks remain constant
	 * for two rows and run in the following sequence: RED, ORANGE, YELLOW,
	 * GREEN, and CYAN.
	 */
	
	private void setupBricks() {
		double originX = (getWidth() - NBRICK_COLUMNS * BRICK_WIDTH - 9 * BRICK_SEP) / 2;		
		Color color = null;
		
		for (int r = 0; r < NBRICK_ROWS; r++) {
			// Setup the color the first two rows to RED, then follows by ORANGE,
			// YELLOW, GREEN, and CYAN.
			if (r < 2) {
				color = Color.RED;				
			} else if (r < 4) {
				color = Color.ORANGE;
			} else if (r < 6) {
				color = Color.YELLOW;
			} else if (r < 8) {
				color = Color.GREEN;				
			} else {
				color = Color.CYAN;
			}
			
			for (int c = 0; c < NBRICK_COLUMNS; c++) {				
				double x = originX + c * (BRICK_WIDTH + BRICK_SEP);
				double y = BRICK_Y_OFFSET + r * (BRICK_HEIGHT + BRICK_SEP);
				GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFillColor(color);
				brick.setFilled(true);
				add(brick, x, y);
			}		
		}			
	}
	
	/**
	 * Method: addPaddle
	 * ------------------
	 * This method will add a paddle to the bottom center of the canvas.
	 */
	private void setupPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);	
		paddle.setColor(Color.BLACK);
		double x = (getWidth() - PADDLE_WIDTH) / 2;
		double y = getHeight() - PADDLE_Y_OFFSET;
		add(paddle, x, y);				
	}
	
	/**
	 * Method: mouseMoved
	 * ------------------
	 * This method will move the paddle along the x coordinate of
	 * the mouse. The paddle will always inside the canvas even
	 * when the mouse moves outside the canvas.
	 */
	public void mouseMoved(MouseEvent e) {
		double x = e.getX()- PADDLE_WIDTH/2;
		double y = getHeight() - PADDLE_Y_OFFSET;
		
		if (x + PADDLE_WIDTH >= getWidth()) {
			x = getWidth() - PADDLE_WIDTH;
		}		
		if (x <= 0) {
			x = 0;
		}
		paddle.setLocation(x, y);					
	}
	
	/**
	 * Method: setupBall
	 * -----------------
	 * This method will create a ball that will bounce when it hits
	 * the wall. When the ball hits the paddle it will bounce back.
	 * When the ball hits the brick, it will bounce back and removes
	 * the brick simultaneously.
	 */
	private void setupBall() {		
		makeBall();
		vx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		vy = VELOCITY_Y;
		
		waitForClick();
		
		while(true) {
			
			if (hitLeftWall() || hitRightWall()) {
				vx = -vx;
			}
			if (hitTopWall()) {
				vy = -vy;
			}
			if (hitBottomWall()) {
				vy = -vy;
				remove(ball);				
				gameOverLabel();
				break;
			}
			
			checkForCollisions();
			if (counter == 0) {				
				remove(ball);
				youWinLabel();
				break;
			}
			
			ball.move(vx, vy);
			pause(DELAY);
		}			
	}
	
	/**
	 * Method: makeBall
	 * ----------------
	 * This method will create a ball with radius: BALL_RADIUS.
	 * Then add it to the canvas.
	 */
	private void makeBall() {
		ball = new GOval(2*BALL_RADIUS, 2*BALL_RADIUS);
		double x = (getWidth() - BALL_RADIUS) / 2;
		double y = (getHeight() - BALL_RADIUS) / 2;
		ball.setFilled(true);		
		add(ball, x, y);
	}
	
	/**
	 * Method: hitRightWall
	 * --------------------
	 * This method will check if the ball collides with the
	 * right wall and return a boolean value.
	 * @return: true if the ball collides with right wall
	 */
	private boolean hitRightWall() {
		return ball.getX() >= getWidth() - ball.getWidth();
	}
	
	/**
	 * Method: hitLeftWall
	 * --------------------
	 * This method will check if the ball collides with the
	 * left wall and return a boolean value.
	 * @return: true if the ball collides with left wall
	 */
	private boolean hitLeftWall() {
		return ball.getX() <= 0;
	}
	
	/**
	 * Method: hitToptWall
	 * --------------------
	 * This method will check if the ball collides with the
	 * top wall and return a boolean value.
	 * @return: true if the ball collides with top wall
	 */
	private boolean hitTopWall() {
		return ball.getY() <= 0;
	}
	
	/**
	 * Method: hitBottomWall
	 * --------------------
	 * This method will check if the ball collides with the
	 * bottom wall and return a boolean value.
	 * @return: true if the ball collides with bottom wall
	 */
	private boolean hitBottomWall() {
		return ball.getY() >= getHeight() - ball.getHeight();
	}
	
	/**
	 * Method: checkForCollisions
	 * --------------------------
	 * This method will check when the ball collides with the
	 * bricks or the paddle. If the ball collides with the paddle,
	 * the ball will bounce back. If the ball collides with the
	 * bricks, the ball will bounce back and removes the brick
	 * simultaneously.
	 */
	private void checkForCollisions() {
		GObject collider = getCollidingObject();			
		removeBricks(collider);				
	}
	
	/**
	 * Method: getCollidingObject
	 * --------------------------
	 * This method will check when the ball collides with an object.
	 * Then it will return that colliding object.
	 * @return: return the colliding object
	 */
	private GObject getCollidingObject() {
		GObject obj = getElementAt(ball.getX(), ball.getY());
		if (obj != null) {
			return obj;
		} else {
			obj = getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY());
			if (obj != null) {
				return obj;
			} else {
				obj = getElementAt(ball.getX(), ball.getY() + 2*BALL_RADIUS);
				if (obj != null) {
					return obj;
				} else {
					obj = getElementAt(ball.getX() + 2*BALL_RADIUS, 
							ball.getY() + 2*BALL_RADIUS);
					if (obj != null) {
						return obj;
					}
				}
			}
		}
		return null;		
	}	
	
	/**
	 * Method: removeBricks
	 * --------------------
	 * This method will check the colliding object. If the colliding
	 * object is a paddle, the ball will bounce back. If the colliding
	 * object is a brick, the brick will be removed and the ball
	 * will bounce back simultaneously.
	 * @param collider
	 */
	private void removeBricks(GObject collider) {		
		if (collider == paddle) {
			vy = -vy;			
			
		} else if (collider != null) {
			counter--;
			vy = -vy;			
			remove(collider);
			println(counter);			
		}				
	}
	
	private void gameOverLabel() {
		GLabel label = new GLabel("GAME OVER");
		label.setFont("SansSerif-30");
		double x = (getWidth() - label.getWidth()) / 2;
		double y = (getHeight() - label.getHeight()) / 2;
		add(label, x, y);	
	}
	
	private void youWinLabel() {
		GLabel label = new GLabel("YOU WIN!");
		label.setFont("SansSerif-30");
		double x = (getWidth() - label.getWidth()) / 2;
		double y = (getHeight() - label.getHeight()) / 2;
		add(label, x, y);	
	}

}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

