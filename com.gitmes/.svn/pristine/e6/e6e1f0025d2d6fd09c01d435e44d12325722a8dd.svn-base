// 중복 품목 허용 방식으로 변경된 코드
// 선택된 품목 ID를 추적하는 Set 객체는 더 이상 중복 체크용으로 사용하지 않음
let selectedProductionDate = new Date().toISOString().split("T")[0]; // 기본값

// 날짜 선택 시 변경
document.addEventListener("DOMContentLoaded", () => {
    const dateInput = document.getElementById("productionDate");
    if (dateInput) {
        selectedProductionDate = dateInput.value;
    }
});

// 모달 열기
function openModal() {
    // 신규 등록이므로 선택된 지시서 ID 초기화
    selectedProductionDate = new Date().toISOString().split("T")[0];
    
    document.getElementById("productionOrderForm").reset();
    document.getElementById("itemInputList").innerHTML = "";
    document.getElementById("modalProductionDate").value = selectedProductionDate;
    
    // 신규 등록을 위한 폼 액션 설정
    document.getElementById("productionOrderForm").action = "/m11/s01/insert";
    
    // 모달 제목 변경
    document.querySelector("#productionOrderModal h2").textContent = "생산지시서 등록";
    
    addItemRow();
    document.getElementById("productionOrderModal").classList.remove("hidden");
}

function closeModal() {
    document.getElementById("productionOrderModal").classList.add("hidden");
}

let itemIndex = 0;

function addItemRow() {
    const container = document.getElementById("itemInputList");

    const row = document.createElement("div");
    row.className = "flex gap-2 mb-2 items-center";

    // 품목 선택
    row.innerHTML = `
        <select name="items[${itemIndex}].id" class="border p-2 rounded w-1/3" required onchange="updateItemDetails(this, ${itemIndex})">
            <option value="">품목 선택</option>
            ${createItemOptions()}
        </select>
        <input type="number" name="items[${itemIndex}].quantity" class="border p-2 rounded w-1/4" placeholder="수량" min="1" step="1" required/>
        <input type="text" name="items[${itemIndex}].unit" class="border p-2 rounded w-1/4" placeholder="단위" readonly/>
        <input type="text" name="items[${itemIndex}].memo" class="border p-2 rounded w-1/4" placeholder="비고"/>
        <button type="button" class="text-red-500" onclick="removeItemRow(this, ${itemIndex})">✕</button>
    `;

    container.appendChild(row);
    itemIndex++;
}

// 품목 선택 시 단위 등 정보만 업데이트 (중복 체크 제거)
function updateItemDetails(selectElement, index) {
    const selectedItemId = selectElement.value;
    
    // 빈 값 선택은 건너뜀
    if (!selectedItemId) return;
    
    // 단위 업데이트 로직 실행
    updateUnit(selectElement, index);
}

function createItemOptions(selectedItemId = null) {
    return items.map(item => {
        const selected = item.id == selectedItemId ? 'selected' : '';
        return `<option value="${item.id}" data-unit="${item.unit}" ${selected}>${item.name}</option>`;
    }).join('');
}

function updateUnit(selectElement, index) {
    const selectedOption = selectElement.options[selectElement.selectedIndex];
    const unit = selectedOption.getAttribute('data-unit');
    const unitInput = document.querySelector(`input[name="items[${index}].unit"]`);
    unitInput.value = unit;
}

function removeItemRow(button, index) {
    const row = button.parentElement;
    row.remove();
}

function loadProductionItems(productionDate) {
    selectedProductionDate = productionDate; // 선택된 지시서 날짜 저장 
    
    fetch('/m11/s01/' + selectedProductionDate + '/items')
        .then(response => response.json())
        .then(data => {
            let tableBody = document.getElementById("production-items-table-body");
            tableBody.innerHTML = "";

            data.forEach(production => {                
                let row = document.createElement("tr");
                row.innerHTML = `
                    <td class="border p-2">${production.itemName}</td>
                    <td class="border p-2">${production.quantity}</td>
                    <td class="border p-2">${production.itemUnit}</td>
                    <td class="border p-2">${production.productionStage}</td>
                    <td class="border p-2">${production.memo || ''}</td>
                `;
                tableBody.appendChild(row);
            });
            
            // 품목 수정 버튼 활성화
            document.getElementById("orderDetailTitle").textContent = "생산 품목 목록 (" + selectedProductionDate + ")";
        })
        .catch(error => {
            console.error('Error loading production items:', error);
        });
}

function openEditModal() {
    if (!selectedProductionDate) {
        alert("수정할 생산지시서를 먼저 선택해주세요.");
        return;
    }
    
    // 선택된 지시서의 데이터를 가져와서 모달에 채우기
    fetch('/m11/s01/' + selectedProductionDate + '/items')
        .then(response => response.json())
        .then(data => {
            document.getElementById("productionOrderForm").reset();
            document.getElementById("itemInputList").innerHTML = "";

            // 수정 모드를 위한 폼 액션 변경 (update 엔드포인트로)
            document.getElementById("productionOrderForm").action = "/m11/s01/update/" + selectedProductionDate;

            // 모달 제목 변경
            document.querySelector("#productionOrderModal h2").textContent = "생산지시서 수정";

            // 모달 표시
            document.getElementById("productionOrderModal").classList.remove("hidden");
             
            document.getElementById("modalProductionDate").value = selectedProductionDate;
                
            // 품목 데이터 추가
            if (data.length > 0) {
                data.forEach(item => {
                    addItemRowWithData(item);
                });
            } else {
                addItemRow(); // 빈 행 추가
            }

        })
        .catch(error => {
            console.error('Error loading production items for edit:', error);
        });
}

// 기존 데이터로 품목 행 추가하는 함수
function addItemRowWithData(itemData) {
    const container = document.getElementById("itemInputList");

    const row = document.createElement("div");
    row.className = "flex gap-2 mb-2 items-center";

    row.innerHTML = `
        <select name="items[${itemIndex}].id" class="border p-2 rounded w-1/3" required onchange="updateItemDetails(this, ${itemIndex})">
            <option value="">품목 선택</option>
            ${createItemOptions(itemData.itemId)}
        </select>
        <input type="number" name="items[${itemIndex}].quantity" class="border p-2 rounded w-1/4" placeholder="수량" min="1" step="1" required value="${itemData.quantity}"/>
        <input type="text" name="items[${itemIndex}].unit" class="border p-2 rounded w-1/4" placeholder="단위" readonly value="${itemData.itemUnit}"/>
        <input type="text" name="items[${itemIndex}].memo" class="border p-2 rounded w-1/4" placeholder="비고" value="${itemData.memo || ''}"/>
        <button type="button" class="text-red-500" onclick="removeItemRow(this, ${itemIndex})">✕</button>
    `;

    container.appendChild(row);
    itemIndex++;
}

function deleteProductionOrder(button) {
    if (confirm("정말 삭제하시겠습니까?")) {
        fetch(`/m11/s01/delete/${button.dataset.productiondate}`)
        .then(() => location.reload());
    }
}

// 폼 제출 전 유효성 검사 - 중복 체크 제거
document.getElementById("productionOrderForm").addEventListener("submit", function(event) {
    const selects = document.querySelectorAll("#itemInputList select");
    const emptySelects = Array.from(selects).filter(select => select.value === "");
    
    // 빈 선택이 있는지 확인
    if (emptySelects.length > 0) {
        event.preventDefault();
        alert("모든 품목을 선택해주세요.");
        return false;
    }
    
    // 중복 체크는 제거됨 - 이제 중복 품목 허용
    
    return true;
});