package hbase_server;

import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.platform.Verticle;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/*
 *  [Hbase class]
 *  instantiate the Hbase class to enable hbase connection and query.
 */
private class Hbase {

    private final static int MAX = 100;
    private static HTablePool pool;

    public static HTableInterface table_q2;
    public static HTableInterface table_q3;
    public static HTableInterface table_q4;
    public static HTableInterface table_q5;
    public static HTableInterface table_q6;
    public static HTableInterface table_q6_r;

    public static final byte[] b_user = Bytes.toBytes("user");
    public static final byte[] b_t_id = Bytes.toBytes("tId");
    public static final byte[] b_text = Bytes.toBytes("Text");
    public static final byte[] b_score = Bytes.toBytes("Score");
    public static final byte[] b_value = Bytes.toBytes("value");

    private ConcurrentMap<String, String> cache3;

    public Hbase(String filepath) {
        try {
            cache3 = new ConcurrentLinkedHashMap.Builder<String, String>()
                    .maximumWeightedCapacity(3000000)
                    .build();
            /* initialize the hbase configuration */
            Configuration HBASE_CONFIG = new Configuration();
            HBASE_CONFIG.addResource(new Path(filepath));
            pool = new HTablePool(HBASE_CONFIG, MAX);

        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public String get_hbase_Q2(String userid, String time) {

        table_q2 = pool.getTable(Bytes.toBytes("twitter-q2"));
        byte[] prefix = Bytes.toBytes(userid + time);
        Scan scan = new Scan(prefix);
        Filter prefixFilter = new PrefixFilter(prefix);
        scan.setFilter(prefixFilter);

        String rtn = "";
        try {
            ResultScanner resultScanner = table_q2.getScanner(scan);
            for (Result result : resultScanner) {

                String tid = Bytes.toString(result.getValue(b_user, b_t_id));
                String score = Bytes.toString(result.getValue(b_user, b_score));
                String temp = Bytes.toString(result.getValue(b_user, b_text));
                String text = temp.substring(1, temp.length()-1);

                rtn += tid + ":" + score + ":" + text + "\n";
            }
            resultScanner.close();
            table_q2.close();

        } catch (IOException e) {
            // e.printStackTrace();
        }
        rtn = rtn.replaceAll("\\\\n", "\n");
        return rtn;
    }

    public String get_hbase_Q3(String userid) {

        table_q3 = pool.getTable(Bytes.toBytes("twitter-q3"));
        if(cache3.containsKey(userid)) {
            return cache3.get(userid);
        }

        Get g = new Get(Bytes.toBytes(userid));
        Result result = null;

        try {

            result = table_q3.get(g);
            if(result == null) {
                return "";
            }
            table_q3.close();

        } catch (IOException e) {
            // e.printStackTrace();
        }
        String rtn = new String(result.value());
        cache3.put(userid, rtn);
        return rtn;
    }

    public String get_hbase_Q4(String date, String location, String m, String n) {

        table_q4 = pool.getTable(Bytes.toBytes("twitter-q4"));
        Integer int_m = Integer.parseInt(m);
        Integer int_n = Integer.parseInt(n) + 1;

        m = String.format("%08d", int_m);
        n = String.format("%08d", int_n);


        String startRow = date + "_" + location + "_" + m;
        String stopRow  = date + "_" + location + "_" + n;

        Scan scan = new Scan(Bytes.toBytes(startRow), Bytes.toBytes(stopRow));
        scan.addColumn(b_user, b_value);

        String rtn = "";
        try {
            ResultScanner resultScanner = table_q4.getScanner(scan);
            for (Result result : resultScanner) {
                byte [] value = result.getValue(b_user, b_value);
                rtn += Bytes.toString(value) + "\n";
            }
            resultScanner.close();
            table_q4.close();

        } catch (IOException e) {
            // e.printStackTrace();
        }
        return rtn;
    }

    public String get_hbase_Q5(String a, String b) {

        table_q5 = pool.getTable(Bytes.toBytes("twitter-q5"));
        Get user_a_row = new Get(Bytes.toBytes(a));
        Get user_b_row = new Get(Bytes.toBytes(b));

        Result result_a, result_b;
        String rtn = a + "\t" + b + "\tWINNER\n";

        try {
            /* retrieve result based on row_key */
            result_a = table_q5.get(user_a_row);
            result_b = table_q5.get(user_b_row);

            String score_a = Bytes.toString(result_a.getValue(b_user, b_score));
            String score_b = Bytes.toString(result_b.getValue(b_user, b_score));

            String[] soce_parts_a = score_a.split("_");
            String[] soce_parts_b = score_b.split("_");

            /* user_a's data {user:s1, user:s2, user:s3, user:total} */
            String as1 = soce_parts_a[0];
            String as2 = soce_parts_a[1];
            String as3 = soce_parts_a[2];
            String atotal = soce_parts_a[3];

            /* user_b's data {user:s1, user:s2, user:s3, user:total} */
            String bs1 = soce_parts_b[0];
            String bs2 = soce_parts_b[1];
            String bs3 = soce_parts_b[2];
            String btotal = soce_parts_b[3];

            /* construct the return value */
            rtn += (as1 + "\t" + bs1 + "\t" + (Integer.parseInt(as1) > Integer.parseInt(bs1) ? (a + "\n") :
                                              (Integer.parseInt(as1) < Integer.parseInt(bs1) ? (b) : ("X")) + "\n"));
            rtn += (as2 + "\t" + bs2 + "\t" + (Integer.parseInt(as2) > Integer.parseInt(bs2) ? (a + "\n") :
                                              (Integer.parseInt(as2) < Integer.parseInt(bs2) ? (b) : ("X")) + "\n"));
            rtn += (as3 + "\t" + bs3 + "\t" + (Integer.parseInt(as3) > Integer.parseInt(bs3) ? (a + "\n") :
                                              (Integer.parseInt(as3) < Integer.parseInt(bs3) ? (b) : ("X")) + "\n"));
            rtn += (atotal + "\t" + btotal + "\t" + (Integer.parseInt(atotal) > Integer.parseInt(btotal) ? (a + "\n") :
                                              (Integer.parseInt(atotal) < Integer.parseInt(btotal) ? (b) : ("X")) + "\n"));
            table_q5.close();

        } catch (IOException e) {
            // e.printStackTrace();
        }
        return rtn;
    }

    public String get_hbase_Q6(String a, String b) {

        long BIG = 999999999999999L;

        table_q6 = pool.getTable(Bytes.toBytes("twitter-q6"));
        table_q6_r = pool.getTable(Bytes.toBytes("twitter-q6r"));

        a = String.format("%015d", Long.parseLong(a, 10));
        b = Long.toString(BIG - Long.parseLong(b));

        boolean isEqual = a.equals(b);

        /* scan upwards, starting at user_id a */
        Scan scan_a = new Scan(Bytes.toBytes(a));
        scan_a.addColumn(b_user, b_value);
        scan_a.setBatch(1);
        scan_a.setCacheBlocks(true);

        /* scan downwards, starting at user_id b */
        Scan scan_b = null;
        if(!isEqual) {
            scan_b = new Scan(Bytes.toBytes(b));
            scan_b.addColumn(b_user, b_value);
            scan_b.setBatch(1);
            scan_b.setCacheBlocks(true);
        }
        String rtn = "";
        try {
            /* retrieve result from scanner */
            ResultScanner scanner_a = table_q6.getScanner(scan_a);
            ResultScanner scanner_b = null;
            if(!isEqual) {
                scanner_b = table_q6_r.getScanner(scan_b);
            }

            String val1 = "", val2 = "";

            val1 = Bytes.toString(scanner_a.next().getValue(b_user, b_value));
            if (!isEqual) {
                val2 = Bytes.toString(scanner_b.next().getValue(b_user, b_value));
            }

            if (isEqual) {
                String [] m = val1.split("_");
                rtn = m[0] + "\n";
            } else {
                String [] m = val1.split("_");
                String [] n = val2.split("_");
                rtn = Long.toString((Long.parseLong(n[1]) - Long.parseLong(m[1]) + Long.parseLong(m[0]) )) + "\n";
                scanner_b.close();
            }
            scanner_a.close();

            table_q6.close();
            table_q6_r.close();

        } catch (IOException e) {
            // e.printStackTrace();
        }
        return rtn;
    }
}

/*
 *  [Vert.x server class]
 *  run Vert.x server with request handler q1 ~ q6.
 */
public class hbase_server extends Verticle {

    private static String X = "6876766832351765396496377534476050002970857483815262918450355869850085167053394672634315391224052153";
    private static String GROUP_ID ="NoLife,900670946900,846052939032,028020059431";
    private static HashMap<String, BigInteger>hashTable_Q1 = new HashMap<String, BigInteger>();
    private static String hbaseConfigPath = "/home/ubuntu/git_home/Cloud_Final_Phase_3/hbase_server/conf/hbase-site.xml";
    private static int BACK_LOG_NUM = 65536;
    private static int SEND_BUFFER_SIZE = 1024;
    private static int RECEIVE_BUFFER_SIZE = 1024;
    private static int PORT = 80;

    /* hbase instance */
    private static Hbase hbase;

    public void start() {

        /* init HBase database */
        hbase = new Hbase(hbaseConfigPath);

        /* create and configure the Vert.x server */
        HttpServer server = vertx.createHttpServer();
        server.setAcceptBacklog(BACK_LOG_NUM);
        server.setSendBufferSize(SEND_BUFFER_SIZE);
        server.setReceiveBufferSize(RECEIVE_BUFFER_SIZE);

        server.requestHandler(new Handler<HttpServerRequest>() {

            public void handle(HttpServerRequest req) {

                String rsp = "";
                String req_path = req.path();

                if (req_path.equals("/q1")) {

                    MultiMap urlParam = req.params();
                    String xy = urlParam.get("key");
                    if (xy != null && xy.length() > 0)
                    {
                        SimpleDateFormat date_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date now = new Date();
                        String currTime = date_time.format(now);
                        BigInteger answer = hashTable_Q1.get(xy);
                        if (answer != null)
                        {
                            rsp = answer+"\n"+GROUP_ID+"\n"+currTime+"\n";
                        }
                        else
                        {
                            BigInteger bigXY = new BigInteger(xy);
                            BigInteger bigX  = new BigInteger(X);
                            BigInteger bigY  = bigXY.divide(bigX);
                            hashTable_Q1.put(xy, bigY);
                            rsp = bigY+"\n"+GROUP_ID+"\n"+currTime+"\n";
                        }
                    }
                    req.response().end(rsp);

                } else if (req_path.equals("/q2")) {

                    MultiMap urlParam = req.params();
                    String user_id = urlParam.get("userid");
                    String tweet_time = urlParam.get("tweet_time");

                    if (user_id    != null && user_id.length()    > 0 &&
                        tweet_time != null && tweet_time.length() > 0)
                    {
                        rsp = GROUP_ID + "\n";
                        String query_time = tweet_time.substring(0, 10) + "+" + tweet_time.substring(11);
                        rsp += hbase.get_hbase_Q2(user_id, query_time);
                        req.response().end(rsp);
                    }

                } else if (req_path.equals("/q3")) {

                    MultiMap urlParam = req.params();
                    String user_id = urlParam.get("userid");
                    if (user_id == null || user_id.length() == 0) {
                        return;
                    }
                    rsp = GROUP_ID + "\n";

                    String retweet_buddy = hbase.get_hbase_Q3(user_id);
                    retweet_buddy = retweet_buddy.replaceAll("\\\\n", "\n");
                    rsp += retweet_buddy;
                    rsp += "\n";
                    req.response().end(rsp);

                } else if (req_path.equals("/q4")) {

                    MultiMap urlParam = req.params();
                    String date = urlParam.get("date");
                    String location = urlParam.get("location");
                    String minrank  = urlParam.get("m");
                    String maxrank  = urlParam.get("n");

                    if( date     != null && location != null &&
                        minrank  != null && maxrank  != null )
                    {
                        rsp = GROUP_ID + "\n";
                        rsp += hbase.get_hbase_Q4(date, location, minrank, maxrank);
                        req.response().end(rsp);
                    }

                } else if (req_path.equals("/q5")) {

                    MultiMap urlParam = req.params();
                    String user_a = urlParam.get("m");
                    String user_b = urlParam.get("n");

                    if (user_a != null && user_b != null) {
                        rsp = GROUP_ID + "\n";
                        rsp += hbase.get_hbase_Q5(user_a, user_b);
                        req.response().end(rsp);
                    }

                } else if (req_path.equals("/q6")) {

                    MultiMap urlParam = req.params();
                    String user_a = urlParam.get("m");
                    String user_b = urlParam.get("n");

                    if (user_a != null && user_b != null) {
                        rsp = GROUP_ID + "\n";
                        rsp += hbase.get_hbase_Q6(user_a, user_b);
                        req.response().end(rsp);
                    }
                }
            }
        }).listen(PORT);
    }
}
