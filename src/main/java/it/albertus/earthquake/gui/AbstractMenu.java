package it.albertus.earthquake.gui;

import org.eclipse.swt.widgets.MenuItem;

import it.albertus.earthquake.resources.Messages;

public abstract class AbstractMenu {

	protected static final String LBL_MENU_ITEM_COPY_LINK = "lbl.menu.item.copy.link";
	protected static final String LBL_MENU_ITEM_EXPORT_CSV = "lbl.menu.item.export.csv";
	protected static final String LBL_MENU_ITEM_GOOGLE_MAPS_BROWSER = "lbl.menu.item.google.maps.browser";
	protected static final String LBL_MENU_ITEM_GOOGLE_MAPS_POPUP = "lbl.menu.item.google.maps.popup";
	protected static final String LBL_MENU_ITEM_OPEN_BROWSER = "lbl.menu.item.open.browser";
	protected static final String LBL_MENU_ITEM_SHOW_MAP = "lbl.menu.item.show.map";

	protected MenuItem copyLinkMenuItem;
	protected MenuItem exportCsvMenuItem;
	protected MenuItem googleMapsBrowserMenuItem;
	protected MenuItem googleMapsPopupMenuItem;
	protected MenuItem openBrowserMenuItem;
	protected MenuItem showMapMenuItem;

	public void updateTexts() {
		getCopyLinkMenuItem().setText(Messages.get(LBL_MENU_ITEM_COPY_LINK));
		getExportCsvMenuItem().setText(Messages.get(LBL_MENU_ITEM_EXPORT_CSV));
		getGoogleMapsBrowserMenuItem().setText(Messages.get(LBL_MENU_ITEM_GOOGLE_MAPS_BROWSER));
		getGoogleMapsPopupMenuItem().setText(Messages.get(LBL_MENU_ITEM_GOOGLE_MAPS_POPUP));
		getOpenBrowserMenuItem().setText(Messages.get(LBL_MENU_ITEM_OPEN_BROWSER));
		getShowMapMenuItem().setText(Messages.get(LBL_MENU_ITEM_SHOW_MAP));
	}

	public MenuItem getCopyLinkMenuItem() {
		return copyLinkMenuItem;
	}

	public MenuItem getExportCsvMenuItem() {
		return exportCsvMenuItem;
	}

	public MenuItem getGoogleMapsBrowserMenuItem() {
		return googleMapsBrowserMenuItem;
	}

	public MenuItem getGoogleMapsPopupMenuItem() {
		return googleMapsPopupMenuItem;
	}

	public MenuItem getOpenBrowserMenuItem() {
		return openBrowserMenuItem;
	}

	public MenuItem getShowMapMenuItem() {
		return showMapMenuItem;
	}

}
