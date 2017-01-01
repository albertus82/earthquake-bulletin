package it.albertus.earthquake.gui.job;

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
import java.util.zip.GZIPInputStream;

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

import it.albertus.earthquake.EarthquakeBulletin;
import it.albertus.earthquake.gui.EarthquakeBulletinGui;
import it.albertus.earthquake.gui.SearchForm;
import it.albertus.earthquake.html.TableData;
import it.albertus.earthquake.html.transformer.HtmlTableDataTransformer;
import it.albertus.earthquake.model.Earthquake;
import it.albertus.earthquake.model.Format;
import it.albertus.earthquake.net.HttpConnector;
import it.albertus.earthquake.resources.Messages;
import it.albertus.earthquake.rss.transformer.RssItemTransformer;
import it.albertus.earthquake.rss.xml.Rss;
import it.albertus.jface.SwtThreadExecutor;
import it.albertus.util.IOUtils;
import it.albertus.util.NewLine;

public class SearchJob extends Job {

	private final EarthquakeBulletinGui gui;
	private boolean shouldRun = true;
	private boolean shouldSchedule = true;

	public SearchJob(final EarthquakeBulletinGui gui) {
		super("Search");
		this.gui = gui;
		this.setUser(true);
	}

	private class JobVariables {
		private boolean formValid;
		private long waitTimeInMillis;
		private Format format;
		private boolean error;
		private final Map<String, String> params = new LinkedHashMap<>();

		public boolean isFormValid() {
			return formValid;
		}

		public void setFormValid(boolean formValid) {
			this.formValid = formValid;
		}

		public long getWaitTimeInMillis() {
			return waitTimeInMillis;
		}

		public void setWaitTimeInMillis(long waitTimeInMillis) {
			this.waitTimeInMillis = waitTimeInMillis;
		}

		public Format getFormat() {
			return format;
		}

		public void setFormat(Format format) {
			this.format = format;
		}

		public boolean isError() {
			return error;
		}

		public void setError(boolean error) {
			this.error = error;
		}

		public Map<String, String> getParams() {
			return params;
		}
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		try {
			monitor.beginTask("Search", 1);

			final JobVariables jobVariables = new JobVariables();

			new SwtThreadExecutor(gui.getShell()) {
				@Override
				protected void run() {
					final boolean formValid = gui.getSearchForm().isValid();
					jobVariables.setFormValid(formValid);
					if (!formValid) {
						gui.getSearchForm().getStopButton().notifyListeners(SWT.Selection, null);
					}
				}
			}.start();

			if (jobVariables.isFormValid()) {
				jobVariables.setFormat(SearchForm.Defaults.FORMAT);

				new SwtThreadExecutor(gui.getShell()) {
					@Override
					protected void run() {
						gui.getSearchForm().updateButtons();
						gui.getShell().setCursor(gui.getShell().getDisplay().getSystemCursor(SWT.CURSOR_WAIT));

						// Parametri di ricerca
						final SearchForm form = gui.getSearchForm();

						for (final Entry<Format, Button> entry : form.getFormatRadios().entrySet()) {
							if (entry.getValue().getSelection()) {
								jobVariables.setFormat(entry.getKey());
								break;
							}
						}
						final Map<String, String> params = jobVariables.getParams();
						params.put("fmt", jobVariables.getFormat().getValue());
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
									jobVariables.setWaitTimeInMillis(waitTimeInMinutes * 1000 * 60);
									gui.getSearchForm().getStopButton().setEnabled(true);
								}
							}
							catch (final RuntimeException re) {/* Ignore */}
						}
					}
				}.start();

				final StringBuilder urlSb = new StringBuilder(EarthquakeBulletin.BASE_URL).append("/eqinfo/list.php?fmt=").append(jobVariables.getParams().get("fmt"));
				for (final Entry<String, String> param : jobVariables.getParams().entrySet()) {
					if (param.getValue() != null && !param.getValue().isEmpty() && !param.getKey().equals("fmt")) {
						urlSb.append("&").append(param.getKey()).append("=").append(param.getValue());
					}
				}

				Rss rss = null;
				TableData td = null;
				InputStream innerStream = null;
				InputStream wrapperStream = null;
				try {
					final URL url = new URL(urlSb.toString());
					final HttpURLConnection urlConnection = openConnection(url);
					final String responseContentEncoding = urlConnection.getHeaderField("Content-Encoding");
					final boolean gzip = responseContentEncoding != null && responseContentEncoding.toLowerCase().contains("gzip");
					innerStream = urlConnection.getInputStream();
					if (gzip) {
						wrapperStream = new GZIPInputStream(innerStream);
					}
					else {
						wrapperStream = innerStream;
					}
					switch (jobVariables.getFormat()) {
					case RSS:
						final JAXBContext jaxbContext = JAXBContext.newInstance(Rss.class);
						final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
						rss = (Rss) jaxbUnmarshaller.unmarshal(wrapperStream);
						urlConnection.disconnect();
						break;
					case HTML:
						td = new TableData();
						try (final BufferedReader br = new BufferedReader(new InputStreamReader(wrapperStream))) {
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
					jobVariables.setError(true);
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
					IOUtils.closeQuietly(wrapperStream, innerStream);
				}

				final Collection<Earthquake> earthquakes = new TreeSet<>();
				if (!jobVariables.isError()) {
					try {
						switch (jobVariables.getFormat()) {
						case RSS:
							earthquakes.addAll(RssItemTransformer.fromRss(rss));
							break;
						case HTML:
							earthquakes.addAll(HtmlTableDataTransformer.fromHtml(td));
							break;
						}
					}
					catch (final Exception e) {
						jobVariables.setError(true);
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
				}

				if (!jobVariables.isError()) {
					new SwtThreadExecutor(gui.getShell()) {
						@Override
						protected void run() {
							final Earthquake[] newData = earthquakes.toArray(new Earthquake[0]);
							final Earthquake[] oldData = (Earthquake[]) gui.getResultsTable().getTableViewer().getInput();
							gui.getResultsTable().getTableViewer().setInput(newData);
							if (oldData != null && !Arrays.equals(newData, oldData)) {
								gui.getMapCanvas().clear();
								if (newData.length > 0 && newData[0] != null && oldData.length > 0 && !newData[0].equals(oldData[0])) {
									gui.getTrayIcon().showBalloonToolTip(newData[0]);
								}
							}
						}
					}.start();
				}

				new SwtThreadExecutor(gui.getShell()) {
					@Override
					protected void run() {
						final long waitTimeInMillis = jobVariables.getWaitTimeInMillis();
						if (waitTimeInMillis > 0) {
							schedule(waitTimeInMillis);
						}
						else {
							gui.getSearchForm().getStopButton().notifyListeners(SWT.Selection, null);
						}
						gui.getShell().setCursor(null);
					}
				}.start();
			}
			monitor.done();
			return Status.OK_STATUS;
		}
		catch (final RuntimeException re) {
			re.printStackTrace();
			throw re;
		}
	}

	private HttpURLConnection openConnection(final URL url) throws IOException {
		final HttpURLConnection urlConnection = HttpConnector.openConnection(url);
		urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");
		urlConnection.addRequestProperty("Accept", "*/*");
		urlConnection.addRequestProperty("Accept-Encoding", "gzip");
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
