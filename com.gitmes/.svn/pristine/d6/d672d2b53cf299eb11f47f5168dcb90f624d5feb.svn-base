// 선택된 품목 ID를 추적하는 Set 객체 추가
let selectedItemIds = new Set();

let selectedOrderDate = new Date().toISOString().split("T")[0]; // 기본값
let selectedOrderId = null;
// 날짜 선택 시 변경
document.addEventListener("DOMContentLoaded", () => {
    const dateInput = document.getElementById("orderDate");
    selectedOrderDate = dateInput.value; // 서버에서 받은 selectedDate를 그대로 사용
});

// 모달 열기
function openModal() {
    // 신규 등록이므로 선택된 주문 ID 초기화
    selectedOrderId = null;
    
    // 선택된 품목 ID 초기화
    selectedItemIds.clear();
    
    document.getElementById("orderForm").reset();
    document.getElementById("itemInputList").innerHTML = "";
    document.getElementById("modalOrderDate").value = selectedOrderDate;
    
    // 신규 등록을 위한 폼 액션 설정
    document.getElementById("orderForm").action = "/m01/s01/insert";
    
    // 모달 제목 변경
    document.querySelector("#orderModal h2").textContent = "발주서 등록";
    
    addItemRow();
    document.getElementById("orderModal").classList.remove("hidden");
}

function closeModal() {
    document.getElementById("orderModal").classList.add("hidden");
}

let itemIndex = 0;

function addItemRow() {
    const container = document.getElementById("itemInputList");

    const row = document.createElement("div");
    row.className = "flex gap-2 mb-2 items-center";

    // 품목 선택시 자동으로 단가와 단위 입력
    row.innerHTML = `
        <select name="items[${itemIndex}].id" class="border p-2 rounded w-1/3" required onchange="checkDuplicateAndUpdate(this, ${itemIndex})">
            <option value="">품목 선택</option>
            ${createItemOptions()}
        </select>
        <input type="number" name="items[${itemIndex}].quantity" class="border p-2 rounded w-1/4" placeholder="수량" min="1" step="1" required onchange="updateAmount(${itemIndex})"/>
        <input type="text" name="items[${itemIndex}].unit" class="border p-2 rounded w-1/4" placeholder="단위" readonly/>
        <input type="number" name="items[${itemIndex}].price" class="border p-2 rounded w-1/4" placeholder="단가" min="0" step="0.01" readonly onchange="updateAmount(${itemIndex})"/>
        <button type="button" class="text-red-500" onclick="removeItemRow(this, ${itemIndex})">✕</button>
    `;

    container.appendChild(row);
    itemIndex++;
}

// 품목 선택 시 중복 체크 후 업데이트
function checkDuplicateAndUpdate(selectElement, index) {
    const selectedItemId = selectElement.value;
    
    // 빈 값 선택은 건너뜀
    if (!selectedItemId) return;
    
    // 중복 체크
    if (selectedItemIds.has(selectedItemId)) {
        alert('이미 선택된 품목입니다. 다른 품목을 선택하세요.');
        selectElement.value = ''; // 선택 초기화
        return;
    }
    
    // 이전에 선택했던 항목이 있을 경우 제거
    const prevItemId = selectElement.getAttribute('data-selected-item');
    if (prevItemId) {
        selectedItemIds.delete(prevItemId);
    }
    
    // 현재 선택 항목 추가
    selectedItemIds.add(selectedItemId);
    selectElement.setAttribute('data-selected-item', selectedItemId);
    
    // 기존 단가와 단위 업데이트 로직 실행
    updateUnitAndPrice(selectElement, index);
}

function createItemOptions(selectedItemId = null) {
    return items.map(item => {
        const selected = item.id == selectedItemId ? 'selected' : '';
        return `<option value="${item.id}" data-price="${item.price}" data-unit="${item.unit}" ${selected}>${item.name}</option>`;
    }).join('');
}

