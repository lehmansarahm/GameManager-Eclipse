package edu.temple.gamemanager;

/**
 * The primary logic class for the Game Manager library.  Allows us to monitor
 * the user's location and fire off "restricted area" events according to whatever
 * method we choose.  The Unity environment (or any environment using this library)
 * has no knowledge of or need to know how the location is determined; it only
 * responds to the generated events.
 */
public class LocationTracker {
	/**
	 * Publicly available method, allowing the Unity environment to provide
	 * an instance of the listener class for us to manipulate.
	 */
	public void SetLocationUpdateListener(LocationUpdateListener listener) {
        // do something
	}
}