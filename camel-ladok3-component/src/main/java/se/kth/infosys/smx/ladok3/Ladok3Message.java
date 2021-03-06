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

public class Ladok3Message {
  public static final class Header {
    public static final String EntryId = "ladok3AtomEntryId";
    public static final String EntryUpdated = "ladok3AtomEntryUpdated";
    public static final String Feed = "ladok3AtomFeed";
    public static final String EventType = "ladok3EventType";
    public static final String EventId = "ladok3EventId";
    public static final String KeyType = "ladok3KeyType";
    public static final String KeyValue = "ladok3KeyValue";
    public static final String Service = "ladok3Service";
    public static final String Operation = "ladok3ServiceOperation";
    public static final String Params = "ladok3Params";
    public static final String IsLastFeed = "ladok3IsLastFeed";
    public static final String MessageType = "ladok3MessageType";
    public static final String SequenceNumber = "ladok3MessageSequenceNumber";
    public static final String EntryItemIndex = "ladok3AtomEntryIndexInFeed";
    public static final String Username = "ladok3Username";
  }

  public static final class MessageType {
    public static final String Start = "ladok3FeedStart";
    public static final String Event = "ladok3Event";
    public static final String Done = "ladok3FeedDone";
  }
}
