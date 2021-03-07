package it.albertus.eqbulletin.gui;

import org.eclipse.swt.widgets.MenuItem;

import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.Multilanguage;
import lombok.Getter;

@Getter
abstract class AbstractMenu implements Multilanguage {

	protected static final String LBL_MENU_ITEM_COPY_LINK = "lbl.menu.item.copy.link";
	protected static final String LBL_MENU_ITEM_EXPORT_CSV = "lbl.menu.item.export.csv";
	protected static final String LBL_MENU_ITEM_GOOGLE_MAPS_BROWSER = "lbl.menu.item.google.maps.browser";
	protected static final String LBL_MENU_ITEM_EPICENTER_MAP_POPUP = "lbl.menu.item.epicenter.map.popup";
	protected static final String LBL_MENU_ITEM_OPEN_BROWSER = "lbl.menu.item.open.browser";
	protected static final String LBL_MENU_ITEM_SHOW_MAP = "lbl.menu.item.show.map";
	protected static final String LBL_MENU_ITEM_SHOW_MOMENT_TENSOR = "lbl.menu.item.show.moment.tensor";

	protected MenuItem copyLinkMenuItem;
	protected MenuItem exportCsvMenuItem;
	protected MenuItem googleMapsBrowserMenuItem;
	protected MenuItem epicenterMapPopupMenuItem;
	protected MenuItem openBrowserMenuItem;
	protected MenuItem showMapMenuItem;
	protected MenuItem showMomentTensorMenuItem;

	@Override
	public void updateLanguage() {
		copyLinkMenuItem.setText(Messages.get(LBL_MENU_ITEM_COPY_LINK));
		exportCsvMenuItem.setText(Messages.get(LBL_MENU_ITEM_EXPORT_CSV));
		googleMapsBrowserMenuItem.setText(Messages.get(LBL_MENU_ITEM_GOOGLE_MAPS_BROWSER));
		epicenterMapPopupMenuItem.setText(Messages.get(LBL_MENU_ITEM_EPICENTER_MAP_POPUP));
		openBrowserMenuItem.setText(Messages.get(LBL_MENU_ITEM_OPEN_BROWSER));
		showMapMenuItem.setText(Messages.get(LBL_MENU_ITEM_SHOW_MAP));
		showMomentTensorMenuItem.setText(Messages.get(LBL_MENU_ITEM_SHOW_MOMENT_TENSOR));
	}

}
