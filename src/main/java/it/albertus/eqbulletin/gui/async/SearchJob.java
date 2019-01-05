package it.albertus.eqbulletin.gui.async;

import java.util.Arrays;
import java.util.List;
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

import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.Images;
import it.albertus.eqbulletin.gui.SearchForm;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.Format;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.service.BulletinProvider;
import it.albertus.eqbulletin.service.SearchJobVars;
import it.albertus.eqbulletin.service.geofon.GeofonBulletinProvider;
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
		monitor.beginTask("Search", IProgressMonitor.UNKNOWN);

		final SearchJobVars jobVariables = new SearchJobVars();

		new DisplayThreadExecutor(gui.getShell()).execute(() -> jobVariables.setFormValid(gui.getSearchForm().isValid()));

		if (jobVariables.isFormValid()) {
			jobVariables.setFormat(SearchForm.Defaults.FORMAT);

			new DisplayThreadExecutor(gui.getShell()).execute(() -> {
				gui.getSearchForm().getSearchButton().setText(Messages.get("lbl.form.button.stop"));
				gui.getShell().setCursor(gui.getShell().getDisplay().getSystemCursor(SWT.CURSOR_APPSTARTING));

				// Search parameters
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
							}
						}
						catch (final RuntimeException e) {
							logger.log(Level.WARNING, e.toString(), e);
						}
					}
				}
			});

			try {
				final List<Earthquake> newDataList = provider.getEarthquakes(jobVariables, monitor::isCanceled);
				final Earthquake[] newDataArray = newDataList.toArray(new Earthquake[0]);

				new DisplayThreadExecutor(gui.getShell()).execute(() -> {
					final Earthquake[] oldDataArray = (Earthquake[]) gui.getResultsTable().getTableViewer().getInput();
					gui.getResultsTable().getTableViewer().setInput(newDataArray);
					gui.getTrayIcon().updateToolTipText(newDataArray.length > 0 ? newDataArray[0] : null);
					if (gui.getMapCanvas().getEarthquake() != null && !newDataList.contains(gui.getMapCanvas().getEarthquake())) {
						gui.getMapCanvas().clear();
					}
					if (oldDataArray != null && !Arrays.equals(newDataArray, oldDataArray) && newDataArray.length > 0 && newDataArray[0] != null && oldDataArray.length > 0 && !newDataArray[0].equals(oldDataArray[0]) && gui.getTrayIcon().getTrayItem() != null && gui.getTrayIcon().getTrayItem().getVisible()) {
						gui.getTrayIcon().showBalloonToolTip(newDataArray[0]);
					}
				});
			}
			catch (final Exception e) {
				if (monitor.isCanceled()) {
					logger.log(Level.FINE, "Job was canceled.", e);
				}
				else {
					final String message = e.getMessage();
					logger.log(Level.WARNING, message, e);
					handleError(e.getCause() != null ? e.getCause() : e, message, IStatus.WARNING);
				}
			}
			catch (final LinkageError e) {
				logger.log(Level.SEVERE, e.toString(), e);
				handleError(e, e.toString(), IStatus.ERROR);
			}

			new DisplayThreadExecutor(gui.getShell()).execute(() -> {
				final long waitTimeInMillis = jobVariables.getWaitTimeInMillis();
				if (waitTimeInMillis > 0) {
					schedule(waitTimeInMillis);
					gui.getSearchForm().getSearchButton().setText(Messages.get("lbl.form.button.submit"));
				}
				else {
					gui.getSearchForm().cancelJob();
				}
				gui.getShell().setCursor(null);
			});
		}
		monitor.done();
		return Status.OK_STATUS;
	}

	private void handleError(final Throwable throwable, final String message, final int severity) {
		new DisplayThreadExecutor(gui.getShell()).execute(() -> {
			if (gui.getTrayIcon() == null || gui.getTrayIcon().getTrayItem() == null || !gui.getTrayIcon().getTrayItem().getVisible()) {
				EnhancedErrorDialog.openError(gui.getShell(), Messages.get("lbl.window.title"), message, severity, throwable, Images.getMainIconArray());
			}
		});
	}

	@Override
	protected void canceling() {
		provider.cancel();
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
