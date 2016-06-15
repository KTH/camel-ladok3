package se.kth.infosys.smx.ladok3;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class ladok3ComponentTest extends CamelTestSupport {

    @Test
    public void testladok3() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);       
        
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("ladok3://foo")
                  .to("ladok3://bar")
                  .to("mock:result");
            }
        };
    }
}
