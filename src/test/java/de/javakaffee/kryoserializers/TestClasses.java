/*
 * Copyright 2010 Martin Grotzke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package de.javakaffee.kryoserializers;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.mutable.MutableInt;

import com.google.common.base.Charsets;

import de.javakaffee.kryoserializers.TestClasses.Person.Gender;

/**
 * Test utilities
 *
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
@SuppressWarnings({ "SameParameterValue", "WeakerAccess", "unused" })
public class TestClasses {

	static Person createPerson(final String name, final Gender gender, final String... emailAddresses) {
		final Person person = new Person();
		person.setName(name);
		person.setGender(gender);
		if (emailAddresses != null) {
			final HashMap<String, Object> props = new HashMap<>();
			for (int i = 0; i < emailAddresses.length; i++) {
				final String emailAddress = emailAddresses[i];
				props.put("email" + i, new Email(name, emailAddress));
			}
			person.setProps(props);
		}
		return person;
	}

	static Person createPerson(final String name, final Gender gender, final Integer age,
			final String... emailAddresses) {
		final Person person = new Person();
		person.setName(name);
		person.setGender(gender);
		person.setAge(age);
		final HashMap<String, Object> props = new HashMap<>();
		for (int i = 0; i < emailAddresses.length; i++) {
			final String emailAddress = emailAddresses[i];
			props.put("email" + i, new Email(name, emailAddress));
		}
		person.setProps(props);
		return person;
	}

	static ClassWithoutDefaultConstructor createClassWithoutDefaultConstructor(final String string) {
		return new ClassWithoutDefaultConstructor(string);
	}

	static PrivateClass createPrivateClass(final String string) {
		final PrivateClass result = new PrivateClass();
		result.foo = string;
		return result;
	}

	static Container createContainer() {
		return new Container();
	}

	static SomeInterface createProxy() {
		return (SomeInterface) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				new Class<?>[] { SomeInterface.class, Serializable.class },
				new MyInvocationHandler(SomeInterfaceImpl.class));
	}

	interface SomeInterface {
		String hello();
	}

	static class MyInvocationHandler implements InvocationHandler {

		private final Class<?> _targetClazz;
		private transient Object _target;

		public MyInvocationHandler(final Class<?> targetClazz) {
			_targetClazz = targetClazz;
		}

		public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
			if (_target == null) {
				_target = _targetClazz.newInstance();
			}
			return method.invoke(_target, args);
		}
	}

	static class SomeInterfaceImpl implements SomeInterface {

		/**
		 * {@inheritDoc}
		 */
		public String hello() {
			return "hi";
		}

	}

	public static class Container {

		@SuppressWarnings("unused")
		private final Body _body;

		public Container() {
			_body = new Body();
		}

		class Body {
		}

	}

	public static class Person implements Serializable {

		private static final long serialVersionUID = 1L;
		private final Collection<Person> _friends = new ArrayList<>();
		private String _name;
		private Gender _gender;
		private Integer _age;
		private Map<String, Object> _props;

		public String getName() {
			return _name;
		}

		public void setName(final String name) {
			_name = name;
		}

		public void addFriend(final Person p) {
			_friends.add(p);
		}

		public Map<String, Object> getProps() {
			return _props;
		}

		public void setProps(final Map<String, Object> props) {
			_props = props;
		}

		public Gender getGender() {
			return _gender;
		}

		public void setGender(final Gender gender) {
			_gender = gender;
		}

		public Integer getAge() {
			return _age;
		}

		public void setAge(final Integer age) {
			_age = age;
		}

		public Collection<Person> getFriends() {
			return _friends;
		}

		private boolean flatEquals(final Collection<?> c1, final Collection<?> c2) {
			return c1 == c2 || c1 != null && c2 != null && c1.size() == c2.size();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((_age == null) ? 0 : _age.hashCode());
			result = prime * result + _friends.size();
			result = prime * result + ((_gender == null) ? 0 : _gender.hashCode());
			result = prime * result + ((_name == null) ? 0 : _name.hashCode());
			result = prime * result + ((_props == null) ? 0 : _props.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Person other = (Person) obj;
			if (_age == null) {
				if (other._age != null) {
					return false;
				}
			} else if (!_age.equals(other._age)) {
				return false;
			}
			if (!flatEquals(_friends, other._friends)) {
				return false;
			}
			if (_gender == null) {
				if (other._gender != null) {
					return false;
				}
			} else if (!_gender.equals(other._gender)) {
				return false;
			}
			if (_name == null) {
				if (other._name != null) {
					return false;
				}
			} else if (!_name.equals(other._name)) {
				return false;
			}
			if (_props == null) {
				return other._props == null;
			} else
				return _props.equals(other._props);
		}

		@Override
		public String toString() {
			return "Person [_age=" + _age + ", _friends.size=" + _friends.size() + ", _gender=" + _gender + ", _name="
					+ _name + ", _props=" + _props + "]";
		}

		enum Gender {
			MALE,
			FEMALE
		}

	}

	public static class Email implements Serializable {

		private static final long serialVersionUID = 1L;

		private String _name;
		private String _email;

		public Email() {
		}

		public Email(final String name, final String email) {
			super();
			_name = name;
			_email = email;
		}

		public String getName() {
			return _name;
		}

		public void setName(final String name) {
			_name = name;
		}

		public String getEmail() {
			return _email;
		}

		public void setEmail(final String email) {
			_email = email;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((_email == null) ? 0 : _email.hashCode());
			result = prime * result + ((_name == null) ? 0 : _name.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Email other = (Email) obj;
			if (_email == null) {
				if (other._email != null) {
					return false;
				}
			} else if (!_email.equals(other._email)) {
				return false;
			}
			if (_name == null) {
				return other._name == null;
			} else
				return _name.equals(other._name);
		}

		@Override
		public String toString() {
			return "Email [_email=" + _email + ", _name=" + _name + "]";
		}

	}

	public static class PublicClass {
		PrivateClass privateClass;

		public PublicClass() {
		}

		public PublicClass(final PrivateClass protectedClass) {
			this.privateClass = protectedClass;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((privateClass == null) ? 0 : privateClass.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final PublicClass other = (PublicClass) obj;
			if (privateClass == null) {
				return other.privateClass == null;
			} else
				return privateClass.equals(other.privateClass);
		}
	}

	private static class PrivateClass {
		String foo;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((foo == null) ? 0 : foo.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final PrivateClass other = (PrivateClass) obj;
			if (foo == null) {
				return other.foo == null;
			} else
				return foo.equals(other.foo);
		}
	}

	public static class ClassWithoutDefaultConstructor {
		final String value;

		public ClassWithoutDefaultConstructor(final String value) {
			this.value = value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final ClassWithoutDefaultConstructor other = (ClassWithoutDefaultConstructor) obj;
			if (value == null) {
				return other.value == null;
			} else
				return value.equals(other.value);
		}

		@Override
		public String toString() {
			return "ClassWithoutDefaultConstructor [value=" + value + "]";
		}
	}

	@SuppressWarnings("unused")
	public static class MyContainer {

		private final boolean _boolean;
		private final Boolean _Boolean;
		private final Class<?> _Class;
		private final StringBuilder _StringBuilder;
		private final StringBuffer _StringBuffer;
		private final Currency _Currency;
		private final Set<String> _HashSet;
		private final Map<String, Integer> _HashMap;
		private int _int;
		private long _long;
		private String _String;
		private Long _Long;
		private Integer _Integer;
		private Character _Character;
		private Byte _Byte;
		private Double _Double;
		private Float _Float;
		private Short _Short;
		private BigDecimal _BigDecimal;
		private AtomicInteger _AtomicInteger;
		private AtomicLong _AtomicLong;
		private MutableInt _MutableInt;
		private Integer[] _IntegerArray;
		private Date _Date;
		private Calendar _Calendar;
		private List<String> _ArrayList;
		private int[] _intArray;
		private long[] _longArray;
		private short[] _shortArray;
		private float[] _floatArray;
		private double[] _doubleArray;
		private byte[] _byteArray;
		private char[] _charArray;
		private String[] _StringArray;
		private Person[] _PersonArray;

		@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
		public MyContainer() {

			_int = 1;
			_long = 2;
			_boolean = true;
			_Boolean = Boolean.TRUE;
			_Class = String.class;
			_String = "3";
			_StringBuffer = new StringBuffer("foo");
			_StringBuilder = new StringBuilder("foo");
			_Long = 4L;
			_Integer = 5;
			_Character = 'c';
			_Byte = "b".getBytes(Charsets.UTF_8)[0];
			_Double = 6d;
			_Float = 7f;
			_Short = (short) 8;
			_BigDecimal = new BigDecimal(9);
			_AtomicInteger = new AtomicInteger(10);
			_AtomicLong = new AtomicLong(11);
			_MutableInt = new MutableInt(12);
			_IntegerArray = new Integer[] { 13 };
			_Date = new Date(System.currentTimeMillis() - 10000);
			_Calendar = Calendar.getInstance();
			_Currency = Currency.getInstance("EUR");
			_ArrayList = new ArrayList<>(Arrays.asList("foo"));
			_HashSet = new HashSet<>();
			_HashSet.add("14");

			_HashMap = new HashMap<>();
			_HashMap.put("foo", 23);
			_HashMap.put("bar", 42);

			_intArray = new int[] { 1, 2 };
			_longArray = new long[] { 1, 2 };
			_shortArray = new short[] { 1, 2 };
			_floatArray = new float[] { 1, 2 };
			_doubleArray = new double[] { 1, 2 };
			_byteArray = "42".getBytes(Charsets.UTF_8);
			_charArray = "42".toCharArray();
			_StringArray = new String[] { "23", "42" };
			_PersonArray = new Person[] { createPerson("foo bar", Gender.MALE, 42) };

		}

		public int getInt() {
			return _int;
		}

		public void setInt(final int i) {
			_int = i;
		}

		public long getLong() {
			return _long;
		}

		public void setLong(final long l) {
			_long = l;
		}

		public String getString() {
			return _String;
		}

		public void setString(final String string) {
			_String = string;
		}

		public Long getLongWrapper() {
			return _Long;
		}

		public void setLongWrapper(final Long l) {
			_Long = l;
		}

		public Integer getInteger() {
			return _Integer;
		}

		public void setInteger(final Integer integer) {
			_Integer = integer;
		}

		public Character getCharacter() {
			return _Character;
		}

		public void setCharacter(final Character character) {
			_Character = character;
		}

		public Byte getByte() {
			return _Byte;
		}

		public void setByte(final Byte b) {
			_Byte = b;
		}

		public Double getDouble() {
			return _Double;
		}

		public void setDouble(final Double d) {
			_Double = d;
		}

		public Float getFloat() {
			return _Float;
		}

		public void setFloat(final Float f) {
			_Float = f;
		}

		public Short getShort() {
			return _Short;
		}

		public void setShort(final Short s) {
			_Short = s;
		}

		public BigDecimal getBigDecimal() {
			return _BigDecimal;
		}

		public void setBigDecimal(final BigDecimal bigDecimal) {
			_BigDecimal = bigDecimal;
		}

		public AtomicInteger getAtomicInteger() {
			return _AtomicInteger;
		}

		public void setAtomicInteger(final AtomicInteger atomicInteger) {
			_AtomicInteger = atomicInteger;
		}

		public AtomicLong getAtomicLong() {
			return _AtomicLong;
		}

		public void setAtomicLong(final AtomicLong atomicLong) {
			_AtomicLong = atomicLong;
		}

		public MutableInt getMutableInt() {
			return _MutableInt;
		}

		public void setMutableInt(final MutableInt mutableInt) {
			_MutableInt = mutableInt;
		}

		public Integer[] getIntegerArray() {
			return _IntegerArray;
		}

		public void setIntegerArray(final Integer[] integerArray) {
			_IntegerArray = integerArray;
		}

		public Date getDate() {
			return _Date;
		}

		public void setDate(final Date date) {
			_Date = date;
		}

		public Calendar getCalendar() {
			return _Calendar;
		}

		public void setCalendar(final Calendar calendar) {
			_Calendar = calendar;
		}

		public List<String> getArrayList() {
			return _ArrayList;
		}

		public void setArrayList(final List<String> arrayList) {
			_ArrayList = arrayList;
		}

		public int[] getIntArray() {
			return _intArray;
		}

		public void setIntArray(final int[] intArray) {
			_intArray = intArray;
		}

		public long[] getLongArray() {
			return _longArray;
		}

		public void setLongArray(final long[] longArray) {
			_longArray = longArray;
		}

		public short[] getShortArray() {
			return _shortArray;
		}

		public void setShortArray(final short[] shortArray) {
			_shortArray = shortArray;
		}

		public float[] getFloatArray() {
			return _floatArray;
		}

		public void setFloatArray(final float[] floatArray) {
			_floatArray = floatArray;
		}

		public double[] getDoubleArray() {
			return _doubleArray;
		}

		public void setDoubleArray(final double[] doubleArray) {
			_doubleArray = doubleArray;
		}

		public byte[] getByteArray() {
			return _byteArray;
		}

		public void setByteArray(final byte[] byteArray) {
			_byteArray = byteArray;
		}

		public char[] getCharArray() {
			return _charArray;
		}

		public void setCharArray(final char[] charArray) {
			_charArray = charArray;
		}

		public String[] getStringArray() {
			return _StringArray;
		}

		public void setStringArray(final String[] stringArray) {
			_StringArray = stringArray;
		}

		public Person[] getPersonArray() {
			return _PersonArray;
		}

		public void setPersonArray(final Person[] personArray) {
			_PersonArray = personArray;
		}

		public Set<String> getHashSet() {
			return _HashSet;
		}

		public Map<String, Integer> getHashMap() {
			return _HashMap;
		}

	}

	static class Holder<T> {
		T item;

		/**
		 * Default constructor, added for kryo...
		 */
		public Holder() {
		}

		public Holder(final T item) {
			this.item = item;
		}
	}

	static class HolderList<T> {
		List<Holder<T>> holders;

		public HolderList(final List<Holder<T>> holders) {
			this.holders = holders;
		}
	}

	static class CounterHolder {
		AtomicInteger item;

		public CounterHolder(final AtomicInteger item) {
			this.item = item;
		}
	}

	static class CounterHolderArray {
		CounterHolder[] holders;

		public CounterHolderArray(final CounterHolder... holders) {
			this.holders = holders;
		}
	}

	static class HolderArray<T> {
		Holder<T>[] holders;

		@SafeVarargs
		public HolderArray(final Holder<T>... holders) {
			this.holders = holders;
		}
	}

	public static class HashMapWithIntConstructorOnly extends HashMap<Object, Object> {

		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unused")
		private HashMapWithIntConstructorOnly() {
		}

		public HashMapWithIntConstructorOnly(final int size) {
			super(size);
		}

	}

}
