app.controller('DbManagerCrtl', function ($scope, $http, $timeout, $rootScope, $mdDialog, $interval, Communication, AlertService) {
    $scope.entryMode = 0; // show add/edit form
    $scope.editMode = 0; // show save/update button
    $scope.showAlert = 0;
    $scope.showError = 0;
    $scope.errorMessage = "";
    $scope.NAME = "Database Configuration";

    $scope.recordList = [];
    $scope.typeList = [];
    $scope.passwordFieldType = 'password'; // Initial password field type

    $scope.record = {
        "DB_CONFIG_ID": 0,
        "CONFIG_NAME": "",
        "DB_URL": "",
        "DB_USER_NAME": "",
        "DB_PASSWORD": "",
        "DB_TYPE": "",
        "STATE": 0
    };

    $scope.testRecord = {
        "DB_URL": "",
        "DB_USER_NAME": "",
        "DB_PASSWORD": ""
    };

    $scope.loadRecords = function () {
        var req = Communication.request("POST", _baseurl_ + "DbManager/get-records", {});

        req.then(function (resp) {
            if (resp.code === "01") {
                $scope.recordList = resp.data.DATA;
                $scope.typeList = resp.data.LIST;
            }
        }, function (err) {
            console.log("ERR", JSON.stringify(err));
        });
    };

    $scope.showSuccessAlert = function(message) {
        $scope.showAlert = 1;
        $scope.showError = 0;
        
        // Auto hide after 3 seconds
        $timeout(function () {
            $scope.hideAlert();
        }, 3000);
        
        // Also show toast if using AlertService
        if (AlertService && AlertService.showSuccess) {
            AlertService.showSuccess(message || "Operation successful!");
        }
    };

    $scope.showErrorAlert = function(message) {
        $scope.showAlert = 0;
        $scope.showError = 1;
        $scope.errorMessage = message || "An error occurred!";
        
        // Auto hide after 5 seconds
        $timeout(function () {
            $scope.hideAlert();
        }, 5000);
        
        // Also show toast if using AlertService
        if (AlertService && AlertService.showError) {
            AlertService.showError(message || "Operation failed!");
        }
    };

    $scope.hideAlert = function() {
        $scope.showAlert = 0;
        $scope.showError = 0;
        $scope.errorMessage = "";
    };

    $scope.showEditMode = function (_obj) {
        $scope.record = angular.copy(_obj);
        $scope.entryMode = 1;
        $scope.editMode = 1;
        $scope.hideAlert();
    };

    $scope.showAddMode = function () {
        $scope.clearForm();
        $scope.entryMode = 1;
        $scope.editMode = 0;
        $scope.hideAlert();
    };

    $scope.closeMode = function () {
        $scope.clearForm();
        $scope.entryMode = 0;
        $scope.editMode = 0;
        $scope.hideAlert();
        $scope.loadRecords();
    };

    $scope.submitObject = function (_type) {
        // Set processing state to true to show the spinner and disable the button
        $scope.isProcessing = true;
        var req = Communication.request("POST", _baseurl_ + "DbManager/manage-object/" + _type, $scope.record);

        req.then(function (resp) {
            $scope.isProcessing = false;
            if (resp.code === "01") {
                $scope.showSuccessAlert(resp.message || "Operation completed successfully!");
                $scope.closeMode();
            } else {
                $scope.showErrorAlert(resp.message || "Operation failed!");
            }
        }, function (err) {
            $scope.isProcessing = false;
            console.log("Error: ", JSON.stringify(err));
            $scope.showErrorAlert(err.message || "Server error occurred!");
        });
    };

    $scope.switchStateObject = function (_obj, state) {
        $scope.record = _obj;
        $scope.record.STATE = state;
        var req = Communication.request("POST", _baseurl_ + "DbManager/manage-object/UPDATE", $scope.record);

        req.then(function (resp) {
            if (resp.code === "01") {
                $scope.showSuccessAlert("Status updated successfully!");
                $scope.loadRecords();
            } else {
                $scope.showErrorAlert(resp.message || "Failed to update status!");
            }
        }, function (err) {
            console.log("Error: ", JSON.stringify(err));
            $scope.showErrorAlert("Error updating status!");
        });
    };

    $scope.clearForm = function () {
        $scope.record = {
            "DB_CONFIG_ID": 0,
            "CONFIG_NAME": "",
            "DB_URL": "",
            "DB_USER_NAME": "",
            "DB_PASSWORD": "",
            "DB_TYPE": "",
            "STATE": 0
        };

        $scope.testRecord = {
            "DB_URL": "",
            "DB_USER_NAME": "",
            "DB_PASSWORD": ""
        };
    };

    // Function to open the test connection modal
    $scope.openTestConnectionModal = function () {
        $scope.messageBoxVisible = false; // Hide the message box initially
        $scope.passwordFieldType = 'password';
        // Copy current record values to testRecord
        $scope.testRecord = angular.copy($scope.record);
        $('#testConnectionModal').modal('show');
    };

    $scope.testConnectionInModal = function () {
        $scope.isTesting = true;
        $scope.messageBoxVisible = false; // Hide the message box initially

        var req = Communication.request("POST", _baseurl_ + "DbManager/test-connection", $scope.testRecord);

        req.then(function (resp) {
            $scope.isTesting = false;

            // Show the success or error message
            $scope.messageBoxVisible = true;
            if (resp.code === "01") {
                $scope.testSuccess = true;
                $scope.testMessage = "Connection successful !!!";
                
                // Also show success alert
                $scope.showSuccessAlert("Connection test successful!");
            } else {
                $scope.testSuccess = false;
                $scope.testMessage = "Connection failed: " + resp.message;
                
                // Show error alert
                $scope.showErrorAlert("Connection test failed!");
            }
        }, function (err) {
            console.log("err: ", JSON.stringify(err));
            $scope.isTesting = false;

            // Show the error message
            $scope.messageBoxVisible = true;
            $scope.testSuccess = false;
            $scope.testMessage = "An error occurred while testing the connection.";
            
            // Show error alert
            $scope.showErrorAlert("Error testing connection!");
        });
    };

    // Toggle password field type between 'text' and 'password'
    $scope.togglePasswordVisibility = function () {
        if ($scope.passwordFieldType === 'password') {
            $scope.passwordFieldType = 'text';
            $scope.testRecord.DB_PASSWORD = "";
        } else {
            $scope.passwordFieldType = 'password';
            $scope.testRecord.DB_PASSWORD = $scope.record.DB_PASSWORD;
        }
    };

    // Function to set the password to the main form
    $scope.setPasswordToMainForm = function () {
        if ($scope.testRecord.DB_PASSWORD !== "") {
            // Assuming 'mainFormRecord' is the object representing the main form's model
            $scope.record.DB_PASSWORD = $scope.testRecord.DB_PASSWORD;
            $scope.testRecord = {
                "DB_URL": "",
                "DB_USER_NAME": "",
                "DB_PASSWORD": ""
            };
            $('#testConnectionModal').modal('hide');
            $scope.showSuccessAlert("Password applied to main form!");
        } else {
            AlertService.showError("No password found to apply !!!")
        }
    };
});