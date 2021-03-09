package it.albertus.eqbulletin.cache;

import java.io.ByteArrayOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

import it.albertus.eqbulletin.model.MomentTensor;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Log
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class PackedMomentTensor implements Serializable {

	private static final long serialVersionUID = 6571208579436743965L;

	private static final Charset charset = StandardCharsets.UTF_8;

	@NonNull private final byte[] bytes;
	private final String etag;

	static PackedMomentTensor pack(@NonNull final MomentTensor momentTensor) {
		final String text = momentTensor.getText();
		log.log(Level.FINE, "Original text.length() = {0,number,#} chars.", text.length());
		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try (final OutputStream os = new DeflaterOutputStream(baos)) {
				os.write(text.getBytes(charset));
			} // The stream is finished and closed automatically.
			final byte[] bytes = baos.toByteArray();
			log.log(Level.FINE, "Compressed bytes.length = {0,number,#} bytes.", bytes.length);
			return new PackedMomentTensor(bytes, momentTensor.getEtag());
		}
		catch (final IOException e) {
			throw new IOError(e);
		}
	}

	MomentTensor unpack() {
		log.log(Level.FINE, "Compressed bytes.length = {0,number,#} bytes.", bytes.length);
		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try (final OutputStream os = new InflaterOutputStream(baos)) {
				os.write(bytes);
			}
			final String text = baos.toString(charset.name());
			log.log(Level.FINE, "Expanded text.length() = {0,number,#} chars.", text.length());
			return new MomentTensor(text, etag);
		}
		catch (final IOException e) {
			throw new IOError(e);
		}
	}

	@Override
	public String toString() {
		return "PackedMomentTensor [etag=" + etag + "]";
	}

}
