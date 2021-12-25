var csrf_header = document.querySelector("meta[name='_csrf_header']").getAttribute('content');
var csrf_token = document.querySelector('meta[name="_csrf"]').getAttribute('content');

// Simply modal window:

const simpleMsgSuccessId = "simplyModalSuccess";
const simpleMsgErrorId = "simplyModalError";

const setSimpleModalBodyId = 'simplyModalBody';
const setSimpleModalTitleId = 'simplyModalTitle';

function getSimpleModalBody(){
    return document.getElementById(setSimpleModalBodyId).innerHTML;
}

function setSimpleModalBody(bodySource){
    document.getElementById(setSimpleModalBodyId).innerHTML = bodySource;
}

function getSimpleModalTitle(){
    return document.getElementById(setSimpleModalTitleId).innerHTML;
}

function setSimpleModalTitle(titleText){
    document.getElementById(setSimpleModalTitleId).innerHTML = titleText;
}

function showSuccessSimplyModal(msgText) {
    let container = document.getElementById(simpleMsgSuccessId);
	container.style.display = 'block';
    container.innerHTML = msgText;
}

function showErrorSimplyModal(msgText) {
    let container = document.getElementById(simpleMsgErrorId);
	container.style.display = 'block';
    container.innerHTML = msgText;
}

/*
* Hidden status message(error/success) in simply modal window;
*/
function resetStatusSimplyModal(){
    let containerSuccess = document.getElementById(simpleMsgSuccessId);
    let containerError = document.getElementById(simpleMsgErrorId);

    containerSuccess.innerHTML = "";
    containerError.innerHTML = "";
    containerError.style.display = 'none';
    containerSuccess.style.display = 'none';
}