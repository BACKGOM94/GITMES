// /js/m01s02.js

// 선택된 발주 ID 저장용
let selectedOrderId = null;
let selectedOrderItems = [];

function loadOrderItems(orderId) {
    selectedOrderId = orderId;

    fetch('/m01/s01/' + orderId + '/items')
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
        alert('먼저 발주서를 선택해주세요.');
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
                <th class="p-2 border">발주 수량</th>
                <th class="p-2 border">단위</th>
                <th class="p-2 border">입고 수량</th>
                <th class="p-2 border">입고 금액 (₩)</th>
            </tr>
        </thead>
        <tbody id="receipt-table-body"></tbody>
    `;

    const tbody = table.querySelector('#receipt-table-body');

	selectedOrderItems.forEach(async item => {
	    const tr = document.createElement('tr');
	    const rowId = `item_${item.itemId}`;
	    const response = await fetch(`/m01/s02/${selectedOrderId}/${item.itemId}`);
	    const receiptData = await response.json();				
		
	    const quantity = receiptData.receiptQuantity ?? item.quantity;
	    const unitPrice = receiptData.receiptPrice ?? item.unitPrice;
	    const itemTotal = quantity * unitPrice;

	    tr.innerHTML = `
	        <input type="hidden" name="items[${item.itemId}].itemId" value="${item.itemId}" />
	        <td class="p-2 border">${item.itemName}</td>
	        <td class="p-2 border">${item.quantity}</td>
	        <td class="p-2 border">${item.itemUnit}</td>
	        <td class="p-2 border">
	            <input type="number" min="0" name="items[${item.itemId}].receiptQuantity"
	                   value="${quantity}" class="border p-1 w-full rounded text-right"
	                   oninput="updateAmount('${rowId}', ${unitPrice})" />
	        </td>
	        <td class="p-2 border" id="${rowId}_amount">₩ ${itemTotal.toLocaleString()}</td>
	    `;

	    tbody.appendChild(tr);
	});

    listContainer.appendChild(table);

    document.getElementById('receiptModal').classList.remove('hidden');
}

// 금액 자동 계산 함수
function updateAmount(rowId, unitPrice) {
    const input = document.querySelector(`[name="items[${rowId.split('_')[1]}].receiptQuantity"]`);
    const amountCell = document.getElementById(`${rowId}_amount`);

    const quantity = parseFloat(input.value || 0);
    const amount = quantity * unitPrice;

    amountCell.textContent = `₩ ${amount.toLocaleString()}`;
}

function closeModal() {
    document.getElementById('receiptModal').classList.add('hidden');
}
