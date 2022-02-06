const debugMode = true;

const analyticInfoContainerId = 'analyticInfo';
const textInfoContainerId = 'textInfo';
const chartsContainerId = 'charts';

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

        'audience_res_ScResolution',
        'audience_res_OS',
        'audience_res_browser',
        'audience_res_Device',
        'audience_res_country',

        'audience_ref_ScResolution',
        'audience_ref_browser',
        'audience_ref_OS',
        'audience_ref_Device',
        'audience_ref_country'];

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

    let filling = function (filter) {
        Object.entries(filter).forEach(([key, value]) => {
            outHtmlCode +=
                `<label class="form-check-label"> ${value} </label>
                <input class="form-check-input" type="checkbox" name='${key}' checked />
                <br>`;
        });
        outHtmlCode += '<hr>';
    }

    // Filling 
    filling(filters.General);
    filling(filters.Audience);
    filling(filters.Audience_res);
    filling(filters.Audience_ref);

    formFilter.innerHTML = outHtmlCode;
}

/*
* Get analytics information from server(using filters) for draw charts and show text;
*/
function updateDataAnalytics() {
    resetAnalyticsInfo();
    //debugLog('Get analytic data');

    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/analytics');
    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                displayOfDataAnalytics(xhr.response);
                // Show general info
                let collapse = document.getElementById('collapse1');
                if (collapse != null)
                    collapse.classList.add('show');
            }
            else
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
    const generalObject = analyticDataObject.general;

    // Set text view
    let containerText = document.getElementById(textInfoContainerId);
    let outInfo = `
        <p>Посетителей online: ${generalObject.onlineVisitors}</p>
        <p>Всего уникальных посетителей: ${generalObject.allUniqueVisitors}</p>
        <p>Всего просмотров: ${generalObject.allCountVisitedResource}</p>
        <p>Среднее количество просмотров страниц: ${generalObject.avgCountVisitedResource}</p>`;
    containerText.innerHTML += outInfo;

    google.charts.load('current', { 'packages': ['corechart'] });
    google.charts.load('current', { 'packages': ['geochart'], 'mapsApiKey': 'AIzaSyD-9tSrke72PouQMnMX-a7eZSW0jkFMBWY' });
    google.charts.setOnLoadCallback(gchartsCallback);

    function gchartsCallback() {
        // General
        drawChart('line', 'general', 'visitedOnDayChart', generalObject.statVisitOnDay, 'Посетителей в день');
        drawChart('pie', 'general', 'generalStatRefererChart', generalObject.statReferer, 'Статистика переходов');
        drawChart('pie', 'general', 'generalStatResourcesChart', generalObject.statResource, 'Статистика посещений страниц');

        // Audience
        const audienceObject = analyticDataObject.audience;
        drawChart('pie', 'audience', 'audienceOSChart', audienceObject.OS, 'Статистика использования операционных систем');
        drawChart('column', 'audience', 'audienceBrowserChart', audienceObject.browser, 'Статистика использования браузеров');
        drawChart('column', 'audience', 'audienceDeviceChart', audienceObject.Device, 'Статистика использования аппаратных устройств');
        drawChart('column', 'audience', 'audienceScResolutionChart', audienceObject.ScResolution, 'Статистика посетителей по разрешениям экрана');
        drawChart('geo', 'audience', 'audienceCountryChart', audienceObject.Country, 'Статистика посетителей по странам');

        // Audience resources
        const titleResourceChart = 'Страница';
        const audienceResource = analyticDataObject.audienceResource;
        drawNested('pie', 'audience_res_ScResolution', 'audience_res_scr_', audienceResource.ScResolution, titleResourceChart);
        drawNested('pie', 'audience_res_browser', 'audience_res_browser_', audienceResource.browser, titleResourceChart);
        drawNested('pie', 'audience_res_OS', 'audience_res_os_', audienceResource.OS, titleResourceChart);
        drawNested('pie', 'audience_res_Device', 'audience_res_device_', audienceResource.Device, titleResourceChart);
        drawNested('pie', 'audience_res_country', 'audience_res_country_', audienceResource.Country, titleResourceChart);

        // Audience referer
        const titleRefererChart = 'Сторонний ресурс';
        const audienceReferer = analyticDataObject.audienceReferer;
        drawNested('pie', 'audience_ref_ScResolution', 'audience_res_scr_', audienceReferer.ScResolution, titleRefererChart);
        drawNested('pie', 'audience_ref_browser', 'audience_ref_browser_', audienceReferer.browser, titleRefererChart);
        drawNested('pie', 'audience_ref_OS', 'audience_ref_os_', audienceReferer.OS, titleRefererChart);
        drawNested('pie', 'audience_ref_Device', 'audience_ref_device_', audienceReferer.Device, titleRefererChart);
        drawNested('pie', 'audience_ref_country', 'audience_ref_country_', audienceReferer.Country, titleRefererChart);
    }
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

    switch (type) {
        case "pie":
            new google.visualization.PieChart(container).draw(data, {
                'title': title, is3D: true,
                width: 1024,
                height: 480
            });
            break;
        case "line":
            new google.visualization.LineChart(container).draw(data, {
                'title': title,
                width: 1024,
                height: 480
            });
            break;
        case "column":
            var view = new google.visualization.DataView(data);
            var optionsColumn = {
                'title': title,
                bar: { groupWidth: "95%" },
                legend: { position: "none" },
                width: 1024,
                height: 480
            };
            new google.visualization.ColumnChart(container).draw(view, optionsColumn);
            break;
        case "geo":
            new google.visualization.GeoChart(container).draw(data, {
                'title': title,
                width: 1024,
                height: 480
            });
            break;
        default:
            debugLog("drawChart:unknown type");
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
    }
}