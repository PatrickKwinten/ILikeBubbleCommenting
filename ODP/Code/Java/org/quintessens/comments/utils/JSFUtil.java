package org.quintessens.comments.utils;

import java.util.Map;
import java.util.List;
import java.util.logging.Level;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;

import lotus.domino.Database;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.Session;
import com.ibm.xsp.binding.PropertyMap;

import org.openntf.domino.xsp.XspOpenLogUtil;

public class JSFUtil {
	
	private String userRoles;
	
	public static String debugMode;
	
	public JSFUtil() throws NotesException{
		XspOpenLogUtil.logEvent(null, this.getClass().getSimpleName().toString() + ". Constructor started.", Level.INFO, null);
	}

	public static Map getApplicationScope() {
		return (Map) resolveVariable("applicationScope");
	}

	public Database getCurrentDatabase() {
		return (Database) resolveVariable("database");
	}

	public static Map getRequestScope() {
		return (Map) resolveVariable("requestScope");
	}

	public static Session getSession() {
		return (Session) resolveVariable("session");
	}

	public Session getSessionAsSigner() {
		return (Session) resolveVariable("sessionAsSigner");
	}

	public static Map getSessionScope() {
		return (Map) resolveVariable("sessionScope");
	}

	public static PropertyMap getViewScope() {
		return (PropertyMap) resolveVariable("viewScope");
	}

	public static Object resolveVariable(String variable) {
		return FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), variable);
	}

    public static UIComponent findComponent(UIComponent topComponent, String compId) {
        if (compId==null){
        	throw new NullPointerException("Component identifier cannot be null");
        }
        if (compId.equals(topComponent.getId())){
        	return topComponent;
        }
        if (topComponent.getChildCount()>0) {
        	List<UIComponent> childComponents=topComponent.getChildren();
        	for (UIComponent currChildComponent : childComponents) {
        		UIComponent foundComponent=findComponent(currChildComponent, compId);
        		if (foundComponent!=null){
        			return foundComponent;
        		}
        	}
        }
        return null;
    } 	

    public static Name getCurrentUser() {
		Session session = getSession();
		try {
			return session.createName(session.getEffectiveUserName());
		} catch (NotesException e) {
			e.printStackTrace();
			return null;
		}			
	}
    
	public static Session getCurrentSession() {
		FacesContext context = FacesContext.getCurrentInstance();
		return (Session) context.getApplication().getVariableResolver().resolveVariable(context, "session");
	}
	
	public String getUserRoles() {
		return userRoles;
	}
	
	public boolean hasRole(String roleName) {
		return (this.userRoles.indexOf(roleName) != -1);
	}	
	

	@SuppressWarnings("unchecked")
	public static String getParameter( String name ) {
		Map<String, String> parameters = (Map<String, String>) resolveVariable( "param" );
		return parameters.get( name );
	}
	

}
