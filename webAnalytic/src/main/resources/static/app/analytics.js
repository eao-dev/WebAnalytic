/*
* Add to page elements checkbox and label for filter;
*
* @param jsonFuncInfoString - JSON object about filter from server;
*/
function updateFilter(jsonFuncInfoString) {
    let formFilter = document.getElementById('formFilter');
    let funcInfoObject = JSON.parse(jsonFuncInfoString);

    let outHtmlCode = '';

    Object.entries(funcInfoObject).forEach(([key, value]) => {
        let src = funcInfoObject[key];
        outHtmlCode +=
            `<label class="form-check-label"> ${value} </label>
            <input class="form-check-input" type="checkbox" name='${key}' checked />
            <br>`;
    });
    formFilter.innerHTML = outHtmlCode;
}

/*
* Get analytics information from server(using filters) for draw charts and show text;
*/
function updateData(){
    console.log('Get analytic data');

    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/websiteManagement/analytics');
    xhr.onreadystatechange = function() {
    if (xhr.readyState === XMLHttpRequest.DONE){
			if(xhr.status === 200) handleJSON_DataAnalytics(xhr.response); else alert('Ошибка получения данных с сервера');
		}
    }

    const data = new FormData(document.formFilter);
    const formJSON = Object.fromEntries(data.entries());

    let filter = JSON.stringify(formJSON, null, 2);

    xhr.setRequestHeader('Content-type', 'application/json;charset=UTF-8');
    xhr.setRequestHeader(csrf_header, csrf_token);
    xhr.send(filter);
}

const analyticInfoContainerId = 'analyticInfo';
const textInfoContainerId = 'textInfo';
const chartsContainerId = 'charts';

