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

	public ResultTable(final Composite parent, final Object layoutData, final GeofonClientGui gui) {
		tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		final Table table = tableViewer.getTable();
		table.setLayoutData(layoutData);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setData(TableDataKey.INITIALIZED.toString(), false);
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
		if (earthquakes != null && !earthquakes.isEmpty()) {
			final Table table = tableViewer.getTable();
			new SwtThreadExecutor(table) {
				@Override
				protected void run() {
					// Disattivazione ridisegno automatico...
					table.setRedraw(false);
					clear();

					// Header (una tantum)...
					if (!(Boolean) table.getData(TableDataKey.INITIALIZED.toString())) {
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

					int i = 0;
					for (final Earthquake e : earthquakes) {
						final TableItem item = new TableItem(table, SWT.NONE);
						item.setText(i++, String.valueOf(e.getTime()));
						item.setText(i++, String.valueOf(e.getMagnitudo()));
						item.setText(i++, String.valueOf(e.getLatitude()));
						item.setText(i++, String.valueOf(e.getLongitude()));
						item.setText(i++, String.valueOf(e.getDepth()));
						item.setText(i++, String.valueOf(e.getStatus()));
						item.setText(i++, String.valueOf(e.getRegion()));
						i = 0;
					}

					// Dimensionamento delle colonne (una tantum)...
					if (!(Boolean) table.getData(TableDataKey.INITIALIZED.toString())) {
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
