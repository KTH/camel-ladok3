package se.kth.infosys.smx.ladok3;
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
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.json.simple.JSONObject;

/**
 * A ListDataSet that reads JSON encoded Ladok3 payloads from a file.
 * 
 * The file is assumed to contain a JSON array of objects representing Ladok3
 * ATOM events.
 * 
 * Relevant headers will be added to messages produced by this dataset.
 * 
 * Example of use:
 * <pre>
 * &lt;bean id="dataSet" class="se.kth.infosys.smx.ladok3.Ladok3JsonDataSet"&gt;
 *   &lt;property name="sourceFile" value="classpath:ladok3-data.json"/&gt;
 *   &lt;property name="size" value="32"/&gt;
 * &lt;/bean&gt;
 * ...
 * &lt;from uri="dataset:dataSet" /&gt;
 * </pre>
 */
public class Ladok3JsonDataSet extends JsonDataSet {
    public Ladok3JsonDataSet() {}

    /**
     * Constructor taking the name of a source file.
     * @param sourceFileName the name of the file.
     * @throws Exception on file access and parse errors.
     */
    public Ladok3JsonDataSet(String sourceFileName) throws Exception {
        super(sourceFileName);
    }

    /**
     * Constructor taking a File object.
     * @param sourceFile the File object.
     * @throws Exception on file access and parse errors.
     */
    public Ladok3JsonDataSet(File sourceFile) throws Exception {
        super(sourceFile);
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    protected void applyHeaders(Exchange exchange, long messageIndex) {
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put(Ladok3Message.Header.MessageType, Ladok3Message.MessageType.Event);
        headers.put(Ladok3Message.Header.SequenceNumber, messageIndex);
        headers.put(Ladok3Message.Header.IsLastFeed, true);

        JSONObject jsonObject = (JSONObject) getJsonObjects().get((int) messageIndex);
        JSONObject eventHeaders = (JSONObject) jsonObject.get("headers");
        for (Object key : eventHeaders.keySet()) {
            headers.put((String) key, (String) eventHeaders.get(key));
        }

/*
        headers.put(Ladok3Message.Header.EntryId, entryId);
        headers.put(Ladok3Message.Header.EntryUpdated, StockholmLocalDateTimeFormatter.formatAsStockolmLocalDateTime(entryUpdated));
        headers.put(Ladok3Message.Header.Feed, feed.getURL().toString());
        headers.put(Ladok3Message.Header.IsLastFeed, feed.isLast());
        headers.put(Ladok3Message.Header.EventType, event.getClass().getName());
        headers.put(Ladok3Message.Header.EventId, event.getHandelseUID());
        headers.put(Ladok3Message.Header.EntryItemIndex,  atomItemIndex);
*/
        exchange.getIn().setHeaders(headers);
    }
}
