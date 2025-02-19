function previewImage(event, previewContainerId) {
    const fileInput = event.target;
    const file = fileInput.files[0];
    const previewContainer = document.getElementById(previewContainerId);

    previewContainer.innerHTML = '';

    if (file && file.type.startsWith('image/')) {
        const reader = new FileReader();
        reader.onload = (e) => {
            const img = document.createElement('img');
            img.src = e.target.result;
            previewContainer.appendChild(img);
        };
        reader.readAsDataURL(file);
    } else {
        alert('이미지 타입만 업로드가 가능합니다.');
        previewContainer.innerHTML = '<div class="image-preview-placeholder">유효한 이미지를 선택하세요</div>';
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const discountRadios = document.querySelectorAll('input[name="isDiscounted"]');
    const discountDetails = document.getElementById("discountDetails");

    discountRadios.forEach(radio => {
        radio.addEventListener("change", function () {
            if (this.value === "true") {
                discountDetails.style.display = "block";
            } else {
                discountDetails.style.display = "none";
            }
        });
    });

    // 초기 상태 체크
    const selectedRadio = document.querySelector('input[name="isDiscounted"]:checked');
    if (selectedRadio && selectedRadio.value === "true") {
        discountDetails.style.display = "block";
    } else {
        discountDetails.style.display = "none";
    }
});

function calculateDiscountPrice() {
    const priceInput = document.getElementById('price');
    const discountRateInput = document.getElementById('discountRate');
    const discountPriceInput = document.getElementById('discountPrice');

    const price = parseFloat(priceInput.value) || 0;
    const discountRate = parseFloat(discountRateInput.value) || 0;

    if (discountRate >= 0 && discountRate <= 100) {
        const discountPrice = price * (1 - discountRate / 100);
        discountPriceInput.value = discountPrice.toFixed(0);
    } else {
        discountPriceInput.value = 0;
    }
}