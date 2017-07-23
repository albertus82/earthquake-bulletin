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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import it.albertus.earthquake.gui.EarthquakeBulletinGui;
import it.albertus.earthquake.gui.Images;
import it.albertus.earthquake.resources.Messages;
import it.albertus.jface.DisplayThreadExecutor;
import it.albertus.jface.EnhancedErrorDialog;
import it.albertus.util.logging.LoggerFactory;

public class ExportCsvJob extends Job {

	private static final Logger logger = LoggerFactory.getLogger(ExportCsvJob.class);

	private static final char CSV_FIELD_SEPARATOR = ';';

	private static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

	private final EarthquakeBulletinGui gui;

	public ExportCsvJob(final EarthquakeBulletinGui gui) {
		super("Export CSV");
		this.gui = gui;
		this.setUser(true);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask("Image download", 1);

		if (gui.getResultsTable() != null && gui.getResultsTable().getTableViewer() != null && gui.getResultsTable().getTableViewer().getTable() != null) {
			new DisplayThreadExecutor(gui.getShell()).execute(new Runnable() {
				@Override
				public void run() {
					gui.getShell().setCursor(gui.getShell().getDisplay().getSystemCursor(SWT.CURSOR_WAIT));
				}
			});

			try (final StringWriter sw = new StringWriter()) {
				final String[] fileName = new String[1];
				new DisplayThreadExecutor(gui.getResultsTable().getTableViewer().getTable()).execute(new Runnable() {
					@Override
					public void run() {
						try (final BufferedWriter bw = new BufferedWriter(sw)) {
							// Head
							for (int i = 0; i < gui.getResultsTable().getTableViewer().getTable().getColumnCount(); i++) {
								if (i != 0) {
									bw.append(CSV_FIELD_SEPARATOR);
								}
								bw.write(gui.getResultsTable().getTableViewer().getTable().getColumn(i).getText());
							}
							bw.newLine();
							// Body
							for (int i = 0; i < gui.getResultsTable().getTableViewer().getTable().getItemCount(); i++) {
								for (int j = 0; j < gui.getResultsTable().getTableViewer().getTable().getColumnCount(); j++) {
									if (j != 0) {
										bw.append(CSV_FIELD_SEPARATOR);
									}
									bw.write(gui.getResultsTable().getTableViewer().getTable().getItem(i).getText(j));
								}
								bw.newLine();
							}
						}
						catch (final IOException e) {
							logger.log(Level.SEVERE, e.toString(), e);
							throw new IllegalStateException(e);
						}
						finally {
							gui.getShell().setCursor(null);
						}
						final FileDialog saveDialog = new FileDialog(gui.getShell(), SWT.SAVE);
						saveDialog.setFilterExtensions(new String[] { "*.CSV;*.csv" });
						saveDialog.setFileName("earthquakes_" + dateFormat.format(new Date()) + ".csv");
						saveDialog.setOverwrite(true);
						fileName[0] = saveDialog.open();
						gui.getShell().setCursor(gui.getShell().getDisplay().getSystemCursor(SWT.CURSOR_WAIT));
					}
				});

				// Salvare su disco
				if (fileName[0] != null) {
					try (final FileWriter fw = new FileWriter(fileName[0]); final BufferedWriter bw = new BufferedWriter(fw)) {
						bw.write(sw.toString());
					}
				}
			}
			catch (final Exception e) {
				final String message = Messages.get("err.job.csv");
				logger.log(Level.WARNING, message, e);
				new DisplayThreadExecutor(gui.getShell()).execute(new Runnable() {
					@Override
					public void run() {
						EnhancedErrorDialog.openError(gui.getShell(), Messages.get("lbl.window.title"), message, IStatus.ERROR, e, Images.getMainIcons());
					}
				});
			}

			new DisplayThreadExecutor(gui.getMapCanvas().getCanvas()).execute(new Runnable() {
				@Override
				public void run() {
					gui.getShell().setCursor(null);
				}
			});
		}

		monitor.done();
		return Status.OK_STATUS;
	}

}
