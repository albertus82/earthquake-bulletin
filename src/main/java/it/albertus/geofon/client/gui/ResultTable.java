package it.albertus.geofon.client.gui;

import it.albertus.geofon.client.gui.job.DownloadMapJob;
import it.albertus.geofon.client.model.Earthquake;
import it.albertus.geofon.client.model.Status;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.jobs.Job;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class ResultTable {

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

	public ResultTable(final Composite parent, final Object layoutData, final GeofonClientGui gui) {
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
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent se) {
				if (!table.isDisposed() && tableViewer.getStructuredSelection() != null) {
					final Earthquake selectedItem = (Earthquake) tableViewer.getStructuredSelection().getFirstElement();
					String guid = selectedItem.getGuid();
					final MapCache cache = gui.getMapCanvas().getCache();
					if (cache.contains(guid)) {
						gui.getMapCanvas().setImage(cache.get(guid));
					}
					else {
						if ((gui.getJob() == null || gui.getJob().getState() == Job.NONE)) {
							gui.getShell().setCursor(gui.getShell().getDisplay().getSystemCursor(SWT.CURSOR_WAIT));
							gui.setJob(new DownloadMapJob(gui, selectedItem));
							gui.getJob().schedule();
						}
					}
				}
			}
		});
		tableViewer.setContentProvider(new ArrayContentProvider());
		comparator = new EarthquakeViewerComparator();
		tableViewer.setComparator(comparator);
	}

	private void createColumns(final Composite parent, final TableViewer viewer) {
		final String[] titles = { "Time", "Magnitudo", "Latitude", "Longitude", "Depth", "Status", "Region" };

		TableViewerColumn col = createTableViewerColumn(titles[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final Earthquake earthquake = (Earthquake) element;
				return formatDate(earthquake.getTime());
			}
		});

		col = createTableViewerColumn(titles[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final Earthquake earthquake = (Earthquake) element;
				return Float.toString(earthquake.getMagnitudo());
			}

		});

		col = createTableViewerColumn(titles[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final Earthquake earthquake = (Earthquake) element;
				return String.valueOf(earthquake.getLatitude());
			}
		});

		col = createTableViewerColumn(titles[3], 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final Earthquake earthquake = (Earthquake) element;
				return String.valueOf(earthquake.getLongitude());
			}
		});

		col = createTableViewerColumn(titles[4], 4);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final Earthquake earthquake = (Earthquake) element;
				return String.valueOf(earthquake.getDepth());
			}
		});

		col = createTableViewerColumn(titles[5], 5);
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

		col = createTableViewerColumn(titles[6], 6);
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

	public TableViewer getTableViewer() {
		return tableViewer;
	}

}
