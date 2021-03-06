angular.module("hvm")
.controller("newProjectCtrl", function($scope, $sce, $routeParams, $rootScope, $location, getEmptyProject, getEmptyAttribute
		, retrieveServicePost, retrieveServicePostByArgs ,searchServicePost, pssProjectListApi, sbpProjectListApi, valueDetailFrameUrl, pssDetailFrameUrl
		,getProjectListUrl, getProjectListSizeUrl, setAttributeWithProjectUrl,removeAttributeUrl,getSkkupssPssProjectListUrl, setHvmProjectUrl, setServicePost, removeProjectPost) {

	
	
	//POPUP(iframe) URL
	
	$scope.trustSrc = function(type, selectedSbp) {
		if (type === "value") {
		    return $sce.trustAsResourceUrl(valueDetailFrameUrl + $scope.result[0].pssPrjId + "&selectValueName=" + $scope.selectValueName);
		} else if (type === "pss") {
		    return $sce.trustAsResourceUrl(pssDetailFrameUrl + $scope.result[0].pssPrjId);
		} else if (type === "sbp") {
			//sbp프로젝트를 선택하는 팝업에서도 sbp를 조회 할 수 있다 
			if ($scope.result[0].sbpPrjId) {
				if ($scope.selectedAttributeForSbp) {
					//!$scope.justViewSbpList 신규생성화면에서 sbp를 선택한다면 무조건 sbp 팝업이 열려야한다 
					// 신규 생성 화면에서 엑티비티를 선택하면 sbp가 선택이 되어 있다면 activity 조회, 아니라면 sbp 선택 팝업 
					if ($scope.selectedAttributeForSbp.sbpId && !$scope.justViewSbpList) {
						return $sce.trustAsResourceUrl("http://sbp.pssd.or.kr/sbp/panel8ForHvm.jsp?seq="+$scope.selectedAttributeForSbp.sbpId+"&hvm=true&memberId=sbpAdmin&sPUID=&docTitle="+$scope.selectedAttributeForSbp.sbpName+"&sProjectName="+$scope.result[0].sbpPrjName);
					} else {
						return $sce.trustAsResourceUrl("http://sbp.pssd.or.kr/sbp/listForHvm.jsp?hvm=true&memberId=sbpAdmin&sPUID="+$scope.result[0].sbpPrjId+"&sProjectName="+$scope.result[0].sbpPrjName);
					}
				}
			} else {
				return $sce.trustAsResourceUrl("http://sbp.pssd.or.kr/sbp/listForHvm.jsp?hvm=true&memberId=sbpAdmin&sPUID="+$scope.tempSbpPrjId+"&sProjectName="+$scope.tempSbpPrjName);
			}
		} else if (type === "sbpView") {
		    return $sce.trustAsResourceUrl("http://am.pssd.or.kr:9095/AMT_SYSTEM/otherActivityUpdate.runa?user_seq=1&sysType=SBP&operType=SR02&activity_name="+$scope.selectedActivityId+"&united_user_seq=tester&user_id=tester&user_name=tester&project_name=test&project_puid=test");
		}
	}
	
	
	//POST Message From iframe

	$scope.$on('selectValuePostMessage', function(event, args) {
		
		if ($scope.viewMode)
			return;
		
		//callType||valueText
		
		$scope.selectedPssValueName = args.data.split("||")[1];
		
		$scope.confirmTitle = 'Value';
		$scope.confirmText = "'"+args.data.split("||")[1]+"' 를(을) 선택하시겠습니까?" 
		
		$scope.confirmActionArray = [$scope.selectValueTreeModal, $scope.closeConfirmModal]
		
		$scope.openConfirmModal();
		
	});
	$scope.$on('selectPssPrjPostMessage', function(event, args) {
		//callType||psId||psame
		
		$scope.selectedPssPrjId = args.data.split("||")[1];
		$scope.selectedPssPrjName = args.data.split("||")[2];
		
		retrieveServicePostByArgs.retrieveServicePostByArgs(getProjectListSizeUrl, {"pssPrjId":$scope.selectedPssPrjId}).then(function(response){
			var totalSize = response.data.totalSize;
			
			$scope.confirmTitle = 'PSS';
			if (totalSize == 1) {
				$scope.confirmText = "'"+args.data.split("||")[2]+"' 를(을) 선택하시겠습니까?" 
				$scope.confirmMsg = "선택하신 프로젝트는 이미 HVM 에 등록되어 있습니다.\n'확인' 버튼을 클릭하시면 등록 되어진 프로젝트를 가져옵니다.";
			} else {
				$scope.confirmText = "'"+args.data.split("||")[2]+"' 를(을) 선택하시겠습니까?" 
			}
			$scope.confirmActionArray = [$scope.selectPssProjectListModal, $scope.closeConfirmModal]
			$scope.openConfirmModal();
		})
		
		
	});
	$scope.$on('selectSbpPostMessage', function(event, args) {

		if ($scope.viewMode)
			return;
		
		//2013 12 17_to be(컬러링)||543||51108||UA_s20601||확인받기
		//sbpName||sbpId(seqId)|| ... ||activityId(activityName)||activity
		
		//console.log('newProjectCtrl selectSbpPostMessage : ', args)
		
		//sbp프로젝트 미리보기시에는 엑티비티를 선택할 수 없게한다 
		if (!$scope.result[0].sbpPrjId)
			return;
		
		$scope.tempSbpName = args.data.split("||")[0];
		$scope.tempSbpId = args.data.split("||")[1];
		$scope.tempActivityId = args.data.split("||")[3];
		$scope.tempActivityName = args.data.split("||")[4];
		
		
		$scope.confirmTitle = 'Activity';
		$scope.confirmText = "'"+args.data.split("||")[4]+"' 를(을) 선택하시겠습니까?" 
		
		$scope.confirmActionArray = [$scope.selectSbpTreeModal, $scope.closeConfirmModal]
		
		$scope.openConfirmModal();
		
	});
	
	
	
	
	//CSS 관련 
	$scope.getPnImg = function(attribut , type) {
		
		if (type == 'P') {
			if (attribute.attributeType == 'P') {
				return 'images/btn_p_on.png';
			} else {
				return 'images/btn_p_off.png';
			}
		} else if (type == 'N') {
			if (attribute.attributeType == 'N') {
				return 'images/btn_n_on.png';
			} else {
				return 'images/btn_n_off.png';
			}
		}
		
	}

	$scope.showSaveBtn = function() {
		
		if ($scope.result[0].pssPrjName == null || $scope.result[0].pssPrjName == undefined) 
			return false;
		if ($scope.result[0].sbpPrjName == null || $scope.result[0].sbpPrjName == undefined)
			return false;
		if ($scope.result[0].attributes == null || $scope.result[0].attributes.length == 0)
			return false;
		
		return true;
	
	}
	
	
	
	//신규작성 P/N 선택 
	$scope.setAttributeType = function(attribute, type) {
		if (attribute.valueName) {
			attribute.attributeType = type;
		}
	}
	
	//SBP 를 그룹핑하여 No? 를 찍기 위한.. 신규작성 시 SBP 프로젝트를 선택하거나 attribute 를 추가하는 시점에 실행된다 
	$scope.$watch('result[0].sbpPrjName', function(newVal, oldVal){
		$scope.getSbpPrjSbpName()
    }, true);
	$scope.$watch('result[0].attributes', function(newVal, oldVal){
		$scope.getSbpPrjSbpName()
    }, true);
	
	$scope.getSbpPrjSbpName = function() {
		
		var project = $scope.result[0];
		
		var attrs = project.attributes;
		var sbpGroupList = null;
		if (attrs != null && attrs.length != 0) {
			var sbpPrjName = project.sbpPrjName;
		
			for (var j = 0; j < attrs.length; j++) {
//				var sbpName = attrs[j].sbpName == null ? '' : attrs[j].sbpName;
				var sbpName = attrs[j].sbpName;
				if (sbpName != null) {
					if (sbpGroupList == null)
						sbpGroupList = [];
					if (sbpName != null && sbpGroupList.indexOf(sbpPrjName+"_"+sbpName) == -1) {
						sbpGroupList.push(sbpPrjName+"_"+sbpName);
					}
				}
			}
		}
		$scope.sbpGroupList = sbpGroupList;
	}
	$scope.getSbpPrjSbpNameNo = function(sbpName) {
		
		var project = $scope.result[0];
		return $scope.sbpGroupList.indexOf(project.sbpPrjName+"_"+sbpName) == -1 ? null : 'No.'+($scope.sbpGroupList.indexOf(project.sbpPrjName+"_"+sbpName) + 1); 
	
	}
	
	$scope.editProject = function(psId) {
		retrieveServicePost.retrieve(getProjectListUrl, '', null, 1, 0, psId).then(function(response){
			if (response.data) {
				$scope.result[0] = response.data[0];
			} else {
				//skupss 데이터베이스에서 정보를 가져온다
				retrieveServicePost.retrieve(getSkkupssPssProjectListUrl, null, null, null, null, psId).then(function(response){
					if (response.data) {
						$scope.result[0].pssPrjId = response.data[0].id;
						$scope.result[0].pssPrjName = response.data[0].name;
						$scope.result[0].pssPrjDescription = response.data[0].description;
						$scope.result[0].pssPrjPicture = response.data[0].picture;
					} else {
					} 
				})
			}
		})
	}
	if ($routeParams.psId != 'new') {
		$scope.editProject($routeParams.psId);
	}
	
	$scope.pssProjectListUrl = pssProjectListApi;
	
	retrieveServicePost.retrieve(getEmptyProject, null, null, null, null).then(function(response){
		$scope.result = response.data;
	})
	$scope.addAttribute = function() {
		retrieveServicePost.retrieve(getEmptyAttribute, null, null, null, null).then(function(response){
			var attribute = response.data[0];
			attribute.prjId = $scope.result[0].id;
		
			//최초추가는 수정모드 
			attribute.editmode = 'true';
			
			if($scope.result[0].attributes.length != 0) {
				var prevSbpId = $scope.result[0].attributes[$scope.result[0].attributes.length - 1].sbpId;
				var prevSbpName = $scope.result[0].attributes[$scope.result[0].attributes.length - 1].sbpName;
				
				attribute.sbpId = prevSbpId;
				attribute.sbpName = prevSbpName;
			}
			
			$scope.result[0].attributes.push(attribute);
		})
	}
	
	
	
	
	
	
	
	
	$scope.saveNewAttribute = function() {
		
		setServicePost.setAttributeWithProject(setAttributeWithProjectUrl, $scope.result[0] , $scope.saveTargetAttrIndex).then(function(response){
			$scope.saveTargetAttribute.editmode = null;
		})
		
	}
	
	$scope.removeAttribute = function() {
		removeProjectPost.removeAttribute(removeAttributeUrl, $scope.removeTargetAttribute.id).then(function(response){
			$scope.result[0].attributes.splice($scope.removeTargetAttrIndex, 1);
		})
	}
	
	$scope.confirmSaveNewAttribute = function(attribute, attrIndex) {
		
		
		
		
		$('input[required]').removeClass('err-empty')
		$('.pnDiv'+attrIndex).find('div[class!="ng-hide"]').find('.emptyPn').removeClass('err-empty')
		
		if ($('#attrTr'+attrIndex).find('.ng-invalid').length == 0 && 
				$('.pnDiv'+attrIndex).find('div[class!="ng-hide"]').find('.emptyPn').length == 0) {
					
					$scope.saveTargetAttribute = null;
					$scope.saveTargetAttrIndex = null;
					
					$scope.saveTargetAttribute = attribute;
					$scope.saveTargetAttrIndex = attrIndex;
					
					
//					$scope.confirmTitle = 'New';
//					$scope.confirmText = "저장 하시겠습니까?" 
//					
//					$scope.confirmActionArray = [$scope.saveNewAttribute, $scope.closeConfirmModal]
//					
//					$scope.openConfirmModal();
					
					$scope.saveNewAttribute();
			
		} else {
			$('#attrTr'+attrIndex).find('.ng-invalid').addClass('err-empty')
			$('.pnDiv'+attrIndex).find('div[class!="ng-hide"]').find('.emptyPn').addClass('err-empty')
		}
		
		
		
		
		
	}
	$scope.confirmRemoveNewAttribute = function(attribute, attrIndex) {
		
		$scope.removeTargetAttribute = null;
		$scope.removeTargetAttrIndex = null;
		
		$scope.removeTargetAttribute = attribute;
		$scope.removeTargetAttrIndex = attrIndex;
		
		$scope.confirmTitle = 'New';
		$scope.confirmText = "삭제 하시겠습니까?" 
		
		$scope.confirmActionArray = [$scope.removeAttribute, $scope.closeConfirmModal]
		
		$scope.openConfirmModal();
		
	}
	
	$scope.cancel = function() {
		$location.path('/projectList');
	}
	$scope.confirmCancel = function() {
		
		$scope.confirmMsg = null;
		
		if ($scope.result[0].attributes) {
			for (var i =0 ; i < $scope.result[0].attributes.length; i++) {
				if ($scope.result[0].attributes[i].editmode) {
					$scope.confirmMsg = "아직 저장 되지 않은 항목이 존재합니다!!";
					break;
				}
			}
		}
		
		$scope.confirmTitle = 'New';
		$scope.confirmText = "목록으로 나가시겠습니까?" 
			
		$scope.confirmActionArray = [$scope.cancel, $scope.closeConfirmModal]
		
		$scope.openConfirmModal();
		
	}
	
	
//	$scope.saveNewProject = function() {
//		
//		$('input[required]').removeClass('err-empty')
//		$('.pnDiv').find('div[class!="ng-hide"]').find('.emptyPn').removeClass('err-empty')
//		
//		if ($('#newProjectFrm').find('.ng-invalid').length == 0 && 
//				$('.pnDiv').find('div[class!="ng-hide"]').find('.emptyPn').length == 0) {
//					setServicePost.setObj(setHvmProjectUrl, null, $scope.result[0]).then(function(response){
//						$location.path('/projectList',true);
//			})
//		} else {
//			$('#newProjectFrm').find('.ng-invalid').addClass('err-empty')
//			$('.pnDiv').find('div[class!="ng-hide"]').find('.emptyPn').addClass('err-empty')
//		}
//	}
//	$scope.cancel = function() {
//		$location.path('/projectList');
//	}
//	$scope.confirmSaveNewProject = function() {
//		
//		$scope.confirmTitle = 'New';
//		$scope.confirmText = "저장 하시겠습니까?" 
//		
//		$scope.confirmActionArray = [$scope.saveNewProject, $scope.closeConfirmModal]
//		
//		$scope.openConfirmModal();
//		
//	}
//	$scope.confirmCancel = function() {
//		
//		$scope.confirmTitle = 'New';
//		$scope.confirmText = "작성을 취소하시겠습니까?" 
//		
//		$scope.confirmActionArray = [$scope.cancel, $scope.closeConfirmModal]
//		
//		$scope.openConfirmModal();
//		
//	}

	$scope.deletePssProject = function() {
		$scope.result[0].pssPrjId = null;
		$scope.result[0].pssPrjName = null;
		
		if ($scope.result[0].attributes) {
			for (var i =0; i < $scope.result[0].attributes.length; i++) {
				$scope.result[0].attributes[i].valueId = null;
				$scope.result[0].attributes[i].valueName = null;
			}
		}
	}
	$scope.deleteSbpProject = function() {
		$scope.result[0].sbpPrjId = null;
		$scope.result[0].sbpPrjName = null;
		
		if ($scope.result[0].attributes) {
			for (var i =0; i < $scope.result[0].attributes.length; i++) {
				$scope.result[0].attributes[i].sbpId = null;
				$scope.result[0].attributes[i].sbpName = null;
				$scope.result[0].attributes[i].activityId = null;
				$scope.result[0].attributes[i].activityName = null;
			}
		}
	}
	//$scope.sbpPrjId = null;
	$scope.selectedSbpProject = function(sbpProject, index) {
		
		$scope.preViewSbpPrjPuId = null;
		$scope.preViewSbpPrjName = null;
		
		$scope.sbpPrjId = sbpProject.project_puid;
		$scope.sbpPrjName = sbpProject.project_name;
		
		$scope.confirmTitle = 'SBP';
		$scope.confirmText = "'"+sbpProject.project_name+"' 를(을) 선택하시겠습니까?" 
		
		$scope.confirmActionArray = [$scope.selectSbpProjectComplete, $scope.closeConfirmModal]
		
		$scope.openConfirmModal();
		
		//alert(sbpProject.project_puid);
	}
	
	$scope.selectSbpProjectComplete = function() {
		$scope.result[0].sbpPrjId = $scope.sbpPrjId;
		$scope.result[0].sbpPrjName = $scope.sbpPrjName;
		$scope.sbpPrjId = null;
		$scope.sbpPrjName = null;
		$scope.sbpProjectList_modal_style = {
		        'display' : 'none'
		    };
		console.log('newProjectCtrl : saveSbpProject : ', $scope.result[0]);
	}
	
	$scope.searchSbpPrj = function() {
		searchServicePost.search(sbpProjectListApi, null).then(
			function(response) {
				$scope.sbp_projects = response.data.list;
				console.log('newProjctCtrl : searchSbpPrj : ',$scope.sbp_projects);
			}
		)
	}

	//$scope.searchSbpPrj();
	
	
	//pssProject modal 

	$scope.getSelectedPssProjectElementOnModal = function() {
		return $('#pssProjectList').contents().find('#selectedPssPrjId')[0];
	}
	
	$scope.openPssProjectListModal = function() {
		$scope.pssProjectList_modal_style = {
				'display' : 'block'
		};
	}
	$scope.closePssProjectListModal = function() {
		$scope.refreshPss = true;
		$scope.pssProjectList_modal_style = {
				'display' : 'none'
		};
	}
	$scope.selectPssProjectListModal = function(arg) {
		
		var psId = null;
		if (arg) {
			psId = arg;
		} else {
			psId = $scope.getSelectedPssProjectElementOnModal().value;
		}
		$scope.getSelectedPssProjectElementOnModal().value = null;
		
		retrieveServicePost.retrieve(getProjectListUrl, '', null, 1, 0, psId).then(function(response){
			if (response.data) {
				$scope.result[0] = response.data[0];
				console.log('newProjectCtrl selectPssProjectList ',JSON.stringify($scope.result))
			} else {
				//skupss 데이터베이스에서 정보를 가져온다
				retrieveServicePost.retrieve(getSkkupssPssProjectListUrl, null, null, null, null, psId).then(function(response){
					if (response.data) {
						//$scope.result = response.data;
						console.log('newProjectCtrl selectPssProjectList SKKUPSS  : ',response)
						$scope.result[0].pssPrjId = response.data[0].id;
						$scope.result[0].pssPrjName = response.data[0].name;
						$scope.result[0].pssPrjDescription = response.data[0].description;
						$scope.result[0].pssPrjPicture = response.data[0].picture;
						
						console.log('newProjectCtrl selectPssProjectList SKKUPSS2  : ',JSON.stringify($scope.result[0]))
					} else {
						console.log('newProjectCtrl selectPssProjectList SKKUPSS Not Exist...');
					} 
				})
			}
		})
		
		
		$scope.pssProjectList_modal_style = {
				'display' : 'none'
		};
		$scope.refreshPss = true;
	}
	
	//sbpProject modal 
	$scope.openSbpProjectListModal = function() {
		$scope.searchSbpPrj();
		$scope.sbpProjectList_modal_style = {
				'display' : 'block'
		};
	}
	$scope.closeSbpProjectListModal = function() {
		$scope.sbpProjectList_modal_style = {
				'display' : 'none'
		};
	}
	$scope.selectSbpProjectListModal = function() {
		$scope.sbpProjectList_modal_style = {
				'display' : 'none'
		};
		console.log('select sbp!!!');
	}
	
	//value tree modal
	$scope.findElementByTextNPaintOnValueTreeModal = function(text, colorCode) {
		//for (var i = 0 ; i<a.length; i++){if(a[i].innerText == '전문적인'){console.log('이거야!!!!')}}
		$($('#valueTree').contents().find('span:contains("'+text+'")').filter('[class="js_action_select_value"]')[0]).parent().parent().css('background-color',colorCode);
	}
	$scope.getSelectedValueElementOnValueTreeModal = function() {
		return $('#valueTree').contents().find('#selectedValue')[0];
	}
	$scope.openValueTreeModal = function(attribute, viewMode) {
		
		if (viewMode)
			$scope.viewMode = viewMode
		
		
		$scope.selectedAttributeForValue = attribute;
		if (attribute.valueName) {
		}
		$scope.selectValueName = attribute.valueName;
		
		
		$scope.valueTree_modal_style = {
	        'display' : 'block'
	    };
		

		$($('#valueTree').contents().find('body')).focus();
		
	}
	$scope.closeValueTreeModal = function() {
		
		
		//$scope.findElementByTextNPaintOnValueTreeModal($scope.selectValueName, '#EAE8E6');
//		$scope.findElementByTextNPaintOnValueTreeModal($scope.selectedAttributeForValue.valueName, '#EAE8E6');
//		$scope.findElementByTextNPaintOnValueTreeModal($scope.getSelectedValueElementOnValueTreeModal().value, '#EAE8E6');
		$scope.selectedAttributeForValue = null;
		$scope.getSelectedValueElementOnValueTreeModal().value = null;
		$scope.valueTree_modal_style = {
	        'display' : 'none'
	    };
	}
	$scope.selectValueTreeModal = function() {
		if($scope.selectedAttributeForValue) {
			$scope.selectedAttributeForValue.valueName = $('#valueTree').contents().find('#selectedValue')[0].value;
			$scope.getSelectedValueElementOnValueTreeModal().value = null;
		}
		//$scope.findElementByTextNPaintOnValueTreeModal($scope.selectValueName, '#EAE8E6');
		//$scope.findElementByTextNPaintOnValueTreeModal($scope.selectedAttributeForValue.valueName, '#EAE8E6');
		$scope.selectedAttributeForValue = null;

		$scope.valueTree_modal_style = {
	        'display' : 'none'
	    };
	}
	
	
	//sbp tree modal
	$scope.openSbpTreeModal = function(attribute, tempSbpPrjId, tempSbpPrjName, justViewSbpList, viewMode) {
		
		if (viewMode)
			$scope.viewMode = viewMode
			
		$scope.preViewSbpPrjPuId = tempSbpPrjId;
		$scope.preViewSbpPrjName = tempSbpPrjName;
		
		$scope.justViewSbpList = justViewSbpList;
		
		if (attribute) {
			$scope.selectedAttributeForSbp = attribute;

			$scope.tempSbpName = null;
			$scope.tempSbpId = null;
			$scope.tempActivityId = null;
			$scope.tempActivityName = null;
		} else {
			$scope.tempSbpPrjId = tempSbpPrjId;
			$scope.tempSbpPrjName = tempSbpPrjName;
		}
		
		$scope.sbpTree_modal_style = {
	        'display' : 'block'
	    };
	}
	$scope.closeSbpTreeModal = function() {
		$scope.selectedAttributeForSbp = null;

		$scope.tempSbpName = null;
		$scope.tempSbpId = null;
		$scope.tempActivityId = null;
		$scope.tempActivityName = null;
		
		$scope.refresh = true;
		$scope.sbpTree_modal_style = {
	        'display' : 'none'
	    };
	}
	$scope.selectSbpTreeModal = function() {

		if($scope.selectedAttributeForSbp) {
			$scope.selectedAttributeForSbp.sbpName = $scope.tempSbpName;
			$scope.selectedAttributeForSbp.sbpId = $scope.tempSbpId;
			$scope.selectedAttributeForSbp.activityId = $scope.tempActivityId;
			$scope.selectedAttributeForSbp.activityName = $scope.tempActivityName;
		}
		
		$scope.selectedAttributeForSbp = null;
		$scope.refresh = true;
		$scope.sbpTree_modal_style = {
	        'display' : 'none'
	    };
		console.log('select sbp!!!' , $scope.result[0]);
	}

	//confirmPssPrj_modal
	
	$scope.openConfirmModal = function() {
		$scope.confirm_modal_style = {
			'display' : 'block'
		};
	}
	$scope.closeConfirmModal = function() {
		$scope.clearConfirm()
		$scope.confirm_modal_style = {
			'display' : 'none'
		};
	}
	$scope.clearConfirm = function() {

		$scope.confirmTitle = null;
		$scope.confirmText = null;
		$scope.confirmMsg = null;
		$scope.confirmAction = null;
		$scope.confirmActionArray = null;
	}
	$scope.confirmModal = function() {
		
		if ($scope.confirmAction) {
			$scope.confirmAction();
		}
		if ($scope.confirmActionArray) {
			for (var i=0 ; i < $scope.confirmActionArray.length; i++) {
				$scope.confirmActionArray[i]();
			}
		}
		$scope.clearConfirm();
		
	}
	
})
		