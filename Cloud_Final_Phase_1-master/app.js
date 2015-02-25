
/**
 * Module dependencies.
 */
var express = require('express');
var http = require('http');
var path = require('path');
var controller = require('./controller');

// instantiate our application
var app = express();

// all environments
app.set('port', process.env.PORT || 80);
app.set('view engine', 'jade');

// Middleware setup
app.use(express.json());
app.use(express.urlencoded());
app.use(express.methodOverride());
app.use(app.router);
app.use(express.static(path.join(__dirname, 'public')));

// RESTful Routing
app.get('/q1', controller.heartbeat); /* rest url for heartbeat test */
app.get('/q2', function(req, res) {   /* query mysql for user_id, creation_time */

    controller.get_mysql(req, function(results) {
        var response = 'NoLife,900670946900,846052939032,028020059431\n';
        for (var i in results) {

            var result    = results[i];
            var tweet_id  = result.tweet_id;
            var score     = result.score;
            var tweettext = result.censored_message;

            response = response + tweet_id + ':' + score + ':' + tweettext + '\n';
        }
        // console.log(response);
        res.send(response);
    });
});

http.createServer(app).listen(app.get('port'), function(){
  console.log('Express server listening on port ' + app.get('port'));
});
