package it.albertus.earthquake.gui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import it.albertus.earthquake.EarthquakeBulletin;
import it.albertus.earthquake.gui.listener.CloseListener;
import it.albertus.earthquake.resources.Messages;
import it.albertus.earthquake.util.InitializationException;
import it.albertus.jface.EnhancedErrorDialog;
import it.albertus.util.Configuration;
import it.albertus.util.Version;

public class EarthquakeBulletinGui extends ApplicationWindow {

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

	public static void run(final InitializationException e) {
		Display.setAppName(Messages.get("msg.application.name"));
		Display.setAppVersion(Version.getInstance().getNumber());
		final Display display = Display.getDefault();

		if (e != null) { // Display error dialog and exit.
			EnhancedErrorDialog.openError(null, Messages.get("lbl.window.title"), e.getLocalizedMessage() != null ? e.getLocalizedMessage() : e.getMessage(), IStatus.ERROR, e.getCause() != null ? e.getCause() : e, Images.getMainIcons());
		}
		else { // Open main window.
			final EarthquakeBulletinGui gui = new EarthquakeBulletinGui();
			gui.open();
			final Shell shell = gui.getShell();
			while (!shell.isDisposed()) {
				if (!display.isDisposed() && !display.readAndDispatch()) {
					display.sleep();
				}
			}
		}

		display.dispose();
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setImages(Images.getMainIcons());
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
		final int code = super.open();

		// Fix invisible (transparent) shell bug with some Linux distibutions
		if (configuration.getBoolean("start.minimized", Defaults.START_MINIMIZED) && Util.isGtk()) {
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
		final Point preferredSize = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		shell.setMinimumSize(preferredSize);
		shell.setSize(preferredSize.x, shell.getSize().y);

		// Fix invisible (transparent) shell bug with some Linux distibutions
		if (configuration.getBoolean("start.minimized", Defaults.START_MINIMIZED) && !Util.isGtk()) {
			shell.setMinimized(true);
		}
	}

	@Override
	protected void initializeBounds() {/* Do not pack the shell */}

	@Override
	protected void createTrimWidgets(final Shell shell) {/* Not needed */}

	@Override
	protected Layout getLayout() {
		return GridLayoutFactory.swtDefaults().create();
	}

	public void updateLanguage() {
		final Shell shell = getShell();
		shell.setRedraw(false);
		menuBar.updateTexts();
		resultsTable.updateTexts();
		searchForm.updateTexts();
		mapCanvas.updateTexts();

		final TableColumn[] columns = resultsTable.getTableViewer().getTable().getColumns();
		final int[] widths = new int[columns.length];
		for (int i = 0; i < columns.length; i++) {
			widths[i] = columns[i].getWidth();
			columns[i].setWidth(1);
		}

		shell.layout(true, true);
		shell.setMinimumSize(shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x, shell.getMinimumSize().y);

		for (int i = 0; i < columns.length; i++) {
			columns[i].setWidth(widths[i]);
		}

		shell.setRedraw(true);
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
