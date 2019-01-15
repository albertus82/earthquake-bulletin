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
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import it.albertus.eqbulletin.EarthquakeBulletin;
import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.async.BulletinExporter;
import it.albertus.eqbulletin.gui.async.MomentTensorAsyncOperation;
import it.albertus.eqbulletin.gui.listener.CopyLinkSelectionListener;
import it.albertus.eqbulletin.gui.listener.EpicenterMapSelectionListener;
import it.albertus.eqbulletin.gui.listener.ExportCsvSelectionListener;
import it.albertus.eqbulletin.gui.listener.GoogleMapsBrowserSelectionListener;
import it.albertus.eqbulletin.gui.listener.OpenInBrowserSelectionListener;
import it.albertus.eqbulletin.gui.listener.ResultsTableContextMenuDetectListener;
import it.albertus.eqbulletin.gui.listener.ShowMapListener;
import it.albertus.eqbulletin.gui.listener.ShowMomentTensorListener;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.Status;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.SwtUtils;
import it.albertus.jface.preference.IPreferencesConfiguration;

public class ResultsTable implements IShellProvider {

	public static class Defaults {
		public static final float MAGNITUDE_BIG = 5.0f;
		public static final float MAGNITUDE_XXL = 6.0f;

		private Defaults() {
			throw new IllegalAccessError("Constants class");
		}
	}

	private static final int TOOLTIP_TIME_DISPLAYED = 5000;

	private static final String MT = "MT";

	private static final int COL_IDX_TIME = 0;
	private static final int COL_IDX_MAGNITUDE = 1;
	private static final int COL_IDX_LATITUDE = 2;
	private static final int COL_IDX_LONGITUDE = 3;
	private static final int COL_IDX_DEPTH = 4;
	private static final int COL_IDX_STATUS = 5;
	private static final int COL_IDX_MT = 6;
	private static final int COL_IDX_REGION = 7;

	private static final String SYM_NAME_FONT_DEFAULT = ResultsTable.class.getName().toLowerCase() + ".default";

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getInstance();

	public static final ThreadLocal<DateFormat> dateFormats = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"));

	private class EarthquakeViewerComparator extends ViewerComparator {

		@SuppressWarnings("unused")
		private static final byte ASCENDING = 0; // NOSONAR
		private static final byte DESCENDING = 1;

		private int propertyIndex = 0;
		private int direction = DESCENDING;

		private int getDirection() {
			return direction == DESCENDING ? SWT.DOWN : SWT.UP;
		}

		private void setColumn(final int columnIndex) {
			if (columnIndex == propertyIndex) {
				direction = 1 - direction;
			}
			else {
				propertyIndex = columnIndex;
				direction = DESCENDING;
			}
		}

