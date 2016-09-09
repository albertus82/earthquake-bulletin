package it.albertus.geofon.client.gui;

import it.albertus.geofon.client.GeofonClient;
import it.albertus.geofon.client.gui.listener.CloseListener;
import it.albertus.geofon.client.gui.util.ImageDownloader;
import it.albertus.geofon.client.resources.Messages;
import it.albertus.util.Configuration;

import java.io.IOException;
import java.util.Locale;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;

public class GeofonClientGui extends ApplicationWindow {

	public interface Defaults {
		boolean START_MINIMIZED = false;
		String LANGUAGE = Locale.getDefault().getLanguage();
	}

	private static final float SASH_MAGNIFICATION_FACTOR = 1.5f;

	public static void run() {
		final Display display = Display.getDefault();
		final GeofonClientGui gui = new GeofonClientGui(display);
		gui.open();

		//		if (true) {
		//			gui.getSearchForm().getSearchButton().notifyListeners(SWT.Selection, null);
		//		}

		final Shell shell = gui.getShell();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				Display.getCurrent().sleep();
			}
		}
		display.dispose();
	}

	private Configuration configuration = GeofonClient.configuration;

	private Image favicon;
	private SearchForm searchForm;
	private ResultTable resultTable;
	private MapCanvas mapCanvas;
	private SashForm sashForm;
	private TrayIcon trayIcon;
	private MenuBar menuBar;

	public GeofonClientGui(final Display display) {
		super(null);
		try {
			favicon = ImageDownloader.downloadImage("http://www.gfz-potsdam.de/favicon.ico");
		}
		catch (final IOException ioe) {/* Ignore */}
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setMinimized(configuration.getBoolean("start.minimized", Defaults.START_MINIMIZED));
		shell.setText(Messages.get("lbl.window.title"));
		if (favicon != null) {
			shell.setImages(new Image[] { favicon });
		}
	}

	@Override
	protected Control createContents(final Composite parent) {
		trayIcon = new TrayIcon(this);

		menuBar = new MenuBar(this);

		searchForm = new SearchForm(this);

		sashForm = new SashForm(parent, SWT.HORIZONTAL);
		sashForm.setSashWidth((int) (sashForm.getSashWidth() * SASH_MAGNIFICATION_FACTOR));
		sashForm.setLayout(new GridLayout());
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		resultTable = new ResultTable(sashForm, new GridData(SWT.FILL, SWT.FILL, true, true), this);

		mapCanvas = new MapCanvas(sashForm);
		sashForm.setWeights(new int[] { 3, 2 });

		return parent;
	}

	@Override
	public int open() {
		int code = super.open();
		for (final Button radio : getSearchForm().getFormatRadios().values()) {
			if (radio.getSelection()) {
				radio.notifyListeners(SWT.Selection, null);
				break;
			}
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
	protected void initializeBounds() {/* Do not pack the shell */}

	@Override
	protected void createTrimWidgets(final Shell shell) {/* Not needed */}

	@Override
	protected Layout getLayout() {
		return new GridLayout();
	}

	public SearchForm getSearchForm() {
		return searchForm;
	}

	public ResultTable getResultTable() {
		return resultTable;
	}

	public MapCanvas getMapCanvas() {
		return mapCanvas;
	}

	public Image getFavicon() {
		return favicon;
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
