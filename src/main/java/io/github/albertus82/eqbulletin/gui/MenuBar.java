package io.github.albertus82.eqbulletin.gui;

import org.eclipse.jface.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import io.github.albertus82.eqbulletin.gui.listener.AboutListener;
import io.github.albertus82.eqbulletin.gui.listener.ArmMenuListener;
import io.github.albertus82.eqbulletin.gui.listener.CopyLinkSelectionListener;
import io.github.albertus82.eqbulletin.gui.listener.EpicenterMapSelectionListener;
import io.github.albertus82.eqbulletin.gui.listener.EventMenuListener;
import io.github.albertus82.eqbulletin.gui.listener.ExitListener;
import io.github.albertus82.eqbulletin.gui.listener.ExportCsvSelectionListener;
import io.github.albertus82.eqbulletin.gui.listener.FileMenuListener;
import io.github.albertus82.eqbulletin.gui.listener.FindSameAreaEventsSelectionListener;
import io.github.albertus82.eqbulletin.gui.listener.GoogleMapsBrowserSelectionListener;
import io.github.albertus82.eqbulletin.gui.listener.OpenInBrowserSelectionListener;
import io.github.albertus82.eqbulletin.gui.listener.PreferencesListener;
import io.github.albertus82.eqbulletin.gui.listener.ShowMapListener;
import io.github.albertus82.eqbulletin.gui.listener.ShowMomentTensorListener;
import io.github.albertus82.eqbulletin.resources.Messages;
import io.github.albertus82.jface.SwtUtils;
import io.github.albertus82.jface.cocoa.CocoaEnhancerException;
import io.github.albertus82.jface.cocoa.CocoaUIEnhancer;
import io.github.albertus82.jface.sysinfo.SystemInformationDialog;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Solo i <tt>MenuItem</tt> che fanno parte di una barra dei men&ugrave; con
 * stile <tt>SWT.BAR</tt> hanno gli acceleratori funzionanti; negli altri casi
 * (ad es. <tt>SWT.POP_UP</tt>), bench&eacute; vengano visualizzate le
 * combinazioni di tasti, gli acceleratori non funzioneranno e le relative
 * combinazioni di tasti saranno ignorate.
 */
@Slf4j
public class MenuBar extends AbstractMenu {