/*
* Draws all charts on the page.
* @param jsonData - data to be displayed on charts.
*/
function handleJSON_DataAnalytics(jsonData) {
    let analyticDataObject = JSON.parse(jsonData);
    const generalObject = analyticDataObject.general;
    const audienceObject = analyticDataObject.audience;
    const audienceResource = analyticDataObject.audienceResource;
    const audienceReferer = analyticDataObject.audienceReferer;

    // Set text view
    let containerText = document.getElementById(textInfoContainerId);
    let outInfo = `
        <p>Посетителей online: ${generalObject.onlineVisitors}</p>
        <p>Всего уникальных посетителей: ${generalObject.allUniqueVisitors}</p>
        <p>Всего просмотров: ${generalObject.allCountVisitedResource}</p>
        <p>Среднее количество просмотров страниц: ${generalObject.avgCountVisitedResource}</p>`;
    containerText.innerHTML += outInfo;

    google.charts.load('current', {'packages':['corechart']});
    google.charts.load('current', {'packages':['geochart'],'mapsApiKey': 'AIzaSyD-9tSrke72PouQMnMX-a7eZSW0jkFMBWY'});
    google.charts.setOnLoadCallback(drawCharts);

    function drawCharts() {
        // Filling the array for draw charts

        let chartsArray = [
            // General
            ['line', 'general', 'visitedOnDayChart', generalObject.statVisitOnDay, 'Посетителей в день'],
            ['pie', 'general', 'generalStatRefererChart', generalObject.statReferer,'Статистика переходов'],
            ['pie', 'general', 'generalStatResourcesChart', generalObject.statResource,'Статистика посещений страниц'],

            // Audience
            [ 'pie', 'audience', 'audienceOSChart', audienceObject.OS,'Статистика использования операционных систем'],
            [ 'column', 'audience', 'audienceBrowserChart', audienceObject.browser,'Статистика использования браузеров'],
            [ 'column', 'audience', 'audienceDeviceChart', audienceObject.Device,'Статистика использования аппаратных устройств'],
            [ 'column', 'audience', 'audienceScResolutionChart', audienceObject.ScResolution,'Статистика посетителей по разрешениям экрана'],
            [ 'geo', 'audience', 'audienceCountryChart', audienceObject.Country,'Статистика посетителей по странам'],
        ];

        // Audience resources
        const titleResourceChart = 'Страница';

        if (audienceResource.ScResolution!=null && audienceResource.ScResolution!=undefined)
        Object.entries(audienceResource.ScResolution).forEach(([name, object]) =>
             chartsArray.push(['pie', 'audience_res_ScResolution', 'audience_res_scr_' + name, object, titleResourceChart + ' ' + name]));

        if (audienceResource.browser!=null && audienceResource.browser!=undefined)
        Object.entries(audienceResource.browser).forEach(([name, object]) =>
            chartsArray.push(['column','audience_res_browser', 'audience_res_browser_'+ name, object ,titleResourceChart + ' ' + name]));

        if (audienceResource.OS!=null && audienceResource.OS!=undefined)
        Object.entries(audienceResource.OS).forEach(([name, object]) =>
            chartsArray.push(['column','audience_res_OS', 'audience_res_os_'+ name, object ,titleResourceChart + ' ' + name]));

        if (audienceResource.Device!=null && audienceResource.Device!=undefined)
        Object.entries(audienceResource.Device).forEach(([name, object]) =>
             chartsArray.push(['column','audience_res_Device', 'audience_res_device_'+ name, object ,titleResourceChart + ' ' + name]));

        if (audienceResource.Country!=null && audienceResource.Country!=undefined)
        Object.entries(audienceResource.Country).forEach(([name, object]) =>
            chartsArray.push(['pie','audience_res_Country', 'audience_res_country_'+ name, object ,titleResourceChart + ' ' + name]));

        // Audience referer
        const titleRefererChart = 'Сторонний ресурс';
        if (audienceReferer.ScResolution!=null && audienceReferer.ScResolution!=undefined)
        Object.entries(audienceReferer.ScResolution).forEach(([name, object]) =>
             chartsArray.push(['pie', 'audience_ref_ScResolution', 'audience_ref_scr_'+ name, object ,titleRefererChart + ' ' + name]));

        if (audienceReferer.browser!=null && audienceReferer.browser!=undefined)
        Object.entries(audienceReferer.browser).forEach(([name, object]) =>
             chartsArray.push(['column', 'audience_ref_browser', 'audience_ref_browser_'+ name, object ,titleRefererChart + ' ' + name]));

        if (audienceReferer.OS!=null && audienceReferer.OS!=undefined)
        Object.entries(audienceReferer.OS).forEach(([name, object]) =>
             chartsArray.push(['column', 'audience_ref_OS', 'audience_ref_os_'+ name, object ,titleRefererChart + ' ' + name]));

        if (audienceReferer.Device!=null && audienceReferer.Device!=undefined)
        Object.entries(audienceReferer.Device).forEach(([name, object]) =>
            chartsArray.push(['column', 'audience_ref_Device', 'audience_ref_device_'+ name, object ,titleRefererChart + ' ' + name]));

        if (audienceReferer.Country!=null && audienceReferer.Country!=undefined)
        Object.entries(audienceReferer.Country).forEach(([name, object]) =>
            chartsArray.push(['pie', 'audience_ref_Country', 'audience_ref_country_'+ name, object ,titleRefererChart + ' ' + name]));

        // Draw pie charts
        chartsArray.forEach(function(item){
            const type            = item[0];
            const containerParent = item[1];
            const containerName   = item[2];
            const objectData      = item[3];
            const titleChart      = item[4];
            const columnHeader1   = item[5];
            const columnHeader2   = item[6];
            if (!drawChart(type, containerParent, containerName, objectData, titleChart, columnHeader1, columnHeader2)){
                console.log(`error draw pie chart:
                containerName: ${containerName}, title: ${titleChart}, c_header1: ${columnHeader1},
                c_header2:${columnHeader2}, object:${objectData}`);
            }
        });

    }
}

