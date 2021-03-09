package it.albertus.eqbulletin.gui;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.decoration.FormControlValidatorDecoration;
import it.albertus.eqbulletin.gui.listener.AreaMapSelectionListener;
import it.albertus.eqbulletin.gui.listener.AutoRefreshButtonSelectionListener;
import it.albertus.eqbulletin.gui.listener.ClearButtonSelectionListener;
import it.albertus.eqbulletin.gui.listener.FormFieldTraverseListener;
import it.albertus.eqbulletin.gui.listener.FormTextModifyListener;
import it.albertus.eqbulletin.gui.listener.FormatRadioSelectionListener;
import it.albertus.eqbulletin.gui.listener.SearchButtonSelectionListener;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.eqbulletin.model.Format;
import it.albertus.eqbulletin.resources.Leaflet;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.Multilanguage;
import it.albertus.jface.SwtUtils;
import it.albertus.jface.decoration.ControlValidatorDecoration;
import it.albertus.jface.listener.FloatVerifyListener;
import it.albertus.jface.listener.IntegerVerifyListener;
import it.albertus.jface.maps.MapBounds;
import it.albertus.jface.maps.leaflet.LeafletMapBoundsDialog;
import it.albertus.jface.maps.leaflet.LeafletMapControl;
import it.albertus.jface.preference.IPreference;
import it.albertus.jface.preference.IPreferencesConfiguration;
import it.albertus.jface.validation.ControlValidator;
import it.albertus.jface.validation.FloatTextValidator;
import it.albertus.jface.validation.IntegerTextValidator;
import it.albertus.jface.validation.Validator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.java.Log;

@Log
@Getter
public class SearchForm implements IShellProvider, Multilanguage {

	public static final float LATITUDE_MIN_VALUE = MapBounds.LATITUDE_MIN_VALUE;
	public static final float LATITUDE_MAX_VALUE = MapBounds.LATITUDE_MAX_VALUE;
	public static final float LONGITUDE_MIN_VALUE = MapBounds.LONGITUDE_MIN_VALUE;
	public static final float LONGITUDE_MAX_VALUE = MapBounds.LONGITUDE_MAX_VALUE;
	public static final float MAGNITUDE_MIN_VALUE = 0;
	public static final float MAGNITUDE_MAX_VALUE = 10;
	public static final int RESULTS_MIN_VALUE = 1;
	public static final int RESULTS_MAX_VALUE = 5000;
	public static final int AUTOREFRESH_MIN_VALUE = 1;

	public static final int COORDINATES_TEXT_LIMIT = 7;
	public static final int MAGNITUDE_TEXT_LIMIT = 4;
	public static final int PERIOD_TEXT_LIMIT = 10;
	public static final int RESULTS_TEXT_LIMIT = Integer.toString(RESULTS_MAX_VALUE).length();
	public static final int AUTOREFRESH_TEXT_LIMIT = 9;

	public static final String DATE_PATTERN = "yyyy-MM-dd";

	private static final byte CDATETIME_INDENT_RIGHT = 3;

	private static final String ERROR_FORM_INTEGER_MIN = "error.form.integer.min";
	private static final String ERROR_FORM_INTEGER_RANGE = "error.form.integer.range";
	private static final String ERROR_FORM_DECIMAL_RANGE = "error.form.decimal.range";

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Defaults {
		public static final boolean AUTOREFRESH_ENABLED = false;
		public static final boolean CRITERIA_RESTRICT = false;
		public static final Format FORMAT = Format.DEFAULT;
	}

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getPreferencesConfiguration();

	private final Shell shell;

	private final Label periodLabel;
	private final Label periodFromLabel;
	private final Label periodToLabel;
	private final CDateTime periodFromDateTime;
	private final CDateTime periodToDateTime;
	private final Label periodFromNote;
	private final Label periodToNote;

	private final Text latitudeFromText;
	private final Text latitudeToText;
	@Getter(AccessLevel.NONE) private Button openMapButton;
	private final Text longitudeFromText;
	private final Text longitudeToText;

	private final Text minimumMagnitudeText;
	private final Button restrictButton;

	private final Map<Format, Button> formatRadios = new EnumMap<>(Format.class);
	private final Label resultsLabel;
	private final Text resultsText;

	private final Button searchButton;
	private final Button autoRefreshButton;
	private final Text autoRefreshText;

	private final LeafletMapBoundsDialog mapBoundsDialog;

	private final Collection<ControlValidator<Text>> validators = new ArrayList<>();

