var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res) {
  res.render('combinePieces', { title: '显示片段组合' });
});

module.exports = router;