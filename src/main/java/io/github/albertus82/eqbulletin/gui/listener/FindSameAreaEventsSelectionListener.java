package io.github.albertus82.eqbulletin.gui.listener;

import java.util.function.Supplier;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import io.github.albertus82.eqbulletin.config.EarthquakeBulletinConfig;
import io.github.albertus82.eqbulletin.gui.ResultsTable;
import io.github.albertus82.eqbulletin.gui.SearchForm;
import io.github.albertus82.eqbulletin.gui.preference.Preference;
import io.github.albertus82.eqbulletin.model.Earthquake;
import io.github.albertus82.eqbulletin.resources.Messages;
import io.github.albertus82.jface.Formatter;
import io.github.albertus82.jface.JFaceMessages;
import io.github.albertus82.jface.listener.IntegerVerifyListener;
import io.github.albertus82.jface.maps.MapBounds;
import io.github.albertus82.jface.preference.IPreferencesConfiguration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FindSameAreaEventsSelectionListener extends SelectionAdapter {

	public static final byte LATITUDE_INTERVAL_MIN = 1;
	public static final byte LATITUDE_INTERVAL_MAX = 5;

	private static final double AUTHALIC_RADIUS = 6371.0072;

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Defaults {
		public static final byte SAME_AREA_EVENTS_LATITUDE_INTERVAL = 1;
	}

	private final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getPreferencesConfiguration();

	@NonNull
	private final Supplier<ResultsTable> resultsTableSupplier;

	@NonNull
	private final Supplier<SearchForm> searchFormSupplier;

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final TableViewer tableViewer = resultsTableSupplier.get().getTableViewer();
		final Earthquake selection = (Earthquake) tableViewer.getStructuredSelection().getFirstElement();
		final Table table = tableViewer.getTable();
		if (selection != null && table != null && !table.isDisposed()) {
			final SearchForm form = searchFormSupplier.get();

			// Ask user for latitude interval
			final ScaleInputDialog dialog = new ScaleInputDialog(table.getShell(), Messages.get("label.sameareaevents.title"), Messages.get("label.sameareaevents.message"), configuration.getByte(Preference.SAME_AREA_EVENTS_LATITUDE_INTERVAL, Defaults.SAME_AREA_EVENTS_LATITUDE_INTERVAL), LATITUDE_INTERVAL_MIN, LATITUDE_INTERVAL_MAX, 1, 1);
			if (Window.OK != dialog.open()) {
				return;
			}
			final float offset = dialog.getValue();

			// Latitude (parallels)
			final float lat = Math.min(MapBounds.LATITUDE_MAX_VALUE - offset, Math.max(MapBounds.LATITUDE_MIN_VALUE + offset, selection.getLatitude().getValue()));
			form.setLatitudeFrom(lat - offset);
			form.setLatitudeTo(lat + offset);

			// Longitude (meridians)
			final float[] lons = computeLons(lat, selection.getLongitude().getValue(), offset);
			form.setLongitudeFrom(lons[0]);
			form.setLongitudeTo(lons[1]);

			form.getSearchButton().notifyListeners(SWT.Selection, null);
		}
	}

	private static float[] computeLons(final float lat, final float lon, final float offset) {
		final float lat0 = lat - offset;
		final float lat1 = lat + offset;
		float lon0 = lon;
		float lon1 = lon;
		final double targetArea = computeArea(-offset, offset, -offset, offset);
		log.debug("lat={}, lon={}, offset={}, targetArea={}", lat, lon, offset, targetArea);
		double actualArea = 0;
		final float step = 0.01f;
		for (int i = 0; actualArea < targetArea; i++) {
			lon0 -= step;
			lon1 += step;
			actualArea = computeArea(lat0, lat1, lon0, lon1);
			log.debug("lat0={}, lat1={}, lon0={}, lon1={} -> actualArea={}", lat0, lat1, lon0, lon1, actualArea);
			if (i >= 180 / step) { // Full longitude range!
				lon0 = -180;
				lon1 = 180;
				break;
			}
		}
		if (lon0 < -180) {
			lon0 += 360;
		}
		if (lon1 > 180) {
			lon1 -= 360;
		}
		log.debug("lon0={}, lon1={}", lon0, lon1);
		return new float[] { lon0, lon1 };
	}

	private static double computeArea(final double lat0deg, final double lat1deg, final double lon0deg, final double lon1deg) {
		final double lat0rad = Math.toRadians(lat0deg);
		final double lat1rad = Math.toRadians(lat1deg);
		final double lon0rad = Math.toRadians(lon0deg);
		final double lon1rad = Math.toRadians(lon1deg);
		final double a = Math.sin(lat1rad) - Math.sin(lat0rad);
		final double b = lon1rad - lon0rad;
		final double c = AUTHALIC_RADIUS * AUTHALIC_RADIUS;
		return a * b * c;
	}

	private static class ScaleInputDialog extends Dialog {

		private final String title;
		private final String message;

		private int value;

		private final int minimum;
		private final int maximum;
		private final int increment;
		private final int pageIncrement;

		private Scale scale;
		private Text text;

		private ScaleInputDialog(@NonNull final Shell parentShell, final String dialogTitle, final String dialogMessage, final int initialValue, final int minimum, final int maximum, final int increment, final int pageIncrement) {
			super(parentShell);
			this.title = dialogTitle;
			this.message = dialogMessage;
			this.value = initialValue;
			this.minimum = minimum;
			this.maximum = maximum;
			this.increment = increment;
			this.pageIncrement = pageIncrement;
		}

		@Override
		protected void buttonPressed(final int buttonId) {
			if (buttonId == IDialogConstants.OK_ID) {
				value = scale.getSelection();
			}
			super.buttonPressed(buttonId);
		}

		@Override
		protected void configureShell(final Shell shell) {
			super.configureShell(shell);
			if (title != null) {
				shell.setText(title);
			}
		}

		@Override
		protected void createButtonsForButtonBar(final Composite parent) {
			createButton(parent, IDialogConstants.OK_ID, JFaceMessages.get("lbl.button.ok"), true);
			createButton(parent, IDialogConstants.CANCEL_ID, JFaceMessages.get("lbl.button.cancel"), false);
			scale.setFocus();
			scale.setSelection(value);
		}

		@Override
		protected Control createDialogArea(final Composite parent) {
			final Composite composite = (Composite) super.createDialogArea(parent);
			if (composite.getLayout() instanceof GridLayout) {
				((GridLayout) composite.getLayout()).numColumns += 3;
			}

			if (message != null) {
				final Label label = new Label(composite, SWT.WRAP);
				label.setText(message);
				final GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
				data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH);
				data.horizontalSpan += 3;
				label.setLayoutData(data);
				label.setFont(parent.getFont());
			}

			scale = new Scale(composite, getScaleStyle());
			scale.setMinimum(minimum);
			scale.setMaximum(maximum);
			scale.setIncrement(increment);
			scale.setPageIncrement(pageIncrement);
			final GridData data = new GridData(GridData.FILL_HORIZONTAL);
			data.verticalAlignment = GridData.FILL;
			data.grabExcessHorizontalSpace = true;
			scale.setLayoutData(data);
			scale.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					text.setText(Integer.toString(scale.getSelection()));
				}
			});

			final Label plusMinusSign = new Label(composite, SWT.NONE);
			plusMinusSign.setText("\u00B1");
			GridDataFactory.swtDefaults().applyTo(plusMinusSign);
			plusMinusSign.setFont(parent.getFont());

			text = new Text(composite, SWT.BORDER | SWT.TRAIL);
			final int widthHint = new Formatter(getClass()).computeWidth(text, Integer.toString(maximum).length(), SWT.NORMAL);
			GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).hint(widthHint, SWT.DEFAULT).applyTo(text);
			text.setTextLimit(Integer.toString(maximum).length());
			text.setText(Integer.toString(value));
			text.addFocusListener(new TextFocusListener());
			text.addVerifyListener(new IntegerVerifyListener(false));

			final Label degreeSign = new Label(composite, SWT.NONE);
			degreeSign.setText("\u00B0");
			GridDataFactory.swtDefaults().applyTo(degreeSign);
			degreeSign.setFont(parent.getFont());

			applyDialogFont(composite);
			return composite;
		}

		public int getValue() {
			return value;
		}

		protected int getScaleStyle() {
			return SWT.HORIZONTAL;
		}

		private class TextFocusListener extends FocusAdapter {
			@Override
			public void focusLost(final FocusEvent fe) {
				try {
					int textValue = Integer.parseInt(text.getText());
					if (textValue > maximum) {
						textValue = maximum;
					}
					if (textValue < minimum) {
						textValue = minimum;
					}
					text.setText(Integer.toString(textValue));
					scale.setSelection(textValue);
				}
				catch (final RuntimeException e) {
					log.debug("Cannot update the selection (which is the value) of the scale:", e);
					text.setText(Integer.toString(scale.getSelection()));
				}
			}
		}
	}

}
