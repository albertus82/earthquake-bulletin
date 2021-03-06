package it.albertus.eqbulletin.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.MomentTensor;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.JFaceMessages;
import it.albertus.jface.SwtUtils;
import it.albertus.jface.closeable.CloseableResource;
import it.albertus.jface.preference.IPreferencesConfiguration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

@Log
public class MomentTensorDialog extends Dialog {

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Defaults {
		public static final boolean LIMIT_HEIGHT = false;
		public static final byte MAX_DIALOGS = 0xF;
	}

	private static final Collection<MomentTensorDialog> instances = new ArrayList<>();

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getPreferencesConfiguration();

	private MomentTensor momentTensor;
	private final Earthquake earthquake;

	private Shell shell;
	private Text text;

	public static synchronized MomentTensorDialog getInstance(final Shell parent, final MomentTensor momentTensor, final Earthquake earthquake) {
		for (final MomentTensorDialog instance : instances) {
			if (instance.earthquake.getGuid().equals(earthquake.getGuid())) {
				return instance;
			}
		}
		return new MomentTensorDialog(parent, momentTensor, earthquake);
	}

	private MomentTensorDialog(final Shell parent, final MomentTensor momentTensor, final Earthquake earthquake) {
		super(parent, SWT.CLOSE | SWT.RESIZE);
		this.earthquake = earthquake;
		this.momentTensor = momentTensor;
		addInstance(); // Available for update on-the-fly.
		setText(earthquake.getSummary());
	}

	public void show() {
		if (shell != null && !shell.isDisposed()) {
			shell.setActive();
		}
		else {
			final byte maxDialogs = configuration.getByte(Preference.MT_MAX_DIALOGS, Defaults.MAX_DIALOGS);
			if (instances.size() <= maxDialogs) {
				open();
			}
			else {
				log.log(Level.FINE, "Moment tensor dialog limit reached ({0}). Sending alert to the user...", maxDialogs);
				removeInstance();
				final MessageBox mb = new MessageBox(getParent(), SWT.ICON_WARNING);
				mb.setText(Messages.get("err.mt.too.many.dialogs.title"));
				mb.setMessage(Messages.get("err.mt.too.many.dialogs.text"));
				mb.open();
			}
		}
	}

	private void open() {
		try {
			shell = new Shell(getParent(), getStyle());
			shell.addDisposeListener(e -> removeInstance()); // No longer available for update on-the-fly.
			final Point defaultSize = getSize(shell);
			shell.setText(getText());
			final Image[] images = Images.getAppIconArray();
			if (images != null && images.length > 0) {
				shell.setImages(images);
			}
			GridLayoutFactory.swtDefaults().applyTo(shell);
			createContents(shell);
			shell.pack();
			if (configuration.getBoolean(Preference.MT_LIMIT_HEIGHT, Defaults.LIMIT_HEIGHT)) {
				shell.setSize(shell.getSize().x, defaultSize.y);
			}
			shell.open();
			final Display display = getParent().getDisplay();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		}
		finally {
			removeInstance(); // No longer available for update on-the-fly.
		}
	}

	private static Point getSize(final Shell shell) {
		final Point normalShellSize = shell.getSize();
		final int size = Math.min(normalShellSize.x, normalShellSize.y);
		return new Point(size, size);
	}

	private void createContents(final Shell shell) {
		text = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
		text.setEditable(false);
		if (Util.isWindows()) {
			text.setBackground(text.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		}
		text.setFont(JFaceResources.getTextFont());
		final String momentTensorText = momentTensor.getText().trim();
		text.setText(momentTensorText);
		try (final CloseableResource<GC> cr = new CloseableResource<>(new GC(text))) {
			final Point textExtent = cr.getResource().textExtent(momentTensorText);
			GridDataFactory.fillDefaults().grab(true, true).hint(textExtent.x, SWT.DEFAULT).applyTo(text);
		}
		createButtonBox(shell);
	}

	private static Composite createButtonBox(final Shell shell) {
		final Composite buttonComposite = new Composite(shell, SWT.NONE);
		GridLayoutFactory.swtDefaults().applyTo(buttonComposite);
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).applyTo(buttonComposite);

		final Button closeButton = new Button(buttonComposite, SWT.PUSH);
		closeButton.setText(JFaceMessages.get("lbl.button.close"));
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.FILL).grab(true, false).minSize(SwtUtils.convertHorizontalDLUsToPixels(closeButton, IDialogConstants.BUTTON_WIDTH), SWT.DEFAULT).applyTo(closeButton);
		closeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent se) {
				shell.close();
			}
		});

		shell.setDefaultButton(closeButton);
		return buttonComposite;
	}

	private synchronized void addInstance() {
		if (instances.add(this)) {
			log.log(Level.FINE, "Moment tensor dialog instance added; instances.size() = {0}.", instances.size());
		}
	}

	private synchronized void removeInstance() {
		if (instances.remove(this)) {
			log.log(Level.FINE, "Moment tensor dialog instance removed; instances.size() = {0}.", instances.size());
		}
	}

	public static synchronized void updateMomentTensorText(final MomentTensor momentTensor, final Earthquake earthquake) {
		int count = 0;
		for (final MomentTensorDialog instance : instances) {
			if (instance.earthquake != null && earthquake.getGuid().equals(instance.earthquake.getGuid())) {
				log.log(Level.FINE, "Updating moment tensor dialog instance {0}...", instance);
				instance.momentTensor = momentTensor; // Useful when the Text field is not initialized.
				if (instance.text != null && !instance.text.isDisposed()) {
					final String oldValue = instance.text.getText();
					final String newValue = instance.momentTensor.getText().trim();
					if (!newValue.equals(oldValue)) {
						instance.text.setText(newValue); // Update the Text field on-the-fly.
					}
				}
				log.log(Level.FINE, "Moment tensor dialog instance {0} updated.", instance);
				count++;
			}
		}
		if (count == 0) {
			log.log(Level.WARNING, "No moment tensor dialog instance found to update for {0}.", earthquake);
		}
	}

}
