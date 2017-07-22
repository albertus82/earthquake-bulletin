package it.albertus.earthquake.gui.job;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

import com.dmurph.URIEncoder;

import it.albertus.earthquake.gui.EarthquakeBulletinGui;
import it.albertus.earthquake.gui.Images;
import it.albertus.earthquake.gui.SearchForm;
import it.albertus.earthquake.model.Earthquake;
import it.albertus.earthquake.model.Format;
import it.albertus.earthquake.resources.Messages;
import it.albertus.earthquake.service.BulletinProvider;
import it.albertus.earthquake.service.GeofonBulletinProvider;
import it.albertus.earthquake.service.SearchJobVars;
import it.albertus.jface.DisplayThreadExecutor;
import it.albertus.jface.EnhancedErrorDialog;
import it.albertus.util.logging.LoggerFactory;

public class SearchJob extends Job {

	private static final Logger logger = LoggerFactory.getLogger(SearchJob.class);

	private final EarthquakeBulletinGui gui;
	private final BulletinProvider provider = new GeofonBulletinProvider();

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

		final SearchJobVars jobVariables = new SearchJobVars();

		new DisplayThreadExecutor(gui.getShell()).execute(new Runnable() {
			@Override
			public void run() {
				final boolean formValid = gui.getSearchForm().isValid();
				jobVariables.setFormValid(formValid);
				if (!formValid) {
					gui.getSearchForm().getStopButton().notifyListeners(SWT.Selection, null);
				}
			}
		});

		if (jobVariables.isFormValid()) {
			jobVariables.setFormat(SearchForm.Defaults.FORMAT);

			new DisplayThreadExecutor(gui.getShell()).execute(new Runnable() {
				@Override
				public void run() {
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
					if (form.getPeriodFromDateTime().isEnabled() && form.getPeriodFromDateTime().getSelection() != null) {
						params.put("datemin", URIEncoder.encodeURI(form.getPeriodFromDateTime().getText()));
					}
					if (form.getPeriodToDateTime().isEnabled() && form.getPeriodToDateTime().getSelection() != null) {
						params.put("datemax", URIEncoder.encodeURI(form.getPeriodToDateTime().getText()));
					}
					params.put("latmin", URIEncoder.encodeURI(form.getLatitudeFromText().getText()));
					params.put("latmax", URIEncoder.encodeURI(form.getLatitudeToText().getText()));
					params.put("lonmin", URIEncoder.encodeURI(form.getLongitudeFromText().getText()));
					params.put("lonmax", URIEncoder.encodeURI(form.getLongitudeToText().getText()));
					params.put("magmin", URIEncoder.encodeURI(form.getMinimumMagnitudeText().getText()));
					params.put("nmax", URIEncoder.encodeURI(form.getResultsText().getText()));

					if (gui.getSearchForm().getAutoRefreshButton().getSelection()) {
						final String time = gui.getSearchForm().getAutoRefreshText().getText().trim();
						if (!time.isEmpty()) {
							try {
								short waitTimeInMinutes = Short.parseShort(time);
								if (waitTimeInMinutes > 0) {
									jobVariables.setWaitTimeInMillis(waitTimeInMinutes * 1000L * 60);
									gui.getSearchForm().getStopButton().setEnabled(true);
								}
							}
							catch (final RuntimeException e) {
								logger.log(Level.WARNING, e.toString(), e);
							}
						}
					}
				}
			});

			try {
				final Collection<Earthquake> earthquakes = provider.getEarthquakes(jobVariables);

				new DisplayThreadExecutor(gui.getShell()).execute(new Runnable() {
					@Override
					public void run() {
						final Earthquake[] newData = earthquakes.toArray(new Earthquake[0]);
						final Earthquake[] oldData = (Earthquake[]) gui.getResultsTable().getTableViewer().getInput();
						gui.getResultsTable().getTableViewer().setInput(newData);
						gui.getTrayIcon().updateToolTipText(newData.length > 0 ? newData[0] : null);
						if (oldData != null && !Arrays.equals(newData, oldData)) {
							gui.getMapCanvas().clear();
							if (newData.length > 0 && newData[0] != null && oldData.length > 0 && !newData[0].equals(oldData[0]) && gui.getTrayIcon().getTrayItem().getVisible()) {
								gui.getTrayIcon().showBalloonToolTip(newData[0]);
							}
						}
					}
				});
			}
			catch (final Exception e) {
				final String message = e.getMessage();
				logger.log(Level.WARNING, message, e);
				new DisplayThreadExecutor(gui.getShell()).execute(new Runnable() {
					@Override
					public void run() {
						if (gui.getTrayIcon() == null || gui.getTrayIcon().getTrayItem() == null || !gui.getTrayIcon().getTrayItem().getVisible()) {
							EnhancedErrorDialog.openError(gui.getShell(), Messages.get("lbl.window.title"), message, IStatus.WARNING, e.getCause() != null ? e.getCause() : e, Images.getMainIcons());
						}
					}
				});
			}

			new DisplayThreadExecutor(gui.getShell()).execute(new Runnable() {
				@Override
				public void run() {
					final long waitTimeInMillis = jobVariables.getWaitTimeInMillis();
					if (waitTimeInMillis > 0) {
						schedule(waitTimeInMillis);
					}
					else {
						gui.getSearchForm().getStopButton().notifyListeners(SWT.Selection, null);
					}
					gui.getShell().setCursor(null);
				}
			});
		}
		monitor.done();
		return Status.OK_STATUS;
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
