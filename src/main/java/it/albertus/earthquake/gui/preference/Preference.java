package it.albertus.earthquake.gui.preference;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.widgets.Composite;

import it.albertus.earthquake.config.EarthquakeBulletinConfiguration;
import it.albertus.earthquake.gui.CloseDialog;
import it.albertus.earthquake.gui.EarthquakeBulletinGui;
import it.albertus.earthquake.gui.MapCache;
import it.albertus.earthquake.gui.SearchForm;
import it.albertus.earthquake.gui.TrayIcon;
import it.albertus.earthquake.gui.listener.MapCanvasPaintListener;
import it.albertus.earthquake.model.Format;
import it.albertus.earthquake.net.HttpConnector;
import it.albertus.earthquake.resources.Messages;
import it.albertus.earthquake.resources.Messages.Language;
import it.albertus.jface.preference.FieldEditorDetails;
import it.albertus.jface.preference.FieldEditorDetails.FieldEditorDetailsBuilder;
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
import it.albertus.jface.preference.field.FloatFieldEditor;
import it.albertus.jface.preference.field.ScaleIntegerFieldEditor;
import it.albertus.jface.preference.page.IPageDefinition;
import it.albertus.util.Localized;
import it.albertus.util.logging.LoggingSupport;

public enum Preference implements IPreference {

	LANGUAGE(new PreferenceDetailsBuilder(PageDefinition.GENERAL).defaultValue(Messages.Defaults.LANGUAGE).build(), new FieldEditorDetailsBuilder(DefaultComboFieldEditor.class).labelsAndValues(Preference.getLanguageComboOptions()).build()),

