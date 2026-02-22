const API_URL = window.location.origin + '/api';

// Логин
document.getElementById('login-form')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch(`${API_URL}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: email,
                password: password
            })
        });

        if (!response.ok) {
            throw new Error('Неверный email или пароль');
        }

        const token = await response.text();
        localStorage.setItem('token', token);
        localStorage.setItem('email', email);

        window.location.href = '/index.html';
    } catch (error) {
        showError(error.message);
    }
});

// Регистрация
document.getElementById('register-form')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const firstName = document.getElementById('firstName').value;
    const lastName = document.getElementById('lastName').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (password !== confirmPassword) {
        showError('Пароли не совпадают');
        return;
    }

    try {
        const response = await fetch(`${API_URL}/users`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                firstName,
                lastName,
                email,
                password
            })
        });

        if (!response.ok) {
            throw new Error('Ошибка регистрации');
        }

        showSuccess('Регистрация успешна! Войдите в систему.');
        setTimeout(() => {
            window.location.href = '/login.html';
        }, 2000);
    } catch (error) {
        showError(error.message);
    }
});

// Выход
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('email');
    window.location.href = '/login.html';
}

// Проверка авторизации
function checkAuth() {
    const token = localStorage.getItem('token');
    if (!token && !window.location.pathname.includes('login') && !window.location.pathname.includes('register')) {
        window.location.href = '/login.html';
    }
}

// Хелперы
function showError(message) {
    const errorEl = document.getElementById('error-message');
    if (errorEl) {
        errorEl.textContent = message;
        errorEl.style.display = 'block';
    }
}

function showSuccess(message) {
    const successEl = document.getElementById('success-message');
    if (successEl) {
        successEl.textContent = message;
        successEl.style.display = 'block';
    }
}

function getAuthHeaders() {
    const token = localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

// Проверяем при загрузке
checkAuth();