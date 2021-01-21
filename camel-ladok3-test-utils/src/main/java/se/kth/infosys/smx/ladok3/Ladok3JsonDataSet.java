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
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.component.dataset.ListDataSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * A ListDataSet that reads JSON encoded Ladok3 payloads from a file.
 *
 * <p>The file is assumed to contain a JSON array of objects representing Ladok3
 * JSON encoded ATOM events. See README.md for details.</p>
 *
 * <p>Relevant headers will be added to messages produced by this dataset.</p>
 *
 * <p>Example of use:
 * <pre>
 * &lt;bean id="dataSet" class="se.kth.infosys.smx.ladok3.Ladok3JsonDataSet"&gt;
 *   &lt;property name="sourceFile" value="classpath:ladok3-data.json"/&gt;
 *   &lt;property name="size" value="32"/&gt;
 * &lt;/bean&gt;
 * ...
 * &lt;from uri="dataset:dataSet" /&gt;
 * </pre></p>
 */
public class Ladok3JsonDataSet extends ListDataSet {
  protected static final JSONParser parser = new JSONParser();

  protected JSONArray jsonObjects = new JSONArray();
  protected File sourceFile;

  /**
   * Default constructor.
   */
  public Ladok3JsonDataSet() {}

  /**
   * Constructor using a file name string.
   *
   * @param sourceFileName The file name.
   * @throws Exception on file access and parse problems.
   */
  public Ladok3JsonDataSet(String sourceFileName) throws Exception {
    this(new File(sourceFileName));
  }

  /**
   * Constructor using a File object.
   *
   * @param sourceFile the File.
   * @throws Exception on file access and parse problems.
   */
  public Ladok3JsonDataSet(File sourceFile) throws Exception {
    setSourceFile(sourceFile);
  }

  /**
   * Get the source file object.
   *
   * @return the source file.
   */
  public File getSourceFile() {
    return sourceFile;
  }

  /**
   * Set the source file object and intialize dataset from contents.
   *
   * @param sourceFile the source file object.
   * @throws Exception on file access and parse problems.
   */
  public void setSourceFile(File sourceFile) throws Exception {
    this.sourceFile = sourceFile;
    readSourceFile();
  }

  /**
   * Gets the internal JSONArray of JSON objects.
   *
   * @return the internal array of JSON objects.
   */
  public JSONArray getJsonObjects() {
    return jsonObjects;
  }

  /**
   * Sets the internal JSONArray of JSON objects.
   *
   * @param jsonObjects an array of JSON objects.
   */
  public void setJsonObjects(JSONArray jsonObjects) {
    this.jsonObjects = jsonObjects;
  }

  /**
   * Read the source file and intializes the internal list of message bodies.
   * Can be overridden by subclasses to tweak behaviour.
   *
   * @throws Exception on file access and parse problems.
   */
  protected void readSourceFile() throws Exception {
    List<Object> bodies = new LinkedList<>();
    jsonObjects = (JSONArray) parser.parse(new FileReader(sourceFile));

    for (Object object : jsonObjects) {
      JSONObject jsonObject = (JSONObject) object;
      JSONObject body = (JSONObject) jsonObject.get("body");
      bodies.add(body.toJSONString().getBytes());
    }
    setDefaultBodies(bodies);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void applyHeaders(Exchange exchange, long messageIndex) {
    Map<String, Object> headers = new HashMap<>();
    headers.put(Ladok3Message.Header.MessageType, Ladok3Message.MessageType.Event);
    headers.put(Ladok3Message.Header.SequenceNumber, messageIndex);
    headers.put(Ladok3Message.Header.IsLastFeed, true);

    JSONObject jsonObject = (JSONObject) getJsonObjects().get((int) messageIndex);
    JSONObject eventHeaders = (JSONObject) jsonObject.get("headers");
    for (Object key : eventHeaders.keySet()) {
      headers.put((String) key, eventHeaders.get(key));
    }
    exchange.getIn().setHeaders(headers);
  }
}
