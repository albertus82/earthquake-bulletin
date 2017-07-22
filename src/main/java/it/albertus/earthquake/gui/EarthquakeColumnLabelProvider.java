package it.albertus.earthquake.gui;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import it.albertus.earthquake.config.EarthquakeBulletinConfiguration;
import it.albertus.earthquake.model.Earthquake;
import it.albertus.util.Configuration;

public class EarthquakeColumnLabelProvider extends ColumnLabelProvider {

	private static final String EARTHQUAKE_BACKGROUND_BIG = "EARTHQUAKE_BACKGROUND_BIG";
	private static final String EARTHQUAKE_BACKGROUND_XXL = "EARTHQUAKE_BACKGROUND_XXL";

	private static final Configuration configuration = EarthquakeBulletinConfiguration.getInstance();

	static {
		JFaceResources.getColorRegistry().put(EARTHQUAKE_BACKGROUND_XXL, new RGB(0xF8, 0xC8, 0xC8));
		JFaceResources.getColorRegistry().put(EARTHQUAKE_BACKGROUND_BIG, new RGB(0xF8, 0xE4, 0xE4));
	}

	@Override
	public Color getBackground(final Object element) {
		if (element instanceof Earthquake) {
			final Earthquake earthquake = (Earthquake) element;
			if (earthquake.getMagnitude() >= configuration.getFloat("magnitude.xxl", ResultsTable.Defaults.MAGNITUDE_XXL)) {
				return JFaceResources.getColorRegistry().get(EARTHQUAKE_BACKGROUND_XXL);
			}
			else if (earthquake.getMagnitude() >= configuration.getFloat("magnitude.big", ResultsTable.Defaults.MAGNITUDE_BIG)) {
				return JFaceResources.getColorRegistry().get(EARTHQUAKE_BACKGROUND_BIG);
			}
		}
		return super.getBackground(element);
	}

}
