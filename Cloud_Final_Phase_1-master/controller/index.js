var bignum = require('bignum');
var mysql  = require('mysql');

var teamID  = 'NoLife',
    aws_id1 = '900670946900', /* Chih-Ang Wang */
    aws_id2 = '846052939032', /* Chih-Feng Lin */
    aws_id3 = '028020059431'; /* Wei-Lin  Tsai */

var hashtbl_key = {}

var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'root',
  password : '0000',
  database : 'tweet'
});
connection.connect();

function getDateTime() {
    var date = new Date();
    var hour = date.getHours(), hour = (hour < 10 ? "0" : "") + hour;
    var min  = date.getMinutes(), min = (min < 10 ? "0" : "") + min;
    var sec  = date.getSeconds(), sec = (sec < 10 ? "0" : "") + sec;
    var year = date.getFullYear();
    var month = date.getMonth() + 1, month = (month < 10 ? "0" : "") + month;
    var day  = date.getDate(), day = (day < 10 ? "0" : "") + day;

    return year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec;
}

module.exports.heartbeat = function(req, res) {
    var keyXY = req.query.key;
    var time  = getDateTime();
    var keyY  = '';

    if (keyXY in hashtbl_key) {
        keyY = hashtbl_key[keyXY];
    } else {
        keyY = bignum(keyXY).div('6876766832351765396496377534476050002970857483815262918450355869850085167053394672634315391224052153');
        hashtbl_key[keyXY] = keyY;
    }

    res.send(keyY + '\n' + teamID + ',' + aws_id1 + ',' + aws_id2 + ',' + aws_id3 + '\n' + time + '\n');
}

module.exports.get_mysql = function(req, callback) {
    var userid = req.query.userid;
    var tweet_time = req.query.tweet_time;

    connection.query('select * from tweetmessages where user_id = "' + userid + '" and creation_time = "' + tweet_time + '"', function(err, results) {
        if (err)
            throw err;
        callback(results);
    });
}