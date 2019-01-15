package it.albertus.eqbulletin.gui.async;

import java.io.IOException;
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
import org.eclipse.swt.widgets.Button;

import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.Images;
import it.albertus.eqbulletin.gui.MapCanvas;
import it.albertus.eqbulletin.gui.ResultsTable;
import it.albertus.eqbulletin.gui.SearchForm;
import it.albertus.eqbulletin.gui.TrayIcon;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.Format;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.service.BulletinProvider;
import it.albertus.eqbulletin.service.CancelException;
import it.albertus.eqbulletin.service.GeofonBulletinProvider;
import it.albertus.eqbulletin.service.SearchRequest;
import it.albertus.eqbulletin.service.decode.DecodeException;
import it.albertus.eqbulletin.service.net.FetchException;
import it.albertus.jface.DisplayThreadExecutor;
import it.albertus.jface.EnhancedErrorDialog;
import it.albertus.util.logging.LoggerFactory;

public class SearchJob extends Job {

	private static final Logger logger = LoggerFactory.getLogger(SearchJob.class);

	private static SearchJob currentJob;

	private final EarthquakeBulletinGui gui;
	private final SearchRequest request;

	private BulletinProvider provider;
	private boolean canceled;

	private SearchJob(final SearchRequest request, final EarthquakeBulletinGui gui) {
		super(SearchJob.class.getSimpleName());
		this.request = request;
		this.gui = gui;
		this.setUser(true);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);

		final SearchForm form = gui.getSearchForm();

		try {
			if (request.isValid()) {
				new DisplayThreadExecutor(gui.getShell()).execute(() -> {
					form.getSearchButton().setText(Messages.get("lbl.form.button.stop"));
					AsyncOperation.setAppStartingCursor(form.getShell());
				});
				provider = new GeofonBulletinProvider();
				final Collection<Earthquake> earthquakes = provider.getEarthquakes(request, monitor::isCanceled);
				updateGui(earthquakes, gui);
			}
		}
		catch (final CancelException e) {
			logger.log(Level.FINE, "Job was canceled.", e);
		}
		catch (final FetchException | DecodeException e) {
			final String message = e.getMessage();
			logger.log(Level.WARNING, message, e);
			handleError(e.getCause() != null ? e.getCause() : e, message, IStatus.WARNING);
		}
		catch (final IOException e) {
			logger.log(Level.WARNING, e.toString(), e);
			handleError(e, e.toString(), IStatus.WARNING);
		}
		catch (final Exception | LinkageError e) {
			logger.log(Level.SEVERE, e.toString(), e);
			handleError(e, e.toString(), IStatus.ERROR);
		}
		finally {
			finish(request, form, this);
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
		if (provider != null) {
			provider.cancel();
		}
	}

	@Override
	public boolean shouldSchedule() {
		return !canceled;
	}

	@Override
	public boolean shouldRun() {
		return !canceled;
	}

	private static SearchRequest evaluateForm(final SearchForm form) {
		final SearchRequest request = new SearchRequest();
		request.setValid(form.isValid());
		request.setDelay(getDelay(form));
		if (request.isValid()) {
			final Map<String, String> params = request.getParameterMap();
			params.put(Format.KEY, getFormatValue(form));
			params.put("mode", form.getRestrictButton().getSelection() ? "mt" : "");
			if (form.getPeriodFromDateTime().isEnabled() && form.getPeriodFromDateTime().getSelection() != null) {
				params.put("datemin", form.getPeriodFromDateTime().getText());
			}
			if (form.getPeriodToDateTime().isEnabled() && form.getPeriodToDateTime().getSelection() != null) {
				params.put("datemax", form.getPeriodToDateTime().getText());
			}
			params.put("latmin", form.getLatitudeFromText().getText());
			params.put("latmax", form.getLatitudeToText().getText());
			params.put("lonmin", form.getLongitudeFromText().getText());
			params.put("lonmax", form.getLongitudeToText().getText());
			params.put("magmin", form.getMinimumMagnitudeText().getText());
			params.put("nmax", form.getResultsText().getText());
		}
		return request;
	}

	private static String getFormatValue(final SearchForm form) {
		for (final Entry<Format, Button> entry : form.getFormatRadios().entrySet()) {
			if (entry.getValue().getSelection()) {
				return entry.getKey().getValue();
			}
		}
		return SearchForm.Defaults.FORMAT.getValue();
	}

	private static long getDelay(final SearchForm form) {
		if (form.getAutoRefreshButton().getSelection()) {
			final String time = form.getAutoRefreshText().getText().trim();
			if (!time.isEmpty()) {
				try {
					final int minutes = Integer.parseInt(time);
					if (minutes > 0) {
						return minutes * 60L * 1000L;
					}
				}
				catch (final RuntimeException e) {
					logger.log(Level.WARNING, e.toString(), e);
				}
			}
		}
		return -1;
	}

	private static void updateGui(final Collection<Earthquake> earthquakes, final EarthquakeBulletinGui gui) {
		final Earthquake[] newDataArray = earthquakes.toArray(new Earthquake[earthquakes.size()]);

		final ResultsTable table = gui.getResultsTable();
		final TrayIcon icon = gui.getTrayIcon();
		final MapCanvas map = gui.getMapCanvas();

		new DisplayThreadExecutor(table.getShell()).execute(() -> {
			final Earthquake[] oldDataArray = (Earthquake[]) table.getTableViewer().getInput();
			table.getTableViewer().setInput(newDataArray);
			icon.updateToolTipText(newDataArray.length > 0 ? newDataArray[0] : null);
			if (map.getEarthquake() != null && !earthquakes.contains(map.getEarthquake())) {
				map.clear();
			}
			if (oldDataArray != null && !Arrays.equals(newDataArray, oldDataArray) && newDataArray.length > 0 && newDataArray[0] != null && oldDataArray.length > 0 && !newDataArray[0].equals(oldDataArray[0]) && icon.getTrayItem() != null && icon.getTrayItem().getVisible()) {
				icon.showBalloonToolTip(newDataArray[0]);
			}
		});
	}

	private static void finish(final SearchRequest request, final SearchForm form, final Job job) {
		new DisplayThreadExecutor(form.getShell()).execute(() -> {
			final long delay = request.getDelay();
			if (delay > 0) {
				job.schedule(delay);
			}
			else {
				cancelCurrentJob();
			}
			if (request.isValid()) {
				AsyncOperation.setDefaultCursor(form.getShell());
				form.getSearchButton().setText(Messages.get("lbl.form.button.submit"));
			}
		});
	}

	public static synchronized SearchJob getCurrentJob() {
		return currentJob;
	}

	public static synchronized void cancelCurrentJob() {
		if (currentJob != null) {
			currentJob.canceled = true;
			currentJob.cancel();
			currentJob = null;
		}
	}

	public static synchronized void scheduleNewJob(final EarthquakeBulletinGui gui) {
		cancelCurrentJob();
		currentJob = new SearchJob(evaluateForm(gui.getSearchForm()), gui);
		currentJob.schedule();
	}

}
