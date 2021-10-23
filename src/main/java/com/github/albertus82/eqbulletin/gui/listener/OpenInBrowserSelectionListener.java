package com.github.albertus82.eqbulletin.gui.listener;

import java.net.URI;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.program.Program;

import com.github.albertus82.eqbulletin.gui.ResultsTable;
import com.github.albertus82.eqbulletin.model.Earthquake;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OpenInBrowserSelectionListener extends SelectionAdapter {

	@NonNull
	private final Supplier<ResultsTable> resultsTableSupplier;

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final Earthquake selection = (Earthquake) resultsTableSupplier.get().getTableViewer().getStructuredSelection().getFirstElement();
		if (selection != null) {
			final Optional<URI> link = selection.getLink();
			if (link.isPresent()) {
				Program.launch(link.get().toString());
			}
		}
	}

}
