package com.afrikawood.banguiwood.utils;

import java.io.File;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class Memory {

	public Memory() {
		
	}
	
	public static void getMemoryInfo(Context context) {
		
		MemoryInfo mi = new MemoryInfo();
		String contextString = Context.ACTIVITY_SERVICE;
		ActivityManager activityManager = (ActivityManager) context.getSystemService(contextString);
		activityManager.getMemoryInfo(mi);
		long availableMegs = mi.availMem / 1048576L;
		
		Log.d("MEMORY USAGE", "" + availableMegs);
		
	}
	
	public static void getRuntimeMemoryUsage() {
		getRuntimeMemoryUsage(null);
	}
	
	public static void getRuntimeMemoryUsage(String note) {
		
		final Runtime runtime = Runtime.getRuntime();
		final long usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
		final long maxHeapSizeInMB = runtime.maxMemory() / 1048576L;
		
		
		Log.d("MEMORY USAGE", "" + (note != null ? note + " - " : "") + "maxHeapSizeInMB : " + maxHeapSizeInMB + ", usedMemInMB : " + usedMemInMB);
		
	}
	
	public static long getFreeMemory() {
	    File path = Environment.getDataDirectory();
	    StatFs stat = new StatFs(path.getPath());
	    long blockSize = stat.getBlockSize();
	    long availableBlocks = stat.getAvailableBlocks();
	    return availableBlocks * blockSize;
	}

	public static long getTotalMemory() {
	    File path = Environment.getDataDirectory();
	    StatFs stat = new StatFs(path.getPath());
	    long blockSize = stat.getBlockSize();
	    long availableBlocks = stat.getBlockCount();
	    return availableBlocks * blockSize;
	}

}