function updateUnitAndPrice(selectElement, index) {
    const selectedOption = selectElement.options[selectElement.selectedIndex];
    const price = selectedOption.getAttribute('data-price');
    const unit = selectedOption.getAttribute('data-unit');

    const priceInput = document.querySelector(`input[name="items[${index}].price"]`);
    const unitInput = document.querySelector(`input[name="items[${index}].unit"]`);
    priceInput.value = price;
    unitInput.value = unit;

    priceInput.removeAttribute('readonly'); // 단가 수정 가능하도록
    updateAmount(index); // 자동 총금액 반영
}

function updateAmount(index) {
    const quantityInput = document.querySelector(`input[name="items[${index}].quantity"]`);
    const priceInput = document.querySelector(`input[name="items[${index}].price"]`);

    const quantity = parseFloat(quantityInput?.value || 0);
    const price = parseFloat(priceInput?.value || 0);

    if (!isNaN(quantity) && !isNaN(price)) {
        // 총금액 재계산
        updateTotalAmount();
    }
}

function updateTotalAmount() {
    let total = 0;
    for (let i = 0; i < itemIndex; i++) {
        const quantity = parseFloat(document.querySelector(`input[name="items[${i}].quantity"]`)?.value || 0);
        const price = parseFloat(document.querySelector(`input[name="items[${i}].price"]`)?.value || 0);
        if (!isNaN(quantity) && !isNaN(price)) {
            total += quantity * price;
        }
    }
    document.getElementById("modalTotalAmount").textContent = total.toLocaleString();
}

// 행 제거 시 선택된 품목 ID도 제거
function removeItemRow(button, index) {
    const row = button.parentElement;
    const select = row.querySelector(`select[name="items[${index}].id"]`);
    const selectedItemId = select.getAttribute('data-selected-item');
    
    if (selectedItemId) {
        selectedItemIds.delete(selectedItemId);
    }
    
    row.remove();
    updateTotalAmount();
}

function loadOrderItems(orderId, isComplete) {
    selectedOrderId = orderId; // 선택된 주문 ID 저장
    
    const editBtn = document.querySelector("button[onclick='openEditModal()']");
    editBtn.disabled = isComplete === 1;
    editBtn.classList.toggle('bg-gray-400', isComplete === 1);
    editBtn.classList.toggle('bg-green-500', isComplete !== 1);
    editBtn.innerText = isComplete === 1 ? '수정 불가 (완료됨)' : '+ 품목 수정';    
    
    fetch('/m01/s01/' + orderId + '/items')
        .then(response => response.json())
        .then(data => {
            let tableBody = document.getElementById("order-items-table-body");
            tableBody.innerHTML = "";

            let totalSum = 0;

            data.forEach(item => {
                const unitPrice = parseFloat(item.unitPrice || 0);
                const quantity = parseFloat(item.quantity || 0);
                const totalPrice = parseFloat(item.totalPrice || (unitPrice * quantity));
                totalSum += totalPrice;

                const formattedUnitPrice = unitPrice.toLocaleString() + '원';
                const formattedTotalPrice = totalPrice.toLocaleString() + '원';
                let row = document.createElement("tr");
                row.innerHTML = `
                    <td class="border p-2">${item.itemName}</td>
                    <td class="border p-2">${item.quantity}</td>
                    <td class="border p-2">${item.itemUnit}</td>
                    <td class="border p-2">${formattedUnitPrice}</td>
                    <td class="border p-2">${formattedTotalPrice}</td>
                `;
                tableBody.appendChild(row);
            });

            // 합계 갱신
            document.getElementById("orderTotalSum").textContent = "₩ " + totalSum.toLocaleString() + "원";
            
            // 품목 수정 버튼 활성화
            document.getElementById("orderDetailTitle").textContent = "발주 품목 목록 (주문 ID: " + orderId + ")";
        })
        .catch(error => {
            console.error('Error loading order items:', error);
        });
}

