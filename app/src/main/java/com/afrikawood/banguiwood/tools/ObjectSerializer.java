package com.afrikawood.banguiwood.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

public class ObjectSerializer {
	
	// http://stackoverflow.com/questions/4118751/how-do-i-serialize-an-object-and-save-it-to-a-file-in-android
	// http://stackoverflow.com/questions/13392571/serializing-objects-into-files-in-android

	public static void serialize(Object object, File toFile) {
		
		try {
			
			FileOutputStream fileOutputStream = new FileOutputStream(toFile);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(object);
			objectOutputStream.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static Object unserialize(File fromFile) {
		
		Object object = null;
		
		if (fromFile.exists()) {
			try {
				
				FileInputStream fileInputStream = new FileInputStream(fromFile);
				ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
				object = objectInputStream.readObject();
		        objectInputStream.close();
	  
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		return object;
		
	}

}
