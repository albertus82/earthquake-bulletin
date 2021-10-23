package io.github.albertus82.eqbulletin.service.net;

import java.io.Serializable;

public interface Cacheable extends Serializable {

	String getEtag();

}
