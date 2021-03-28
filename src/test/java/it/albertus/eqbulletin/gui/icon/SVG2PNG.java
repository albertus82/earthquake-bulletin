package it.albertus.eqbulletin.gui.icon;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;

import lombok.extern.java.Log;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Option;

@Log
@Command
public class SVG2PNG implements Callable<Integer> {

	@Option(names = { "-F", "--from" }, defaultValue = "16") private short from;

	@Option(names = { "-T", "--to" }, defaultValue = "256") private short to;

	@Option(names = { "-O", "--output" }) private Path output = Paths.get("target", "generated-resources", getClass().getPackage().getName().replace('.', File.separatorChar), "map");

	public static void main(final String... args) {
		System.exit(new CommandLine(new SVG2PNG()).setCommandName(SVG2PNG.class.getSimpleName().toLowerCase(Locale.ROOT)).setOptionsCaseInsensitive(true).execute(args));
	}

	@Override
	public Integer call() throws IOException, TranscoderException {
		if (output != null) {
			log.log(Level.INFO, "{0}", output.toAbsolutePath());
			Files.createDirectories(output);
		}
		for (short size = from; size <= to; size += Math.round(Math.max(1, size * .047f) / 2) * 2) {
			final ImageTranscoder it = new PNGTranscoder();
			it.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, (float) size);
			it.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, (float) size);
			final Path path = Paths.get(output.toString(), size + "x" + size + ".png");
			try (final InputStream is = getClass().getResourceAsStream("map.svg"); final OutputStream os = Files.newOutputStream(path); final BufferedOutputStream bos = new BufferedOutputStream(os)) {
				it.transcode(new TranscoderInput(is), new TranscoderOutput(bos));
				log.log(Level.INFO, "{0}x{0}", size);
			}
		}
		return ExitCode.OK;
	}

}
