// Connect to websocket
    const socket = new SockJS('/gs-guide-websocket');
stompClient = Stomp.over(socket);
stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/result', function (resultData) {
        const result = JSON.parse(resultData.body);
        console.log(result);
        displayResult(result);
    });
});

function displayResult(result) {
    $("#result").empty();
    displayQosLog(result.echartQosLog);
}

function displayQosLog(qosLog) {

    $("#result").append("<div id='echartQosLogChart' style='width: 100%;height:800px;'>");

    const myChart = echarts.init(document.getElementById("echartQosLogChart"));
    const symbolSize = 4;
    const chartHeight = "25%";
    const chartWidth = "38%";
    const chartPadding = "5%";

    const option = {
        tooltip: {},
        grid: [
            {x: chartPadding, y: "5%", width: chartWidth, height: chartHeight},
            {x2: chartPadding, y: "5%", width: chartWidth, height: chartHeight},
            {x: chartPadding, y2: "38%", width: chartWidth, height: chartHeight},
            {x2: chartPadding, y2: "38%", width: chartWidth, height: chartHeight},
            {x: chartPadding, y2: "5%", width: chartWidth, height: chartHeight},
            {x2: chartPadding, y2: "5%", width: chartWidth, height: chartHeight}
        ],
        xAxis: [
            {type: "value", gridIndex: 0, name: "Step", axisLabel: {rotate: 50, interval: 0}},
            {type: "value", gridIndex: 1, name: "Step", axisLabel: {rotate: 50, interval: 0}},
            {type: "value", gridIndex: 2, name: "Step", axisLabel: {rotate: 50, interval: 0}},
            {type: "value", gridIndex: 3, name: "Step", axisLabel: {rotate: 50, interval: 0}},
            {type: "value", gridIndex: 4, name: "Step", axisLabel: {rotate: 50, interval: 0}},
            {type: "value", gridIndex: 5, name: "Step", axisLabel: {rotate: 50, interval: 0}},
        ],
        yAxis: [
            {type: "value", gridIndex: 0, name: "Res"},
            {type: "value", gridIndex: 1, name: "Ava"},
            {type: "value", gridIndex: 2, name: "Suc"},
            {type: "value", gridIndex: 3, name: "Rel"},
            {type: "value", gridIndex: 4, name: "Lat"},
            {type: "value", gridIndex: 5, name: "Pri"}
        ],
        dataset: {
            dimensions: [
                "Step",
                "Res",
                "Ava",
                "Suc",
                "Rel",
                "Lat",
                "Pri"
            ],
            source: qosLog
        },
        series: [
            {
                type: "line",
                symbolSize: symbolSize,
                xAxisIndex: 0,
                yAxisIndex: 0,
                encode: {
                    x: "Step",
                    y: "Res",
                    tooltip: [0, 1, 2, 3, 4]
                }
            },
            {
                type: "line",
                symbolSize: symbolSize,
                xAxisIndex: 1,
                yAxisIndex: 1,
                encode: {
                    x: "Step",
                    y: "Ava",
                    tooltip: [0, 1, 2, 3, 4]
                }
            },
            {
                type: "line",
                symbolSize: symbolSize,
                xAxisIndex: 2,
                yAxisIndex: 2,
                encode: {
                    x: "Step",
                    y: "Suc",
                    tooltip: [0, 1, 2, 3, 4]
                }
            },
            {
                type: "line",
                symbolSize: symbolSize,
                xAxisIndex: 3,
                yAxisIndex: 3,
                encode: {
                    x: "Step",
                    y: "Rel",
                    tooltip: [0, 1, 2, 3, 4]
                }
            },
            {
                type: "line",
                symbolSize: symbolSize,
                xAxisIndex: 4,
                yAxisIndex: 4,
                encode: {
                    x: "Step",
                    y: "Lat",
                    tooltip: [0, 1, 2, 3, 4]
                }
            },
            {
                type: "line",
                symbolSize: symbolSize,
                xAxisIndex: 5,
                yAxisIndex: 5,
                encode: {
                    x: "Step",
                    y: "Pri",
                    tooltip: [0, 1, 2, 3, 4]
                }
            }
        ]
    };

    myChart.setOption(option);
}

function postPlannerConfig() {
    const formData = $("#form-planner-config").serializeArray();

    const data = JSON.stringify(formData.reduce((acc, f) => {
        // if (f.value === "on" ) {
        //     acc[f.name] = true;
        // } else if(f.value === "off") {
        //     acc[f.name] = false;
        // } else {
        //     acc[f.name] = f.value;
        // }
        acc[f.name] = f.value;
        return acc
    }, {}));

    stompClient.send("/ws/exec", {}, data)
}
