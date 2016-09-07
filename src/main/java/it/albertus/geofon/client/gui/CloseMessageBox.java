package it.albertus.geofon.client.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class CloseMessageBox {

	public interface Defaults {
		boolean GUI_CONFIRM_CLOSE = false;
	}

	private final MessageBox messageBox;

	private CloseMessageBox(Shell shell) {
		messageBox = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_QUESTION);
		messageBox.setText("Close?");//Messages.get("msg.confirm.close.text"));
		messageBox.setMessage("Close?");//Messages.get("msg.confirm.close.message"));
	}

	public static MessageBox newInstance(Shell shell) {
		return new CloseMessageBox(shell).messageBox;
	}

	public static boolean show() {
		return false;//RouterLoggerClientConfiguration.getInstance().getBoolean("gui.confirm.close", Defaults.GUI_CONFIRM_CLOSE);
	}

}
