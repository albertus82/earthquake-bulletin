package com.github.albertus82.eqbulletin.gui.async;

import static it.albertus.jface.DisplayThreadExecutor.Mode.ASYNC;
import static it.albertus.jface.DisplayThreadExecutor.Mode.SYNC;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Button;

import com.github.albertus82.eqbulletin.gui.EarthquakeBulletinGui;
import com.github.albertus82.eqbulletin.gui.Images;
import com.github.albertus82.eqbulletin.gui.MapCanvas;
import com.github.albertus82.eqbulletin.gui.ResultsTable;
import com.github.albertus82.eqbulletin.gui.StatusBar;
import com.github.albertus82.eqbulletin.gui.TrayIcon;
import com.github.albertus82.eqbulletin.model.Bulletin;
import com.github.albertus82.eqbulletin.model.Earthquake;
import com.github.albertus82.eqbulletin.resources.Messages;
import com.github.albertus82.eqbulletin.service.SearchRequest;
import com.github.albertus82.eqbulletin.service.job.SearchJob;

import it.albertus.jface.DisplayThreadExecutor;
import it.albertus.jface.EnhancedErrorDialog;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class SearchJobChangeListener extends JobChangeAdapter {

	@NonNull
	private final SearchRequest request;
	@NonNull
	private final EarthquakeBulletinGui gui;

	@Override
	public void running(final IJobChangeEvent event) {
		log.debug("Running {}: {}", event.getJob(), request);
		new DisplayThreadExecutor(gui.getShell(), ASYNC).execute(() -> {
			AsyncOperation.setAppStartingCursor(gui.getShell());
			final Button searchButton = gui.getSearchForm().getSearchButton();
			searchButton.setText(Messages.get("label.form.button.stop"));
			searchButton.setEnabled(true);
		});
	}

	@Override
	public void done(final IJobChangeEvent event) {
		log.debug("Done {}: {}", event.getJob(), event.getResult());
		if (event.getResult().getSeverity() != IStatus.CANCEL) {
			try {
				if (!event.getResult().isOK()) {
					throw new AsyncOperationException(event.getJob().getResult());
				}
				final Optional<Bulletin> bulletin = ((SearchJob) event.getJob()).getBulletin();
				if (bulletin.isPresent()) {
					updateGui(bulletin.get(), gui);
				}
			}
			catch (final AsyncOperationException e) {
				e.log(log);
				showErrorDialog(e, gui.getTrayIcon());
			}
			finally {
				final Optional<Long> delay = request.getDelay();
				if (delay.isPresent() && delay.get() > 0) {
					event.getJob().schedule(delay.get());
				}
				else {
					SearchAsyncOperation.cancelCurrentJob();
				}
				new DisplayThreadExecutor(gui.getShell(), ASYNC).execute(() -> {
					AsyncOperation.setDefaultCursor(gui.getShell());
					gui.getSearchForm().getSearchButton().setText(Messages.get("label.form.button.submit"));
				});
			}
		}
	}

	private static void updateGui(final Bulletin bulletin, final EarthquakeBulletinGui gui) {
		final Collection<Earthquake> events = bulletin.getEvents();
		final Earthquake[] newDataArray = events.toArray(new Earthquake[0]);

		final ResultsTable table = gui.getResultsTable();
		final TrayIcon icon = gui.getTrayIcon();
		final MapCanvas map = gui.getMapCanvas();
		final StatusBar bar = gui.getStatusBar();

		new DisplayThreadExecutor(table.getTableViewer().getTable(), ASYNC).execute(() -> {
			final Earthquake[] oldDataArray = (Earthquake[]) table.getTableViewer().getInput();
			if (!Arrays.equals(oldDataArray, newDataArray)) {
				log.debug("Data has changed, performing table update.");
				table.getTableViewer().setInput(newDataArray);
				icon.updateTooltipText(newDataArray.length > 0 ? newDataArray[0] : null);
				if (!eventsContainsMapEvent(events, map)) {
					map.clear();
				}
				if (oldDataArray != null && newDataArray.length > 0 && newDataArray[0] != null && oldDataArray.length > 0 && !newDataArray[0].getGuid().equals(oldDataArray[0].getGuid()) && icon.getTrayItem() != null && icon.getTrayItem().getVisible()) {
					icon.showBalloonTooltip(newDataArray[0]);
				}
			}
			bar.setLastUpdateTime(bulletin.getInstant());
			bar.setItemCount(events.size());
			bar.refresh();
		});
	}

	private static boolean eventsContainsMapEvent(final Iterable<Earthquake> events, final MapCanvas mapCanvas) {
		if (mapCanvas != null) {
			final Earthquake mapEvent = mapCanvas.getEarthquake();
			if (mapEvent != null) {
				for (final Earthquake event : events) {
					if (mapEvent.getGuid().equals(event.getGuid())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static void showErrorDialog(@NonNull final AsyncOperationException e, final TrayIcon trayIcon) {
		if (trayIcon != null && !trayIcon.getShell().isDisposed()) {
			new DisplayThreadExecutor(trayIcon.getShell(), SYNC).execute(() -> {
				if (trayIcon.getTrayItem() == null || !trayIcon.getTrayItem().getVisible()) { // Show error dialog only if not minimized in the tray.
					final Window dialog = new EnhancedErrorDialog(trayIcon.getShell(), EarthquakeBulletinGui.getApplicationName(), e.getMessage(), e.getSeverity(), e.getCause() != null ? e.getCause() : e, Images.getAppIconArray());
					dialog.setBlockOnOpen(true); // Avoid stacking of error dialogs when auto refresh is enabled.
					dialog.open();
				}
			});
		}
	}

}
