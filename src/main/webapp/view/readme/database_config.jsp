<!-- Database Config Section -->
<div class="col-md-12">
    <div class="instruction-box">
        <div class="card-header" data-toggle="collapse" data-target="#databaseConfigCollapse"
             ng-click="toggleChevron('databaseChevron')" style="cursor: pointer;">
            <h5>
                <i class="fa fa-database"></i> Database Configuration
                <i id="databaseChevron" class="fa fa-chevron-down chevron"></i>
                </span>
            </h5>
        </div>
        <div id="databaseConfigCollapse" class="collapse">

            <div class="card-body">
                <p>Database Configuration defines the target databases where this application will transfer,
                    update, insert, and synchronize data. Each configured database represents
                    a destination for data operations from this application:
                </p>
                <ul>
                    <li><strong>Config name</strong> Unique identifier for database</li>
                    <li><strong>URL</strong> Connection URL of the database</li>
                    <li><strong>Schema</strong> Schema name(PostgreSQL), Database name (MYSQL)</li>
                    <li><strong>Status</strong> Defines connection URL is valid</li>
                </ul>

                <div class="mt-3">
                    <div class="table-responsive mt-2">
                        <table class="table table-sm table-hover">
                            <thead>
                            <tr>
                                <th>Name</th>
                                <th width="40%">URL</th>
                                <th>Schema</th>
                                <th>Type</th>
                                <th>Status</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr ng-repeat="db in databaseConfig.databases">
                                <td>{{db.name}}</td>
                                <td width="40%">{{db.url}}</td>
                                <td>{{db.schema}}</td>
                                <td>{{db.type}}</td>
                                <td>{{db.status}}</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>