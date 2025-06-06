function openPermissionModal(button) {
    const userId = button.getAttribute("data-id");
    const userName = button.getAttribute("data-name");
    
    document.getElementById("modalUserId").value = userId;
    document.getElementById("modalUserName").value = userName;

    // 기존 권한 불러오기 (Ajax 요청)
    fetch(`/m98/s02/user-permissions/${userId}`)
        .then(response => response.json())
        .then(data => {
            document.querySelectorAll("#permissionList input[type='checkbox']").forEach(checkbox => {
				const menuId = parseInt(checkbox.value); // 체크박스의 value (메뉴 ID)
				const permission = data.find(item => item.id === menuId); // 해당 메뉴 ID를 가진 데이터 찾기
				checkbox.checked = permission ? permission.granted : false; // granted 값이 true면 체크
            });
        });
    
    document.getElementById("permissionModal").classList.remove("hidden");
}

function closePermissionModal() {
    document.getElementById("permissionModal").classList.add("hidden");
}

// 권한 변경 폼 제출
document.getElementById("permissionForm").addEventListener("submit", function (event) {
    event.preventDefault();
    
    const userId = document.getElementById("modalUserId").value;
    const checkedPermissions = Array.from(document.querySelectorAll("#permissionList input:checked"))
        .map(checkbox => checkbox.value);
		fetch(`/m98/s02/user-permissions/update?userId=${userId}&${checkedPermissions.map(p => `permissions=${p}`).join("&")}`, {
		    method: "POST",
		    headers: {
		        "Content-Type": "application/x-www-form-urlencoded"
		    }
		}).then(response => {
        if (response.ok) {
            alert("권한이 저장되었습니다.");
            closePermissionModal();
        } else {
            alert("저장 중 오류가 발생했습니다.");
        }
    });
});