function openEditModal() {
    if (!selectedOrderId) {
        alert("수정할 발주서를 먼저 선택해주세요.");
        return;
    }
    
    // 선택된 품목 ID 초기화
    selectedItemIds.clear();
    
    // 선택된 주문의 데이터를 가져와서 모달에 채우기
    fetch('/m01/s01/' + selectedOrderId + '/items')
        .then(response => response.json())
        .then(data => {
            document.getElementById("orderForm").reset();
            document.getElementById("itemInputList").innerHTML = "";
            
            // 발주서 정보 설정 (API에서 거래처 ID와 발주일 정보도 가져온다고 가정)
            fetch('/m01/s01/' + selectedOrderId)
                .then(response => response.json())
                .then(orderData => {
                    // 모달의 클라이언트 선택 및 발주일 설정
                    document.querySelector('select[name="clientId"]').value = orderData.client.id;
                    document.getElementById("modalOrderDate").value = document.getElementById("orderDate").value;
                    
                    // 품목 데이터 추가
                    if (data.length > 0) {
                        data.forEach(item => {
                            addItemRowWithData(item);
                        });
                    } else {
                        addItemRow(); // 빈 행 추가
                    }
                    
                    // 수정 모드를 위한 폼 액션 변경 (update 엔드포인트로)
                    document.getElementById("orderForm").action = "/m01/s01/update/" + selectedOrderId;
                    
                    // 모달 제목 변경
                    document.querySelector("#orderModal h2").textContent = "발주서 수정";
                    
                    // 모달 표시
                    document.getElementById("orderModal").classList.remove("hidden");
                    
                    // 총액 업데이트
                    updateTotalAmount();
                });
        })
        .catch(error => {
            console.error('Error loading order items for edit:', error);
        });
}

// 기존 데이터로 품목 행 추가하는 함수
function addItemRowWithData(itemData) {
    const container = document.getElementById("itemInputList");

    const row = document.createElement("div");
    row.className = "flex gap-2 mb-2 items-center";
    
    // 이미 선택된 품목 추가
    if (itemData.itemId) {
        selectedItemIds.add(String(itemData.itemId));
    }

    // 이제 itemId를 직접 사용할 수 있습니다
    row.innerHTML = `
        <select name="items[${itemIndex}].id" class="border p-2 rounded w-1/3" required onchange="checkDuplicateAndUpdate(this, ${itemIndex})" data-selected-item="${itemData.itemId}">
            <option value="">품목 선택</option>
            ${createItemOptions(itemData.itemId)}
        </select>
        <input type="number" name="items[${itemIndex}].quantity" class="border p-2 rounded w-1/4" placeholder="수량" min="1" step="1" required value="${itemData.quantity}" onchange="updateAmount(${itemIndex})"/>
        <input type="text" name="items[${itemIndex}].unit" class="border p-2 rounded w-1/4" placeholder="단위" readonly value="${itemData.itemUnit}"/>
        <input type="number" name="items[${itemIndex}].price" class="border p-2 rounded w-1/4" placeholder="단가" min="0" step="0.01" value="${itemData.unitPrice}" onchange="updateAmount(${itemIndex})"/>
        <button type="button" class="text-red-500" onclick="removeItemRow(this, ${itemIndex})">✕</button>
    `;

    container.appendChild(row);
    itemIndex++;
    
    // 총액 업데이트
    updateAmount(itemIndex - 1);
}

function deleteOrder(button) {
    if (confirm("정말 삭제하시겠습니까?")) {
        fetch(`/m01/s01/delete/${button.dataset.orderid}`)
        .then(() => location.reload());
    }
}

// 폼 제출 전 마지막 검증
document.getElementById("orderForm").addEventListener("submit", function(event) {
    const selects = document.querySelectorAll("#itemInputList select");
    const selectedValues = Array.from(selects).map(select => select.value).filter(val => val !== "");
    
    // 중복된 값이 있는지 확인
    const uniqueValues = new Set(selectedValues);
    if (uniqueValues.size !== selectedValues.length) {
        event.preventDefault();
        alert("중복된 품목이 있습니다. 다시 확인해주세요.");
        return false;
    }
    
    return true;
});