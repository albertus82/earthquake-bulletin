package it.albertus.eqbulletin.gui.listener;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Supplier;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.program.Program;

import it.albertus.eqbulletin.gui.ResultsTable;
import it.albertus.eqbulletin.model.Earthquake;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class GoogleMapsBrowserSelectionListener extends SelectionAdapter {

	private static final byte ZOOM_LEVEL = 6;

	@NonNull
	private final Supplier<ResultsTable> resultsTableSupplier;

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final Earthquake selection = (Earthquake) resultsTableSupplier.get().getTableViewer().getStructuredSelection().getFirstElement();
		if (selection != null) {
			try {
				Program.launch(getGoogleMapsUri(selection).toURL().toString());
			}
			catch (final MalformedURLException | URISyntaxException e) {
				log.error("Invalid Google Maps URL:", e);
			}
		}
	}

	private static URI getGoogleMapsUri(@NonNull final Earthquake event) throws URISyntaxException {
		return new URI(String.format("https://maps.google.com/maps?q=%s,%s&ll=%s,%s&z=%d", event.getLatitude().getValue(), event.getLongitude().getValue(), event.getLatitude().getValue(), event.getLongitude().getValue(), ZOOM_LEVEL));
	}

}
