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
import java.util.logging.Logger;

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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

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
import it.albertus.jface.JFaceMessages;
import it.albertus.jface.Multilanguage;
import it.albertus.jface.SwtUtils;
import it.albertus.jface.decoration.ControlValidatorDecoration;
import it.albertus.jface.listener.FloatVerifyListener;
import it.albertus.jface.listener.IntegerVerifyListener;
import it.albertus.jface.maps.MapBoundsDialog;
import it.albertus.jface.maps.leaflet.LeafletMapBoundsDialog;
import it.albertus.jface.maps.leaflet.LeafletMapControl;
import it.albertus.jface.preference.IPreference;
import it.albertus.jface.preference.IPreferencesConfiguration;
import it.albertus.jface.validation.ControlValidator;
import it.albertus.jface.validation.FloatTextValidator;
import it.albertus.jface.validation.IntegerTextValidator;
import it.albertus.jface.validation.Validator;
import it.albertus.util.logging.LoggerFactory;

public class SearchForm implements IShellProvider, Multilanguage {

	private static final Logger logger = LoggerFactory.getLogger(SearchForm.class);

	public static final int COORDINATES_TEXT_LIMIT = 7;
	public static final int MAGNITUDE_TEXT_LIMIT = 4;
	public static final int PERIOD_TEXT_LIMIT = 10;
	public static final int RESULTS_TEXT_LIMIT = 4;
	public static final int AUTOREFRESH_TEXT_LIMIT = 9;

	public static final float LATITUDE_MIN_VALUE = -90;
	public static final float LATITUDE_MAX_VALUE = 90;
	public static final float LONGITUDE_MIN_VALUE = -180;
	public static final float LONGITUDE_MAX_VALUE = 180;
	public static final float MAGNITUDE_MIN_VALUE = 0;
	public static final float MAGNITUDE_MAX_VALUE = 10;
	public static final int RESULTS_MIN_VALUE = 1;
	public static final int RESULTS_MAX_VALUE = 1000;
	public static final int AUTOREFRESH_MIN_VALUE = 1;

	public static final String DATE_PATTERN = "yyyy-MM-dd";

	private static final byte CDATETIME_INDENT_RIGHT = 3;

	private static final String MSG_KEY_ERR_INTEGER_MIN = "err.preferences.integer.min";
	private static final String MSG_KEY_ERR_INTEGER_RANGE = "err.preferences.integer.range";
	private static final String MSG_KEY_ERR_DECIMAL_RANGE = "err.preferences.decimal.range";

	public static class Defaults {
		public static final boolean AUTOREFRESH_ENABLED = false;
		public static final boolean CRITERIA_RESTRICT = false;
		public static final Format FORMAT = Format.DEFAULT;

		private Defaults() {
			throw new IllegalAccessError("Constants class");
		}
	}

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getInstance();

	private final Shell shell;

	private final Composite formComposite;

	private final Label periodLabel;
	private final Label periodFromLabel;
	private final Label periodToLabel;
	private final CDateTime periodFromDateTime;
	private final CDateTime periodToDateTime;
	private final Label periodFromNote;
	private final Label periodToNote;

	private final Group areaGroup;

	private final Label latitudeLabel;
	private final Label latitudeFromLabel;
	private final Text latitudeFromText;
	private final Label latitudeFromNote;
	private final Label latitudeToLabel;
	private final Text latitudeToText;
	private final Label latitudeToNote;

	private final Label longitudeLabel;
	private final Label longitudeFromLabel;
	private final Text longitudeFromText;
	private final Label longitudeFromNote;
	private final Label longitudeToLabel;
	private final Text longitudeToText;
	private final Label longitudeToNote;

	private final Label minimumMagnitudeLabel;
	private final Text minimumMagnitudeText;
	private final Button restrictButton;

	private final Label outputFormatLabel;
	private final Composite radioComposite;
	private final Map<Format, Button> formatRadios = new EnumMap<>(Format.class);
	private final Label resultsLabel;
	private final Text resultsText;

	private final Group criteriaGroup;

