package edu.temple.gamemanager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.Manifest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author slehr
 *
 */
public class WifiLocationTracker {
    protected Activity mCurrentActivity;
	private WifiManager wifi;
	private WifiScanReceiver wifiReciever;
	
	private String FOLDER_NAME = "GameManager";
	private String FILENAME = "gm_wifiscan.txt";
	
	private List<RestrictedWifi> restrictedValues;
	
	public WifiLocationTracker() {
        restrictedValues = new ArrayList<RestrictedWifi>();
        restrictedValues.add(new RestrictedWifi("18:64:72:4f:5f:12", -48.4));
        restrictedValues.add(new RestrictedWifi("18:64:72:4f:5f:d2", -56.8));
        restrictedValues.add(new RestrictedWifi("18:64:72:4f:6e:32", -57.8));
	}

	/**
	 * 
	 * @param activity
	 */
    public void setActivity(Activity activity)
    {
    	mCurrentActivity = activity;    	
    	int wifiPermissionStatus = 
    			mCurrentActivity.checkCallingOrSelfPermission(Manifest.permission.CHANGE_WIFI_STATE);
    	int extStoragePermissionStatus = 
    			mCurrentActivity.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    	
    	if (wifiPermissionStatus == PackageManager.PERMISSION_GRANTED 
    			&& extStoragePermissionStatus == PackageManager.PERMISSION_GRANTED) {
    		startWifiScanner();
        }
    	else {
            showMessage("Insufficient wifi or external storage access.  "
        		+ "Is the AR manifest properly formatted?", Toast.LENGTH_LONG);
    	}
    	
    }
    
    /**
     * 
     */
    private void startWifiScanner() {
        showMessage("Attempting to start wifi scanner!", Toast.LENGTH_SHORT);            
		wifi = (WifiManager) mCurrentActivity.getSystemService(Context.WIFI_SERVICE);
        mCurrentActivity.registerReceiver(new WifiScanReceiver(), 
    		new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));        
        wifi.startScan();
        showMessage("Wifi scanner started!", Toast.LENGTH_SHORT);
    }
    
	/**
	 * 
	 * @param wifiScanList
	 */
    private void storeWifiScans(List<ScanResult> wifiScanList) {
    	String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
        	try {
        		File folder = new File(Environment.getExternalStorageDirectory(), FOLDER_NAME);
        		if (!folder.exists()) {
        		    folder.mkdirs();
        		}

            	File file = new File(folder, FILENAME);
            	if (file.exists()) {
            		file.delete();
            	}
        		file.createNewFile();
        		
        		FileWriter fw = new FileWriter(file, true);        		
        		BufferedWriter bw = new BufferedWriter(fw);
        		PrintWriter out = new PrintWriter(bw);

        		int restrictedMatches = 0;
        		for(int i = 0; i < wifiScanList.size(); i++){
        			ScanResult result = wifiScanList.get(i);
        			if (result.SSID.equals("tusecurewireless")) {
        				out.println(result.toString());
        				if (scanMatchesRestriction(result.BSSID, result.level)) {
        					restrictedMatches++;
        				}
        			}
        		}

        		out.close();
        		if (restrictedMatches == restrictedValues.size()) {
                    showMessage("YOU ARE IN A RESTRICTED AREA", Toast.LENGTH_LONG);
        		} else {
                    showMessage("You are in an approved area!  Enjoy.", Toast.LENGTH_SHORT);
        		}
        	} catch (IOException ex) {
                showMessage("Unable to store wifi scan file.", Toast.LENGTH_SHORT);
                showMessage("Error message: " + ex.getMessage(), Toast.LENGTH_SHORT);
        	}
        } else {
            showMessage("Unable to write to external storage.", Toast.LENGTH_SHORT);
        }
    }
    
    /**
     * 
     */
    private boolean scanMatchesRestriction(String addr, int rssi) {
		for(int i = 0; i < restrictedValues.size(); i++){
			RestrictedWifi rw = restrictedValues.get(i);
    		if (rw.matchesScan(addr, rssi)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * 
     * @param message
     * @param duration
     */
    private void showMessage(final String message, final int duration) {
        mCurrentActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(mCurrentActivity, message, duration).show();
            }
        });
    }

    /**
     * 
     * @author slehr
     *
     */
    private class WifiScanReceiver extends BroadcastReceiver{
    	/**
    	 * 
    	 * @param c
    	 * @param intent
    	 */
    	public void onReceive(Context c, Intent intent) {
    		// if we got this far, it means that we also have external storage 
    		// writing permissions... prepare to write the scan results to a file
    		storeWifiScans(wifi.getScanResults());
    	}
    }
    
    private class RestrictedWifi {
    	private double RESTRICTION_MARGIN = 0.1;
    	private String bssid;
    	private double avgRSSI;
    	
    	public RestrictedWifi (String bssid, double rssi) {
    		this.bssid = bssid;
    		this.avgRSSI = rssi;
    	}
    	
    	public boolean matchesScan(String bssid, int rssi) {
    		double lowerBound = (avgRSSI * (1 - RESTRICTION_MARGIN));
    		double upperBound = (avgRSSI * (1 + RESTRICTION_MARGIN));
    		return (this.bssid == bssid && lowerBound <= rssi && 
				rssi <= upperBound);
    	}
    }
}