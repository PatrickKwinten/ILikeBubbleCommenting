package org.quintessens.comments.controller;

import java.util.ArrayList;
import java.util.logging.Level;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;
import org.openntf.domino.xsp.XspOpenLogUtil;
import org.quintessens.comments.utils.JSFUtil;
import com.ibm.commons.util.io.json.JsonJavaObject;

public class CommentController extends PageController{

	public ArrayList<JsonJavaObject> loadComments(String parentID) throws NotesException {
		
		JSFUtil jsfUtil = new JSFUtil();
		Session session = jsfUtil.getSession();
		
		ArrayList<JsonJavaObject> comments = new ArrayList<JsonJavaObject>();
		
		Database db = session.getCurrentDatabase();
		String ViewName = "commentsbyparentIDJSON";	
		
		if (!(db==null)) {
			if ((db.isOpen())) {
				//GOOD
			}
			else{
				//BAD
				XspOpenLogUtil.logError(null, null, this.getClass().getSimpleName().toString() + ". loadPictures(). DB not found.", Level.SEVERE, null);		
			}
		}				
		try {
			comments = PageController.loadJSONObjects(db.getServer(), db.getFilePath(), ViewName, parentID, 2);
		} catch (NotesException e) {
			XspOpenLogUtil.logError(null, e, this.getClass().getSimpleName().toString() + ". loadPictures().", Level.SEVERE, null);
		}
		db.recycle();
		
		return comments;
	}
	
	public Integer loadObjectsCounter(String parentID) throws NotesException {
		
		JSFUtil jsfUtil = new JSFUtil();
		Session session = jsfUtil.getSession();
		
		Integer counter = null;
		
		Database db = session.getCurrentDatabase();
		String ViewName = "commentsbyparentIDJSON";	
		
		if (!(db==null)) {
			if ((db.isOpen())) {
				//GOOD
			}
			else{
				//BAD
				XspOpenLogUtil.logError(null, null, this.getClass().getSimpleName().toString() + ". loadPictures(). DB not found.", Level.SEVERE, null);		
			}
		}				
		try {
			counter = PageController.loadObjectsCounter(db.getServer(), db.getFilePath(), ViewName, parentID, 2);
		} catch (NotesException e) {
			XspOpenLogUtil.logError(null, e, this.getClass().getSimpleName().toString() + ". loadPictures().", Level.SEVERE, null);
		}
		db.recycle();
		
		return counter;
	}
	
	
}
