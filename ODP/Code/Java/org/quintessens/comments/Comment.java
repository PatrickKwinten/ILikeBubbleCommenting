package org.quintessens.comments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;

import org.openntf.domino.email.DominoEmail;
import org.openntf.domino.xsp.XspOpenLogUtil;
import org.quintessens.comments.utils.JSFUtil;

public class Comment implements Serializable{
	private static final long serialVersionUID = 1L;
	
	// Some properties
	private String parentId;
	private String replicaId;
	private String objectId;
	
	private Date created;	
	private String from;
	private String post;
	private boolean newNote;
	private boolean readOnly;
	
	public Comment(){
		// Constructor...
		XspOpenLogUtil.logEvent(null, this.getClass().getSimpleName().toString() + ". Constructor started. ", Level.INFO, null);	
	}
	
	public void clear() {
		parentId = "";
		replicaId = "";
		post = "";
		created = null;
	}
	
	public void create() throws NotesException{	
		clear();
		setNewNote(true);
		setReadOnly(false);
		JSFUtil jsfUtil = new JSFUtil();
		Session session = jsfUtil.getSession();
		from = jsfUtil.getCurrentUser().getCanonical();
	}
	
	public void save(String dbName) throws NotesException {
		try {
			JSFUtil jsfUtil = new JSFUtil();
			Session session = jsfUtil.getSession();
			Database db = session.getDatabase("", dbName);
			Document doc = null;		
			if (newNote) {
				// True means never been saved				
				doc = db.createDocument();
				doc.replaceItemValue("form", "comment");
			} else {	
				doc = db.getDocumentByUNID(objectId);
			}		

			// Common elements to save
			doc.replaceItemValue("ObjectIdParent", parentId);
			doc.replaceItemValue("ReplicaIdParent", replicaId);
			doc.replaceItemValue("From", from);
			doc.replaceItemValue("OriginalPost", post);
			doc.computeWithForm(true, true);
			doc.save();
			
			//I am not really good at regular expressions...
			// http://stackoverflow.com/questions/21180921/get-an-array-of-strings-matching-a-pattern-from-a-string
			Pattern tagMatcher = Pattern.compile("[@]+[A-Za-z0-9-_/]+\\b");
			List<String> persons = getArray(tagMatcher, post);
			
			for (String person : persons) {
				DominoEmail mail = new DominoEmail();
				String url = "https://www.google.se/search?q=";
				mail.addToAddress(person);
				mail.setSubject("You have been mentioned");
				mail.addText("You have been invited to participate in a discussion. ");
				mail.addText("Please visit this link to read the thread:");
				mail.addHTML("<br/>");
				mail.addHTML("<a href='" + url + parentId + "'>SelfService</a>");
				mail.setSenderName("SelfService");
				mail.send();
			}		
			doc.recycle();
			db.recycle();
			readOnly = true;
		}catch (NotesException e) {
		// ??
		}	
	}
	
	public void loadValues(Document doc) throws NotesException {
		try{
			parentId = doc.getItemValueString("ObjectIdParent");
			replicaId = doc.getItemValueString("ReplicaIdParent");
			from = doc.getItemValueString("From");
			created = doc.getCreated().toJavaDate();
			newNote = false;
			objectId = doc.getUniversalID();
		}catch (NotesException e) {
			// ??
		}
	}
	
	
	private static List<String> getArray(Pattern tagMatcher, String str) {
	    Matcher m = tagMatcher.matcher(str);
	    List<String> l = new ArrayList<String>();
	    while(m.find()) {
	        String s = m.group(); //will give you "@Alice_Springs"
	        System.out.println(s);
	        s = s.substring(1); // will give you just "Alice_Springs"
	        String s1 = s.replaceAll("_", " ");
	        l.add(s1);
	    }
	    return l;
	}
	
	
	/**
	 * 
	 * @return GETTERS AND SETTERS
	 * 
	 */

	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		System.out.println("parentId:" + parentId);
		this.parentId = parentId;
	}

	public String getReplicaId() {
		return replicaId;
	}
	public void setReplicaId(String replicaId) {
		System.out.println("replicaId:" + replicaId);
		this.replicaId = replicaId;
	}

	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}

	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}

	public String getPost() {
		return post;
	}
	public void setPost(String post) {
		this.post = post;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
	
	
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	
	public Boolean getNewNote() {
		return newNote;
	}
	public void setNewNote(boolean newNote) {
		this.newNote = newNote;
	}
	
}


