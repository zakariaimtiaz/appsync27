app.controller('SyncTableCrtl', function ($scope, $http, $timeout, $rootScope, $mdDialog, $interval, Communication) {
    $scope.entryMode = 0; // show add/edit form
    $scope.editMode = 0; // show save/update button
    $scope.showAlert = 0;
    $scope.NAME = "Table Configuration";
    $scope.select_upsert = 0;

    $scope.recordList = [];
    $scope.serverList = [];
    $scope.dbConfigList = [];
    $scope.tblNameList = []; // Initialize table name list

    $scope.tableNamesAvailable = false;

    $scope.record = {
        "TBL_ID": 0,
        "TBL_CODE": "",
        "TBL_NAME": "",
        "TBL_TYPE": "",
        "SERVER_ID": 0,
        "DB_CONFIG_ID": 0,
        "TBL_PRIMARY_COLUMN": "",
        "TBL_S_TRACKING_COLUMN": "",
        "TBL_S_TRACKING_SQL": "",
        "TBL_C_TRACKING_COLUMN": "",
        "SELECT_SQL": "",
        "UPSERT_SQL": "",
        "SYNC_PRIORITY": 0,
        "CHUNK_SIZE": 0,
        "STATE": 0
    };

    $scope.loadRecords = function () {
        var req = Communication.request("POST", _baseurl_ + "SyncTable/get-records", {});

        req.then(function (resp) {
            if (resp.code === "01") {
                $scope.recordList = resp.data.DATA;
                $scope.serverList = resp.data.SERVER_LIST;
                $scope.dbConfigList = resp.data.CONFIG_LIST;
            }

        }, function (err) {
            console.log("ERR", JSON.stringify(err));
        });
    };

    $scope.showEditMode = function (_obj) {
        $scope.record = _obj;
        $scope.entryMode = 1;
        $scope.editMode = 1;

        $scope.onModeChange(_obj.TBL_TYPE);
        $scope.fetchTableNames(_obj.DB_CONFIG_ID);
    };

    $scope.showAddMode = function () {
        $scope.clearForm();
        $scope.entryMode = 1;
        $scope.editMode = 0;
    };

    $scope.closeMode = function () {
        $scope.clearForm();
        $scope.entryMode = 0;
        $scope.editMode = 0;
    };

    $scope.submitObject = function (_type) {
        var req = Communication.request("POST", _baseurl_ + "SyncTable/manage-object/" + _type, $scope.record);

        req.then(function (resp) {
            if (resp.code === "01") {
                $scope.showAlert = 1;
                $scope.entryMode = 0; // show add/edit form
                $scope.editMode = 0; // show save/update button

                $scope.clearForm();
                $scope.loadRecords();

                $timeout(function () {
                    $scope.showAlert = 0;
                }, 3000);
            }


        }, function (err) {
            console.log("properties saved: ", JSON.stringify(err));
        });
    };

    $scope.switchStateObject = function (_obj, state) {
        $scope.record = _obj;
        $scope.record.STATE = state;

        var req = Communication.request("POST", _baseurl_ + "SyncTable/manage-object/UPDATE", $scope.record);

        req.then(function (resp) {
            if (resp.code === "01") {
                $scope.loadRecords();
            }
        }, function (err) {
            console.log("properties saved: ", JSON.stringify(err));
        });
    };

    $scope.clearForm = function () {
        $scope.record = {
            "TBL_ID": 0,
            "TBL_CODE": "",
            "TBL_NAME": "",
            "TBL_TYPE": "",
            "SERVER_ID": 0,
            "DB_CONFIG_ID": 0,
            "TBL_PRIMARY_COLUMN": "",
            "TBL_S_TRACKING_COLUMN": "",
            "TBL_S_TRACKING_SQL": "",
            "TBL_C_TRACKING_COLUMN": "",
            "SELECT_SQL": "",
            "UPSERT_SQL": "",
            "SYNC_PRIORITY": 0,
            "CHUNK_SIZE": 0,
            "STATE": 0
        };
    };

    $scope.onModeChange = function (TBL_TYPE) {
        if ((TBL_TYPE == 'M' && IS_SERVER) || (TBL_TYPE == 'C' && IS_CLIENT)) {
            $scope.select_upsert = 1;
        }
        if ((TBL_TYPE == 'C' && IS_SERVER) || (TBL_TYPE == 'M' && IS_CLIENT)) {
            $scope.select_upsert = 2;
        }
        if (TBL_TYPE == 'MC') {
            $scope.select_upsert = 3;
        }
    };


    $scope.fetchTableNames = function (dbConfigId) {
        if (dbConfigId) {
            var req = Communication.request("POST", _baseurl_ + "SyncTable/get-table-names/" + dbConfigId, {});
            req.then(function (resp) {
                if (resp.code === "01") {
                    $scope.tblNameList = resp.data;
                    console.log('tblNameList ' + JSON.stringify($scope.tblNameList));
                    $scope.tableNamesAvailable = $scope.tblNameList.length > 0;
                } else {
                    $scope.tableNamesAvailable = false;
                }
            }, function (err) {
                $scope.tableNamesAvailable = false;
                console.log("err: ", JSON.stringify(err));
            });
        }
    };


});