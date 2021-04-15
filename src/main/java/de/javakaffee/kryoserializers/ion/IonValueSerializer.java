package de.javakaffee.kryoserializers.ion;

import com.amazon.ion.IonDatagram;
import com.amazon.ion.IonSystem;
import com.amazon.ion.IonValue;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.reflections.Reflections;

import java.util.Set;

/**
 * A kryo serializer implementation for Amazon Ion types.
 *
 * The specs for data type: https://amzn.github.io/ion-docs/
 * Github repo for java binding of amzn ion: https://github.com/amzn/ion-java
 */
public class IonValueSerializer extends Serializer<IonValue> {
    private final IonSystem _system;

    public IonValueSerializer(IonSystem system) {
        this._system = system;
        setImmutable(true);
    }

    @Override
    public void write(Kryo kryo, Output output, IonValue value) {
        IonDatagram dg = _system.newDatagram(value);
        int size = dg.byteSize();
        byte[] bytes = dg.getBytes();
        output.write(size);
        output.write(bytes);
        output.flush();
    }

    @Override
    public IonValue read(Kryo kryo, Input input, Class<? extends IonValue> type) {
        int size = input.read();
        byte[] bytes = input.readBytes(size);
        return _system.singleValue(bytes);
    }

    /**
     * A utility method to get all subclasses of IonValue. The concrete subclasses
     * are not publicly exposed, only the interfaces are. So, need this to register
     * the classes.
     *
     * @return Set of all subclasses of IonValue
     */
    public static Set<Class<? extends IonValue>> getAllSubclasses() {
        Reflections reflections = new Reflections(IonValue.class.getPackage().getName());
        return reflections.getSubTypesOf(IonValue.class);
    }
}
