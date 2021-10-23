package io.github.albertus82.eqbulletin.gui.async;

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
		final SearchRequest request = new SearchRequest();
		request.setValid(form.isValid());
		request.setDelay(getDelay(form));
		if (request.isValid()) {
			final Map<String, String> params = request.getParameterMap();
			params.put(Format.KEY, getFormatValue(form));
			params.put("mode", form.getRestrictButton().getSelection() ? "mt" : "");
			if (form.getPeriodFromDateTime().isEnabled() && form.getPeriodFromDateTime().getSelection() != null) {
				params.put("datemin", form.getPeriodFromDateTime().getText());
			}
			if (form.getPeriodToDateTime().isEnabled() && form.getPeriodToDateTime().getSelection() != null) {
				params.put("datemax", form.getPeriodToDateTime().getText());
			}
			params.put("latmin", form.getLatitudeFromText().getText());
			params.put("latmax", form.getLatitudeToText().getText());
			params.put("lonmin", form.getLongitudeFromText().getText());
			params.put("lonmax", form.getLongitudeToText().getText());
			params.put("magmin", form.getMinimumMagnitudeText().getText());
			if (form.getResultsText().isEnabled() && !form.getResultsText().getText().isEmpty()) {
				request.setLimit(Short.valueOf(form.getResultsText().getText()));
			}
		}
		return request;
	}

	private static String getFormatValue(final SearchForm form) {
		for (final Entry<Format, Button> entry : form.getFormatRadios().entrySet()) {
			if (entry.getValue().getSelection()) {
				return entry.getKey().getValue();
			}
		}
		return SearchForm.Defaults.FORMAT.getValue();
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
