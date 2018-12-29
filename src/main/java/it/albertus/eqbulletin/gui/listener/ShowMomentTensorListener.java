package it.albertus.eqbulletin.gui.listener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.sun.net.httpserver.Headers;

import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.MomentTensorDialog;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.service.net.ConnectionFactory;
import it.albertus.util.IOUtils;
import it.albertus.util.StringUtils;

public class ShowMomentTensorListener implements Listener {

	private final EarthquakeBulletinGui gui;

	public ShowMomentTensorListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void handleEvent(final Event event) {
		final TableViewer tableViewer = gui.getResultsTable().getTableViewer();
		if (tableViewer != null && !tableViewer.getTable().isDisposed() && tableViewer.getStructuredSelection() != null) {
			final Earthquake selectedItem = (Earthquake) tableViewer.getStructuredSelection().getFirstElement();
			final Shell shell = gui.getShell();
			if (selectedItem != null && shell != null && !shell.isDisposed()) {
				final String momentTensorSolution = fetchMomentTensorSolution(selectedItem);
				final MomentTensorDialog dialog = new MomentTensorDialog(shell, momentTensorSolution);
				dialog.open();
			}
		}
	}

	private String fetchMomentTensorSolution(Earthquake earthquake) {
		final Headers headers = new Headers();
		headers.set("Accept", "text/*");
		headers.set("Accept-Encoding", "gzip");
		try {
			HttpURLConnection urlConnection = ConnectionFactory.makeGetRequest(earthquake.getMomentTensor(), headers);
			final String responseContentEncoding = urlConnection.getContentEncoding();
			final boolean gzip = responseContentEncoding != null && responseContentEncoding.toLowerCase().contains("gzip");
			try (final InputStream internalInputStream = urlConnection.getInputStream(); final InputStream inputStream = gzip ? new GZIPInputStream(internalInputStream) : internalInputStream; final ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
				IOUtils.copy(inputStream, buffer, 2048);
				String charsetName = StandardCharsets.US_ASCII.name();
				String contentType = urlConnection.getContentType();
				if (contentType != null) {
					contentType = contentType.toLowerCase();
					if (contentType.contains("charset=")) {
						charsetName = StringUtils.substringAfter(contentType, "charset=").trim();
					}
				}
				return buffer.toString(charsetName);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			return "";
		}

	}

}
