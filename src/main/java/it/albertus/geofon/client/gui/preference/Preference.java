package it.albertus.geofon.client.gui.preference;

import it.albertus.geofon.client.GeofonClient;
import it.albertus.geofon.client.gui.CloseMessageBox;
import it.albertus.geofon.client.gui.GeofonClientGui;
import it.albertus.geofon.client.gui.MapCache;
import it.albertus.geofon.client.gui.SearchForm;
import it.albertus.geofon.client.gui.TrayIcon;
import it.albertus.geofon.client.gui.listener.MapCanvasPaintListener;
import it.albertus.geofon.client.model.Format;
import it.albertus.geofon.client.net.HttpConnector;
import it.albertus.geofon.client.resources.Messages;
import it.albertus.geofon.client.resources.Messages.Language;
import it.albertus.jface.preference.FieldEditorDetails;
import it.albertus.jface.preference.FieldEditorDetails.FieldEditorDetailsBuilder;
import it.albertus.jface.preference.IPreference;
import it.albertus.jface.preference.LocalizedLabelsAndValues;
import it.albertus.jface.preference.PreferenceDetails;
import it.albertus.jface.preference.PreferenceDetails.PreferenceDetailsBuilder;
import it.albertus.jface.preference.field.DefaultBooleanFieldEditor;
import it.albertus.jface.preference.field.DefaultFloatFieldEditor;
import it.albertus.jface.preference.field.DefaultIntegerFieldEditor;
import it.albertus.jface.preference.field.DefaultRadioGroupFieldEditor;
import it.albertus.jface.preference.field.ScaleIntegerFieldEditor;
import it.albertus.jface.preference.page.IPageDefinition;
import it.albertus.util.Localized;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;

public enum Preference implements IPreference {

	LANGUAGE(new PreferenceDetailsBuilder(PageDefinition.GENERAL).name(GeofonClient.CFG_KEY_LANGUAGE).defaultValue(GeofonClient.Defaults.LANGUAGE).build(), new FieldEditorDetailsBuilder(ComboFieldEditor.class).labelsAndValues(Preference.getLanguageComboOptions()).build()),

