var http = require('http');
var URL = require('url');
var testUrl = 'http://localhost:3000/ask_p?question=%E7%B3%96%E5%B0%BF%E7%97%85%E9%A5%AE%E9%A3%9F';
var p = URL.parse(testUrl); 

console.log(p.href); //取到的值是：http://localhost:3000/ask_p?question=%E7%B3%96%E5%B0%BF%E7%97%85%E9%A5%AE%E9%A3%9F';

console.log(p.protocol); //取到的值是：http: 

console.log( p.hostname);//取到的值是：locahost

console.log(p.host);//取到的值是：localhost:3000

console.log(p.port);//取到的值是：3000

console.log(p.path);//取到的值是：/ask_p?question=%E7%B3%96%E5%B0%BF%E7%97%85%E9%A5%AE%E9%A3%9F
console.log(p.pathname);///ask_p
