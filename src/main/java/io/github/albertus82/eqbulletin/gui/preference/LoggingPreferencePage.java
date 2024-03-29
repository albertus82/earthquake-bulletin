package io.github.albertus82.eqbulletin.gui.preference;

import org.eclipse.swt.widgets.Control;

import io.github.albertus82.eqbulletin.resources.Messages;
import io.github.albertus82.jface.preference.page.BasePreferencePage;
import io.github.albertus82.util.logging.LoggingSupport;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoggingPreferencePage extends BasePreferencePage {

	private String overriddenMessage = Messages.get("label.preferences.logging.overridden");

	@Override
	protected Control createHeader() {
		if (LoggingSupport.getInitialConfigurationProperty() != null) {
			return createInfoComposite(getFieldEditorParent(), overriddenMessage);
		}
		else {
			return null;
		}
	}

}
