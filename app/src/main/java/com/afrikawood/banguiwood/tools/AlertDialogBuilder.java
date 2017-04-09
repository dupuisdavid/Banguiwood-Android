package com.afrikawood.banguiwood.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;


public class AlertDialogBuilder {

	public AlertDialogBuilder() {
		
	}

	public static void build(Context context, String title, String message) {
		new AlertDialog.Builder(context)
		.setTitle(title)
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton("OK", new OnClickListener() {
        	@Override
        	public void onClick(DialogInterface dialog, int which) {
        		// Whatever...                       
        	}
         }).create().show();
	}
	
}
