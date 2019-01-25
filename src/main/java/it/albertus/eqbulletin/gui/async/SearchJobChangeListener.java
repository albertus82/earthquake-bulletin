package it.albertus.eqbulletin.gui.async;

import static it.albertus.jface.DisplayThreadExecutor.Mode.ASYNC;
import static it.albertus.jface.DisplayThreadExecutor.Mode.SYNC;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Button;

import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.Images;
import it.albertus.eqbulletin.gui.MapCanvas;
import it.albertus.eqbulletin.gui.ResultsTable;
import it.albertus.eqbulletin.gui.TrayIcon;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.service.SearchRequest;
import it.albertus.eqbulletin.service.job.SearchJob;
import it.albertus.jface.DisplayThreadExecutor;
import it.albertus.jface.EnhancedErrorDialog;
import it.albertus.util.logging.LoggerFactory;

class SearchJobChangeListener extends JobChangeAdapter {

	private static final Logger logger = LoggerFactory.getLogger(SearchJobChangeListener.class);

	private final SearchRequest request;
	private final EarthquakeBulletinGui gui;

	SearchJobChangeListener(final SearchRequest request, final EarthquakeBulletinGui gui) {
		this.request = request;
		this.gui = gui;
	}

	@Override
	public void running(final IJobChangeEvent event) {
		logger.log(Level.FINE, "Running {0}: {1}", new Object[] { event.getJob(), request });
		new DisplayThreadExecutor(gui.getShell(), ASYNC).execute(() -> {
			AsyncOperation.setAppStartingCursor(gui.getShell());
			final Button searchButton = gui.getSearchForm().getSearchButton();
			searchButton.setText(Messages.get("lbl.form.button.stop"));
			searchButton.setEnabled(true);
		});
	}

	@Override
	public void done(final IJobChangeEvent event) {
		logger.log(Level.FINE, "Done {0}: {1}", new Object[] { event.getJob(), event.getResult() });
		try {
			if (event.getResult().matches(IStatus.ERROR | IStatus.WARNING)) {
				throw new AsyncOperationException(event.getJob().getResult());
			}
			if (event.getResult().isOK()) {
				updateGui(((SearchJob) event.getJob()).getEarthquakes(), gui);
			}
		}
		catch (final AsyncOperationException e) {
			showErrorDialog(e, gui.getTrayIcon());
		}
		finally {
			if (event.getResult().getSeverity() != IStatus.CANCEL) {
				final long delay = request.getDelay();
				if (delay > 0) {
					event.getJob().schedule(delay);
				}
				else {
					SearchAsyncOperation.cancelCurrentJob();
				}
				new DisplayThreadExecutor(gui.getShell(), ASYNC).execute(() -> {
					AsyncOperation.setDefaultCursor(gui.getShell());
					gui.getSearchForm().getSearchButton().setText(Messages.get("lbl.form.button.submit"));
				});
			}
		}
	}

	private static void updateGui(final Collection<Earthquake> earthquakes, final EarthquakeBulletinGui gui) {
		final Earthquake[] newDataArray = earthquakes.toArray(new Earthquake[earthquakes.size()]);

		final ResultsTable table = gui.getResultsTable();
		final TrayIcon icon = gui.getTrayIcon();
		final MapCanvas map = gui.getMapCanvas();

		new DisplayThreadExecutor(table.getShell(), ASYNC).execute(() -> {
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

	private static void showErrorDialog(final AsyncOperationException e, final TrayIcon trayIcon) {
		logger.log(e.getLoggingLevel(), e.getMessage(), e);
		if (trayIcon != null && !trayIcon.getShell().isDisposed()) {
			new DisplayThreadExecutor(trayIcon.getShell(), SYNC).execute(() -> {
				if (trayIcon.getTrayItem() == null || !trayIcon.getTrayItem().getVisible()) { // Show error dialog only if not minimized in the tray.
					final Window dialog = new EnhancedErrorDialog(trayIcon.getShell(), Messages.get("lbl.window.title"), e.getMessage(), e.getSeverity(), e.getCause() != null ? e.getCause() : e, Images.getMainIconArray());
					dialog.setBlockOnOpen(true); // Avoid stacking of error dialogs when auto refresh is enabled.
					dialog.open();
				}
			});
		}
	}

}