	START_MINIMIZED(new PreferenceDetailsBuilder(PageDefinition.GENERAL).defaultValue(EarthquakeBulletinGui.Defaults.START_MINIMIZED).separate().build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	MINIMIZE_TRAY(new PreferenceDetailsBuilder(PageDefinition.GENERAL).defaultValue(TrayIcon.Defaults.MINIMIZE_TRAY).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	CONFIRM_CLOSE(new PreferenceDetailsBuilder(PageDefinition.GENERAL).defaultValue(CloseDialog.Defaults.CONFIRM_CLOSE).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	SEARCH_ON_START(new PreferenceDetailsBuilder(PageDefinition.GENERAL).defaultValue(EarthquakeBulletinGui.Defaults.SEARCH_ON_START).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),

	MAP_RESIZE_HQ(new PreferenceDetailsBuilder(PageDefinition.GENERAL).defaultValue(MapCanvasPaintListener.Defaults.MAP_RESIZE_HQ).separate().build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	MAP_CACHE_SIZE(new PreferenceDetailsBuilder(PageDefinition.GENERAL).defaultValue(MapCache.Defaults.CACHE_SIZE).build(), new FieldEditorDetailsBuilder(ScaleIntegerFieldEditor.class).scaleMinimum(1).scaleMaximum(Byte.MAX_VALUE).scalePageIncrement(8).build()),

	HTTP_CONNECTION_TIMEOUT_MS(new PreferenceDetailsBuilder(PageDefinition.CONNECTION).defaultValue(HttpConnector.Defaults.CONNECTION_TIMEOUT_IN_MILLIS).build(), new FieldEditorDetailsBuilder(EnhancedIntegerFieldEditor.class).build()),
	HTTP_READ_TIMEOUT_MS(new PreferenceDetailsBuilder(PageDefinition.CONNECTION).defaultValue(HttpConnector.Defaults.READ_TIMEOUT_IN_MILLIS).build(), new FieldEditorDetailsBuilder(EnhancedIntegerFieldEditor.class).build()),

	CRITERIA_PERIOD_FROM(new PreferenceDetailsBuilder(PageDefinition.CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.criteria.period") + " " + Messages.get("lbl.form.criteria.period.from") + " " + Messages.get("lbl.form.criteria.period.from.note");
		}
	}).build(), new FieldEditorDetailsBuilder(DateFieldEditor.class).datePattern(SearchForm.DATE_PATTERN).textLimit(SearchForm.PERIOD_TEXT_LIMIT).build()),
	CRITERIA_PERIOD_TO(new PreferenceDetailsBuilder(PageDefinition.CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.criteria.period") + " " + Messages.get("lbl.form.criteria.period.to") + " " + Messages.get("lbl.form.criteria.period.to.note");
		}
	}).build(), new FieldEditorDetailsBuilder(DateFieldEditor.class).datePattern(SearchForm.DATE_PATTERN).textLimit(SearchForm.PERIOD_TEXT_LIMIT).build()),
	CRITERIA_LATITUDE_FROM(new PreferenceDetailsBuilder(PageDefinition.CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.criteria.latitude") + " " + Messages.get("lbl.form.criteria.latitude.from") + " " + Messages.get("lbl.form.criteria.latitude.from.note");
		}
	}).build(), new FieldEditorDetailsBuilder(FloatFieldEditor.class).numberValidRange(SearchForm.LATITUDE_MIN_VALUE, SearchForm.LATITUDE_MAX_VALUE).emptyStringAllowed(true).textLimit(SearchForm.COORDINATES_TEXT_LIMIT).build()),
	CRITERIA_LATITUDE_TO(new PreferenceDetailsBuilder(PageDefinition.CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.criteria.latitude") + " " + Messages.get("lbl.form.criteria.latitude.to") + " " + Messages.get("lbl.form.criteria.latitude.to.note");
		}
	}).build(), new FieldEditorDetailsBuilder(FloatFieldEditor.class).numberValidRange(SearchForm.LATITUDE_MIN_VALUE, SearchForm.LATITUDE_MAX_VALUE).emptyStringAllowed(true).textLimit(SearchForm.COORDINATES_TEXT_LIMIT).build()),
	CRITERIA_LONGITUDE_FROM(new PreferenceDetailsBuilder(PageDefinition.CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.criteria.longitude") + " " + Messages.get("lbl.form.criteria.longitude.from") + " " + Messages.get("lbl.form.criteria.longitude.from.note");
		}
	}).build(), new FieldEditorDetailsBuilder(FloatFieldEditor.class).numberValidRange(SearchForm.LONGITUDE_MIN_VALUE, SearchForm.LONGITUDE_MAX_VALUE).emptyStringAllowed(true).textLimit(SearchForm.COORDINATES_TEXT_LIMIT).build()),
	CRITERIA_LONGITUDE_TO(new PreferenceDetailsBuilder(PageDefinition.CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.criteria.longitude") + " " + Messages.get("lbl.form.criteria.longitude.to") + " " + Messages.get("lbl.form.criteria.longitude.to.note");
		}
	}).build(), new FieldEditorDetailsBuilder(FloatFieldEditor.class).numberValidRange(SearchForm.LONGITUDE_MIN_VALUE, SearchForm.LONGITUDE_MAX_VALUE).emptyStringAllowed(true).textLimit(SearchForm.COORDINATES_TEXT_LIMIT).build()),
	CRITERIA_MAGNITUDE(new PreferenceDetailsBuilder(PageDefinition.CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.criteria.magnitude");
		}
	}).build(), new FieldEditorDetailsBuilder(FloatFieldEditor.class).numberValidRange(SearchForm.MAGNITUDE_MIN_VALUE, SearchForm.MAGNITUDE_MAX_VALUE).emptyStringAllowed(true).textLimit(SearchForm.MAGNITUDE_TEXT_LIMIT).build()),
	CRITERIA_FORMAT(new PreferenceDetailsBuilder(PageDefinition.CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.format");
		}
	}).defaultValue(SearchForm.Defaults.FORMAT.getValue()).build(), new FieldEditorDetailsBuilder(DefaultRadioGroupFieldEditor.class).labelsAndValues(getFormatRadioOptions()).radioNumColumns(2).radioUseGroup(true).build()),
	CRITERIA_LIMIT(new PreferenceDetailsBuilder(PageDefinition.CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.limit") + " " + Messages.get("lbl.form.limit.note");
		}
	}).build(), new FieldEditorDetailsBuilder(EnhancedIntegerFieldEditor.class).emptyStringAllowed(true).numberValidRange(SearchForm.RESULTS_MIN_VALUE, SearchForm.RESULTS_MAX_VALUE).build()),
	CRITERIA_RESTRICT(new PreferenceDetailsBuilder(PageDefinition.CRITERIA).defaultValue(SearchForm.Defaults.CRITERIA_RESTRICT).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.criteria.restrict");
		}
	}).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	AUTOREFRESH_ENABLED(new PreferenceDetailsBuilder(PageDefinition.CRITERIA).defaultValue(SearchForm.Defaults.AUTOREFRESH_ENABLED).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	AUTOREFRESH_MINS(new PreferenceDetailsBuilder(PageDefinition.CRITERIA).parent(AUTOREFRESH_ENABLED).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.button.autorefresh");
		}
	}).build(), new FieldEditorDetailsBuilder(EnhancedIntegerFieldEditor.class).numberMinimum(SearchForm.AUTOREFRESH_MIN_VALUE).emptyStringAllowed(true).textLimit(SearchForm.AUTOREFRESH_TEXT_LIMIT).build()),

	LOGGING_LEVEL(new PreferenceDetailsBuilder(PageDefinition.LOGGING).defaultValue(EarthquakeBulletinConfiguration.Defaults.LOGGING_LEVEL.intValue()).build(), new FieldEditorDetailsBuilder(DefaultComboFieldEditor.class).labelsAndValues(getLoggingComboOptions()).build()),
	LOGGING_FILES_PATH(new PreferenceDetailsBuilder(PageDefinition.LOGGING).defaultValue(EarthquakeBulletinConfiguration.Defaults.LOGGING_FILES_PATH).build(), new FieldEditorDetailsBuilder(EnhancedDirectoryFieldEditor.class).emptyStringAllowed(false).directoryMustExist(false).directoryDialogMessage(new Localized() {
		@Override
		public String getString() {
			return Messages.get("msg.preferences.directory.dialog.message.log");
		}
	}).build()),
	LOGGING_FILES_LIMIT(new PreferenceDetailsBuilder(PageDefinition.LOGGING).defaultValue(EarthquakeBulletinConfiguration.Defaults.LOGGING_FILES_LIMIT).build(), new FieldEditorDetailsBuilder(ScaleIntegerFieldEditor.class).scaleMinimum(512).scaleMaximum(8192).scalePageIncrement(512).build()),
	LOGGING_FILES_COUNT(new PreferenceDetailsBuilder(PageDefinition.LOGGING).defaultValue(EarthquakeBulletinConfiguration.Defaults.LOGGING_FILES_COUNT).build(), new FieldEditorDetailsBuilder(ScaleIntegerFieldEditor.class).scaleMinimum(1).scaleMaximum(9).scalePageIncrement(1).build());

	private static final String LABEL_KEY_PREFIX = "lbl.preferences.";

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
		return preferenceDetails.getLabel().getString();
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
	public Set<? extends IPreference> getChildren() {
		final Set<Preference> preferences = EnumSet.noneOf(Preference.class);
		for (final Preference item : Preference.values()) {
			if (this.equals(item.getParent())) {
				preferences.add(item);
			}
		}
		return preferences;
	}

	@Override
	public FieldEditor createFieldEditor(final Composite parent) {
		return fieldEditorFactory.createFieldEditor(getName(), getLabel(), parent, fieldEditorDetails);
	}

	public static IPreference forName(final String name) {
		if (name != null) {
			for (final IPreference preference : Preference.values()) {
				if (name.equals(preference.getName())) {
					return preference;
				}
			}
		}
		return null;
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
			options.put(name, value);
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
			options.put(name, value);
		}
		return options;
	}

	public static StaticLabelsAndValues getLoggingComboOptions() {
		final Map<Integer, Level> levels = LoggingSupport.getLevels();
		final StaticLabelsAndValues options = new StaticLabelsAndValues(levels.size());
		for (final Entry<Integer, Level> entry : levels.entrySet()) {
			options.put(entry.getValue().getName(), entry.getKey().toString());
		}
		return options;
	}

}
