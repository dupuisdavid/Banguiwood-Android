package com.afrikawood.banguiwood.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.util.Log;

public class AssetsUtilities {

	public static boolean assetExists(Context context, String path) {
	    boolean assetIsOk = false;
	    
	    try {
	        InputStream stream = context.getAssets().open(path);
	        stream.close();
	        assetIsOk = true;
	    } catch (FileNotFoundException e) {
	        Log.w("AssetsUtilities", "assetExists failed: " + e.toString());
	    } catch (IOException e) {
	        Log.w("AssetsUtilities", "assetExists failed: " + e.toString());
	    }
	    
	    return assetIsOk;
	}

}
