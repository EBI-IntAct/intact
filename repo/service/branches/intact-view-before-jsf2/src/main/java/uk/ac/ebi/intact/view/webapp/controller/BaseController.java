package uk.ac.ebi.intact.view.webapp.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.conversation.ConversationBindingEvent;
import org.apache.myfaces.orchestra.conversation.ConversationBindingListener;
import org.apache.myfaces.trinidad.component.UIXCollection;
import org.apache.myfaces.trinidad.component.UIXTable;
import org.apache.myfaces.trinidad.component.UIXTree;
import org.apache.myfaces.trinidad.context.RequestContext;
import org.apache.myfaces.trinidad.model.RowKeySet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * IntAct JSF Base Controller.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public abstract class BaseController implements Serializable, ConversationBindingListener {

    private static final Log log = LogFactory.getLog( BaseController.class );

    @Autowired
    private ApplicationContext applicationContext;

    protected void addMessage(String message, String detail) {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage facesMessage = new FacesMessage(message, detail);
        context.addMessage(null, facesMessage);
    }

    protected void addInfoMessage(String message, String detail) {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, message, detail);
        context.addMessage(null, facesMessage);
    }

    protected void addWarningMessage(String message, String detail) {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_WARN, message, detail);
        context.addMessage(null, facesMessage);
    }

    protected void addErrorMessage(String message, String detail) {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, detail);
        context.addMessage(null, facesMessage);
    }

    protected List getSelected(String tableOrTreeId) {
        UIXCollection uixCollection = (UIXCollection) FacesContext.getCurrentInstance().getViewRoot().findComponent(tableOrTreeId);

        final RowKeySet state;
        if (uixCollection instanceof UIXTable) {
            state = ((UIXTable) uixCollection).getSelectedRowKeys();
        } else {
            state = ((UIXTree) uixCollection).getSelectedRowKeys();
        }

        Iterator<Object> selection = state.iterator();
        Object oldKey = uixCollection.getRowKey();

        List selectedEntries = new ArrayList();
        while (selection.hasNext()) {
            uixCollection.setRowKey(selection.next());
            selectedEntries.add(uixCollection.getRowData());
        }
        uixCollection.setRowKey(oldKey);
        return selectedEntries;
    }

    protected void resetSelection(String tableOrTreeId) {
        UIXCollection uixCollection = (UIXCollection) FacesContext.getCurrentInstance().getViewRoot().findComponent(tableOrTreeId);

        if (uixCollection instanceof UIXTable) {
            ((UIXTable) uixCollection).setSelectedRowKeys(null);
        } else {
            ((UIXTree) uixCollection).setSelectedRowKeys(null);
        }
    }

    protected void refreshTable(String tableId, Object value) {
    	UIXTable table = (UIXTable) FacesContext.getCurrentInstance().getViewRoot().findComponent(tableId);
        table.setValue(value);
        RequestContext afContext = RequestContext.getCurrentInstance();
        afContext.addPartialTarget(table);
    }

    protected UIComponent getComponentFromView(String componentId) {
        return FacesContext.getCurrentInstance().getViewRoot().findComponent(componentId);
    }

    protected void refreshComponent(String componentId) {
        UIComponent comp = getComponentFromView(componentId);

        if (comp != null) {
            RequestContext.getCurrentInstance().addPartialTarget(comp);
        }
    }

    /**
     * Use this method to get a value using a list of parameter names. The names are iterated in order
     * and if a value is found, that value is return. This method is useful to create synonym parameters.
     * @param paramNames The parameter names, which are synonyms
     * @return The value
     */
    protected String getParameterValue(String ... paramNames) {
        for (String paramName : paramNames) {
            String value = FacesContext.getCurrentInstance()
                    .getExternalContext().getRequestParameterMap().get(paramName);

            if (value != null) {
                return value;
            }
        }

        return null;
    }

    protected Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public void valueBound(ConversationBindingEvent event) {

        if (log.isDebugEnabled()) {
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            log.debug("Conversation event (value bound): conversation="+event.getConversation().getName()+
                ", name="+event.getName()+", session: "+request.getSession().getId());
        }
    }

    public void valueUnbound(ConversationBindingEvent event) {
        if (log.isDebugEnabled()) {
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            log.debug("Conversation event (value unbound): conversation="+event.getConversation().getName()+
                    ", name="+event.getName()+", session: "+request.getSession().getId());
        }
    }
}

