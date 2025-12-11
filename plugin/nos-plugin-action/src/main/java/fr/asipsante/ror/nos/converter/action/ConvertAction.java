/*
 * (c) Copyright 1998-2017, ASIP. All rights reserved.
 */
package fr.asipsante.ror.nos.converter.action;

import com.eviware.soapui.impl.WorkspaceImpl;
import com.eviware.soapui.plugins.ActionConfiguration;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.action.support.AbstractSoapUIAction;
import fr.asipsante.ror.nos.converter.ihm.DialogConverter;


@ActionConfiguration(actionGroup = ActionGroups.WORKSPACE_ACTIONS)
public class ConvertAction extends AbstractSoapUIAction<WorkspaceImpl> {

    public ConvertAction() {
        super("Importer Nomenclatures", "Plugin permettant de faire la convertion des"
                + " fichiers de nomenclatures au format .tabs en .xml");
    }

    @Override
    public void perform(WorkspaceImpl t, Object o) {
        UISupport.showDialog(new DialogConverter());
    }
}
