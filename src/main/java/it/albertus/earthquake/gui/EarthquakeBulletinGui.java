package it.albertus.earthquake.gui;

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
import it.albertus.util.Version;

public class EarthquakeBulletinGui extends ApplicationWindow {

	public interface Defaults {
		boolean START_MINIMIZED = false;
		boolean SEARCH_ON_START = false;
	}

	private static final Configuration configuration = EarthquakeBulletin.configuration;

	private static final float SASH_MAGNIFICATION_FACTOR = 1.5f;
	private static final int[] SASH_WEIGHTS = { 3, 2 };

	public static void run() {
		Display.setAppName(Messages.get("msg.application.name"));
		Display.setAppVersion(Version.getInstance().getNumber());
		final Display display = Display.getDefault();
		final EarthquakeBulletinGui gui = new EarthquakeBulletinGui(display);
		gui.open();

		final Shell shell = gui.getShell();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				Display.getCurrent().sleep();
			}
		}
		display.dispose();
	}

	private SearchForm searchForm;
	private ResultsTable resultsTable;
	private MapCanvas mapCanvas;
	private SashForm sashForm;
	private TrayIcon trayIcon;
	private MenuBar menuBar;

	public EarthquakeBulletinGui(final Display display) {
		super(null);
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
