const debugMode = true;

const analyticInfoContainerId = 'analyticInfo';
const textInfoContainerId = 'textInfo';
const chartsContainerId = 'charts';

let analyticsData = null;

/*
 * Debug out.
 */
function debugLog(msg) {
    if (debugMode)
        console.log(msg);
}

/*
 * Clear all data in the div containing the charts.
 */
function resetAnalyticsInfo() {

    const infoBlocks = [
        'textInfo',
        'general',
        'audience',

        'ars_ScResolution',
        'ars_OS',
        'ars_browser',
        'ars_Device',
        'ars_country',

        'arf_ScResolution',
        'arf_browser',
        'arf_OS',
        'arf_Device',
        'arf_country'
    ];

    for (let item of infoBlocks) {
        const div = document.getElementById(item);
        if (div != null)
            div.innerHTML = '';
    }
}

/*
 * Add to page elements checkbox and label for filter.
 *
 * @param jsonFuncInfoString - JSON object about filter from server;
 */
function fillingFilter(jsonFuncInfoString) {
    let formFilter = document.getElementById('formFilter');
    const filters = JSON.parse(jsonFuncInfoString);

    let outHtmlCode = '';

    let filling = function(filter) {
        Object.entries(filter).forEach(([key, value]) => {
            outHtmlCode +=
                `<label class="form-check-label"> ${value} </label>
                <input class="form-check-input" type="checkbox" name='${key}' checked />
                <br>`;
        });
        outHtmlCode += '<hr>';
    }

    // Filling 
    filling(filters.generalGroup);
    filling(filters.audienceGroup);
    filling(filters.audienceResGroup);
    filling(filters.audienceRefGroup);

    formFilter.innerHTML = outHtmlCode;
}

/*
 * Get analytics information from server(using filters) for draw charts and show text;
 */
function updateDataAnalytics() {
    resetAnalyticsInfo();

    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/analytics');
    xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                analyticsData = xhr.response;
                displayOfDataAnalytics(analyticsData);
                // Show general info
                let collapse = document.getElementById('collapse1');
                if (collapse != null)
                    collapse.classList.add('show');
            } else
                alert('Ошибка получения данных с сервера');
        }
    }

    const data = new FormData(document.formFilter);
    const formJSON = Object.fromEntries(data.entries());

    let filter = JSON.stringify(formJSON, null, 2);

    xhr.setRequestHeader('Content-type', 'application/json;charset=UTF-8');
    xhr.setRequestHeader(csrf_header, csrf_token);
    xhr.send(filter);
}

/*
 * Display of analytical data.
 * @param jsonData - analytical data.
 */
function displayOfDataAnalytics(jsonData) {
    let analyticDataObject = JSON.parse(jsonData);
    const generalGroup = analyticDataObject.generalGroup;

    // Text info
    let containerText = document.getElementById(textInfoContainerId);
    let outInfo = `
        <div class="textGeneralInfo">
        <b>Посетителей online:</b> ${generalGroup.online} <br>
        <b>Всего уникальных посетителей:</b> ${generalGroup.allUniqueVisitor}<br>
        <b>Всего просмотров:</b> ${generalGroup.allCountVisitedRes}<br>
        <b>Среднее количество просмотров страниц:</b> ${generalGroup.avgCountVisitedRes}<br>
        </dvi>`;
    containerText.innerHTML += outInfo;

    // Charts
    google.charts.load('current', { 'packages': ['corechart'] });
    google.charts.load('current', { 'packages': ['geochart'], 'mapsApiKey': 'AIzaSyD-9tSrke72PouQMnMX-a7eZSW0jkFMBWY' });
    google.charts.setOnLoadCallback(function() {
        // General
        drawChart('line', 'general', '0', generalGroup.statVisitOnDay, 'Посещений в день');
        drawChart('pie', 'general', '1', generalGroup.statReferer, 'Статистика переходов');
        drawChart('pie', 'general', '2', generalGroup.statResource, 'Статистика посещений страниц');

        // Audience
        const audienceGroup = analyticDataObject.audienceGroup;
        drawChart('pie', 'audience', '3', audienceGroup.statOS, 'Статистика использования операционных систем');
        drawChart('column', 'audience', '4', audienceGroup.statBrowser, 'Статистика использования браузеров');
        drawChart('column', 'audience', '5', audienceGroup.statDevice, 'Статистика использования аппаратных устройств');
        drawChart('column', 'audience', '6', audienceGroup.statScResolution, 'Статистика посетителей по разрешениям экрана');
        drawChart('geo', 'audience', '7', audienceGroup.statCountry, 'Статистика посетителей по странам');

        // Audience resources
        const titleResourceChart = 'Страница';
        const audienceResGroup = analyticDataObject.audienceResGroup;
        drawNested('pie', 'ars_ScResolution', '8', audienceResGroup.statResScResolution, titleResourceChart);
        drawNested('pie', 'ars_browser', '9', audienceResGroup.statResBrowser, titleResourceChart);
        drawNested('pie', 'ars_OS', '10', audienceResGroup.statResOS, titleResourceChart);
        drawNested('pie', 'ars_Device', '11', audienceResGroup.statResDevice, titleResourceChart);
        drawNested('pie', 'ars_country', '12', audienceResGroup.statResCountry, titleResourceChart);

        // Audience referer
        const titleRefererChart = 'Сторонний ресурс';
        const audienceRefGroup = analyticDataObject.audienceRefGroup;
        drawNested('pie', 'arf_ScResolution', '13', audienceRefGroup.statRefScResolution, titleRefererChart);
        drawNested('pie', 'arf_browser', '14', audienceRefGroup.statRefBrowser, titleRefererChart);
        drawNested('pie', 'arf_OS', '15', audienceRefGroup.statRefOS, titleRefererChart);
        drawNested('pie', 'arf_Device', '16', audienceRefGroup.statRefDevice, titleRefererChart);
        drawNested('pie', 'arf_country', '17', audienceRefGroup.statRefCountry, titleRefererChart);
    });
}

