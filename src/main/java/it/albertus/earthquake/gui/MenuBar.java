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
public class MenuBar extends AbstractMenu {

	private static final String LBL_MENU_HEADER_FILE = "lbl.menu.header.file";
	private static final String LBL_MENU_ITEM_EXIT = "lbl.menu.item.exit";
	private static final String LBL_MENU_HEADER_EVENT = "lbl.menu.header.event";
	private static final String LBL_MENU_HEADER_TOOLS = "lbl.menu.header.tools";
	private static final String LBL_MENU_ITEM_PREFERENCES = "lbl.menu.item.preferences";
	private static final String LBL_MENU_HEADER_HELP = "lbl.menu.header.help";
	private static final String LBL_MENU_ITEM_ABOUT = "lbl.menu.item.about";

	private static final Logger logger = LoggerFactory.getLogger(MenuBar.class);

	private final Menu fileMenu;
	private final MenuItem fileMenuHeader;
	private MenuItem fileExitItem;

	private final Menu eventMenu;
	private final MenuItem eventMenuHeader;

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

		exportCsvMenuItem = new MenuItem(fileMenu, SWT.PUSH);
		exportCsvMenuItem.setText(Messages.get(LBL_MENU_ITEM_EXPORT_CSV) + SwtUtils.getMod1ShortcutLabel(SwtUtils.KEY_SAVE));
		exportCsvMenuItem.setAccelerator(SWT.MOD1 | SwtUtils.KEY_SAVE);
		exportCsvMenuItem.addSelectionListener(new ExportCsvSelectionListener(gui));

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

	@Override
	public void updateTexts() {
		super.updateTexts();
		fileMenuHeader.setText(Messages.get(LBL_MENU_HEADER_FILE));
		if (fileExitItem != null && !fileExitItem.isDisposed()) {
			fileExitItem.setText(Messages.get(LBL_MENU_ITEM_EXIT));
		}
		eventMenuHeader.setText(Messages.get(LBL_MENU_HEADER_EVENT));
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

}
