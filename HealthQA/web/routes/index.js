var express = require('express');
var router = express.Router();
var fs = require('fs-extra')
var exec = require('child_process').exec;
var uuid = require('node-uuid');
var os = require('os');
var URL = require('url');   

//2016.4.18
var user = require('../models/userLog.js');
       


/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: '健康问答系统' });
});

router.get('/team', function(req, res, next) {
  res.render('team', { title: 'CCCDL团队成员' });
});

router.get("/ask_a", function(req, res, next) {
	var id = uuid.v4();
	var type ="a";
	console.log("=========================");
	console.log("Accept task: "+id);
	console.log(req.query.question);
	console.log("Processing...");
	var tmp_file = '../tmp/'+id+'.asw';
	var child = exec('java -jar ask.jar "'+id+'" "'+req.query.question+'" "'+type+'"', 
		{
			encoding: 'utf8',
			timeout: 30000,
			cwd: require('path').resolve(__dirname, '..', '..')
		}, function (error, stdout, stderr) {
		    console.log(stdout);
		    console.log(stderr);
		    var tokens = stdout.toString().split(/\s/);
			console.log('tokens.length = ' + tokens.length);
		    var signal = null;
		    for(var i = 0; i < tokens.length; i ++) {
			var token = tokens[i].trim();
		    	//console.log("testing: "+token)
		    	if( /^[0-9-]{1,3}$/.test(token) ) {
		    		signal = token;
		    		// console.log("pass");
		    	} else {
		    		// console.log("not match");
		    	}
		    }
		    console.log("Recieve the signal ["+signal+"]");
		    console.log("=========================");
		    // console.log(lines);
		    if (error != null) {
				console.log('exec error: ' + error);
				res.render('error', {
					message: error.message,
					error: error
			    });
			} else if(signal == -11) {
				console.log("Result: No similar questions in dataset");
				res.render('onlyAnswer', {
					question: req.query.question,
					err: -11
				});
			} else if(signal == -12) {
				console.log("Result: No answers for the similar questions in dataset");
				res.render('onlyAnswer', {
					question: req.query.question,
					err: -12
				});
			} else if(signal == -13) {
				console.log("Result: No pieces for the similar questions in dataset");
				res.render('onlyAnswer', {
					question: req.query.question,
					err: -13
				});
			} else if(signal == -2) {
				console.log("Result: Error when write into file");
				res.render('onlyAnswer', {
					question: req.query.question,
					err: -2
				});
			} else if (signal == 0) {
				fs.readFile(tmp_file, function(err, data){
					// console.log(data);
				    if(err) throw err;
				    var result = JSON.parse(data);
				    console.log(tmp_file);
				    console.log(result);
				    fs.removeSync(tmp_file);
					res.render('onlyAnswer', {
						question: req.query.question,
						err: 0,
						result: result
					});
				});
			} else {
				res.render('onlyAnswer', {
					question: req.query.question,
					err: -1
				});
			}
		});

 });


router.get('/ask', function(req, res, next) {

	console.log("~~~~~~~~~~~~~~~~~~~~~~~~~~");
	var ipAddress = req.connection.remoteAddress ||req.headers['x-forwarded-for'] || req.headers['x-real-ip'] ||
        req.socket.remoteAddress ||
        req.connection.socket.remoteAddress;
 	// var ipAddress;
 	// var forwardedIpsStr = req.header('x-forwarded-for'); 
  //   if (forwardedIpsStr) {
  //       var forwardedIps = forwardedIpsStr.split(',');
  //       ipAddress = forwardedIps[0];
  //   }
  //   if (!ipAddress) {
  //       ipAddress = req.connection.remoteAddress;
  //   }
  //   console.log(ipAddress);

	var cond_info = req.query.url;
	var question = req.query.reqBody;
	var t = req.query.itime;

	connection = user.connect();
    // console.log("success");
    
	var sql = "INSERT INTO userLog VALUES(0,?,?,?,?)";
	connection.query(sql, [ipAddress, cond_info, question,t], function(err, result) {
		// Add ur code here
		if(err){
			console.log("insert error:"+err.message);
      		return err;
      	}
	});
});



