package it.albertus.eqbulletin.gui;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.util.Configuration;

public class EarthquakeColumnLabelProvider extends ColumnLabelProvider {

	private static final String EARTHQUAKE_BACKGROUND_BIG = "EARTHQUAKE_BACKGROUND_BIG";
	private static final String EARTHQUAKE_BACKGROUND_XXL = "EARTHQUAKE_BACKGROUND_XXL";

	private static final Configuration configuration = EarthquakeBulletinConfig.getInstance();

	static {
		JFaceResources.getColorRegistry().put(EARTHQUAKE_BACKGROUND_XXL, new RGB(0xF8, 0xC8, 0xC8));
		JFaceResources.getColorRegistry().put(EARTHQUAKE_BACKGROUND_BIG, new RGB(0xF8, 0xE4, 0xE4));
	}

	@Override
	public final Color getBackground(final Object element) {
		return getBackground((Earthquake) element);
	}

	private final Color getBackground(final Earthquake element) {
		if (element.getMagnitude() >= configuration.getFloat("magnitude.xxl", ResultsTable.Defaults.MAGNITUDE_XXL)) {
			return JFaceResources.getColorRegistry().get(EARTHQUAKE_BACKGROUND_XXL);
		}
		else if (element.getMagnitude() >= configuration.getFloat("magnitude.big", ResultsTable.Defaults.MAGNITUDE_BIG)) {
			return JFaceResources.getColorRegistry().get(EARTHQUAKE_BACKGROUND_BIG);
		}
		else {
			return super.getBackground(element);
		}
	}

	@Override
	public final Font getFont(final Object element) {
		return getFont((Earthquake) element);
	}

	protected Font getFont(final Earthquake element) {
		return super.getFont(element);
	}

	@Override
	public final Color getForeground(final Object element) {
		return getForeground((Earthquake) element);
	}

	protected Color getForeground(final Earthquake element) {
		return super.getForeground(element);
	}

	@Override
	public final String getText(final Object element) {
		return getText((Earthquake) element);
	}

	protected String getText(final Earthquake element) {
		return super.getText(element);
	}

	@Override
	public final String getToolTipText(final Object element) {
		return getToolTipText((Earthquake) element);
	}

	protected String getToolTipText(final Earthquake element) {
		return super.getToolTipText(element);
	}

}
