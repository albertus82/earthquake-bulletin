package it.albertus.geofon.client.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;

public class GeofonClientGui extends ApplicationWindow {

	private Image mainIcon;
	private SearchForm searchForm;

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
			HttpURLConnection urlConnection = openConnection(faviconUrl, 5000, 5000);
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
		return new GridLayout();
	}

	@Override
	protected Control createContents(final Composite parent) {
		parent.setLayout(new GridLayout(7, false));

		searchForm = new SearchForm(parent);

		return parent;
	}

	private HttpURLConnection openConnection(final URL url, final int connectionTimeout, final int readTimeout) throws IOException {
		final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setConnectTimeout(connectionTimeout);
		urlConnection.setReadTimeout(readTimeout);
		urlConnection.addRequestProperty("Accept", "text/xml");
		return urlConnection;
	}

}
