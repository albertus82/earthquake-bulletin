package it.albertus.eqbulletin.gui.preference;

import static it.albertus.eqbulletin.gui.preference.PageDefinition.CONNECTION;
import static it.albertus.eqbulletin.gui.preference.PageDefinition.CRITERIA;
import static it.albertus.eqbulletin.gui.preference.PageDefinition.GENERAL;
import static it.albertus.eqbulletin.gui.preference.PageDefinition.LOGGING;

import java.net.Proxy.Type;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.util.Util;
import org.eclipse.swt.widgets.Composite;

import it.albertus.eqbulletin.EarthquakeBulletin;
import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.CloseDialog;
import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.MapCanvas;
import it.albertus.eqbulletin.gui.ResultsTable;
import it.albertus.eqbulletin.gui.SearchForm;
import it.albertus.eqbulletin.gui.TrayIcon;
import it.albertus.eqbulletin.model.Format;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.resources.Messages.Language;
import it.albertus.eqbulletin.service.MapCache;
import it.albertus.eqbulletin.service.net.ConnectionFactory;
import it.albertus.jface.preference.FieldEditorDetails;
import it.albertus.jface.preference.FieldEditorDetails.FieldEditorDetailsBuilder;
import it.albertus.jface.preference.FieldEditorFactory;
import it.albertus.jface.preference.IPreference;
import it.albertus.jface.preference.LocalizedLabelsAndValues;
import it.albertus.jface.preference.PreferenceDetails;
import it.albertus.jface.preference.PreferenceDetails.PreferenceDetailsBuilder;
import it.albertus.jface.preference.StaticLabelsAndValues;
import it.albertus.jface.preference.field.DateFieldEditor;
import it.albertus.jface.preference.field.DefaultBooleanFieldEditor;
import it.albertus.jface.preference.field.DefaultComboFieldEditor;
import it.albertus.jface.preference.field.DefaultRadioGroupFieldEditor;
import it.albertus.jface.preference.field.EnhancedDirectoryFieldEditor;
import it.albertus.jface.preference.field.EnhancedIntegerFieldEditor;
import it.albertus.jface.preference.field.EnhancedStringFieldEditor;
import it.albertus.jface.preference.field.FloatFieldEditor;
import it.albertus.jface.preference.field.ListFieldEditor;
import it.albertus.jface.preference.field.PasswordFieldEditor;
import it.albertus.jface.preference.field.ScaleIntegerFieldEditor;
import it.albertus.jface.preference.page.IPageDefinition;
import it.albertus.util.Localized;
import it.albertus.util.logging.LoggingSupport;

public enum Preference implements IPreference {

	LANGUAGE(new PreferenceDetailsBuilder(GENERAL).defaultValue(Messages.Defaults.LANGUAGE).build(), new FieldEditorDetailsBuilder(DefaultComboFieldEditor.class).labelsAndValues(Preference.getLanguageComboOptions()).build()),
	TIMEZONE(new PreferenceDetailsBuilder(GENERAL).defaultValue(EarthquakeBulletin.Defaults.TIME_ZONE_ID).build(), new FieldEditorDetailsBuilder(Util.isGtk() ? ListFieldEditor.class : DefaultComboFieldEditor.class).labelsAndValues(getTimeZoneComboOptions()).height(3).build()), // GTK3 combo rendering is slow when item count is high

