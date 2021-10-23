package io.github.albertus82.eqbulletin.gui.preference;

import static io.github.albertus82.eqbulletin.gui.preference.PageDefinition.ADVANCED;
import static io.github.albertus82.eqbulletin.gui.preference.PageDefinition.CACHE;
import static io.github.albertus82.eqbulletin.gui.preference.PageDefinition.CONNECTION;
import static io.github.albertus82.eqbulletin.gui.preference.PageDefinition.CRITERIA;
import static io.github.albertus82.eqbulletin.gui.preference.PageDefinition.GENERAL;
import static io.github.albertus82.eqbulletin.gui.preference.PageDefinition.LOGGING;

import java.awt.SystemTray;
import java.net.Proxy;
import java.time.ZoneId;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.widgets.Composite;

import io.github.albertus82.eqbulletin.cache.BeachBallCache;
import io.github.albertus82.eqbulletin.cache.MapImageCache;
import io.github.albertus82.eqbulletin.cache.MomentTensorCache;
import io.github.albertus82.eqbulletin.config.LanguageConfigAccessor;
import io.github.albertus82.eqbulletin.config.TimeZoneConfigAccessor;
import io.github.albertus82.eqbulletin.config.logging.LoggingConfig;
import io.github.albertus82.eqbulletin.config.logging.LoggingLevel;
import io.github.albertus82.eqbulletin.gui.CloseDialog;
import io.github.albertus82.eqbulletin.gui.EarthquakeBulletinGui;
import io.github.albertus82.eqbulletin.gui.MapCanvas;
import io.github.albertus82.eqbulletin.gui.MomentTensorDialog;
import io.github.albertus82.eqbulletin.gui.ResultsTable;
import io.github.albertus82.eqbulletin.gui.SearchForm;
import io.github.albertus82.eqbulletin.gui.TrayIcon;
import io.github.albertus82.eqbulletin.model.Format;
import io.github.albertus82.eqbulletin.resources.Messages;
import io.github.albertus82.eqbulletin.resources.Messages.Language;
import io.github.albertus82.eqbulletin.service.GeofonUtils;
import io.github.albertus82.eqbulletin.service.decode.html.HtmlBulletinVersion;
import io.github.albertus82.eqbulletin.service.net.ConnectionFactory;
import io.github.albertus82.jface.SwtUtils;
import io.github.albertus82.jface.preference.FieldEditorDetails;
import io.github.albertus82.jface.preference.FieldEditorDetails.FieldEditorDetailsBuilder;
import io.github.albertus82.jface.preference.FieldEditorFactory;
import io.github.albertus82.jface.preference.IPreference;
import io.github.albertus82.jface.preference.LocalizedLabelsAndValues;
import io.github.albertus82.jface.preference.PreferenceDetails;
import io.github.albertus82.jface.preference.PreferenceDetails.PreferenceDetailsBuilder;
import io.github.albertus82.jface.preference.StaticLabelsAndValues;
import io.github.albertus82.jface.preference.field.DateFieldEditor;
import io.github.albertus82.jface.preference.field.DefaultBooleanFieldEditor;
import io.github.albertus82.jface.preference.field.DefaultComboFieldEditor;
import io.github.albertus82.jface.preference.field.DefaultRadioGroupFieldEditor;
import io.github.albertus82.jface.preference.field.EnhancedDirectoryFieldEditor;
import io.github.albertus82.jface.preference.field.EnhancedIntegerFieldEditor;
import io.github.albertus82.jface.preference.field.EnhancedStringFieldEditor;
import io.github.albertus82.jface.preference.field.FloatFieldEditor;
import io.github.albertus82.jface.preference.field.ListFieldEditor;
import io.github.albertus82.jface.preference.field.PasswordFieldEditor;
import io.github.albertus82.jface.preference.field.ScaleIntegerFieldEditor;
import io.github.albertus82.jface.preference.field.ValidatedComboFieldEditor;
import io.github.albertus82.jface.preference.page.IPageDefinition;

public enum Preference implements IPreference {

