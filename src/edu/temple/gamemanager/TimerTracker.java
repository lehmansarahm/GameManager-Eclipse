package edu.temple.gamemanager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A secondary logic class for the Game Manager library.  Allows us to fire
 * regularly timed update events.
 */
public class TimerTracker {
	private Timer timer;
	
	/**
	 * Publicly available method, allowing the Unity environment to provide
	 * an instance of the listener class for us to manipulate.  In this case,
	 * we will fire the update event as often as the listener requires.
	 */
	public void SetTimerUpdateListener(TimerUpdateListener listener) {
		timer = new Timer();
		timer.schedule(new Updater(listener), 0, 5000);
	}
	
	/**
	 * Nested class to toggle the listener events
	 */
	class Updater extends TimerTask {
		private TimerUpdateListener listener;
		
		/**
		 * Constructor to set the target listener instance
		 */
		public Updater(TimerUpdateListener listener) {
			this.listener = listener;
		}
		
		/**
		 * Timed method to fire off listener update event
		 */
		public void run() {
			this.listener.onTimerUpdate();
		}
	}
}