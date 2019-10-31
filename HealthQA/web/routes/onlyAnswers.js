var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res) {
  res.render('onlyAnswer', { title: '仅显示答案' });
});

module.exports = router;