	private final Composite buttonsComposite;
	private final Button searchButton;
	private final Label resultsNote;
	private final Button autoRefreshButton;
	private final Text autoRefreshText;
	private final Button clearButton;
	private final Button openMap;

	private final LeafletMapBoundsDialog mapBoundsDialog;

	private final Collection<ControlValidator<Text>> validators = new ArrayList<>();

	SearchForm(final EarthquakeBulletinGui gui) {
		shell = gui.getShell();

		final TraverseListener formFieldTraverseListener = new FormFieldTraverseListener(gui);
		final ModifyListener formTextModifyListener = new FormTextModifyListener(this);
		final VerifyListener coordinatesVerifyListener = new FloatVerifyListener(true);

		formComposite = new Composite(shell, SWT.NONE);
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(2).applyTo(formComposite);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(formComposite);

		criteriaGroup = new Group(formComposite, SWT.NONE);
		criteriaGroup.setText(Messages.get("lbl.form.criteria.group"));
		GridLayoutFactory.swtDefaults().numColumns(7).applyTo(criteriaGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(criteriaGroup);

		periodLabel = new Label(criteriaGroup, SWT.NONE);
		periodLabel.setText(Messages.get("lbl.form.criteria.period"));
		periodFromLabel = new Label(criteriaGroup, SWT.NONE);
		periodFromLabel.setText(Messages.get("lbl.form.criteria.period.from"));
		periodFromDateTime = new CDateTime(criteriaGroup, CDT.DROP_DOWN | CDT.BORDER);
		periodFromDateTime.setPattern(DATE_PATTERN);
		periodFromDateTime.setLocale(Messages.Language.ENGLISH.equals(Messages.getLanguage()) ? Locale.US : Messages.getLanguage().getLocale());
		periodFromDateTime.addTraverseListener(formFieldTraverseListener);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).indent(CDATETIME_INDENT_RIGHT, 0).applyTo(periodFromDateTime);
		periodFromNote = new Label(criteriaGroup, SWT.NONE);
		periodFromNote.setText(Messages.get("lbl.form.criteria.period.from.note"));
		periodToLabel = new Label(criteriaGroup, SWT.NONE);
		periodToLabel.setText(Messages.get("lbl.form.criteria.period.to"));
		periodToDateTime = new CDateTime(criteriaGroup, CDT.DROP_DOWN | CDT.BORDER);
		periodToDateTime.setPattern(DATE_PATTERN);
		periodToDateTime.setLocale(Messages.Language.ENGLISH.equals(Messages.getLanguage()) ? Locale.US : Messages.getLanguage().getLocale());
		periodToDateTime.addTraverseListener(formFieldTraverseListener);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).indent(CDATETIME_INDENT_RIGHT, 0).applyTo(periodToDateTime);
		periodToNote = new Label(criteriaGroup, SWT.NONE);
		periodToNote.setText(Messages.get("lbl.form.criteria.period.to.note"));

		areaGroup = new Group(criteriaGroup, SWT.NONE);
		areaGroup.setText(Messages.get("lbl.form.criteria.area"));
		GridLayoutFactory.swtDefaults().numColumns(8).applyTo(areaGroup);
		GridDataFactory.fillDefaults().span(7, 1).applyTo(areaGroup);

		latitudeLabel = new Label(areaGroup, SWT.NONE);
		latitudeLabel.setText(Messages.get("lbl.form.criteria.latitude"));
		latitudeFromLabel = new Label(areaGroup, SWT.NONE);
		latitudeFromLabel.setText(Messages.get("lbl.form.criteria.latitude.from"));
		latitudeFromText = new Text(areaGroup, SWT.BORDER);
		latitudeFromText.setTextLimit(COORDINATES_TEXT_LIMIT);
		latitudeFromText.addTraverseListener(formFieldTraverseListener);
		latitudeFromText.addVerifyListener(coordinatesVerifyListener);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(latitudeFromText);
		latitudeFromNote = new Label(areaGroup, SWT.NONE);
		latitudeFromNote.setText(Messages.get("lbl.form.criteria.latitude.from.note"));
		latitudeToLabel = new Label(areaGroup, SWT.NONE);
		latitudeToLabel.setText(Messages.get("lbl.form.criteria.latitude.to"));
		latitudeToText = new Text(areaGroup, SWT.BORDER);
		latitudeToText.setTextLimit(COORDINATES_TEXT_LIMIT);
		latitudeToText.addTraverseListener(formFieldTraverseListener);
		latitudeToText.addVerifyListener(coordinatesVerifyListener);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(latitudeToText);
		latitudeToNote = new Label(areaGroup, SWT.NONE);
		latitudeToNote.setText(Messages.get("lbl.form.criteria.latitude.to.note"));

