package it.albertus.eqbulletin.gui.preference;

import org.eclipse.swt.widgets.Control;

import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.preference.page.BasePreferencePage;
import it.albertus.util.logging.LoggingSupport;

public class LoggingPreferencePage extends BasePreferencePage {

	private String overriddenMessage = Messages.get("lbl.preferences.logging.overridden");

	@Override
	protected Control createHeader() {
		if (LoggingSupport.getInitialConfigurationProperty() != null) {
			return createInfoComposite(getFieldEditorParent(), overriddenMessage);
		}
		else {
			return null;
		}
	}

	public final String getOverriddenMessage() {
		return overriddenMessage;
	}

	public final void setOverriddenMessage(final String overriddenMessage) {
		this.overriddenMessage = overriddenMessage;
	}

}
