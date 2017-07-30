package it.albertus.earthquake.gui.job;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import it.albertus.earthquake.gui.Images;
import it.albertus.earthquake.gui.ResultsTable;
import it.albertus.earthquake.resources.Messages;
import it.albertus.jface.DisplayThreadExecutor;
import it.albertus.jface.EnhancedErrorDialog;
import it.albertus.util.logging.LoggerFactory;

public class ExportCsvJob extends Job {

	private static final Logger logger = LoggerFactory.getLogger(ExportCsvJob.class);

	private final ResultsTable resultsTable;

	public ExportCsvJob(final ResultsTable resultsTable) {
		super("Export CSV");
		if (resultsTable == null) {
			throw new IllegalArgumentException(String.valueOf(resultsTable));
		}
		this.resultsTable = resultsTable;
		this.setUser(true);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask("Export CSV", IProgressMonitor.UNKNOWN);

		if (resultsTable.getTableViewer() != null && resultsTable.getTableViewer().getTable() != null) {
			final Table table = resultsTable.getTableViewer().getTable();

			new DisplayThreadExecutor(table).execute(new Runnable() {
				@Override
				public void run() {
					final Shell shell = table.getShell();
					shell.setCursor(shell.getDisplay().getSystemCursor(SWT.CURSOR_WAIT));
				}
			});

			try (final StringWriter sw = new StringWriter()) {
				final Variables vars = new Variables();
				new DisplayThreadExecutor(table).execute(new CsvBuilderJob(table, sw, vars));

				if (vars.getException() != null) {
					return Status.CANCEL_STATUS;
				}

				// Save file (outside the UI thread)
				if (vars.getFileName() != null) {
					try (final FileWriter fw = new FileWriter(vars.getFileName()); final BufferedWriter bw = new BufferedWriter(fw)) {
						bw.write(sw.toString());
					}
				}
			}
			catch (final Exception e) {
				final String message = Messages.get("err.job.csv.save");
				logger.log(Level.WARNING, message, e);
				new DisplayThreadExecutor(table).execute(new Runnable() {
					@Override
					public void run() {
						EnhancedErrorDialog.openError(table.getShell(), Messages.get("lbl.window.title"), message, IStatus.WARNING, e, Images.getMainIcons());
					}
				});
			}

			new DisplayThreadExecutor(table).execute(new Runnable() {
				@Override
				public void run() {
					table.getShell().setCursor(null);
				}
			});
		}

		monitor.done();
		return Status.OK_STATUS;
	}

	private static class CsvBuilderJob implements Runnable {

		private static final char CSV_FIELD_SEPARATOR = ';';
		private static final String[] CSV_FILE_EXTENSIONS = { "*.CSV;*.csv" };

		private static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		private static final DateFormat timeFormat = new SimpleDateFormat("HHmmss");

		private final Table table;
		private final Writer writer;
		private final Variables vars;

		public CsvBuilderJob(final Table source, final Writer destination, final Variables vars) {
			this.table = source;
			this.writer = destination;
			this.vars = vars;
		}

		@Override
		public void run() {
			final Shell shell = table.getShell();
			try (final BufferedWriter bw = new BufferedWriter(writer)) {
				writeCsv(table, bw);
			}
			catch (final Exception e) {
				final String message = Messages.get("err.job.csv.create");
				logger.log(Level.SEVERE, message, e);
				EnhancedErrorDialog.openError(shell, Messages.get("lbl.window.title"), message, IStatus.ERROR, e, Images.getMainIcons());
				vars.setException(e);
			}
			finally {
				shell.setCursor(null);
			}
			if (vars.getException() == null) {
				vars.setFileName(openSaveDialog(shell));
				shell.setCursor(shell.getDisplay().getSystemCursor(SWT.CURSOR_WAIT));
			}
		}

		private void writeCsv(final Table table, final BufferedWriter writer) throws IOException {
			for (int i = 0; i < table.getColumnCount(); i++) { // Head
				if (i != 0) {
					writer.append(CSV_FIELD_SEPARATOR);
				}
				writer.write(table.getColumn(i).getText());
			}
			writer.newLine();
			for (int i = 0; i < table.getItemCount(); i++) { // Body
				for (int j = 0; j < table.getColumnCount(); j++) {
					if (j != 0) {
						writer.append(CSV_FIELD_SEPARATOR);
					}
					writer.write(table.getItem(i).getText(j));
				}
				writer.newLine();
			}
		}

		private String openSaveDialog(final Shell shell) {
			final FileDialog saveDialog = new FileDialog(shell, SWT.SAVE);
			saveDialog.setFilterExtensions(CSV_FILE_EXTENSIONS);
			final Date sysdate = new Date();
			saveDialog.setFileName(String.format("earthquakebulletin_%s_%s.csv", dateFormat.format(sysdate), timeFormat.format(sysdate)));
			saveDialog.setOverwrite(true);
			return saveDialog.open();
		}
	}

	private class Variables {
		private String fileName;
		private Exception exception;

		private String getFileName() {
			return fileName;
		}

		private void setFileName(final String fileName) {
			this.fileName = fileName;
		}

		private Exception getException() {
			return exception;
		}

		private void setException(final Exception exception) {
			this.exception = exception;
		}
	}

}
