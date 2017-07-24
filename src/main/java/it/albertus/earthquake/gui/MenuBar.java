package it.albertus.earthquake.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import it.albertus.earthquake.gui.listener.AboutListener;
import it.albertus.earthquake.gui.listener.CloseListener;
import it.albertus.earthquake.gui.listener.CopyLinkSelectionListener;
import it.albertus.earthquake.gui.listener.EventMenuListener;
import it.albertus.earthquake.gui.listener.ExportCsvSelectionListener;
import it.albertus.earthquake.gui.listener.FileMenuListener;
import it.albertus.earthquake.gui.listener.GoogleMapsBrowserSelectionListener;
import it.albertus.earthquake.gui.listener.GoogleMapsPopupSelectionListener;
import it.albertus.earthquake.gui.listener.OpenInBrowserSelectionListener;
import it.albertus.earthquake.gui.listener.PreferencesListener;
import it.albertus.earthquake.gui.listener.ShowMapListener;
import it.albertus.earthquake.resources.Messages;
import it.albertus.jface.SwtUtils;
import it.albertus.jface.cocoa.CocoaEnhancerException;
import it.albertus.jface.cocoa.CocoaUIEnhancer;
import it.albertus.util.logging.LoggerFactory;

/**
 * Solo i <tt>MenuItem</tt> che fanno parte di una barra dei men&ugrave; con
 * stile <tt>SWT.BAR</tt> hanno gli acceleratori funzionanti; negli altri casi
 * (ad es. <tt>SWT.POP_UP</tt>), bench&eacute; vengano visualizzate le
 * combinazioni di tasti, gli acceleratori non funzioneranno e le relative
 * combinazioni di tasti saranno ignorate.
 */
public class MenuBar {

	private static final String LBL_MENU_HEADER_FILE = "lbl.menu.header.file";
	public static final String LBL_MENU_ITEM_EXPORT_CSV = "lbl.menu.item.export.csv";
	private static final String LBL_MENU_ITEM_EXIT = "lbl.menu.item.exit";
	private static final String LBL_MENU_HEADER_EVENT = "lbl.menu.header.event";
	public static final String LBL_MENU_ITEM_COPY_LINK = "lbl.menu.item.copy.link";
	public static final String LBL_MENU_ITEM_GOOGLE_MAPS_BROWSER = "lbl.menu.item.google.maps.browser";
	public static final String LBL_MENU_ITEM_GOOGLE_MAPS_POPUP = "lbl.menu.item.google.maps.popup";
	public static final String LBL_MENU_ITEM_OPEN_BROWSER = "lbl.menu.item.open.browser";
	public static final String LBL_MENU_ITEM_SHOW_MAP = "lbl.menu.item.show.map";
	private static final String LBL_MENU_HEADER_TOOLS = "lbl.menu.header.tools";
	private static final String LBL_MENU_ITEM_PREFERENCES = "lbl.menu.item.preferences";
	private static final String LBL_MENU_HEADER_HELP = "lbl.menu.header.help";
	private static final String LBL_MENU_ITEM_ABOUT = "lbl.menu.item.about";

	private static final Logger logger = LoggerFactory.getLogger(MenuBar.class);

	private final Menu fileMenu;
	private final MenuItem fileMenuHeader;
	private final MenuItem fileExportCsvItem;
	private MenuItem fileExitItem;

	private final Menu eventMenu;
	private final MenuItem eventMenuHeader;
	private final MenuItem showMapMenuItem;
	private final MenuItem openBrowserMenuItem;
	private final MenuItem copyLinkMenuItem;
	private final MenuItem googleMapsBrowserMenuItem;
	private final MenuItem googleMapsPopupMenuItem;

	private Menu toolsMenu;
	private MenuItem toolsMenuHeader;
	private MenuItem toolsPreferencesMenuItem;

	private Menu helpMenu;
	private MenuItem helpMenuHeader;
	private MenuItem helpAboutItem;

