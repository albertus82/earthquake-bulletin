package it.albertus.eqbulletin.gui.listener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.sun.net.httpserver.Headers;

import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.MomentTensorDialog;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.service.net.ConnectionFactory;
import it.albertus.jface.EnhancedErrorDialog;
import it.albertus.jface.SwtUtils;
import it.albertus.util.IOUtils;
import it.albertus.util.StringUtils;
import it.albertus.util.logging.LoggerFactory;

public class ShowMomentTensorListener implements Listener {

	private static final Logger logger = LoggerFactory.getLogger(ShowMomentTensorListener.class);

	private final EarthquakeBulletinGui gui;

	public ShowMomentTensorListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void handleEvent(final Event event) {
		final TableViewer tableViewer = gui.getResultsTable().getTableViewer();
		if (tableViewer != null && !tableViewer.getTable().isDisposed() && tableViewer.getStructuredSelection() != null) {
			final Earthquake selectedItem = (Earthquake) tableViewer.getStructuredSelection().getFirstElement();
			final Shell shell = gui.getShell();
			if (selectedItem != null && shell != null && !shell.isDisposed()) {
				final String momentTensorSolution = fetchMomentTensorSolution(shell, selectedItem);
				if (momentTensorSolution != null) {
					final MomentTensorDialog dialog = new MomentTensorDialog(shell, momentTensorSolution);
					dialog.open();
				}
			}
		}
	}

	private String fetchMomentTensorSolution(final Shell shell, final Earthquake earthquake) {
		final String[] s = new String[1];
		try {
			SwtUtils.blockShell(shell);
			ModalContext.run(new IRunnableWithProgress() {
				private static final String TASK_NAME = "Fetching moment tensor solution";

				@Override
				public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask(TASK_NAME, IProgressMonitor.UNKNOWN);
					try {
						s[0] = fetchMomentTensorSolution(earthquake);
					}
					catch (final IOException e) {
						throw new InvocationTargetException(e);
					}
					monitor.done();
				}
			}, true, new NullProgressMonitor(), shell.getDisplay());
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
		return s[0];
	}

	private String fetchMomentTensorSolution(final Earthquake earthquake) throws IOException {
		final Headers headers = new Headers();
		headers.set("Accept", "text/*");
		headers.set("Accept-Encoding", "gzip");
		final HttpURLConnection urlConnection = ConnectionFactory.makeGetRequest(earthquake.getMomentTensor(), headers);
		final String responseContentEncoding = urlConnection.getContentEncoding();
		final boolean gzip = responseContentEncoding != null && responseContentEncoding.toLowerCase().contains("gzip");
		try (final InputStream internalInputStream = urlConnection.getInputStream(); final InputStream inputStream = gzip ? new GZIPInputStream(internalInputStream) : internalInputStream; final ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
			IOUtils.copy(inputStream, buffer, 2048);
			String charsetName = StandardCharsets.US_ASCII.name();
			String contentType = urlConnection.getContentType();
			if (contentType != null) {
				contentType = contentType.toLowerCase();
				if (contentType.contains("charset=")) {
					charsetName = StringUtils.substringAfter(contentType, "charset=").trim();
				}
			}
			return buffer.toString(charsetName);
		}
	}

}
