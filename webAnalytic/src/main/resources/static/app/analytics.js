const debugMode = true;

/*
 * This object contains ID of containers.
 */
const containersId = {
    textInfo: 'textInfo',
    general: 'general',
    audience: 'audience',

    arsSc: 'ars_ScResolution',
    arsBrowser: 'ars_browser',
    arsOS: 'ars_OS',
    arsDevice: 'ars_Device',
    arsCountry: 'ars_country',

    arfScR: 'arf_ScResolution',
    arfBrowser: 'arf_browser',
    arfOS: 'arf_OS',
    arfDevice: 'arf_Device',
    arfCountry: 'arf_country'
};


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
    for (key in containersId) {
        const div = document.getElementById(containersId[key]);
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
                alert('???????????? ?????????????????? ???????????? ?? ??????????????');
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
    let z = containersId.textInfo;
    let containerText = document.getElementById(z);
    let outInfo = `
        <div class="textGeneralInfo">
        <b>?????????????????????? online:</b> ${generalGroup.online} <br>
        <b>?????????? ???????????????????? ??????????????????????:</b> ${generalGroup.allUniqueVisitor}<br>
        <b>?????????? ????????????????????:</b> ${generalGroup.allCountVisitedRes}<br>
        <b>?????????????? ???????????????????? ???????????????????? ??????????????:</b> ${generalGroup.avgCountVisitedRes}<br>
        </dvi>`;
    containerText.innerHTML += outInfo;

    // Charts
    google.charts.load('current', { 'packages': ['corechart'] });
    google.charts.load('current', { 'packages': ['geochart'], 'mapsApiKey': 'AIzaSyD-9tSrke72PouQMnMX-a7eZSW0jkFMBWY' });
    google.charts.setOnLoadCallback(function() {
        // General
        drawChart('line', containersId.general, '0', generalGroup.statVisitOnDay, '?????????????????? ?? ????????');
        drawChart('pie', containersId.general, '1', generalGroup.statReferer, '???????????????????? ??????????????????');
        drawChart('pie', containersId.general, '2', generalGroup.statResource, '???????????????????? ?????????????????? ??????????????');

        // Audience
        const audienceGroup = analyticDataObject.audienceGroup;
        drawChart('pie', containersId.audience, '3', audienceGroup.statOS, '???????????????????? ?????????????????????????? ???????????????????????? ????????????');
        drawChart('column', containersId.audience, '4', audienceGroup.statBrowser, '???????????????????? ?????????????????????????? ??????????????????');
        drawChart('column', containersId.audience, '5', audienceGroup.statDevice, '???????????????????? ?????????????????????????? ???????????????????? ??????????????????');
        drawChart('column', containersId.audience, '6', audienceGroup.statScResolution, '???????????????????? ?????????????????????? ???? ?????????????????????? ????????????');
        drawChart('geo', containersId.audience, '7', audienceGroup.statCountry, '???????????????????? ?????????????????????? ???? ??????????????');


        // Audience resources
        const titleResourceChart = '????????????????';
        const audienceResGroup = analyticDataObject.audienceResGroup;
        drawNested('pie', containersId.arsSc, '8', audienceResGroup.statResScResolution, titleResourceChart);
        drawNested('pie', containersId.arsBrowser, '9', audienceResGroup.statResBrowser, titleResourceChart);
        drawNested('pie', containersId.arsOS, '10', audienceResGroup.statResOS, titleResourceChart);
        drawNested('pie', containersId.arsDevice, '11', audienceResGroup.statResDevice, titleResourceChart);
        drawNested('pie', containersId.arsCountry, '12', audienceResGroup.statResCountry, titleResourceChart);

        // Audience referer
        const titleRefererChart = '?????????????????? ????????????';
        const audienceRefGroup = analyticDataObject.audienceRefGroup;
        drawNested('pie', containersId.arfScR, '13', audienceRefGroup.statRefScResolution, titleRefererChart);
        drawNested('pie', containersId.arfBrowser, '14', audienceRefGroup.statRefBrowser, titleRefererChart);
        drawNested('pie', containersId.arfOS, '15', audienceRefGroup.statRefOS, titleRefererChart);
        drawNested('pie', containersId.arfDevice, '16', audienceRefGroup.statRefDevice, titleRefererChart);
        drawNested('pie', containersId.arfCountry, '17', audienceRefGroup.statRefCountry, titleRefererChart);
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
$(document.querySelectorAll('.collapse')).on('show.bs.collapse', resize);