package it.albertus.eqbulletin.gui;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.listener.CloseListener;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.util.InitializationException;
import it.albertus.jface.EnhancedErrorDialog;
import it.albertus.jface.Events;
import it.albertus.jface.Multilanguage;
import it.albertus.jface.SwtUtils;
import it.albertus.jface.closeable.CloseableDevice;
import it.albertus.jface.preference.IPreferencesConfiguration;
import it.albertus.util.Version;
import it.albertus.util.logging.LoggerFactory;

public class EarthquakeBulletinGui extends ApplicationWindow implements Multilanguage {

	public static final String SHELL_MAXIMIZED = "shell.maximized";
	private static final String SHELL_SASH_WEIGHT = "shell.sash.weight";
	private static final String SHELL_SIZE_X = "shell.size.x";
	private static final String SHELL_SIZE_Y = "shell.size.y";
	private static final String SHELL_LOCATION_X = "shell.location.x";
	private static final String SHELL_LOCATION_Y = "shell.location.y";
	private static final Point POINT_ZERO = new Point(0, 0);

	public static class Defaults {
		public static final boolean START_MINIMIZED = false;
		public static final boolean SEARCH_ON_START = false;
		public static final boolean SHELL_MAXIMIZED = false;
		private static final int[] SASH_WEIGHTS = { 3, 2 };

		private Defaults() {
			throw new IllegalAccessError("Constants class");
		}
	}

	private static final float SASH_MAGNIFICATION_FACTOR = 1.5f;

	private static final Logger logger = LoggerFactory.getLogger(EarthquakeBulletinGui.class);

	private final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getPreferencesConfiguration();

	private final Collection<Multilanguage> multilanguages = new ArrayList<>();

	private TrayIcon trayIcon;
	private MenuBar menuBar;
	private SearchForm searchForm;
	private ResultsTable resultsTable;
	private MapCanvas mapCanvas;
	private StatusBar statusBar;

	private SashForm sashForm;

	/** Shell maximized status. May be null in some circumstances. */
	private Boolean shellMaximized;

	/** Shell size. May be null in some circumstances. */
	private Point shellSize;

	/** Shell location. May be null in some circumstances. */
	private Point shellLocation;

	private EarthquakeBulletinGui() {
		super(null);
		logger.log(Level.CONFIG, "{0}", configuration);
		addStatusLine();
	}

	public static void run(final InitializationException ie) {
		Display.setAppName(Messages.get("msg.application.name"));
		Display.setAppVersion(Version.getNumber());
		try (final CloseableDevice<Display> cd = new CloseableDevice<>(Display.getDefault())) {
			final Display display = cd.getDevice();

			if (ie != null) { // Display error dialog and exit.
				EnhancedErrorDialog.openError(null, Messages.get("lbl.window.title"), ie.getLocalizedMessage() != null ? ie.getLocalizedMessage() : ie.getMessage(), IStatus.ERROR, ie.getCause() != null ? ie.getCause() : ie, Images.getMainIconArray());
			}
			else { // Open main window.
				final EarthquakeBulletinGui gui = new EarthquakeBulletinGui();
				gui.open();
				final Shell shell = gui.getShell();
				try {
					while (!shell.isDisposed()) {
						if (!display.isDisposed() && !display.readAndDispatch()) {
							display.sleep();
						}
					}
				}
				catch (final Exception e) {
					final String message = Messages.get("err.fatal");
					if (shell.isDisposed()) {
						logger.log(Level.FINE, message, e);
					}
					else {
						logger.log(Level.SEVERE, message, e);
						EnhancedErrorDialog.openError(shell, Messages.get("msg.application.name"), message, IStatus.ERROR, e, display.getSystemImage(SWT.ICON_ERROR));
					}
				}
			}
		}
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setImages(Images.getMainIconArray());
		shell.setText(Messages.get("lbl.window.title"));
	}