/*
* Draw  charts into container with id {containerId};
*
* @param type        - type of chart [pie, line, ...];
* @param containerId - id of container;
* @param containerParent - id of parent container;
* @param objectData  - object with data;
* @param title       - title of chart;
* @param columnHeader 1 - head of column1;
* @param columnHeader 2 - head of column2;
*/
function drawChart(type, containerParent, containerId, objectData, title, columnHeader1, columnHeader2){
    if (objectData === undefined ||
        type === undefined ||
        objectData === undefined ){

        console.log(`Invalid parameter (drawChart):
                     ${type}, ${containerId}, ${objectData}, ${title}, ${columnHeader1}, ${columnHeader2}`);

        return false;
    }

    if (columnHeader1 === undefined) columnHeader1 = '';
    if (columnHeader2 === undefined) columnHeader2 = '';

    let parentContainer = document.getElementById(containerParent);
    if (parentContainer==null) {
        console.log(`parent container not found! containerId - '${containerParent}'`);
        return false;
    }

    let container = document.getElementById(containerId);
    if (container==null){
        container = document.createElement("div");
        container.id = containerId;
        let parentContainer = document.getElementById(chartsContainerId);
        if (parentContainer != null || parentContainer !== undefined) {
            parentContainer.appendChild(container);
        }
    }

    parentContainer.appendChild(container);

    let arrayInfo = [];

    // set header
    arrayInfo.push([columnHeader1, columnHeader2]);

    // set data
    Object.entries(objectData).forEach(([key, value]) => arrayInfo.push([key, value]));
    console.log(arrayInfo);

    let data = google.visualization.arrayToDataTable(arrayInfo);
    if (data == null || data === undefined)
        return false;

    // draw

    switch (type) {
        case "pie":
            new google.visualization.PieChart(container).draw(data, {'title':title,is3D:true,
               width: 1024,
               height: 480});
            break;
        case "line":
            new google.visualization.LineChart(container).draw(data,  {'title':title,
            width: 1024,
            height: 480});
            break;
        case "column":
          var view = new google.visualization.DataView(data);
          var optionsColumn = {
                'title': title,
                bar: {groupWidth: "95%"},
                legend: { position: "none" },
                width: 1024,
                height: 480
            };
            new google.visualization.ColumnChart(container).draw(view, optionsColumn);
            break;
        case "geo":
            new google.visualization.GeoChart(container).draw(data, {'title':title,
            width: 1024,
            height: 480});
            break;
        default:
            console.log("drawChart:unknown type");
            return false;
    }

    return true;
}

/*
* Create and send to server report for site;
*/
function createReport(siteId) {

    resetStatusSimplyModal();

    fileName = document.getElementById('fileName').value;
    let srcReport = `
    <!DOCTYPE html>
    <html lang="ru">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta charset="UTF-8"/>
        <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/css/bootstrap.min.css"
              rel="stylesheet"
              integrity="sha384-+0n0xVW2eSR5OomGNYDnhzAbDsOXxcvSN1TPprVMTNDbiYZCxYbOOl7+AMvyTG2x"
              crossorigin="anonymous"/>
        <link rel="stylesheet" href="/fontawesome/css/all.min.css"/>
        <link rel="preconnect" href="https://fonts.googleapis.com"/>
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin/>
        <link href="https://fonts.googleapis.com/css2?family=EB+Garamond:ital,wght@0,400;0,700;1,400&display=swap"
              rel="stylesheet"/>
        <title>Отчёт ${fileName}</title>
    </head>

    <body>
        <center><h1>Отчёт ${fileName}</h1></center><br>
        ${document.getElementById(analyticInfoContainerId).innerHTML}
    </body>
    <script
            src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-gtEjrD/SeCtmISkJkNUaaKMoLD0//ElJ19smozuHV6z3Iehds+3Ulb9Bn9Plx0x4"
            crossorigin="anonymous"
    ></script>
    </html>
    `;

    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/reports/add');
    xhr.onreadystatechange = function() {
    if (xhr.readyState === XMLHttpRequest.DONE){
      if(xhr.status === 201) showSuccessSimplyModal('Успешно'); else showErrorSimplyModal('Ошибка');
     }
    }

    const data = new FormData();
    data.append('siteId', siteId);
    data.append('fileName', fileName);
    data.append('reportSource', srcReport);

    xhr.setRequestHeader(csrf_header, csrf_token);
    xhr.send(data);
}