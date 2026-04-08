app.controller('ClientManagerCrtl', function($scope, $http, $timeout, $rootScope, $mdDialog, $interval, Communication) {
    $scope.entryMode = 0; // show add/edit form
    $scope.editMode = 0; // show save/update button
    $scope.showAlert = 0;
    $scope.NAME = "Client Configuration";
    
    $scope.recordList = [];
    $scope.record = {
        "ID": 0,
        "NAME": "",
        "CODE": "",
        "STATE":0
    };

    $scope.loadRecords = function(){
        console.log("getting all svr manager records...");
        var req = Communication.request("POST", _baseurl_ + "ClientManager/get-records", {});

        req.then(function(resp) {
            console.log("response: " + JSON.stringify(resp));
            
            if (resp.code === "01") {
                $scope.recordList = resp.data;
            }
            
        }, function(err) {
            console.log("ERR", JSON.stringify(err));
        });
    };
    
    $scope.showEditMode = function(_obj){
        $scope.record = _obj;
        $scope.entryMode = 1;
        $scope.editMode = 1;
    };
    
    $scope.showAddMode = function(){
        $scope.clearForm();
        $scope.entryMode = 1;
        $scope.editMode = 0;
    };
    
    $scope.closeMode = function(){
        $scope.clearForm();
        $scope.entryMode = 0;
        $scope.editMode = 0;
    };
    
    $scope.submitObject = function(_type){
        console.log("saving record data...");
        var req = Communication.request("POST", _baseurl_ + "ClientManager/manage-object/"+_type, $scope.record);

        req.then(function(resp) {
            console.log("response: " + JSON.stringify(resp));
            if( resp.code === "01" ){
                $scope.showAlert = 1;
                $scope.entryMode = 0; // show add/edit form
                $scope.editMode = 0; // show save/update button
    
                $scope.clearForm();
                $scope.loadRecords();
                
                $timeout(function(){$scope.showAlert = 0;},3000);
            }
            

        }, function(err) {
            console.log("properties saved: ", JSON.stringify(err));
        });
    };
    
    $scope.switchStateObject = function(_obj, state){
        $scope.record = _obj;
        $scope.record.STATE = state;
        console.log("activate/inactivate record data...");
        
        var req = Communication.request("POST", _baseurl_ + "ClientManager/manage-object/UPDATE", $scope.record);

        req.then(function(resp) {
            console.log("response: " + JSON.stringify(resp));
            if( resp.code === "01" ){
                $scope.loadRecords();
            }
            

        }, function(err) {
            console.log("properties saved: ", JSON.stringify(err));
        });
    };
    
    $scope.clearForm = function(){
        $scope.record = {
            "ID": 0,
            "NAME": "",
            "CODE": "",
            "STATE":0
        };
    };

});