package it.albertus.geofon.client.gui;

import it.albertus.geofon.client.gui.util.ImageDownloader;

import java.io.IOException;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;

public class GeofonClientGui extends ApplicationWindow {

	private static final float SASH_MAGNIFICATION_FACTOR = 1.5f;

	private Image mainIcon;

	private SearchForm searchForm;

	private ResultTable resultTable;

	private MapCanvas mapCanvas;

	private SashForm sashForm;

	private volatile Job job;

	public GeofonClientGui(final Display display) {
		super(null);

		try {
			mainIcon = ImageDownloader.downloadImage("http://www.gfz-potsdam.de/favicon.ico");
		}
		catch (final IOException ioe) {/* Ignore */}

		open();

		//		connect();

		final Shell shell = getShell();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				Display.getCurrent().sleep();
			}
		}

		//		release();
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		// shell.setMinimized(configuration.getBoolean("gui.start.minimized", Defaults.GUI_START_MINIMIZED));
		shell.setText("Geofon Client");
		if (mainIcon != null) {
			shell.setImages(new Image[] { mainIcon });
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

	@Override
	protected Control createContents(final Composite parent) {
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

	public Image getMainIcon() {
		return mainIcon;
	}

	public void setMainIcon(Image mainIcon) {
		this.mainIcon = mainIcon;
	}

	public SearchForm getSearchForm() {
		return searchForm;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public ResultTable getResultTable() {
		return resultTable;
	}

	public MapCanvas getMapCanvas() {
		return mapCanvas;
	}

}
