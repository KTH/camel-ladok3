package se.kth.infosys.smx.ladok3.internal;

public class Ladok3Message {
    public final class Header {
        public static final String FeedId = "ladok3FeedId";
        public static final String GroupID = "JMSXGroupID";
        public static final String GroupSeq = "JMSXGroupSeq";
        public static final String EventType = "ladok3EventType";
        public static final String MessageType = "ladok3MessageType";
    }
    public final class MessageType {
        public static final String Event = "Event";
        public static final String StartFeed = "StartFeed";
        public static final String EndFeed = "EndFeed";
    }
}