		openMap = new Button(areaGroup, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.FILL).span(1, 2).applyTo(openMap);
		openMap.setToolTipText(Messages.get("lbl.form.button.map.tooltip"));
		openMap.addSelectionListener(new AreaMapSelectionListener(this));

		longitudeLabel = new Label(areaGroup, SWT.NONE);
		longitudeLabel.setText(Messages.get("lbl.form.criteria.longitude"));
		longitudeFromLabel = new Label(areaGroup, SWT.NONE);
		longitudeFromLabel.setText(Messages.get("lbl.form.criteria.longitude.from"));
		longitudeFromText = new Text(areaGroup, SWT.BORDER);
		longitudeFromText.setTextLimit(COORDINATES_TEXT_LIMIT);
		longitudeFromText.addTraverseListener(formFieldTraverseListener);
		longitudeFromText.addVerifyListener(coordinatesVerifyListener);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(longitudeFromText);
		longitudeFromNote = new Label(areaGroup, SWT.NONE);
		longitudeFromNote.setText(Messages.get("lbl.form.criteria.longitude.from.note"));
		longitudeToLabel = new Label(areaGroup, SWT.NONE);
		longitudeToLabel.setText(Messages.get("lbl.form.criteria.longitude.to"));
		longitudeToText = new Text(areaGroup, SWT.BORDER);
		longitudeToText.setTextLimit(COORDINATES_TEXT_LIMIT);
		longitudeToText.addTraverseListener(formFieldTraverseListener);
		longitudeToText.addVerifyListener(coordinatesVerifyListener);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(longitudeToText);
		longitudeToNote = new Label(areaGroup, SWT.NONE);
		longitudeToNote.setText(Messages.get("lbl.form.criteria.longitude.to.note"));

		minimumMagnitudeLabel = new Label(criteriaGroup, SWT.NONE);
		minimumMagnitudeLabel.setText(Messages.get("lbl.form.criteria.magnitude"));
		GridDataFactory.swtDefaults().span(2, 1).applyTo(minimumMagnitudeLabel);
		minimumMagnitudeText = new Text(criteriaGroup, SWT.BORDER);
		minimumMagnitudeText.setTextLimit(MAGNITUDE_TEXT_LIMIT);
		minimumMagnitudeText.addTraverseListener(formFieldTraverseListener);
		minimumMagnitudeText.addVerifyListener(new FloatVerifyListener(false));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(minimumMagnitudeText);
		restrictButton = new Button(criteriaGroup, SWT.CHECK);
		restrictButton.setText(Messages.get("lbl.form.criteria.restrict"));
		restrictButton.setSelection(configuration.getBoolean(Preference.CRITERIA_RESTRICT, Defaults.CRITERIA_RESTRICT));
		GridDataFactory.swtDefaults().span(4, 1).applyTo(restrictButton);