	public MenuBar(final EarthquakeBulletinGui gui) {
		final CloseListener closeListener = new CloseListener(gui);
		final AboutListener aboutListener = new AboutListener(gui);
		final PreferencesListener preferencesListener = new PreferencesListener(gui);

		boolean cocoaMenuCreated = false;

		if (Util.isCocoa()) {
			try {
				new CocoaUIEnhancer(gui.getShell().getDisplay()).hookApplicationMenu(closeListener, aboutListener, preferencesListener);
				cocoaMenuCreated = true;
			}
			catch (final CocoaEnhancerException cee) {
				logger.log(Level.WARNING, Messages.get("err.cocoa.enhancer"), cee);
			}
		}

		final Menu bar = new Menu(gui.getShell(), SWT.BAR); // Bar

		// File
		fileMenu = new Menu(gui.getShell(), SWT.DROP_DOWN);
		fileMenuHeader = new MenuItem(bar, SWT.CASCADE);
		fileMenuHeader.setText(Messages.get(LBL_MENU_HEADER_FILE));
		fileMenuHeader.setMenu(fileMenu);

		fileExportCsvItem = new MenuItem(fileMenu, SWT.PUSH);
		fileExportCsvItem.setText(Messages.get(LBL_MENU_ITEM_EXPORT_CSV) + SwtUtils.getMod1ShortcutLabel(SwtUtils.KEY_SAVE));
		fileExportCsvItem.setAccelerator(SWT.MOD1 | SwtUtils.KEY_SAVE);
		fileExportCsvItem.addSelectionListener(new ExportCsvSelectionListener(gui));

		if (!cocoaMenuCreated) {
			new MenuItem(fileMenu, SWT.SEPARATOR);

			fileExitItem = new MenuItem(fileMenu, SWT.PUSH);
			fileExitItem.setText(Messages.get(LBL_MENU_ITEM_EXIT));
			fileExitItem.addSelectionListener(new CloseListener(gui));
		}

		// Event
		eventMenu = new Menu(gui.getShell(), SWT.DROP_DOWN);
		eventMenuHeader = new MenuItem(bar, SWT.CASCADE);
		eventMenuHeader.setText(Messages.get(LBL_MENU_HEADER_EVENT));
		eventMenuHeader.setMenu(eventMenu);
		final EventMenuListener eventMenuListener = new EventMenuListener(gui);
		eventMenu.addMenuListener(eventMenuListener);
		eventMenuHeader.addArmListener(eventMenuListener);

		// Show map...
		showMapMenuItem = new MenuItem(eventMenu, SWT.PUSH);
		showMapMenuItem.setText(Messages.get(LBL_MENU_ITEM_SHOW_MAP));
		showMapMenuItem.addListener(SWT.Selection, new ShowMapListener(gui));

		new MenuItem(eventMenu, SWT.SEPARATOR);

		// Open in browser...
		openBrowserMenuItem = new MenuItem(eventMenu, SWT.PUSH);
		openBrowserMenuItem.setText(Messages.get(LBL_MENU_ITEM_OPEN_BROWSER));
		openBrowserMenuItem.addSelectionListener(new OpenInBrowserSelectionListener(gui));

		// Copy link...
		copyLinkMenuItem = new MenuItem(eventMenu, SWT.PUSH);
		copyLinkMenuItem.setText(Messages.get(LBL_MENU_ITEM_COPY_LINK));
		copyLinkMenuItem.addSelectionListener(new CopyLinkSelectionListener(gui));

		new MenuItem(eventMenu, SWT.SEPARATOR);

		// Google Maps Popup...
		googleMapsPopupMenuItem = new MenuItem(eventMenu, SWT.PUSH);
		googleMapsPopupMenuItem.setText(Messages.get(LBL_MENU_ITEM_GOOGLE_MAPS_POPUP));
		googleMapsPopupMenuItem.addSelectionListener(new GoogleMapsPopupSelectionListener(gui));

		// Google Maps in browser...
		googleMapsBrowserMenuItem = new MenuItem(eventMenu, SWT.PUSH);
		googleMapsBrowserMenuItem.setText(Messages.get(LBL_MENU_ITEM_GOOGLE_MAPS_BROWSER));
		googleMapsBrowserMenuItem.addSelectionListener(new GoogleMapsBrowserSelectionListener(gui));

		if (!cocoaMenuCreated) {
			// Tools
			toolsMenu = new Menu(gui.getShell(), SWT.DROP_DOWN);
			toolsMenuHeader = new MenuItem(bar, SWT.CASCADE);
			toolsMenuHeader.setText(Messages.get(LBL_MENU_HEADER_TOOLS));
			toolsMenuHeader.setMenu(toolsMenu);

			toolsPreferencesMenuItem = new MenuItem(toolsMenu, SWT.PUSH);
			toolsPreferencesMenuItem.setText(Messages.get(LBL_MENU_ITEM_PREFERENCES));
			toolsPreferencesMenuItem.addSelectionListener(new PreferencesListener(gui));

			// Help
			helpMenu = new Menu(gui.getShell(), SWT.DROP_DOWN);
			helpMenuHeader = new MenuItem(bar, SWT.CASCADE);
			helpMenuHeader.setText(Messages.get(LBL_MENU_HEADER_HELP));
			helpMenuHeader.setMenu(helpMenu);

			helpAboutItem = new MenuItem(helpMenu, SWT.PUSH);
			helpAboutItem.setText(Messages.get(LBL_MENU_ITEM_ABOUT));
			helpAboutItem.addSelectionListener(new AboutListener(gui));
		}

		final FileMenuListener fileMenuListener = new FileMenuListener(gui);
		fileMenu.addMenuListener(fileMenuListener);
		fileMenuHeader.addArmListener(fileMenuListener);

		gui.getShell().setMenuBar(bar);
	}

