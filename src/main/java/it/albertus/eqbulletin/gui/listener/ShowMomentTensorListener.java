package it.albertus.eqbulletin.gui.listener;

import java.util.function.Supplier;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import it.albertus.eqbulletin.gui.ResultsTable;
import it.albertus.eqbulletin.gui.async.MomentTensorAsyncOperation;
import it.albertus.eqbulletin.model.Earthquake;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ShowMomentTensorListener implements Listener {

	private final @NonNull Supplier<ResultsTable> resultsTableSupplier;

	@Override
	public void handleEvent(final Event event) {
		final TableViewer tableViewer = resultsTableSupplier.get().getTableViewer();
		if (tableViewer != null && !tableViewer.getTable().isDisposed() && tableViewer.getStructuredSelection() != null) {
			final Earthquake earthquake = (Earthquake) tableViewer.getStructuredSelection().getFirstElement();
			MomentTensorAsyncOperation.execute(earthquake, resultsTableSupplier.get().getShell());
		}
	}

}
