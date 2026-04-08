<!-- App Config Section -->
<div class="col-md-12">
    <div class="instruction-box">
        <div class="card-header" data-toggle="collapse" data-target="#appConfigCollapse"
             ng-click="toggleChevron('appChevron')" style="cursor: pointer;">
            <h5>
                <i class="fa fa-cogs"></i> App Configuration
                <i id="appChevron" class="fa fa-chevron-down chevron"></i>
            </h5>
        </div>
        <div id="appConfigCollapse" class="collapse">
            <div class="card-body">
                <p>This configuration defines whether the application would be <span class="highlight">SERVER</span> or <span class="highlight">CLIENT</span>:</p>
                <ul>
                    <li>If Type set as <span class="highlight">SERVER</span>:
                        <ul class="sub-list">
                            <li>Client configuration options will appear in the left menu</li>
                            <li>This instance can manage multiple client applications</li>
                            <li>Can push configurations to connected clients</li>
                            <li>Will act as the central data repository</li>
                        </ul>
                    </li>
                    <li>If Type set as <span class="highlight">CLIENT</span>:
                        <ul class="sub-list">
                            <li>Server configuration options will appear in the left menu</li>
                            <li>This instance will connect to a server application</li>
                            <li>Will receive configurations from the server</li>
                            <li>Will sync data periodically with the server</li>
                        </ul>
                    </li>
                </ul>
                <p><strong>Note:</strong> Changing this setting will modify the available menu options immediately.</p>
            </div>
        </div>
    </div>
</div>