package it.albertus.earthquake.gui.job;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;

import it.albertus.earthquake.gui.Images;
import it.albertus.earthquake.gui.ResultsTable;
import it.albertus.earthquake.resources.Messages;
import it.albertus.jface.DisplayThreadExecutor;
import it.albertus.jface.EnhancedErrorDialog;
import it.albertus.util.logging.LoggerFactory;

public class ExportCsvJob extends Job {

	private static final Logger logger = LoggerFactory.getLogger(ExportCsvJob.class);

	private static final char CSV_FIELD_SEPARATOR = ';';
	private static final String[] CSV_FILE_EXTENSIONS = { "*.CSV;*.csv" };

	private static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	private static final DateFormat timeFormat = new SimpleDateFormat("HHmmss");

	private final IShellProvider gui;
	private final ResultsTable resultsTable;

	public ExportCsvJob(final IShellProvider gui, final ResultsTable resultsTable) {
		super("Export CSV");
		this.gui = gui;
		this.resultsTable = resultsTable;
		this.setUser(true);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask("Export CSV", IProgressMonitor.UNKNOWN);

		if (resultsTable != null && resultsTable.getTableViewer() != null && resultsTable.getTableViewer().getTable() != null) {
			new DisplayThreadExecutor(gui.getShell()).execute(new Runnable() {
				@Override
				public void run() {
					gui.getShell().setCursor(gui.getShell().getDisplay().getSystemCursor(SWT.CURSOR_WAIT));
				}
			});

			try (final StringWriter sw = new StringWriter()) {
				final Table table = resultsTable.getTableViewer().getTable();
				final Variables vars = new Variables();
				new DisplayThreadExecutor(table).execute(new Runnable() {
					@Override
					public void run() {
						try (final BufferedWriter bw = new BufferedWriter(sw)) {
							writeCsv(table, bw);
						}
						catch (final Exception e) {
							final String message = Messages.get("err.job.csv.create");
							logger.log(Level.SEVERE, message, e);
							EnhancedErrorDialog.openError(gui.getShell(), Messages.get("lbl.window.title"), message, IStatus.ERROR, e, Images.getMainIcons());
							vars.setException(e);
						}
						finally {
							gui.getShell().setCursor(null);
						}
						if (vars.getException() == null) {
							vars.setFileName(openSaveDialog());
							gui.getShell().setCursor(gui.getShell().getDisplay().getSystemCursor(SWT.CURSOR_WAIT));
						}
					}
				});

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
				new DisplayThreadExecutor(gui.getShell()).execute(new Runnable() {
					@Override
					public void run() {
						EnhancedErrorDialog.openError(gui.getShell(), Messages.get("lbl.window.title"), message, IStatus.WARNING, e, Images.getMainIcons());
					}
				});
			}

			new DisplayThreadExecutor(gui.getShell()).execute(new Runnable() {
				@Override
				public void run() {
					gui.getShell().setCursor(null);
				}
			});
		}

		monitor.done();
		return Status.OK_STATUS;
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

	private String openSaveDialog() {
		final FileDialog saveDialog = new FileDialog(gui.getShell(), SWT.SAVE);
		saveDialog.setFilterExtensions(CSV_FILE_EXTENSIONS);
		final Date sysdate = new Date();
		saveDialog.setFileName(String.format("earthquakebulletin_%s_%s.csv", dateFormat.format(sysdate), timeFormat.format(sysdate)));
		saveDialog.setOverwrite(true);
		return saveDialog.open();
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
