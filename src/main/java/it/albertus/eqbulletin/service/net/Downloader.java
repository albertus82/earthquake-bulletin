package it.albertus.eqbulletin.service.net;

import java.io.IOException;

public interface Downloader<A, O> {

	O download(A arg) throws IOException;

	O download(A arg, O cached) throws IOException;

}