	@Getter(AccessLevel.NONE) private final LocalizedWidgets localizedWidgets = new LocalizedWidgets();

	SearchForm(@NonNull final EarthquakeBulletinGui gui) {
		shell = gui.getShell();

		final TraverseListener formFieldTraverseListener = new FormFieldTraverseListener(gui);
		final ModifyListener formTextModifyListener = new FormTextModifyListener(this);
		final VerifyListener coordinatesVerifyListener = new FloatVerifyListener(true);

		final Composite formComposite = new Composite(shell, SWT.NONE);
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(2).applyTo(formComposite);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(formComposite);

		final Group criteriaGroup = newLocalizedWidget(new Group(formComposite, SWT.NONE), "label.form.criteria.group");
		GridLayoutFactory.swtDefaults().numColumns(7).applyTo(criteriaGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(criteriaGroup);

		periodLabel = newLocalizedWidget(new Label(criteriaGroup, SWT.NONE), "label.form.criteria.period");
		periodFromLabel = newLocalizedWidget(new Label(criteriaGroup, SWT.NONE), "label.form.criteria.period.from");
		periodFromDateTime = new CDateTime(criteriaGroup, CDT.DROP_DOWN | CDT.BORDER);
		periodFromDateTime.setPattern(DATE_PATTERN);
		periodFromDateTime.setLocale(Messages.Language.ENGLISH.equals(Messages.getLanguage()) ? Locale.US : Messages.getLanguage().getLocale());
		periodFromDateTime.addTraverseListener(formFieldTraverseListener);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).indent(CDATETIME_INDENT_RIGHT, 0).applyTo(periodFromDateTime);
		periodFromNote = newLocalizedWidget(new Label(criteriaGroup, SWT.NONE), "label.form.criteria.period.from.note");
		periodToLabel = newLocalizedWidget(new Label(criteriaGroup, SWT.NONE), "label.form.criteria.period.to");
		periodToDateTime = new CDateTime(criteriaGroup, CDT.DROP_DOWN | CDT.BORDER);
		periodToDateTime.setPattern(DATE_PATTERN);
		periodToDateTime.setLocale(Messages.Language.ENGLISH.equals(Messages.getLanguage()) ? Locale.US : Messages.getLanguage().getLocale());
		periodToDateTime.addTraverseListener(formFieldTraverseListener);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).indent(CDATETIME_INDENT_RIGHT, 0).applyTo(periodToDateTime);
		periodToNote = newLocalizedWidget(new Label(criteriaGroup, SWT.NONE), "label.form.criteria.period.to.note");

		final Group areaGroup = newLocalizedWidget(new Group(criteriaGroup, SWT.NONE), "label.form.criteria.area");
		GridLayoutFactory.swtDefaults().numColumns(8).applyTo(areaGroup);
		GridDataFactory.fillDefaults().span(7, 1).applyTo(areaGroup);

		newLocalizedWidget(new Label(areaGroup, SWT.NONE), "label.form.criteria.latitude");
		newLocalizedWidget(new Label(areaGroup, SWT.NONE), "label.form.criteria.latitude.from");
		latitudeFromText = new Text(areaGroup, SWT.BORDER);
		latitudeFromText.setTextLimit(COORDINATES_TEXT_LIMIT);
		latitudeFromText.addTraverseListener(formFieldTraverseListener);
		latitudeFromText.addVerifyListener(coordinatesVerifyListener);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(latitudeFromText);
		newLocalizedWidget(new Label(areaGroup, SWT.NONE), "label.form.criteria.latitude.from.note");
		newLocalizedWidget(new Label(areaGroup, SWT.NONE), "label.form.criteria.latitude.to");
		latitudeToText = new Text(areaGroup, SWT.BORDER);
		latitudeToText.setTextLimit(COORDINATES_TEXT_LIMIT);
		latitudeToText.addTraverseListener(formFieldTraverseListener);
		latitudeToText.addVerifyListener(coordinatesVerifyListener);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(latitudeToText);
		newLocalizedWidget(new Label(areaGroup, SWT.NONE), "label.form.criteria.latitude.to.note");

