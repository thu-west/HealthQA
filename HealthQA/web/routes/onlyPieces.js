var express = require('express');
var router = express.Router();



/* GET home page. */
router.get('/', function(req, res, next){

  res.render('onlyPieces', { title: '健康问答系统' });
});

// router.get('ask_p',function(req,res,next) {
// 	var userUrl = req.params;
// 	// var userIp = req.connection.remoteAddress;
// 	var userIp = req.ip;
// 	var userContent = req.query.question;
// 	console.log(uri);

// });

// var newUser = new UserLog({
// 	user_url:userUrl,
// 	user_ip:userIp,
// 	user_content:userContent

// });

// newUser.save(function(err,result){
// 	if(err){
// 		res.locals.error = err;
// 		return;
// 	}
// })


module.exports = router;