package edu.temple.gamemanager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * @author slehr
 *
 */
public class LocationTracker {
	private Timer timer;
	
	/**
	 * 
	 */
	public void SetLocationUpdateListener(LocationUpdateListener listener) {
		timer = new Timer();
		timer.schedule(new RestrictionToggler(listener), 0, 10000);
	}
	
	class RestrictionToggler extends TimerTask {
		private LocationUpdateListener listener;
		private boolean inRestrictedArea;
		
		/**
		 * 
		 */
		public RestrictionToggler(LocationUpdateListener listener) {
			this.listener = listener;
			this.inRestrictedArea = false;
		}
		
		/**
		 * 
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