package edu.temple.gamemanager;

import android.net.wifi.ScanResult;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.List;
import javax.xml.transform.TransformerException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 */
public class WifiConfigUtility {
	public static String NO_RESULTS_FOUND = "No results found";
	
	private String FOLDER_NAME = "GameManager";
	private String FILENAME = "gm_wificonfig.json";
	private String ROOT_NAME = "game-manager-config";
	private String AREA_RESTRICTION_NAME = "restricted-areas";
	private String BSSID_ATTRIB_NAME = "mac";
	private String RSSI_ATTRIB_NAME = "level";
	private int INDENT_LEVEL = 2;

	private PrintWriter out;
	private JSONArray restrictedAreas;
	
	/**
	 * 
	 */
	public WifiConfigUtility() throws IOException {
    	String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
    		File configFile = initializeConfig();
        	FileWriter fw = new FileWriter(configFile, true);
        	BufferedWriter bw = new BufferedWriter(fw);
        	out = new PrintWriter(bw);
        }
		restrictedAreas = new JSONArray();
	}
	
	/**
	 * 
	 */
	public void finalizeConfig() throws JSONException {
		JSONObject root = new JSONObject();
		JSONArray configSections = new JSONArray();
		
		JSONObject restrictedAreasObject = new JSONObject();
		restrictedAreasObject.put(AREA_RESTRICTION_NAME, restrictedAreas);
		configSections.put(restrictedAreasObject);
		
		root.put(ROOT_NAME, configSections);		
		out.print(root.toString(INDENT_LEVEL));
		out.close();
	}
	
	/**
	 * 
	 */
	public void addScanResultsToRestrictedArea(String areaName, List<ScanResult> scanResults) throws JSONException {
		JSONArray scans = new JSONArray();
		for (int i = 0; i < scanResults.size(); i++) {
			ScanResult result = scanResults.get(i);
			JSONObject scan = new JSONObject();
			scan.put(BSSID_ATTRIB_NAME, result.BSSID);
			scan.put(RSSI_ATTRIB_NAME, result.level);
			scans.put(scan);
		}
		
		JSONObject area = new JSONObject();
		area.put(areaName, scans);
		restrictedAreas.put(area);
	}
	
	/**
	 * 
	 */
	public String compareScanResultsToRestrictedAreas(List<ScanResult> scanResults) throws IOException, JSONException {
    	JSONObject configRoot = loadJSONFromFile();
    	restrictedAreas = configRoot.getJSONArray(AREA_RESTRICTION_NAME);
    	for (int i = 0; i < restrictedAreas.length(); i++) {
    		// iterate through area restrictions
    	}
    	
		return this.NO_RESULTS_FOUND;
	}
	
	/**
	 * 
	 */
	private File initializeConfig() throws IOException {
		File folder = new File(Environment.getExternalStorageDirectory(), FOLDER_NAME);
		if (!folder.exists()) {
		    folder.mkdirs();
		}

    	File configFile = new File(folder, FILENAME);
    	if (configFile.exists()) {
    		configFile.delete();
    	}
    	
    	configFile.createNewFile();
    	return configFile;
	}
	
	/**
	 * 
	 */
	private JSONObject loadJSONFromFile() throws IOException, JSONException {
		File configFile = initializeConfig();
        FileInputStream fis = new FileInputStream(configFile);
        
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        fis.close();
        return (new JSONObject(new String(buffer, "UTF-8")));
	}
}