package it.albertus.earthquake.gui.decoration;

import it.albertus.jface.decoration.TextDecoration;
import it.albertus.jface.validation.TextValidator;
import it.albertus.util.Localized;

import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;

public class SearchFormTextDecoration extends TextDecoration {

	public SearchFormTextDecoration(final TextValidator validator, final Localized message) {
		super(validator, message, DEFAULT_STYLE, FieldDecorationRegistry.DEC_WARNING);
	}

	/** Make some room for warning icon. */
	@Override
	protected void adjustLayoutData(final Text text, final Image image) {
		if (text.getParent().getLayout() instanceof GridLayout && text.getLayoutData() instanceof GridData) {
			final GridLayout gridLayout = (GridLayout) text.getParent().getLayout();
			final GridData gridData = (GridData) text.getLayoutData();
			gridData.horizontalIndent = image.getBounds().width - gridLayout.horizontalSpacing + 1;
		}
	}

}
