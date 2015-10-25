package de.javakaffee.kryoserializers;

import java.nio.charset.Charset;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * This custom serializer provides ability to serialize and deserialize those
 * java nio charsets which are private or default and not visible outside. Kryo
 * fails to deserialize those. Exampl: UTF-8, UTF-16, ISO_8859_1 etc.
 * 
 * @author jagrutmehta
 *
 */

public class CharsetSerializer extends Serializer<Charset> {

	public CharsetSerializer() {
		setImmutable(true);
	}

	@Override
	public Charset read(Kryo kryo, Input input, Class<Charset> type) {
		return Charset.forName(kryo.readObject(input, String.class));
	}

	@Override
	public void write(Kryo kryo, Output output, Charset charset) {
		kryo.writeObject(output, charset.name());
	}

	/**
	 * Convinient method to register this serializer.
	 * 
	 * @param kryo
	 * @param charSetName
	 *            for example "UTF-8"
	 * @param s
	 *            instance of this class.
	 */
	public static void registerCharsets(Kryo kryo, String charSetName,
			Serializer<Charset> s) {
		kryo.register(Charset.forName(charSetName).getClass(), s);
	}

	/**
	 * Registers following Charsets. UTF-8, UTF-16, UTF-16BE, UTF-16LE,
	 * ISO_8859_1
	 * 
	 * @param kryo
	 *            Kryo instance where registration needs to happen.
	 */
	public static void registerKnownCharSets(Kryo kryo) {
		CharsetSerializer serializer = new CharsetSerializer();
		// Register those Charset classes which are not public.
		registerCharsets(kryo, "UTF-8", serializer);
		registerCharsets(kryo, "UTF-16", serializer);
		registerCharsets(kryo, "UTF-16BE", serializer);
		registerCharsets(kryo, "UTF-16LE", serializer);
		registerCharsets(kryo, "ISO_8859_1", serializer);
	}

}
