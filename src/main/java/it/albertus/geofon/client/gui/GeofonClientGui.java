package it.albertus.geofon.client.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
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

	private volatile Job job;

	private ResultTable resultTable;

	private SashForm sashForm;

	public GeofonClientGui(final Display display) {
		super(null);

		downloadIcon();

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

	protected void downloadIcon() {
		InputStream is = null;
		try {
			URL faviconUrl = new URL("http://www.gfz-potsdam.de/favicon.ico");
			final HttpURLConnection urlConnection = (HttpURLConnection) faviconUrl.openConnection();
			urlConnection.setConnectTimeout(5000);
			urlConnection.setReadTimeout(5000);
			urlConnection.addRequestProperty("Accept", "image/*");
			is = urlConnection.getInputStream();
			final ImageData[] images = new ImageLoader().load(is);
			urlConnection.disconnect();

			if (images.length > 0) {
				mainIcon = new Image(Display.getCurrent(), images[0]);
			}
			urlConnection.disconnect();
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				is.close();
			}
			catch (final Exception e) {/* Ignore */}
		}
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		//		shell.setMinimized(configuration.getBoolean("gui.start.minimized", Defaults.GUI_START_MINIMIZED));
		shell.setText("Geofon Client");
		if (mainIcon != null) {
			shell.setImages(new Image[] { mainIcon });
		}
	}

	@Override
	protected void createTrimWidgets(final Shell shell) {/* Not needed */}

	@Override
	protected Layout getLayout() {
		return new GridLayout(7, false);
	}

	@Override
	protected Control createContents(final Composite parent) {
		searchForm = new SearchForm(this);

		sashForm = new SashForm(parent, SWT.VERTICAL);
		sashForm.setSashWidth((int) (sashForm.getSashWidth() * SASH_MAGNIFICATION_FACTOR));
		sashForm.setLayout(new GridLayout());
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 7, 1));

		resultTable = new ResultTable(sashForm, new GridData(SWT.FILL, SWT.FILL, true, true), this);
		
		Composite lowerPane = new Composite(sashForm, SWT.NULL);
		lowerPane.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

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

}
