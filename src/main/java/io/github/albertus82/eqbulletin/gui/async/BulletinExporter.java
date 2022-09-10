package io.github.albertus82.eqbulletin.gui.async;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import io.github.albertus82.eqbulletin.gui.EarthquakeBulletinGui;
import io.github.albertus82.eqbulletin.gui.Images;
import io.github.albertus82.eqbulletin.resources.Messages;
import io.github.albertus82.jface.EnhancedErrorDialog;
import io.github.albertus82.jface.SwtUtils;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BulletinExporter implements IRunnableWithProgress {

	private static final String TASK_NAME = "Exporting earthquake bulletin";

	private static final char CSV_FIELD_SEPARATOR = ';';
	private static final String[] CSV_FILE_EXTENSIONS = { "*.CSV;*.csv" };

	private static final byte[] UTF8_BOM = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };

	@NonNull
	private final String fileName;
	@NonNull
	private final String data;

	@Override
	public void run(final IProgressMonitor monitor) throws InvocationTargetException {
		monitor.beginTask(TASK_NAME, IProgressMonitor.UNKNOWN);

		try {
			final Path path = Paths.get(fileName);
			Files.write(path, UTF8_BOM);
			try (final OutputStream fos = Files.newOutputStream(Paths.get(fileName), StandardOpenOption.APPEND); final OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8); final BufferedWriter bw = new BufferedWriter(osw)) {
				bw.write(data);
			}
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
				log.warn("Unable to save CSV file:", e);
				SwtUtils.unblockShell(shell);
				EnhancedErrorDialog.openError(shell, EarthquakeBulletinGui.getApplicationName(), Messages.get("error.job.csv.save"), IStatus.WARNING, e.getCause() != null ? e.getCause() : e, Images.getAppIconArray());
			}
			catch (final Exception e) { // NOSONAR Either re-interrupt this method or rethrow the "InterruptedException" that can be caught here. "InterruptedException" should not be ignored (java:S2142)
				log.error("Cannot export the earthquake bulletin as CSV file:", e);
				SwtUtils.unblockShell(shell);
				EnhancedErrorDialog.openError(shell, EarthquakeBulletinGui.getApplicationName(), Messages.get("error.job.csv.create"), IStatus.ERROR, e, Images.getAppIconArray());
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
		saveDialog.setFileName("earthquakebulletin_" + DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now()) + ".csv");
		saveDialog.setOverwrite(true);
		return saveDialog.open();
	}

}
