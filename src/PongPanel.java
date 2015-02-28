import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Time;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class PongPanel extends JPanel implements KeyListener {
	
	Point paddleSize;
	Point ballPosition;
	Point ballDirection;
	Point ballSize;
	int frameRate;
	int gameSpeed;
	int paddleSpeed;
	int score1 = 0;
	int score2 = 0;
	private boolean buttonDownPressed = false;
	private boolean buttonUpPressed = false;
	
	private Point PositionPlayerOne = new Point(10,100);
	private Point PositionPlayerTwo = new Point(425, 100);
	private int paddleDirection = 0;

	
    //construct a PongPanel
    public PongPanel(Point ballDirection, Point ballPosition,  Point ballSize, int frameRate, int gameSpeed, Point paddlePointP1, Point paddlePointP2, Point paddleSize, int paddleSpeed, int score1, int score2){
    	//Set Values
    	this.paddleSize = paddleSize;
    	this.ballSize = ballSize;
    	this.gameSpeed = gameSpeed;
    	this.frameRate = frameRate;
    	this.paddleSpeed = paddleSpeed;
        this.ballPosition = ballPosition;
        this.ballDirection = ballDirection;
        this.PositionPlayerOne =  paddlePointP1; 
        this.PositionPlayerTwo = paddlePointP2;
        this.score1 = score1;
        this.score2 = score2;
        setBackground(Color.BLACK);
        
        //listen to key presses
        setFocusable(true);
        addKeyListener(this);
    }
    
    //paint a ball & components
    public void paintComponent(Graphics g){

        super.paintComponent(g);
        g.setFont(new Font("Arial", 1, 30));
        g.setColor(Color.WHITE);
        //g.drawString("Player1", 3, 15);
        //g.drawString("Player2", 400, 15);
        for (int i = 10; i < 300; i += 43) {
			g.drawString("|", 225, i);
		}
        g.drawString("|", 225, 10);
        //g.drawString("time" , 200, 290);
        g.drawString(score1 + "       " + score2, 175, 33);
        //Draw Ball
        g.fillRect(ballPosition.x, ballPosition.y, ballSize.x, ballSize.y);
        //Draw PlayerOne
        g.fillRect(PositionPlayerOne.x, PositionPlayerOne.y, paddleSize.x, paddleSize.y);
        //Draw Player Two
        g.fillRect(PositionPlayerTwo.x, PositionPlayerTwo.y, paddleSize.x, paddleSize.y);  
    }
    
	/**
	 * 
	 */
	public void keyPressed(KeyEvent event) {
		System.out.println("taste gedrueckt");
		if(event.getKeyCode() == KeyEvent.VK_UP) {
			paddleDirection = -1;
		}else if(event.getKeyCode() == KeyEvent.VK_DOWN) {
			paddleDirection = 1;
		}
		
	}

	/**
	 * 
	 */
	public void keyReleased(KeyEvent event) {
		System.out.println("taste loslassen");
		if(event.getKeyCode() == KeyEvent.VK_UP) {
			paddleDirection = 0;
		}else if(event.getKeyCode() == KeyEvent.VK_DOWN) {
			paddleDirection = 0;
		}
		
	}

	/**
	 * 
	 */
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 
	 * @param ballPosition
	 * @param paddlePointP1
	 * @param paddlePointP2
	 * @param ScorePoint
	 */
	public void setValues(Point ballPosition, Point paddlePointP1, Point paddlePointP2, Point ScorePoint) {
		System.out.println("BALLPOSITION: " + ballPosition);
		this.ballPosition = ballPosition;
		this.PositionPlayerOne = paddlePointP1;
		this.PositionPlayerTwo = paddlePointP2;
		this.score1 = ScorePoint.x;
		this.score2 = ScorePoint.y;	
	}
	
	/**
	 * 
	 * @return
	 */
	public int getValue() {
		System.out.println("PADDLEDIRECTION: " + paddleDirection);
		return paddleDirection;
	}
    
}








