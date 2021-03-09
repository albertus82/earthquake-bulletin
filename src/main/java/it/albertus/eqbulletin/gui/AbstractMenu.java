package it.albertus.eqbulletin.gui;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.Multilanguage;
import it.albertus.jface.i18n.LocalizedWidgets;
import it.albertus.util.ISupplier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

@Getter
abstract class AbstractMenu implements Multilanguage {

	protected static final String LABEL_MENU_ITEM_COPY_LINK = "label.menu.item.copy.link";
	protected static final String LABEL_MENU_ITEM_EXPORT_CSV = "label.menu.item.export.csv";
	protected static final String LABEL_MENU_ITEM_GOOGLE_MAPS_BROWSER = "label.menu.item.google.maps.browser";
	protected static final String LABEL_MENU_ITEM_EPICENTER_MAP_POPUP = "label.menu.item.epicenter.map.popup";
	protected static final String LABEL_MENU_ITEM_OPEN_BROWSER = "label.menu.item.open.browser";
	protected static final String LABEL_MENU_ITEM_SHOW_MAP = "label.menu.item.show.map";
	protected static final String LABEL_MENU_ITEM_SHOW_MOMENT_TENSOR = "label.menu.item.show.moment.tensor";

	protected MenuItem copyLinkMenuItem;
	protected MenuItem exportCsvMenuItem;
	protected MenuItem googleMapsBrowserMenuItem;
	protected MenuItem epicenterMapPopupMenuItem;
	protected MenuItem openBrowserMenuItem;
	protected MenuItem showMapMenuItem;
	protected MenuItem showMomentTensorMenuItem;

	@Getter(AccessLevel.NONE) private final LocalizedWidgets localizedWidgets = new LocalizedWidgets();

	@Override
	public void updateLanguage() {
		localizedWidgets.resetAllTexts();
	}

	protected MenuItem newLocalizedMenuItem(@NonNull final Menu parent, final int style, @NonNull final String messageKey) {
		return newLocalizedMenuItem(parent, style, () -> Messages.get(messageKey));
	}

	protected MenuItem newLocalizedMenuItem(@NonNull final Menu parent, final int style, @NonNull final ISupplier<String> textSupplier) {
		return localizedWidgets.putAndReturn(new MenuItem(parent, style), textSupplier).getKey();
	}

}
