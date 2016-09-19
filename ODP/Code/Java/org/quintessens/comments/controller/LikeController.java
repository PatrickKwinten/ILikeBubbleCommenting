package org.quintessens.comments.controller;

import java.util.ArrayList;
import java.util.Vector;

import org.quintessens.comments.utils.JSFUtil;

import com.ibm.commons.util.io.json.JsonJavaObject;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;

public class LikeController {
	
	
	public Integer getCount(String DatabaseName, String DocumentId, String ViewName) throws NotesException{	
		Integer count = 0;
		Session session = JSFUtil.getSession();
		Database db = session.getDatabase("", DatabaseName);
		try{
			View vw = db.getView(ViewName);	
			if(null != vw){
				Document doc = vw.getDocumentByKey(DocumentId);
				if (null != doc){
					Vector likers =	doc.getItemValue("Likers");
					count = likers.size();					
				}			
			}	
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;		
	}
	
	public Vector getLikers(String DatabaseName, String DocumentId) throws NotesException{	
		Vector likers = null;
		Session session = JSFUtil.getSession();
		Database db = session.getDatabase("", DatabaseName);
		try{
			View vw = db.getView("likes");	
			if(null != vw){
				Document doc = vw.getDocumentByKey(DocumentId);
				if (null != doc){
					likers = doc.getItemValue("Likers");
								
				}			
			}	
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return likers;		
	}
	
	public String getLinkLabel(String DatabaseName, String DocumentId) throws NotesException{
		Session session = JSFUtil.getSession();
		Database db = session.getDatabase("", DatabaseName);
		String str = null;
		try{
			View vw = db.getView("likes");	
			if(null != vw){
				Document doc = vw.getDocumentByKey(DocumentId);
				if (null != doc){
					Vector likers =	doc.getItemValue("Likers");
					if (likers.contains(session.getEffectiveUserName())){
						str = "unlike";
					}
					else{
						str = "like";
					}
					
				}
				else{
					str = "like"; 
				}
			}	
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
	
	public void likeorunlike(String DatabaseName, String DocumentId) throws NotesException{
		Session session = JSFUtil.getSession();
		Database db = session.getDatabase("",DatabaseName);
		try{
			View vw = db.getView("likes");	
			if(null != vw){
				Document doc = vw.getDocumentByKey(DocumentId);
				if (null != doc){
					Vector likers =	doc.getItemValue("Likers");
					if (likers.contains(session.getEffectiveUserName())){
						likers.remove(session.getEffectiveUserName());
						doc.replaceItemValue("Likers", likers);			
					}
					else{
						likers.add(session.getEffectiveUserName());
						doc.replaceItemValue("Likers", likers);				
					}					
				}
				else{
					doc  = db.createDocument();
					doc.appendItemValue("Form","like");
					doc.appendItemValue("ObjectIdParent",DocumentId);
					doc.appendItemValue("Authors","[liker]");
					doc.appendItemValue("Likers",session.getEffectiveUserName());					
				}
				doc.computeWithForm(true, false);
				doc.save();
			}	
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
