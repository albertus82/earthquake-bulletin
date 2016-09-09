package it.albertus.geofon.client.gui;

import it.albertus.geofon.client.GeofonClient;
import it.albertus.geofon.client.resources.Messages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class CloseMessageBox {

	public interface Defaults {
		boolean CONFIRM_CLOSE = false;
	}

	private final MessageBox messageBox;

	private CloseMessageBox(Shell shell) {
		messageBox = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_QUESTION);
		messageBox.setText(Messages.get("msg.confirm.close.text"));
		messageBox.setMessage(Messages.get("msg.confirm.close.message"));
	}

	public static MessageBox newInstance(Shell shell) {
		return new CloseMessageBox(shell).messageBox;
	}

	public static boolean show() {
		return GeofonClient.configuration.getBoolean("confirm.close", Defaults.CONFIRM_CLOSE);
	}

}
