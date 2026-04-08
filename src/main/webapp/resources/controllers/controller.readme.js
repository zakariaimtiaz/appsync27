app.controller('ReadmeCrtl', function ($scope, $timeout) {
    $scope.NAME = "Read Me";
    $scope.appConfig = {
        type: 'MASTER',
        name: 'Main Application',
        code: 'APP001'
    };

    $scope.serverConfig = {
        servers: [
            {name: 'SERVER_1', address: 'http://localhost:8080/appSync27'}
        ]
    };

    $scope.clientConfig = {
        clients: [
            {name: 'CLIENT_1', code: 'COO1'}
        ]
    };

    $scope.databaseConfig = {
        databases: [
            {name: 'eHospital', url: 'jdbc:mysql://localhost:3307/ehospital_1_0_1', schema: 'ehospital', type: 'POSTGRES', status: 'VALID'},
            {name: 'STAKEHOLDER', url: 'jdbc:postgresql://localhost:5432/cms_stkhldr_1_1', schema: 'cms_stkhldr_1_1', type: 'MYSQL', status: 'VALID'}
        ]
    };



    $scope.allExpanded = false;

    // Toggle chevron icon rotation
    $scope.toggleChevron = function (chevronId) {
        $timeout(function () {
            var chevron = document.getElementById(chevronId);
            if (chevron) {
                chevron.classList.toggle('rotated');
            }
        }, 100);
    };

    // Toggle all sections
    $scope.toggleAllSections = function () {
        $scope.allExpanded = !$scope.allExpanded;

        // Get all collapse elements
        var collapses = document.querySelectorAll('.collapse');
        collapses.forEach(function (collapse) {
            if ($scope.allExpanded) {
                $(collapse).collapse('show');
            } else {
                $(collapse).collapse('hide');
            }
        });

        // Rotate all chevrons
        var chevrons = document.querySelectorAll('.chevron');
        chevrons.forEach(function (chevron) {
            if ($scope.allExpanded) {
                chevron.classList.add('rotated');
            } else {
                chevron.classList.remove('rotated');
            }
        });
    };
});