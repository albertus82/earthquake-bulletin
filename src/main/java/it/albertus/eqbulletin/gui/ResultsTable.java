package it.albertus.eqbulletin.gui;

import static javax.swing.SortOrder.ASCENDING;
import static javax.swing.SortOrder.DESCENDING;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Supplier;

import javax.swing.SortOrder;

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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import it.albertus.eqbulletin.cache.BeachBallCache;
import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.config.TimeZoneConfig;
import it.albertus.eqbulletin.gui.async.BeachBallAsyncOperation;
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
import it.albertus.eqbulletin.model.BeachBall;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.Status;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.Multilanguage;
import it.albertus.jface.SwtUtils;
import it.albertus.jface.closeable.CloseableResource;
import it.albertus.jface.maps.CoordinateUtils;
import it.albertus.jface.preference.IPreferencesConfiguration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

public class ResultsTable implements Multilanguage {

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Defaults {
		public static final float MAGNITUDE_BIG = 5.0f;
		public static final float MAGNITUDE_XXL = 6.0f;
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

	private static final String SYM_NAME_FONT_DEFAULT = ResultsTable.class.getName() + ".default";

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getPreferencesConfiguration();

	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

	private static class EarthquakeViewerComparator extends ViewerComparator {

		private int propertyIndex = 0;
		private SortOrder direction = DESCENDING;

		private int getDirection() {
			return DESCENDING.equals(direction) ? SWT.DOWN : SWT.UP;
		}

		private void setColumn(final int columnIndex) {
			if (columnIndex == propertyIndex) {
				direction = ASCENDING.equals(direction) ? DESCENDING : ASCENDING;
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
				final Optional<Status> status1 = eq1.getStatus();
				final Optional<Status> status2 = eq2.getStatus();
				if (status1.isPresent() && status2.isPresent()) {
					rc = status1.get().compareTo(status2.get());
				}
				else if (status1.isPresent()) {
					rc = 1;
				}
				else if (status2.isPresent()) {
					rc = -1;
				}
				else {
					rc = 0;
				}
				break;
			case COL_IDX_MT:
				if (!eq1.getMomentTensorUri().isPresent() && !eq2.getMomentTensorUri().isPresent() || eq1.getMomentTensorUri().isPresent() && eq2.getMomentTensorUri().isPresent()) {
					rc = 0;
				}
				else {
					rc = eq1.getMomentTensorUri().isPresent() ? -1 : 1;
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

	@Getter private final TableViewer tableViewer;
	private final EarthquakeViewerComparator comparator;
	private final HashMap<Integer, Supplier<String>> labelsMap = new HashMap<>();

	@Getter private final ContextMenu contextMenu;

	private boolean initialized = false;

	ResultsTable(@NonNull final Composite parent, final Object layoutData) {
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
				autoSizeStatusColumn(tableViewer.getTable(), input); // Manage missing Status column values when data source is HTML
			}
		};
		final Table table = tableViewer.getTable();
		createColumns(table);
		if (layoutData != null) {
			table.setLayoutData(layoutData);
		}
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.addListener(SWT.DefaultSelection, new ShowMapListener(() -> this));
		tableViewer.setContentProvider(new ArrayContentProvider());
		ColumnViewerToolTipSupport.enableFor(tableViewer);
		comparator = new EarthquakeViewerComparator();
		tableViewer.setComparator(comparator);

		contextMenu = new ContextMenu(this);
	}

	private void createColumns(final Table table) {
		int i = 0;
		for (final String suffix : new String[] { "time", "magnitude", "latitude", "longitude", "depth", "status", "mt", "region" }) {
			labelsMap.put(i++, () -> Messages.get("label.table." + suffix));
		}

		createTimeColumn();
		createMagnitudeColumn();
		createLatitudeColumn();
		createLongitudeColumn();
		createDepthColumn();
		createStatusColumn();
		createMomentTensorColumn();
		createRegionColumn();

		table.addMouseListener(new TableMouseListener());
		table.addMouseMoveListener(new TableMouseMoveListener());

		table.setRedraw(false);
		packColumns(table);
		table.setRedraw(true);
	}

	private void createTimeColumn() {
		final TableViewerColumn col = createTableViewerColumn(labelsMap.get(COL_IDX_TIME).get(), COL_IDX_TIME);
		col.setLabelProvider(new EarthquakeColumnLabelProvider() {
			@Override
			protected String getText(final Earthquake element) {
				return dateTimeFormatter.withZone(TimeZoneConfig.getZoneId()).format(element.getTime());
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
			private final NumberFormat numberFormat = CoordinateUtils.newFormatter();

			@Override
			protected String getText(final Earthquake element) {
				return element.getLatitude().toString(numberFormat);
			}
		});
	}

	private void createLongitudeColumn() {
		final TableViewerColumn col = createTableViewerColumn(labelsMap.get(COL_IDX_LONGITUDE).get(), COL_IDX_LONGITUDE);
		col.getColumn().setAlignment(SWT.RIGHT);
		col.setLabelProvider(new EarthquakeColumnLabelProvider() {
			private final NumberFormat numberFormat = CoordinateUtils.newFormatter();

			@Override
			protected String getText(final Earthquake element) {
				return element.getLongitude().toString(numberFormat);
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
				final Optional<Status> status = element.getStatus();
				return status.isPresent() ? status.get().toString() : null;
			}

			@Override
			protected Color getForeground(final Earthquake element) {
				final Optional<Status> status = element.getStatus();
				if (status.isPresent()) {
					return col.getColumn().getDisplay().getSystemColor(Status.A.equals(status.get()) ? SWT.COLOR_RED : SWT.COLOR_DARK_GREEN);
				}
				else {
					return null;
				}
			}

			@Override
			protected String getToolTipText(final Earthquake element) {
				final Optional<Status> status = element.getStatus();
				return status.isPresent() ? status.get().getDescription() : null;
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

			private Image image = null;

			@Override
			protected String getText(final Earthquake element) {
				return element.getMomentTensorUri().isPresent() ? MT : "";
			}

			@Override
			protected Color getForeground(final Earthquake element) {
				return col.getColumn().getDisplay().getSystemColor(SWT.COLOR_LINK_FOREGROUND);
			}

			@Override
			public Image getToolTipImage(final Earthquake element) {
				if (image != null) {
					image.dispose();
				}
				final BeachBall cachedObject = BeachBallCache.getInstance().get(element.getGuid());
				if (cachedObject != null) {
					try (final ByteArrayInputStream in = new ByteArrayInputStream(cachedObject.getBytes())) {
						final ImageData data = new ImageData(in);
						image = new Image(shell.getDisplay(), data);
						return image;
					}
					catch (final IOException e) {
						throw new IllegalStateException(e);
					}
				}
				return null;
			}

			@Override
			public Color getToolTipBackgroundColor(final Object object) {
				return col.getColumn().getDisplay().getSystemColor(SWT.COLOR_TRANSPARENT);
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
			packColumn(table, column);
		}
	}

	private static void packColumn(final Table table, final TableColumn column) {
		column.pack();
		if (Util.isCocoa()) { // colmuns are badly resized on Cocoa, more space is actually needed
			try (final CloseableResource<GC> cr = new CloseableResource<>(new GC(table))) {
				column.setWidth(column.getWidth() + cr.getResource().stringExtent("  ").x);
			}
		}
		else if (Util.isGtk()) { // colmuns are badly resized on GTK, more space is actually needed
			try (final CloseableResource<GC> cr = new CloseableResource<>(new GC(table))) {
				column.setWidth(column.getWidth() + cr.getResource().stringExtent(" ").x);
			}
		}

	}

	private static void autoSizeStatusColumn(final Table table, final Object input) {
		if (input instanceof Earthquake[]) {
			final Earthquake[] elements = (Earthquake[]) input;
			if (elements.length > 0) {
				if (elements[0].getStatus().isPresent()) {
					if (table.getColumn(COL_IDX_STATUS).getWidth() == 0) {
						packColumn(table, table.getColumn(COL_IDX_STATUS));
					}
				}
				else {
					table.getColumn(COL_IDX_STATUS).setWidth(0);
				}
			}
		}
	}

	@Override
	public void updateLanguage() {
		if (tableViewer != null) {
			final Table table = tableViewer.getTable();
			for (final Entry<Integer, Supplier<String>> e : labelsMap.entrySet()) {
				table.getColumn(e.getKey()).setText(e.getValue().get());
			}
		}
		if (contextMenu != null) {
			contextMenu.updateLanguage();
		}
	}

	public void exportCsv() {
		if (tableViewer != null && tableViewer.getTable() != null && tableViewer.getTable().getItemCount() > 0) {
			BulletinExporter.export(tableViewer.getTable());
		}
	}

	@Getter
	public class ContextMenu extends AbstractMenu {

		private final Menu menu;

		private ContextMenu(@NonNull final ResultsTable rt) {
			final Table table = tableViewer.getTable();
			menu = new Menu(table);

			// Show map...
			showMapMenuItem = newLocalizedMenuItem(menu, SWT.PUSH, LABEL_MENU_ITEM_SHOW_MAP);
			showMapMenuItem.addListener(SWT.Selection, new ShowMapListener(() -> rt));
			menu.setDefaultItem(showMapMenuItem);

			// Show moment tensor solution...
			showMomentTensorMenuItem = newLocalizedMenuItem(menu, SWT.PUSH, LABEL_MENU_ITEM_SHOW_MOMENT_TENSOR);
			showMomentTensorMenuItem.addListener(SWT.Selection, new ShowMomentTensorListener(() -> rt));

			new MenuItem(menu, SWT.SEPARATOR);

			// Open in browser...
			openBrowserMenuItem = newLocalizedMenuItem(menu, SWT.PUSH, LABEL_MENU_ITEM_OPEN_BROWSER);
			openBrowserMenuItem.addSelectionListener(new OpenInBrowserSelectionListener(() -> rt));

			// Copy link...
			copyLinkMenuItem = newLocalizedMenuItem(menu, SWT.PUSH, LABEL_MENU_ITEM_COPY_LINK);
			copyLinkMenuItem.addSelectionListener(new CopyLinkSelectionListener(() -> rt));

			new MenuItem(menu, SWT.SEPARATOR);

			// Epicenter map popup...
			epicenterMapPopupMenuItem = newLocalizedMenuItem(menu, SWT.PUSH, LABEL_MENU_ITEM_EPICENTER_MAP_POPUP);
			epicenterMapPopupMenuItem.addSelectionListener(new EpicenterMapSelectionListener(() -> rt));

			// Google Maps in browser...
			googleMapsBrowserMenuItem = newLocalizedMenuItem(menu, SWT.PUSH, LABEL_MENU_ITEM_GOOGLE_MAPS_BROWSER);
			googleMapsBrowserMenuItem.addSelectionListener(new GoogleMapsBrowserSelectionListener(() -> rt));

			new MenuItem(menu, SWT.SEPARATOR);

			exportCsvMenuItem = newLocalizedMenuItem(menu, SWT.PUSH, () -> Messages.get(LABEL_MENU_ITEM_EXPORT_CSV) + SwtUtils.getMod1ShortcutLabel(SwtUtils.KEY_SAVE));
			exportCsvMenuItem.addSelectionListener(new ExportCsvSelectionListener(() -> rt));
			exportCsvMenuItem.setAccelerator(SWT.MOD1 | SwtUtils.KEY_SAVE);

			table.addMenuDetectListener(new ResultsTableContextMenuDetectListener(rt));
		}
	}

	private class TableMouseListener extends MouseAdapter {
		@Override
		public void mouseDown(final MouseEvent e) {
			if (e.button == 1) {
				final ViewerCell cell = tableViewer.getCell(new Point(e.x, e.y));
				if (cell != null && cell.getColumnIndex() == COL_IDX_MT && MT.equals(cell.getText()) && cell.getElement() instanceof Earthquake) {
					final Earthquake earthquake = (Earthquake) cell.getElement();
					MomentTensorAsyncOperation.execute(earthquake, tableViewer.getTable().getShell());
				}
			}
		}
	}

	private class TableMouseMoveListener implements MouseMoveListener {

		private String guid = null;

		@Override
		public void mouseMove(final MouseEvent e) {
			final ViewerCell cell = tableViewer.getCell(new Point(e.x, e.y));
			if (cell != null && cell.getColumnIndex() == COL_IDX_MT && MT.equals(cell.getText()) && cell.getElement() instanceof Earthquake) {
				final Earthquake earthquake = (Earthquake) cell.getElement();
				if (guid == null || !guid.equals(earthquake.getGuid())) {
					guid = earthquake.getGuid();
					BeachBallAsyncOperation.execute(earthquake);
				}
				if (shell.getCursor() == null) {
					shell.setCursor(shell.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
				}
			}
			else if (shell.getDisplay().getSystemCursor(SWT.CURSOR_HAND).equals(shell.getCursor())) {
				shell.setCursor(null);
			}
		}
	}

}
