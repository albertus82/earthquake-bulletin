package it.albertus.earthquake.gui.job;

import it.albertus.earthquake.EarthquakeBulletin;
import it.albertus.earthquake.gui.EarthquakeBulletinGui;
import it.albertus.earthquake.gui.SearchForm;
import it.albertus.earthquake.model.Earthquake;
import it.albertus.earthquake.model.Format;
import it.albertus.earthquake.net.HttpConnector;
import it.albertus.earthquake.resources.Messages;
import it.albertus.earthquake.rss.transformer.RssItemTransformer;
import it.albertus.earthquake.rss.xml.Rss;
import it.albertus.earthquake.xhtml.TableData;
import it.albertus.earthquake.xhtml.transformer.XhtmlTableDataTransformer;
import it.albertus.jface.SwtThreadExecutor;
import it.albertus.util.NewLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.MessageBox;

import com.dmurph.URIEncoder;

public class SearchJob extends Job {

	private final EarthquakeBulletinGui gui;
	private boolean shouldRun = true;
	private boolean shouldSchedule = true;

	public SearchJob(final EarthquakeBulletinGui gui) {
		super("Search");
		this.gui = gui;
		this.setUser(true);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask("Search", 1);

		final long[] waitTimeInMillis = new long[1];

		final Format[] format = new Format[] { SearchForm.Defaults.FORMAT };

		final Map<String, String> params = new LinkedHashMap<>();

		new SwtThreadExecutor(gui.getShell()) {
			@Override
			protected void run() {
				gui.getSearchForm().getSearchButton().setEnabled(false);
				gui.getShell().setCursor(gui.getShell().getDisplay().getSystemCursor(SWT.CURSOR_WAIT));

				// Parametri di ricerca
				final SearchForm form = gui.getSearchForm();

				for (final Entry<Format, Button> entry : form.getFormatRadios().entrySet()) {
					if (entry.getValue().getSelection()) {
						format[0] = entry.getKey();
						break;
					}
				}
				params.put("fmt", format[0].getValue());
				params.put("mode", form.getRestrictButton().getSelection() ? "mt" : "");
				if (form.getPeriodFromText().isEnabled()) {
					params.put("datemin", URIEncoder.encodeURI(form.getPeriodFromText().getText()));
				}
				if (form.getPeriodToText().isEnabled()) {
					params.put("datemax", URIEncoder.encodeURI(form.getPeriodToText().getText()));
				}
				params.put("latmin", URIEncoder.encodeURI(form.getLatitudeFromText().getText()));
				params.put("latmax", URIEncoder.encodeURI(form.getLatitudeToText().getText()));
				params.put("lonmin", URIEncoder.encodeURI(form.getLongitudeFromText().getText()));
				params.put("lonmax", URIEncoder.encodeURI(form.getLongitudeToText().getText()));
				params.put("magmin", URIEncoder.encodeURI(form.getMinimumMagnitudeText().getText()));
				params.put("nmax", URIEncoder.encodeURI(form.getResultsText().getText()));

				if (gui.getSearchForm().getAutoRefreshButton().getSelection()) {
					try {
						short waitTimeInMinutes = Short.parseShort(gui.getSearchForm().getAutoRefreshText().getText());
						if (waitTimeInMinutes > 0) {
							waitTimeInMillis[0] = waitTimeInMinutes * 1000 * 60;
							gui.getSearchForm().getStopButton().setEnabled(true);
						}
					}
					catch (final RuntimeException re) {/* Ignore */}
				}
			}
		}.start();

		final StringBuilder urlSb = new StringBuilder(EarthquakeBulletin.BASE_URL).append("/eqinfo/list.php?fmt=").append(params.get("fmt"));
		for (final Entry<String, String> param : params.entrySet()) {
			if (param.getValue() != null && !param.getValue().isEmpty() && !param.getKey().equals("fmt")) {
				urlSb.append("&").append(param.getKey()).append("=").append(param.getValue());
			}
		}

		Rss rss = null;
		TableData td = null;
		InputStream is = null;
		try {
			final URL url = new URL(urlSb.toString());
			final HttpURLConnection urlConnection = openConnection(url);
			is = urlConnection.getInputStream();
			switch (format[0]) {
			case RSS:
				final JAXBContext jaxbContext = JAXBContext.newInstance(Rss.class);
				final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				rss = (Rss) jaxbUnmarshaller.unmarshal(is);
				urlConnection.disconnect();
				break;
			case XHTML:
				td = new TableData();
				try (final BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
					String line = null;
					while ((line = br.readLine()) != null) {
						if (line.trim().toLowerCase().contains("<tr")) {
							final StringBuilder block = new StringBuilder();
							while (!(line = br.readLine()).toLowerCase().contains("</tr")) {
								block.append(line.trim()).append(NewLine.SYSTEM_LINE_SEPARATOR);
							}
							td.addItem(block.toString());
						}
					}
				}
				break;
			}
		}
		catch (final Exception e) {
			e.printStackTrace();
			new SwtThreadExecutor(gui.getShell()) {
				@Override
				protected void run() {
					if (gui.getTrayIcon() == null || gui.getTrayIcon().getTrayItem() == null || !gui.getTrayIcon().getTrayItem().getVisible()) {
						final MessageBox dialog = new MessageBox(gui.getShell(), SWT.ICON_WARNING);
						dialog.setText(Messages.get("lbl.window.title"));
						dialog.setMessage(Messages.get("err.job.search"));
						dialog.open();
					}
				}
			}.start();
		}
		finally {
			try {
				is.close();
			}
			catch (final Exception e) {/* Ignore */}
		}

		final Collection<Earthquake> earthquakes = new TreeSet<>();
		try {
			switch (format[0]) {
			case RSS:
				earthquakes.addAll(RssItemTransformer.fromRss(rss));
				break;
			case XHTML:
				earthquakes.addAll(XhtmlTableDataTransformer.fromXhtml(td));
				break;
			}
		}
		catch (final Exception e) {
			e.printStackTrace();
			new SwtThreadExecutor(gui.getShell()) {
				@Override
				protected void run() {
					if (gui.getTrayIcon() == null || gui.getTrayIcon().getTrayItem() == null || !gui.getTrayIcon().getTrayItem().getVisible()) {
						final MessageBox dialog = new MessageBox(gui.getShell(), SWT.ICON_WARNING);
						dialog.setText(Messages.get("lbl.window.title"));
						dialog.setMessage(Messages.get("err.job.decode"));
						dialog.open();
					}
				}
			}.start();
		}

		new SwtThreadExecutor(gui.getShell()) {
			@Override
			protected void run() {
				final Earthquake[] newData = earthquakes.toArray(new Earthquake[0]);
				final Earthquake[] oldData = (Earthquake[]) gui.getResultsTable().getTableViewer().getInput();
				gui.getResultsTable().getTableViewer().setInput(newData);
				if (oldData != null && !Arrays.equals(newData, oldData)) {
					gui.getMapCanvas().clear();
					if (newData.length > 0 && newData[0] != null) {
						gui.getTrayIcon().showBalloonToolTip(newData[0]);
					}
				}
			}
		}.start();

		new SwtThreadExecutor(gui.getShell()) {
			@Override
			protected void run() {
				if (waitTimeInMillis[0] > 0) {
					schedule(waitTimeInMillis[0]);
				}
				else {
					gui.getSearchForm().getStopButton().notifyListeners(SWT.Selection, null);
				}
				gui.getShell().setCursor(null);
			}
		}.start();

		monitor.done();
		return Status.OK_STATUS;
	}

	private HttpURLConnection openConnection(final URL url) throws IOException {
		final HttpURLConnection urlConnection = HttpConnector.openConnection(url);
		urlConnection.addRequestProperty("Accept", "*/xml");
		return urlConnection;
	}

	@Override
	public boolean shouldSchedule() {
		return shouldSchedule;
	}

	@Override
	public boolean shouldRun() {
		return shouldRun;
	}

	public void setShouldRun(final boolean shouldRun) {
		this.shouldRun = shouldRun;
	}

	public void setShouldSchedule(final boolean shouldSchedule) {
		this.shouldSchedule = shouldSchedule;
	}

}
