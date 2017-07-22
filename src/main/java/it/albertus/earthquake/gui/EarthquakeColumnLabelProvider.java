package it.albertus.earthquake.gui;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import it.albertus.earthquake.model.Earthquake;

public class EarthquakeColumnLabelProvider extends ColumnLabelProvider {

	private static final String EARTHQUAKE_BACKGROUND_BIG = "EARTHQUAKE_BACKGROUND_BIG";
	private static final String EARTHQUAKE_BACKGROUND_XXL = "EARTHQUAKE_BACKGROUND_XXL";

	static {
		JFaceResources.getColorRegistry().put(EARTHQUAKE_BACKGROUND_XXL, new RGB(0xF8, 0xC8, 0xC8));
		JFaceResources.getColorRegistry().put(EARTHQUAKE_BACKGROUND_BIG, new RGB(0xF8, 0xE4, 0xE4));
	}

	@Override
	public Color getBackground(final Object element) {
		if (element instanceof Earthquake) {
			final Earthquake earthquake = (Earthquake) element;
			if (earthquake.getMagnitude() >= 6) {
				return JFaceResources.getColorRegistry().get(EARTHQUAKE_BACKGROUND_XXL);
			}
			else if (earthquake.getMagnitude() >= 5) {
				return JFaceResources.getColorRegistry().get(EARTHQUAKE_BACKGROUND_BIG);
			}
		}
		return super.getBackground(element);
	}

}
