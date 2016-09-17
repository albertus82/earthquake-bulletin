package it.albertus.earthquake.gui.listener;

import it.albertus.earthquake.gui.AboutDialog;
import it.albertus.earthquake.resources.Messages;
import it.albertus.util.Version;

import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class AboutSelectionListener extends SelectionAdapter {

	private final IShellProvider gui;

	public AboutSelectionListener(final IShellProvider gui) {
		this.gui = gui;
	}

	@Override
	public void widgetSelected(SelectionEvent event) {
		final AboutDialog aboutDialog = new AboutDialog(gui.getShell());
		aboutDialog.setText(Messages.get("lbl.about.title"));
		aboutDialog.setMessage(Messages.get("msg.application.name") + ' ' + Messages.get("msg.version", Version.getInstance().getNumber(), Version.getInstance().getDate()));
		aboutDialog.setApplicationUrl(Messages.get("msg.website"));
		aboutDialog.setIconUrl(Messages.get("msg.info.icon.site"));
		aboutDialog.open();
	}

}