		outputFormatLabel = new Label(criteriaGroup, SWT.NONE);
		outputFormatLabel.setText(Messages.get("lbl.form.format"));
		radioComposite = new Composite(criteriaGroup, SWT.NONE);
		radioComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		GridDataFactory.swtDefaults().grab(false, false).span(2, 1).applyTo(radioComposite);
		Format selectedFormat;
		try {
			selectedFormat = Format.valueOf(configuration.getString(Preference.CRITERIA_FORMAT, Defaults.FORMAT.name()).trim().toUpperCase());
		}
		catch (final IllegalArgumentException e) {
			logger.log(Level.WARNING, e.toString(), e);
			selectedFormat = Defaults.FORMAT;
		}
		for (final Format format : Format.values()) {
			final Button radio = new Button(radioComposite, SWT.RADIO);
			radio.addSelectionListener(new FormatRadioSelectionListener(this, radio, format));
			radio.setText(format.getLabel());
			radio.setSelection(format.equals(selectedFormat));
			formatRadios.put(format, radio);
		}
		resultsLabel = new Label(criteriaGroup, SWT.NONE);
		resultsLabel.setText(Messages.get("lbl.form.limit"));
		GridDataFactory.swtDefaults().grab(false, false).span(2, 1).applyTo(resultsLabel);
		resultsText = new Text(criteriaGroup, SWT.BORDER);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(resultsText);
		resultsText.setTextLimit(RESULTS_TEXT_LIMIT);
		resultsText.addTraverseListener(formFieldTraverseListener);
		resultsText.addVerifyListener(new IntegerVerifyListener(false));
		resultsNote = new Label(criteriaGroup, SWT.NONE);
		resultsNote.setText(Messages.get("lbl.form.limit.note"));

		// Buttons
		buttonsComposite = new Composite(formComposite, SWT.NONE);
		GridLayoutFactory.swtDefaults().applyTo(buttonsComposite);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(buttonsComposite);

		autoRefreshButton = new Button(buttonsComposite, SWT.CHECK);
		autoRefreshButton.setText(Messages.get("lbl.form.button.autorefresh"));
		autoRefreshButton.setSelection(configuration.getBoolean(Preference.AUTOREFRESH_ENABLED, Defaults.AUTOREFRESH_ENABLED));
		GridDataFactory.swtDefaults().applyTo(autoRefreshButton);

		autoRefreshText = new Text(buttonsComposite, SWT.BORDER);
		autoRefreshText.setTextLimit(AUTOREFRESH_TEXT_LIMIT);
		autoRefreshText.addTraverseListener(formFieldTraverseListener);
		autoRefreshText.addVerifyListener(new IntegerVerifyListener(false));
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(autoRefreshText);

		searchButton = new Button(buttonsComposite, SWT.NONE);
		searchButton.setText(Messages.get("lbl.form.button.submit"));
		GridDataFactory.fillDefaults().grab(true, true).minSize(SwtUtils.convertHorizontalDLUsToPixels(searchButton, IDialogConstants.BUTTON_WIDTH), SWT.DEFAULT).applyTo(searchButton);

		clearButton = new Button(buttonsComposite, SWT.NONE);
		clearButton.setText(Messages.get("lbl.form.button.clear"));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(clearButton);

		// Listeners
		searchButton.addSelectionListener(new SearchButtonSelectionListener(gui));
		clearButton.addSelectionListener(new ClearButtonSelectionListener(this));
		autoRefreshButton.addSelectionListener(new AutoRefreshButtonSelectionListener(this));
		autoRefreshButton.notifyListeners(SWT.Selection, null);

		// Decorators
		ControlValidator<Text> validator = new FloatTextValidator(latitudeFromText, true, LATITUDE_MIN_VALUE, LATITUDE_MAX_VALUE);
		new FormControlValidatorDecoration(validator, () -> JFaceMessages.get(MSG_KEY_ERR_DECIMAL_RANGE, LATITUDE_MIN_VALUE, LATITUDE_MAX_VALUE));
		validators.add(validator);

		validator = new FloatTextValidator(latitudeToText, true, LATITUDE_MIN_VALUE, LATITUDE_MAX_VALUE);
		new FormControlValidatorDecoration(validator, () -> JFaceMessages.get(MSG_KEY_ERR_DECIMAL_RANGE, LATITUDE_MIN_VALUE, LATITUDE_MAX_VALUE));
		validators.add(validator);

		validator = new FloatTextValidator(longitudeFromText, true, LONGITUDE_MIN_VALUE, LONGITUDE_MAX_VALUE);
		new FormControlValidatorDecoration(validator, () -> JFaceMessages.get(MSG_KEY_ERR_DECIMAL_RANGE, LONGITUDE_MIN_VALUE, LONGITUDE_MAX_VALUE));
		validators.add(validator);

