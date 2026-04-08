<!-- Client Config Section -->
<div class="col-md-12">
    <div class="instruction-box">
        <div class="card-header" data-toggle="collapse" data-target="#clientConfigCollapse"
             ng-click="toggleChevron('clientChevron')" style="cursor: pointer;">
            <h5>
                <i class="fa fa-desktop"></i> Client Configuration
                <i id="clientChevron" class="fa fa-chevron-down chevron"></i>
                </span>
            </h5>
        </div>
        <div id="clientConfigCollapse" class="collapse">

            <div class="card-body">
                <p>Client configuration defines its unique identifier for communicating with the master:</p>
                <ul>
                    <li><strong>Name:</strong> Name of the client</li>
                    <li><strong>Code:</strong> Unique identifier for client</li>
                </ul>

                <div class="mt-3">
                    <div class="table-responsive mt-2">
                        <table class="table table-sm table-hover">
                            <thead>
                            <tr>
                                <th>Name</th>
                                <th>Code</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr ng-repeat="db in clientConfig.clients">
                                <td>{{db.name}}</td>
                                <td>{{db.code}}</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div class="alert alert-info mt-3">
                    <i class="fa fa-info-circle"></i>
                    <strong>Note:</strong> Client configuration is only available when the application is set as <span class="highlight">SERVER</span> type.
                </div>
            </div>
        </div>
    </div>
</div>