	LANGUAGE(new PreferenceDetailsBuilder(GENERAL).defaultValue(LanguageConfigAccessor.DEFAULT_LANGUAGE).build(), new FieldEditorDetailsBuilder(DefaultComboFieldEditor.class).labelsAndValues(Preference.getLanguageComboOptions()).build()),
	TIMEZONE(new PreferenceDetailsBuilder(GENERAL).defaultValue(TimeZoneConfigAccessor.DEFAULT_ZONE_ID).build(), new FieldEditorDetailsBuilder(Boolean.TRUE.equals(SwtUtils.isGtk3()) ? ListFieldEditor.class : DefaultComboFieldEditor.class).labelsAndValues(getTimeZoneComboOptions()).height(3).build()), // GTK3 combo rendering is slow when item count is high

	START_MINIMIZED(new PreferenceDetailsBuilder(GENERAL).defaultValue(EarthquakeBulletinGui.Defaults.START_MINIMIZED).separate().build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	MINIMIZE_TRAY(new PreferenceDetailsBuilder(GENERAL).defaultValue(TrayIcon.Defaults.MINIMIZE_TRAY).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).disabled(!SystemTray.isSupported()).build()),
	CONFIRM_CLOSE(new PreferenceDetailsBuilder(GENERAL).defaultValue(CloseDialog.Defaults.CONFIRM_CLOSE).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	SEARCH_ON_START(new PreferenceDetailsBuilder(GENERAL).defaultValue(EarthquakeBulletinGui.Defaults.SEARCH_ON_START).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),

	MAP_ZOOM_LEVEL(new PreferenceDetailsBuilder(GENERAL).defaultValue(MapCanvas.Defaults.MAP_ZOOM_LEVEL).separate().build(), new FieldEditorDetailsBuilder(DefaultComboFieldEditor.class).labelsAndValues(getZoomComboOptions()).build()),
	MAP_RESIZE_HQ(new PreferenceDetailsBuilder(GENERAL).defaultValue(MapCanvas.Defaults.MAP_RESIZE_HQ).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),

	MAGNITUDE_BIG(new PreferenceDetailsBuilder(GENERAL).defaultValue(ResultsTable.Defaults.MAGNITUDE_BIG).separate().build(), new FieldEditorDetailsBuilder(FloatFieldEditor.class).numberValidRange(SearchForm.MAGNITUDE_MIN_VALUE, SearchForm.MAGNITUDE_MAX_VALUE).build()),
	MAGNITUDE_XXL(new PreferenceDetailsBuilder(GENERAL).defaultValue(ResultsTable.Defaults.MAGNITUDE_XXL).build(), new FieldEditorDetailsBuilder(FloatFieldEditor.class).numberValidRange(SearchForm.MAGNITUDE_MIN_VALUE, SearchForm.MAGNITUDE_MAX_VALUE).build()),

	HTTP_CONNECTION_TIMEOUT_MS(new PreferenceDetailsBuilder(CONNECTION).defaultValue(ConnectionFactory.Defaults.CONNECTION_TIMEOUT_IN_MILLIS).build(), new FieldEditorDetailsBuilder(EnhancedIntegerFieldEditor.class).build()),
	HTTP_READ_TIMEOUT_MS(new PreferenceDetailsBuilder(CONNECTION).defaultValue(ConnectionFactory.Defaults.READ_TIMEOUT_IN_MILLIS).build(), new FieldEditorDetailsBuilder(EnhancedIntegerFieldEditor.class).build()),

	PROXY_ENABLED(new PreferenceDetailsBuilder(CONNECTION).defaultValue(ConnectionFactory.Defaults.PROXY_ENABLED).separate().build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	PROXY_MANUAL(new PreferenceDetailsBuilder(CONNECTION).parent(PROXY_ENABLED).defaultValue(ConnectionFactory.Defaults.PROXY_MANUAL).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	PROXY_TYPE(new PreferenceDetailsBuilder(CONNECTION).parent(PROXY_MANUAL).defaultValue(ConnectionFactory.Defaults.PROXY_TYPE.name()).build(), new FieldEditorDetailsBuilder(DefaultComboFieldEditor.class).labelsAndValues(getProxyTypeComboOptions()).boldCustomValues(false).build()),
	PROXY_ADDRESS(new PreferenceDetailsBuilder(CONNECTION).parent(PROXY_MANUAL).defaultValue(ConnectionFactory.Defaults.PROXY_ADDRESS).build(), new FieldEditorDetailsBuilder(EnhancedStringFieldEditor.class).textLimit(253).emptyStringAllowed(false).boldCustomValues(false).build()),
	PROXY_PORT(new PreferenceDetailsBuilder(CONNECTION).parent(PROXY_MANUAL).defaultValue(ConnectionFactory.Defaults.PROXY_PORT).build(), new FieldEditorDetailsBuilder(EnhancedIntegerFieldEditor.class).numberValidRange(1, 65535).boldCustomValues(false).build()),
	PROXY_AUTH_REQUIRED(new PreferenceDetailsBuilder(CONNECTION).parent(PROXY_MANUAL).defaultValue(ConnectionFactory.Defaults.PROXY_ENABLED).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	PROXY_USERNAME(new PreferenceDetailsBuilder(CONNECTION).parent(PROXY_AUTH_REQUIRED).build(), new FieldEditorDetailsBuilder(EnhancedStringFieldEditor.class).build()),
	PROXY_PASSWORD(new PreferenceDetailsBuilder(CONNECTION).parent(PROXY_AUTH_REQUIRED).build(), new FieldEditorDetailsBuilder(PasswordFieldEditor.class).build()),

	CRITERIA_PERIOD_FROM(new PreferenceDetailsBuilder(CRITERIA).label(() -> Messages.get("label.form.criteria.period") + " " + Messages.get("label.form.criteria.period.from") + " " + Messages.get("label.form.criteria.period.from.note")).build(), new FieldEditorDetailsBuilder(DateFieldEditor.class).datePattern(SearchForm.DATE_PATTERN).textLimit(SearchForm.PERIOD_TEXT_LIMIT).build()),
	CRITERIA_PERIOD_TO(new PreferenceDetailsBuilder(CRITERIA).label(() -> Messages.get("label.form.criteria.period") + " " + Messages.get("label.form.criteria.period.to") + " " + Messages.get("label.form.criteria.period.to.note")).build(), new FieldEditorDetailsBuilder(DateFieldEditor.class).datePattern(SearchForm.DATE_PATTERN).textLimit(SearchForm.PERIOD_TEXT_LIMIT).build()),
	CRITERIA_LATITUDE_FROM(new PreferenceDetailsBuilder(CRITERIA).label(() -> Messages.get("label.form.criteria.latitude") + " " + Messages.get("label.form.criteria.latitude.from") + " " + Messages.get("label.form.criteria.latitude.from.note")).build(), new FieldEditorDetailsBuilder(FloatFieldEditor.class).numberValidRange(SearchForm.LATITUDE_MIN_VALUE, SearchForm.LATITUDE_MAX_VALUE).emptyStringAllowed(true).textLimit(SearchForm.COORDINATES_TEXT_LIMIT).build()),
	CRITERIA_LATITUDE_TO(new PreferenceDetailsBuilder(CRITERIA).label(() -> Messages.get("label.form.criteria.latitude") + " " + Messages.get("label.form.criteria.latitude.to") + " " + Messages.get("label.form.criteria.latitude.to.note")).build(), new FieldEditorDetailsBuilder(FloatFieldEditor.class).numberValidRange(SearchForm.LATITUDE_MIN_VALUE, SearchForm.LATITUDE_MAX_VALUE).emptyStringAllowed(true).textLimit(SearchForm.COORDINATES_TEXT_LIMIT).build()),
	CRITERIA_LONGITUDE_FROM(new PreferenceDetailsBuilder(CRITERIA).label(() -> Messages.get("label.form.criteria.longitude") + " " + Messages.get("label.form.criteria.longitude.from") + " " + Messages.get("label.form.criteria.longitude.from.note")).build(), new FieldEditorDetailsBuilder(FloatFieldEditor.class).numberValidRange(SearchForm.LONGITUDE_MIN_VALUE, SearchForm.LONGITUDE_MAX_VALUE).emptyStringAllowed(true).textLimit(SearchForm.COORDINATES_TEXT_LIMIT).build()),
	CRITERIA_LONGITUDE_TO(new PreferenceDetailsBuilder(CRITERIA).label(() -> Messages.get("label.form.criteria.longitude") + " " + Messages.get("label.form.criteria.longitude.to") + " " + Messages.get("label.form.criteria.longitude.to.note")).build(), new FieldEditorDetailsBuilder(FloatFieldEditor.class).numberValidRange(SearchForm.LONGITUDE_MIN_VALUE, SearchForm.LONGITUDE_MAX_VALUE).emptyStringAllowed(true).textLimit(SearchForm.COORDINATES_TEXT_LIMIT).build()),
	CRITERIA_MAGNITUDE(new PreferenceDetailsBuilder(CRITERIA).label(() -> Messages.get("label.form.criteria.magnitude")).build(), new FieldEditorDetailsBuilder(FloatFieldEditor.class).numberValidRange(SearchForm.MAGNITUDE_MIN_VALUE, SearchForm.MAGNITUDE_MAX_VALUE).emptyStringAllowed(true).textLimit(SearchForm.MAGNITUDE_TEXT_LIMIT).build()),
	CRITERIA_FORMAT(new PreferenceDetailsBuilder(CRITERIA).label(() -> Messages.get("label.form.format")).defaultValue(SearchForm.Defaults.FORMAT.getValue()).build(), new FieldEditorDetailsBuilder(DefaultRadioGroupFieldEditor.class).labelsAndValues(getFormatRadioOptions()).radioNumColumns(2).radioUseGroup(true).build()),
	CRITERIA_LIMIT(new PreferenceDetailsBuilder(CRITERIA).label(() -> Messages.get("label.form.limit") + " " + Messages.get("label.form.limit.note")).build(), new FieldEditorDetailsBuilder(EnhancedIntegerFieldEditor.class).emptyStringAllowed(true).numberValidRange(SearchForm.RESULTS_MIN_VALUE, SearchForm.RESULTS_MAX_VALUE).build()),
	CRITERIA_RESTRICT(new PreferenceDetailsBuilder(CRITERIA).defaultValue(SearchForm.Defaults.CRITERIA_RESTRICT).label(() -> Messages.get("label.form.criteria.restrict")).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	AUTOREFRESH_ENABLED(new PreferenceDetailsBuilder(CRITERIA).defaultValue(SearchForm.Defaults.AUTOREFRESH_ENABLED).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	AUTOREFRESH_MINS(new PreferenceDetailsBuilder(CRITERIA).parent(AUTOREFRESH_ENABLED).label(() -> Messages.get("label.form.button.autorefresh")).build(), new FieldEditorDetailsBuilder(EnhancedIntegerFieldEditor.class).numberMinimum(SearchForm.AUTOREFRESH_MIN_VALUE).emptyStringAllowed(true).textLimit(SearchForm.AUTOREFRESH_TEXT_LIMIT).build()),

	MAP_CACHE_SIZE(new PreferenceDetailsBuilder(CACHE).defaultValue(MapImageCache.Defaults.CACHE_SIZE).build(), new FieldEditorDetailsBuilder(ScaleIntegerFieldEditor.class).scaleMinimum(1).scaleMaximum(Byte.MAX_VALUE).scalePageIncrement(8).build()),
	MAP_CACHE_SAVE(new PreferenceDetailsBuilder(CACHE).defaultValue(MapImageCache.Defaults.CACHE_SAVE).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	MT_CACHE_SIZE(new PreferenceDetailsBuilder(CACHE).defaultValue(MomentTensorCache.Defaults.CACHE_SIZE).build(), new FieldEditorDetailsBuilder(ScaleIntegerFieldEditor.class).scaleMinimum(1).scaleMaximum(Byte.MAX_VALUE).scalePageIncrement(8).build()),
	MT_CACHE_SAVE(new PreferenceDetailsBuilder(CACHE).defaultValue(MomentTensorCache.Defaults.CACHE_SAVE).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	MTI_CACHE_SIZE(new PreferenceDetailsBuilder(CACHE).defaultValue(BeachBallCache.Defaults.CACHE_SIZE).build(), new FieldEditorDetailsBuilder(ScaleIntegerFieldEditor.class).scaleMinimum(1).scaleMaximum(Byte.MAX_VALUE).scalePageIncrement(8).build()),
	MTI_CACHE_SAVE(new PreferenceDetailsBuilder(CACHE).defaultValue(BeachBallCache.Defaults.CACHE_SAVE).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),

	LOGGING_CONSOLE_LEVEL(new PreferenceDetailsBuilder(LOGGING).defaultValue(LoggingConfig.Defaults.LOGGING_LEVEL.toString()).build(), new FieldEditorDetailsBuilder(DefaultComboFieldEditor.class).labelsAndValues(getLoggingComboOptions()).build()),
	LOGGING_FILES_ENABLED(new PreferenceDetailsBuilder(LOGGING).separate().defaultValue(LoggingConfig.Defaults.LOGGING_FILES_ENABLED).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	LOGGING_FILES_LEVEL(new PreferenceDetailsBuilder(LOGGING).parent(LOGGING_FILES_ENABLED).defaultValue(LoggingConfig.Defaults.LOGGING_LEVEL.toString()).build(), new FieldEditorDetailsBuilder(DefaultComboFieldEditor.class).labelsAndValues(getLoggingComboOptions()).build()),
	LOGGING_FILES_PATH(new PreferenceDetailsBuilder(LOGGING).parent(LOGGING_FILES_ENABLED).defaultValue(LoggingConfig.Defaults.LOGGING_FILES_PATH).build(), new FieldEditorDetailsBuilder(EnhancedDirectoryFieldEditor.class).emptyStringAllowed(false).directoryMustExist(false).directoryDialogMessage(() -> Messages.get("message.preferences.directory.dialog.message.log")).build()),
	LOGGING_FILES_LIMIT(new PreferenceDetailsBuilder(LOGGING).parent(LOGGING_FILES_ENABLED).defaultValue(LoggingConfig.Defaults.LOGGING_FILES_MAX_SIZE_KB).build(), new FieldEditorDetailsBuilder(ScaleIntegerFieldEditor.class).scaleMinimum(512).scaleMaximum(8192).scalePageIncrement(512).build()),
	LOGGING_FILES_COUNT(new PreferenceDetailsBuilder(LOGGING).parent(LOGGING_FILES_ENABLED).defaultValue(LoggingConfig.Defaults.LOGGING_FILES_MAX_INDEX).build(), new FieldEditorDetailsBuilder(ScaleIntegerFieldEditor.class).scaleMinimum(1).scaleMaximum(9).scalePageIncrement(1).build()),
	LOGGING_FILES_COMPRESSION_ENABLED(new PreferenceDetailsBuilder(LOGGING).parent(LOGGING_FILES_ENABLED).defaultValue(LoggingConfig.Defaults.LOGGING_FILES_COMPRESSION_ENABLED).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),

	GEOFON_BASE_URL(new PreferenceDetailsBuilder(ADVANCED).defaultValue(GeofonUtils.DEFAULT_GEOFON_BASE_URL).build(), new FieldEditorDetailsBuilder(ValidatedComboFieldEditor.class).emptyStringAllowed(false).boldCustomValues(true).labelsAndValues(new StaticLabelsAndValues(GeofonUtils.NEW_GEOFON_BASE_URL, GeofonUtils.NEW_GEOFON_BASE_URL).put(GeofonUtils.OLD_GEOFON_BASE_URL, GeofonUtils.OLD_GEOFON_BASE_URL)).build()),
	HTML_BULLETIN_VERSION(new PreferenceDetailsBuilder(ADVANCED).defaultValue(HtmlBulletinVersion.DEFAULT.name()).build(), new FieldEditorDetailsBuilder(DefaultRadioGroupFieldEditor.class).labelsAndValues(getDecoderRadioOptions()).radioNumColumns(2).radioUseGroup(true).build()),
	MT_MAX_DIALOGS(new PreferenceDetailsBuilder(ADVANCED).defaultValue(MomentTensorDialog.Defaults.MAX_DIALOGS).build(), new FieldEditorDetailsBuilder(ScaleIntegerFieldEditor.class).scaleMinimum(1).scaleMaximum(Byte.MAX_VALUE).scalePageIncrement(10).build()),
	MT_LIMIT_HEIGHT(new PreferenceDetailsBuilder(ADVANCED).defaultValue(MomentTensorDialog.Defaults.LIMIT_HEIGHT).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build());

	private static final String LABEL_KEY_PREFIX = "label.preferences.";

	private static final FieldEditorFactory fieldEditorFactory = new FieldEditorFactory();

	private final PreferenceDetails preferenceDetails;
	private final FieldEditorDetails fieldEditorDetails;

	Preference(final PreferenceDetails preferenceDetails, final FieldEditorDetails fieldEditorDetails) {
		this.preferenceDetails = preferenceDetails;
		this.fieldEditorDetails = fieldEditorDetails;
		if (preferenceDetails.getName() == null) {
			preferenceDetails.setName(name().toLowerCase(Locale.ROOT).replace('_', '.'));
		}
		if (preferenceDetails.getLabel() == null) {
			preferenceDetails.setLabel(() -> Messages.get(LABEL_KEY_PREFIX + preferenceDetails.getName()));
		}
	}

	@Override
	public String getName() {
		return preferenceDetails.getName();
	}

	@Override
	public String getLabel() {
		return preferenceDetails.getLabel().get();
	}

	@Override
	public IPageDefinition getPageDefinition() {
		return preferenceDetails.getPageDefinition();
	}

	@Override
	public String getDefaultValue() {
		return preferenceDetails.getDefaultValue();
	}

	@Override
	public IPreference getParent() {
		return preferenceDetails.getParent();
	}

	@Override
	public boolean isRestartRequired() {
		return preferenceDetails.isRestartRequired();
	}

	@Override
	public boolean isSeparate() {
		return preferenceDetails.isSeparate();
	}

	@Override
	public Preference[] getChildren() {
		final Set<Preference> preferences = EnumSet.noneOf(Preference.class);
		for (final Preference item : Preference.values()) {
			if (this.equals(item.getParent())) {
				preferences.add(item);
			}
		}
		return preferences.toArray(new Preference[] {});
	}

	@Override
	public FieldEditor createFieldEditor(final Composite parent) {
		return fieldEditorFactory.createFieldEditor(getName(), getLabel(), parent, fieldEditorDetails);
	}

	public static LocalizedLabelsAndValues getLanguageComboOptions() {
		final Language[] values = Messages.Language.values();
		final LocalizedLabelsAndValues options = new LocalizedLabelsAndValues(values.length);
		for (final Language language : values) {
			final Locale locale = language.getLocale();
			final String value = locale.getLanguage();
			options.add(() -> locale.getDisplayLanguage(locale), value);
		}
		return options;
	}

	public static LocalizedLabelsAndValues getFormatRadioOptions() {
		final Format[] values = Format.values();
		final LocalizedLabelsAndValues options = new LocalizedLabelsAndValues(values.length);
		for (final Format format : values) {
			final String value = format.getValue();
			options.add(format::getLabel, value);
		}
		return options;
	}

	public static LocalizedLabelsAndValues getDecoderRadioOptions() {
		final HtmlBulletinVersion[] values = HtmlBulletinVersion.values();
		final LocalizedLabelsAndValues options = new LocalizedLabelsAndValues(values.length);
		for (final HtmlBulletinVersion version : values) {
			final String value = version.name();
			options.add(version::getLabel, value);
		}
		return options;
	}

	public static LocalizedLabelsAndValues getZoomComboOptions() {
		final Collection<Integer> values = MapCanvas.getZoomLevels();
		final LocalizedLabelsAndValues options = new LocalizedLabelsAndValues(values.size());
		for (final int level : values) {
			final String value = Integer.toString(level);
			options.add(() -> Messages.get("label.preferences.map.zoom.level." + (level == 0 ? "auto" : "custom"), value), value);
		}
		return options;
	}

	public static StaticLabelsAndValues getLoggingComboOptions() {
		final StaticLabelsAndValues options = new StaticLabelsAndValues(LoggingLevel.values().length);
		for (final LoggingLevel level : LoggingLevel.values()) {
			options.put(level.toString(), level.toString());
		}
		return options;
	}

	public static StaticLabelsAndValues getTimeZoneComboOptions() {
		final Collection<String> zones = new TreeSet<>(ZoneId.getAvailableZoneIds());
		final StaticLabelsAndValues options = new StaticLabelsAndValues(zones.size());
		for (final String zone : zones) {
			options.put(zone, zone);
		}
		return options;
	}

	public static StaticLabelsAndValues getProxyTypeComboOptions() {
		final Proxy.Type[] types = Proxy.Type.values();
		final StaticLabelsAndValues options = new StaticLabelsAndValues(types.length - 1);
		for (final Proxy.Type type : types) {
			if (!Proxy.Type.DIRECT.equals(type)) {
				options.put(type.toString(), type.name());
			}
		}
		return options;
	}

}
