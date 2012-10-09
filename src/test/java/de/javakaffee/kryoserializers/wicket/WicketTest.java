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
package de.javakaffee.kryoserializers.wicket;

import java.io.ByteArrayOutputStream;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

import de.javakaffee.kryoserializers.KryoReflectionFactorySupport;
import de.javakaffee.kryoserializers.KryoTest;

/**
 * A general test for several wicket serializations that don't require
 * specific serializers.
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class WicketTest {

    public static final String SERIALIZED_CLASS_NAME = MarkupContainer.class.getName() + "$ChildList";
    
    private Kryo _kryo;
    private WicketTester _wicketTester;

    @BeforeTest
    protected void beforeTest() {
        _kryo = new KryoReflectionFactorySupport() {
            
            
            @SuppressWarnings("rawtypes")
			@Override
            public Serializer<?> newSerializer(
            		Class<? extends Serializer> serializerClass, Class type) {
            	if ( SERIALIZED_CLASS_NAME.equals( type.getName() ) ) {
                    return new FieldSerializer( this, type );
                }
            	return super.newSerializer(serializerClass, type);
            }
            
            @SuppressWarnings("rawtypes")
			@Override
            protected Serializer<?> newDefaultSerializer( final Class type ) {
                final FieldSerializer<?> result = new FieldSerializer( this, type );
                result.setIgnoreSyntheticFields( false );
                return result;
            }
            
        };
        _kryo.setRegistrationRequired( false);
        
        final WebApplication application = new WebApplication() {			
            
            @Override
            public Class<? extends Page> getHomePage() {
                return TestPage.class;
            }
            
        };

        _wicketTester = new WicketTester( application );
    }
    
    @AfterTest
    protected void afterTest() {
        _wicketTester.destroy();
    }

    /**
     * Tests that MarkupContainer.ChildList is serialized/deserialized correctly.
     * It needs ReflectionFactory support, ReferenceFieldSerializer as default
     * serializer and the FieldSerializer for MarkupContainer.ChildList (instead of
     * default CollectionSerializer).
     * 
     * @throws Exception
     */
    @Test( enabled = true )
    public void testMarkupContainerChildList() throws Exception {
        final MarkupContainer markupContainer = new WebMarkupContainer("foo");
        markupContainer.add( new Label( "label1", "foo" ) );
        markupContainer.add( new Label( "label", "hello" ) );
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream);        
        _kryo.writeObject(output, markupContainer);
        output.close();
        final byte[] serialized = outputStream.toByteArray();
        Input input = new Input(serialized);      
        final MarkupContainer deserialized = _kryo.readObject(input, markupContainer.getClass() );
        input.close();
        KryoTest.assertDeepEquals( deserialized, markupContainer );
    }

    @Test( enabled = true )
    public void testFeedbackPanel() throws Exception {
        final FeedbackPanel markupContainer = new FeedbackPanel("foo");
        //markupContainer.info( "foo" );
        final Component child = markupContainer.get( 0 );
        child.isVisible();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream);        
        _kryo.writeObject(output, markupContainer);
        output.close();
        final byte[] serialized = outputStream.toByteArray();
        Input input = new Input(serialized);
  
        final MarkupContainer deserialized = _kryo.readObject(input, markupContainer.getClass() );
        input.close();
        final Component deserializedChild = deserialized.get( 0 );
        deserializedChild.isVisible();
        
        KryoTest.assertDeepEquals( deserialized, markupContainer );
    }
    
    @Test( enabled = true )
    public void testTestPanel() throws Exception {
        TestPage markupContainer = new TestPage();
        //markupContainer.info( "foo" );
        Component child = markupContainer.get( 0 );
        child.isVisible();
     
        markupContainer = (TestPage)_wicketTester.startPage(markupContainer);
        byte[] serialized = KryoUtils.serialize(_kryo, markupContainer);
        TestPage deserialized = KryoUtils.deserialize(_kryo, serialized, markupContainer.getClass() );
        Component deserializedChild = deserialized.get( 0 );
        deserializedChild.isVisible();
        
        KryoTest.assertDeepEquals( deserialized, markupContainer );
        
        Assert.assertEquals(markupContainer.getPanel().getTestEnum(), TestEnum.A);        
        Assert.assertEquals(markupContainer.getPanel().getDefaultModelObject(), "test");
        
        _wicketTester.executeAjaxEvent(markupContainer.getPanel().getAjaxLink(), "onclick");
        
        serialized = KryoUtils.serialize(_kryo, markupContainer);
        deserialized = KryoUtils.deserialize(_kryo, serialized, markupContainer.getClass() );
        
        Assert.assertEquals(markupContainer.getPanel().getTestEnum(), TestEnum.b);        
        Assert.assertEquals(markupContainer.getPanel().getDefaultModelObject(), "test");
        

    }
    
    
    @Test( enabled = true )
    public void testTestFormPanel() throws Exception {
        TestFormPage markupContainer = new TestFormPage();
        //markupContainer.info( "foo" );
        Component child = markupContainer.get( 0 );
        child.isVisible();
     
        markupContainer = (TestFormPage)_wicketTester.startPage(markupContainer);
        byte[] serialized = KryoUtils.serialize(_kryo, markupContainer);
        TestFormPage deserialized = KryoUtils.deserialize(_kryo, serialized, markupContainer.getClass() );
        Component deserializedChild = deserialized.get( 0 );
        deserializedChild.isVisible();
        
        KryoTest.assertDeepEquals( deserialized, markupContainer );
        
        _wicketTester.executeListener(markupContainer.getPanel().getForm());
        
        serialized = KryoUtils.serialize(_kryo, markupContainer);
        deserialized = KryoUtils.deserialize(_kryo, serialized, markupContainer.getClass() );
        
        KryoTest.assertDeepEquals( deserialized, markupContainer );       
    }
    

}
