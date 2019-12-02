<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>HWSC UI</title>

    <script src="/webjars/jquery/jquery.js"></script>

    <script src="/webjars/sockjs-client/sockjs.min.js"></script>

    <script src="/webjars/stomp-websocket/stomp.min.js"></script>

    <script src="/webjars/bootstrap/js/bootstrap.min.js"></script>
    <link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <script src="/webjars/echarts/echarts.js"></script>

    <link href="css/index.css" rel="stylesheet">
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
                    <form id="form-planner-config">

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
                            <div class="col">
                                <label for="mutationAddConceptAddPossibility">mutationAddConceptAddPossibility</label>
                                <input type="text" class="form-control" id="mutationAddConceptAddPossibility"
                                       name="mutationAddConceptAddPossibility"
                                       value=${(planner_config.mutationAddConceptAddPossibility)!"0.5"} required>
                            </div>
                            <div class="col">
                                <label for="mutationAddConceptChangePossibility">mutationAddConceptChangePossibility</label>
                                <input type="text" class="form-control" id="mutationAddConceptChangePossibility"
                                       name="mutationAddConceptChangePossibility"
                                       value=${(planner_config.mutationAddConceptChangePossibility)!"0.5"} required>
                            </div>
                        </div>

                        <div class="row">
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="checkbox" id="enableAutoStop"
                                       name="enableAutoStop"
                                       value="true"
                                        <#if (planner_config.enableAutoStop)?has_content
                                        && (planner_config.enableAutoStop) == true>
                                            checked
                                        </#if>
                                >
                                <label class="form-check-label" for="enableAutoStop">enableAutoStop</label>
                            </div>

                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="checkbox" id="saveToFile"
                                       name="saveToFile"
                                       value="true"
                                        <#if (planner_config.saveToFile)?has_content
                                        && (planner_config.saveToFile) == true>
                                            checked
                                        </#if>
                                >
                                <label class="form-check-label" for="saveToFile">saveToFile</label>
                            </div>

                            <div class="col">
                                <label for="autoStopStep">autoStopStep</label>
                                <input type="text" class="form-control" id="autoStopStep" name="autoStopStep"
                                       value=${(planner_config.autoStopStep)! "10"} required>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col">
                                <label for="evaluator">evaluator</label>
                                <select id="evaluator" name="evaluator" class="form-control">
                                    <#list evaluators as evaluator>
                                        <option <#if (planner_config.evaluator)?has_content && evaluator == planner_config.evaluator>selected</#if> >
                                            ${evaluator}
                                        </option>
                                    </#list>
                                </select>
                            </div>
                            <div class="col">
                                <label for="indicator">indicator</label>
                                <select id="indicator" name="indicator" class="form-control">
                                    <#list indicators as indicator>
                                        <option <#if (planner_config.evaluator)?has_content && indicator == planner_config.indicator>selected</#if> >
                                            ${indicator}
                                        </option>
                                    </#list>
                                </select>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col">
                                <label for="innerPlanMaxStep">innerPlanMaxStep</label>
                                <input type="text" class="form-control" id="innerPlanMaxStep" name="innerPlanMaxStep"
                                       value=${(planner_config.innerPlanMaxStep)!"10"}
                                       required>
                            </div>
                            <div class="col">
                                <label for="maxStateSize">maxStateSize</label>
                                <input type="text" class="form-control" id="maxStateSize" name="maxStateSize"
                                       value=${(planner_config.maxStateSize)!"10"}
                                       required>
                            </div>
                        </div>

                    </form>
                </div>
                <div class="row">
                    <button class="btn btn-primary left-buffer top-buffer" onclick="postPlannerConfig()">Run planner</button>
                </div>
            </div>
        </div>
    </div>

    <#-- Result area -->
    <div class="row top-buffer">
        <div class="card">
            <div class="card-header font-weight-bold">
                Result
            </div>
            <div class="card-body">
                <div id="result"></div>
            </div>
        </div>
    </div>

</div>
</body>
</html>