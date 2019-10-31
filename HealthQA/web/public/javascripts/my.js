var qaweb = angular.module('qaweb', []);

qaweb.controller("ResultCtrl", function($scope) {
	// $scope.result = result;
	$scope.display_answer = {question:{}, content:[]};

	$scope.showAnswer = function (aid, i) {
		  // alert(JSON.stringify(result.alist[aid]));
		$("#DisAnswer").hide();
		$scope.display_answer.content = result.alist[aid].content;
		$scope.display_answer.question = result.qlist[result.alist[aid].qid];
		$("#Result .qritem:eq("+i+")").append($("#DisAnswer"))
		$("#DisAnswer").show("blind");

		// alert(display_answer.content);
		//$("#DisAnswer").dialog("open");
	}
     
	$scope.hideAnswer = function() {
		$("#DisAnswer").hide("blind");
	}
})


qaweb.controller("ResultCtrl_a", function($scope) {
	// $scope.result = result;
	$scope.display_answer = {question:{}, content:[]};

	$scope.showAnswer = function (aid, i) {
		  // alert(JSON.stringify(result.alist[aid].content));
		$("#DisAnswer").hide();
		$scope.display_answer.content = result.alist[aid].content;
		$scope.display_answer.question = result.qlist[result.alist[aid].qid];
		$("#Result .qritem:eq("+i+")").append($("#DisAnswer"))
		$("#DisAnswer").show("blind");

	}
     
	$scope.hideAnswer = function() {
		$("#DisAnswer").hide("blind");
	}

	// $scope.save = function(cond_id, answer_content_cls) {
	// 	var reqBody = {
	// 		cond:$('#'+cond_id).value,
	// 		question:$('.'+answer_content_cls).value
	// 	};

	// 	var url = "log/info";
  //       var question=$(".qainput").val();
		// $.AJAX(function() {
		// 	url:'http://127.0.0.1:3000/',
		// 	type:'post',
		// 	data:{"url":url,"reqBody":reqBody}
		// 	success:function(data){
            
		// 	}
		// })
	// 	.error(function() {
	// 		// Failed
	// 	});
	// }
})


// $(document).ready(function(){
// $(".qamain").click(function(){
// 	$(".question").show();
// 	})
// })

// $(document).ready(function(){
// 	$(".qamain").keydown(function(){
// 		$(".question").hide();
// 	})
// })



$(document).ready(function(){
	$("#top_b_ul li").click(function(){
		$(this).siblings().find("a").removeClass("topactive");
		$(this).find("a").addClass("topactive");
		var thisli=$(this).index()+1;
		for(var i=1;i<4;i++){
			$("#con_one_"+i).hide();

		}
       $("#con_one_"+thisli).show();
        // alert(thisli);
       var cond=$(this).text();
       var question=$(".qainput").val();

        var date = new Date();
    	var seperator1 = "-";
    	var seperator2 = ":";
    	var month = date.getMonth() + 1;
    	var strDate = date.getDate();
    	if (month >= 1 && month <= 9) {
       		 month = "0" + month;
    	}
   		 if (strDate >= 0 && strDate <= 9) {
       		 strDate = "0" + strDate;
   		}
    	var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
            + " " + date.getHours() + seperator2 + date.getMinutes()
            + seperator2 + date.getSeconds();
        // alert(currentdate);

     
		$.ajax({
			url:'/ask',
			async: true,
		    type:'get',
			data:{"url":cond,"reqBody":question,"itime":currentdate},
			success:function(data){
              alert("1");
			}
			// error:function(){
			//   alert("2");
		 //   }
		})
       })	
 
    var lochref=window.location.href;
	var firsthref=lochref.split('/');
    for(var i=0;i<firsthref.length;i++){
      var sechref=firsthref[3].split('?');
      var resuhref=sechref[0];
      
      if(resuhref=='ask_p'){
        $("#one2 a").addClass("topactive");
        $("#con_one_2").show();
        $("#con_one_1").hide();
        }
      if(resuhref=='ask_pz'){
        $("#one3 a").addClass("topactive");
        $("#con_one_3").show();
        $("#con_one_1").hide();
        }
     }

});