router.get("/ask_p", function(req, res, next) {
	var id = uuid.v4();
	var type = "p";
	console.log("=========================");
	console.log("Accept task: "+id);
	console.log(req.query.question);
	console.log("Processing...");
	var tmp_file = '../tmp/'+id+'.asw';
	var child = exec('java -jar ask.jar "'+id+'" "'+req.query.question+'" "'+type+'"', 
		{
			encoding: 'utf8',
			timeout: 30000,
			cwd: require('path').resolve(__dirname, '..', '..')
		}, function (error, stdout, stderr) {
		    console.log(stdout);
		    console.log(stderr);
		     var tokens = stdout.toString().split(/\s/);
			console.log('tokens.length = ' + tokens.length);
		    var signal = null;
		    for(var i = 0; i < tokens.length; i ++) {
                        var token = tokens[i].trim();
                        //console.log("testing: "+token)
                        if( /^[0-9-]{1,3}$/.test(token) ) {
                                signal = token;
                                // console.log("pass");
                        } else {
                                // console.log("not match");
                        }
            }
		    console.log("Recieve the signal ["+signal+"]");
		    console.log("=========================");
		    // console.log(lines);
		    if (error != null) {
				console.log('exec error: ' + error);
				res.render('error', {
					message: error.message,
					error: error
			    });
			} else if(signal == -11) {
				console.log("Result: No similar questions in dataset");
				res.render('onlyPieces', {
					question: req.query.question,
					err: -11
				});
			} else if(signal == -12) {
				console.log("Result: No answers for the similar questions in dataset");
				res.render('onlyPieces', {
					question: req.query.question,
					err: -12
				});
			} else if(signal == -13) {
				console.log("Result: No pieces for the similar questions in dataset");
				res.render('onlyPieces', {
					question: req.query.question,
					err: -13
				});
			} else if(signal == -2) {
				console.log("Result: Error when write into file");
				res.render('onlyPieces', {
					question: req.query.question,
					err: -2
				});
			} else if (signal == 0) {
				fs.readFile(tmp_file, function(err, data){
					// console.log(data);
				    if(err) throw err;
				    var result = JSON.parse(data);
				    console.log(tmp_file);
				    console.log(result);
				    fs.removeSync(tmp_file);
					res.render('onlyPieces', {
						question: req.query.question,
						err: 0,
						result: result
					});
				});
			} else {
				res.render('onlyPieces', {
					question: req.query.question,
					err: -1
				});
			}
		});

 });


router.get("/ask_pz", function(req, res, next) {
	var id = uuid.v4();
	var type = "pz"
	console.log("=========================");
	console.log("Accept task: "+id);
	console.log(req.query.question);
	console.log("Processing...");
	var tmp_file = '../tmp/'+id+'.asw';
	var child = exec('java -jar ask.jar "'+id+'" "'+req.query.question+'" "'+type+'"', 
		{
			encoding: 'utf8',
			timeout: 30000,
			cwd: require('path').resolve(__dirname, '..', '..')
		}, function (error, stdout, stderr) {
		    console.log(stdout);
		    console.log(stderr);
		     var tokens = stdout.toString().split(/\s/);
			console.log('tokens.length = ' + tokens.length);
		    var signal = null;
		    for(var i = 0; i < tokens.length; i ++) {
                        var token = tokens[i].trim();
                        //console.log("testing: "+token)
                        if( /^[0-9-]{1,3}$/.test(token) ) {
                                signal = token;
                                // console.log("pass");
                        } else {
                                // console.log("not match");
                        }
                    }
		    console.log("Recieve the signal ["+signal+"]");
		    console.log("=========================");
		    // console.log(lines);
		    if (error != null) {
				console.log('exec error: ' + error);
				res.render('error', {
					message: error.message,
					error: error
			    });
			} else if(signal == -11) {
				console.log("Result: No similar questions in dataset");
				res.render('combinePieces', {
					question: req.query.question,
					err: -11
				});
			} else if(signal == -12) {
				console.log("Result: No answers for the similar questions in dataset");
				res.render('combinePieces', {
					question: req.query.question,
					err: -12
				});
			} else if(signal == -13) {
				console.log("Result: No pieces for the similar questions in dataset");
				res.render('combinePieces', {
					question: req.query.question,
					err: -13
				});
			} else if(signal == -2) {
				console.log("Result: Error when write into file");
				res.render('combinePieces', {
					question: req.query.question,
					err: -2
				});
			} else if (signal == 0) {
				fs.readFile(tmp_file, function(err, data){
					// console.log(data);
				    if(err) throw err;
				    var result = JSON.parse(data);
				    console.log(tmp_file);
				    console.log(result);
				    fs.removeSync(tmp_file);
					res.render('combinePieces', {
						question: req.query.question,
						err: 0,
						result: result
					});
				});
			} else {
				res.render('combinePieces', {
					question: req.query.question,
					err: -1
				});
			}
		});

 });





module.exports = router;