	START_MINIMIZED(new PreferenceDetailsBuilder(GENERAL).defaultValue(EarthquakeBulletinGui.Defaults.START_MINIMIZED).separate().build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	MINIMIZE_TRAY(new PreferenceDetailsBuilder(GENERAL).defaultValue(TrayIcon.Defaults.MINIMIZE_TRAY).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	CONFIRM_CLOSE(new PreferenceDetailsBuilder(GENERAL).defaultValue(CloseDialog.Defaults.CONFIRM_CLOSE).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	SEARCH_ON_START(new PreferenceDetailsBuilder(GENERAL).defaultValue(EarthquakeBulletinGui.Defaults.SEARCH_ON_START).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),

	MAP_ZOOM_LEVEL(new PreferenceDetailsBuilder(GENERAL).defaultValue(MapCanvas.Defaults.MAP_ZOOM_LEVEL).separate().build(), new FieldEditorDetailsBuilder(DefaultComboFieldEditor.class).labelsAndValues(getZoomComboOptions()).build()),
	MAP_RESIZE_HQ(new PreferenceDetailsBuilder(GENERAL).defaultValue(MapCanvas.Defaults.MAP_RESIZE_HQ).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	MAP_CACHE_SIZE(new PreferenceDetailsBuilder(GENERAL).defaultValue(MapCache.Defaults.CACHE_SIZE).build(), new FieldEditorDetailsBuilder(ScaleIntegerFieldEditor.class).scaleMinimum(1).scaleMaximum(Byte.MAX_VALUE).scalePageIncrement(8).build()),

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

	CRITERIA_PERIOD_FROM(new PreferenceDetailsBuilder(CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.criteria.period") + " " + Messages.get("lbl.form.criteria.period.from") + " " + Messages.get("lbl.form.criteria.period.from.note");
		}
	}).build(), new FieldEditorDetailsBuilder(DateFieldEditor.class).datePattern(SearchForm.DATE_PATTERN).textLimit(SearchForm.PERIOD_TEXT_LIMIT).build()),
	CRITERIA_PERIOD_TO(new PreferenceDetailsBuilder(CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.criteria.period") + " " + Messages.get("lbl.form.criteria.period.to") + " " + Messages.get("lbl.form.criteria.period.to.note");
		}
	}).build(), new FieldEditorDetailsBuilder(DateFieldEditor.class).datePattern(SearchForm.DATE_PATTERN).textLimit(SearchForm.PERIOD_TEXT_LIMIT).build()),
	CRITERIA_LATITUDE_FROM(new PreferenceDetailsBuilder(CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.criteria.latitude") + " " + Messages.get("lbl.form.criteria.latitude.from") + " " + Messages.get("lbl.form.criteria.latitude.from.note");
		}
	}).build(), new FieldEditorDetailsBuilder(FloatFieldEditor.class).numberValidRange(SearchForm.LATITUDE_MIN_VALUE, SearchForm.LATITUDE_MAX_VALUE).emptyStringAllowed(true).textLimit(SearchForm.COORDINATES_TEXT_LIMIT).build()),
	CRITERIA_LATITUDE_TO(new PreferenceDetailsBuilder(CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.criteria.latitude") + " " + Messages.get("lbl.form.criteria.latitude.to") + " " + Messages.get("lbl.form.criteria.latitude.to.note");
		}
	}).build(), new FieldEditorDetailsBuilder(FloatFieldEditor.class).numberValidRange(SearchForm.LATITUDE_MIN_VALUE, SearchForm.LATITUDE_MAX_VALUE).emptyStringAllowed(true).textLimit(SearchForm.COORDINATES_TEXT_LIMIT).build()),
	CRITERIA_LONGITUDE_FROM(new PreferenceDetailsBuilder(CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.criteria.longitude") + " " + Messages.get("lbl.form.criteria.longitude.from") + " " + Messages.get("lbl.form.criteria.longitude.from.note");
		}
	}).build(), new FieldEditorDetailsBuilder(FloatFieldEditor.class).numberValidRange(SearchForm.LONGITUDE_MIN_VALUE, SearchForm.LONGITUDE_MAX_VALUE).emptyStringAllowed(true).textLimit(SearchForm.COORDINATES_TEXT_LIMIT).build()),
	CRITERIA_LONGITUDE_TO(new PreferenceDetailsBuilder(CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.criteria.longitude") + " " + Messages.get("lbl.form.criteria.longitude.to") + " " + Messages.get("lbl.form.criteria.longitude.to.note");
		}
	}).build(), new FieldEditorDetailsBuilder(FloatFieldEditor.class).numberValidRange(SearchForm.LONGITUDE_MIN_VALUE, SearchForm.LONGITUDE_MAX_VALUE).emptyStringAllowed(true).textLimit(SearchForm.COORDINATES_TEXT_LIMIT).build()),
	CRITERIA_MAGNITUDE(new PreferenceDetailsBuilder(CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.criteria.magnitude");
		}
	}).build(), new FieldEditorDetailsBuilder(FloatFieldEditor.class).numberValidRange(SearchForm.MAGNITUDE_MIN_VALUE, SearchForm.MAGNITUDE_MAX_VALUE).emptyStringAllowed(true).textLimit(SearchForm.MAGNITUDE_TEXT_LIMIT).build()),
	CRITERIA_FORMAT(new PreferenceDetailsBuilder(CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.format");
		}
	}).defaultValue(SearchForm.Defaults.FORMAT.getValue()).build(), new FieldEditorDetailsBuilder(DefaultRadioGroupFieldEditor.class).labelsAndValues(getFormatRadioOptions()).radioNumColumns(2).radioUseGroup(true).build()),
	CRITERIA_LIMIT(new PreferenceDetailsBuilder(CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.limit") + " " + Messages.get("lbl.form.limit.note");
		}
	}).build(), new FieldEditorDetailsBuilder(EnhancedIntegerFieldEditor.class).emptyStringAllowed(true).numberValidRange(SearchForm.RESULTS_MIN_VALUE, SearchForm.RESULTS_MAX_VALUE).build()),
	CRITERIA_RESTRICT(new PreferenceDetailsBuilder(CRITERIA).defaultValue(SearchForm.Defaults.CRITERIA_RESTRICT).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.criteria.restrict");
		}
	}).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	AUTOREFRESH_ENABLED(new PreferenceDetailsBuilder(CRITERIA).defaultValue(SearchForm.Defaults.AUTOREFRESH_ENABLED).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	AUTOREFRESH_MINS(new PreferenceDetailsBuilder(CRITERIA).parent(AUTOREFRESH_ENABLED).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.button.autorefresh");
		}
	}).build(), new FieldEditorDetailsBuilder(EnhancedIntegerFieldEditor.class).numberMinimum(SearchForm.AUTOREFRESH_MIN_VALUE).emptyStringAllowed(true).textLimit(SearchForm.AUTOREFRESH_TEXT_LIMIT).build()),

	LOGGING_LEVEL(new PreferenceDetailsBuilder(LOGGING).defaultValue(EarthquakeBulletinConfig.Defaults.LOGGING_LEVEL.getName()).build(), new FieldEditorDetailsBuilder(DefaultComboFieldEditor.class).labelsAndValues(getLoggingComboOptions()).build()),
	LOGGING_FILES_ENABLED(new PreferenceDetailsBuilder(LOGGING).separate().defaultValue(EarthquakeBulletinConfig.Defaults.LOGGING_FILES_ENABLED).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	LOGGING_FILES_PATH(new PreferenceDetailsBuilder(LOGGING).parent(LOGGING_FILES_ENABLED).defaultValue(EarthquakeBulletinConfig.Defaults.LOGGING_FILES_PATH).build(), new FieldEditorDetailsBuilder(EnhancedDirectoryFieldEditor.class).emptyStringAllowed(false).directoryMustExist(false).directoryDialogMessage(new Localized() {
		@Override
		public String getString() {
			return Messages.get("msg.preferences.directory.dialog.message.log");
		}
	}).build()),
	LOGGING_FILES_LIMIT(new PreferenceDetailsBuilder(LOGGING).parent(LOGGING_FILES_ENABLED).defaultValue(EarthquakeBulletinConfig.Defaults.LOGGING_FILES_LIMIT).build(), new FieldEditorDetailsBuilder(ScaleIntegerFieldEditor.class).scaleMinimum(512).scaleMaximum(8192).scalePageIncrement(512).build()),
	LOGGING_FILES_COUNT(new PreferenceDetailsBuilder(LOGGING).parent(LOGGING_FILES_ENABLED).defaultValue(EarthquakeBulletinConfig.Defaults.LOGGING_FILES_COUNT).build(), new FieldEditorDetailsBuilder(ScaleIntegerFieldEditor.class).scaleMinimum(1).scaleMaximum(9).scalePageIncrement(1).build());

	private static final String LABEL_KEY_PREFIX = "lbl.preferences.";

	private static final FieldEditorFactory fieldEditorFactory = new FieldEditorFactory();

	private final PreferenceDetails preferenceDetails;
	private final FieldEditorDetails fieldEditorDetails;

	Preference(final PreferenceDetails preferenceDetails, final FieldEditorDetails fieldEditorDetails) {
		this.preferenceDetails = preferenceDetails;
		this.fieldEditorDetails = fieldEditorDetails;
		if (preferenceDetails.getName() == null) {
			preferenceDetails.setName(name().toLowerCase().replace('_', '.'));
		}
		if (preferenceDetails.getLabel() == null) {
			preferenceDetails.setLabel(new Localized() {
				@Override
				public String getString() {
					return Messages.get(LABEL_KEY_PREFIX + preferenceDetails.getName());
				}
			});
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
			final Localized name = new Localized() {
				@Override
				public String getString() {
					return locale.getDisplayLanguage(locale);
				}
			};
			options.add(name, value);
		}
		return options;
	}

	public static LocalizedLabelsAndValues getFormatRadioOptions() {
		final Format[] values = Format.values();
		final LocalizedLabelsAndValues options = new LocalizedLabelsAndValues(values.length);
		for (final Format format : values) {
			final String value = format.getValue();
			final Localized name = new Localized() {
				@Override
				public String getString() {
					return format.getLabel();
				}
			};
			options.add(name, value);
		}
		return options;
	}

	public static LocalizedLabelsAndValues getZoomComboOptions() {
		final short[] values = MapCanvas.getZoomLevels();
		final LocalizedLabelsAndValues options = new LocalizedLabelsAndValues(values.length);
		for (final int level : values) {
			final String value = Integer.toString(level);
			final Localized name = new Localized() {
				@Override
				public String getString() {
					return Messages.get("lbl.preferences.map.zoom.level." + (level == 0 ? "auto" : "custom"), value);
				}
			};
			options.add(name, value);
		}
		return options;
	}

	public static StaticLabelsAndValues getLoggingComboOptions() {
		final Map<Integer, Level> levels = LoggingSupport.getLevels();
		final StaticLabelsAndValues options = new StaticLabelsAndValues(levels.size());
		for (final Level level : levels.values()) {
			options.put(level.toString(), level.getName());
		}
		return options;
	}

	public static StaticLabelsAndValues getTimeZoneComboOptions() {
		final String[] zones = TimeZone.getAvailableIDs();
		Arrays.sort(zones);
		final StaticLabelsAndValues options = new StaticLabelsAndValues(zones.length);
		for (final String zone : zones) {
			options.put(zone, zone);
		}
		return options;
	}

	public static StaticLabelsAndValues getProxyTypeComboOptions() {
		final Type[] types = Type.values();
		final StaticLabelsAndValues options = new StaticLabelsAndValues(types.length - 1);
		for (final Type type : types) {
			if (!Type.DIRECT.equals(type)) {
				options.put(type.toString(), type.name());
			}
		}
		return options;
	}

}
