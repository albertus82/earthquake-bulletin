package io.github.albertus82.eqbulletin.gui.async;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Button;

import io.github.albertus82.eqbulletin.gui.EarthquakeBulletinGui;
import io.github.albertus82.eqbulletin.gui.SearchForm;
import io.github.albertus82.eqbulletin.model.Format;
import io.github.albertus82.eqbulletin.service.GeofonBulletinProvider;
import io.github.albertus82.eqbulletin.service.SearchRequest;
import io.github.albertus82.eqbulletin.service.job.SearchJob;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SearchAsyncOperation extends AsyncOperation {

	private static SearchJob currentJob;

	public static synchronized void execute(final EarthquakeBulletinGui gui) {
		final SearchRequest request = evaluateForm(gui.getSearchForm());
		log.debug("{}", request);
		if (request.isValid()) {
			final Button searchButton = gui.getSearchForm().getSearchButton();
			searchButton.setEnabled(false);
			cancelCurrentJob();
			final SearchJob job = new SearchJob(request, new GeofonBulletinProvider());
			job.addJobChangeListener(new SearchJobChangeListener(request, gui));
			job.schedule();
			setCurrentJob(job);
		}
	}

	public static synchronized void cancelCurrentJob() {
		if (currentJob != null) {
			currentJob.setCanceled(true);
			currentJob.cancel();
			currentJob = null;
		}
	}

	public static synchronized Job getCurrentJob() {
		return currentJob;
	}

	private static synchronized void setCurrentJob(final SearchJob job) {
		currentJob = job;
	}

	private static SearchRequest evaluateForm(final SearchForm form) {
		final Format format = getFormat(form);
		final SearchRequest request = new SearchRequest(format, form.isValid(), getDelay(form));
		if (request.isValid()) {
			final Map<String, String> params = request.getParameterMap();
			if (!Format.QUAKEML.equals(format)) {
				params.put(Format.PARAM_NAME, format.getParamValue());
				params.put("mode", form.getRestrictButton().getSelection() ? "mt" : "");
			}
			if (form.getPeriodFromDateTime().isEnabled() && form.getPeriodFromDateTime().getSelection() != null) {
				try {
					final LocalDate date = LocalDate.parse(form.getPeriodFromDateTime().getText().trim());
					params.put(Format.QUAKEML.equals(format) ? "start" : "datemin", date.toString());
				}
				catch (final DateTimeParseException e) {
					log.info("Invalid start date:", e);
				}
			}
			if (form.getPeriodToDateTime().isEnabled() && form.getPeriodToDateTime().getSelection() != null) {
				try {
					final LocalDate date = LocalDate.parse(form.getPeriodToDateTime().getText().trim());
					params.put(Format.QUAKEML.equals(format) ? "end" : "datemax", Format.QUAKEML.equals(format) ? date.plus(1, ChronoUnit.DAYS).toString() : date.toString());
				}
				catch (final DateTimeParseException e) {
					log.info("Invalid end date:", e);
				}
			}
			final String latmin = form.getLatitudeFromText().getText();
			if (latmin != null && !latmin.trim().isEmpty()) {
				params.put(Format.QUAKEML.equals(format) ? "minlatitude" : "latmin", latmin.trim());
			}
			final String latmax = form.getLatitudeToText().getText();
			if (latmax != null && !latmax.trim().isEmpty()) {
				params.put(Format.QUAKEML.equals(format) ? "maxlatitude" : "latmax", latmax.trim());
			}
			final String lonmin = form.getLongitudeFromText().getText();
			if (lonmin != null && !lonmin.trim().isEmpty()) {
				params.put(Format.QUAKEML.equals(format) ? "minlongitude" : "lonmin", lonmin.trim());
			}
			final String lonmax = form.getLongitudeToText().getText();
			if (lonmax != null && !lonmax.trim().isEmpty()) {
				params.put(Format.QUAKEML.equals(format) ? "maxlongitude" : "lonmax", lonmax.trim());
			}
			final String magmin = form.getMinimumMagnitudeText().getText();
			if (magmin != null && !magmin.trim().isEmpty()) {
				params.put(Format.QUAKEML.equals(format) ? "minmagnitude" : "magmin", magmin.trim());
			}
			if (form.getResultsText().isEnabled()) {
				final String limit = form.getResultsText().getText();
				if (limit != null && !limit.trim().isEmpty()) {
					request.setLimit(Short.valueOf(limit.trim()));
				}
			}
		}
		return request;
	}

	private static Format getFormat(final SearchForm form) {
		for (final Entry<Format, Button> entry : form.getFormatRadios().entrySet()) {
			if (entry.getValue().getSelection()) {
				return entry.getKey();
			}
		}
		return SearchForm.Defaults.FORMAT;
	}

	private static Long getDelay(final SearchForm form) {
		if (form.getAutoRefreshButton().getSelection()) {
			final String time = form.getAutoRefreshText().getText().trim();
			if (!time.isEmpty()) {
				try {
					final int minutes = Integer.parseInt(time);
					if (minutes > 0) {
						return TimeUnit.MINUTES.toMillis(minutes);
					}
				}
				catch (final RuntimeException e) {
					log.warn("Cannot determine delay:", e);
				}
			}
		}
		return null;
	}

}
