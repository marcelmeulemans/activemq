/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.activemq.transport.amqp;

import static org.junit.Assert.assertTrue;

import java.util.UUID;
import java.util.Vector;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.Wait;
import org.apache.qpid.proton.messenger.Messenger;
import org.apache.qpid.proton.messenger.impl.MessengerImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JProtonClientTest {
    private static final Logger LOG = LoggerFactory.getLogger(JProtonClientTest.class);
    BrokerService brokerService;
    Vector<Throwable> exceptions = new Vector<Throwable>();

    @Before
    public void startBroker() throws Exception {
        exceptions.clear();
        brokerService = new BrokerService();
        brokerService.setPersistent(false);
        brokerService.setAdvisorySupport(false);
    }

    @After
    public void stopBroker() throws Exception {
        if (brokerService != null) {
            brokerService.stop();
        }
    }

    @Test
    public void testInactivityMonitor() throws Exception {

        brokerService.addConnector("amqp://localhost:1234?wireFormat.maxInactivityDuration=1000");
        brokerService.start();

        Thread t1 = new Thread() {
            Messenger mng = new MessengerImpl();

            @Override
            public void run() {

                try {
                    LOG.info("Starting");
                    mng.setBlocking(false);
                    mng.start();
                    mng.subscribe("amqp://localhost:1234/queue://" + UUID.randomUUID().toString());
                    while (mng.incoming() < 1) {
                        mng.recv();
                    }

                } catch (Exception ex) {
                    LOG.error("unexpected exception on connect/disconnect", ex);
                    exceptions.add(ex);
                }
            }
        };

        t1.start();

        assertTrue("one connection", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return 1 == brokerService.getTransportConnectors().get(0).connectionCount();
            }
        }));
        LOG.info("Got one connection");
        // and it should be closed due to inactivity
        assertTrue("no dangling connections", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return 0 == brokerService.getTransportConnectors().get(0).connectionCount();
            }
        }));
        LOG.info("Zero connections");
        assertTrue("no exceptions", exceptions.isEmpty());
    }
}
