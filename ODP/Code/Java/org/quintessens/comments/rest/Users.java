package org.quintessens.comments.rest;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;

import javax.servlet.http.HttpServletResponse;

import lotus.domino.Database;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

import org.openntf.domino.xsp.XspOpenLogUtil;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonParser;

import com.ibm.domino.services.rest.RestServiceEngine;
import com.ibm.xsp.extlib.component.rest.CustomService;
import com.ibm.xsp.extlib.component.rest.CustomServiceBean;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class Users  extends CustomServiceBean{
	
	@Override
	public void renderService(CustomService service, RestServiceEngine engine)  {
		HttpServletResponse response = engine.getHttpResponse();
		response.setHeader("Content-Type", "application/json; charset=UTF-8");
		try {
			
			PrintWriter writer = response.getWriter();
			JsonJavaArray data = new JsonJavaArray();			
			ArrayList<JsonJavaObject> JSONObjects = new ArrayList<JsonJavaObject>();
			
			//goto a view where 2nd column displays a json object e.g. 
			//{name: "Adam DeVille", username: "ADeVille", image: "http://dev1/fakenames40k.nsf/avatars/avatar1.png",…}
			Session session = ExtLibUtil.getCurrentSession();
			Database db = session.getDatabase("", "fakenames40k.nsf");	
			View vw = db.getView("peoplemention");
			
			if (!(vw == null)) {
				JsonJavaFactory factory = JsonJavaFactory.instanceEx;	
				ViewEntryCollection vec = null;
				vec = vw.getAllEntries();		
				ViewEntry entry = vec.getFirstEntry();
			
				while (entry != null) {
					Vector<?> columnValues = entry.getColumnValues();
					String colJson = String.valueOf(columnValues.get(1));
					JsonJavaObject json = null;
					try {
						json = (JsonJavaObject) JsonParser.fromJson(factory, colJson);											
						if (json != null) {
							JSONObjects.add(json);
						}
					} catch (JsonException e) {
						XspOpenLogUtil.logError(null, e, "JSFUtil loadJSONObjects()", Level.SEVERE, null);					
					}					
				   ViewEntry tempEntry = entry;
				   entry = vec.getNextEntry();
				   tempEntry.recycle();
				} 	
				vw.recycle();
			}
			
			JsonJavaObject json = new JsonJavaObject();
			json.put("data", JSONObjects);
			writer.write(json.toString());
			writer.close();	
			
			db.recycle();
			
		} catch (Throwable t) {
			XspOpenLogUtil.logError(t);
		}
	}
	

}
