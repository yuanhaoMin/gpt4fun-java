const messagesContainer = document.getElementById('messages-container');
const inputBox = document.getElementById('input-box');
const resetButton = document.getElementById('reset-button');
const sendButton = document.getElementById('send-button');
const updateButton = document.getElementById('update-button');

// Shared functions
function createAndAppendMessage(text, sender) {
    const messageElement = document.createElement('div');
    const messageParagraph = document.createElement('p');
    messageElement.classList.add('message', sender);
    messageParagraph.style.width = '100%'; // Achieve word wrap
    messageParagraph.style.wordWrap = 'break-word'; // Achieve word wrap
	const processedText = text.replace(/\n/g, '<br/>'); // Required anyway
    messageParagraph.innerHTML = processedText; // Set text in paragraph
    messageElement.appendChild(messageParagraph);
    messagesContainer.appendChild(messageElement);
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}
function clearAllTextBox() {
    messagesContainer.innerHTML = '';
    inputBox.value = '';
}

sendButton.addEventListener('click', senduserMessageAndDisplayResponse);
inputBox.addEventListener('keydown', (event) => {
    if (event.key === 'Enter') {
        senduserMessageAndDisplayResponse();
    }
});
async function senduserMessageAndDisplayResponse() {
    const userMessage = inputBox.value.trim();
    inputBox.value = '';
    if (!userMessage)
        return;
    const displayedUserMessage = 'User： ' + userMessage;
    createAndAppendMessage(displayedUserMessage, 'user');
    // const responseMessage = 'HIHIHHHIHIHIHIHIHIHIIHIHIHI';
    const responseMessage = await completeChatApi(userMessage);
    createAndAppendMessage(responseMessage, 'assistant');
}
async function completeChatApi(userMessage) {
    const apiEndpoint = 'http://gpt4fun-gpt4fun-be.azuremicroservices.io/chamber/chat/messages';
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

resetButton.addEventListener('click', resetHistory);
async function resetHistory() {
    const userId = 1;
    clearAllTextBox();
    const apiEndpoint = `http://gpt4fun-gpt4fun-be.azuremicroservices.io/chamber/chat/history/${userId}`;
    const response = await fetch(apiEndpoint, {
        method: 'DELETE'
    });
    const {
        responseMessage
    } = await response.json();
    createAndAppendMessage(responseMessage, 'assistant');
}

updateButton.addEventListener('click', updateSystemMessage);
async function updateSystemMessage() {
    const systemMessage = inputBox.value.trim();
    if (!systemMessage)
        return;
    clearAllTextBox();
    const responseMessage = await updateSystemMessageApi(systemMessage);
    createAndAppendMessage(responseMessage, 'assistant');
}
async function updateSystemMessageApi(systemMessage) {
    const userId = 1;
    const apiEndpoint = `http://gpt4fun-gpt4fun-be.azuremicroservices.io/chamber/chat/messages/${userId}`;
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
    const {
        responseMessage
    } = await response.json();
    return responseMessage
}

// Record
let isRecording = false;
let chunks = [];

const recordButton = document.getElementById('record-button');

recordButton.addEventListener('click', () => {
    if (!isRecording) {
		isRecording = true;
        recordButton.style.backgroundColor = '#225c5b';
    } else {
		isRecording = false;
        recordButton.style.backgroundColor = '#343541';
    }
});

function startRecording() {
    chunks = [];
    const mediaRecorder = new MediaRecorder(audioStream);
    mediaRecorder.addEventListener('dataavailable', event => {
        chunks.push(event.data);
    });
    mediaRecorder.addEventListener('stop', () => {
        isRecording = false;
        const blob = new Blob(chunks);
        const formData = new FormData();
        formData.append('audio', blob, 'audio.wav');
        const response = fetch('http://gpt4fun-gpt4fun-be.azuremicroservices.io/chamber/speech/speech-to-text', {
            method: 'POST',
            headers: {
                'Content-Type': 'multipart/form-data'
            },
            body: formData
        });
        const {
            responseMessage
        } = response.json();
    });
    mediaRecorder.start();
}

function stopRecording() {
    const mediaRecorder = new MediaRecorder(audioStream);
    mediaRecorder.stop();
}
