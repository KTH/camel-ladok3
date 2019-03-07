/*
 * MIT License
 *
 * Copyright (c) 2017 Kungliga Tekniska högskolan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package se.kth.infosys.smx.ladok3;

import org.apache.camel.Message;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("Does not work without connection to Ladok")
public class Ladok3ComponentTest extends CamelBlueprintTestSupport {
    private Message message = null;

    @Override
    protected String[] loadConfigAdminConfigurationFile() {
        // which .cfg file to use, and the name of the persistence-id
        return new String[]{"test.properties", "se.kth.infosys.smx.ladok3"};
    }

    @Override
    protected String getBlueprintDescriptor() {
        return "/OSGI-INF/blueprint/blueprint.xml";
    }

    @Test
    public void testladok3() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.setExpectedMessageCount(20);
        mock.expectedHeaderValuesReceivedInAnyOrder(Ladok3Message.Header.SequenceNumber, 0, 1);

        assertMockEndpointsSatisfied();

        message = mock.getExchanges().get(0).getIn();
        assertTrue(message.getHeader("ladok3MessageType", String.class).equals("ladok3FeedStart"));
        assertTrue(message.getHeader("ladok3IsLastFeed", Boolean.class).equals(false));
        assertTrue(message.getHeader(Ladok3Message.Header.Feed).equals("https://api.mit-integration.ladok.se:443/uppfoljning/feed/1"));
    }
}
