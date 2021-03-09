package it.albertus.eqbulletin.gui.listener;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Supplier;
import java.util.logging.Level;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.program.Program;

import it.albertus.eqbulletin.gui.ResultsTable;
import it.albertus.eqbulletin.model.Earthquake;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Log
@RequiredArgsConstructor
public class GoogleMapsBrowserSelectionListener extends SelectionAdapter {

	private static final byte ZOOM_LEVEL = 6;

	@NonNull private final Supplier<ResultsTable> resultsTableSupplier;

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final Earthquake selection = (Earthquake) resultsTableSupplier.get().getTableViewer().getStructuredSelection().getFirstElement();
		if (selection != null) {
			try {
				Program.launch(getGoogleMapsUri(selection).toURL().toString());
			}
			catch (final MalformedURLException | URISyntaxException e) {
				log.log(Level.SEVERE, "Invalid Google Maps URL:", e);
			}
		}
	}

	private static URI getGoogleMapsUri(final Earthquake event) throws URISyntaxException {
		return new URI(String.format("https://maps.google.com/maps?q=%s,%s&ll=%s,%s&z=%d", event.getLatitude().getValue(), event.getLongitude().getValue(), event.getLatitude().getValue(), event.getLongitude().getValue(), ZOOM_LEVEL));
	}

}
