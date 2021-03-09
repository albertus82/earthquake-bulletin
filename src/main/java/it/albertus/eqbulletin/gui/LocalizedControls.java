package it.albertus.eqbulletin.gui;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import it.albertus.util.ISupplier;

public class LocalizedControls {

	private static final String DATA_KEY = LocalizedControls.class.getName() + ".textSupplier";

	private final Collection<Item> items = new ArrayList<>();
	private final Collection<Label> labels = new ArrayList<>();
	private final Collection<Button> buttons = new ArrayList<>();
	private final Collection<Group> groups = new ArrayList<>();

	public void updateTexts() {
		for (final Item control : items) {
			resetText(control);
		}
		for (final Label control : labels) {
			resetText(control);
		}
		for (final Button control : buttons) {
			resetText(control);
		}
		for (final Group control : groups) {
			resetText(control);
		}
	}

	public MenuItem newLocalizedMenuItem(final Menu parent, final int style, final ISupplier<String> textSupplier) {
		final MenuItem control = new MenuItem(parent, style);
		control.setData(DATA_KEY, textSupplier);
		resetText(control);
		items.add(control);
		return control;
	}

	public Label newLocalizedLabel(final Composite parent, final int style, final ISupplier<String> textSupplier) {
		final Label control = new Label(parent, style);
		control.setData(DATA_KEY, textSupplier);
		resetText(control);
		labels.add(control);
		return control;
	}

	public Button newLocalizedButton(final Composite parent, final int style, final ISupplier<String> textSupplier) {
		final Button control = new Button(parent, style);
		control.setData(DATA_KEY, textSupplier);
		resetText(control);
		buttons.add(control);
		return control;
	}

	public Group newLocalizedGroup(final Composite parent, final int style, final ISupplier<String> textSupplier) {
		final Group control = new Group(parent, style);
		control.setData(DATA_KEY, textSupplier);
		resetText(control);
		groups.add(control);
		return control;
	}

	private static void resetText(final Item control) {
		if (control != null && !control.isDisposed()) {
			final Object data = control.getData(DATA_KEY);
			if (data instanceof ISupplier) {
				control.setText(String.valueOf(((ISupplier<?>) data).get()));
			}
		}
	}

	private static void resetText(final Label control) {
		if (control != null && !control.isDisposed()) {
			final Object data = control.getData(DATA_KEY);
			if (data instanceof ISupplier) {
				control.setText(String.valueOf(((ISupplier<?>) data).get()));
			}
		}
	}

	private static void resetText(final Button control) {
		if (control != null && !control.isDisposed()) {
			final Object data = control.getData(DATA_KEY);
			if (data instanceof ISupplier) {
				control.setText(String.valueOf(((ISupplier<?>) data).get()));
			}
		}
	}

	private static void resetText(final Group control) {
		if (control != null && !control.isDisposed()) {
			final Object data = control.getData(DATA_KEY);
			if (data instanceof ISupplier) {
				control.setText(String.valueOf(((ISupplier<?>) data).get()));
			}
		}
	}

}
