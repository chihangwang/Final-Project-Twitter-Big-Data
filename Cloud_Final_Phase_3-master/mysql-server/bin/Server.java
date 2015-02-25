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
                            
                            PreparedStatement preparedStatement = (PreparedStatement) conn.prepareStatement(sql_str);
                            ResultSet rs = preparedStatement.executeQuery(sql_str);
                            
                            rsp = GROUP_ID;
                            while (rs.next())
                            {
                                String tag_id = rs.getString("tag_id").trim();
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
                else if (req_path.equals("/q5"))
                {
                    
                    try {
                        MultiMap urlParam = req.params();
                        String userid_A  = urlParam.get("m");
                        String userid_B  = urlParam.get("n");
                        int scoreA1 = 0;
                        int scoreA2 = 0;
                        int scoreA3 = 0;
                        int scoreA = 0;
                        
                        int scoreB1 = 0;
                        int scoreB2 = 0;
                        int scoreB3 = 0;
                        int scoreB = 0;
                        
                        String winner1 = "";
                        String winner2 = "";
                        String winner3 = "";
                        String winner4 = "";
                        
                        if(
                           userid_A  != null &&
                           userid_B  != null
                           )
                        {
                            rsp = GROUP_ID;
                            rsp = rsp + "\n" + userid_A + "\t" + userid_B + "\t" + "WINNER";
                            
                            String sql_str1 = "select scoreA, scoreB, scoreC, total from userid_score where user_id = " + userid_A;
                            PreparedStatement preparedStatement = (PreparedStatement) conn.prepareStatement(sql_str1);
                            ResultSet rs = preparedStatement.executeQuery(sql_str1);
                            
                            while (rs.next())
                            {
                                scoreA1 = rs.getInt("scoreA");
                                scoreA2 = rs.getInt("scoreB");
                                scoreA3 = rs.getInt("scoreC");
                                scoreA = rs.getInt("total");
                            }
                
                            rs.close();
                            preparedStatement.close();
                            
                            String sql_str2 = "select scoreA, scoreB, scoreC, total from userid_score where user_id = " + userid_B;
                            preparedStatement = (PreparedStatement) conn.prepareStatement(sql_str2);
                            rs = preparedStatement.executeQuery(sql_str2);
                            
                            while (rs.next())
                            {
                                scoreB1 = rs.getInt("scoreA");
                                scoreB2 = rs.getInt("scoreB");
                                scoreB3 = rs.getInt("scoreC");
                                scoreB = rs.getInt("total");
                            }
                            rs.close();
                            preparedStatement.close();
                            
                            if (scoreA1 > scoreB1) {
                                winner1 = userid_A;
                            } else if (scoreA1 < scoreB1) {
                                winner1 = userid_B;
                            } else {
                                winner1 = "X";
                            }
                            
                            if (scoreA2 > scoreB2) {
                                winner2 = userid_A;
                            } else if (scoreA2 < scoreB2) {
                                winner2 = userid_B;
                            } else {
                                winner2 = "X";
                            }
                            
                            if (scoreA3 > scoreB3) {
                                winner3 = userid_A;
                            } else if (scoreA3 < scoreB3) {
                                winner3 = userid_B;
                            } else {
                                winner3 = "X";
                            }
                            
                            if (scoreA > scoreB) {
                                winner4 = userid_A;
                            } else if (scoreA < scoreB) {
                                winner4 = userid_B;
                            } else {
                                winner4 = "X";
                            }
                            
                            String scoreA1_s = Integer.toString(scoreA1);
                            String scoreA2_s = Integer.toString(scoreA2);
                            String scoreA3_s = Integer.toString(scoreA3);
                            String scoreA_s = Integer.toString(scoreA);
                            
                            String scoreB1_s = Integer.toString(scoreB1);
                            String scoreB2_s = Integer.toString(scoreB2);
                            String scoreB3_s = Integer.toString(scoreB3);
                            String scoreB_s = Integer.toString(scoreB);
                            
                            rsp = rsp + "\n" + scoreA1_s +"\t" + scoreB1_s + "\t" + winner1 + "\n" + scoreA2_s + "\t" + scoreB2_s + "\t" + winner2 + "\n" + scoreA3_s + "\t" + scoreB3_s + "\t" + winner3 + "\n" + scoreA_s + "\t" + scoreB_s + "\t" + winner4 + "\n";
                            
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
                else if (req_path.equals("/q6"))
                {
                    try {
                        MultiMap urlParam = req.params();
                        String userid_A  = urlParam.get("m");
                        String userid_B  = urlParam.get("n");
                        int count_A = 0;
                        int cum_count_A = 0;
                        int cum_count_B = 0;
                        
                        if(
                           userid_A  != null &&
                           userid_B  != null
                           )
                        {
                            rsp = GROUP_ID;
                            
                            String sql_str1 = "select count, cum_count from shutter_count where user_id >= " + userid_A + " and user_id <= " + userid_B + " ORDER BY user_id ASC limit 1";
                            PreparedStatement preparedStatement = (PreparedStatement) conn.prepareStatement(sql_str1);
                            ResultSet rs = preparedStatement.executeQuery(sql_str1);
                            
                            while (rs.next())
                            {
                                count_A = rs.getInt("count");
                                cum_count_A = rs.getInt("cum_count");
                            }
                            rs.close();
                            preparedStatement.close();
                            
                            String sql_str2 = "select count, cum_count from shutter_count where user_id >= " + userid_A + " and user_id <= " + userid_B + " ORDER BY user_id DESC limit 1";
                            preparedStatement = (PreparedStatement) conn.prepareStatement(sql_str2);
                            rs = preparedStatement.executeQuery(sql_str2);
                            
                            while (rs.next())
                            {
                                cum_count_B = rs.getInt("cum_count");
                            }
                            rs.close();
                            preparedStatement.close();
                            
                            int result = cum_count_B - cum_count_A + count_A;
                            String result_s = Integer.toString(result);
                            
                            rsp = rsp + "\n" + result_s + "\n";
                            
                            
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