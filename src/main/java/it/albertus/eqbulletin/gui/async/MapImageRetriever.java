package it.albertus.eqbulletin.gui.async;

import java.io.Serializable;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.widgets.Shell;

import it.albertus.eqbulletin.cache.MapImageCache;
import it.albertus.eqbulletin.gui.MapCanvas;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.MapImage;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.service.job.MapImageDownloadJob;
import it.albertus.eqbulletin.service.net.MapImageDownloader;
import it.albertus.jface.EnhancedErrorDialog;
import it.albertus.jface.SwtUtils;
import it.albertus.util.DaemonThreadFactory;
import it.albertus.util.logging.LoggerFactory;

public class MapImageRetriever implements Retriever<Earthquake, MapImage> {

	private static final Logger logger = LoggerFactory.getLogger(MapImageRetriever.class);

	private static final ThreadFactory threadFactory = new DaemonThreadFactory();

	@Override
	public MapImage retrieve(final Earthquake earthquake, final Shell shell) {
		final MapImageCache cache = MapImageCache.getInstance();
		final String guid = earthquake.getGuid();
		final MapImage cachedObject = cache.get(guid);
		if (cachedObject == null) {
			logger.log(Level.FINE, "Cache miss for key \"{0}\". Cache size: {1}.", new Serializable[] { guid, cache.getSize() });
			try {
				SwtUtils.setWaitCursor(shell);
				final MapImageDownloadJob job = new MapImageDownloadJob(earthquake);
				JobRunner.run(job, shell.getDisplay());
				final MapImage downloadedObject = job.getDownloadedObject();
				if (downloadedObject != null) {
					cache.put(guid, downloadedObject);
					return downloadedObject; // Avoid unpack from cache the first time.
				}
			}
			catch (final OperationException e) {
				logger.log(e.getLoggingLevel(), e.getMessage());
				SwtUtils.setDefaultCursor(shell);
				if (!shell.isDisposed()) {
					EnhancedErrorDialog.openError(shell, Messages.get("lbl.window.title"), e.getMessage(), e.getSeverity(), e.getCause(), shell.getDisplay().getSystemImage(e.getSystemImageId()));
				}
			}
			finally {
				SwtUtils.setDefaultCursor(shell);
			}
		}
		else {
			logger.log(Level.FINE, "Cache hit for key \"{0}\". Cache size: {1}.", new Serializable[] { guid, cache.getSize() });
			checkForUpdateAndRefreshIfNeeded(cachedObject, earthquake);
		}
		return cache.get(guid);
	}

	private static void checkForUpdateAndRefreshIfNeeded(final MapImage cachedObject, final Earthquake earthquake) {
		if (cachedObject.getEtag() != null && !cachedObject.getEtag().trim().isEmpty()) {
			final Runnable checkForUpdate = () -> {
				try {
					final MapImage downloadedObject = new MapImageDownloader().download(earthquake, cachedObject);
					if (!cachedObject.equals(downloadedObject)) {
						MapCanvas.updateMapImage(downloadedObject, earthquake); // Update UI on-the-fly.
						MapImageCache.getInstance().put(earthquake.getGuid(), downloadedObject);
					}
				}
				catch (final Exception e) {
					logger.log(Level.WARNING, e.toString(), e);
				}
			};
			threadFactory.newThread(checkForUpdate).start();
		}
	}

}
