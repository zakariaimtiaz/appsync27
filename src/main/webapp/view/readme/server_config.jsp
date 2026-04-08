<!-- Server Config Section -->
<div class="col-md-12">
    <div class="instruction-box">
        <div class="card-header" data-toggle="collapse" data-target="#serverConfigCollapse"
             ng-click="toggleChevron('serverChevron')" style="cursor: pointer;">
            <h5>
                <i class="fa fa-server"></i> Server Configuration
                <i id="serverChevron" class="fa fa-chevron-down chevron"></i>
            </h5>
        </div>
        <div id="serverConfigCollapse" class="collapse">
            <div class="card-body">
                <p>Server configuration defines which server this application communicates with:</p>
                <ul>
                    <li><strong>Server Name:</strong> Unique identifier for servers</li>
                    <li><strong>Server Address:</strong> Connection URL</li>
                </ul>

                <div class="mt-3">
                    <div class="table-responsive mt-2">
                        <table class="table table-sm table-hover">
                            <thead>
                            <tr>
                                <th>Server Name</th>
                                <th>Server Address</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr ng-repeat="db in serverConfig.servers">
                                <td>{{db.name}}</td>
                                <td>{{db.address}}</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div class="alert alert-info mt-3">
                    <i class="fa fa-info-circle"></i>
                    <strong>Note:</strong> Server configuration is only available when the application is set as <span class="highlight">CLIENT</span> type.
                </div>
            </div>
        </div>
    </div>
</div>