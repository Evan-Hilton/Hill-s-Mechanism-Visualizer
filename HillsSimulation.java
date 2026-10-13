package hills;

import javax.swing.*;

import PersonalProjects.Game3D.MyKeyListener;
import PersonalProjects.Game3D.MyMouseListener;
import PersonalProjects.Game3D.MyMouseMotionListener;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class HillsSimulation extends JPanel {
	// basic information about the program
	public static int width = 900;
	public static int height = 600; // 900
	public static int frameRate = 1;
	public static int currentFrame = 0;
	public static Color backgroundColor = Color.white;
	public static Random rng = new Random();

	// current units: AU, Solar Mass, days

	// for rendering ONLY
	public static double bHRadius = 10;
	public static double starRadius = 3;
	public static double scale = 10;
	public static int starPathSize = 150;
	public static boolean starPathing = false;

	public static double timeStep = 0.1; // days
	public static boolean paused = true;
	public static double zoomPower = 0;
	public static double xView = 0;
	public static double yView = 0;
	public static boolean starView = false;

	// other instance variables
	// the length of one side
	public static double G = 2.934e-4;
	public static double bHMass = 4e6;
	public static double bSMass1 = 3;
	public static double bSMass2 = 3;
	public static double binaryMajorAxis = 1;
	public static double rbt = 14 * (binaryMajorAxis / (0.1)) * Math.pow(1 / (bSMass1 + bSMass2), 1 / 3)
			* Math.pow(bHMass / 1e6, 1 / 3);

//	public static double binaryInitialXVel = 1.3727;
//	public static double binaryInitialYVel = -2.273;
	public static double binaryInitialX = -490.2781;
	public static double binaryInitialY = 664.11823;
	public static double initialVel = 1.696; //1.696
	public static double binaryInitialXVel = 1.3727;
	public static double binaryInitialYVel = -2;

	public static StellarBody centralBlackHole = new StellarBody(0, 0, bHMass);
	public static ArrayList<StellarBody[]> binaries = new ArrayList<StellarBody[]>();
	public static ArrayList<Color> binaryColors = new ArrayList<Color>();

	public static double star1Energy;
	public static double star2Energy;

	public static void initialize() {
		//there are 5 paramaters: initial velocity, initial angle, a, initial phase, orbital velocity (not as relevant i think)
		int numBinaries = 10; // 10
		for(int i = 0; i < numBinaries; i ++) {
			for(int r = 0; r < 1; r ++) { // 4
				StellarBody[] binary = new StellarBody[2];
				
				//orbital velocity
				double binaryOrbitSpeed = Math.sqrt(G * bSMass1 / (binaryMajorAxis * 2.2));
				System.out.println(binaryOrbitSpeed);
				
				//initial phase
				double phase = (1. * i / numBinaries) * Math.PI;
				double xOffset = (binaryMajorAxis / 2) * Math.cos(phase);
				double yOffset = (binaryMajorAxis / 2) * Math.sin(phase);
				double xVelOff = binaryOrbitSpeed * Math.sin(phase);
				double yVelOff = -binaryOrbitSpeed * Math.cos(phase);
				
		 		//initial angle
				double angle = ((1. * r / numBinaries) * 0.04) - 0.7; // - 0.88
				
				//initial velocity
				double initVel = initialVel + 0;
				double binInitXVel = initVel * Math.cos(angle);
				double binInitYVel = initVel * Math.sin(angle);
				
				binary[0] = new StellarBody(binaryInitialX + xOffset, binaryInitialY + yOffset, binInitXVel + xVelOff, binInitYVel + yVelOff, bSMass1, starPathSize);
				binary[1] = new StellarBody(binaryInitialX - xOffset, binaryInitialY - yOffset, binInitXVel - xVelOff, binInitYVel - yVelOff, bSMass2, starPathSize);
				binaries.add(binary);
				binaryColors.add(Color.getHSBColor((float)(1. * i / numBinaries), 1, 1));
				//binaryColors.add(Color.black);
			}
		}
	}

	public static void mainLoop() {
		if (!paused) {
			//System.out.println(binaries.get(0)[0].getEnergy() + " " + binaries.get(0)[1].getEnergy());			
			xView = 0;
			yView = 0;
			if(starView) {
				for(int i = 0; i < binaries.size(); i ++) {
					xView -= (binaries.get(i)[0].X() + binaries.get(i)[1].X());
					yView -= (binaries.get(i)[0].Y() + binaries.get(i)[1].Y());
				}
				xView /= (2. * binaries.size());
				yView /= (2. * binaries.size());
			}
			
			for(int i = 0; i < binaries.size(); i ++) {
				updateAcceleration(binaries.get(i)[0], binaries.get(i)[1]);
				updateAcceleration(binaries.get(i)[1], binaries.get(i)[0]);
			}
			
			for(int i = 0; i < binaries.size(); i ++) {
				updateAcceleration(binaries.get(i)[0], centralBlackHole);
				updateAcceleration(binaries.get(i)[1], centralBlackHole);
			}
			
			for(int i = 0; i < binaries.size(); i ++) {
				binaries.get(i)[0].update(timeStep);
				binaries.get(i)[0].clearAcceleration();
				binaries.get(i)[1].update(timeStep);
				binaries.get(i)[1].clearAcceleration();
			}
			
			for(int i = 0; i < binaries.size(); i ++) {
				binaries.get(i)[0].updatePotentialEnergy(binaries.get(i)[1], centralBlackHole, G);
				binaries.get(i)[1].updatePotentialEnergy(binaries.get(i)[0], centralBlackHole, G);
			}
		}
	}

	public static void paintLoop(Graphics2D g) {
		g.setColor(Color.black);
		
		int count = 0;
		for(int i = 0; i < binaries.size(); i ++) {
			count ++;
			g.setColor(binaryColors.get(i));
			if(binaries.get(i)[0].getEnergy() > 0) {
				g.fillRect(2 + count * 8, (int) (-binaries.get(i)[0].getEnergy() * 150) + (int)(height / 2), 6, (int) (binaries.get(i)[0].getEnergy() * 150));
			}else {
				g.fillRect(2 + count * 8, (int)(height / 2), 6, (int) (-binaries.get(i)[0].getEnergy() * 150));
			}
			count ++;
			if(binaries.get(i)[1].getEnergy() > 0) {
				g.fillRect(2 + count * 8, (int) (-binaries.get(i)[1].getEnergy() * 150) + (int)(height / 2), 6, (int) (binaries.get(i)[1].getEnergy() * 150));
			}else {
				g.fillRect(2 + count * 8, (int)(height / 2), 6, (int) (-binaries.get(i)[1].getEnergy() * 150));
			}
			count ++;
			if((binaries.get(i)[1].getEnergy() + binaries.get(i)[0].getEnergy()) > 0) {
				g.fillRect(2 + count * 8, (int) (-(binaries.get(i)[1].getEnergy() + binaries.get(i)[0].getEnergy()) * 150) + (int)(height / 2), 6, (int) ((binaries.get(i)[1].getEnergy() + binaries.get(i)[0].getEnergy()) * 150));
			}else {
				g.fillRect(2 + count * 8, (int)(height / 2), 6, (int) ((-binaries.get(i)[1].getEnergy() + -binaries.get(i)[0].getEnergy()) * 150));
			}
		}
		
		g.fillRect((int)(width - 10 - (scale * 50)), (int)(height - 60), (int)(scale * 50), 5); // 50 AU
		g.fillRect((int)(width - 10 - (scale * 5000)), (int)(height - 50), (int)(scale * 5000), 5); // 5000
		g.setColor(Color.black);

		// translate the camera to the average of the two stars
		g.translate(width / 2, height / 2);
		g.translate(scale * xView, scale * yView);

		// circle with binary tidal disruption radius
		g.drawOval((int) (-rbt * scale), (int) (-rbt * scale), (int) (rbt * 2 * scale), (int) (rbt * 2 * scale));

		// chat said this was the disruption radius (the closer the better ig
		g.drawOval(-42 * (int) scale / 2, -42 * (int) scale / 2, 42 * (int) scale, 42 * (int) scale);

		// black hole
		paintCircle(centralBlackHole.X() * scale, centralBlackHole.Y() * scale, bHRadius, g);

		//stars
		ArrayList<StellarBody[]> binariesCopy = (ArrayList<StellarBody[]>)binaries.clone();
		for(int i = 0; i < binariesCopy.size(); i ++) {
			g.setColor(binaryColors.get(i));
			paintCircle(binariesCopy.get(i)[0].X() * scale, binariesCopy.get(i)[0].Y() * scale, starRadius, g);
			paintCircle(binariesCopy.get(i)[1].X() * scale, binariesCopy.get(i)[1].Y() * scale, starRadius, g);
			if(starPathing) {
				LinkedList<double[]> starPath = binariesCopy.get(i)[0].starPath();
				drawStarPath(starPath, g);
				starPath = binariesCopy.get(i)[1].starPath();
				drawStarPath(starPath, g);
			}
		}
	}

	public static void drawStarPath(LinkedList<double[]> path, Graphics2D g) {
		LinkedList<double[]> pathCopy = (LinkedList<double[]>) path.clone();
		if (pathCopy != null && pathCopy.size() != 0) {
			double[] prev = pathCopy.get(0);
			for (int r = 1; r < pathCopy.size(); r++) {
				double[] next = pathCopy.get(r);
				g.drawLine((int) (prev[0] * scale), (int) (prev[1] * scale), (int) (next[0] * scale),
						(int) (next[1] * scale));
				prev = next;
			}
		}
	}

	public static double getDistance(StellarBody s1, StellarBody s2) {
		return Math.sqrt(Math.pow(s1.X() - s2.X(), 2) + Math.pow(s1.Y() - s2.Y(), 2));
	}

	public static void clearGravity(ArrayList<StellarBody> starss) {
		for (int i = 0; i < starss.size(); i++) {
			starss.get(i).clearAcceleration();
		}
	}

	public static void updateAcceleration(StellarBody s1, StellarBody s2) {
		double[] gravityForce = calculateGravityForce(s2, s1, G);
		s1.updateAcceleration(gravityForce[0] / s1.mass(), gravityForce[1] / s1.mass());
	}

	public static void doAcceleration(ArrayList<StellarBody> starss, double ts) {
		for (int i = 0; i < starss.size(); i++) {
			starss.get(i).update(ts);
		}
	}

	public static double[] calculateGravityForce(StellarBody b1, StellarBody b2, double G) {
		double[] directionVector = new double[] { 1. * b1.X() - b2.X(), 1. * b1.Y() - b2.Y() };
		double distance = Math.sqrt(directionVector[0] * directionVector[0] + directionVector[1] * directionVector[1]);
		directionVector[0] /= distance; // these two lines just to make the direction vector a unit vector
		directionVector[1] /= distance;

		double force = G * b1.mass() * b2.mass() / (distance * distance);
		directionVector[0] *= force;
		directionVector[1] *= force;

		return directionVector;
	}

	public static void paintCircle(double x, double y, double r, Graphics2D g) {
		g.fillOval((int) (x - r), (int) (y - r), (int) (r * 2), (int) (r * 2));
	}

	// ------------------------------------------- o
	// -------------------------------------------------

	/**
	 * These below 2 methods are setup and do not need to be changed; mainLoop() and
	 * paintLoop() will be called every frame. Frame rate is determined above in the
	 * static class variables. However, the frame rate corresponds to something
	 * different than normal in this program: frameRate describes how many
	 * milliseconds the system waits before each call of mainLoop() and paintLoop(),
	 * so if either of these methods takes a non-insignificant time to run, the
	 * frame rate of the program will not correspond to the frameRate variable
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		// antialiasing off means hard edges, antialiasing on means soft edges
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// background
		g.setColor(backgroundColor);
		g.fillRect(0, 0, 2000, 1000);

		// calling paintLoop()
		paintLoop(g2d);
	}

	public HillsSimulation() {
		KeyListener d = new MyKeyListener();
		addKeyListener(d);
		MouseListener mML = new MyMouseListener();
		addMouseListener(mML);
		MouseMotionListener mMML = new MyMouseMotionListener();
		addMouseMotionListener(mMML);
		setFocusable(true);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Java 2D Graphics Example");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new HillsSimulation());
		frame.setSize(width, height);
		frame.setVisible(true);
		initialize();
		while (true) {
			// currentFrame describes how many frames have occurred throughout the duration
			// of run time
			currentFrame++;
			frame.repaint();
			mainLoop();
			try {
				Thread.sleep(frameRate);
			} catch (Exception e) {
				System.out.println("Error!");
			}
		}
	}

	public class MyKeyListener implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
			// this is a dumb method, use 'keyPressed(KeyEvent e)' unless you know what
			// you're doing
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_EQUALS) {
				scale *= 2;
				zoomPower--;
			}
			if (e.getKeyCode() == KeyEvent.VK_MINUS) {
				scale /= 2;
				zoomPower++;
			}

			if (e.getKeyCode() == KeyEvent.VK_P) {
				timeStep *= 2;
			}
			if (e.getKeyCode() == KeyEvent.VK_O) {
				timeStep /= 2;
			}

			if (e.getKeyCode() == KeyEvent.VK_W) {
				yView += scale / 4;
			}
			if (e.getKeyCode() == KeyEvent.VK_A) {
				xView += scale / 4;
			}
			if (e.getKeyCode() == KeyEvent.VK_S) {
				yView -= scale / 4;
			}
			if (e.getKeyCode() == KeyEvent.VK_D) {
				xView -= scale / 4;
			}

			if (e.getKeyCode() == KeyEvent.VK_B) {
				timeStep *= -1;
			}

			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				paused = !paused;
			}
			
			if (e.getKeyCode() == KeyEvent.VK_S) {
				starView = !starView;
			}

		}

		@Override
		public void keyReleased(KeyEvent e) {

		}
	}

	public class MyMouseListener implements MouseListener {
		@Override
		public void mouseExited(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {

		}

		@Override
		public void mouseClicked(MouseEvent e) {

		}
	}

	public class MyMouseMotionListener implements MouseMotionListener {
		@Override
		public void mouseDragged(MouseEvent e) {

		}

		@Override
		public void mouseMoved(MouseEvent e) {

		}
	}
}