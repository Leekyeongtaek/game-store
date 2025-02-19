document.addEventListener('DOMContentLoaded', function () {
    const pagination = document.querySelector('.pagination');
    if (pagination) {
        pagination.addEventListener('click', function (event) {
            event.preventDefault();

            const target = event.target;
            if (target.hasAttribute('data-page')) {
                const page = target.getAttribute('data-page');
                if (page !== null) {
                    fetchDataWithFilters(page);
                }
            }
        });
    }
});

function handleResponse(response, contentSelector) {
    const parser = new DOMParser();
    const doc = parser.parseFromString(response, 'text/html');
    const selector = doc.querySelector(contentSelector);
    const hasData = selector && selector.children.length > 0;

    if (hasData) {
        updateContent(doc, contentSelector);
        updateContent(doc, '.pagination');
    }

    updateGameListDisplay(hasData, contentSelector);
}

function updateContent(doc, selector) {
    const newContent = doc.querySelector(selector);
    const existingContent = document.querySelector(selector);
    existingContent.innerHTML = newContent.innerHTML;
}

function updateGameListDisplay(hasData, contentSelector) {
    if (hasData) {
        document.querySelector('.no-data').style.display = 'none';
        document.querySelector(contentSelector).style.display = 'flex';
        document.querySelector('.pagination').style.display = 'flex';
    } else {
        document.querySelector('.no-data').style.display = 'block';
        document.querySelector(contentSelector).style.display = 'none';
        document.querySelector('.pagination').style.display = 'none';
    }
}

function checkNoData(contentSelector) {
    console.log('checkNoData 실행...');
    const contentElement = document.querySelector(contentSelector);

    if (!contentElement) {
        return;
    }

    if (contentElement.children.length === 0) {
        document.querySelector('.no-data').style.display = 'block';
        document.querySelector('.pagination').style.display = 'none';
    }
}