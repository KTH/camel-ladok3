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

import org.apache.camel.component.dataset.ListDataSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * A ListDataSet that reads JSON encoded message bodies from a file.
 * Message bodies will be encoded as byte messages containing JSON data.
 * 
 * The file is assumed to contain a JSON array of objects
 * 
 * Example of use:
 * <pre>
 * &lt;bean id="jsonDataSet" class="se.kth.infosys.smx.ladok3.JsonDataSet"&gt;
 *   &lt;property name="sourceFile" value="classpath:json-data.json"/&gt;
 *   &lt;property name="size" value="32"/&gt;
 * &lt;/bean&gt;
 * ...
 * &lt;from uri="dataset:jsonDataSet" /&gt;
 * </pre>
 */
public class JsonDataSet extends ListDataSet {
    protected static final JSONParser parser = new JSONParser();

    private JSONArray jsonObjects = new JSONArray();
    private File sourceFile;
    
    /**
     * Default constructor.
     */
    public JsonDataSet() {}

    /**
     * Constructor using a file name string.
     * 
     * @param sourceFileName The file name.
     * @throws Exception on file access and parse problems.
     */
    public JsonDataSet(String sourceFileName) throws Exception {
        this(new File(sourceFileName));
    }

    /**
     * Constructor using a File object.
     *
     * @param sourceFile the File.
     * @throws Exception on file access and parse problems.
     */
    public JsonDataSet(File sourceFile) throws Exception {
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
        List<Object> bodies = new LinkedList<Object>();
        jsonObjects = (JSONArray) parser.parse(new FileReader(sourceFile));

        for (int i = 0; i < jsonObjects.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonObjects.get(i);
            bodies.add(jsonObject.toJSONString().getBytes());
        }
        setDefaultBodies(bodies);
    }
}
