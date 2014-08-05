/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.transport.amqp;

import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.wireformat.WireFormatFactory;

/**
 * Creates WireFormat objects that marshalls the <a href="http://stomp.codehaus.org/">Stomp</a> protocol.
 */
public class AmqpWireFormatFactory implements WireFormatFactory {

    private long maxInactivityDuration = 0;

    @Override
    public WireFormat createWireFormat() {
        AmqpWireFormat amqpWireFormat = new AmqpWireFormat();
        amqpWireFormat.setMaxInactivityDuration(getMaxInactivityDuration());
        return amqpWireFormat;
    }

    public long getMaxInactivityDuration() {
        System.out.println("Get maxInactivityDuration");
        return maxInactivityDuration;
    }

    public void setMaxInactivityDuration(long maxInactivityDuration) {
        System.out.println("Set maxInactivityDuration to " + maxInactivityDuration);
        this.maxInactivityDuration = maxInactivityDuration;
    }
}
