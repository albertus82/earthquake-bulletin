package io.github.albertus82.eqbulletin.gui.listener;

import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import io.github.albertus82.eqbulletin.gui.AboutDialog;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AboutListener extends SelectionAdapter implements Listener {

	@NonNull
	private final IShellProvider provider;

	@Override
	public void widgetSelected(final SelectionEvent e) {
		execute();
	}

	@Override
	public void handleEvent(final Event event) {
		execute();
	}

	private void execute() {
		new AboutDialog(provider.getShell()).open();
	}

}
