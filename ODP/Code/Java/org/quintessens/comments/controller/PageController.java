package org.quintessens.comments.controller;

import java.util.ArrayList;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonParser;
import com.ibm.domino.xsp.module.nsf.NotesContext;

public class PageController {

	public static ArrayList<JsonJavaObject> loadJSONObjects(String ServerName, String DatabaseName, String ViewName, String Key, Integer ColIdx) throws NotesException {
		ArrayList<JsonJavaObject> JSONObjects = new ArrayList<JsonJavaObject>();		
		
		NotesContext nct = NotesContext.getCurrent();
		Session session = nct.getCurrentSession();
		Database DB = session.getDatabase(ServerName, DatabaseName);
		
		if (!(DB==null)) {
			View luView = DB.getView(ViewName); 
			
			if (!(luView == null)) {
				JsonJavaFactory factory = JsonJavaFactory.instanceEx;
				
				ViewEntryCollection vec = luView.getAllEntriesByKey(Key, true);

				ViewEntry entry = vec.getFirstEntry();
				while (entry != null) {
					
					/*View: 
					 * 1. Column: Key (sorted)
					 * 2. Column: JSON String: (z.b. {"docUNID": "FAE110220E57ECE7C12578A700375101","name": "Petar Angelchev","job": "Bauingenieur","pictureURL": "https://notes.voessing.de/Kontaktdatenbank.nsf/0/FAE110220E57ECE7C12578A700375101/$FILE/PortalPicture.jpg"})
					*/
					
					Vector<?> columnValues = entry.getColumnValues();
					String colJson = String.valueOf(columnValues.get(ColIdx));
					JsonJavaObject json = null;
					
					try {
						json = (JsonJavaObject) JsonParser.fromJson(factory, colJson);
						if (json != null) {
							JSONObjects.add(json);
						}
							
					} catch (JsonException e) {
						System.out.println("ERROR somewhere");
					}
						
				   ViewEntry tempEntry = entry;
				   entry = vec.getNextEntry();
				   tempEntry.recycle();
				} 	
				luView.recycle();
			}
			DB.recycle();
		}
		return JSONObjects;
	}
	
	public static Integer loadObjectsCounter(String ServerName, String DatabaseName, String ViewName, String Key, Integer ColIdx) throws NotesException {
		Integer Counter = 0;		
		
		NotesContext nct = NotesContext.getCurrent();
		Session session = nct.getCurrentSession();
		Database DB = session.getDatabase(ServerName, DatabaseName);
		
		if (!(DB==null)) {
			View luView = DB.getView(ViewName); 
			
			if (!(luView == null)) {
				ViewEntryCollection vec = luView.getAllEntriesByKey(Key, true);

				ViewEntry entry = vec.getFirstEntry();
				while (entry != null) {
					
					/*View: 
					 * 1. Column: Key (sorted)
					 * 2. Column: JSON String: (z.b. {"docUNID": "FAE110220E57ECE7C12578A700375101","name": "Petar Angelchev","job": "Bauingenieur","pictureURL": "https://notes.voessing.de/Kontaktdatenbank.nsf/0/FAE110220E57ECE7C12578A700375101/$FILE/PortalPicture.jpg"})
					*/
					Counter++;
										
				   ViewEntry tempEntry = entry;
				   entry = vec.getNextEntry();
				   tempEntry.recycle();
				} 	
				luView.recycle();
			}
			DB.recycle();
		}
		return Counter;
	}
	
}

