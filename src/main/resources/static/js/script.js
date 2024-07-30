function validateForm() {
    const input = document.getElementById('promptInput');
    const errorMessage = document.getElementById('error-message');

    if (input.value.trim() === '') {
        // Input is empty, show error
        input.classList.add('input-error');
        errorMessage.style.display = 'block';
        return false; // Prevent form submission
    } else {
        // Input is not empty, proceed with form submission
        input.classList.remove('input-error');
        errorMessage.style.display = 'none';
        sendPrompt(); // Call sendPrompt() function
        return false; // Prevent default form submission
    }
}

// 쿠키를 가져오는 함수
function getCookie(key) {
    var result = null;
    var cookie = document.cookie.split(';');
    cookie.some(function (item) {
        item = item.replace(' ', '');

        var dic = item.split('=');

        if (key === dic[0]) {
            result = dic[1];
            return true;
        }
    });

    return result;
}

// 쿠키를 삭제하는 함수
function deleteCookie(name) {
    document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}

function httpRequest(method, url, body, success, fail) {
    fetch(url, {
        method: method,
        headers: {
            Authorization: 'Bearer ' + localStorage.getItem('access_token'),
            'Content-Type': 'application/json',
        },
        body: body,
    }).then(response => {
        if (response.ok) { // Check if the response status is in the range 200-299
            return response.json().catch(() => ({})); // Handle empty JSON responses
        } else if (response.status === 401 && getCookie('refresh_token')) {
            // Refresh token logic
            const refresh_token = getCookie('refresh_token');
            return fetch('/api/token', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ refreshToken: refresh_token }),
            })
                .then(res => {
                    if (res.ok) {
                        return res.json();
                    }
                    throw new Error('Failed to refresh token');
                })
                .then(result => {
                    localStorage.setItem('access_token', result.accessToken);
                    return fetch(url, {
                        method: method,
                        headers: {
                            Authorization: 'Bearer ' + localStorage.getItem('access_token'),
                            'Content-Type': 'application/json',
                        },
                        body: body,
                    }).then(res => res.json().catch(() => ({})));
                });
        } else {
            throw new Error('Request failed');
        }
    })
        .then(data => success(data))
        .catch(error => fail(error));
}

// 성공 및 실패 콜백 함수 정의
function success(data) {
    const content = data.choices[0].message.content;
    redirectToPrompt(content);
}

function fail() {
    alert("만료된 토큰이거나 로그인이 되지 않았습니다.");
    window.location.href = '/login';
}

// /views/prompt로 리디렉션 요청 함수 정의
function redirectToPrompt(content) {
    const body = JSON.stringify({ content: content });

    httpRequest("POST", "/views/prompt", body, function() {
        // 서버에서 세션에 저장된 데이터를 사용하는 페이지로 리디렉션
        window.location.href = '/views/prompt';
    }, fail);
}

// sendPrompt 함수 수정
function sendPrompt() {
    const input = document.getElementById('promptInput').value;
    const body = JSON.stringify({ userPrompt: input });

    httpRequest("POST", "/api/v1/chatGpt/prompt", body, success, fail);
}

function copyContent() {
    const copyBtn = document.getElementById('copyBtn');
    const content = document.getElementById('content');

    if (copyBtn && content) { // Ensure elements are not null
        const range = document.createRange();
        range.selectNode(content);
        window.getSelection().removeAllRanges();
        window.getSelection().addRange(range);

        try {
            document.execCommand('copy');
            // Change button text to "복사되었습니다!"
            copyBtn.textContent = '복사되었습니다!';
        } catch (err) {
            console.error('복사 실패: ', err);
            // Optionally, you could revert the button text in case of error
            copyBtn.textContent = '복사 실패';
        }

        window.getSelection().removeAllRanges();
    } else {
        console.error('복사 버튼 또는 내용 요소를 찾을 수 없습니다.');
    }
}

function backAction() {
    window.location.href = '/views/generator'
}