package hills;

import java.util.ArrayList;
import java.util.LinkedList;

public class StellarBody {
	private double x;
	private double y;
	private double xVel;
	private double yVel;
	private double xAccel;
	private double yAccel;
	
	private double mass;
	
	private LinkedList<double[]> starPath;
	private int starPathSize;
	
	private double potentialEnergy;
	
	public StellarBody(double m) {
		this.x = 0;
		this.y = 0;
		this.xVel = 0;
		this.yVel = 0;
		this.xAccel = 0;
		this.yAccel = 0;
		
		this.mass = m;
		
		this.starPath = new LinkedList<double[]>();
		this.starPathSize = 365;
	}
	
	public StellarBody(double x, double y, double m) {
		this.x = x;
		this.y = y;
		this.xVel = 0;
		this.yVel = 0;
		this.xAccel = 0;
		this.yAccel = 0;
		
		this.mass = m;
		
		this.starPath = new LinkedList<double[]>();
		this.starPathSize = 365;
	}
	
	public StellarBody(double x, double y, double xVel, double yVel, double m) {
		this.x = x;
		this.y = y;
		this.xVel = xVel;
		this.yVel = yVel;
		this.xAccel = 0;
		this.yAccel = 0;
		
		this.mass = m;
		
		this.starPath = new LinkedList<double[]>();
		this.starPathSize = 365;
	}
	
	public StellarBody(double x, double y, double xVel, double yVel, double m, int sps) {
		this.x = x;
		this.y = y;
		this.xVel = xVel;
		this.yVel = yVel;
		this.xAccel = 0;
		this.yAccel = 0;
		
		this.mass = m;
		
		this.starPath = new LinkedList<double[]>();
		this.starPathSize = sps;
	}
	
	public StellarBody(double x, double y, double angle, double velocity, double m, int sps, boolean d) {
		this.x = x;
		this.y = y;
		this.xVel = velocity * Math.cos(angle);
		this.yVel = velocity * Math.sin(angle);
		this.xAccel = 0;
		this.yAccel = 0;
		
		this.mass = m;
		
		this.starPath = new LinkedList<double[]>();
		this.starPathSize = sps;
	}
	
	/**
	 * updates with the current acceleration
	 */
	public void update(double timeStep) {
		this.xVel += (this.xAccel * timeStep);
		this.yVel += (this.yAccel * timeStep);
		
		this.x += (this.xVel * timeStep);
		this.y += (this.yVel * timeStep);
		
		this.starPath.addLast(new double[] {this.x, this.y});
		if(this.starPath.size() > starPathSize) {
			starPath.remove();
		}
	}
	
	/**
	 * updates with a given acceleration vector
	 * @param xAccel - the horizontal component of the acceleration vector
	 * @param yAccel - the vertical component of the acceleration vector
	 */
	public void update(double xAccel, double yAccel, double timeStep) {
		this.xAccel = xAccel;
		this.yAccel = yAccel;
		
		this.xVel += (this.xAccel * timeStep);
		this.yVel += (this.yAccel * timeStep);
		
		this.x += (this.xVel * timeStep);
		this.y += (this.yVel * timeStep);
		
		this.starPath.addLast(new double[] {this.x, this.y});
		if(this.starPath.size() > starPathSize) {
			starPath.remove();
		}
	}
	
	public void updateAcceleration(double xA, double yA) {
		this.xAccel += xA;
		this.yAccel += yA;
	}
	
	public void clearAcceleration() {
		this.xAccel = 0;
		this.yAccel = 0;
	}
	
	public void updatePotentialEnergy(StellarBody b1, StellarBody b2, double G) {
		double p1 = -G * b1.mass * this.mass / (Math.sqrt((this.x - b1.x)*(this.x - b1.x)+(this.y - b1.y)*(this.y - b1.y)));
		double p2 = -G * b2.mass * this.mass / (Math.sqrt((this.x - b2.x)*(this.x - b2.x)+(this.y - b2.y)*(this.y - b2.y)));
		this.potentialEnergy = p1 + p2;
	}
	
	public double getEnergy() {
		return this.potentialEnergy + this.getKE();
	}
	
	public double mass() {
		return this.mass;
	}
	
	public double X() {
		return this.x;
	}
	
	public double Y() {
		return this.y;
	}
	
	public LinkedList<double[]> starPath(){
		return this.starPath;
	}
	
	public double getMass() {
		return this.mass;
	}
	
	public double getXVel() {
		return this.xVel;
	}
	
	public double getYVel() {
		return this.yVel;
	}
	
	public double getXAccel() {
		return this.xAccel;
	}
	
	public double getYAccel() {
		return this.yAccel;
	}
	
	public double getKE() {
		return 0.5 * this.mass * (this.xVel * this.xVel + this.yVel * this.yVel);
	}
}
