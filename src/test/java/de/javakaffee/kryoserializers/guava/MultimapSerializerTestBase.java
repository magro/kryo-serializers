/*
 * Copyright 2018 Martin Grotzke
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
package de.javakaffee.kryoserializers.guava;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.DataProvider;

import com.google.common.collect.Multimap;

@SuppressWarnings("unchecked")
public abstract class MultimapSerializerTestBase {

	protected <K, V> void populateMultimap(Multimap<K, V> multimap, Object[] contents) {
		for (int index = 0; index < contents.length; ) {
			multimap.put((K) contents[index++], (V) contents[index++]);
		}
	}

	protected <K, V> void assertEqualMultimaps(boolean orderedKeys, boolean orderedValues, Multimap<K, V> actual,
			Multimap<K, V> expected) {
		if (orderedKeys) {
			Assert.assertEquals(actual.keySet(), expected.keySet());
		} else {
			Assert.assertEqualsNoOrder(actual.keySet().toArray(), expected.keySet().toArray());
		}
		for (final K key : expected.keySet()) {
			if (orderedValues) {
				Assert.assertEquals(actual.get(key), expected.get(key));
			} else {
				Assert.assertEqualsNoOrder(actual.get(key).toArray(), expected.get(key).toArray());
			}
		}
	}

	@DataProvider(name = "Google Guava multimaps")
	public Object[][][] getMultimaps() {
		final Object[][] multimaps =
				new Object[][] { new Object[] {}, new Object[] { "foo", "bar" }, new Object[] { "foo", null },
						new Object[] { null, "bar" }, new Object[] { null, null },
						new Object[] { "new", Thread.State.NEW, "run", Thread.State.RUNNABLE },
						new Object[] { 1.0, "foo", null, "bar", 1.0, null, null, "baz", 1.0, "wibble" },
						new Object[] { 'a', 1, 'b', 2, 'c', 3, 'a', 4, 'b', 5 },
						new Object[] { 'a', 1, 'b', 2, 'c', 3, 'a', 1, 'b', 2 } };
		final Object[][][] toProvide = new Object[multimaps.length][][];
		int index = 0;
		for (final Object[] multimap : multimaps) {
			toProvide[index++] = new Object[][] { multimap };
		}
		return toProvide;
	}

	@DataProvider(name = "Google Guava multimaps (no nulls)")
	public Object[][][] getMultimapsNoNulls() {
		final List<Object[][]> multimaps = new ArrayList<Object[][]>();
		for (final Object[][] multimap : getMultimaps()) {
			boolean isNull = false;
			for (final Object element : multimap[0]) {
				if (element == null) {
					isNull = true;
					break;
				}
			}
			if (!isNull) {
				multimaps.add(multimap);
			}
		}
		return multimaps.toArray(new Object[multimaps.size()][][]);
	}
}
