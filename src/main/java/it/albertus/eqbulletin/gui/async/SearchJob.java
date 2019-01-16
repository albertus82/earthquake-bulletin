package it.albertus.eqbulletin.gui.async;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.Images;
import it.albertus.eqbulletin.gui.MapCanvas;
import it.albertus.eqbulletin.gui.ResultsTable;
import it.albertus.eqbulletin.gui.SearchForm;
import it.albertus.eqbulletin.gui.TrayIcon;
import it.albertus.eqbulletin.model.Earthquake;
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

	private final EarthquakeBulletinGui gui;
	private final SearchRequest request;

	private BulletinProvider provider;
	private boolean canceled;

	public SearchJob(final SearchRequest request, final EarthquakeBulletinGui gui) {
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
			new DisplayThreadExecutor(gui.getShell()).execute(() -> {
				form.getSearchButton().setText(Messages.get("lbl.form.button.stop"));
				AsyncOperation.setAppStartingCursor(form.getShell());
			});
			provider = new GeofonBulletinProvider();
			final Collection<Earthquake> earthquakes = provider.getEarthquakes(request, monitor::isCanceled);
			updateGui(earthquakes, gui);
		}
		catch (final CancelException e) {
			logger.log(Level.FINE, "Job was canceled:", e);
		}
		catch (final FetchException | DecodeException e) {
			final String message = e.getMessage();
			logger.log(Level.WARNING, message, e);
			handleError(e.getCause() != null ? e.getCause() : e, message, IStatus.WARNING);
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
				SearchAsyncOperation.cancelCurrentJob();
			}
			AsyncOperation.setDefaultCursor(form.getShell());
			form.getSearchButton().setText(Messages.get("lbl.form.button.submit"));
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

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(final boolean canceled) {
		this.canceled = canceled;
	}

}
