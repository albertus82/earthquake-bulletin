package it.albertus.geofon.client.gui;

import it.albertus.geofon.client.model.Earthquake;
import it.albertus.jface.SwtThreadExecutor;

import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class ResultTable {

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

	private final TableViewer tableViewer;
	private volatile List<Earthquake> currentData;

	public ResultTable(final Composite parent, final Object layoutData, final GeofonClientGui gui) {
		tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		tableViewer.setData(TableDataKey.INITIALIZED.toString(), false);
		final Table table = tableViewer.getTable();
		table.setLayoutData(layoutData);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	public void clear() {
		if (tableViewer != null) {
			final Table table = tableViewer.getTable();
			if (table != null && !table.isDisposed() && table.getColumns() != null && table.getColumns().length != 0) {
				table.setRedraw(false);
				table.removeAll();
				table.setRedraw(true);
			}
		}
	}

	public void showResults(final List<Earthquake> earthquakes) {
		if (earthquakes != null && !earthquakes.isEmpty() && !earthquakes.equals(currentData)) {
			currentData = earthquakes;
			final Table table = tableViewer.getTable();
			new SwtThreadExecutor(table) {
				@Override
				protected void run() {
					clear();

					// Disattivazione ridisegno automatico...
					table.setRedraw(false);

					// Header (una tantum)...
					if (!(Boolean) tableViewer.getData(TableDataKey.INITIALIZED.toString())) {
						TableColumn column = new TableColumn(table, SWT.NONE);
						column.setText("Time");
						column = new TableColumn(table, SWT.NONE);
						column.setText("Magnitudo");
						column = new TableColumn(table, SWT.NONE);
						column.setText("Latitude");
						column = new TableColumn(table, SWT.NONE);
						column.setText("Longitude");
						column = new TableColumn(table, SWT.NONE);
						column.setText("Depth");
						column = new TableColumn(table, SWT.NONE);
						column.setText("Status");
						column = new TableColumn(table, SWT.NONE);
						column.setText("Region");
					}

					for (final Earthquake earthquake : earthquakes) {
						final TableItem item = new TableItem(table, SWT.NONE);
						int i = 0;
						item.setText(i++, String.valueOf(earthquake.getTime()));
						item.setText(i++, String.valueOf(earthquake.getMagnitudo()));
						item.setText(i++, String.valueOf(earthquake.getLatitude()));
						item.setText(i++, String.valueOf(earthquake.getLongitude()));
						item.setText(i++, String.valueOf(earthquake.getDepth()));
						item.setText(i++, String.valueOf(earthquake.getStatus()));
						item.setText(i++, String.valueOf(earthquake.getRegion()));
					}

					// Dimensionamento delle colonne (una tantum)...
					if (!(Boolean) tableViewer.getData(TableDataKey.INITIALIZED.toString())) {
						for (int j = 0; j < table.getColumns().length; j++) {
							table.getColumn(j).pack();
						}
						table.setData(TableDataKey.INITIALIZED.toString(), true);
					}

					// Attivazione ridisegno automatico...
					table.setRedraw(true);
				}
			}.start();
		}
	}

}