		@Override
		public int compare(final Viewer viewer, final Object e1, final Object e2) {
			final Earthquake eq1 = (Earthquake) e1;
			final Earthquake eq2 = (Earthquake) e2;
			int rc;
			switch (propertyIndex) {
			case COL_IDX_TIME:
				rc = eq1.getTime().compareTo(eq2.getTime());
				break;
			case COL_IDX_MAGNITUDE:
				rc = Float.compare(eq1.getMagnitude(), eq2.getMagnitude());
				break;
			case COL_IDX_LATITUDE:
				rc = eq1.getLatitude().compareTo(eq2.getLatitude());
				break;
			case COL_IDX_LONGITUDE:
				rc = eq1.getLongitude().compareTo(eq2.getLongitude());
				break;
			case COL_IDX_DEPTH:
				rc = eq1.getDepth().compareTo(eq2.getDepth());
				break;
			case COL_IDX_STATUS:
				rc = eq1.getStatus().compareTo(eq2.getStatus());
				break;
			case COL_IDX_MT:
				if (eq1.getMomentTensorUrl() == null && eq2.getMomentTensorUrl() == null || eq1.getMomentTensorUrl() != null && eq2.getMomentTensorUrl() != null) {
					rc = 0;
				}
				else {
					rc = eq1.getMomentTensorUrl() != null ? -1 : 1;
				}
				break;
			case COL_IDX_REGION:
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

	private final Shell shell;

	private final TableViewer tableViewer;
	private final EarthquakeViewerComparator comparator;
	private final HashMap<Integer, Supplier<String>> labelsMap = new HashMap<>();

	private final ContextMenu contextMenu;

	private boolean initialized = false;

	public ResultsTable(final Composite parent, final Object layoutData, final EarthquakeBulletinGui gui) {
		shell = parent.getShell();
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

	@Override
	public Shell getShell() {
		return shell;
	}

	private void createColumns(final Table table) {
		int i = 0;
		for (final String suffix : new String[] { "time", "magnitude", "latitude", "longitude", "depth", "status", "mt", "region" }) {
			labelsMap.put(i++, () -> Messages.get("lbl.table." + suffix));
		}

		createTimeColumn();
		createMagnitudeColumn();
		createLatitudeColumn();
		createLongitudeColumn();
		createDepthColumn();
		createStatusColumn();
		createMomentTensorColumn();
		createRegionColumn();

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(final MouseEvent e) {
				if (e.button == 1) {
					final ViewerCell cell = tableViewer.getCell(new Point(e.x, e.y));
					if (cell != null && cell.getColumnIndex() == COL_IDX_MT && MT.equals(cell.getText()) && cell.getElement() instanceof Earthquake) {
						final Earthquake earthquake = (Earthquake) cell.getElement();
						MomentTensorAsyncOperation.execute(earthquake, table.getShell());
					}
				}
			}
		});

		table.setRedraw(false);
		packColumns(table);
		table.setRedraw(true);
	}

	private void createTimeColumn() {
		final TableViewerColumn col = createTableViewerColumn(labelsMap.get(COL_IDX_TIME).get(), COL_IDX_TIME);
		col.setLabelProvider(new EarthquakeColumnLabelProvider() {
			@Override
			protected String getText(final Earthquake element) {
				final DateFormat df = dateFormats.get();
				df.setTimeZone(TimeZone.getTimeZone(configuration.getString(Preference.TIMEZONE, EarthquakeBulletin.Defaults.TIME_ZONE_ID)));
				return df.format(element.getTime());
			}
		});
	}

	private void createMagnitudeColumn() {
		final TableViewerColumn col = createTableViewerColumn(labelsMap.get(COL_IDX_MAGNITUDE).get(), COL_IDX_MAGNITUDE);
		col.getColumn().setAlignment(SWT.CENTER);
		col.setLabelProvider(new EarthquakeColumnLabelProvider() {
			@Override
			protected String getText(final Earthquake element) {
				return Float.toString(element.getMagnitude());
			}

			@Override
			protected Color getForeground(final Earthquake element) {
				if (element.getMagnitude() >= configuration.getFloat(Preference.MAGNITUDE_XXL, Defaults.MAGNITUDE_XXL)) {
					return col.getColumn().getDisplay().getSystemColor(SWT.COLOR_RED);
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
	}

	private void createLatitudeColumn() {
		final TableViewerColumn col = createTableViewerColumn(labelsMap.get(COL_IDX_LATITUDE).get(), COL_IDX_LATITUDE);
		col.getColumn().setAlignment(SWT.RIGHT);
		col.setLabelProvider(new EarthquakeColumnLabelProvider() {
			@Override
			protected String getText(final Earthquake element) {
				return String.valueOf(element.getLatitude());
			}
		});
	}

	private void createLongitudeColumn() {
		final TableViewerColumn col = createTableViewerColumn(labelsMap.get(COL_IDX_LONGITUDE).get(), COL_IDX_LONGITUDE);
		col.getColumn().setAlignment(SWT.RIGHT);
		col.setLabelProvider(new EarthquakeColumnLabelProvider() {
			@Override
			protected String getText(final Earthquake element) {
				return String.valueOf(element.getLongitude());
			}
		});
	}

	private void createDepthColumn() {
		final TableViewerColumn col = createTableViewerColumn(labelsMap.get(COL_IDX_DEPTH).get(), COL_IDX_DEPTH);
		col.getColumn().setAlignment(SWT.RIGHT);
		col.setLabelProvider(new EarthquakeColumnLabelProvider() {
			@Override
			protected String getText(final Earthquake element) {
				return String.valueOf(element.getDepth());
			}
		});
	}

	private void createStatusColumn() {
		final TableViewerColumn col = createTableViewerColumn(labelsMap.get(COL_IDX_STATUS).get(), COL_IDX_STATUS);
		col.getColumn().setAlignment(SWT.CENTER);
		col.setLabelProvider(new EarthquakeColumnLabelProvider() {
			@Override
			protected String getText(final Earthquake element) {
				return String.valueOf(element.getStatus());
			}

			@Override
			protected Color getForeground(final Earthquake element) {
				return col.getColumn().getDisplay().getSystemColor(Status.A.equals(element.getStatus()) ? SWT.COLOR_RED : SWT.COLOR_DARK_GREEN);
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
	}

	private void createMomentTensorColumn() {
		final TableViewerColumn col = createTableViewerColumn(labelsMap.get(COL_IDX_MT).get(), COL_IDX_MT);
		col.getColumn().setAlignment(SWT.CENTER);
		col.setLabelProvider(new EarthquakeColumnLabelProvider() {
			@Override
			protected String getText(final Earthquake element) {
				return element.getMomentTensorUrl() != null ? MT : "";
			}

			@Override
			protected Color getForeground(final Earthquake element) {
				return col.getColumn().getDisplay().getSystemColor(SWT.COLOR_LINK_FOREGROUND);
			}

			@Override
			protected String getToolTipText(final Earthquake element) {
				return element.getMomentTensorUrl() != null ? Messages.get("lbl.table.mt.tooltip") : null;
			}

			@Override
			public int getToolTipTimeDisplayed(final Object object) {
				return TOOLTIP_TIME_DISPLAYED;
			}
		});
	}

	private void createRegionColumn() {
		final TableViewerColumn col = createTableViewerColumn(labelsMap.get(COL_IDX_REGION).get(), COL_IDX_REGION);
		col.setLabelProvider(new EarthquakeColumnLabelProvider() {
			@Override
			protected String getText(final Earthquake element) {
				return String.valueOf(element.getRegion());
			}
		});
	}

	private TableViewerColumn createTableViewerColumn(final String title, final int index) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setResizable(true);
		column.setMoveable(true);
		column.addSelectionListener(createSelectionAdapter(column, index));
		return viewerColumn;
	}

	private SelectionAdapter createSelectionAdapter(final TableColumn column, final int index) {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				comparator.setColumn(index);
				final Table table = tableViewer.getTable();
				table.setSortDirection(comparator.getDirection());
				table.setSortColumn(column);
				tableViewer.refresh();
			}
		};
	}

	private static void packColumns(final Table table) {
		for (final TableColumn column : table.getColumns()) {
			column.pack();
			if (Util.isGtk()) { // colmuns are badly resized on GTK, more space is actually needed
				GC gc = null;
				try {
					gc = new GC(table);
					column.setWidth(column.getWidth() + gc.stringExtent(" ").x);
				}
				finally {
					if (gc != null) {
						gc.dispose();
					}
				}
			}
		}
	}

	public void updateTexts() {
		if (tableViewer != null) {
			final Table table = tableViewer.getTable();
			for (final Entry<Integer, Supplier<String>> e : labelsMap.entrySet()) {
				table.getColumn(e.getKey()).setText(e.getValue().get());
			}
		}
		if (contextMenu != null) {
			contextMenu.updateTexts();
		}
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

		private ContextMenu(final EarthquakeBulletinGui gui) {
			final Table table = tableViewer.getTable();
			menu = new Menu(table);

			// Show map...
			showMapMenuItem = new MenuItem(menu, SWT.PUSH);
			showMapMenuItem.setText(Messages.get(LBL_MENU_ITEM_SHOW_MAP));
			showMapMenuItem.addListener(SWT.Selection, new ShowMapListener(gui));
			menu.setDefaultItem(showMapMenuItem);

			// Show moment tensor solution...
			showMomentTensorMenuItem = new MenuItem(menu, SWT.PUSH);
			showMomentTensorMenuItem.setText(Messages.get(LBL_MENU_ITEM_SHOW_MOMENT_TENSOR));
			showMomentTensorMenuItem.addListener(SWT.Selection, new ShowMomentTensorListener(gui));

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
