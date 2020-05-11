package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class Main extends Canvas implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7604959185792845431L;
	private boolean running;
	private Thread thread;
	//
	private int mX;
	private int mY;
	private float r = 0.5f;
	private float g = 0.5f;
	private float b = 0.0f;
	private int t = 1;

	public static void main(String[] args) {
		new Main().start();
	}
	
	public synchronized void start() {
		if(this.running == true) {
			return;
		}
		this.thread = new Thread(this);
		this.thread.start();
		this.running = true;
	}
	
	public synchronized void stop() {
		this.running = false;
		//clean up
	}
	
	private void init() {
		JFrame frame = new JFrame("Color Picker");
		frame.setSize(960, 800);
		this.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				mX = e.getX();
				mY = e.getY();
			}
		});
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);

		this.requestFocus();
		//
	}
	
	@Override
	public void run() {
		this.init();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int updates = 0;
		int frames = 0;
		while(this.running == true) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1){
				this.tick();
				updates++;
				delta--;
			}
			this.render();
			frames++;
			
			if(System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println("FPS: " + frames + " TICKS: " + updates);
				frames = 0;
				updates = 0;
			}
		}
		System.exit(0);
	}
	
	private void tick() {
		this.t++;
		this.r = this.map(this.mX, 0, this.getWidth()+1, 0.0f, 1.0f);
		this.g = this.map(this.mY, 0, this.getHeight()+1, 0.0f, 1.0f);
		this.b = (float) (0.5f*Math.sin(Math.toRadians(this.t))+0.5f);
	}
	
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform af = g2d.getTransform();
		//start draw
			//bg
		g.setColor(new Color(this.r, this.g, this.b));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		//end draw
		g2d.setTransform(af);
		g.dispose();
		bs.show();
	}

	public float map(float n, float start1, float stop1, float start2, float stop2) {
		return ((n-start1)/(stop1-start1))*(stop2-start2)+start2;
	}
	
}