		openMapButton = new Button(areaGroup, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.FILL).span(1, 2).applyTo(openMapButton);
		openMapButton.setToolTipText(Messages.get("label.form.button.map.tooltip"));
		final int buttonVerticalSize = SwtUtils.convertVerticalDLUsToPixels(openMapButton, 25);
		for (final Entry<Rectangle, Image> entry : Images.getMapIconMap().entrySet()) {
			if (entry.getKey().height < buttonVerticalSize - buttonVerticalSize / 6.8f) { // leaving some room around the image
				log.log(Level.FINE, "Open Map button size: {0}; setting OpenStreetMap icon: {1}.", new Object[] { buttonVerticalSize, entry });
				openMapButton.setImage(entry.getValue());
				break;
			}
		}
		openMapButton.addSelectionListener(new AreaMapSelectionListener(this));

		newLocalizedWidget(new Label(areaGroup, SWT.NONE), "label.form.criteria.longitude");
		newLocalizedWidget(new Label(areaGroup, SWT.NONE), "label.form.criteria.longitude.from");
		longitudeFromText = new Text(areaGroup, SWT.BORDER);
		longitudeFromText.setTextLimit(COORDINATES_TEXT_LIMIT);
		longitudeFromText.addTraverseListener(formFieldTraverseListener);
		longitudeFromText.addVerifyListener(coordinatesVerifyListener);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(longitudeFromText);
		newLocalizedWidget(new Label(areaGroup, SWT.NONE), "label.form.criteria.longitude.from.note");
		newLocalizedWidget(new Label(areaGroup, SWT.NONE), "label.form.criteria.longitude.to");
		longitudeToText = new Text(areaGroup, SWT.BORDER);
		longitudeToText.setTextLimit(COORDINATES_TEXT_LIMIT);
		longitudeToText.addTraverseListener(formFieldTraverseListener);
		longitudeToText.addVerifyListener(coordinatesVerifyListener);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(longitudeToText);
		newLocalizedWidget(new Label(areaGroup, SWT.NONE), "label.form.criteria.longitude.to.note");

		final Label minimumMagnitudeLabel = newLocalizedWidget(new Label(criteriaGroup, SWT.NONE), "label.form.criteria.magnitude");
		GridDataFactory.swtDefaults().span(2, 1).applyTo(minimumMagnitudeLabel);
		minimumMagnitudeText = new Text(criteriaGroup, SWT.BORDER);
		minimumMagnitudeText.setTextLimit(MAGNITUDE_TEXT_LIMIT);
		minimumMagnitudeText.addTraverseListener(formFieldTraverseListener);
		minimumMagnitudeText.addVerifyListener(new FloatVerifyListener(false));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(minimumMagnitudeText);
		restrictButton = newLocalizedWidget(new Button(criteriaGroup, SWT.CHECK), "label.form.criteria.restrict");
		restrictButton.setSelection(configuration.getBoolean(Preference.CRITERIA_RESTRICT, Defaults.CRITERIA_RESTRICT));
		GridDataFactory.swtDefaults().span(4, 1).applyTo(restrictButton);

		newLocalizedWidget(new Label(criteriaGroup, SWT.NONE), "label.form.format");
		final Composite radioComposite = new Composite(criteriaGroup, SWT.NONE);
		radioComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		GridDataFactory.swtDefaults().grab(false, false).span(2, 1).applyTo(radioComposite);
		Format selectedFormat;
		try {
			selectedFormat = Format.valueOf(configuration.getString(Preference.CRITERIA_FORMAT, Defaults.FORMAT.name()).trim().toUpperCase());
		}
		catch (final IllegalArgumentException e) {
			log.log(Level.WARNING, e.toString(), e);
			selectedFormat = Defaults.FORMAT;
		}
		for (final Format format : Format.values()) {
			final Button radio = new Button(radioComposite, SWT.RADIO);
			radio.addSelectionListener(new FormatRadioSelectionListener(this, radio, format));
			radio.setText(format.getLabel());
			radio.setSelection(format.equals(selectedFormat));
			formatRadios.put(format, radio);
		}
		resultsLabel = newLocalizedWidget(new Label(criteriaGroup, SWT.NONE), "label.form.limit");
		GridDataFactory.swtDefaults().grab(false, false).span(2, 1).applyTo(resultsLabel);
		resultsText = new Text(criteriaGroup, SWT.BORDER);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(resultsText);
		resultsText.setTextLimit(RESULTS_TEXT_LIMIT);
		resultsText.addTraverseListener(formFieldTraverseListener);
		resultsText.addVerifyListener(new IntegerVerifyListener(false));
		newLocalizedWidget(new Label(criteriaGroup, SWT.NONE), "label.form.limit.note");

		// Buttons
		final Composite buttonsComposite = new Composite(formComposite, SWT.NONE);
		GridLayoutFactory.swtDefaults().applyTo(buttonsComposite);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(buttonsComposite);

