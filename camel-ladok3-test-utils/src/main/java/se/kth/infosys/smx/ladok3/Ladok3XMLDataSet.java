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
import java.util.LinkedList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * A ListDataSet that reads JSON encoded Ladok3 payloads from a file.
 * 
 * <p>The file is assumed to contain a JSON array of objects representing Ladok3
 * ATOM events encoded as XML. See README.md for details.</p>
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
public class Ladok3XMLDataSet extends Ladok3JsonDataSet {
  /**
   * Default constructor.
   */
  public Ladok3XMLDataSet() {}

  /**
   * Constructor using a file name string.
   * 
   * @param sourceFileName The file name.
   * @throws Exception on file access and parse problems.
   */
  public Ladok3XMLDataSet(String sourceFileName) throws Exception {
    super(sourceFileName);
  }

  /**
   * Constructor using a File object.
   *
   * @param sourceFile the File.
   * @throws Exception on file access and parse problems.
   */
  public Ladok3XMLDataSet(File sourceFile) throws Exception {
    super(sourceFile);
  }

  /**
   * Read the source file and intializes the internal list of message bodies.
   * Can be overridden by subclasses to tweak behaviour.
   * 
   * @throws Exception on file access and parse problems.
   */
  protected void readSourceFile() throws Exception {
    List<Object> bodies = new LinkedList<Object>();
    jsonObjects = (JSONArray) parser.parse(new FileReader(sourceFile));

    for (int i = 0; i < jsonObjects.size(); i++) {
      JSONObject jsonObject = (JSONObject) jsonObjects.get(i);
      String body = (String) jsonObject.get("body");
      bodies.add(body.getBytes());
    }
    setDefaultBodies(bodies);
  }
}
