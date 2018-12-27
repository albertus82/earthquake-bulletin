package it.albertus.eqbulletin.gui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.function.Supplier;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import it.albertus.eqbulletin.EarthquakeBulletin;
import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.job.BulletinExporter;
import it.albertus.eqbulletin.gui.listener.CopyLinkSelectionListener;
import it.albertus.eqbulletin.gui.listener.EpicenterMapSelectionListener;
import it.albertus.eqbulletin.gui.listener.ExportCsvSelectionListener;
import it.albertus.eqbulletin.gui.listener.GoogleMapsBrowserSelectionListener;
import it.albertus.eqbulletin.gui.listener.OpenInBrowserSelectionListener;
import it.albertus.eqbulletin.gui.listener.ResultsTableContextMenuDetectListener;
import it.albertus.eqbulletin.gui.listener.ShowMapListener;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.Status;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.SwtUtils;
import it.albertus.jface.preference.IPreferencesConfiguration;

public class ResultsTable {

	public static class Defaults {
		public static final float MAGNITUDE_BIG = 5.0f;
		public static final float MAGNITUDE_XXL = 6.0f;

		private Defaults() {
			throw new IllegalAccessError("Constants class");
		}
	}

	private static final int TOOLTIP_TIME_DISPLAYED = 5000;

	private static final String SYM_NAME_FONT_DEFAULT = ResultsTable.class.getName().toLowerCase() + ".default";

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getInstance();

	public static final ThreadLocal<DateFormat> dateFormat = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"));

	private class EarthquakeViewerComparator extends ViewerComparator {

		private static final int DESCENDING = 1;

		private int propertyIndex;
		private int direction = DESCENDING;

		public EarthquakeViewerComparator() {
			this.propertyIndex = 0;
			direction = DESCENDING;
		}

		public int getDirection() {
			return direction == 1 ? SWT.DOWN : SWT.UP;
		}

		public void setColumn(final int column) {
			if (column == this.propertyIndex) {
				direction = 1 - direction;
			}
			else {
				this.propertyIndex = column;
				direction = DESCENDING;
			}
		}

		@Override
		public int compare(final Viewer viewer, final Object e1, final Object e2) {
			final Earthquake eq1 = (Earthquake) e1;
			final Earthquake eq2 = (Earthquake) e2;
			int rc;
			switch (propertyIndex) {
			case 0:
				rc = eq1.getTime().compareTo(eq2.getTime());
				break;
			case 1:
				rc = Float.compare(eq1.getMagnitude(), eq2.getMagnitude());
				break;
			case 2:
				rc = eq1.getLatitude().compareTo(eq2.getLatitude());
				break;
			case 3:
				rc = eq1.getLongitude().compareTo(eq2.getLongitude());
				break;
			case 4:
				rc = eq1.getDepth().compareTo(eq2.getDepth());
				break;
			case 5:
				rc = eq1.getStatus().compareTo(eq2.getStatus());
				break;
			case 6:
				rc = eq1.getRegion().compareTo(eq2.getRegion());
				break;
			default:
				rc = 0;
			}
			if (direction == DESCENDING) {
				rc = -rc;
			}
			return rc;
		}
	}

	private final TableViewer tableViewer;
	private final EarthquakeViewerComparator comparator;
	private final HashMap<Integer, Supplier<String>> labelsMap = new HashMap<>();

	private final ContextMenu contextMenu;

	private boolean initialized = false;

	public ResultsTable(final Composite parent, final Object layoutData, final EarthquakeBulletinGui gui) {
		tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION) {
			// Auto resize columns on content change
			@Override
			protected void inputChanged(final Object input, final Object oldInput) {
				super.inputChanged(input, oldInput);
				if (!initialized) {
					final Table table = tableViewer.getTable();
					table.setRedraw(false);
					final TableColumn sortedColumn = table.getSortColumn();
					table.setSortColumn(null);
					packColumns(table);
					table.setSortColumn(sortedColumn);
					table.setRedraw(true);
					initialized = true;
				}
			}
		};
		final Table table = tableViewer.getTable();
		createColumns(table);
		table.setLayoutData(layoutData);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.addListener(SWT.DefaultSelection, new ShowMapListener(gui));
		tableViewer.setContentProvider(new ArrayContentProvider());
		ColumnViewerToolTipSupport.enableFor(tableViewer);
		comparator = new EarthquakeViewerComparator();
		tableViewer.setComparator(comparator);

