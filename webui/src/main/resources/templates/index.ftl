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

    <#--Planner config area-->
    <div class="row top-buffer">
        <div class="card">
            <div class="card-header font-weight-bold">
                Planner Config
            </div>
            <div class="card-body">
                <div class="form-row">
                    <form action="/planner/exec" method="post">
                        <div class="row">
                            <div class="col">
                                <label for="dataset">Dataset</label>
                                <select id="dataset" name="dataset" class="form-control">
                                    <#list dataset_list as dataset>
                                        <option <#if (planner_config.dataset)?has_content && dataset == planner_config.dataset>selected</#if> >
                                            ${dataset}
                                        </option>
                                    </#list>
                                </select>
                            </div>
                            <div class="col">
                                <label for="maxGen">maxGen</label>
                                <input type="text" class="form-control" id="maxGen" name="maxGen"
                                       value=${(planner_config.maxGen)!"200"}
                                       required>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col">
                                <label for="populationSize">populationSize</label>
                                <input type="text" class="form-control" id="populationSize" name="populationSize"
                                       value=${(planner_config.populationSize)!"50"}
                                       required>
                            </div>
                            <div class="col">
                                <label for="offspringSize">offspringSize</label>
                                <input type="text" class="form-control" id="offspringSize" name="offspringSize"
                                       value=${(planner_config.offspringSize)!"200"}
                                       required>
                            </div>
                            <div class="col">
                                <label for="survivalSize">survivalSize</label>
                                <input type="text" class="form-control" id="survivalSize" name="survivalSize"
                                       value=${(planner_config.survivalSize)!"20"}
                                       required>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col">
                                <label for="crossoverPossibility">crossoverPossibility</label>
                                <input type="text" class="form-control" id="crossoverPossibility"
                                       name="crossoverPossibility"
                                       value=${(planner_config.crossoverPossibility)!"0.7"} required>
                            </div>
                            <div class="col">
                                <label for="mutationPossibility">mutationPossibility</label>
                                <input type="text" class="form-control" id="mutationPossibility"
                                       name="mutationPossibility"
                                       value=${(planner_config.mutationPossibility)!"0.3"} required>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col">
                                <label for="mutationAddStateWeight">mutationAddStateWeight</label>
                                <input type="text" class="form-control" id="mutationAddStateWeight"
                                       name="mutationAddStateWeight"
                                       value=${(planner_config.mutationAddStateWeight)!"3"} required>
                            </div>
                            <div class="col">
                                <label for="mutationAddConceptWeight">mutationAddConceptWeight</label>
                                <input type="text" class="form-control" id="mutationAddConceptWeight"
                                       name="mutationAddConceptWeight"
                                       value=${(planner_config.mutationAddConceptWeight)!"5"} required>
                            </div>
                            <div class="col">
                                <label for="mutationDelStateWeight">mutationDelStateWeight</label>
                                <input type="text" class="form-control" id="mutationDelStateWeight"
                                       name="mutationDelStateWeight"
                                       value=${(planner_config.mutationDelStateWeight)!"1"} required>
                            </div>
                            <div class="col">
                                <label for="mutationDelConceptWeight">mutationDelConceptWeight</label>
                                <input type="text" class="form-control" id="mutationDelConceptWeight"
                                       name="mutationDelConceptWeight"
                                       value=${(planner_config.mutationDelConceptWeight)!"1"} required>
                            </div>
                        </div>

                        <div class="row">
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="checkbox" id="enableConcurrentMode"
                                       name="enableConcurrentMode"
                                        <#if (planner_config.enableConcurrentMode)?has_content
                                        && (planner_config.enableConcurrentMode) == true>
                                            checked
                                        </#if>
                                >
                                <label class="form-check-label" for="inlineCheckbox1">enableConcurrentMode</label>
                            </div>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="checkbox" id="enableAutoStop"
                                       name="enableAutoStop"
                                        <#if (planner_config.enableAutoStop)?has_content
                                        && (planner_config.enableAutoStop) == true>
                                            checked
                                        </#if>
                                >
                                <label class="form-check-label" for="inlineCheckbox2">enableAutoStop</label>
                            </div>
                            <div class="col">
                                <label for="autoStopStep">autoStopStep</label>
                                <input type="text" class="form-control" id="autoStopStep" name="autoStopStep"
                                       value=${(planner_config.autoStopStep)! "10"} required>
                            </div>
                        </div>

                        <button type="submit" class="btn btn-primary">Run planner</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

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