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

package org.ops4j.pax.sham.core.foo.internal;

import org.ops4j.pax.sham.core.foo.HelloWorld;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Alin Dreghiciu (adreghiciu@gmail.com)
 * @since 1.0.0, November 07, 2011
 */
public class BundleListenerRegisteringServiceActivator
    implements BundleActivator
{

    private ServiceRegistration serviceRegistration;

    private BundleListener bundleListener;

    @Override
    public void start( final BundleContext bundleContext )
        throws Exception
    {
        bundleContext.addBundleListener(
            bundleListener = new BundleListener()
            {
                @Override
                public void bundleChanged( final BundleEvent bundleEvent )
                {
                    switch ( bundleEvent.getType() )
                    {
                        case BundleEvent.STARTED:
                            serviceRegistration = bundleContext.registerService(
                                HelloWorld.class.getName(), new HelloWorldImpl(), null
                            );
                            break;

                        case BundleEvent.STOPPED:
                            serviceRegistration.unregister();
                            break;
                    }
                }
            }
        );
    }

    @Override
    public void stop( final BundleContext bundleContext )
        throws Exception
    {
        bundleContext.removeBundleListener( bundleListener );
    }

}
