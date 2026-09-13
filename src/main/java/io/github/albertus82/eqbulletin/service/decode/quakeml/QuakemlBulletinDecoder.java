package io.github.albertus82.eqbulletin.service.decode.quakeml;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.xml.datatype.XMLGregorianCalendar;

import org.quakeml.xmlns.bed._1.EvaluationMode;
import org.quakeml.xmlns.bed._1.EvaluationStatus;
import org.quakeml.xmlns.bed._1.Event;
import org.quakeml.xmlns.bed._1.EventDescription;
import org.quakeml.xmlns.bed._1.FocalMechanism;
import org.quakeml.xmlns.bed._1.Magnitude;
import org.quakeml.xmlns.bed._1.MomentTensor;
import org.quakeml.xmlns.bed._1.Origin;
import org.quakeml.xmlns.bed._1.RealQuantity;
import org.quakeml.xmlns.bed._1.TimeQuantity;
import org.quakeml.xmlns.quakeml._1.Quakeml;

import io.github.albertus82.eqbulletin.model.Depth;
import io.github.albertus82.eqbulletin.model.Earthquake;
import io.github.albertus82.eqbulletin.model.Latitude;
import io.github.albertus82.eqbulletin.model.Longitude;
import io.github.albertus82.eqbulletin.model.Status;
import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class QuakemlBulletinDecoder {

	private QuakemlBulletinDecoder() {
		// Utility class.
	}

	/**
	 * Converts a QuakeML Event into the application's Earthquake model.
	 *
	 * @param event QuakeML event
	 * @return mapped earthquake
	 * @throws IllegalArgumentException if one of the mandatory Earthquake
	 *         properties cannot be obtained
	 */
	private static Earthquake toEarthquake(final Event event) {
		Objects.requireNonNull(event, "event must not be null");
		final List<JAXBElement<?>> elements = event.getDescriptionOrCommentOrFocalMechanism();
		final String guid = requireText(event.getPublicID(), "event.publicID");
		final String preferredOriginId = findString(elements, "preferredOriginID");
		final String preferredMagnitudeId = findString(elements, "preferredMagnitudeID");
		final Origin origin = findPreferred(elements, "origin", preferredOriginId, Origin.class);
		final Magnitude magnitude = findPreferred(elements, "magnitude", preferredMagnitudeId, Magnitude.class);
		final TimeQuantity timeQuantity = findTimeQuantity(origin);
		final ZonedDateTime time = toZonedDateTime(requireTimeValue(timeQuantity, "origin.time"), "origin.time");
		final float latitudeValue = requireFiniteFloat(findRealQuantityValue(origin, "latitude"), "origin.latitude");
		final float longitudeValue = requireFiniteFloat(findRealQuantityValue(origin, "longitude"), "origin.longitude");
		final double depthMeters = requireFiniteDouble(findRealQuantityValue(origin, "depth"), "origin.depth");
		final int depthKm = toDepthKilometres(depthMeters);
		final float magnitudeValue = requireFiniteFloat(findRealQuantityValue(magnitude, "mag"), "magnitude.mag");
		final String region = findRegion(origin).orElseGet(() -> findDescription(event).orElse(""));
		final Status status = findStatus(origin);
		final URI momentTensorUri = findMomentTensorUri(elements, findString(elements, "preferredFocalMechanismID"));

		return new Earthquake(guid, time, magnitudeValue, Latitude.valueOf(latitudeValue), Longitude.valueOf(longitudeValue), Depth.valueOf(depthKm), status, region, null, // QuakeML Event has no equivalent "link" field.
				null, // QuakeML Event has no equivalent enclosure URI.
				momentTensorUri);
	}

	/**
	 * Finds the preferred object referenced by a preferredXXXID element. If no
	 * reference is available/resolvable, the first object of the requested type is
	 * returned.
	 */
	private static <T> T findPreferred(final List<JAXBElement<?>> elements, final String elementName, final String preferredId, final Class<T> type) {

		T first = null;

		for (final JAXBElement<?> element : elements) {
			if (!elementName.equals(element.getName().getLocalPart())) {
				continue;
			}

			final Object value = element.getValue();

			if (!type.isInstance(value)) {
				continue;
			}

			final T candidate = type.cast(value);

			if (first == null) {
				first = candidate;
			}

			if (preferredId != null && preferredId.equals(publicId(candidate))) {
				return candidate;
			}
		}

		if (first == null) {
			throw new IllegalArgumentException("Event does not contain a " + elementName);
		}

		return first;
	}

	/**
	 * Extracts the textual value of an Event-level resource reference.
	 */
	private static String findString(final List<JAXBElement<?>> elements, final String elementName) {

		for (final JAXBElement<?> element : elements) {
			if (elementName.equals(element.getName().getLocalPart())) {
				final Object value = element.getValue();

				if (value != null) {
					return value.toString();
				}
			}
		}

		return null;
	}

	private static RealQuantity findRealQuantity(final Origin origin, final String elementName) {

		return findRealQuantity(origin.getCompositeTimeOrCommentOrOriginUncertainty(), elementName);
	}

	private static RealQuantity findRealQuantity(final Magnitude magnitude, final String elementName) {

		return findRealQuantity(magnitude.getCommentOrStationMagnitudeContributionOrMag(), elementName);
	}

	private static RealQuantity findRealQuantity(final List<JAXBElement<?>> elements, final String elementName) {

		for (final JAXBElement<?> element : elements) {
			if (elementName.equals(element.getName().getLocalPart())) {
				final Object value = element.getValue();

				if (value instanceof RealQuantity) {
					return (RealQuantity) value;
				}
			}
		}

		return null;
	}

	private static Double findRealQuantityValue(final Origin origin, final String elementName) {

		return findRealQuantityValue(findRealQuantity(origin, elementName));
	}

	private static Double findRealQuantityValue(final Magnitude magnitude, final String elementName) {

		return findRealQuantityValue(findRealQuantity(magnitude, elementName));
	}

	/**
	 * RealQuantity contains another generated JAXB list. The actual numeric value
	 * is the JAXBElement whose local name is "value".
	 */
	private static Double findRealQuantityValue(final RealQuantity quantity) {

		if (quantity == null) {
			return null;
		}

		for (final JAXBElement<Double> element : quantity.getValueOrUncertaintyOrLowerUncertainty()) {

			if ("value".equals(element.getName().getLocalPart())) {
				return element.getValue();
			}
		}

		return null;
	}

	private static XMLGregorianCalendar requireValue(final RealQuantity quantity, final String field) {

		if (quantity == null) {
			throw new IllegalArgumentException("Missing mandatory field: " + field);
		}

		for (final JAXBElement<?> element : quantity.getValueOrUncertaintyOrLowerUncertainty()) {

			if ("value".equals(element.getName().getLocalPart())) {
				final Object value = element.getValue();

				if (value instanceof XMLGregorianCalendar) {
					return (XMLGregorianCalendar) value;
				}
			}
		}

		throw new IllegalArgumentException("Missing mandatory value: " + field);
	}

	private static ZonedDateTime toZonedDateTime(final XMLGregorianCalendar calendar, final String field) {

		try {
			return calendar.toGregorianCalendar().toZonedDateTime();
		}
		catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid date/time in " + field, e);
		}
	}

	private static Optional<String> findRegion(final Origin origin) {
		for (final JAXBElement<?> element : origin.getCompositeTimeOrCommentOrOriginUncertainty()) {

			if ("region".equals(element.getName().getLocalPart())) {
				final Object value = element.getValue();

				if (value != null) {
					return Optional.of(value.toString());
				}
			}
		}

		return Optional.empty();
	}

	private static Optional<String> findDescription(final Event event) {
		for (final JAXBElement<?> element : event.getDescriptionOrCommentOrFocalMechanism()) {

			if (!"description".equals(element.getName().getLocalPart())) {
				continue;
			}

			final Object value = element.getValue();

			if (!(value instanceof EventDescription)) {
				continue;
			}

			final EventDescription description = (EventDescription) value;

			for (final Object item : description.getTextOrType()) {
				if (item instanceof String && !((String) item).trim().isEmpty()) {
					return Optional.of(((String) item).trim());
				}
			}
		}

		return Optional.empty();
	}

	/**
	 * Maps QuakeML's evaluation information to the application's much smaller
	 * Status enum.
	 *
	 * Confirmed/reviewed/final/rejected are treated as confirmed unless the origin
	 * is explicitly automatic. Manual origins are treated as manually revised.
	 */
	private static Status findStatus(final Origin origin) {
		EvaluationMode mode = null;
		EvaluationStatus evaluationStatus = null;

		for (final JAXBElement<?> element : origin.getCompositeTimeOrCommentOrOriginUncertainty()) {

			final String name = element.getName().getLocalPart();
			final Object value = element.getValue();

			if ("evaluationMode".equals(name) && value instanceof EvaluationMode) {
				mode = (EvaluationMode) value;
			}
			else if ("evaluationStatus".equals(name) && value instanceof EvaluationStatus) {
				evaluationStatus = (EvaluationStatus) value;
			}
		}

		if (mode == EvaluationMode.AUTOMATIC) {
			return Status.A;
		}

		if (mode == EvaluationMode.MANUAL) {
			if (evaluationStatus == EvaluationStatus.CONFIRMED || evaluationStatus == EvaluationStatus.REVIEWED || evaluationStatus == EvaluationStatus.FINAL) {
				return Status.C;
			}

			return Status.M;
		}

		if (evaluationStatus == EvaluationStatus.CONFIRMED || evaluationStatus == EvaluationStatus.REVIEWED || evaluationStatus == EvaluationStatus.FINAL) {
			return Status.C;
		}

		return null;
	}

	/**
	 * The QuakeML MomentTensor itself has a publicID, which is the closest
	 * representation of the application's momentTensorUri.
	 */
	private static URI findMomentTensorUri(final List<JAXBElement<?>> eventElements, final String preferredFocalMechanismId) {

		FocalMechanism first = null;
		FocalMechanism preferred = null;

		for (final JAXBElement<?> element : eventElements) {
			if (!"focalMechanism".equals(element.getName().getLocalPart())) {
				continue;
			}

			final Object value = element.getValue();

			if (!(value instanceof FocalMechanism)) {
				continue;
			}

			final FocalMechanism focalMechanism = (FocalMechanism) value;

			if (first == null) {
				first = focalMechanism;
			}

			if (preferredFocalMechanismId != null && preferredFocalMechanismId.equals(focalMechanism.getPublicID())) {
				preferred = focalMechanism;
			}
		}

		final FocalMechanism focalMechanism = preferred != null ? preferred : first;

		if (focalMechanism == null) {
			return null;
		}

		for (final JAXBElement<?> element : focalMechanism.getWaveformIDOrCommentOrMomentTensor()) {

			if (!"momentTensor".equals(element.getName().getLocalPart())) {
				continue;
			}

			final Object value = element.getValue();

			if (value instanceof MomentTensor) {
				return toUri(((MomentTensor) value).getPublicID());
			}
		}

		return null;
	}

	private static String publicId(final Object object) {
		if (object instanceof Origin) {
			return ((Origin) object).getPublicID();
		}

		if (object instanceof Magnitude) {
			return ((Magnitude) object).getPublicID();
		}

		return null;
	}

	private static URI toUri(final String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}

		try {
			return URI.create(value);
		}
		catch (final IllegalArgumentException e) {
			return null;
		}
	}

	private static String requireText(final String value, final String field) {

		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException("Missing mandatory field: " + field);
		}

		return value;
	}

	private static float requireFiniteFloat(final Double value, final String field) {

		if (value == null || !Double.isFinite(value)) {
			throw new IllegalArgumentException("Missing or invalid field: " + field);
		}

		final float result = value.floatValue();

		if (!Float.isFinite(result)) {
			throw new IllegalArgumentException("Value outside float range: " + field);
		}

		return result;
	}

	private static double requireFiniteDouble(final Double value, final String field) {

		if (value == null || !Double.isFinite(value)) {
			throw new IllegalArgumentException("Missing or invalid field: " + field);
		}

		return value;
	}

	/**
	 * QuakeML defines Origin.depth in metres; the application model stores whole
	 * kilometres.
	 */
	private static int toDepthKilometres(final double depthMeters) {
		if (depthMeters < 0.0) {
			throw new IllegalArgumentException("Negative earthquake depth: " + depthMeters);
		}

		return (int) Math.round(depthMeters / 1000.0);
	}

	private static TimeQuantity findTimeQuantity(final Origin origin) {
		for (final JAXBElement<?> element : origin.getCompositeTimeOrCommentOrOriginUncertainty()) {

			if (!"time".equals(element.getName().getLocalPart())) {
				continue;
			}

			final Object value = element.getValue();

			if (value instanceof TimeQuantity) {
				return (TimeQuantity) value;
			}
		}

		return null;
	}

	private static XMLGregorianCalendar requireTimeValue(final TimeQuantity quantity, final String field) {

		if (quantity == null) {
			throw new IllegalArgumentException("Missing mandatory field: " + field);
		}

		for (final JAXBElement<?> element : quantity.getValueOrUncertaintyOrLowerUncertainty()) {

			if (!"value".equals(element.getName().getLocalPart())) {
				continue;
			}

			final Object value = element.getValue();

			if (value instanceof XMLGregorianCalendar) {
				return (XMLGregorianCalendar) value;
			}
		}

		throw new IllegalArgumentException("Missing mandatory value: " + field);
	}

	public static Collection<Earthquake> decode(final Quakeml quakeml) {
		Objects.requireNonNull(quakeml, "quakeml must not be null");
		final List<Earthquake> list = new ArrayList<>();
		for (final Object e : quakeml.getEventParameters().getCommentOrEventOrDescription()) {
			if (e instanceof Event) {
				list.add(toEarthquake((Event) e));
			}
			else {
				log.error("Ignored QuakeML element: {}", e);
			}
		}
		return list;
	}

}
