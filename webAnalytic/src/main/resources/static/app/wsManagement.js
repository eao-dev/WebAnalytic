/**
Config for JS-tracker
*/
const onlineStatAddr = 'http://127.0.0.1:8081/';
const collectorAddr = 'http://127.0.0.1:8080/collector/';
const onlineStatInterval = 10;

/**
 *
 * Change permission access for user in relation to the site;
 * Set status to simply modal windows.
 */
function changePermissionSiteAccess(userId, siteId, state) {
    resetStatusSimplyModal();

    const xhr = new XMLHttpRequest();
    xhr.open('PUT', '/userManagement/changePermissionSiteAccess/');
    xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE) {

            switch (xhr.status) {
                case 202:
                    {
                        showSuccessSimplyModal(JSON.parse(xhr.response).actionStatus.message);
                        break;
                    }
                case 200:
                    {
                        showErrorSimplyModal(JSON.parse(xhr.response).actionStatus.message)
                        break;
                    }
                default:
                    {
                        showErrorSimplyModal(`Ошибка: код сервера ${xhr.status}`);
                        break;
                    }
            }
        }
    }

    let formData = new FormData();
    formData.append('userId', userId);
    formData.append('siteId', siteId);
    formData.append('state', state.checked);

    xhr.setRequestHeader(csrf_header, csrf_token);
    xhr.send(formData);
}

/**
 * Set the tracker-code to simply modal window body.
 */
function generateCode(siteId) {
    setSimpleModalTitle('Javascript-крд для размещения на сайте');

    let outStringScript = `<script>const siteId=${siteId};const collectorAddr='${collectorAddr}';
    const onlineStatAddr='${onlineStatAddr}';const onlineStatInterval = ${onlineStatInterval};</script>
    <script async src='/tracker.js'></script>`;
    outStringScript = outStringScript.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    setSimpleModalBody('<div class="codeShow"><p>' + outStringScript + '</p></div>');
}

/**
 * Load list of users with access state for specific site.
 */
function loadUserList(siteId) {
    resetStatusSimplyModal();

    setSimpleModalTitle('Список пользователей имеющих доступ к сайту');
    setSimpleModalBody('');

    const xhr = new XMLHttpRequest();
    xhr.open('GET', `/userManagement/getPermission/?siteId=${siteId}`);
    xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                let fullModalBody = "";
                let obj = JSON.parse(xhr.response);
                for (let item of obj.userListPermission) {
                    let name, id;
                    let state = item["isAllow"];
                    for (let key in item) {
                        name = key;
                        id = item[name];
                        break;
                    }

                    check = state ? 'checked' : '';

                    let modalBodyItem = `<div class="form-check form-switch">
                                   <label class="form-check-label"> ${name}  </label>
                                   <input class="form-check-input"
                                    onchange='changePermissionSiteAccess(${id},${siteId}, this)'
                                   type="checkbox" ${check}/></div>`;

                    fullModalBody += modalBodyItem;
                }
                setSimpleModalBody(`<div class="modal-user-access">${fullModalBody}</div>`)

            } else modalBody.innerHtml = "Ошибка:" + xhr.status;
        }
    }
    let formData = new FormData();
    formData.append('siteId', siteId);

    xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhr.send(formData);
}