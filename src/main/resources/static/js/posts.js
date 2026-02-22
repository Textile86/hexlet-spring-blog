
// Загрузка постов
async function loadPosts() {
    try {
        const tagFilter = document.getElementById('tag-filter')?.value || '';
        let url = `${API_URL}/posts?size=100`;

        const response = await fetch(url);
        if (!response.ok) throw new Error('Ошибка загрузки постов');

        const data = await response.json();
        const posts = data.content || [];

        // Фильтруем по тегу если выбран
        const filteredPosts = tagFilter
            ? posts.filter(post => post.tags?.some(tag => tag.id == tagFilter))
            : posts;

        displayPosts(filteredPosts);
    } catch (error) {
        document.getElementById('posts-container').innerHTML =
            `<div class="message error">Ошибка: ${error.message}</div>`;
    }
}

// Отображение постов
function displayPosts(posts) {
    const container = document.getElementById('posts-container');

    if (posts.length === 0) {
        container.innerHTML = '<p style="text-align: center; color: #999;">Постов пока нет</p>';
        return;
    }

    container.innerHTML = posts.map(post => `
        <div class="card">
            <h2>${escapeHtml(post.title)}</h2>
            <div class="card-meta">
                Опубликовано: ${formatDate(post.createdAt)}
                ${post.published ? '✓ Опубликовано' : '✗ Черновик'}
            </div>
            <div class="card-content">
                ${escapeHtml(post.content.substring(0, 200))}...
            </div>
            ${post.tags?.length ? `
                <div class="tags">
                    ${post.tags.map(tag => `<span class="tag">${escapeHtml(tag.name)}</span>`).join('')}
                </div>
            ` : ''}
            <div style="margin-top: 15px;">
                <a href="/post.html?id=${post.id}" class="btn">Читать</a>
                <a href="/create-post.html?id=${post.id}" class="btn btn-secondary">Редактировать</a>
                <button onclick="deletePost(${post.id})" class="btn btn-danger">Удалить</button>
            </div>
        </div>
    `).join('');
}

// Загрузка одного поста
async function loadPost(id) {
    try {
        const response = await fetch(`${API_URL}/posts/${id}`);
        if (!response.ok) throw new Error('Пост не найден');

        const post = await response.json();
        displaySinglePost(post);
    } catch (error) {
        document.getElementById('post-container').innerHTML =
            `<div class="message error">Ошибка: ${error.message}</div>`;
    }
}

// Отображение одного поста
function displaySinglePost(post) {
    document.getElementById('post-container').innerHTML = `
        <div class="card">
            <h1>${escapeHtml(post.title)}</h1>
            <div class="card-meta">
                Опубликовано: ${formatDate(post.createdAt)}
                ${post.updatedAt !== post.createdAt ? ` • Обновлено: ${formatDate(post.updatedAt)}` : ''}
            </div>
            ${post.tags?.length ? `
                <div class="tags">
                    ${post.tags.map(tag => `<span class="tag">${escapeHtml(tag.name)}</span>`).join('')}
                </div>
            ` : ''}
            <div class="card-content" style="margin-top: 20px; white-space: pre-wrap;">
                ${escapeHtml(post.content)}
            </div>
            <div style="margin-top: 30px;">
                <a href="/index.html" class="btn btn-secondary">← Назад к списку</a>
                <a href="/create-post.html?id=${post.id}" class="btn">Редактировать</a>
            </div>
        </div>
    `;
}

// Создание/редактирование поста
async function savePost(event) {
    event.preventDefault();

    const id = new URLSearchParams(window.location.search).get('id');
    const title = document.getElementById('title').value;
    const content = document.getElementById('content').value;
    const published = document.getElementById('published').checked;

    // Получаем выбранные теги
    const tagCheckboxes = document.querySelectorAll('input[name="tags"]:checked');
    const tagIds = Array.from(tagCheckboxes).map(cb => parseInt(cb.value));

    const postData = { title, content, published, tagIds };

    // Для создания нужен userId
    if (!id) {
        // Получаем текущего пользователя
        try {
            const usersResponse = await fetch(`${API_URL}/users`, {
                headers: getAuthHeaders()
            });
            const users = await usersResponse.json();
            const currentEmail = localStorage.getItem('email');
            const currentUser = users.find(u => u.email === currentEmail);

            if (currentUser) {
                postData.userId = currentUser.id;
            }
        } catch (error) {
            console.error('Ошибка получения пользователя:', error);
        }
    }

    try {
        const url = id ? `${API_URL}/posts/${id}` : `${API_URL}/posts`;
        const method = id ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method,
            headers: getAuthHeaders(),
            body: JSON.stringify(postData)
        });

        if (!response.ok) throw new Error('Ошибка сохранения поста');

        window.location.href = '/index.html';
    } catch (error) {
        alert('Ошибка: ' + error.message);
    }
}

// Загрузка поста для редактирования
async function loadPostForEdit(id) {
    try {
        const response = await fetch(`${API_URL}/posts/${id}`);
        if (!response.ok) throw new Error('Пост не найден');

        const post = await response.json();

        document.getElementById('title').value = post.title;
        document.getElementById('content').value = post.content;
        document.getElementById('published').checked = post.published;

        // Отмечаем теги
        post.tags?.forEach(tag => {
            const checkbox = document.querySelector(`input[name="tags"][value="${tag.id}"]`);
            if (checkbox) checkbox.checked = true;
        });

        document.querySelector('h2').textContent = 'Редактирование поста';
    } catch (error) {
        alert('Ошибка загрузки поста: ' + error.message);
    }
}

// Удаление поста
async function deletePost(id) {
    if (!confirm('Удалить этот пост?')) return;

    try {
        const response = await fetch(`${API_URL}/posts/${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });

        if (!response.ok) throw new Error('Ошибка удаления');

        loadPosts();
    } catch (error) {
        alert('Ошибка: ' + error.message);
    }
}

// Загрузка тегов для фильтра
async function loadTagsForFilter() {
    try {
        const response = await fetch(`${API_URL}/tags`);
        const tags = await response.json();

        const select = document.getElementById('tag-filter');
        if (!select) return;

        tags.forEach(tag => {
            const option = document.createElement('option');
            option.value = tag.id;
            option.textContent = tag.name;
            select.appendChild(option);
        });

        select.addEventListener('change', loadPosts);
    } catch (error) {
        console.error('Ошибка загрузки тегов:', error);
    }
}

// Загрузка тегов для формы поста
async function loadTagsForForm() {
    try {
        const response = await fetch(`${API_URL}/tags`);
        const tags = await response.json();

        const container = document.getElementById('tags-container');
        if (!container) return;

        container.innerHTML = tags.map(tag => `
            <label style="display: inline-block; margin-right: 15px;">
                <input type="checkbox" name="tags" value="${tag.id}">
                ${escapeHtml(tag.name)}
            </label>
        `).join('');
    } catch (error) {
        console.error('Ошибка загрузки тегов:', error);
    }
}

// Хелперы
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('ru-RU', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function getAuthHeaders() {
    const token = localStorage.getItem('token'); // или как вы его сохраняете
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}