package io.github.albertus82.eqbulletin.gui.preference;

import java.util.Locale;

import org.eclipse.jface.resource.ImageDescriptor;

import io.github.albertus82.eqbulletin.resources.Messages;
import it.albertus.jface.preference.page.BasePreferencePage;
import it.albertus.jface.preference.page.IPageDefinition;
import it.albertus.jface.preference.page.PageDefinitionDetails;
import it.albertus.jface.preference.page.PageDefinitionDetails.PageDefinitionDetailsBuilder;

public enum PageDefinition implements IPageDefinition {

	GENERAL,
	CONNECTION,
	CRITERIA,
	CACHE,
	LOGGING(new PageDefinitionDetailsBuilder().pageClass(LoggingPreferencePage.class).build()),
	ADVANCED;

	private static final String LABEL_KEY_PREFIX = "label.preferences.";

	private final PageDefinitionDetails pageDefinitionDetails;

	PageDefinition() {
		this(new PageDefinitionDetailsBuilder().build());
	}

	PageDefinition(final PageDefinitionDetails pageDefinitionDetails) {
		this.pageDefinitionDetails = pageDefinitionDetails;
		if (pageDefinitionDetails.getNodeId() == null) {
			pageDefinitionDetails.setNodeId(name().toLowerCase(Locale.ROOT).replace('_', '.'));
		}
		if (pageDefinitionDetails.getLabel() == null) {
			pageDefinitionDetails.setLabel(() -> Messages.get(LABEL_KEY_PREFIX + pageDefinitionDetails.getNodeId()));
		}
	}

	@Override
	public String getNodeId() {
		return pageDefinitionDetails.getNodeId();
	}

	@Override
	public String getLabel() {
		return pageDefinitionDetails.getLabel().get();
	}

	@Override
	public Class<? extends BasePreferencePage> getPageClass() {
		return pageDefinitionDetails.getPageClass();
	}

	@Override
	public IPageDefinition getParent() {
		return pageDefinitionDetails.getParent();
	}

	@Override
	public ImageDescriptor getImage() {
		return pageDefinitionDetails.getImage();
	}

}
