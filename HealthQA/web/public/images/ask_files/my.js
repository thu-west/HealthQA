// function showAnswer( aid, qid ) {
// 	alert(result.alist[aid]);
// 	alert(result.qlist[qid]);
// }


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
		//$("#DisAnswer").dialog("open");
	}

	$scope.hideAnswer = function() {
		$("#DisAnswer").hide("blind");
	}
})