		autoRefreshButton = newLocalizedWidget(new Button(buttonsComposite, SWT.CHECK), "label.form.button.autorefresh");
		autoRefreshButton.setSelection(configuration.getBoolean(Preference.AUTOREFRESH_ENABLED, Defaults.AUTOREFRESH_ENABLED));
		GridDataFactory.swtDefaults().applyTo(autoRefreshButton);

		autoRefreshText = new Text(buttonsComposite, SWT.BORDER);
		autoRefreshText.setTextLimit(AUTOREFRESH_TEXT_LIMIT);
		autoRefreshText.addTraverseListener(formFieldTraverseListener);
		autoRefreshText.addVerifyListener(new IntegerVerifyListener(false));
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(autoRefreshText);

		searchButton = newLocalizedWidget(new Button(buttonsComposite, SWT.NONE), "label.form.button.submit");
		GridDataFactory.fillDefaults().grab(true, true).minSize(SwtUtils.convertHorizontalDLUsToPixels(searchButton, IDialogConstants.BUTTON_WIDTH), SWT.DEFAULT).applyTo(searchButton);

		final Button clearButton = newLocalizedWidget(new Button(buttonsComposite, SWT.NONE), "label.form.button.clear");
		GridDataFactory.fillDefaults().grab(true, true).applyTo(clearButton);

		// Listeners
		searchButton.addSelectionListener(new SearchButtonSelectionListener(gui));
		clearButton.addSelectionListener(new ClearButtonSelectionListener(this));
		autoRefreshButton.addSelectionListener(new AutoRefreshButtonSelectionListener(this));
		autoRefreshButton.notifyListeners(SWT.Selection, null);

		// Decorators
		ControlValidator<Text> validator = new FloatTextValidator(latitudeFromText, true, LATITUDE_MIN_VALUE, LATITUDE_MAX_VALUE);
		new FormControlValidatorDecoration(validator, () -> Messages.get(ERROR_FORM_DECIMAL_RANGE, LATITUDE_MIN_VALUE, LATITUDE_MAX_VALUE));
		validators.add(validator);

		validator = new FloatTextValidator(latitudeToText, true, LATITUDE_MIN_VALUE, LATITUDE_MAX_VALUE);
		new FormControlValidatorDecoration(validator, () -> Messages.get(ERROR_FORM_DECIMAL_RANGE, LATITUDE_MIN_VALUE, LATITUDE_MAX_VALUE));
		validators.add(validator);

		validator = new FloatTextValidator(longitudeFromText, true, LONGITUDE_MIN_VALUE, LONGITUDE_MAX_VALUE);
		new FormControlValidatorDecoration(validator, () -> Messages.get(ERROR_FORM_DECIMAL_RANGE, LONGITUDE_MIN_VALUE, LONGITUDE_MAX_VALUE));
		validators.add(validator);

		validator = new FloatTextValidator(longitudeToText, true, LONGITUDE_MIN_VALUE, LONGITUDE_MAX_VALUE);
		new FormControlValidatorDecoration(validator, () -> Messages.get(ERROR_FORM_DECIMAL_RANGE, LONGITUDE_MIN_VALUE, LONGITUDE_MAX_VALUE));
		validators.add(validator);

		validator = new FloatTextValidator(minimumMagnitudeText, true, MAGNITUDE_MIN_VALUE, MAGNITUDE_MAX_VALUE);
		new FormControlValidatorDecoration(validator, () -> Messages.get(ERROR_FORM_DECIMAL_RANGE, MAGNITUDE_MIN_VALUE, MAGNITUDE_MAX_VALUE));
		validators.add(validator);

		validator = new IntegerTextValidator(resultsText, true, RESULTS_MIN_VALUE, RESULTS_MAX_VALUE);
		new FormControlValidatorDecoration(validator, () -> Messages.get(ERROR_FORM_INTEGER_RANGE, RESULTS_MIN_VALUE, RESULTS_MAX_VALUE));
		validators.add(validator);

		validator = new IntegerTextValidator(autoRefreshText, true, AUTOREFRESH_MIN_VALUE, null);
		new ControlValidatorDecoration(validator, () -> Messages.get(ERROR_FORM_INTEGER_MIN, AUTOREFRESH_MIN_VALUE));
		validators.add(validator);

