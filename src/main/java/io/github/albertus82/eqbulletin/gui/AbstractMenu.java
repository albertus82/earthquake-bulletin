package io.github.albertus82.eqbulletin.gui;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import io.github.albertus82.eqbulletin.resources.Messages;
import io.github.albertus82.jface.Multilanguage;
import io.github.albertus82.jface.i18n.LocalizedWidgets;
import io.github.albertus82.util.ISupplier;
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
	protected static final String LABEL_MENU_ITEM_FIND_EVENTS_SAME_AREA = "label.menu.item.find.events.same.area";

	protected MenuItem copyLinkMenuItem;
	protected MenuItem exportCsvMenuItem;
	protected MenuItem googleMapsBrowserMenuItem;
	protected MenuItem epicenterMapPopupMenuItem;
	protected MenuItem openBrowserMenuItem;
	protected MenuItem showMapMenuItem;
	protected MenuItem showMomentTensorMenuItem;
	protected MenuItem findEventsSameAreaMenuItem;

	@Getter(AccessLevel.NONE)
	private final LocalizedWidgets localizedWidgets = new LocalizedWidgets();

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
