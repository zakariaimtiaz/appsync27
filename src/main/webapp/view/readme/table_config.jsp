<!-- Table Config Section -->
<div class="col-md-12">
    <div class="instruction-box">
        <div class="card-header" data-toggle="collapse" data-target="#tableConfigCollapse"
             ng-click="toggleChevron('tableChevron')" style="cursor: pointer;">
            <h5>
                <i class="fa fa-table"></i> Table Configuration
                <i id="tableChevron" class="fa fa-chevron-down chevron"></i>
                </span>
            </h5>
        </div>
        <div id="tableConfigCollapse" class="collapse">

            <div class="card-body">
                <p>
                    Table Configuration defines the specific tables that participate in data transfer, insert, update,
                    and synchronization operations. Each configured database represents a destination for data
                    operations performed by this application.
                    <strong>While transferring data, the table type, table name, and table code must be identical in
                        both the Server and Client applications to ensure correct mapping and synchronization.</strong>
                </p>
                <div class="alert alert-info mt-3">
                    <i class="fa fa-info-circle"></i>
                    <strong>Note:</strong> SQL queries are <span class="highlight">database-specific</span>.
                    The <code>version_no</code> field must be updated automatically after data operations,
                    preferably by using database triggers.
                </div>
                <div style="border: 1px solid lightgray; padding: 5px;">
                    <ul>
                        <li>Scenario 1: <span class="highlight">Transfer data from Server to Client</span>:
                            <ul class="sub-list">
                                <li>Set Table Type as MASTER</li>
                                <li>Server App must have a SELECT query using `S TRACKING KEY` e.g version_no</li>
                                <li>Client App provides its maximum version_no</li>
                                <li>Server sends records where version_no > :version_no</li>
                                <li>Client App upserts the received data into its target table</li>
                            </ul>
                        </li>
                    </ul>

                    <div class="mt-3">
                        <div class="table-responsive mt-2" style="margin: 30px;">
                            <table class="table table-sm table-hover">
                                <thead>
                                <tr>
                                    <th width="25%">Field</th>
                                    <th width="30%">Value</th>
                                    <th>Explanation</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td><b>APP TYPE</b></td>
                                    <td>SERVER</td>
                                    <td>Indicates this configuration belongs to the central server</td>
                                </tr>
                                <tr>
                                    <td>DATABASE</td>
                                    <td>eHospital</td>
                                    <td>Name of the connected database</td>
                                </tr>
                                <tr>
                                    <td>TABLE TYPE</td>
                                    <td>MASTER (M)</td>
                                    <td>Defines synchronization category</td>
                                </tr>
                                <tr>
                                    <td>NAME</td>
                                    <td>user_info</td>
                                    <td>Physical database table name</td>
                                </tr>
                                <tr>
                                    <td>CODE</td>
                                    <td>TM001</td>
                                    <td>Unique internal table identifier</td>
                                </tr>
                                <tr>
                                    <td>PRIMARY KEY</td>
                                    <td>attendance_id</td>
                                    <td>Primary key column used for record identification.Column used as unique identifier and referenced in ON CONFLICT (...) during upsert</td>
                                </tr>
                                <tr>
                                    <td>S TRACKING KEY</td>
                                    <td>version_no</td>
                                    <td>Server-side tracking column used to determine records eligible for synchronization.</td>
                                </tr>
                                <tr>
                                    <td width="30%">SELECT SQL</td>
                                    <td>
                                        <code>
                                            select * from :table <br>
                                            where version_no > :version_no<br>
                                            order by version_no asc<br>
                                            limit :chunk_size
                                        </code>
                                    </td>
                                    <td>Fetches all eligible records from the server MASTER table for synchronization. Parameters (:table, :version_no, :chunk_size) remain unchanged.</td>
                                </tr>
                                <tr>
                                    <td width="30%">SYNC PRIORITY</td>
                                    <td>1001</td>
                                    <td>Acts as execution order during synchronization.</td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <div class="mt-3">
                        <div class="table-responsive mt-2" style="margin: 30px;">
                            <table class="table table-sm table-hover">
                                <thead>
                                <tr>
                                    <th width="30%">Field</th>
                                    <th>Value</th>
                                    <th>Explanation</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td><b>APP TYPE</b></td>
                                    <td><b>CLIENT</b></td>
                                    <td>Application role in synchronization architecture.</td>
                                </tr>
                                <tr>
                                    <td>DATABASE</td>
                                    <td>eHospital</td>
                                    <td>Target database name.</td>
                                </tr>
                                <tr>
                                    <td>TABLE TYPE</td>
                                    <td>MASTER (M)</td>
                                    <td>Indicates this table acts as a master data source.</td>
                                </tr>
                                <tr>
                                    <td>NAME</td>
                                    <td>user_info</td>
                                    <td>Physical table name in the client database.</td>
                                </tr>
                                <tr>
                                    <td>CODE</td>
                                    <td>TM001</td>
                                    <td>Unique identifier for sync configuration.</td>
                                </tr>
                                <tr>
                                    <td>PRIMARY KEY</td>
                                    <td>attendance_id</td>
                                    <td>Primary key column used for record identification.Column used as unique identifier and referenced in ON CONFLICT (...) during upsert</td>
                                </tr>
                                <tr>
                                    <td>S TRACKING KEY</td>
                                    <td>version_no</td>
                                    <td>Client-side tracking column used to determine max reference number eligible for synchronization.</td>
                                </tr>
                                <tr>
                                    <td>UPSERT SQL</td>
                                    <td>
                                        <code>
                                            INSERT INTO user_info<br>
                                            (user_id, user_code, email, user_login_id, password, version_no) <br>
                                            VALUES (:user_id::uuid, :user_code, :email, :user_login_id, :password,
                                            :version_no) <br>
                                            ON CONFLICT (user_id) DO UPDATE <br>
                                            SET <br>
                                            user_code = :user_code,<br>
                                            email = :email,<br>
                                            user_login_id = :user_login_id,<br>
                                            version_no = :version_no
                                        </code>
                                    </td>
                                    <td>UPSERT statement used for synchronization.Use :parameterName format in SQL</td>
                                </tr>
                                <tr>
                                    <td>SYNC PRIORITY</td>
                                    <td>1001</td>
                                    <td>Determines execution order during synchronization.</td>
                                </tr>
                                </tbody>
                            </table>

                        </div>
                    </div>
                </div>
                <div style="border: 1px solid lightgray; padding: 5px;">
                    <ul>
                        <li>Scenario 2: <span class="highlight">Insert data from Client to Server</span>:
                            <ul class="sub-list">
                                <li>Set Table Type as CLIENT</li>
                                <li>Client App must have a SELECT query using `C TRACKING KEY` e.g sent_flag</li>
                                <li>Client sends unsent records to Server</li>
                                <li>Server App inserts or updates data using an UPSERT query</li>
                                <li>Client marks records as sent after successful transfer</li>
                            </ul>
                        </li>
                    </ul>

                    <div class="mt-3">
                        <div class="table-responsive mt-2" style="margin: 30px;">
                            <table class="table table-sm table-hover">
                                <thead>
                                <tr>
                                    <th width="30%">Field</th>
                                    <th>Value</th>
                                    <th>Explanation</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td><b>APP TYPE</b></td>
                                    <td><b>SERVER</b></td>
                                    <td>Application role in synchronization architecture.</td>
                                </tr>
                                <tr>
                                    <td>DATABASE</td>
                                    <td>eHospital</td>
                                    <td>Target database name.</td>
                                </tr>
                                <tr>
                                    <td>TABLE TYPE</td>
                                    <td>CLIENT (C)</td>
                                    <td>Indicates this table stores client-originated data.</td>
                                </tr>
                                <tr>
                                    <td>NAME</td>
                                    <td>user_info</td>
                                    <td>Physical table name in the server database.</td>
                                </tr>
                                <tr>
                                    <td>CODE</td>
                                    <td>TM001</td>
                                    <td>Unique identifier for sync configuration.</td>
                                </tr>
                                <tr>
                                    <td>PRIMARY KEY</td>
                                    <td>attendance_id</td>
                                    <td>Primary key column used for record identification.Column used as unique identifier and referenced in ON CONFLICT (...) during upsert</td>
                                </tr>
                                <tr>
                                    <td>S TRACKING KEY</td>
                                    <td>version_no</td>
                                    <td>Server-side tracking column used to determine records eligible for synchronization.</td>
                                </tr>
                                <tr>
                                    <td>UPSERT SQL</td>
                                    <td>
                                        <code>
                                            INSERT INTO user_info<br>
                                            (user_id, user_code, email, user_login_id, password, version_no) <br>
                                            VALUES (:user_id::uuid, :user_code, :email, :user_login_id, :password,
                                            :version_no) <br>
                                            ON CONFLICT (user_id) DO UPDATE <br>
                                            SET <br>
                                            user_code = :user_code,<br>
                                            email = :email,<br>
                                            user_login_id = :user_login_id,<br>
                                            version_no = :version_no
                                        </code>
                                    </td>
                                    <td>UPSERT statement used during synchronization from client to server.Use :parameterName format in SQL
                                    </td>
                                </tr>
                                <tr>
                                    <td>SYNC PRIORITY</td>
                                    <td>1001</td>
                                    <td>Determines execution order during synchronization.</td>
                                </tr>
                                </tbody>
                            </table>

                        </div>
                    </div>

                    <div class="mt-3">
                        <div class="table-responsive mt-2" style="margin: 30px;">
                            <table class="table table-sm table-hover">
                                <thead>
                                <tr>
                                    <th width="30%">Field</th>
                                    <th>Value</th>
                                    <th>Explanation</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td><b>APP TYPE</b></td>
                                    <td><b>CLIENT</b></td>
                                    <td>Application role in synchronization architecture.</td>
                                </tr>
                                <tr>
                                    <td>DATABASE</td>
                                    <td>eHospital</td>
                                    <td>Source database name on client side.</td>
                                </tr>
                                <tr>
                                    <td>TABLE TYPE</td>
                                    <td>CLIENT (C)</td>
                                    <td>Indicates this table originates from client environment.</td>
                                </tr>
                                <tr>
                                    <td>NAME</td>
                                    <td>user_info</td>
                                    <td>Physical table name in the client database.</td>
                                </tr>
                                <tr>
                                    <td>CODE</td>
                                    <td>TM001</td>
                                    <td>Unique sync configuration identifier.</td>
                                </tr>
                                <tr>
                                    <td>PRIMARY KEY</td>
                                    <td>attendance_id</td>
                                    <td>Primary key column used for record identification.Column used as unique identifier and referenced in ON CONFLICT (...) during upsert</td>
                                </tr>
                                <tr>
                                    <td>C TRACKING KEY</td>
                                    <td>sent_flag</td>
                                    <td>Client-side tracking column used to determine max reference number eligible for synchronization.</td>
                                </tr>
                                <tr>
                                    <td>SELECT SQL</td>
                                    <td>
                                        <code>
                                            select * from :table <br>
                                            where sent_flag = FALSE <br>
                                            order by disease_id asc <br>
                                            limit :chunk_size
                                        </code>
                                    </td>
                                    <td>Query used to fetch unsynchronized records in batches from client.</td>
                                </tr>
                                <tr>
                                    <td>SYNC PRIORITY</td>
                                    <td>1001</td>
                                    <td>Determines execution order during synchronization.</td>
                                </tr>
                                </tbody>
                            </table>

                        </div>
                    </div>
                </div>
                <div style="border: 1px solid lightgray; padding: 5px;">
                    <ul>
                        <li>Scenario 3: <span class="highlight">Bi-directional data transfer (Server - Client)</span>:
                            <ul class="sub-list">
                                <li>Set Table Type as MASTER-CLIENT</li>
                                <li>Both Server and Client Apps must have SELECT and UPSERT queries</li>
                                <li>Server App fetches data using version_no</li>
                                <li>Client App fetches data using sent_flag</li>
                                <li>Data flows safely in both directions</li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>