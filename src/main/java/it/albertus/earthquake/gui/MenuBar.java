package it.albertus.earthquake.gui;

import org.eclipse.jface.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import it.albertus.earthquake.gui.listener.AboutSelectionListener;
import it.albertus.earthquake.gui.listener.CloseListener;
import it.albertus.earthquake.gui.listener.PreferencesSelectionListener;
import it.albertus.earthquake.resources.Messages;
import it.albertus.jface.cocoa.CocoaUIEnhancer;

/**
 * Solo i <tt>MenuItem</tt> che fanno parte di una barra dei men&ugrave; con
 * stile <tt>SWT.BAR</tt> hanno gli acceleratori funzionanti; negli altri casi
 * (ad es. <tt>SWT.POP_UP</tt>), bench&eacute; vengano visualizzate le
 * combinazioni di tasti, gli acceleratori non funzioneranno e le relative
 * combinazioni di tasti saranno ignorate.
 */
public class MenuBar {

	private Menu bar;

	private Menu fileMenu;
	private MenuItem fileMenuHeader;
	private MenuItem fileExitItem;

	private Menu toolsMenu;
	private MenuItem toolsMenuHeader;
	private MenuItem toolsPreferencesMenuItem;

	private Menu helpMenu;
	private MenuItem helpMenuHeader;
	private MenuItem helpAboutItem;

	public MenuBar(final EarthquakeBulletinGui gui) {
		if (Util.isCocoa()) {
			createCocoaMenu(gui);
		}
		else {
			createStandardMenu(gui);
		}
	}

	private void createCocoaMenu(final EarthquakeBulletinGui gui) {
		try {
			new CocoaUIEnhancer(gui.getShell().getDisplay()).hookApplicationMenu(new CloseListener(gui), new AboutSelectionListener(gui), new PreferencesSelectionListener(gui));
		}
		catch (final Throwable t) {
			t.printStackTrace();
			createStandardMenu(gui); // fail-safe
		}
	}

	private void createStandardMenu(final EarthquakeBulletinGui gui) {
		bar = new Menu(gui.getShell(), SWT.BAR); // Bar

		// File
		fileMenu = new Menu(gui.getShell(), SWT.DROP_DOWN);
		fileMenuHeader = new MenuItem(bar, SWT.CASCADE);
		fileMenuHeader.setText(Messages.get("lbl.menu.header.file"));
		fileMenuHeader.setMenu(fileMenu);

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
		toolsPreferencesMenuItem.addSelectionListener(new PreferencesSelectionListener(gui));

		// Help
		helpMenu = new Menu(gui.getShell(), SWT.DROP_DOWN);
		helpMenuHeader = new MenuItem(bar, SWT.CASCADE);
		helpMenuHeader.setText(Messages.get("lbl.menu.header.help"));
		helpMenuHeader.setMenu(helpMenu);

		helpAboutItem = new MenuItem(helpMenu, SWT.PUSH);
		helpAboutItem.setText(Messages.get("lbl.menu.item.about"));
		helpAboutItem.addSelectionListener(new AboutSelectionListener(gui));

		gui.getShell().setMenuBar(bar);
	}

	public void updateTexts() {
		if (fileMenuHeader != null && !fileMenuHeader.isDisposed()) {
			fileMenuHeader.setText(Messages.get("lbl.menu.header.file"));
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

	public Menu getBar() {
		return bar;
	}

	public Menu getFileMenu() {
		return fileMenu;
	}

	public MenuItem getFileMenuHeader() {
		return fileMenuHeader;
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
