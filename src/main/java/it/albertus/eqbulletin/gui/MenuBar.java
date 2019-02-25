package it.albertus.eqbulletin.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.util.Util;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import it.albertus.eqbulletin.gui.listener.AboutListener;
import it.albertus.eqbulletin.gui.listener.ArmMenuListener;
import it.albertus.eqbulletin.gui.listener.CloseListener;
import it.albertus.eqbulletin.gui.listener.CopyLinkSelectionListener;
import it.albertus.eqbulletin.gui.listener.EpicenterMapSelectionListener;
import it.albertus.eqbulletin.gui.listener.EventMenuListener;
import it.albertus.eqbulletin.gui.listener.ExportCsvSelectionListener;
import it.albertus.eqbulletin.gui.listener.FileMenuListener;
import it.albertus.eqbulletin.gui.listener.GoogleMapsBrowserSelectionListener;
import it.albertus.eqbulletin.gui.listener.OpenInBrowserSelectionListener;
import it.albertus.eqbulletin.gui.listener.PreferencesListener;
import it.albertus.eqbulletin.gui.listener.ShowMapListener;
import it.albertus.eqbulletin.gui.listener.ShowMomentTensorListener;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.SwtUtils;
import it.albertus.jface.cocoa.CocoaEnhancerException;
import it.albertus.jface.cocoa.CocoaUIEnhancer;
import it.albertus.jface.sysinfo.SystemInformationDialog;
import it.albertus.util.logging.LoggerFactory;

/**
 * Solo i <tt>MenuItem</tt> che fanno parte di una barra dei men&ugrave; con
 * stile <tt>SWT.BAR</tt> hanno gli acceleratori funzionanti; negli altri casi
 * (ad es. <tt>SWT.POP_UP</tt>), bench&eacute; vengano visualizzate le
 * combinazioni di tasti, gli acceleratori non funzioneranno e le relative
 * combinazioni di tasti saranno ignorate.
 */
public class MenuBar extends AbstractMenu implements IShellProvider {

	private static final String LBL_MENU_HEADER_FILE = "lbl.menu.header.file";
	private static final String LBL_MENU_ITEM_EXIT = "lbl.menu.item.exit";
	private static final String LBL_MENU_HEADER_EVENT = "lbl.menu.header.event";
	private static final String LBL_MENU_HEADER_TOOLS = "lbl.menu.header.tools";
	private static final String LBL_MENU_ITEM_FEREGION = "lbl.menu.item.feregion";
	private static final String LBL_MENU_ITEM_PREFERENCES = "lbl.menu.item.preferences";
	private static final String LBL_MENU_HEADER_HELP = "lbl.menu.header.help";
	private static final String LBL_MENU_HEADER_HELP_WINDOWS = "lbl.menu.header.help.windows";
	private static final String LBL_MENU_ITEM_SYSTEM_INFO = "lbl.menu.item.system.info";
	private static final String LBL_MENU_ITEM_ABOUT = "lbl.menu.item.about";

	private static final Logger logger = LoggerFactory.getLogger(MenuBar.class);

	private final Shell shell;

	private final MenuItem fileMenuHeader;
	private MenuItem fileExitItem;

	private final MenuItem eventMenuHeader;

	private final MenuItem toolsMenuHeader;
	private final MenuItem toolsFERegionMenuItem;
	private MenuItem toolsPreferencesMenuItem;

	private final MenuItem helpMenuHeader;
	private final MenuItem helpSystemInfoItem;
	private MenuItem helpAboutItem;

