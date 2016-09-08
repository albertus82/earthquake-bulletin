package it.albertus.geofon.client.gui;

import it.albertus.geofon.client.gui.listener.CloseListener;
import it.albertus.geofon.client.gui.listener.PreferencesSelectionListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * Solo i <tt>MenuItem</tt> che fanno parte di una barra dei men&ugrave; con
 * stile <tt>SWT.BAR</tt> hanno gli acceleratori funzionanti; negli altri casi
 * (ad es. <tt>SWT.POP_UP</tt>), bench&eacute; vengano visualizzate le
 * combinazioni di tasti, gli acceleratori non funzioneranno e le relative
 * combinazioni di tasti saranno ignorate.
 */
public class MenuBar {

	private final Menu bar;

	private final Menu fileMenu;
	private final MenuItem fileMenuHeader;
	private final MenuItem fileExitItem;

	private final Menu toolsMenu;
	private final MenuItem toolsMenuHeader;
	private final MenuItem toolsPreferencesMenuItem;

	//	private final Menu helpMenu;
	//	private final MenuItem helpMenuHeader;
	//	private final MenuItem helpAboutItem;

	public MenuBar(final GeofonClientGui gui) {
		bar = new Menu(gui.getShell(), SWT.BAR); // Barra

		/* File */
		fileMenu = new Menu(gui.getShell(), SWT.DROP_DOWN);
		fileMenuHeader = new MenuItem(bar, SWT.CASCADE);
		fileMenuHeader.setText("&File");//Messages.get("lbl.menu.header.file"));
		fileMenuHeader.setMenu(fileMenu);

		fileExitItem = new MenuItem(fileMenu, SWT.PUSH);
		fileExitItem.setText("&Exit");//Messages.get("lbl.menu.item.exit"));
		fileExitItem.addSelectionListener(new CloseListener(gui));

		/* Tools */
		toolsMenu = new Menu(gui.getShell(), SWT.DROP_DOWN);
		toolsMenuHeader = new MenuItem(bar, SWT.CASCADE);
		toolsMenuHeader.setText("&Tools");//Messages.get("lbl.menu.header.tools"));
		toolsMenuHeader.setMenu(toolsMenu);

		toolsPreferencesMenuItem = new MenuItem(toolsMenu, SWT.PUSH);
		toolsPreferencesMenuItem.setText("&Preferences");//Messages.get("lbl.menu.item.preferences"));
		toolsPreferencesMenuItem.addSelectionListener(new PreferencesSelectionListener(gui));

		/* Help */
		//		helpMenu = new Menu(gui.getShell(), SWT.DROP_DOWN);
		//		helpMenuHeader = new MenuItem(bar, SWT.CASCADE);
		//		helpMenuHeader.setText("&Help");//Messages.get("lbl.menu.header.help"));
		//		helpMenuHeader.setMenu(helpMenu);
		//
		//		helpAboutItem = new MenuItem(helpMenu, SWT.PUSH);
		//		helpAboutItem.setText("&About");//Messages.get("lbl.menu.item.about"));
		//		helpAboutItem.addSelectionListener(new AboutSelectionListener(gui));

		gui.getShell().setMenuBar(bar);
	}

	public void updateTexts() {
		//		fileMenuHeader.setText(Messages.get("lbl.menu.header.file"));
		//		fileExitItem.setText(Messages.get("lbl.menu.item.exit"));
		//		toolsMenuHeader.setText(Messages.get("lbl.menu.header.tools"));
		//		toolsPreferencesMenuItem.setText(Messages.get("lbl.menu.item.preferences"));
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

}
