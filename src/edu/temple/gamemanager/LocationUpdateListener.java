package edu.temple.gamemanager;

/**
 * Event listener to allow Unity classes to subscribe 
 * to when the user enters / leaves restricted areas
 */
public interface LocationUpdateListener {
	void onRestrictedAreaEntered();
	void onRestrictedAreaLeft();
}