	MenuBar(final EarthquakeBulletinGui gui) {
		this.shell = gui.getShell();

		final CloseListener closeListener = new CloseListener(gui);
		final AboutListener aboutListener = new AboutListener(gui);
		final PreferencesListener preferencesListener = new PreferencesListener(gui);

		boolean cocoaMenuCreated = false;

		if (Util.isCocoa()) {
			try {
				new CocoaUIEnhancer(shell.getDisplay()).hookApplicationMenu(closeListener, aboutListener, preferencesListener);
				cocoaMenuCreated = true;
			}
			catch (final CocoaEnhancerException cee) {
				logger.log(Level.WARNING, Messages.get("err.cocoa.enhancer"), cee);
			}
		}

		final Menu bar = new Menu(shell, SWT.BAR); // Bar

		// File
		final Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
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

		final FileMenuListener fileMenuListener = new FileMenuListener(gui);
		fileMenu.addMenuListener(fileMenuListener);
		fileMenuHeader.addArmListener(fileMenuListener);

		// Event
		final Menu eventMenu = new Menu(shell, SWT.DROP_DOWN);
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

		// Show moment tensor...
		showMomentTensorMenuItem = new MenuItem(eventMenu, SWT.PUSH);
		showMomentTensorMenuItem.setText(Messages.get(LBL_MENU_ITEM_SHOW_MOMENT_TENSOR));
		showMomentTensorMenuItem.addListener(SWT.Selection, new ShowMomentTensorListener(gui));

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

		// Epicenter map popup...
		epicenterMapPopupMenuItem = new MenuItem(eventMenu, SWT.PUSH);
		epicenterMapPopupMenuItem.setText(Messages.get(LBL_MENU_ITEM_EPICENTER_MAP_POPUP));
		epicenterMapPopupMenuItem.addSelectionListener(new EpicenterMapSelectionListener(gui));

		// Google Maps in browser...
		googleMapsBrowserMenuItem = new MenuItem(eventMenu, SWT.PUSH);
		googleMapsBrowserMenuItem.setText(Messages.get(LBL_MENU_ITEM_GOOGLE_MAPS_BROWSER));
		googleMapsBrowserMenuItem.addSelectionListener(new GoogleMapsBrowserSelectionListener(gui));

		// Tools
		final Menu toolsMenu = new Menu(shell, SWT.DROP_DOWN);
		toolsMenuHeader = new MenuItem(bar, SWT.CASCADE);
		toolsMenuHeader.setText(Messages.get(LBL_MENU_HEADER_TOOLS));
		toolsMenuHeader.setMenu(toolsMenu);

		toolsFERegionMenuItem = new MenuItem(toolsMenu, SWT.PUSH);
		toolsFERegionMenuItem.setText(Messages.get(LBL_MENU_ITEM_FEREGION));
		toolsFERegionMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				new FERegionDialog(shell).open();
			}
		});

		if (!cocoaMenuCreated) {
			new MenuItem(toolsMenu, SWT.SEPARATOR);

			toolsPreferencesMenuItem = new MenuItem(toolsMenu, SWT.PUSH);
			toolsPreferencesMenuItem.setText(Messages.get(LBL_MENU_ITEM_PREFERENCES));
			toolsPreferencesMenuItem.addSelectionListener(new PreferencesListener(gui));
		}

		// Help
		final Menu helpMenu = new Menu(shell, SWT.DROP_DOWN);
		helpMenuHeader = new MenuItem(bar, SWT.CASCADE);
		helpMenuHeader.setText(Messages.get(Util.isWindows() ? LBL_MENU_HEADER_HELP_WINDOWS : LBL_MENU_HEADER_HELP));
		helpMenuHeader.setMenu(helpMenu);

		helpSystemInfoItem = new MenuItem(helpMenu, SWT.PUSH);
		helpSystemInfoItem.setText(Messages.get(LBL_MENU_ITEM_SYSTEM_INFO));
		helpSystemInfoItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				SystemInformationDialog.open(shell);
			}
		});

		if (!cocoaMenuCreated) {
			new MenuItem(helpMenu, SWT.SEPARATOR);

			helpAboutItem = new MenuItem(helpMenu, SWT.PUSH);
			helpAboutItem.setText(Messages.get(LBL_MENU_ITEM_ABOUT));
			helpAboutItem.addSelectionListener(new AboutListener(gui));
		}

		final ArmMenuListener helpMenuListener = e -> helpSystemInfoItem.setEnabled(SystemInformationDialog.isAvailable());
		helpMenu.addMenuListener(helpMenuListener);
		helpMenuHeader.addArmListener(helpMenuListener);

		shell.setMenuBar(bar);
	}

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		fileMenuHeader.setText(Messages.get(LBL_MENU_HEADER_FILE));
		if (fileExitItem != null && !fileExitItem.isDisposed()) {
			fileExitItem.setText(Messages.get(LBL_MENU_ITEM_EXIT));
		}
		eventMenuHeader.setText(Messages.get(LBL_MENU_HEADER_EVENT));
		if (toolsMenuHeader != null && !toolsMenuHeader.isDisposed()) {
			toolsMenuHeader.setText(Messages.get(LBL_MENU_HEADER_TOOLS));
		}
		if (toolsFERegionMenuItem != null && !toolsFERegionMenuItem.isDisposed()) {
			toolsFERegionMenuItem.setText(Messages.get(LBL_MENU_ITEM_FEREGION));
		}
		if (toolsPreferencesMenuItem != null && !toolsPreferencesMenuItem.isDisposed()) {
			toolsPreferencesMenuItem.setText(Messages.get(LBL_MENU_ITEM_PREFERENCES));
		}
		helpMenuHeader.setText(Messages.get(Util.isWindows() ? LBL_MENU_HEADER_HELP_WINDOWS : LBL_MENU_HEADER_HELP));
		helpSystemInfoItem.setText(Messages.get(LBL_MENU_ITEM_SYSTEM_INFO));
		if (helpAboutItem != null && !helpAboutItem.isDisposed()) {
			helpAboutItem.setText(Messages.get(LBL_MENU_ITEM_ABOUT));
		}
	}

	@Override
	public Shell getShell() {
		return shell;
	}

}
