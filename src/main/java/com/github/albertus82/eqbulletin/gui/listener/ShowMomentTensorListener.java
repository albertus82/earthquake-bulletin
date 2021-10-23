package com.github.albertus82.eqbulletin.gui.listener;

import java.util.function.Supplier;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.github.albertus82.eqbulletin.gui.ResultsTable;
import com.github.albertus82.eqbulletin.gui.async.MomentTensorAsyncOperation;
import com.github.albertus82.eqbulletin.model.Earthquake;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ShowMomentTensorListener implements Listener {

	@NonNull
	private final Supplier<ResultsTable> resultsTableSupplier;

	@Override
	public void handleEvent(final Event event) {
		final TableViewer tableViewer = resultsTableSupplier.get().getTableViewer();
		if (tableViewer != null && !tableViewer.getTable().isDisposed() && tableViewer.getStructuredSelection() != null) {
			final Earthquake earthquake = (Earthquake) tableViewer.getStructuredSelection().getFirstElement();
			MomentTensorAsyncOperation.execute(earthquake, tableViewer.getTable().getShell());
		}
	}

}
