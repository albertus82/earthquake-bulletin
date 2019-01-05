package it.albertus.eqbulletin.util;

@FunctionalInterface
public interface CancellationStatus {

	/**
	 * Returns whether cancellation of current operation has been requested.
	 * Long-running operations should poll to see if cancellation has been
	 * requested.
	 *
	 * @return <code>true</code> if cancellation has been requested, and
	 *         <code>false</code> otherwise
	 */
	boolean isCanceled();

}
