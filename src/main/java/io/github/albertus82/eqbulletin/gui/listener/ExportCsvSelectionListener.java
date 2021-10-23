package io.github.albertus82.eqbulletin.gui.listener;

import java.util.function.Supplier;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import io.github.albertus82.eqbulletin.gui.ResultsTable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExportCsvSelectionListener extends SelectionAdapter {

	@NonNull
	private final Supplier<ResultsTable> resultsTableSupplier;

	@Override
	public void widgetSelected(final SelectionEvent e) {
		final ResultsTable resultsTable = resultsTableSupplier.get();
		if (resultsTable != null) {
			resultsTable.exportCsv();
		}
	}

}
