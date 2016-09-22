package edu.temple.gamemanager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * The primary logic class for the Game Manager library.  Allows us to monitor
 * the user's location and fire off "restricted area" events according to whatever
 * method we choose.  The Unity environment (or any environment using this library)
 * has no knowledge of or need to know how the location is determined; it only
 * responds to the generated events.
 */
public class LocationTracker {
	private Timer timer;
	
	/**
	 * Publicly available method, allowing the Unity environment to provide
	 * an instance of the listener class for us to manipulate.  In this case,
	 * we will toggle the area restriction events every 10 seconds.
	 */
	public void SetLocationUpdateListener(LocationUpdateListener listener) {
		timer = new Timer();
		timer.schedule(new RestrictionToggler(listener), 0, 10000);
	}
	
	/**
	 * Nested class to toggle the listener events
	 */
	class RestrictionToggler extends TimerTask {
		private LocationUpdateListener listener;
		private boolean inRestrictedArea;
		
		/**
		 * Constructor to set the target listener instance and initial 
		 * area restriction state
		 */
		public RestrictionToggler(LocationUpdateListener listener) {
			this.listener = listener;
			this.inRestrictedArea = false;
		}
		
		/**
		 * Timed method to toggle and execute the listener events
		 */
		public void run() {
			if (inRestrictedArea) {
				listener.onRestrictedAreaEntered();
			} else {
				listener.onRestrictedAreaLeft();
			}
			inRestrictedArea = !inRestrictedArea;
		}
	}
}