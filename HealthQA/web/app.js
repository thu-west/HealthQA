var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var session = require('express-session');/* 2016.4.7*/

var routes = require('./routes/index');
var users = require('./routes/users');
//liu 2016.3.24
var onlyAnswers = require('./routes/onlyAnswers');
var onlyPieces = require('./routes/onlyPieces');
var combinePieces = require('./routes/combinePieces');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

// uncomment after placing your favicon in /public
//app.use(favicon(__dirname + '/public/favicon.ico'));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));
//2016.4.18   
// app.use(cookieParser());
// app.use(session({
//     secret:'mysession',
//     resave:false,
//     saveUninitialized:true
//  }));



app.use('/', routes);
app.use('/users', users);

//liu 2016.3.24
app.use('/onlyAnswers',onlyAnswers);
app.use('/onlyPieces',onlyPieces);
app.use('/combinePieces',combinePieces);


// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
  app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
      message: err.message,
      error: err
    });
  });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
  res.status(err.status || 500);
  res.render('error', {
    message: err.message,
    error: {}
  });
});


module.exports = app;