		// Text modify listeners
		latitudeFromText.addModifyListener(formTextModifyListener);
		latitudeToText.addModifyListener(formTextModifyListener);
		longitudeFromText.addModifyListener(formTextModifyListener);
		longitudeToText.addModifyListener(formTextModifyListener);
		minimumMagnitudeText.addModifyListener(formTextModifyListener);
		resultsText.addModifyListener(formTextModifyListener);
		autoRefreshText.addModifyListener(formTextModifyListener);

		// Load parameters from configuration
		periodFromDateTime.setSelection(getConfiguredDate(Preference.CRITERIA_PERIOD_FROM));
		periodToDateTime.setSelection(getConfiguredDate(Preference.CRITERIA_PERIOD_TO));
		latitudeFromText.setText(getConfiguredFloatString(Preference.CRITERIA_LATITUDE_FROM));
		latitudeToText.setText(getConfiguredFloatString(Preference.CRITERIA_LATITUDE_TO));
		longitudeFromText.setText(getConfiguredFloatString(Preference.CRITERIA_LONGITUDE_FROM));
		longitudeToText.setText(getConfiguredFloatString(Preference.CRITERIA_LONGITUDE_TO));
		minimumMagnitudeText.setText(getConfiguredFloatString(Preference.CRITERIA_MAGNITUDE));
		resultsText.setText(getConfiguredIntegerString(Preference.CRITERIA_LIMIT));
		autoRefreshText.setText(getConfiguredIntegerString(Preference.AUTOREFRESH_MINS));

		// Map
		mapBoundsDialog = new LeafletMapBoundsDialog(shell);
		mapBoundsDialog.setText(Messages.get("label.map.bounds.title"));
		mapBoundsDialog.setImages(Images.getAppIconArray());
		mapBoundsDialog.getOptions().getControls().put(LeafletMapControl.ZOOM, "");
		mapBoundsDialog.getOptions().getControls().put(LeafletMapControl.ATTRIBUTION, "");
		mapBoundsDialog.getOptions().getControls().put(LeafletMapControl.SCALE, "");
		if (Leaflet.LAYERS != null && !Leaflet.LAYERS.isEmpty()) {
			mapBoundsDialog.getOptions().getControls().put(LeafletMapControl.LAYERS, Leaflet.LAYERS);
		}
	}

	public boolean isValid() {
		for (final Validator validator : validators) {
			if (!validator.isValid()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void updateLanguage() {
		periodFromDateTime.setLocale(Messages.Language.ENGLISH.equals(Messages.getLanguage()) ? Locale.US : Messages.getLanguage().getLocale());
		periodToDateTime.setLocale(Messages.Language.ENGLISH.equals(Messages.getLanguage()) ? Locale.US : Messages.getLanguage().getLocale());
		mapBoundsDialog.setText(Messages.get("label.map.bounds.title"));
		if (openMapButton.getToolTipText() != null && !openMapButton.getToolTipText().isEmpty()) {
			openMapButton.setToolTipText(Messages.get("label.form.button.map.tooltip"));
		}
		if (!openMapButton.getText().isEmpty()) {
			openMapButton.setText(Messages.get("label.form.button.map"));
		}
		localizedWidgets.updateTexts();
	}

	private <T extends Widget> T newLocalizedWidget(@NonNull final T widget, @NonNull final String messageKey) {
		localizedWidgets.add(widget, () -> Messages.get(messageKey));
		return widget;
	}

	private static String getConfiguredFloatString(final IPreference preference) {
		String value = "";
		try {
			final Float number = configuration.getFloat(preference);
			if (number != null) {
				value = number.toString();
			}
		}
		catch (final RuntimeException e) {
			log.log(Level.WARNING, e.toString(), e);
		}
		return value;
	}

	private static String getConfiguredIntegerString(final IPreference preference) {
		String value = "";
		try {
			final Integer number = configuration.getInt(preference);
			if (number != null) {
				value = number.toString();
			}
		}
		catch (final RuntimeException e) {
			log.log(Level.WARNING, e.toString(), e);
		}
		return value;
	}

	private static Date getConfiguredDate(final IPreference preference) {
		Date value = null;
		final String dateStr = configuration.getString(preference);
		if (dateStr != null && !dateStr.trim().isEmpty()) {
			try {
				final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().appendPattern(DATE_PATTERN).parseDefaulting(ChronoField.HOUR_OF_DAY, 0).toFormatter().withZone(ZoneOffset.UTC);
				value = Date.from(dateTimeFormatter.parse(dateStr, Instant::from));
			}
			catch (final RuntimeException e) {
				log.log(Level.WARNING, e.toString(), e);
			}
		}
		return value;
	}

}