		validator = new FloatTextValidator(longitudeToText, true, LONGITUDE_MIN_VALUE, LONGITUDE_MAX_VALUE);
		new FormControlValidatorDecoration(validator, () -> JFaceMessages.get(MSG_KEY_ERR_DECIMAL_RANGE, LONGITUDE_MIN_VALUE, LONGITUDE_MAX_VALUE));
		validators.add(validator);

		validator = new FloatTextValidator(minimumMagnitudeText, true, MAGNITUDE_MIN_VALUE, MAGNITUDE_MAX_VALUE);
		new FormControlValidatorDecoration(validator, () -> JFaceMessages.get(MSG_KEY_ERR_DECIMAL_RANGE, MAGNITUDE_MIN_VALUE, MAGNITUDE_MAX_VALUE));
		validators.add(validator);

		validator = new IntegerTextValidator(resultsText, true, RESULTS_MIN_VALUE, RESULTS_MAX_VALUE);
		new FormControlValidatorDecoration(validator, () -> JFaceMessages.get(MSG_KEY_ERR_INTEGER_RANGE, RESULTS_MIN_VALUE, RESULTS_MAX_VALUE));
		validators.add(validator);

		validator = new IntegerTextValidator(autoRefreshText, true, AUTOREFRESH_MIN_VALUE, null);
		new ControlValidatorDecoration(validator, () -> JFaceMessages.get(MSG_KEY_ERR_INTEGER_MIN, AUTOREFRESH_MIN_VALUE));
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
		mapBoundsDialog.setText(Messages.get("lbl.map.bounds.title"));
		mapBoundsDialog.setImages(Images.getMainIconArray());
		mapBoundsDialog.getOptions().getControls().put(LeafletMapControl.ZOOM, "");
		mapBoundsDialog.getOptions().getControls().put(LeafletMapControl.ATTRIBUTION, "");
		mapBoundsDialog.getOptions().getControls().put(LeafletMapControl.SCALE, "");
		if (Leaflet.LAYERS != null && !Leaflet.LAYERS.isEmpty()) {
			mapBoundsDialog.getOptions().getControls().put(LeafletMapControl.LAYERS, Leaflet.LAYERS);
		}
	}

	void setOpenMapButtonImage() {
		final Point buttonSize = openMap.getSize();
		for (final Entry<Rectangle, Image> entry : Images.getOpenStreetMapIconMap().entrySet()) {
			if (entry.getKey().height < buttonSize.y - SwtUtils.convertVerticalDLUsToPixels(openMap, 3)) {
				logger.log(Level.FINE, "Setting OpenStreetMap icon: {0}", entry);
				openMap.setImage(entry.getValue());
				break;
			}
		}
		openMap.setRedraw(true);
		openMap.requestLayout();
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
		criteriaGroup.setText(Messages.get("lbl.form.criteria.group"));
		periodLabel.setText(Messages.get("lbl.form.criteria.period"));
		periodFromLabel.setText(Messages.get("lbl.form.criteria.period.from"));
		periodFromNote.setText(Messages.get("lbl.form.criteria.period.from.note"));
		periodToLabel.setText(Messages.get("lbl.form.criteria.period.to"));
		periodToNote.setText(Messages.get("lbl.form.criteria.period.to.note"));
		areaGroup.setText(Messages.get("lbl.form.criteria.area"));
		latitudeLabel.setText(Messages.get("lbl.form.criteria.latitude"));
		latitudeFromLabel.setText(Messages.get("lbl.form.criteria.latitude.from"));
		latitudeFromNote.setText(Messages.get("lbl.form.criteria.latitude.from.note"));
		latitudeToLabel.setText(Messages.get("lbl.form.criteria.latitude.to"));
		latitudeToNote.setText(Messages.get("lbl.form.criteria.latitude.to.note"));
		longitudeLabel.setText(Messages.get("lbl.form.criteria.longitude"));
		longitudeFromLabel.setText(Messages.get("lbl.form.criteria.longitude.from"));
		longitudeFromNote.setText(Messages.get("lbl.form.criteria.longitude.from.note"));
		longitudeToLabel.setText(Messages.get("lbl.form.criteria.longitude.to"));
		longitudeToNote.setText(Messages.get("lbl.form.criteria.longitude.to.note"));
		minimumMagnitudeLabel.setText(Messages.get("lbl.form.criteria.magnitude"));
		restrictButton.setText(Messages.get("lbl.form.criteria.restrict"));
		outputFormatLabel.setText(Messages.get("lbl.form.format"));
		resultsLabel.setText(Messages.get("lbl.form.limit"));
		resultsNote.setText(Messages.get("lbl.form.limit.note"));
		autoRefreshButton.setText(Messages.get("lbl.form.button.autorefresh"));
		searchButton.setText(Messages.get("lbl.form.button.submit"));
		clearButton.setText(Messages.get("lbl.form.button.clear"));
		if (openMap.getToolTipText() != null && !openMap.getToolTipText().isEmpty()) {
			openMap.setToolTipText(Messages.get("lbl.form.button.map.tooltip"));
		}
		if (!openMap.getText().isEmpty()) {
			openMap.setText(Messages.get("lbl.form.button.map"));
		}
		mapBoundsDialog.setText(Messages.get("lbl.map.bounds.title"));
	}

	private String getConfiguredFloatString(final IPreference preference) {
		String value = "";
		try {
			final Float number = configuration.getFloat(preference);
			if (number != null) {
				value = number.toString();
			}
		}
		catch (final RuntimeException e) {
			logger.log(Level.WARNING, e.toString(), e);
		}
		return value;
	}

	private String getConfiguredIntegerString(final IPreference preference) {
		String value = "";
		try {
			final Integer number = configuration.getInt(preference);
			if (number != null) {
				value = number.toString();
			}
		}
		catch (final RuntimeException e) {
			logger.log(Level.WARNING, e.toString(), e);
		}
		return value;
	}

	private Date getConfiguredDate(final IPreference preference) {
		Date value = null;
		final String dateStr = configuration.getString(preference);
		if (dateStr != null && !dateStr.trim().isEmpty()) {
			try {
				final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().appendPattern(DATE_PATTERN).parseDefaulting(ChronoField.HOUR_OF_DAY, 0).toFormatter().withZone(ZoneOffset.UTC);
				value = Date.from(dateTimeFormatter.parse(dateStr, Instant::from));
			}
			catch (final RuntimeException e) {
				logger.log(Level.WARNING, e.toString(), e);
			}
		}
		return value;
	}

	@Override
	public Shell getShell() {
		return shell;
	}

	public Label getPeriodLabel() {
		return periodLabel;
	}

	public Label getPeriodFromLabel() {
		return periodFromLabel;
	}

	public Label getPeriodToLabel() {
		return periodToLabel;
	}

	public CDateTime getPeriodFromDateTime() {
		return periodFromDateTime;
	}

	public CDateTime getPeriodToDateTime() {
		return periodToDateTime;
	}

	public Label getPeriodFromNote() {
		return periodFromNote;
	}

	public Label getPeriodToNote() {
		return periodToNote;
	}

	public Text getLatitudeFromText() {
		return latitudeFromText;
	}

	public Text getLatitudeToText() {
		return latitudeToText;
	}

	public Text getLongitudeFromText() {
		return longitudeFromText;
	}

	public Text getLongitudeToText() {
		return longitudeToText;
	}

	public Text getMinimumMagnitudeText() {
		return minimumMagnitudeText;
	}

	public Button getRestrictButton() {
		return restrictButton;
	}

	public Map<Format, Button> getFormatRadios() {
		return formatRadios;
	}

	public Label getResultsLabel() {
		return resultsLabel;
	}

	public Text getResultsText() {
		return resultsText;
	}

	public Button getSearchButton() {
		return searchButton;
	}

	public Button getAutoRefreshButton() {
		return autoRefreshButton;
	}

	public Text getAutoRefreshText() {
		return autoRefreshText;
	}

	public MapBoundsDialog getMapBoundsDialog() {
		return mapBoundsDialog;
	}

	public Collection<ControlValidator<Text>> getValidators() {
		return validators;
	}

}
