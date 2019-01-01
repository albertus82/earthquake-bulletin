package it.albertus.eqbulletin.gui.job;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import org.eclipse.core.internal.jobs.JobStatus;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;

import com.sun.net.httpserver.Headers;

import it.albertus.eqbulletin.cache.MomentTensorCache;
import it.albertus.eqbulletin.gui.MomentTensorDialog;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.MomentTensor;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.service.net.ConnectionFactory;
import it.albertus.jface.EnhancedErrorDialog;
import it.albertus.jface.SwtUtils;
import it.albertus.util.DaemonThreadFactory;
import it.albertus.util.IOUtils;
import it.albertus.util.StringUtils;
import it.albertus.util.logging.LoggerFactory;

public class MomentTensorRetriever extends Job {

	private static final int BUFFER_SIZE = 512;

	private static final String TASK_NAME = "Fetching moment tensor";

	private static final Logger logger = LoggerFactory.getLogger(MomentTensorRetriever.class);

	private static final ExecutorService executorService = Executors.newSingleThreadExecutor(new DaemonThreadFactory());

	private static MomentTensorRetriever instance;

	private final Earthquake earthquake;
	private final MomentTensor cachedMomentTensor;

	private MomentTensor momentTensor; // The result.

	public static MomentTensor retrieve(final Earthquake earthquake, final Shell shell) {
		if (instance != null && instance.getState() != Job.NONE) {
			logger.log(Level.FINE, "Job already running, ignored call for GUID {0}.", earthquake.getGuid());
			return null;
		}
		final MomentTensorCache cache = MomentTensorCache.getInstance();
		final String guid = earthquake.getGuid();
		final MomentTensor cachedMomentTensor = cache.get(guid);
		if (cachedMomentTensor == null) {
			logger.log(Level.FINE, "Cache miss for key \"{0}\". Cache size: {1}", new Serializable[] { guid, cache.getSize() });
			try {
				SwtUtils.setWaitCursor(shell);
				instance = new MomentTensorRetriever(earthquake);
				ModalContextRunner.run(instance, shell.getDisplay());
				final MomentTensor momentTensor = instance.getMomentTensor();
				if (momentTensor != null) {
					cache.put(guid, momentTensor);
					return momentTensor; // Avoid unpack from cache the first time.
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
			logger.log(Level.FINE, "Cache hit for key \"{0}\". Cache size: {1}", new Serializable[] { guid, cache.getSize() });
			checkForUpdateAndRefreshIfNeeded(cachedMomentTensor, earthquake);
		}
		return cache.get(guid);
	}

	private static void checkForUpdateAndRefreshIfNeeded(final MomentTensor cachedMomentTensor, final Earthquake earthquake) {
		if (cachedMomentTensor.getEtag() != null && !cachedMomentTensor.getEtag().trim().isEmpty()) {
			final Runnable checkForUpdate = () -> {
				try {
					final MomentTensorRetriever operation = new MomentTensorRetriever(earthquake, cachedMomentTensor);
					operation.run();
					final MomentTensor updatedMomentTensor = operation.getMomentTensor();
					if (updatedMomentTensor != null && !cachedMomentTensor.getText().equals(updatedMomentTensor.getText())) {
						MomentTensorDialog.update(updatedMomentTensor, earthquake); // Update UI on-the-fly.
						MomentTensorCache.getInstance().put(earthquake.getGuid(), updatedMomentTensor);
					}
				}
				catch (final Exception e) {
					logger.log(Level.WARNING, e.toString(), e);
				}
			};
			executorService.execute(checkForUpdate);
		}
	}

	private MomentTensorRetriever(final Earthquake earthquake) {
		this(earthquake, null);
	}

	private MomentTensorRetriever(final Earthquake earthquake, final MomentTensor cachedMomentTensor) {
		super(TASK_NAME);
		this.earthquake = earthquake;
		this.cachedMomentTensor = cachedMomentTensor;
	}

	@Override
	public IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask(TASK_NAME, IProgressMonitor.UNKNOWN);
		try {
			run();
			monitor.done();
			return JobStatus.OK_STATUS;
		}
		catch (final IOException e) {
			return new Status(IStatus.WARNING, getClass().getName(), Messages.get("err.job.mt.show"), e);
		}
		catch (final Exception e) {
			return new Status(IStatus.ERROR, getClass().getName(), Messages.get("err.job.mt.show"), e);
		}
	}

	private void run() throws IOException {
		///////////////////////////////////////////////////////////////////
		//		try {
		//			TimeUnit.SECONDS.sleep(6);
		//		}
		//		catch (InterruptedException e) {
		//			e.printStackTrace();
		//		}
		//				if (true) {
		//					throw new IOException("dsfdfds");
		//				}
		///////////////////////////////////////////////////////////////////
		final Headers headers = new Headers();
		headers.set("Accept", "text/*");
		headers.set("Accept-Encoding", "gzip");
		if (cachedMomentTensor != null && cachedMomentTensor.getEtag() != null && !cachedMomentTensor.getEtag().trim().isEmpty()) {
			headers.set("If-None-Match", cachedMomentTensor.getEtag());
		}
		final HttpURLConnection connection = ConnectionFactory.makeGetRequest(earthquake.getMomentTensorUrl(), headers);
		if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
			momentTensor = cachedMomentTensor; // Not modified.
		}
		else {
			final String responseContentEncoding = connection.getContentEncoding();
			final boolean gzip = responseContentEncoding != null && responseContentEncoding.toLowerCase().contains("gzip");
			try (final InputStream raw = connection.getInputStream(); final InputStream in = gzip ? new GZIPInputStream(raw) : raw; final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				IOUtils.copy(in, out, BUFFER_SIZE);
				String charsetName = StandardCharsets.US_ASCII.name();
				String contentType = connection.getContentType();
				if (contentType != null) {
					contentType = contentType.toLowerCase();
					if (contentType.contains("charset=")) {
						charsetName = StringUtils.substringAfter(contentType, "charset=").trim();
					}
				}
				logger.log(Level.FINE, "Content-Type charset: {0}", charsetName);
				momentTensor = new MomentTensor(out.toString(charsetName), connection.getHeaderField("Etag"));
			}
		}
	}

	private MomentTensor getMomentTensor() {
		return momentTensor;
	}

}
