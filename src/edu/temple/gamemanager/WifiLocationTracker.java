package edu.temple.gamemanager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.Manifest;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * @author slehr
 *
 */
public class WifiLocationTracker {
	protected LocationUpdateListener listener;
    protected Activity mCurrentActivity;	
	private WifiConfigUtility wifiConfig;
	
	private Timer timer;
	private boolean firstScan = true;

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
    	
    	try {
        	if (wifiPermissionStatus == PackageManager.PERMISSION_GRANTED 
        			&& extStoragePermissionStatus == PackageManager.PERMISSION_GRANTED) {
        		wifiConfig = new WifiConfigUtility();
        		startWifiScanner();
            }
        	else {
                showMessage("Insufficient wifi or external storage access.  "
            		+ "Is the AR manifest properly formatted?", Toast.LENGTH_LONG);
        	}
    	} catch (Exception ex) {
			showMessage(ex.getMessage(), Toast.LENGTH_LONG);
		}
    }
    
	/**
	 *
	 */
	public void SetLocationUpdateListener(LocationUpdateListener listener) {
		this.listener = listener;
	}
    
    /**
     * 
     */
    private void startWifiScanner() {
        showMessage("Attempting to start wifi scanner!", Toast.LENGTH_SHORT);            
		final WifiManager wifi = (WifiManager) mCurrentActivity.getSystemService(Context.WIFI_SERVICE);
        mCurrentActivity.registerReceiver(new BroadcastReceiver() {
        	/**
        	 * 
        	 * @param c
        	 * @param intent
        	 */
        	public void onReceive(Context c, Intent intent) {
        		try {
        			if (firstScan) {
            			wifiConfig.addScanResultsToRestrictedArea("lab333", wifi.getScanResults());
            			wifiConfig.finalizeConfig();                    
            			firstScan = false;
            			showMessage("Scans logged.  Ready to play!", Toast.LENGTH_SHORT);
        			} else {
        				String area = wifiConfig.compareScanResultsToRestrictedAreas(wifi.getScanResults());
        				handleAreaUpdate(area);
        			}
        		} catch (Exception ex) {
        			showMessage(ex.getMessage(), Toast.LENGTH_LONG);
        		}
        	}
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));        
        
        timer = new Timer();
		timer.schedule(new WifiScanner(wifi), 0, 5000);
        showMessage("Wifi scanner started!", Toast.LENGTH_SHORT);
    }
    
    /**
     * 
     */
    private void handleAreaUpdate(String areaComparisonResult) {
    	boolean listenerAvailable = !(this.listener == null);
		if (areaComparisonResult.equals(WifiConfigUtility.NO_RESULTS_FOUND)) {
			if (listenerAvailable) {
				listener.onRestrictedAreaLeft();
			}
			showMessage("Congrats, you're in an approved area!", Toast.LENGTH_SHORT);
		} else {
			if (listenerAvailable) {
				listener.onRestrictedAreaEntered();
			}
			showMessage("Oh no, you're in a restricted area!", Toast.LENGTH_SHORT);
		}
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
     */
	private class WifiScanner extends TimerTask {
		private WifiManager wifiManager;
		
		/**
		 * 
		 */
		public WifiScanner(WifiManager manager) {
			this.wifiManager = manager;
		}
		
		/**
		 * 
		 */
		public void run() {
			wifiManager.startScan();
		}
	}
}