package it.albertus.eqbulletin.gui.async;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import it.albertus.eqbulletin.gui.Images;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.EnhancedErrorDialog;
import it.albertus.jface.SwtUtils;
import it.albertus.util.logging.LoggerFactory;

public class BulletinExporter implements IRunnableWithProgress {

	private static final String TASK_NAME = "Exporting earthquake bulletin";

	private static final char CSV_FIELD_SEPARATOR = ';';
	private static final String[] CSV_FILE_EXTENSIONS = { "*.CSV;*.csv" };

	private static final Logger logger = LoggerFactory.getLogger(BulletinExporter.class);

	private final String fileName;
	private final String data;

	private BulletinExporter(final String fileName, final String data) {
		this.fileName = fileName;
		this.data = data;
	}

	@Override
	public void run(final IProgressMonitor monitor) throws InvocationTargetException {
		monitor.beginTask(TASK_NAME, IProgressMonitor.UNKNOWN);

		try (final FileWriter fw = new FileWriter(fileName); final BufferedWriter bw = new BufferedWriter(fw)) {
			bw.write(data);
		}
		catch (final IOException e) {
			throw new InvocationTargetException(e);
		}

		monitor.done();
	}

	public static void export(final Table table) {
		final Shell shell = table.getShell();
		final String fileName = openSaveDialog(shell);
		if (fileName != null && !fileName.trim().isEmpty()) {
			try (final StringWriter sw = new StringWriter()) {
				SwtUtils.blockShell(shell);
				try (final BufferedWriter bw = new BufferedWriter(sw)) {
					writeCsv(table, bw);
				}
				final BulletinExporter exporter = new BulletinExporter(fileName, sw.toString());
				ModalContext.run(exporter, true, new NullProgressMonitor(), shell.getDisplay());
			}
			catch (final InvocationTargetException e) {
				final String message = Messages.get("err.job.csv.save");
				logger.log(Level.WARNING, message, e);
				SwtUtils.unblockShell(shell);
				EnhancedErrorDialog.openError(shell, Messages.get("lbl.window.title"), message, IStatus.WARNING, e.getCause() != null ? e.getCause() : e, Images.getMainIconArray());
			}
			catch (final Exception e) {
				final String message = Messages.get("err.job.csv.create");
				logger.log(Level.SEVERE, message, e);
				SwtUtils.unblockShell(shell);
				EnhancedErrorDialog.openError(shell, Messages.get("lbl.window.title"), message, IStatus.ERROR, e, Images.getMainIconArray());
			}
			finally {
				SwtUtils.unblockShell(shell);
			}
		}
	}

	private static void writeCsv(final Table table, final BufferedWriter writer) throws IOException {
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

	private static String openSaveDialog(final Shell shell) {
		final FileDialog saveDialog = new FileDialog(shell, SWT.SAVE);
		saveDialog.setFilterExtensions(CSV_FILE_EXTENSIONS);
		saveDialog.setFileName("earthquakebulletin_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv");
		saveDialog.setOverwrite(true);
		return saveDialog.open();
	}

}