/*
 * Draw charts into container with id {containerId};
 *
 * @param type        - type of chart [pie, line, ...];
 * @param containerId - id of container;
 * @param containerParent - id of parent container;
 * @param objectData  - object with data;
 * @param title       - title of chart;
 */
function drawChart(type, containerParent, containerId, objectData, title) {
    if (objectData == null ||
        type == undefined ||
        objectData == undefined) {

        debugLog(`Invalid parameter (drawChart):
                     ${type}, ${containerId}, ${objectData}, ${title}`);

        return false;
    }

    let parentContainer = document.getElementById(containerParent);
    if (parentContainer == null) {
        debugLog(`parent container not found! containerId - '${containerParent}'`);
        return false;
    }

    let container = document.getElementById(containerId);
    if (container == null) {
        container = document.createElement("div");
        container.id = containerId;
    }

    if (parentContainer != null)
        parentContainer.appendChild(container);

    let arrayInfo = [];

    // set header
    arrayInfo.push(['', '']);

    // set data
    Object.entries(objectData).forEach(([key, value]) => arrayInfo.push([key, value]));

    let data = google.visualization.arrayToDataTable(arrayInfo);
    if (data == null)
        return false;

    // draw

    try {
        switch (type) {
            case "pie":
                new google.visualization.PieChart(container).draw(data, {
                    'title': title,
                    is3D: true,
                    height: 240
                });
                break;
            case "line":
                new google.visualization.LineChart(container).draw(data, {
                    'title': title,
                    height: 240
                });
                break;
            case "column":
                var view = new google.visualization.DataView(data);
                var optionsColumn = {
                    'title': title,
                    bar: { groupWidth: "95%" },
                    legend: { position: "none" },
                    height: 240
                };
                new google.visualization.ColumnChart(container).draw(view, optionsColumn);
                break;
            case "geo":
                new google.visualization.GeoChart(container).draw(data, {
                    'title': title,
                    height: 480
                });
                break;
            default:
                debugLog("drawChart:unknown type");
                return false;
        }
    } catch (exception) {
        debugLog(exception);
        return false;
    }


    return true;
}

/*
 * Drawing nested elements.
 */
function drawNested(type, containerParent, containerNamePrefix, objectData, titleChart) {
    if (objectData != null && objectData != undefined) {
        Object.entries(objectData).forEach(
            ([name, object]) => {
                drawChart(type, containerParent, containerNamePrefix + name, object, titleChart + ' ' + name);
            }
        );
    } else {
        debugLog('drawNested: invalud objectData!');
    }
}

/*
 * Resize grapics. 
 */
function resize() {
    if (analyticsData != null) {
        resetAnalyticsInfo();
        displayOfDataAnalytics(analyticsData);
    }
}

window.onresize = resize;

$(document.querySelectorAll('.collapse')).on('show.bs.collapse', window.onresize);