	START_MINIMIZED(new PreferenceDetailsBuilder(PageDefinition.GENERAL).defaultValue(GeofonClientGui.Defaults.START_MINIMIZED).separate().build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	MINIMIZE_TRAY(new PreferenceDetailsBuilder(PageDefinition.GENERAL).defaultValue(TrayIcon.Defaults.MINIMIZE_TRAY).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	CONFIRM_CLOSE(new PreferenceDetailsBuilder(PageDefinition.GENERAL).defaultValue(CloseMessageBox.Defaults.CONFIRM_CLOSE).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	SEARCH_ON_START(new PreferenceDetailsBuilder(PageDefinition.GENERAL).defaultValue(GeofonClientGui.Defaults.SEARCH_ON_START).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),

	MAP_RESIZE_HQ(new PreferenceDetailsBuilder(PageDefinition.GENERAL).defaultValue(MapCanvasPaintListener.Defaults.MAP_RESIZE_HQ).separate().build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	MAP_CACHE_SIZE(new PreferenceDetailsBuilder(PageDefinition.GENERAL).defaultValue(MapCache.Defaults.CACHE_SIZE).build(), new FieldEditorDetailsBuilder(ScaleIntegerFieldEditor.class).scaleMinimum(1).scaleMaximum(Byte.MAX_VALUE).scalePageIncrement(8).build()),

	HTTP_CONNECTION_TIMEOUT_MS(new PreferenceDetailsBuilder(PageDefinition.CONNECTION).defaultValue(HttpConnector.Defaults.CONNECTION_TIMEOUT_IN_MILLIS).build(), new FieldEditorDetailsBuilder(DefaultIntegerFieldEditor.class).build()),
	HTTP_READ_TIMEOUT_MS(new PreferenceDetailsBuilder(PageDefinition.CONNECTION).defaultValue(HttpConnector.Defaults.READ_TIMEOUT_IN_MILLIS).build(), new FieldEditorDetailsBuilder(DefaultIntegerFieldEditor.class).build()),

	CRITERIA_PERIOD_FROM(new PreferenceDetailsBuilder(PageDefinition.CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.criteria.period") + " " + Messages.get("lbl.form.criteria.period.from") + " " + Messages.get("lbl.form.criteria.period.from.note");
		};
	}).build(), new FieldEditorDetailsBuilder(StringFieldEditor.class).textLimit(SearchForm.PERIOD_TEXT_LIMIT).build()),
	CRITERIA_PERIOD_TO(new PreferenceDetailsBuilder(PageDefinition.CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.criteria.period") + " " + Messages.get("lbl.form.criteria.period.to") + " " + Messages.get("lbl.form.criteria.period.to.note");
		};
	}).build(), new FieldEditorDetailsBuilder(StringFieldEditor.class).textLimit(SearchForm.PERIOD_TEXT_LIMIT).build()),
	CRITERIA_LATITUDE_FROM(new PreferenceDetailsBuilder(PageDefinition.CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.criteria.latitude") + " " + Messages.get("lbl.form.criteria.latitude.from") + " " + Messages.get("lbl.form.criteria.latitude.from.note");
		};
	}).build(), new FieldEditorDetailsBuilder(DefaultFloatFieldEditor.class).numberValidRange(SearchForm.LATITUDE_MIN_VALUE, SearchForm.LATITUDE_MAX_VALUE).emptyStringAllowed(true).textLimit(SearchForm.COORDINATES_TEXT_LIMIT).build()),
	CRITERIA_LATITUDE_TO(new PreferenceDetailsBuilder(PageDefinition.CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.criteria.latitude") + " " + Messages.get("lbl.form.criteria.latitude.to") + " " + Messages.get("lbl.form.criteria.latitude.to.note");
		};
	}).build(), new FieldEditorDetailsBuilder(DefaultFloatFieldEditor.class).numberValidRange(SearchForm.LATITUDE_MIN_VALUE, SearchForm.LATITUDE_MAX_VALUE).emptyStringAllowed(true).textLimit(SearchForm.COORDINATES_TEXT_LIMIT).build()),
	CRITERIA_LONGITUDE_FROM(new PreferenceDetailsBuilder(PageDefinition.CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.criteria.longitude") + " " + Messages.get("lbl.form.criteria.longitude.from") + " " + Messages.get("lbl.form.criteria.longitude.from.note");
		};
	}).build(), new FieldEditorDetailsBuilder(DefaultFloatFieldEditor.class).numberValidRange(SearchForm.LONGITUDE_MIN_VALUE, SearchForm.LONGITUDE_MAX_VALUE).emptyStringAllowed(true).textLimit(SearchForm.COORDINATES_TEXT_LIMIT).build()),
	CRITERIA_LONGITUDE_TO(new PreferenceDetailsBuilder(PageDefinition.CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.criteria.longitude") + " " + Messages.get("lbl.form.criteria.longitude.to") + " " + Messages.get("lbl.form.criteria.longitude.to.note");
		};
	}).build(), new FieldEditorDetailsBuilder(DefaultFloatFieldEditor.class).numberValidRange(SearchForm.LONGITUDE_MIN_VALUE, SearchForm.LONGITUDE_MAX_VALUE).emptyStringAllowed(true).textLimit(SearchForm.COORDINATES_TEXT_LIMIT).build()),
	CRITERIA_MAGNITUDE(new PreferenceDetailsBuilder(PageDefinition.CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.criteria.magnitude");
		};
	}).build(), new FieldEditorDetailsBuilder(DefaultFloatFieldEditor.class).numberValidRange(SearchForm.MAGNITUDE_MIN_VALUE, SearchForm.MAGNITUDE_MAX_VALUE).emptyStringAllowed(true).textLimit(SearchForm.MAGNITUDE_TEXT_LIMIT).build()),
	CRITERIA_FORMAT(new PreferenceDetailsBuilder(PageDefinition.CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.format");
		};
	}).defaultValue(SearchForm.Defaults.FORMAT).build(), new FieldEditorDetailsBuilder(DefaultRadioGroupFieldEditor.class).labelsAndValues(getFormatRadioOptions()).radioNumColumns(2).radioUseGroup(true).build()),
	CRITERIA_LIMIT(new PreferenceDetailsBuilder(PageDefinition.CRITERIA).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.limit") + " " + Messages.get("lbl.form.limit.note");
		};
	}).build(), new FieldEditorDetailsBuilder(DefaultIntegerFieldEditor.class).emptyStringAllowed(true).textLimit(SearchForm.RESULTS_TEXT_LIMIT).build()),
	CRITERIA_RESTRICT(new PreferenceDetailsBuilder(PageDefinition.CRITERIA).defaultValue(SearchForm.Defaults.CRITERIA_RESTRICT).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.criteria.restrict");
		};
	}).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	AUTOREFRESH_ENABLED(new PreferenceDetailsBuilder(PageDefinition.CRITERIA).defaultValue(SearchForm.Defaults.AUTOREFRESH_ENABLED).build(), new FieldEditorDetailsBuilder(DefaultBooleanFieldEditor.class).build()),
	AUTOREFRESH_MINS(new PreferenceDetailsBuilder(PageDefinition.CRITERIA).parent(AUTOREFRESH_ENABLED).label(new Localized() {
		@Override
		public String getString() {
			return Messages.get("lbl.form.button.autorefresh");
		};
	}).build(), new FieldEditorDetailsBuilder(DefaultIntegerFieldEditor.class).emptyStringAllowed(true).textLimit(SearchForm.AUTO_REFRESH_TEXT_LIMIT).build());

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
			final String value = format.name();
			final Localized name = new Localized() {
				@Override
				public String getString() {
					return format.getDescription();
				}
			};
			options.put(name, value);
		}
		return options;
	}

}