	public void updateTexts() {
		fileMenuHeader.setText(Messages.get(LBL_MENU_HEADER_FILE));
		fileExportCsvItem.setText(Messages.get(LBL_MENU_ITEM_EXPORT_CSV));
		if (fileExitItem != null && !fileExitItem.isDisposed()) {
			fileExitItem.setText(Messages.get(LBL_MENU_ITEM_EXIT));
		}
		eventMenuHeader.setText(Messages.get(LBL_MENU_HEADER_EVENT));
		showMapMenuItem.setText(Messages.get(MenuBar.LBL_MENU_ITEM_SHOW_MAP));
		openBrowserMenuItem.setText(Messages.get(MenuBar.LBL_MENU_ITEM_OPEN_BROWSER));
		copyLinkMenuItem.setText(Messages.get(MenuBar.LBL_MENU_ITEM_COPY_LINK));
		googleMapsPopupMenuItem.setText(Messages.get(MenuBar.LBL_MENU_ITEM_GOOGLE_MAPS_POPUP));
		googleMapsBrowserMenuItem.setText(Messages.get(MenuBar.LBL_MENU_ITEM_GOOGLE_MAPS_BROWSER));
		if (toolsMenuHeader != null && !toolsMenuHeader.isDisposed()) {
			toolsMenuHeader.setText(Messages.get(LBL_MENU_HEADER_TOOLS));
		}
		if (toolsPreferencesMenuItem != null && !toolsPreferencesMenuItem.isDisposed()) {
			toolsPreferencesMenuItem.setText(Messages.get(LBL_MENU_ITEM_PREFERENCES));
		}
		if (helpMenuHeader != null && !helpMenuHeader.isDisposed()) {
			helpMenuHeader.setText(Messages.get(LBL_MENU_HEADER_HELP));
		}
		if (helpAboutItem != null && !helpAboutItem.isDisposed()) {
			helpAboutItem.setText(Messages.get(LBL_MENU_ITEM_ABOUT));
		}
	}

	public Menu getFileMenu() {
		return fileMenu;
	}

	public MenuItem getFileMenuHeader() {
		return fileMenuHeader;
	}

	public MenuItem getFileExportCsvItem() {
		return fileExportCsvItem;
	}

	public MenuItem getFileExitItem() {
		return fileExitItem;
	}

	public MenuItem getShowMapMenuItem() {
		return showMapMenuItem;
	}

	public MenuItem getOpenBrowserMenuItem() {
		return openBrowserMenuItem;
	}

	public MenuItem getCopyLinkMenuItem() {
		return copyLinkMenuItem;
	}

	public MenuItem getGoogleMapsBrowserMenuItem() {
		return googleMapsBrowserMenuItem;
	}

	public MenuItem getGoogleMapsPopupMenuItem() {
		return googleMapsPopupMenuItem;
	}

	public Menu getToolsMenu() {
		return toolsMenu;
	}

	public MenuItem getToolsMenuHeader() {
		return toolsMenuHeader;
	}

	public MenuItem getToolsPreferencesMenuItem() {
		return toolsPreferencesMenuItem;
	}

	public Menu getHelpMenu() {
		return helpMenu;
	}

	public MenuItem getHelpMenuHeader() {
		return helpMenuHeader;
	}

	public MenuItem getHelpAboutItem() {
		return helpAboutItem;
	}

}
