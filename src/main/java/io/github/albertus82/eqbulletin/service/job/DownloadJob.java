package io.github.albertus82.eqbulletin.service.job;

import java.util.Optional;

public interface DownloadJob<T> {

	Optional<T> getDownloadedObject();

}
