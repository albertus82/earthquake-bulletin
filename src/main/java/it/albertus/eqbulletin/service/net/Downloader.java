package it.albertus.eqbulletin.service.net;

import java.io.IOException;

@FunctionalInterface
public interface Downloader<A, O> {

	O download(A arg) throws IOException;

}
