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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;

/**
 * TODO
 *
 * @author Alin Dreghiciu (adreghiciu@gmail.com)
 * @since 1.0.0, November 07, 2011
 */
public abstract class ShamBundle
    implements Bundle
{

    // ----------------------------------------------------------------------
    // Implementation fields
    // ----------------------------------------------------------------------

    /**
     * Bundle headers. Lazy initialized.
     */
    private Properties headers;

    /**
     * Bundle context of this bundle. Injected by {@link ShamFramework} on bundle creation.
     */
    private ShamBundleContext bundleContext;

    /**
     * Id of this bundle. Injected by {@link ShamFramework} on bundle creation.
     */
    private long id;

    /**
     * Bundle exports. Lazy initialized.
     */
    private List<String> exports;

    /**
     * Bundle state. Defaults to {@link Bundle#INSTALLED}.
     */
    private int state;

    /**
     * Bundle
     */
    private List<BundleListener> bundleListeners;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    /**
     * Only {@link ShamFramework} should create sham bundles.
     */
    ShamBundle()
    {

    }

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    /**
     * Builder method for specifying bundle symbolic name.
     *
     * @param bsn bundle symbolic name
     * @return itself, for fluent api usage
     */
    public ShamBundle withBundleSymbolicName( final String bsn )
    {
        getHeadersAsProperties().setProperty( Constants.BUNDLE_SYMBOLICNAME, bsn );
        return this;
    }

    /**
     * Builder method for specifying exported packages.
     *
     * @param exports exported packages
     * @return itself, for fluent api usage
     */
    public ShamBundle withPackages( final String... exports )
    {
        this.getExports().addAll( Arrays.asList( exports ) );
        getHeadersAsProperties().setProperty( Constants.EXPORT_PACKAGE, exports() );
        return this;
    }

    /**
     * Builder method for specifying bundle state.
     *
     * @param state bundle state
     * @return itself, for fluent api usage
     */
    public ShamBundle setState( int state )
    {
        this.state = state;
        return this;
    }

    @Override
    public long getBundleId()
    {
        return id;
    }

    @Override
    public ShamBundleContext getBundleContext()
    {
        return bundleContext;
    }

    @Override
    public Dictionary getHeaders()
    {
        return getHeadersAsProperties();
    }

    @Override
    public int getState()
    {
        if ( state == 0 )
        {
            state = Bundle.INSTALLED;
        }
        return state;
    }

    @Override
    public void start()
        throws BundleException
    {
        setState( Bundle.STARTING );
        sendBundleEventOfType( BundleEvent.STARTING );
        setState( Bundle.ACTIVE );
        sendBundleEventOfType( BundleEvent.STARTED );
    }

    @Override
    public void start( final int i )
        throws BundleException
    {
        start();
    }

    @Override
    public void stop()
        throws BundleException
    {
        setState( Bundle.STOPPING );
        sendBundleEventOfType( BundleEvent.STOPPING );
        setState( Bundle.INSTALLED );
        sendBundleEventOfType( BundleEvent.STOPPED );
    }

    @Override
    public void stop( final int i )
        throws BundleException
    {
        stop();
    }

    public List<BundleListener> getBundleListeners()
    {
        if ( bundleListeners == null )
        {
            bundleListeners = new ArrayList<BundleListener>();
        }
        return bundleListeners;
    }

    // ----------------------------------------------------------------------
    // Implementation methods
    // ----------------------------------------------------------------------

    ShamBundle setBundleContext( final ShamBundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
        return this;
    }

    ShamBundle setBundleId( final long id )
    {
        this.id = id;
        return this;
    }

    private String exports()
    {
        final StringBuilder sb = new StringBuilder();
        for ( final String export : getExports() )
        {
            if ( sb.length() > 0 )
            {
                sb.append( "," );
            }
            sb.append( export );
        }
        return sb.toString();
    }

    private List<String> getExports()
    {
        if ( exports == null )
        {
            exports = new ArrayList<String>();
        }
        return exports;
    }

    private Properties getHeadersAsProperties()
    {
        if ( headers == null )
        {
            headers = new Properties();
        }
        return headers;
    }

    private void sendBundleEventOfType( final int type )
    {
        for ( final BundleListener bundleListener : getBundleListeners() )
        {
            bundleListener.bundleChanged( new BundleEvent( type, this ) );
        }
    }

}
