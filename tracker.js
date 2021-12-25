function sendDataToCollector() {

  const referer = btoa(document.referrer);
  const page    = btoa(window.location.pathname);
  const scr     = btoa(`${window.screen.width}x${window.screen.height}`);

  const xhr = new XMLHttpRequest();
  xhr.open('POST', `${trackerAddr`}/collector);

  let objectSend = {
    'siteId':siteId,
    'ref': referer,
    'page': page,
    'scr': scr
  };

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
    'siteId':siteId,
    'visitorId': getCookie('uid')
  };

  xhr.setRequestHeader('Content-type', 'application/json;charset=UTF-8');
  let jsonSend = JSON.stringify(objectSend, null, 2);
  xhr.send(jsonSend);
}

function pingRun() {
  ping();
  setInterval(ping, (onlineStatInterval*1000) );
}

sendDataToCollector();
pingRun();