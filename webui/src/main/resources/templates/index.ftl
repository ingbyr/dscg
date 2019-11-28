<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>HWSC UI</title>
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link href="css/index.css" rel="stylesheet">
    <script src="js/jquery-3.4.1.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <script src="js/echarts.min.js"></script>
    <script src="js/index.js"></script>
</head>
<body>
<div class="container" id="main">
    <#include "planner.ftl">
    <div class="row top-buffer">
        <div class="card">
            <div class="card-header font-weight-bold">
                Display Qos
            </div>
            <div class="card-body">
                <button type="button" class="btn btn-primary" onclick="displayQos()">Display Qos Chart</button>
                <div id="display-qos"></div>
            </div>
        </div>
    </div>

</div>
</body>
</html>