	@Override
	protected Control createContents(final Composite parent) {
		trayIcon = new TrayIcon(this);
		multilanguages.add(trayIcon);

		menuBar = new MenuBar(this);
		multilanguages.add(menuBar);

		searchForm = new SearchForm(this);
		multilanguages.add(searchForm);

		sashForm = new SashForm(parent, SWT.HORIZONTAL);
		sashForm.setSashWidth(Math.round(sashForm.getSashWidth() * SASH_MAGNIFICATION_FACTOR));
		GridLayoutFactory.swtDefaults().applyTo(sashForm);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(sashForm);

		resultsTable = new ResultsTable(sashForm, GridDataFactory.fillDefaults().grab(true, true).create(), this);
		multilanguages.add(resultsTable);

		mapCanvas = new MapCanvas(sashForm);
		multilanguages.add(mapCanvas);

		sashForm.setWeights(new int[] { configuration.getInt(SHELL_SASH_WEIGHT + ".0", Defaults.SASH_WEIGHTS[0]), configuration.getInt(SHELL_SASH_WEIGHT + ".1", Defaults.SASH_WEIGHTS[1]) });

		statusBar = new StatusBar(this);
		multilanguages.add(statusBar);

		return parent;
	}

	@Override
	public int open() {
		final int code = super.open();
		searchForm.setOpenMapButtonImage();

		final UpdateShellStatusListener listener = new UpdateShellStatusListener();
		getShell().addListener(SWT.Resize, listener);
		getShell().addListener(SWT.Move, listener);
		getShell().addListener(SWT.Activate, new MaximizeShellListener());
		getShell().addListener(SWT.Deactivate, new DeactivateShellListener());

		if (SwtUtils.isGtk3() == null || SwtUtils.isGtk3()) { // fixes invisible (transparent) shell bug with some Linux distibutions
			setMinimizedMaximizedShellStatus();
		}

		for (final Button radio : searchForm.getFormatRadios().values()) {
			if (radio.getSelection()) {
				radio.notifyListeners(SWT.Selection, null);
				break;
			}
		}
		if (configuration.getBoolean(Preference.SEARCH_ON_START, Defaults.SEARCH_ON_START)) {
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

		final Integer sizeX = configuration.getInt(SHELL_SIZE_X);
		final Integer sizeY = configuration.getInt(SHELL_SIZE_Y);
		if (sizeX != null && sizeY != null) {
			shell.setSize(Math.max(sizeX, preferredSize.x), Math.max(sizeY, preferredSize.y));
		}

		final Integer locationX = configuration.getInt(SHELL_LOCATION_X);
		final Integer locationY = configuration.getInt(SHELL_LOCATION_Y);
		if (locationX != null && locationY != null) {
			if (new Rectangle(locationX, locationY, shell.getSize().x, shell.getSize().y).intersects(shell.getDisplay().getBounds())) {
				shell.setLocation(locationX, locationY);
			}
			else {
				logger.log(Level.WARNING, "Illegal shell location ({0,number,#}, {1,number,#}) for size ({2}).", new Serializable[] { locationX, locationY, shell.getSize() });
			}
		}

		if (SwtUtils.isGtk3() != null && !SwtUtils.isGtk3()) { // fixes invisible (transparent) shell bug with some Linux distibutions
			setMinimizedMaximizedShellStatus();
		}
	}

	private void setMinimizedMaximizedShellStatus() {
		if (configuration.getBoolean(Preference.START_MINIMIZED, Defaults.START_MINIMIZED)) {
			getShell().setMinimized(true);
		}
		else if (configuration.getBoolean(SHELL_MAXIMIZED, Defaults.SHELL_MAXIMIZED)) {
			getShell().setMaximized(true);
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

	public void updateTimeZone() {
		resultsTable.getTableViewer().refresh();
		statusBar.refresh();
	}

	@Override
	public void updateLanguage() {
		final Shell shell = getShell();
		shell.setRedraw(false);

		for (final Multilanguage element : multilanguages) {
			element.updateLanguage();
		}

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

	public TrayIcon getTrayIcon() {
		return trayIcon;
	}

	public MenuBar getMenuBar() {
		return menuBar;
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

	public StatusBar getStatusBar() {
		return statusBar;
	}

	@Override
	protected final StatusLineManager getStatusLineManager() { // This method must be visible in this package
		return super.getStatusLineManager();
	}

	@Override
	protected final void createStatusLine(final Shell shell) { // This method must be visible in this package
		super.createStatusLine(shell);
	}

	private class UpdateShellStatusListener implements Listener {
		@Override
		public void handleEvent(final Event event) {
			logEvent(event);
			final Shell shell = getShell();
			if (shell != null && !shell.isDisposed()) {
				shellMaximized = shell.getMaximized();
				if (Boolean.FALSE.equals(shellMaximized) && !POINT_ZERO.equals(shell.getSize())) {
					shellSize = shell.getSize();
					shellLocation = shell.getLocation();
				}
			}
			logger.log(Level.FINE, "shellMaximized: {0} - shellSize: {1} - shellLocation: {2}", new Serializable[] { shellMaximized, shellSize, shellLocation });
		}
	}

	private class MaximizeShellListener implements Listener {
		private boolean firstTime = true;

		@Override
		public void handleEvent(final Event event) {
			logEvent(event);
			if (firstTime && !getShell().isDisposed() && !configuration.getBoolean(Preference.MINIMIZE_TRAY, TrayIcon.Defaults.MINIMIZE_TRAY) && configuration.getBoolean(Preference.START_MINIMIZED, Defaults.START_MINIMIZED) && configuration.getBoolean(SHELL_MAXIMIZED, Defaults.SHELL_MAXIMIZED)) {
				firstTime = false;
				getShell().setMaximized(true);
			}
		}
	}

	private class DeactivateShellListener implements Listener {
		private boolean firstTime = true;

		@Override
		public void handleEvent(final Event event) {
			logEvent(event);
			if (firstTime && configuration.getBoolean(Preference.START_MINIMIZED, Defaults.START_MINIMIZED)) {
				firstTime = false;
			}
			else {
				saveShellStatus();
			}
		}
	}

	private static void logEvent(final Event event) {
		logger.log(Level.FINE, "{0} {1}", new Object[] { Events.getName(event), event });
	}

	public void saveShellStatus() {
		final List<Integer> sashWeights;
		if (sashForm != null && !sashForm.isDisposed()) {
			sashWeights = Arrays.stream(sashForm.getWeights()).boxed().collect(Collectors.toList());
		}
		else {
			sashWeights = Collections.emptyList();
		}
		new Thread(() -> { // don't perform I/O in UI thread
			try {
				configuration.reload(); // make sure the properties are up-to-date
			}
			catch (final IOException e) {
				logger.log(Level.WARNING, e.toString(), e);
				return; // abort
			}
			final Properties properties = configuration.getProperties();

			if (shellMaximized != null) {
				properties.setProperty(SHELL_MAXIMIZED, Boolean.toString(shellMaximized));
			}
			if (shellSize != null) {
				properties.setProperty(SHELL_SIZE_X, Integer.toString(shellSize.x));
				properties.setProperty(SHELL_SIZE_Y, Integer.toString(shellSize.y));
			}
			if (shellLocation != null) {
				properties.setProperty(SHELL_LOCATION_X, Integer.toString(shellLocation.x));
				properties.setProperty(SHELL_LOCATION_Y, Integer.toString(shellLocation.y));
			}

			// Save sash weights
			for (int i = 0; i < sashWeights.size(); i++) {
				properties.setProperty(SHELL_SASH_WEIGHT + '.' + i, Integer.toString(sashWeights.get(i)));
			}

			logger.log(Level.CONFIG, "{0}", configuration);

			try {
				configuration.save(); // save configuration
			}
			catch (final IOException e) {
				logger.log(Level.WARNING, e.toString(), e);
			}
		}, "Save shell status").start();
	}

}
