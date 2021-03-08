package it.albertus.eqbulletin.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.Multilanguage;
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

	private final Collection<MenuItem> localizedMenuItems = new ArrayList<>();

	@Override
	public void updateLanguage() {
		for (final MenuItem menuItem : localizedMenuItems) {
			menuItem.setText(Messages.get(menuItem));
		}
	}

	protected MenuItem newLocalizedMenuItem(@NonNull final Menu parent, final int style, @NonNull final String messageKey) {
		final MenuItem menuItem = new MenuItem(parent, style);
		menuItem.setData(messageKey);
		menuItem.setText(Messages.get(menuItem));
		localizedMenuItems.add(menuItem);
		return menuItem;
	}

	protected MenuItem newLocalizedMenuItem(@NonNull final Menu parent, final int style, @NonNull final Supplier<String> messageSupplier) {
		final MenuItem menuItem = new MenuItem(parent, style);
		menuItem.setData(messageSupplier);
		menuItem.setText(Messages.get(menuItem));
		localizedMenuItems.add(menuItem);
		return menuItem;
	}

}
