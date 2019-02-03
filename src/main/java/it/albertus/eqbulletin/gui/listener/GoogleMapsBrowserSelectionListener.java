package it.albertus.eqbulletin.gui.listener;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.program.Program;

import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.util.logging.LoggerFactory;

public class GoogleMapsBrowserSelectionListener extends SelectionAdapter {

	private static final Logger logger = LoggerFactory.getLogger(GoogleMapsBrowserSelectionListener.class);

	private final EarthquakeBulletinGui gui;

	public GoogleMapsBrowserSelectionListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final Earthquake selection = (Earthquake) gui.getResultsTable().getTableViewer().getStructuredSelection().getFirstElement();
		if (selection != null) {
			try {
				Program.launch(getGoogleMapsUri(selection).toURL().toString());
			}
			catch (final MalformedURLException | URISyntaxException e) {
				logger.log(Level.SEVERE, "Invalid Google Maps URL:", e);
			}
		}
	}

	private static URI getGoogleMapsUri(final Earthquake event) throws URISyntaxException {
		return new URI(String.format("https://maps.google.com/maps?q=%s,%s&ll=%s,%s&z=6", event.getLatitude().getValue(), event.getLongitude().getValue(), event.getLatitude().getValue(), event.getLongitude().getValue()));
	}

}