		contextMenu = new ContextMenu(gui);
	}

	private void createColumns(final Table table) {
		int i = 0;
		for (final String suffix : new String[] { "time", "magnitude", "latitude", "longitude", "depth", "status", "region" }) {
			labelsMap.put(i++, () -> Messages.get("lbl.table." + suffix));
		}

		TableViewerColumn col = createTableViewerColumn(labelsMap.get(0).get(), 0);
		col.setLabelProvider(new EarthquakeColumnLabelProvider() {
			@Override
			protected String getText(final Earthquake element) {
				final DateFormat df = dateFormat.get();
				df.setTimeZone(TimeZone.getTimeZone(configuration.getString(Preference.TIMEZONE, EarthquakeBulletin.Defaults.TIME_ZONE_ID)));
				return df.format(element.getTime());
			}
		});

		col = createTableViewerColumn(labelsMap.get(1).get(), 1);
		col.getColumn().setAlignment(SWT.CENTER);
		col.setLabelProvider(new EarthquakeColumnLabelProvider() {
			@Override
			protected String getText(final Earthquake element) {
				return Float.toString(element.getMagnitude());
			}

			@Override
			protected Color getForeground(final Earthquake element) {
				if (element.getMagnitude() >= configuration.getFloat(Preference.MAGNITUDE_XXL, Defaults.MAGNITUDE_XXL)) {
					return table.getDisplay().getSystemColor(SWT.COLOR_RED);
				}
				else {
					return super.getForeground(element);
				}
			}

			@Override
			protected Font getFont(final Earthquake element) {
				if (element.getMagnitude() >= configuration.getFloat(Preference.MAGNITUDE_BIG, Defaults.MAGNITUDE_BIG) && getTableViewer().getTable().getItemCount() != 0) {
					final FontRegistry fontRegistry = JFaceResources.getFontRegistry();
					if (!fontRegistry.hasValueFor(SYM_NAME_FONT_DEFAULT)) {
						fontRegistry.put(SYM_NAME_FONT_DEFAULT, getTableViewer().getTable().getItem(0).getFont(0).getFontData());
					}
					return fontRegistry.getBold(SYM_NAME_FONT_DEFAULT);
				}
				return super.getFont(element);
			}
		});

		col = createTableViewerColumn(labelsMap.get(2).get(), 2);
		col.getColumn().setAlignment(SWT.RIGHT);
		col.setLabelProvider(new EarthquakeColumnLabelProvider() {
			@Override
			protected String getText(final Earthquake element) {
				return String.valueOf(element.getLatitude());
			}
		});

		col = createTableViewerColumn(labelsMap.get(3).get(), 3);
		col.getColumn().setAlignment(SWT.RIGHT);
		col.setLabelProvider(new EarthquakeColumnLabelProvider() {
			@Override
			protected String getText(final Earthquake element) {
				return String.valueOf(element.getLongitude());
			}
		});

		col = createTableViewerColumn(labelsMap.get(4).get(), 4);
		col.getColumn().setAlignment(SWT.RIGHT);
		col.setLabelProvider(new EarthquakeColumnLabelProvider() {
			@Override
			protected String getText(final Earthquake element) {
				return String.valueOf(element.getDepth());
			}
		});

		col = createTableViewerColumn(labelsMap.get(5).get(), 5);
		col.getColumn().setAlignment(SWT.CENTER);
		col.setLabelProvider(new EarthquakeColumnLabelProvider() {
			@Override
			protected String getText(final Earthquake element) {
				return String.valueOf(element.getStatus());
			}

			@Override
			protected Color getForeground(final Earthquake element) {
				return table.getDisplay().getSystemColor(Status.A.equals(element.getStatus()) ? SWT.COLOR_RED : SWT.COLOR_DARK_GREEN);
			}

			@Override
			protected String getToolTipText(final Earthquake element) {
				return element.getStatus().getDescription();
			}

			@Override
			public int getToolTipTimeDisplayed(final Object object) {
				return TOOLTIP_TIME_DISPLAYED;
			}
		});

		col = createTableViewerColumn(labelsMap.get(6).get(), 6);
		col.setLabelProvider(new EarthquakeColumnLabelProvider() {
			@Override
			protected String getText(final Earthquake element) {
				return String.valueOf(element.getRegion());
			}
		});

		table.setRedraw(false);
		packColumns(table);
		table.setRedraw(true);
	}

	private static void packColumns(final Table table) {
		for (int j = 0; j < table.getColumnCount(); j++) {
			table.getColumn(j).pack();
			if (Util.isGtk()) { // colmuns are badly resized on GTK, more space is actually needed
				GC gc = null;
				try {
					gc = new GC(table);
					table.getColumn(j).setWidth(table.getColumn(j).getWidth() + gc.stringExtent(" ").x);
				}
				finally {
					if (gc != null) {
						gc.dispose();
					}
				}
			}
		}
	}

	private TableViewerColumn createTableViewerColumn(final String title, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setResizable(true);
		column.setMoveable(true);
		column.addSelectionListener(createSelectionAdapter(column, colNumber));
		return viewerColumn;
	}

	private SelectionAdapter createSelectionAdapter(final TableColumn column, final int index) {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				comparator.setColumn(index);
				tableViewer.getTable().setSortDirection(comparator.getDirection());
				tableViewer.getTable().setSortColumn(column);
				tableViewer.refresh();
			}
		};
	}

	public void updateTexts() {
		final Table table = tableViewer.getTable();
		for (final Entry<Integer, Supplier<String>> e : labelsMap.entrySet()) {
			table.getColumn(e.getKey()).setText(e.getValue().get());
		}
		contextMenu.updateTexts();
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}

	public ContextMenu getContextMenu() {
		return contextMenu;
	}

	public void exportCsv() {
		if (tableViewer != null && tableViewer.getTable() != null && tableViewer.getTable().getItemCount() > 0) {
			BulletinExporter.export(tableViewer.getTable());
		}
	}

	public class ContextMenu extends AbstractMenu {

		private final Menu menu;

		public ContextMenu(final EarthquakeBulletinGui gui) {
			final Table table = ResultsTable.this.getTableViewer().getTable();
			menu = new Menu(table);

			// Show map...
			showMapMenuItem = new MenuItem(menu, SWT.PUSH);
			showMapMenuItem.setText(Messages.get(LBL_MENU_ITEM_SHOW_MAP));
			showMapMenuItem.addListener(SWT.Selection, new ShowMapListener(gui));
			menu.setDefaultItem(showMapMenuItem);

			new MenuItem(menu, SWT.SEPARATOR);

			// Open in browser...
			openBrowserMenuItem = new MenuItem(menu, SWT.PUSH);
			openBrowserMenuItem.setText(Messages.get(LBL_MENU_ITEM_OPEN_BROWSER));
			openBrowserMenuItem.addSelectionListener(new OpenInBrowserSelectionListener(gui));

			// Copy link...
			copyLinkMenuItem = new MenuItem(menu, SWT.PUSH);
			copyLinkMenuItem.setText(Messages.get(LBL_MENU_ITEM_COPY_LINK));
			copyLinkMenuItem.addSelectionListener(new CopyLinkSelectionListener(gui));

			new MenuItem(menu, SWT.SEPARATOR);

			// Epicenter map popup...
			epicenterMapPopupMenuItem = new MenuItem(menu, SWT.PUSH);
			epicenterMapPopupMenuItem.setText(Messages.get(LBL_MENU_ITEM_EPICENTER_MAP_POPUP));
			epicenterMapPopupMenuItem.addSelectionListener(new EpicenterMapSelectionListener(gui));

			// Google Maps in browser...
			googleMapsBrowserMenuItem = new MenuItem(menu, SWT.PUSH);
			googleMapsBrowserMenuItem.setText(Messages.get(LBL_MENU_ITEM_GOOGLE_MAPS_BROWSER));
			googleMapsBrowserMenuItem.addSelectionListener(new GoogleMapsBrowserSelectionListener(gui));

			new MenuItem(menu, SWT.SEPARATOR);

			exportCsvMenuItem = new MenuItem(menu, SWT.PUSH);
			exportCsvMenuItem.setText(Messages.get(LBL_MENU_ITEM_EXPORT_CSV) + SwtUtils.getMod1ShortcutLabel(SwtUtils.KEY_SAVE));
			exportCsvMenuItem.addSelectionListener(new ExportCsvSelectionListener(gui));
			exportCsvMenuItem.setAccelerator(SWT.MOD1 | SwtUtils.KEY_SAVE);

			table.addMenuDetectListener(new ResultsTableContextMenuDetectListener(ResultsTable.this));
		}

		public Menu getMenu() {
			return menu;
		}
	}

}
