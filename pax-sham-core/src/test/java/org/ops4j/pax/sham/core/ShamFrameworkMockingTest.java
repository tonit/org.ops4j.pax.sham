/*
 * Copyright 2011 Alin Dreghiciu.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ops4j.pax.sham.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.ops4j.pax.sham.core.behavior.BundleListenerBehavior.applyBundleListenerBehavior;

import java.util.Dictionary;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.ops4j.pax.sham.core.foo.HelloWorld;
import org.ops4j.pax.sham.core.foo.internal.BundleListenerRegisteringServiceActivator;
import org.ops4j.pax.sham.core.foo.internal.HelloWorldImpl;
import org.ops4j.pax.sham.core.foo.internal.RegisteringServiceActivator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Alin Dreghiciu (adreghiciu@gmail.com)
 * @since 1.0.0, November 07, 2011
 */
public class ShamFrameworkMockingTest
{

    @Test
    public void activatorRegisteringAService()
        throws Exception
    {
        final ShamBundle foo = new ShamFramework().installBundle();
        final ShamBundleContext bundleContext = foo.getBundleContext();
        final ServiceRegistration serviceRegistration = mock( ServiceRegistration.class );

        when( bundleContext.registerService( anyString(), any(), Matchers.<Dictionary>any() ) )
            .thenReturn( serviceRegistration );

        final BundleActivator activator = new RegisteringServiceActivator();

        activator.start( bundleContext );
        activator.stop( bundleContext );

        final ArgumentCaptor<String> typeCaptor = ArgumentCaptor.forClass( String.class );
        final ArgumentCaptor<HelloWorld> serviceCaptor = ArgumentCaptor.forClass( HelloWorld.class );

        verify( bundleContext ).registerService(
            typeCaptor.capture(), serviceCaptor.capture(), Matchers.<Dictionary>any()
        );

        assertThat( typeCaptor.getValue(), is( equalTo( HelloWorld.class.getName() ) ) );
        assertThat( serviceCaptor.getValue(), is( instanceOf( HelloWorldImpl.class ) ) );

        verify( serviceRegistration ).unregister();
    }

    @Test
    public void bundleListenerRegisteringAService()
        throws Exception
    {
        final ShamBundle foo = new ShamFramework().installBundle();
        final ShamBundleContext bundleContext = foo.getBundleContext();
        final ServiceRegistration serviceRegistration = mock( ServiceRegistration.class );

        final ArgumentCaptor<BundleListener> blCaptor = ArgumentCaptor.forClass( BundleListener.class );

        when( bundleContext.registerService( anyString(), any(), Matchers.<Dictionary>any() ) )
            .thenReturn( serviceRegistration );

        final BundleActivator activator = new BundleListenerRegisteringServiceActivator();

        activator.start( bundleContext );

        verify( bundleContext ).addBundleListener( blCaptor.capture() );

        foo.start();
        blCaptor.getValue().bundleChanged( new BundleEvent( BundleEvent.STARTED, foo ) );

        foo.stop();
        blCaptor.getValue().bundleChanged( new BundleEvent( BundleEvent.STOPPED, foo ) );

        activator.stop( bundleContext );

        final ArgumentCaptor<String> typeCaptor = ArgumentCaptor.forClass( String.class );
        final ArgumentCaptor<HelloWorld> serviceCaptor = ArgumentCaptor.forClass( HelloWorld.class );

        verify( bundleContext ).registerService(
            typeCaptor.capture(), serviceCaptor.capture(), Matchers.<Dictionary>any()
        );

        assertThat( typeCaptor.getValue(), is( equalTo( HelloWorld.class.getName() ) ) );
        assertThat( serviceCaptor.getValue(), is( instanceOf( HelloWorldImpl.class ) ) );

        verify( serviceRegistration ).unregister();
    }

    @Test
    public void bundleListenerRegisteringAServiceUsingBundleListenerBehavior()
        throws Exception
    {
        final ShamBundle foo = applyBundleListenerBehavior( new ShamFramework().installBundle() );

        final ShamBundleContext bundleContext = foo.getBundleContext();
        final ServiceRegistration serviceRegistration = mock( ServiceRegistration.class );

        when( bundleContext.registerService( anyString(), any(), Matchers.<Dictionary>any() ) )
            .thenReturn( serviceRegistration );

        final BundleActivator activator = new BundleListenerRegisteringServiceActivator();

        activator.start( bundleContext );

        verify( bundleContext ).addBundleListener( Matchers.<BundleListener>any() );

        foo.start();
        foo.stop();

        activator.stop( bundleContext );

        final ArgumentCaptor<String> typeCaptor = ArgumentCaptor.forClass( String.class );
        final ArgumentCaptor<HelloWorld> serviceCaptor = ArgumentCaptor.forClass( HelloWorld.class );

        verify( bundleContext ).registerService(
            typeCaptor.capture(), serviceCaptor.capture(), Matchers.<Dictionary>any()
        );

        assertThat( typeCaptor.getValue(), is( equalTo( HelloWorld.class.getName() ) ) );
        assertThat( serviceCaptor.getValue(), is( instanceOf( HelloWorldImpl.class ) ) );

        verify( serviceRegistration ).unregister();
    }

}
