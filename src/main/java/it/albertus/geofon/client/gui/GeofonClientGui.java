package it.albertus.geofon.client.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.FillLayout;
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
	
	private MapCanvas mapCanvas;

	private SashForm verticalSashForm;

	private SashForm horizontalSashForm;

	private Composite leftComposite;

	private Composite rightComposite;

	public GeofonClientGui(final Display display) {
		super(null);

		try {
			mainIcon = downloadImage(new URL("http://www.gfz-potsdam.de/favicon.ico"));
		}
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

	public Image downloadImage(final URL url) {
		InputStream is = null;
		Image image = null;
		try {
			final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(5000);
			urlConnection.setReadTimeout(5000);
			urlConnection.addRequestProperty("Accept", "image/*");
			is = urlConnection.getInputStream();
			final ImageData[] images = new ImageLoader().load(is);
			urlConnection.disconnect();

			if (images.length > 0) {
				image = new Image(Display.getCurrent(), images[0]);
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
		return image;
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

		verticalSashForm = new SashForm(parent, SWT.VERTICAL);
		verticalSashForm.setSashWidth((int) (verticalSashForm.getSashWidth() * SASH_MAGNIFICATION_FACTOR));
		verticalSashForm.setLayout(new GridLayout());
		verticalSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 7, 1));

		resultTable = new ResultTable(verticalSashForm, new GridData(SWT.FILL, SWT.FILL, true, true), this);

		horizontalSashForm = new SashForm(verticalSashForm, SWT.HORIZONTAL);
		horizontalSashForm.setSashWidth((int) (horizontalSashForm.getSashWidth() * SASH_MAGNIFICATION_FACTOR));
		horizontalSashForm.setLayout(new GridLayout());
		horizontalSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		leftComposite = new Composite(horizontalSashForm, SWT.BORDER);
		leftComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		leftComposite.setLayout(new FillLayout());

		rightComposite = new Composite(horizontalSashForm, SWT.BORDER);
		rightComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		rightComposite.setLayout(new FillLayout());

		mapCanvas = new MapCanvas(leftComposite);

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
