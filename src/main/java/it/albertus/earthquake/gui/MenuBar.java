package it.albertus.earthquake.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import it.albertus.earthquake.gui.listener.AboutListener;
import it.albertus.earthquake.gui.listener.CloseListener;
import it.albertus.earthquake.gui.listener.ExportCsvListener;
import it.albertus.earthquake.gui.listener.FileMenuArmListener;
import it.albertus.earthquake.gui.listener.PreferencesListener;
import it.albertus.earthquake.resources.Messages;
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

	private static final Logger logger = LoggerFactory.getLogger(MenuBar.class);

	private final Menu fileMenu;
	private final MenuItem fileMenuHeader;
	private final MenuItem fileExportCsvItem;
	private MenuItem fileExitItem;

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
		fileMenuHeader.setText(Messages.get("lbl.menu.header.file"));
		fileMenuHeader.setMenu(fileMenu);

		fileExportCsvItem = new MenuItem(fileMenu, SWT.PUSH);
		fileExportCsvItem.setText(Messages.get("lbl.menu.item.export.csv"));
		fileExportCsvItem.addSelectionListener(new ExportCsvListener(gui));

		if (!cocoaMenuCreated) {
			new MenuItem(fileMenu, SWT.SEPARATOR);

			fileExitItem = new MenuItem(fileMenu, SWT.PUSH);
			fileExitItem.setText(Messages.get("lbl.menu.item.exit"));
			fileExitItem.addSelectionListener(new CloseListener(gui));

			// Tools
			toolsMenu = new Menu(gui.getShell(), SWT.DROP_DOWN);
			toolsMenuHeader = new MenuItem(bar, SWT.CASCADE);
			toolsMenuHeader.setText(Messages.get("lbl.menu.header.tools"));
			toolsMenuHeader.setMenu(toolsMenu);

			toolsPreferencesMenuItem = new MenuItem(toolsMenu, SWT.PUSH);
			toolsPreferencesMenuItem.setText(Messages.get("lbl.menu.item.preferences"));
			toolsPreferencesMenuItem.addSelectionListener(new PreferencesListener(gui));

			// Help
			helpMenu = new Menu(gui.getShell(), SWT.DROP_DOWN);
			helpMenuHeader = new MenuItem(bar, SWT.CASCADE);
			helpMenuHeader.setText(Messages.get("lbl.menu.header.help"));
			helpMenuHeader.setMenu(helpMenu);

			helpAboutItem = new MenuItem(helpMenu, SWT.PUSH);
			helpAboutItem.setText(Messages.get("lbl.menu.item.about"));
			helpAboutItem.addSelectionListener(new AboutListener(gui));
		}

		fileMenuHeader.addArmListener(new FileMenuArmListener(gui));

		gui.getShell().setMenuBar(bar);
	}

	public void updateTexts() {
		if (fileMenuHeader != null && !fileMenuHeader.isDisposed()) {
			fileMenuHeader.setText(Messages.get("lbl.menu.header.file"));
		}
		if (fileExportCsvItem != null && !fileExportCsvItem.isDisposed()) {
			fileExportCsvItem.setText(Messages.get("lbl.menu.item.export.csv"));
		}
		if (fileExitItem != null && !fileExitItem.isDisposed()) {
			fileExitItem.setText(Messages.get("lbl.menu.item.exit"));
		}
		if (toolsMenuHeader != null && !toolsMenuHeader.isDisposed()) {
			toolsMenuHeader.setText(Messages.get("lbl.menu.header.tools"));
		}
		if (toolsPreferencesMenuItem != null && !toolsPreferencesMenuItem.isDisposed()) {
			toolsPreferencesMenuItem.setText(Messages.get("lbl.menu.item.preferences"));
		}
		if (helpMenuHeader != null && !helpMenuHeader.isDisposed()) {
			helpMenuHeader.setText(Messages.get("lbl.menu.header.help"));
		}
		if (helpAboutItem != null && !helpAboutItem.isDisposed()) {
			helpAboutItem.setText(Messages.get("lbl.menu.item.about"));
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
