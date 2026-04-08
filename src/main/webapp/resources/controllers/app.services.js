
app.factory('CommunicationService', function($http) {
    return {
        getRequest: function(type, url, reqData) {
            
            var requestStr = JSON.stringify(reqData);
            var returnObj = {
              method: type.toUpperCase(),
              url: url,
              headers: {'Content-Type': 'application/json'},
              dataType: 'json',
              data: requestStr
            };
          return returnObj;
        }

    }
});

app.factory('Communication', function ($http, $q, $timeout, CommunicationService) {
    return {
        request: function (type, url, reqData) {            
            var deferred = $q.defer();
            var req;            
            $timeout(function () {
                var req = CommunicationService.getRequest(type, url, reqData);
                console.log(JSON.stringify(req));

                $http(req).then(function (msg) {
                    deferred.resolve(msg.data);

                }, function (err) {
                    deferred.reject(err);
                });
            });            
            return deferred.promise;
        },        
        globalReqData:function(){
            var topping = 0;
            var from_date = "1970-01-01";
            var to_date = "1970-01-31";
            
            if (arguments.length === 0 || arguments.length === 1) { 
                topping = arguments[0];
                var _DATE_ = new Date();
                _DATE_ = addDays(_DATE_, 1);                
                var _year = _DATE_.getFullYear();
                var _month = _DATE_.getMonth() + 1;
                var _dayOfMonth = _DATE_.getDate();                
                _month = (_month<10) ? "0" +_month : _month;                
                to_date = _year + "-" + _month + "-" + _dayOfMonth;                
            } else if( arguments.length > 1 ){ // requires two parameter first one is topping, and second one is selected date
                topping = arguments[0];
                var selected_date = arguments[1];                              
                var dateArray = selected_date.split(" ");
                var _d = new Date(dateArray[1], parseInt(getMonthIndex( dateArray[0] ), 10), 0);
                from_date = dateArray[1] + "-" + getMonthIndex( dateArray[0] ) + "-01";
                to_date = dateArray[1] + "-" + getMonthIndex( dateArray[0] )  + "-" + _d.getDate();               
            }
            var req = {"FROM_DATE":from_date, "TO_DATE":to_date, "TOPPING": topping, "month_selected":"2017-11-01"}
            return req;
        }
    }
});

app.factory('AlertService', function() {
    return {
        showSuccess: function(message) {
            Swal.fire({
                icon: 'success',
                title: 'Success',
                text: message,
                confirmButtonText: 'OK'
            });
        },
        showError: function(message) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: message,
                confirmButtonText: 'OK'
            });
        }
    };
});
