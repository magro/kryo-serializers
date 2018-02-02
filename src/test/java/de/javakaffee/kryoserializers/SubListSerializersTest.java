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

import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;

/**
 * Test for {@link SubListSerializers}.
 *
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class SubListSerializersTest {

	private Kryo _kryo;

	@SafeVarargs
	private static <T> ArrayList<T> arrayList(final T... items) {
		return new ArrayList<>(Arrays.asList(items));
	}

	@BeforeClass
	public void beforeClass() {
		_kryo = new KryoReflectionFactorySupport() {

			@Override
			@SuppressWarnings("rawtypes")
			public Serializer<?> getDefaultSerializer(final Class type) {
				final Serializer<List<?>> subListSerializer = SubListSerializers.createFor(type);
				if (subListSerializer != null) {
					return subListSerializer;
				}
				return super.getDefaultSerializer(type);
			}

		};
	}

	private void doTest(final List<TestEnum> subList) {
		final byte[] serialized = serialize(_kryo, subList);
		@SuppressWarnings("unchecked")
		final List<TestEnum> deserialized = deserialize(_kryo, serialized, subList.getClass());

		assertEquals(deserialized, subList);
		assertEquals(deserialized.remove(0), subList.remove(0));
	}

	private void doTestCopy(final List<TestEnum> subList) {
		final List<TestEnum> copy = _kryo.copy(subList);

		assertEquals(copy, subList);
		assertEquals(copy.remove(0), subList.remove(0));
	}

	@Test()
	public void testSubList() {
		final List<TestEnum> subList = new LinkedList<>(Arrays.asList(TestEnum.values())).subList(1, 2);
		doTest(subList);
	}

	@Test()
	public void testCopySubList() {
		final List<TestEnum> subList = new LinkedList<>(Arrays.asList(TestEnum.values())).subList(1, 2);
		doTestCopy(subList);
	}

	@Test()
	public void testSubListSubList() {
		final List<TestEnum> subList = new LinkedList<>(Arrays.asList(TestEnum.values())).subList(1, 3).subList(1, 2);
		doTest(subList);
	}

	@Test()
	public void testCopySubListSubList() {
		final List<TestEnum> subList = new LinkedList<>(Arrays.asList(TestEnum.values())).subList(1, 3).subList(1, 2);
		doTestCopy(subList);
	}

	@Test()
	public void testArrayListSubList() {
		final List<TestEnum> subList = new ArrayList<>(Arrays.asList(TestEnum.values())).subList(1, 2);
		doTest(subList);
	}

	@Test()
	public void testCopyArrayListSubList() {
		final List<TestEnum> subList = new ArrayList<>(Arrays.asList(TestEnum.values())).subList(1, 2);
		doTestCopy(subList);
	}

	@Test()
	public void testArrayListSubListSubList() {
		final List<TestEnum> subList = new ArrayList<>(Arrays.asList(TestEnum.values())).subList(1, 3).subList(1, 2);
		doTest(subList);
	}

	@Test()
	public void testCopyArrayListSubListSubList() {
		final List<TestEnum> subList = new ArrayList<>(Arrays.asList(TestEnum.values())).subList(1, 3).subList(1, 2);
		doTestCopy(subList);
	}

	@Test()
	public void testArrayListSubListWithSharedItems() {
		final List<String> mylist = arrayList("1", "1", "2", "1", "1");
		final List<String> subList = mylist.subList(0, 5);

		final byte[] serialized = serialize(_kryo, subList);
		@SuppressWarnings("unchecked")
		final List<String> deserialized = deserialize(_kryo, serialized, subList.getClass());

		assertEquals(deserialized, subList);
		assertEquals(deserialized, mylist);
	}

	@Test()
	@SuppressWarnings({ "unchecked", "Duplicates" })
	public void testNestedArrayListSubListWithSharedItems_1() {
		final List<String> list1 = arrayList("1", "1", "2");
		final List<String> list1SubList1 = list1.subList(0, 3);

		final List<String> list1SubList2 = list1.subList(1, 3);

		final List<String> list2 = arrayList("1", "2", "3");
		final List<String> list2SubList1 = list2.subList(0, 3);

		final List<List<String>> lists =
				new ArrayList<>(Arrays.asList(list1SubList1, list1SubList2, list2SubList1, list1, list2));

		final byte[] serialized = serialize(_kryo, lists);
		final List<List<String>> deserialized = deserialize(_kryo, serialized, lists.getClass());

		assertEquals(deserialized, lists);
	}

	@Test()
	@SuppressWarnings({ "unchecked", "Duplicates" })
	public void testNestedArrayListSubListWithSharedItems_2() {
		final List<String> l1 = arrayList("1", "1", "2");
		final List<String> l1s1 = l1.subList(0, 3);

		final List<String> l1s2 = l1.subList(1, 3);

		final List<String> l2 = arrayList("1", "2", "3");
		final List<String> l2s1 = l2.subList(0, 3);

		final List<List<String>> lists = new ArrayList<>(Arrays.asList(l1, l2, l1s1, l1s2, l2s1));

		final byte[] serialized = serialize(_kryo, lists);
		final List<List<String>> deserialized = deserialize(_kryo, serialized, lists.getClass());

		assertEquals(deserialized, lists);
	}

	enum TestEnum {
		ITEM1,
		ITEM2,
		ITEM3
	}

}
