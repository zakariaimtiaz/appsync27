app.controller('AppManagerCrtl', function($scope, $http, $timeout, Communication) {
    $scope.NAME = "App Configuration";
    $scope.typeList = [];
    
    // Message display variables
    $scope.showMessage = false;
    $scope.isSuccess = false;
    $scope.messageText = "";
    
    // Processing state
    $scope.isProcessing = false;
    
    // Record model
    $scope.record = {
        "ID": 0,
        "NAME": "",
        "CODE": "",
        "TYPE": "",
        "STATE": "1"
    };

    // Function to show messages
    $scope.showMessageAlert = function(isSuccess, message, duration) {
        $scope.showMessage = true;
        $scope.isSuccess = isSuccess;
        $scope.messageText = message || (isSuccess ? "Operation completed successfully!" : "An error occurred!");
        
        // Auto hide after specified duration (default 3 seconds)
        $timeout(function() {
            $scope.hideMessage();
        }, duration || 3000);
    };

    // Function to hide messages
    $scope.hideMessage = function() {
        $scope.showMessage = false;
        $scope.messageText = "";
    };

    // Load application configuration
    $scope.loadRecords = function(){
        var req = Communication.request("POST", _baseurl_ + "AppProperties/get-records", {});

        req.then(function(resp) {
            if (resp.code === "01") {                // Ensure STATE is string
                resp.data.STATE = String(resp.data.STATE);
                
                $scope.record = resp.data;
                console.log("STATE value:", $scope.record.STATE);
                console.log("STATE type:", typeof $scope.record.STATE);
                $scope.typeList = resp.data.TYPE_LIST || [];
            } else {
                $scope.showMessageAlert(false, "Failed to load configuration");
            }
        }, function(err) {
            console.error("Error loading records:", err);
            $scope.showMessageAlert(false, "Error loading configuration: " + (err.message || "Unknown error"));
        });
    };

    // Submit configuration
    $scope.submitObject = function(_type){
        // Validate required fields
        if (!$scope.record.TYPE || !$scope.record.NAME || !$scope.record.CODE) {
            $scope.showMessageAlert(false, "Please fill all required fields");
            return;
        }
        
        $scope.isProcessing = true;
        var req = Communication.request("POST", _baseurl_ + "AppProperties/manage-object/" + _type, $scope.record);

        req.then(function(resp) {
            $scope.isProcessing = false;
            
            if (resp.code === "01") {
                $scope.showMessageAlert(true, resp.message || "Configuration updated successfully!");
                
                // Update local record if response contains updated data
                if (resp.data) {
                    $scope.record = resp.data;
                }
                
                // Reload the page after a delay to reflect menu changes
                $timeout(function() {
                    window.location.reload();
                }, 1500);
            } else {
                $scope.showMessageAlert(false, resp.message || "Failed to update configuration");
            }
        }, function(err) {
            $scope.isProcessing = false;
            console.error("Error saving configuration:", err);
            $scope.showMessageAlert(false, "Error saving configuration: " + (err.message || "Server error"));
        });
    };
});