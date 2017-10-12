package it.albertus.eqbulletin.gui.decoration;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;

import it.albertus.jface.decoration.ControlValidatorDecoration;
import it.albertus.jface.validation.ControlValidator;
import it.albertus.util.Localized;

public class SearchFormControlValidatorDecoration extends ControlValidatorDecoration {

	public SearchFormControlValidatorDecoration(final ControlValidator<?> validator, final Localized message) {
		super(validator, message);
	}

	/** Make some room for the icon. */
	@Override
	protected void adjustLayoutData(final Control control, final Image image) {
		if (control.getParent().getLayout() instanceof GridLayout && control.getLayoutData() instanceof GridData) {
			final GridLayout gridLayout = (GridLayout) control.getParent().getLayout();
			final GridData gridData = (GridData) control.getLayoutData();
			gridData.horizontalIndent = image.getBounds().width - gridLayout.horizontalSpacing + 1;
		}
	}

}
