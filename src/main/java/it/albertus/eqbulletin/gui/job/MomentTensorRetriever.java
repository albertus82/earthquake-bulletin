package it.albertus.eqbulletin.gui.job;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.sun.net.httpserver.Headers;

import it.albertus.eqbulletin.cache.MomentTensorCache;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.MomentTensor;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.service.net.ConnectionFactory;
import it.albertus.jface.EnhancedErrorDialog;
import it.albertus.jface.SwtUtils;
import it.albertus.util.IOUtils;
import it.albertus.util.StringUtils;
import it.albertus.util.logging.LoggerFactory;

public class MomentTensorRetriever implements IRunnableWithProgress {

	private static final int BUFFER_SIZE = 512;

	private static final String TASK_NAME = "Fetching moment tensor";

	private static final Logger logger = LoggerFactory.getLogger(MomentTensorRetriever.class);

	private final Earthquake earthquake;

	private MomentTensor result;

	public static MomentTensor retrieve(final Earthquake earthquake, final Shell shell) {
		final MomentTensorCache cache = MomentTensorCache.getInstance();
		final String guid = earthquake.getGuid();
		if (!cache.contains(guid)) {
			logger.log(Level.FINE, "Cache miss for key \"{0}\". Cache size: {1}", new Serializable[] { guid, cache.getSize() });
			try {
				SwtUtils.blockShell(shell);
				final MomentTensorRetriever operation = new MomentTensorRetriever(earthquake);
				ModalContext.run(operation, true, new NullProgressMonitor(), shell.getDisplay());
				cache.put(guid, operation.getResult());
				return operation.getResult(); // Avoid unpack the first time.
			}
			catch (final InvocationTargetException e) {
				final String message = Messages.get("err.job.mt.show");
				logger.log(Level.WARNING, message, e);
				SwtUtils.unblockShell(shell);
				EnhancedErrorDialog.openError(shell, Messages.get("lbl.window.title"), message, IStatus.WARNING, e.getCause() != null ? e.getCause() : e, shell.getDisplay().getSystemImage(SWT.ICON_WARNING));
			}
			catch (final Exception e) {
				final String message = Messages.get("err.job.mt.show");
				logger.log(Level.SEVERE, message, e);
				SwtUtils.unblockShell(shell);
				EnhancedErrorDialog.openError(shell, Messages.get("lbl.window.title"), message, IStatus.ERROR, e, shell.getDisplay().getSystemImage(SWT.ICON_ERROR));
			}
			finally {
				SwtUtils.unblockShell(shell);
			}
		}
		else {
			logger.log(Level.FINE, "Cache hit for key \"{0}\". Cache size: {1}", new Serializable[] { guid, cache.getSize() });
		}
		return cache.get(guid);
	}

	private MomentTensorRetriever(final Earthquake earthquake) {
		this.earthquake = earthquake;
	}

	@Override
	public void run(final IProgressMonitor monitor) throws InvocationTargetException {
		monitor.beginTask(TASK_NAME, IProgressMonitor.UNKNOWN);
		try {
			final Headers headers = new Headers();
			headers.set("Accept", "text/*");
			headers.set("Accept-Encoding", "gzip");
			final HttpURLConnection connection = ConnectionFactory.makeGetRequest(earthquake.getMomentTensorUrl(), headers);
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
				result = new MomentTensor(out.toString(charsetName), connection.getHeaderField("Etag"));
			}
		}
		catch (final IOException e) {
			throw new InvocationTargetException(e);
		}
		monitor.done();
	}

	private MomentTensor getResult() {
		return result;
	}

}
