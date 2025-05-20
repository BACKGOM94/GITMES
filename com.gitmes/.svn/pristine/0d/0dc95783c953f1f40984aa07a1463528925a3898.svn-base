// /js/m31s02.js 파일 수정

// 선택된 수주 ID 저장용
let selectedOrderId = null;
let selectedOrderItems = [];

function loadOrderItems(orderId) {
    selectedOrderId = orderId;

    fetch('/m31/s01/' + orderId + '/items')
        .then(res => res.json())
        .then(data => {
            selectedOrderItems = data;
            renderOrderItems(data);
        })
        .catch(err => console.error('품목 로딩 실패:', err));
}

function renderOrderItems(items) {
    const tbody = document.getElementById('order-items-table-body');
    const totalEl = document.getElementById('orderTotalSum');
    tbody.innerHTML = '';
    let totalSum = 0;

    items.forEach(item => {
        const row = document.createElement('tr');
        const itemTotal = item.quantity * item.unitPrice;
        totalSum += itemTotal;
		
        row.innerHTML = `
            <td class="p-2 border">${item.itemName}</td>
            <td class="p-2 border">${item.quantity}</td>
            <td class="p-2 border">${item.itemUnit}</td>
            <td class="p-2 border">₩ ${item.unitPrice.toLocaleString()}</td>
            <td class="p-2 border">₩ ${itemTotal.toLocaleString()}</td>
        `;

        tbody.appendChild(row);
    });

    totalEl.textContent = `₩ ${totalSum.toLocaleString()}원`;
}

function openEditModal() {
    if (!selectedOrderId || selectedOrderItems.length === 0) {
        alert('먼저 수주서를 선택해주세요.');
        return;
    }

    document.getElementById('receiptId').value = selectedOrderId;
    const listContainer = document.getElementById('receiptMaterialList');
    listContainer.innerHTML = '';

    const table = document.createElement('table');
    table.className = 'w-full border border-gray-300 text-center mb-4';

    table.innerHTML = `
        <thead class="bg-gray-100">
            <tr>
                <th class="p-2 border">품목명</th>
                <th class="p-2 border">재고 수량</th>
				<th class="p-2 border">수주 수량</th>
                <th class="p-2 border">단위</th>
                <th class="p-2 border">출고 수량</th>
                <th class="p-2 border">출고 금액 (₩)</th>
            </tr>
        </thead>
        <tbody id="receipt-table-body"></tbody>
    `;

    const tbody = table.querySelector('#receipt-table-body');

	selectedOrderItems.forEach(async item => {		
	    const tr = document.createElement('tr');
	    const rowId = `item_${item.itemId}`;
	    const response = await fetch(`/m31/s02/${selectedOrderId}/${item.itemId}`);
	    const receiptData = await response.json();				
		
	    const quantity = receiptData.receiptQuantity ?? item.quantity;
	    const unitPrice = receiptData.receiptPrice ?? item.unitPrice;
	    const itemTotal = quantity * unitPrice;
        const inventoryQty = receiptData.inventory;

	    tr.innerHTML = `
	        <input type="hidden" name="items[${item.itemId}].itemId" value="${item.itemId}" />
	        <td class="p-2 border">${item.itemName}</td>
	        <td class="p-2 border inventory-qty" data-inventory="${inventoryQty}">${inventoryQty}</td>
			<td class="p-2 border">${item.quantity}</td>
	        <td class="p-2 border">${item.itemUnit}</td>
	        <td class="p-2 border">
	            <input type="number" min="0" name="items[${item.itemId}].receiptQuantity"
	                   value="${quantity}" class="border p-1 w-full rounded text-right outbound-qty"
	                   data-item-id="${item.itemId}"
	                   oninput="updateAmount('${rowId}', ${unitPrice})" />
	        </td>
	        <td class="p-2 border" id="${rowId}_amount">₩ ${itemTotal.toLocaleString()}</td>
	    `;

	    tbody.appendChild(tr);
	});

    listContainer.appendChild(table);

    // 폼 제출 이벤트 리스너 등록
    const form = document.getElementById('receiptForm');
    form.onsubmit = validateInventory;

    document.getElementById('receiptModal').classList.remove('hidden');
}

// 재고 수량 검증 함수
function validateInventory(event) {
    const outboundInputs = document.querySelectorAll('.outbound-qty');
    let isValid = true;
    let invalidItems = [];

    outboundInputs.forEach(input => {
        const itemId = input.getAttribute('data-item-id');
        const outboundQty = parseFloat(input.value || 0);
        
        // 해당 입력 필드와 연결된 재고 수량 찾기
        const inventoryCell = input.closest('tr').querySelector('.inventory-qty');
        const inventoryQty = parseFloat(inventoryCell.getAttribute('data-inventory') || 0);
        
        // 출고 수량이 재고 수량보다 많은지 검사
        if (outboundQty > inventoryQty) {
            isValid = false;
            input.classList.add('border-red-500');
            
            // 어떤 품목이 재고 초과인지 기록
            const itemName = input.closest('tr').querySelector('td:first-of-type').textContent;
            invalidItems.push({
                name: itemName,
                inventory: inventoryQty,
                outbound: outboundQty
            });
        } else {
            input.classList.remove('border-red-500');
        }
    });

    if (!isValid) {
        event.preventDefault(); // 폼 제출 방지
        
        // 에러 메시지 생성
        let errorMsg = '다음 품목의 출고 수량이 재고보다 많습니다:\n\n';
        invalidItems.forEach(item => {
            errorMsg += `- ${item.name}: 재고 ${item.inventory}개, 출고 ${item.outbound}개\n`;
        });
        errorMsg += '\n출고 수량을 조정해주세요.';
        
        alert(errorMsg);
        return false;
    }
    
    return true;
}

// 금액 자동 계산 함수
function updateAmount(rowId, unitPrice) {
    const input = document.querySelector(`[name="items[${rowId.split('_')[1]}].receiptQuantity"]`);
    const amountCell = document.getElementById(`${rowId}_amount`);

    const quantity = parseFloat(input.value || 0);
    const amount = quantity * unitPrice;

    amountCell.textContent = `₩ ${amount.toLocaleString()}`;
    
    // 입력값 변경 시에도 실시간으로 유효성 검사
    const inventoryCell = input.closest('tr').querySelector('.inventory-qty');
    const inventoryQty = parseFloat(inventoryCell.getAttribute('data-inventory') || 0);
    
    if (quantity > inventoryQty) {
        input.classList.add('border-red-500');
    } else {
        input.classList.remove('border-red-500');
    }
}

function closeModal() {
    document.getElementById('receiptModal').classList.add('hidden');
    
    // 모달 닫을 때 오류 표시 초기화
    const errorInputs = document.querySelectorAll('.border-red-500');
    errorInputs.forEach(input => {
        input.classList.remove('border-red-500');
    });
}