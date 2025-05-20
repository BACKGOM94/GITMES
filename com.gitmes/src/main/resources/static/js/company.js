document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("companyForm"); // 폼 가져오기
    const addressInput = document.getElementById("address");
    const detailAddressInput = document.getElementById("detailAddress");
    const searchAddressButton = document.getElementById("searchAddress");

    /** 📌 Daum 주소 API 연동 */
    searchAddressButton.addEventListener("click", function () {
        new daum.Postcode({
            oncomplete: function (data) {
                let fullAddress = data.address;
                let extraAddress = '';

                if (data.addressType === 'R') {
                    if (data.bname) extraAddress += data.bname;
                    if (data.buildingName) {
                        extraAddress += (extraAddress ? ', ' : '') + data.buildingName;
                    }
                    fullAddress += extraAddress ? ` (${extraAddress})` : '';
                }

                addressInput.value = fullAddress;
                detailAddressInput.value = '';
                detailAddressInput.focus();
            }
        }).open();
    });

    /** 📌 폼 유효성 검사 */
    form.addEventListener("submit", function (event) {
        const businessNumber = document.getElementById("businessNumber").value.trim();
        const managerUsername = document.getElementById("managerUsername").value.trim();
        const password = document.getElementById("managerPassword").value.trim();
        const address = addressInput.value.trim();
        const detailAddress = detailAddressInput.value.trim();

        // 🚨 사업자 등록번호 체크 (10자리 숫자)
        if (!/^\d{10}$/.test(businessNumber)) {
            alert("🚨 사업자 등록번호는 10자리 숫자로 입력해야 합니다.");
            event.preventDefault();
            return;
        }

        // 🚨 매니저 아이디 체크 (최소 4자리)
        if (managerUsername.length < 4) {
            alert("🚨 매니저 아이디는 최소 4자리 이상이어야 합니다.");
            event.preventDefault();
            return;
        }

        // 🚨 비밀번호 체크 (최소 6자리)
        if (password.length < 6) {
            alert("🚨 비밀번호는 최소 6자리 이상이어야 합니다.");
            event.preventDefault();
            return;
        }

        // 🚨 주소 필수 입력
        if (!address) {
            alert("🚨 주소를 입력하세요.");
            event.preventDefault();
            return;
        }

        // 🚨 상세 주소 필수 입력
        if (!detailAddress) {
            alert("🚨 상세 주소를 입력하세요.");
            event.preventDefault();
            return;
        }

        alert("✅ 회사 등록이 완료되었습니다!");
    });
});
