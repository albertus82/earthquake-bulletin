package it.albertus.earthquake.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;

import it.albertus.earthquake.EarthquakeBulletin;
import it.albertus.earthquake.gui.listener.CloseListener;
import it.albertus.earthquake.resources.Messages;
import it.albertus.util.Configuration;
import it.albertus.util.ExceptionUtils;
import it.albertus.util.IOUtils;
import it.albertus.util.Version;
import it.albertus.util.logging.LoggerFactory;

public class EarthquakeBulletinGui extends ApplicationWindow {

	private static final Logger logger = LoggerFactory.getLogger(EarthquakeBulletinGui.class);

	public static class Defaults {
		public static final boolean START_MINIMIZED = false;
		public static final boolean SEARCH_ON_START = false;

		private Defaults() {
			throw new IllegalAccessError("Constants class");
		}
	}

	private static final Configuration configuration = EarthquakeBulletin.getConfiguration();

	private static final float SASH_MAGNIFICATION_FACTOR = 1.5f;
	private static final int[] SASH_WEIGHTS = { 3, 2 };

	private SearchForm searchForm;
	private ResultsTable resultsTable;
	private MapCanvas mapCanvas;
	private SashForm sashForm;
	private TrayIcon trayIcon;
	private MenuBar menuBar;

	public EarthquakeBulletinGui() {
		super(null);
	}

	public static void run(final Exception initializationException) {
		Display.setAppName(Messages.get("msg.application.name"));
		Display.setAppVersion(Version.getInstance().getNumber());
		final Display display = Display.getDefault();
		final Shell shell;

		if (initializationException != null) { // Display error dialog and exit.
			shell = new Shell(display);
			final MultiStatus status = createMultiStatus(IStatus.ERROR, initializationException.getCause() != null ? initializationException.getCause() : initializationException);
			ErrorDialog.openError(shell, Messages.get("lbl.window.title"), initializationException.getLocalizedMessage() != null ? initializationException.getLocalizedMessage() : initializationException.getMessage(), status);
			shell.dispose();
		}
		else { // Open main window.
			final EarthquakeBulletinGui gui = new EarthquakeBulletinGui();
			gui.open();
			shell = gui.getShell();
		}

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				Display.getCurrent().sleep();
			}
		}
		display.dispose();
	}

	private static MultiStatus createMultiStatus(final int severity, final Throwable throwable) {
		final List<IStatus> childStatuses = new ArrayList<>();

		StringReader sr = null;
		BufferedReader br = null;
		try {
			sr = new StringReader(ExceptionUtils.getStackTrace(throwable));
			br = new BufferedReader(sr);
			String line;
			while ((line = br.readLine()) != null) {
				final IStatus status = new Status(severity, throwable.getClass().getName(), line);
				childStatuses.add(status);
			}
		}
		catch (final IOException ioe) {
			logger.log(Level.WARNING, ioe.getLocalizedMessage() != null ? ioe.getLocalizedMessage() : ioe.getMessage(), ioe);
		}
		finally {
			IOUtils.closeQuietly(br, sr);
		}

		return new MultiStatus(throwable.getClass().getName(), IStatus.ERROR, childStatuses.toArray(new IStatus[] {}), throwable.toString(), throwable);
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setImages(Images.getMainIcons());

		// Fix invisible (transparent) shell bug with some Linux distibutions
		if (!Util.isGtk() && configuration.getBoolean("start.minimized", Defaults.START_MINIMIZED)) {
			shell.setMinimized(true);
		}

		shell.setText(Messages.get("lbl.window.title"));
	}

	@Override
	protected Control createContents(final Composite parent) {
		trayIcon = new TrayIcon(this);

		menuBar = new MenuBar(this);

		searchForm = new SearchForm(this);

		sashForm = new SashForm(parent, SWT.HORIZONTAL);
		sashForm.setSashWidth((int) (sashForm.getSashWidth() * SASH_MAGNIFICATION_FACTOR));
		GridLayoutFactory.swtDefaults().applyTo(sashForm);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(sashForm);

		resultsTable = new ResultsTable(sashForm, GridDataFactory.fillDefaults().grab(true, true).create(), this);

		mapCanvas = new MapCanvas(sashForm);
		sashForm.setWeights(SASH_WEIGHTS);

		return parent;
	}

	@Override
	public int open() {
		int code = super.open();

		// Fix invisible (transparent) shell bug with some Linux distibutions
		if (Util.isGtk() && configuration.getBoolean("start.minimized", Defaults.START_MINIMIZED)) {
			getShell().setMinimized(true);
		}

		for (final Button radio : searchForm.getFormatRadios().values()) {
			if (radio.getSelection()) {
				radio.notifyListeners(SWT.Selection, null);
				break;
			}
		}
		if (configuration.getBoolean("search.on.start", Defaults.SEARCH_ON_START)) {
			searchForm.getSearchButton().notifyListeners(SWT.Selection, null);
		}
		return code;
	}

	@Override
	protected void handleShellCloseEvent() {
		final Event event = new Event();
		new CloseListener(this).handleEvent(event);
		if (event.doit) {
			super.handleShellCloseEvent();
		}
	}

	@Override
	protected void constrainShellSize() {
		super.constrainShellSize();
		final Shell shell = getShell();
		shell.setMinimumSize(shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true));
	}

	@Override
	protected void initializeBounds() {/* Do not pack the shell */}

	@Override
	protected void createTrimWidgets(final Shell shell) {/* Not needed */}

	@Override
	protected Layout getLayout() {
		return GridLayoutFactory.swtDefaults().create();
	}

	public SearchForm getSearchForm() {
		return searchForm;
	}

	public ResultsTable getResultsTable() {
		return resultsTable;
	}

	public MapCanvas getMapCanvas() {
		return mapCanvas;
	}

	public TrayIcon getTrayIcon() {
		return trayIcon;
	}

	public SashForm getSashForm() {
		return sashForm;
	}

	public MenuBar getMenuBar() {
		return menuBar;
	}

}
