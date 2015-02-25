import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.platform.Verticle;
import org.vertx.java.core.MultiMap;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.math.BigInteger;
import org.vertx.java.core.buffer.Buffer;
import java.util.HashMap;
import org.vertx.java.core.http.HttpServer;

/** import hbase part */
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.HTableInterface;


public class Server extends Verticle {
    private final static String X = "68767668323517653964963775344760500029708574\
                      83815262918450355869850085167053394672634315391224052153";
    private static HashMap<String, BigInteger>hashTable_Q1 = new HashMap<String, BigInteger>();
    private static HashMap<String, String>hashTable_Q3 = new HashMap<String, String>();
    private final static String GROUP_ID ="NoLife,900670946900,846052939032,028020059431";
    private static int BACK_LOG_NUM = 65536;
    private static int SEND_BUFFER_SIZE = 1024;
    private static int RECEIVE_BUFFER_SIZE = 1024;
    private static int PORT = 80;
    private static Connection conn = null;

    /** for hase */
    private final static int NUM_CONNECTIONS = 100;
    private static Configuration conf;
    private static HTablePool hPool;
    private static HTableInterface q2Interface;
    private static HTableInterface q3Interface;
    private static HTableInterface q4Interface;
    private final static String q2TableName = "twitter-q2";
    private final static String q3TableName = "twitter-q3";
    private final static String q4TableName = "twitter-q4";
    private final byte[] F = Bytes.toBytes("user");
    private final byte[] C_ID = Bytes.toBytes("tID");
    private final byte[] C_SCORE = Bytes.toBytes("Score");
    private final byte[] C_TEXT = Bytes.toBytes("Text");
    private final byte[] C_VALUE = Bytes.toBytes("value");
   

    private final byte[] EMPTY = Bytes.toBytes("");

    public static void doQ1(HttpServerRequest req) {
        MultiMap urlParam = req.params();
        String xy = urlParam.get("key");
        if (xy != null && xy.length() > 0) {
            SimpleDateFormat date_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            String currTime = date_time.format(now);
            BigInteger answer = hashTable_Q1.get(xy);
            if (answer != null) {
                rsp = answer+"\n"+GROUP_ID+"\n"+currTime+"\n";
            } else {
                BigInteger bigXY = new BigInteger(xy);
                BigInteger bigX  = new BigInteger(X);
                BigInteger bigY  = bigXY.divide(bigX);
                hashTable_Q1.put(xy, bigY);
                rsp = bigY+"\n"+GROUP_ID+"\n"+currTime+"\n";
            }
        }
        req.response().end(rsp);
    }

    public static void doHbaseQ2(HttpServerRequest req) {
        MultiMap paraMap = req.params();
        String userId = paraMap.get("userid");
        String tweetTime = paraMap.get("tweet_time");
        String qTime = tweetTime.substring(0, 10)+" " + tweetTime.substring(11);
        String key = userId  + qTime;
        String tmpResult = null;
        byte[] prefixRowKey = Bytes.toBytes(key);

        rsp += GROUP_ID + "\n";

        // create scan 
        Scan myScan = new Scan();
        myScan.setRowPrefixFilter(prefixRowKey);
	ResultScanner myResultScanner = q2Interface.getScanner(myScan);
	while ((Result res = myResultScanner.next()) != null) {
	    tmpResult = Bytes.toString(res.getValue(T, C_ID)); 
	    rsp += tmpResult + ":";
	    tmpResult = Bytes.toString(res.getValue(T, C_SCORE)); 
	    rsp += tmpResult + ":";
	    tmpResult = Bytes.toString(res.getValue(T, C_TEXT));
	    userList = userList.replaceAll("\\n", "");
	    rsp += tmpResult + '\n';
	}
        req.response().end(rsp);
    }

    public static void doHbaseQ3(HttpServerRequest req) {
        MultiMap paraMap = req.params();
        String userId = paraMap.get("userid");
        if (userId == null || userId.length() == 0) { return; }

        // TODO: may do cache in the future        
        // get result from hBase
        Get myGet= new Get(Bytes.toBytes(userId));
	Result qResult =  q3Interface.get(myGet);
        String realResult = Bytes.toString(qResult.getValue(F, C_VALUE));
    
        rsp += GROUP_ID + "\n";
        rsp += realResult;
        
        rsp += "\n";
        req.response().end(rsp);
    }

    public static void doHbaseQ4(HttpServerRequest req) {
        MultiMap paraMap = req.params();
        String inDate = paraMap.get("date");
        String inLocation = paraMap.get("location");
        String inMin = paraMap.get("m");
        String inMax = paraMap.get("n");

        if (inDate == null || inDate.length() == 0) { return; }
        if (inLocation == null || inLocation.length() == 0) { return; }
        if (inMin == null || inMin.length() == 0) { return; }
        if (inMax == null || inMax.length() == 0) { return; }

        rsp += GROUP_ID + "\n";
    
        int intMin = -1;
        int intMax = -1;

        try {
            intMin = Integer.parseInt(inMin);
        } catch (Exception e) { }
        try {
            intMax = Integer.parseInt(inMax);
        } catch (Exception e) { }

        if ( (intMin == -1 ) || (intMax == -1 )) { return; }
        if ( intMin > intMax ) { return; }

        // TODO: may do cache in the future        
        for ( int i = intMin; i <= intMax; i++) {
            String qString = inDate + "_" + inLocation + "_" + Integer.toString(i);
            // get result from hBase
            Get myGet= new Get(Bytes.toBytes(qString));
            Result qResult =  q4Interface.get(myGet);
            String realResult = Bytes.toString(qResult.getValue(F, C_VALUE)); 
            rsp += realResult + "\n";
        }

        req.response().end(rsp);
    }

    public static void setupHbase(){
        conf = HBaseConfiguration.create();
        hPool = new HTablePool(conf, NUM_CONNECTIONS);
        q2Interface = hPool.getTable(Bytes.toBytes(q2TableName));
        q3Interface = hPool.getTable(Bytes.toBytes(q2TableName));
        q4Interface = hPool.getTable(Bytes.toBytes(q2TableName));
    }

    public void start() {
        HttpServer server = vertx.createHttpServer();
        server.setAcceptBacklog(BACK_LOG_NUM);
        server.setSendBufferSize(SEND_BUFFER_SIZE);
        server.setReceiveBufferSize(RECEIVE_BUFFER_SIZE);

        setupHbase();

        server.requestHandler(new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest req) {
                String rsp = "";
                String req_path = req.path();

                if (req_path.equals("/q1")) {
                    doQ1(req);
                } else if (req_path.equals("/q2")){
                    doHbaseQ2(req);
                } else if (req_path.equals("/q3")){
                    doHbaseQ3(req);
                } else if (req_path.equals("/q4")){
                    doHbaseQ4(req);
                }

            }
        }).listen(PORT);
    }
}
