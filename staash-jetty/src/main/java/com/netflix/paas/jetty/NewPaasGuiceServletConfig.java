/*******************************************************************************
 * /***
 *  *
 *  *  Copyright 2013 Netflix, Inc.
 *  *
 *  *     Licensed under the Apache License, Version 2.0 (the "License");
 *  *     you may not use this file except in compliance with the License.
 *  *     You may obtain a copy of the License at
 *  *
 *  *         http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *     Unless required by applicable law or agreed to in writing, software
 *  *     distributed under the License is distributed on an "AS IS" BASIS,
 *  *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *     See the License for the specific language governing permissions and
 *  *     limitations under the License.
 *  *
 ******************************************************************************/
package com.netflix.paas.jetty;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.netflix.governator.guice.LifecycleInjector;
import com.netflix.paas.cassandra.MetaCassandraBootstrap;
import com.netflix.paas.cassandra.MetaModule;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class NewPaasGuiceServletConfig extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return LifecycleInjector.builder()
            .withModules(
                new MetaModule(),
                //new EurekaModule(),
                new JerseyServletModule() {
                    @Override
                    protected void configureServlets() {
                        // Route all requests through GuiceContainer
                        bind(GuiceContainer.class).asEagerSingleton();
                        serve("/*").with(GuiceContainer.class);
                    }
                },
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(MetaCassandraBootstrap.class).asEagerSingleton();
                     }
                }
            )
            .createInjector();
    }
}
