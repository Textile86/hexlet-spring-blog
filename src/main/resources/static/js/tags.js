
// Загрузка тегов
async function loadTags() {
    try {
        const response = await fetch(`${API_URL}/tags`);
        if (!response.ok) throw new Error('Ошибка загрузки тегов');

        const tags = await response.json();
        displayTags(tags);
    } catch (error) {
        document.getElementById('tags-container').innerHTML =
            `<div class="message error">Ошибка: ${error.message}</div>`;
    }
}

// Отображение тегов
function displayTags(tags) {
    const container = document.getElementById('tags-container');

    if (tags.length === 0) {
        container.innerHTML = '<p style="text-align: center; color: #999;">Тегов пока нет</p>';
        return;
    }

    container.innerHTML = tags.map(tag => `
        <div class="card" style="display: flex; justify-content: space-between; align-items: center;">
            <span class="tag" style="font-size: 16px; padding: 8px 16px;">${escapeHtml(tag.name)}</span>
            <button onclick="deleteTag(${tag.id})" class="btn btn-danger">Удалить</button>
        </div>
    `).join('');
}

// Создание тега
async function createTag(event) {
    event.preventDefault();

    const name = document.getElementById('tag-name').value;

    try {
        const response = await fetch(`${API_URL}/tags`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify({ name })
        });

        if (!response.ok) throw new Error('Ошибка создания тега');

        document.getElementById('tag-name').value = '';
        loadTags();
    } catch (error) {
        alert('Ошибка: ' + error.message);
    }
}

// Удаление тега
async function deleteTag(id) {
    if (!confirm('Удалить этот тег?')) return;

    try {
        const response = await fetch(`${API_URL}/tags/${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });

        if (!response.ok) throw new Error('Ошибка удаления');

        loadTags();
    } catch (error) {
        alert('Ошибка: ' + error.message);
    }
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}