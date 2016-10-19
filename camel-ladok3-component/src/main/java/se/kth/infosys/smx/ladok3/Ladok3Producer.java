/*
 * MIT License
 *
 * Copyright (c) 2016 Kungliga Tekniska högskolan
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

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.infosys.ladok3.Ladok3StudentInformationService;
import se.ladok.schemas.studentinformation.Student;

/**
 * The ladok3 producer.
 */
public class Ladok3Producer extends DefaultProducer {
    private static final Logger logger = LoggerFactory.getLogger(Ladok3Producer.class);
    private final Ladok3StudentInformationService studentInformationService;

    public Ladok3Producer(Ladok3Endpoint endpoint) throws Exception {
        super(endpoint);
        studentInformationService = new Ladok3StudentInformationService(endpoint.getHost(), endpoint.getCert(), endpoint.getKey());
    }

    public void process(Exchange exchange) throws Exception {
        Object object = exchange.getIn().getBody();
        
        if (object instanceof Student) {
            Student student = (Student) object;

            logger.debug("Getting Ladok data for student: " + student.getUid());
            Student fromLadok = studentInformationService.studentUID(student.getUid());
            exchange.getOut().setBody(fromLadok);
        }
    }
}