	MenuBar(@NonNull final EarthquakeBulletinGui gui) {
		final Shell shell = gui.getShell();

		final ExitListener exitListener = new ExitListener(gui);
		final AboutListener aboutListener = new AboutListener(gui);
		final PreferencesListener preferencesListener = new PreferencesListener(gui);

		boolean cocoaMenuCreated = false;

		if (Util.isCocoa()) {
			try {
				new CocoaUIEnhancer(shell.getDisplay()).hookApplicationMenu(exitListener, aboutListener, preferencesListener);
				cocoaMenuCreated = true;
			}
			catch (final CocoaEnhancerException e) {
				log.warn("Unable to enhance Cocoa UI:", e);
			}
		}

		final Menu bar = new Menu(shell, SWT.BAR); // Bar

		// File
		final Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
		final MenuItem fileMenuHeader = newLocalizedMenuItem(bar, SWT.CASCADE, "label.menu.header.file");
		fileMenuHeader.setMenu(fileMenu);

		exportCsvMenuItem = newLocalizedMenuItem(fileMenu, SWT.PUSH, () -> Messages.get(LABEL_MENU_ITEM_EXPORT_CSV) + SwtUtils.getMod1ShortcutLabel(SwtUtils.KEY_SAVE));
		exportCsvMenuItem.setAccelerator(SWT.MOD1 | SwtUtils.KEY_SAVE);
		exportCsvMenuItem.addSelectionListener(new ExportCsvSelectionListener(gui::getResultsTable));

		if (!cocoaMenuCreated) {
			new MenuItem(fileMenu, SWT.SEPARATOR);

			final MenuItem fileExitItem = newLocalizedMenuItem(fileMenu, SWT.PUSH, "label.menu.item.exit");
			fileExitItem.addSelectionListener(new ExitListener(gui));
		}

		final FileMenuListener fileMenuListener = new FileMenuListener(gui);
		fileMenu.addMenuListener(fileMenuListener);
		fileMenuHeader.addArmListener(fileMenuListener);

		// Event
		final Menu eventMenu = new Menu(shell, SWT.DROP_DOWN);
		final MenuItem eventMenuHeader = newLocalizedMenuItem(bar, SWT.CASCADE, "label.menu.header.event");
		eventMenuHeader.setMenu(eventMenu);
		final EventMenuListener eventMenuListener = new EventMenuListener(gui);
		eventMenu.addMenuListener(eventMenuListener);
		eventMenuHeader.addArmListener(eventMenuListener);

		// Show map...
		showMapMenuItem = newLocalizedMenuItem(eventMenu, SWT.PUSH, LABEL_MENU_ITEM_SHOW_MAP);
		showMapMenuItem.addListener(SWT.Selection, new ShowMapListener(gui::getResultsTable));

		// Show moment tensor...
		showMomentTensorMenuItem = newLocalizedMenuItem(eventMenu, SWT.PUSH, LABEL_MENU_ITEM_SHOW_MOMENT_TENSOR);
		showMomentTensorMenuItem.addListener(SWT.Selection, new ShowMomentTensorListener(gui::getResultsTable));

		new MenuItem(eventMenu, SWT.SEPARATOR);

		// Open in browser...
		openBrowserMenuItem = newLocalizedMenuItem(eventMenu, SWT.PUSH, LABEL_MENU_ITEM_OPEN_BROWSER);
		openBrowserMenuItem.addSelectionListener(new OpenInBrowserSelectionListener(gui::getResultsTable));

		// Copy link...
		copyLinkMenuItem = newLocalizedMenuItem(eventMenu, SWT.PUSH, LABEL_MENU_ITEM_COPY_LINK);
		copyLinkMenuItem.addSelectionListener(new CopyLinkSelectionListener(gui::getResultsTable));

		new MenuItem(eventMenu, SWT.SEPARATOR);

		// Epicenter map popup...
		epicenterMapPopupMenuItem = newLocalizedMenuItem(eventMenu, SWT.PUSH, LABEL_MENU_ITEM_EPICENTER_MAP_POPUP);
		epicenterMapPopupMenuItem.addSelectionListener(new EpicenterMapSelectionListener(gui::getResultsTable));

		// Google Maps in browser...
		googleMapsBrowserMenuItem = newLocalizedMenuItem(eventMenu, SWT.PUSH, LABEL_MENU_ITEM_GOOGLE_MAPS_BROWSER);
		googleMapsBrowserMenuItem.addSelectionListener(new GoogleMapsBrowserSelectionListener(gui::getResultsTable));

		new MenuItem(eventMenu, SWT.SEPARATOR);

		// Find events in the same area
		findSameAreaEventsMenuItem = newLocalizedMenuItem(eventMenu, SWT.PUSH, LABEL_MENU_ITEM_FIND_EVENTS_SAME_AREA);
		findSameAreaEventsMenuItem.addSelectionListener(new FindSameAreaEventsSelectionListener(gui::getResultsTable, gui::getSearchForm));

		// Tools
		final Menu toolsMenu = new Menu(shell, SWT.DROP_DOWN);
		final MenuItem toolsMenuHeader = newLocalizedMenuItem(bar, SWT.CASCADE, "label.menu.header.tools");
		toolsMenuHeader.setMenu(toolsMenu);

		final MenuItem toolsFERegionMenuItem = newLocalizedMenuItem(toolsMenu, SWT.PUSH, "label.menu.item.feregion");
		toolsFERegionMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				new FERegionDialog(shell).open();
			}
		});

		if (!cocoaMenuCreated) {
			new MenuItem(toolsMenu, SWT.SEPARATOR);

			final MenuItem toolsPreferencesMenuItem = newLocalizedMenuItem(toolsMenu, SWT.PUSH, "label.menu.item.preferences");
			toolsPreferencesMenuItem.addSelectionListener(new PreferencesListener(gui));
		}

		// Help
		final Menu helpMenu = new Menu(shell, SWT.DROP_DOWN);
		final MenuItem helpMenuHeader = newLocalizedMenuItem(bar, SWT.CASCADE, Util.isWindows() ? "label.menu.header.help.windows" : "label.menu.header.help");
		helpMenuHeader.setMenu(helpMenu);

		final MenuItem helpSystemInfoItem = newLocalizedMenuItem(helpMenu, SWT.PUSH, "label.menu.item.system.info");
		helpSystemInfoItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				SystemInformationDialog.open(shell);
			}
		});

		if (!cocoaMenuCreated) {
			new MenuItem(helpMenu, SWT.SEPARATOR);

			final MenuItem helpAboutItem = newLocalizedMenuItem(helpMenu, SWT.PUSH, "label.menu.item.about");
			helpAboutItem.addSelectionListener(new AboutListener(gui));
		}

		final ArmMenuListener helpMenuListener = e -> helpSystemInfoItem.setEnabled(SystemInformationDialog.isAvailable());
		helpMenu.addMenuListener(helpMenuListener);
		helpMenuHeader.addArmListener(helpMenuListener);

		shell.setMenuBar(bar);
	}

}
