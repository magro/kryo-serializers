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
package de.javakaffee.kryoserializers.spring;

import static de.javakaffee.kryoserializers.KryoTest.assertDeepEquals;
import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;
import static de.javakaffee.kryoserializers.spring.SpringTest.HelloWorldService.SRVC_ID;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Collection;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;

import de.javakaffee.kryoserializers.CopyForIterateCollectionSerializer;

/**
 * A general test for several wicket serializations that don't require specific
 * serializers.
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class SpringTest {

	private ClassPathXmlApplicationContext context;

	@BeforeTest
	protected void beforeTest() {

		/**
		 * Create a new ApplicationContext, loading the definitions from the
		 * given XML file
		 */
		context = new ClassPathXmlApplicationContext("applicationContext.xml");
	}

	private Kryo createKryo() {
		final Kryo kryo = new Kryo() {
			@Override
			@SuppressWarnings({ "rawtypes" })
			public Serializer<?> getDefaultSerializer(final Class type) {
				if (Collection.class.isAssignableFrom(type)) {
					return new CopyForIterateCollectionSerializer();
				}
				if (DefaultListableBeanFactory.class.isAssignableFrom(type)) {
					return new JavaSerializer();
				}
				return super.getDefaultSerializer(type);
			}
		};
		kryo.setRegistrationRequired(false);
		return kryo;
	}

	@AfterTest
	protected void afterTest() {
		context.close();
	}

	@Test
	public void testSerializeApplicationContext() throws Exception {
		final HelloWorldService obj = (HelloWorldService) context.getBean(SRVC_ID);
		final String name = "hello spring";
		obj.setName(name);

		final MyPojo bean = context.getBean(MyPojo.class);
		assertNotNull(bean.beanFactory);
		assertEquals(
				((HelloWorldService) bean.beanFactory.getBean(SRVC_ID)).getName(),
				name);

		// Use different kryo instances to simulate real world (so that references etc are not shared).
		final MyPojo deserialized = deserialize(createKryo(), serialize(createKryo(), new Output(4094, -1), bean), MyPojo.class);
		assertDeepEquals(deserialized, bean);
		assertNotNull(deserialized.beanFactory);
		assertEquals(
				((HelloWorldService) deserialized.beanFactory.getBean(SRVC_ID)).getName(),
				name);
	}

	@org.springframework.stereotype.Component
	static class MyPojo implements BeanFactoryAware {

		public BeanFactory beanFactory;

		@Override
		public void setBeanFactory(final BeanFactory beanFactory)
				throws BeansException {
			this.beanFactory = beanFactory;
		}

	}

	@org.springframework.stereotype.Component(HelloWorldService.SRVC_ID)
	static class HelloWorldService {

		static final String SRVC_ID = "helloWorldService";

		private String name;

		public void setName(final String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}

}
