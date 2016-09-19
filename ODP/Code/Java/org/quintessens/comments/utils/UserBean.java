package org.quintessens.comments.utils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import javax.faces.context.FacesContext;

import lotus.domino.Database;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.Session;

public class UserBean {
	
	private final List<String> aclLevelNames = new ArrayList<String>();
	public final List<String> aclPriviliges = new ArrayList<String>();
	private Session session = null;
	private int aclLevel;
	private String aclLevelName;
	private String userNameCommon;
	private String userNameAbbreviated;
	private String userNameCanonical;
	private String userRoles;
	private String emailAdress;
	private String mailFilePath;
	
	public Database db;
	
	public UserBean(Database db) {
		this.init(db);
	}
	
	@SuppressWarnings("static-access")
	public void init(Database db) {
		session = getCurrentSession();
		try {
			Database refDb = db;
			
			Name name = session.createName(session.getEffectiveUserName());
			this.userNameCommon = name.getCommon();
			this.userNameAbbreviated = name.getAbbreviated();
			this.userNameCanonical = name.getCanonical();
			this.emailAdress = (String) session.evaluate(
					"@NameLookup( [Exhaustive] ; \""
							+ session.getEffectiveUserName()
							+ "\"; \"InternetAddress\")").elementAt(0);
			this.mailFilePath = (String) session.evaluate(
					"@NameLookup( [Exhaustive] ; \""
							+ session.getEffectiveUserName()
							+ "\"; \"MailFile\")").elementAt(0);
			
			aclLevelNames.add("ACL.LEVEL_NO_ACCESS");
			aclLevelNames.add("ACL.LEVEL_DEPOSITOR");
			aclLevelNames.add("ACL.LEVEL_READER");
			aclLevelNames.add("ACL.LEVEL_AUTHOR");
			aclLevelNames.add("ACL.LEVEL_EDITOR");
			aclLevelNames.add("ACL.LEVEL_DESIGNER");
			aclLevelNames.add("ACL.LEVEL_MANAGER");
			
			this.aclLevel = refDb.queryAccess(this.userNameCanonical);
			
			//this.aclLevel = session.getCurrentDatabase().queryAccess(this.userNameCanonical);
			this.aclLevelName = aclLevelNames.get(this.aclLevel);
		
	        //int accPriv = session.getCurrentDatabase().queryAccessPrivileges(session.getEffectiveUserName());
	        int accPriv = refDb.queryAccessPrivileges(session.getEffectiveUserName());
			
			if ((accPriv & refDb.DBACL_CREATE_DOCS) > 0){
				if (!aclPriviliges.contains("DBACL_CREATE_DOCS")){
					aclPriviliges.add("DBACL_CREATE_DOCS");
				}			
			}			
			if ((accPriv & refDb.DBACL_DELETE_DOCS) > 0){
				if (!aclPriviliges.contains("DBACL_DELETE_DOCS")){
					aclPriviliges.add("DBACL_DELETE_DOCS");
				}					
			}	
			if ((accPriv & refDb.DBACL_CREATE_PRIV_AGENTS) > 0){
				if (!aclPriviliges.contains("DBACL_CREATE_PRIV_AGENTS")){
					aclPriviliges.add("DBACL_CREATE_PRIV_AGENTS");
				}					
			}
			if ((accPriv & refDb.DBACL_CREATE_PRIV_FOLDERS_VIEWS) > 0){
				if (!aclPriviliges.contains("DBACL_CREATE_PRIV_FOLDERS_VIEWS")){
					aclPriviliges.add("DBACL_CREATE_PRIV_FOLDERS_VIEWS");
				}					
			}	
			if ((accPriv & refDb.DBACL_CREATE_SHARED_FOLDERS_VIEWS) > 0){
				if (!aclPriviliges.contains("DBACL_CREATE_SHARED_FOLDERS_VIEWS")){
					aclPriviliges.add("DBACL_CREATE_SHARED_FOLDERS_VIEWS");
				}					
			}	
			if ((accPriv & refDb.DBACL_CREATE_SCRIPT_AGENTS) > 0){
				if (!aclPriviliges.contains("DBACL_CREATE_SCRIPT_AGENTS")){
					aclPriviliges.add("DBACL_CREATE_SCRIPT_AGENTS");
				}					
			}	
			if ((accPriv & refDb.DBACL_READ_PUBLIC_DOCS) > 0){
				if (!aclPriviliges.contains("DBACL_READ_PUBLIC_DOCS")){
					aclPriviliges.add("DBACL_READ_PUBLIC_DOCS");
				}					
			}	
			if ((accPriv & refDb.DBACL_WRITE_PUBLIC_DOCS) > 0){
				if (!aclPriviliges.contains("DBACL_WRITE_PUBLIC_DOCS")){
					aclPriviliges.add("DBACL_WRITE_PUBLIC_DOCS");
				}					
			}	
			if ((accPriv & refDb.DBACL_REPLICATE_COPY_DOCS) > 0){
				if (!aclPriviliges.contains("DBACL_REPLICATE_COPY_DOCS")){
					aclPriviliges.add("DBACL_REPLICATE_COPY_DOCS");
				}					
			}
			
			this.userRoles = this.implode(refDb.queryAccessRoles(this.userNameCanonical));
			
		} catch (NotesException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getUserNameAbbreviated() {
		return userNameAbbreviated;
	}
	public String getUserNameCanonical() {
		return userNameCanonical;
	}
	public String getAclLevelName() {
		return aclLevelName;
	}
	public String getEmailAdress() {
		return emailAdress;
	}
	public String getMailFilePath() {
		return mailFilePath;
	}
	public int getAclLevel() {
		return aclLevel;
	}
	public String getUserNameCommon() {
		return userNameCommon;
	}
	public String getUserRoles() {
		return userRoles;
	}
	private String implode(Vector v) {
		StringBuilder builder = new StringBuilder();
		Enumeration e = v.elements();
		while (e.hasMoreElements()) {
			builder.append(e.nextElement().toString());
		}
		return builder.toString();
	}
	public boolean hasRole(String roleName) {
		return (this.userRoles.indexOf(roleName) != -1);
	}
	public static Session getCurrentSession() {
		FacesContext context = FacesContext.getCurrentInstance();
		return (Session) context.getApplication().getVariableResolver().resolveVariable(context, "session");
	}
}