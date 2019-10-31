var mysql  = require('mysql');  //调用mysql模块

function connectServer(){
    var connection = mysql.createConnection({     
      host     : '166.111.134.46',       
      user     : 'shengming',              
      password : 'BWLJPb9tgc',        
      port: '3306',                  
      database:'healthqa',
    }); 
    
  //   connection.connect(function(err){
  //     if(err){       
  //     console.log('[query] - :'+err);
  //     return;
  //   }
  //   console.log('[connection connect]  succeed!');
  // }); 

    return connection;
  }

function insertLog(connection,IP,MODULE,CONTENT,callback){    
  var userAddSql = 'INSERT INTO userLog VALUES(0,?,?,?)';
  connection.query(userAddSql,[IP,MODULE,CONTENT],function(err,result){
    if(err){
      console.log("insert error:"+err.message);
      return err;
    }
    callback(err,result);
  });

}

exports.connect = connectServer;
exports.insertLog = insertLog;

// connection.connect(function(err){
//     if(err){        
//           console.log('[query] - :'+err);
//         return;
//     }
//       console.log('[connection connect]  succeed!');
// });  

// function UserLog(userlog){
//   this.IP = userlog.IP;
//   this.MODULE = userlog.MODULE;
//   this.CONTENT = userlog.CONTENT;
// };

// module.exports = UserLog;

// //保存数据
// UserLog.prototype.save = function save(callback){
//   var userlog = {
//     IP:this.IP,
//     MODULE:this.MODULE,
//     CONTENT:this.CONTENT
//   };

//   var userAddSql = 'INSERT INTO userLog VALUES(0,?,?,?)';

//   connection.query(userAddSql,[userlog.IP,userlog.MODULE,userlog.CONTENT],function(err,result){
//     if(err){
//       console.log("insert error:"+err.message);
//       return;
//     }

//     connection.release();

//     console.log("invoked[save]");
//     callback(err,result); 
//   });

// };




