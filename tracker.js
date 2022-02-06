let cookieUid = getCookie('uid');

function sendDataToCollector() {

  const referer = (document.referrer == "") ? "" : btoa(new URL(document.referrer).host);
  const page = btoa(window.location.pathname);
  const scr = btoa(`${window.screen.width}x${window.screen.height}`);

  console.log(referer);

  const xhr = new XMLHttpRequest();
  xhr.open('POST', collectorAddr);

  let objectSend = {
    'siteId': siteId,
    'ref': referer,
    'page': page,
    'scr': scr,
    'uid': cookieUid
  };

  xhr.onreadystatechange = function () {
    if (xhr.readyState === XMLHttpRequest.DONE) {
      if (xhr.status === 201) { // Added new visitor
        // Set cookie
        cookieUid = xhr.response;
        document.cookie = `uid=${cookieUid};`
        console.log(document.cookie);
      }
    }
  }

  xhr.setRequestHeader('Content-type', 'application/json;charset=UTF-8');
  let jsonSend = JSON.stringify(objectSend, null, 2);
  xhr.send(jsonSend);
}

function getCookie(name) {
  let matches = document.cookie.match(new RegExp(
    "(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
  ));
  return matches ? decodeURIComponent(matches[1]) : undefined;
}

function ping() {
  const xhr = new XMLHttpRequest();
  const onlineStatPing = `${onlineStatAddr}ping`;
  xhr.open('POST', onlineStatPing);

  let objectSend = {
    'siteId': siteId,
    'visitorId': cookieUid
  };

  xhr.setRequestHeader('Content-type', 'application/json;charset=UTF-8');
  let jsonSend = JSON.stringify(objectSend, null, 2);
  xhr.send(jsonSend);
}

function pingRun() {
  ping();
  setInterval(ping, (onlineStatInterval * 1000));
}

sendDataToCollector();
pingRun();