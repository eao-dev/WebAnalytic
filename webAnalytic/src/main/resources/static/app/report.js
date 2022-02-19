/*
 * Create and send to server report for site;
 */
function createReport(siteId) {

    resetStatusSimplyModal();

    const fileName = document.getElementById('fileName').value;
    const srcReport = `
    <!DOCTYPE html>
    <html lang="ru">
        ${reportHeader}
    <body>
        <p class="text-center h1">${fileName}</p><br>
        ${document.getElementById(analyticInfoContainerId).innerHTML}
    </body>
    </html>`;

    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/reports/add');
    xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 201)
                showSuccessSimplyModal('Успешно');
            else
                showErrorSimplyModal('Ошибка');
        }
    }

    const data = new FormData();
    data.append('siteId', siteId);
    data.append('fileName', fileName);
    data.append('reportSource', srcReport);

    xhr.setRequestHeader(csrf_header, csrf_token);
    xhr.send(data);
}

const reportHeader = `
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta charset="UTF-8"/>
<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/css/bootstrap.min.css"
      rel="stylesheet"
      integrity="sha384-+0n0xVW2eSR5OomGNYDnhzAbDsOXxcvSN1TPprVMTNDbiYZCxYbOOl7+AMvyTG2x"
      crossorigin="anonymous"/>
<link rel="preconnect" href="https://fonts.googleapis.com"/>
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin/>
<link href="https://fonts.googleapis.com/css2?family=EB+Garamond:ital,wght@0,400;0,700;1,400&display=swap" rel="stylesheet"/>
<!-- BOOTSTRAP 5 -->
<script
  src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/js/bootstrap.bundle.min.js"
  integrity="sha384-gtEjrD/SeCtmISkJkNUaaKMoLD0//ElJ19smozuHV6z3Iehds+3Ulb9Bn9Plx0x4" crossorigin="anonymous">
</script>
<title>Отчёт</title>
</head>`;