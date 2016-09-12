package it.albertus.geofon.client.gui;

import it.albertus.geofon.client.gui.listener.CopyLinkSelectionListener;
import it.albertus.geofon.client.gui.listener.GoogleMapsSelectionListener;
import it.albertus.geofon.client.gui.listener.OpenInBrowserSelectionListener;
import it.albertus.geofon.client.gui.listener.ResultsTableContextMenuDetectListener;
import it.albertus.geofon.client.gui.listener.ShowMapListener;
import it.albertus.geofon.client.model.Earthquake;
import it.albertus.geofon.client.model.Status;
import it.albertus.geofon.client.resources.Messages;
import it.albertus.util.Localized;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class ResultsTable {

	private static final int NUMBER_OF_COLUMNS = 7;

	/** Use {@link #formatDate} method instead. */
	@Deprecated
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

	public static synchronized String formatDate(final Date date) {
		return dateFormat.format(date);
	}

	enum TableDataKey {
		INITIALIZED(Boolean.class);

		private final Class<?> type;

		private TableDataKey(final Class<?> type) {
			this.type = type;
		}

		public Class<?> getType() {
			return type;
		}
	}

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

		public void setColumn(int column) {
			if (column == this.propertyIndex) {
				direction = 1 - direction;
			}
			else {
				this.propertyIndex = column;
				direction = DESCENDING;
			}
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			final Earthquake eq1 = (Earthquake) e1;
			final Earthquake eq2 = (Earthquake) e2;
			int rc = 0;
			switch (propertyIndex) {
			case 0:
				rc = eq1.getTime().compareTo(eq2.getTime());
				break;
			case 1:
				rc = Float.compare(eq1.getMagnitudo(), eq2.getMagnitudo());
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
	private final HashMap<Integer, Localized> labelsMap = new HashMap<>(NUMBER_OF_COLUMNS);

	private final Menu contextMenu;
	private final MenuItem showMapMenuItem;
	private final MenuItem openInBrowserMenuItem;
	private final MenuItem copyLinkMenuItem;
	private final MenuItem googleMapsMenuItem;

	public ResultsTable(final Composite parent, final Object layoutData, final GeofonClientGui gui) {
		tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION) {
			@Override
			protected void inputChanged(Object input, Object oldInput) {
				super.inputChanged(input, oldInput);
				if (!(Boolean) tableViewer.getData(TableDataKey.INITIALIZED.toString())) {
					final Table table = tableViewer.getTable();
					table.setRedraw(false);
					final TableColumn sortedColumn = table.getSortColumn();
					table.setSortColumn(null);
					for (int j = 0; j < table.getColumns().length; j++) {
						table.getColumn(j).pack();
					}
					table.setSortColumn(sortedColumn);
					table.setRedraw(true);
					tableViewer.setData(TableDataKey.INITIALIZED.toString(), true);
				}
			}
		};
		tableViewer.setData(TableDataKey.INITIALIZED.toString(), false);
		createColumns(parent, tableViewer);
		final Table table = tableViewer.getTable();
		table.setLayoutData(layoutData);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.addListener(SWT.DefaultSelection, new ShowMapListener(gui));
		tableViewer.setContentProvider(new ArrayContentProvider());
		comparator = new EarthquakeViewerComparator();
		tableViewer.setComparator(comparator);

		contextMenu = new Menu(table);

		// Show map...
		showMapMenuItem = new MenuItem(contextMenu, SWT.PUSH);
		showMapMenuItem.setText(Messages.get("lbl.menu.item.show.map"));
		showMapMenuItem.addListener(SWT.Selection, new ShowMapListener(gui));
		contextMenu.setDefaultItem(showMapMenuItem);

		new MenuItem(contextMenu, SWT.SEPARATOR);

		// Open in browser...
		openInBrowserMenuItem = new MenuItem(contextMenu, SWT.PUSH);
		openInBrowserMenuItem.setText(Messages.get("lbl.menu.item.open.in.browser"));
		openInBrowserMenuItem.addSelectionListener(new OpenInBrowserSelectionListener(this));

		// Copy link...
		copyLinkMenuItem = new MenuItem(contextMenu, SWT.PUSH);
		copyLinkMenuItem.setText(Messages.get("lbl.menu.item.copy.link"));
		copyLinkMenuItem.addSelectionListener(new CopyLinkSelectionListener(this));

		// Google Maps...
		googleMapsMenuItem = new MenuItem(contextMenu, SWT.PUSH);
		googleMapsMenuItem.setText(Messages.get("lbl.menu.item.google.maps"));
		googleMapsMenuItem.addSelectionListener(new GoogleMapsSelectionListener(this));

		table.addMenuDetectListener(new ResultsTableContextMenuDetectListener(this));
	}

	private void createColumns(final Composite parent, final TableViewer viewer) {
		labelsMap.put(0, new Localized() {
			@Override
			public String getString() {
				return Messages.get("lbl.table.time");
			}
		});
		labelsMap.put(1, new Localized() {
			@Override
			public String getString() {
				return Messages.get("lbl.table.magnitudo");
			}
		});
		labelsMap.put(2, new Localized() {
			@Override
			public String getString() {
				return Messages.get("lbl.table.latitude");
			}
		});
		labelsMap.put(3, new Localized() {
			@Override
			public String getString() {
				return Messages.get("lbl.table.longitude");
			}
		});
		labelsMap.put(4, new Localized() {
			@Override
			public String getString() {
				return Messages.get("lbl.table.depth");
			}
		});
		labelsMap.put(5, new Localized() {
			@Override
			public String getString() {
				return Messages.get("lbl.table.status");
			}
		});
		labelsMap.put(6, new Localized() {
			@Override
			public String getString() {
				return Messages.get("lbl.table.region");
			}
		});

		TableViewerColumn col = createTableViewerColumn(labelsMap.get(0).getString(), 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final Earthquake earthquake = (Earthquake) element;
				return formatDate(earthquake.getTime());
			}
		});

		col = createTableViewerColumn(labelsMap.get(1).getString(), 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final Earthquake earthquake = (Earthquake) element;
				return Float.toString(earthquake.getMagnitudo());
			}

		});

		col = createTableViewerColumn(labelsMap.get(2).getString(), 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final Earthquake earthquake = (Earthquake) element;
				return String.valueOf(earthquake.getLatitude());
			}
		});

		col = createTableViewerColumn(labelsMap.get(3).getString(), 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final Earthquake earthquake = (Earthquake) element;
				return String.valueOf(earthquake.getLongitude());
			}
		});

		col = createTableViewerColumn(labelsMap.get(4).getString(), 4);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final Earthquake earthquake = (Earthquake) element;
				return String.valueOf(earthquake.getDepth());
			}
		});

		col = createTableViewerColumn(labelsMap.get(5).getString(), 5);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final Earthquake earthquake = (Earthquake) element;
				return String.valueOf(earthquake.getStatus());
			}

			@Override
			public Color getForeground(final Object element) {
				final Earthquake earthquake = (Earthquake) element;
				return parent.getDisplay().getSystemColor(Status.A.equals(earthquake.getStatus()) ? SWT.COLOR_RED : SWT.COLOR_DARK_GREEN);
			}
		});

		col = createTableViewerColumn(labelsMap.get(6).getString(), 6);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final Earthquake earthquake = (Earthquake) element;
				return String.valueOf(earthquake.getRegion());
			}
		});

		final Table table = viewer.getTable();
		table.setRedraw(false);
		for (int j = 0; j < table.getColumns().length; j++) {
			table.getColumn(j).pack();
		}
		table.setRedraw(true);
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
		final SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comparator.setColumn(index);
				int dir = comparator.getDirection();
				tableViewer.getTable().setSortDirection(dir);
				tableViewer.getTable().setSortColumn(column);
				tableViewer.refresh();
			}
		};
		return selectionAdapter;
	}

	public void updateTexts() {
		final Table table = tableViewer.getTable();
		for (final int i : labelsMap.keySet()) {
			table.getColumn(i).setText(labelsMap.get(i).getString());
		}
		showMapMenuItem.setText(Messages.get("lbl.menu.item.show.map"));
		googleMapsMenuItem.setText(Messages.get("lbl.menu.item.google.maps"));
		openInBrowserMenuItem.setText(Messages.get("lbl.menu.item.open.in.browser"));
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}

	public Menu getContextMenu() {
		return contextMenu;
	}

	public MenuItem getShowMapMenuItem() {
		return showMapMenuItem;
	}

	public MenuItem getCopyLinkMenuItem() {
		return copyLinkMenuItem;
	}

	public MenuItem getOpenInBrowserMenuItem() {
		return openInBrowserMenuItem;
	}

	public MenuItem getGoogleMapsMenuItem() {
		return googleMapsMenuItem;
	}

}
