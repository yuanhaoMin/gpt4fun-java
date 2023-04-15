const messagesContainer = document.getElementById('messages-container');
const inputBox = document.getElementById('input-box');
const sendButton = document.getElementById('send-button');
const updateButton = document.getElementById('update-button');
const resetButton = document.getElementById('reset-button');

// for text area
function auto_grow(element) {
	console.log(element.style.height);
	console.log(element.style.scrollHeight);
    element.style.height = "44px";
    element.style.height = (element.scrollHeight)+"px";
}
function reset_height(element) {
	element.style.height = element.style.minHeight;
}


sendButton.addEventListener('click', sendUserInputAndDisplayResponse);
inputBox.addEventListener('keydown', (event) => {
    if (event.key === 'Enter') {
        sendUserInputAndDisplayResponse();
    }
});
async function sendUserInputAndDisplayResponse() {
    var userInput = inputBox.value.trim();
    if (!userInput)
        return;
	userInput = '我： ' + userInput;
    createAndAppendMessage(userInput, 'user');
    inputBox.value = '';
	reset_height(inputBox);
    const response = await completeChat(userInput);
    createAndAppendMessage(response, 'assistant');
}
async function completeChat(userMessage) {
    const apiEndpoint = 'http://localhost:8080/chamber/chat/messages';
    const userId = 1;
    const requestBody = {
        userId,
        userMessage: userMessage,
    };
    const response = await fetch(apiEndpoint, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestBody)
    });
    // use object destructuring to get responseMessage directly
    const {
        responseMessage
    } = await response.json();
    return responseMessage;
}
function createAndAppendMessage(text, sender) {
    const messageElement = document.createElement('div');
    messageElement.classList.add('message', sender);
    text = text.replace(/\n/g, '<br>');
    console.log(text);
    messageElement.innerHTML = text;
    messagesContainer.appendChild(messageElement);
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

resetButton.addEventListener('click', resetHistory);
async function resetHistory() {
    const userId = 1;
    const apiEndpoint = `http://localhost:8080/chamber/chat/history/${userId}`;
    const response = await fetch(apiEndpoint, {
        method: 'DELETE'
    });
    const {
        responseMessage
    } = await response.json();
    messagesContainer.innerHTML = '';
	inputBox.value = '';
	reset_height(inputBox);
    createAndAppendMessage(responseMessage, 'assistant');
}

updateButton.addEventListener('click', updateSystemMessage);
async function updateSystemMessage() {
    const systemMessage = inputBox.value.trim();
    if (!systemMessage)
        return;
    const userId = 1;
    const apiEndpoint = `http://localhost:8080/chamber/chat/messages/${userId}`;
    const requestBody = {
        systemMessage: systemMessage
    };
    const response = await fetch(apiEndpoint, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestBody)
    });
	inputBox.value = '';
	reset_height(inputBox);
    const {
        responseMessage
    } = await response.json();
    createAndAppendMessage(responseMessage, 'assistant');
}
