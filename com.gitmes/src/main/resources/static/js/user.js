function openUserModal() {
    document.getElementById('userModal').classList.remove('hidden');
    document.getElementById('modalTitle').textContent = "사용자 추가";
    document.getElementById('userForm').reset();
    document.getElementById('userId').readOnly = false; // ID 필드를 수정 가능하게 설정
    
    // 피드백 초기화
    const feedbackElement = document.getElementById('usernameFeedback');
    if (feedbackElement) {
        feedbackElement.textContent = '';
        feedbackElement.classList.remove('text-red-600', 'text-green-600');
    }
}

function editUserModal(button) {
    document.getElementById('userModal').classList.remove('hidden');
    document.getElementById('modalTitle').textContent = "사용자 수정";

    document.getElementById('userId').value = button.dataset.id || ''; 
    document.getElementById('userName').value = button.dataset.name || ''; 
    document.getElementById('userUsername').value = button.dataset.username || ''; 
    document.getElementById('userPhone').value = button.dataset.phone || ''; 
    document.getElementById('userAddress').value = button.dataset.address || ''; 
    document.getElementById('userEnabled').checked = button.dataset.enabled === "true"; 

    document.getElementById('userId').readOnly = true; // 수정 시 ID 필드 비활성화
    
    // 피드백 초기화
    const feedbackElement = document.getElementById('usernameFeedback');
    if (feedbackElement) {
        feedbackElement.textContent = '';
        feedbackElement.classList.remove('text-red-600', 'text-green-600');
    }
}

function closeUserModal() {
    document.getElementById('userModal').classList.add('hidden');
}

function checkUserId() {
    const userId = document.getElementById('userUsername').value;
    if (!userId) return true; // ID가 비어 있으면 제출 진행

    if (document.getElementById('userId').readOnly) return true; // 수정 모드에서는 ID 확인 안 함

    // ID 중복 확인 요청
    return new Promise((resolve) => {
        fetch(`/m98/s01/duplicate/${userId}`)
        .then(response => response.json())
        .then(data => {
            if (data.exists) {
                // 중복일 경우 처리
                const feedbackElement = document.getElementById('usernameFeedback');
                if (feedbackElement) {
                    feedbackElement.textContent = "이미 사용 중인 아이디입니다.";
                    feedbackElement.classList.add('text-red-600');
                    feedbackElement.classList.remove('text-green-600');
                }
                resolve(false); // 제출 취소
            } else {
                // 중복 아닐 경우 처리
                const feedbackElement = document.getElementById('usernameFeedback');
                if (feedbackElement) {
                    feedbackElement.textContent = "사용 가능한 아이디입니다.";
                    feedbackElement.classList.add('text-green-600');
                    feedbackElement.classList.remove('text-red-600');
                }
                resolve(true); // 제출 진행
            }
        })
        .catch(error => {
            console.error('중복 확인 중 오류:', error);
            resolve(true);
        });
    });
}

function checkUsernameDuplicate(username) {
    if (!username) {
        return; // ID가 비어 있으면 중복 체크 안 함
    }

    fetch(`/m98/s01/duplicate/${username}`)
    .then(response => response.json())
    .then(data => {
        const feedbackElement = document.getElementById('usernameFeedback');
        const userIdInput = document.getElementById('userUsername');

        if (data) {
            feedbackElement.textContent = "이미 사용 중인 아이디입니다.";
            feedbackElement.classList.add('text-red-600');
            feedbackElement.classList.remove('text-green-600');
            userIdInput.setCustomValidity('이미 사용 중인 아이디입니다.'); // 폼 제출 방지
        } else {
            feedbackElement.textContent = "사용 가능한 아이디입니다.";
            feedbackElement.classList.add('text-green-600');
            feedbackElement.classList.remove('text-red-600');
            userIdInput.setCustomValidity(''); // 폼 제출 허용
        }
    })
    .catch(err => {
        console.error('중복 확인 오류:', err);
    });
}

function deleteUser(button) {
    if (confirm("정말 삭제하시겠습니까?")) {
        fetch(`/m98/s01/delete/${button.dataset.username}`)
        .then(() => location.reload());
    }
}

function searchAddress() {
    new daum.Postcode({
        oncomplete: function(data) {
            document.getElementById('userAddress').value = data.address;
            document.getElementById('userAddressDetail').focus(); // 상세 주소 입력으로 포커스 이동
        }
    }).open();
}

// DOM 로드 시 이벤트 리스너 추가
document.addEventListener('DOMContentLoaded', function() {
    const usernameInput = document.getElementById('userUsername');
    if (usernameInput) {
        usernameInput.addEventListener('blur', function() {
            checkUsernameDuplicate(this.value);
        });
    }
});