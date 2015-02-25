import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.platform.Verticle;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.math.BigInteger;

import com.mysql.jdbc.*;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;

public class Server extends Verticle {

    private static String X = "6876766832351765396496377534476050002970857483815262918450355869850085167053394672634315391224052153";
    private static HashMap<String, BigInteger>hashTable_Q1 = new HashMap<String, BigInteger>();
    private static HashMap<String, String>hashTable_Q3 = new HashMap<String, String>();
    private static String GROUP_ID ="NoLife,900670946900,846052939032,028020059431";
    private static int BACK_LOG_NUM = 65536;
    private static int SEND_BUFFER_SIZE = 1024;
    private static int RECEIVE_BUFFER_SIZE = 1024;
    private static int PORT = 80;
    private static Connection conn = null;

    public void start() {

        /* vert.x server system setup */
        HttpServer server = vertx.createHttpServer();
        server.setAcceptBacklog(BACK_LOG_NUM);
        server.setSendBufferSize(SEND_BUFFER_SIZE);
        server.setReceiveBufferSize(RECEIVE_BUFFER_SIZE);

        /* MySQL JDBC configuration setup */
        String USER = "root";
        String PASSWORD = "123456";
        String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        String DB_URL = "jdbc:mysql://localhost/tweet?useUnicode=true&characterEncoding=UTF-8";

        try
        {
            /* establish connection to the MySQL server */
            Class.forName(JDBC_DRIVER);
            conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASSWORD);
        }
        catch (ClassNotFoundException | SQLException e)
        {
            // prevent io system call, ignore Exception e
            // e.printStackTrace();
        }


        server.requestHandler(new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest req) {
                String rsp = "";
                String req_path = req.path();

                if (req_path.equals("/q1"))
                {
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
                }
                else if (req_path.equals("/q2"))
                {
                    MultiMap urlParam = req.params();
                    String user_id = urlParam.get("userid");
                    String tweet_time = urlParam.get("tweet_time");

                    if (user_id != null
                        && user_id.length() > 0
                        && tweet_time != null
                        && tweet_time.length() > 0)
                    {
                        try
                        {
                            String query_time = tweet_time.substring(0, 10)+"+"+tweet_time.substring(11);
                            String sql_str = "select tweet_id, score, censored_message from tweetmessages where user_id = '"+user_id+"' and creation_time = '"+query_time+"' group by tweet_id";
                            PreparedStatement preparedStatement = (PreparedStatement) conn.prepareStatement(sql_str);
                            ResultSet rs = preparedStatement.executeQuery(sql_str);

                            rsp = GROUP_ID;
                            while (rs.next())
                            {
                                String tweet_id = rs.getString("tweet_id");
                                String score = rs.getString("score");
                                String tweet_text = rs.getString("censored_message");
                                String text_trim = tweet_text.trim();
                                rsp += "\n"+tweet_id+":"+score+":"+text_trim;
                            }
                            rsp += "\n";
                            rs.close();
                            preparedStatement.close();
                            req.response().end(rsp);
                        }
                        catch (SQLException e)
                        {
                            // prevent io system call, ignore Exception e
                            // e.printStackTrace();
                        }
                    }
                }
                else if (req_path.equals("/q3"))
                {
                    MultiMap urlParam = req.params();
                    String user_id = urlParam.get("userid");

                    if(user_id != null && user_id.length() > 0) {

                       try
                        {
                            String ans = hashTable_Q3.get(user_id);
                            /* cache hit */
                            if(ans != null) {
                                rsp = ans;
                            }
                            /* cache miss */
                            else {

                                String sql_str = "select retweet_id from retweet_info where user_id = " + user_id;
                                PreparedStatement preparedStatement = (PreparedStatement) conn.prepareStatement(sql_str);
                                ResultSet rs = preparedStatement.executeQuery(sql_str);

                                rsp = GROUP_ID;
                                while (rs.next())
                                {
                                    String retweet_id = rs.getString("retweet_id");
                                    rsp += "\n"+retweet_id;
                                }
                                rsp += "\n";
                                rs.close();
                                preparedStatement.close();

                                /* push into hashtable */
                                hashTable_Q3.put(user_id, rsp);
                            }
                            /* write response to client */
                            req.response().end(rsp);
                        }
                        catch (SQLException e)
                        {
                            // prevent io system call, ignore Exception e
                            // e.printStackTrace();
                        }
                    }
                }
                else if (req_path.equals("/q4"))
                {
                    try {
                        MultiMap urlParam = req.params();
                        String date = urlParam.get("date");
                        String location = urlParam.get("location");
                        String minrank  = urlParam.get("m");
                        String maxrank  = urlParam.get("n");

                        if(
                            date     != null &&
                            location != null &&
                            minrank  != null &&
                            maxrank  != null
                        )
                        {
                            String sql_str = "select tag_id from retweet_body where time = '" + date
                                           + "' and location = '" + location + "' and rank >= " + minrank
                                           + " and rank <= " + maxrank + " order by rank";

                            // String sql_str = "select tag_id from retweet_body where time = '" + date
                            //                + "' and location = '" + location + "' and rank between " + minrank
                            //                + " and " + maxrank;

                            PreparedStatement preparedStatement = (PreparedStatement) conn.prepareStatement(sql_str);
                            ResultSet rs = preparedStatement.executeQuery(sql_str);

                            rsp = GROUP_ID;
                            while (rs.next())
                            {
                                String tag_id = rs.getString("tag_id");
                                rsp += "\n"+tag_id;
                            }
                            rsp += "\n";
                            rs.close();
                            preparedStatement.close();
                            /* write response to client */
                            req.response().end(rsp);
                        }
                    }
                    catch (SQLException e)
                    {
                        // prevent io system call, ignore Exception e
                        // e.printStackTrace();
                    }
                }
            }
        /* server listens on PORT */
        }).listen(PORT);
    }
}
