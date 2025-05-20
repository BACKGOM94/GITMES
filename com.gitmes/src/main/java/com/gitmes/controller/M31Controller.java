package com.gitmes.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gitmes.dto.OrderItemResponseDTO;
import com.gitmes.dto.SalesItemResponseDTO;
import com.gitmes.model.Client;
import com.gitmes.model.Company;
import com.gitmes.model.Inventory;
import com.gitmes.model.Item;
import com.gitmes.model.Order;
import com.gitmes.model.OrderData;
import com.gitmes.model.Sales;
import com.gitmes.model.SalesData;
import com.gitmes.model.User;
import com.gitmes.service.ClientService;
import com.gitmes.service.InventoryService;
import com.gitmes.service.ItemService;
import com.gitmes.service.SalesService;
import com.gitmes.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/m31")
public class M31Controller {

	private final UserService userService;
	private final ItemService itemService;
	private final ClientService clientService;
	private final SalesService salesService;
	private final InventoryService inventoryService;
	
	@GetMapping("/s01")
	public String move_s01(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			Model model, Principal principal) {
		
	    if (principal == null) return "redirect:/login";
	    User user = userService.getuserinfo(principal.getName());

	    LocalDate targetDate = (date != null) ? date : LocalDate.now();

	    List<Map<String, Object>> simpleSellable = new ArrayList<>();
	    for (Item item : itemService.SellableItemList(user.getCompany())) {
	        Map<String, Object> map = new HashMap<>();
	        map.put("id", item.getId());
	        map.put("name", item.getItemName());
	        map.put("unit", item.getUnit());
	        map.put("price", item.getPrice());
	        simpleSellable.add(map);
	    }

	    model.addAttribute("clients", clientService.SalesClientList(user.getCompany()));
	    model.addAttribute("salesList", salesService.getSalesByDate(user.getCompany(), targetDate));
	    model.addAttribute("items", simpleSellable);
	    model.addAttribute("selectedDate", targetDate);
	    
		return "user/m31/s01";
	}

    @PostMapping("/s01/insert")
    public String insertOrder(@RequestParam("clientId") Long clientId,
                              @RequestParam("orderDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate saleDate,
                              @RequestParam Map<String, String> paramMap,
                              Principal principal) {
        if (principal == null) return "redirect:/login";
        
        // 로그인 사용자 정보
        User user = userService.getuserinfo(principal.getName());
        Company company = user.getCompany();

        Sales sales = Sales.builder()
                .company(company)
                .client(clientService.getClientById(clientId))
                .saleDate(saleDate)
                .isComplete(2)
                .build();

        salesService.insertSales(sales);

        // 2. 품목 반복 등록
        Set<Integer> indices = paramMap.keySet().stream()
                .filter(k -> k.matches("items\\[\\d+\\]\\.id"))
                .map(k -> Integer.parseInt(k.replaceAll("\\D", "")))
                .collect(Collectors.toCollection(TreeSet::new));
        for (Integer idx : indices) {
            String itemIdKey = String.format("items[%d].id", idx);
            String quantityKey = String.format("items[%d].quantity", idx);
            String priceKey = String.format("items[%d].price", idx);

            if (!paramMap.containsKey(itemIdKey) || !paramMap.containsKey(quantityKey) || !paramMap.containsKey(priceKey)) continue;

            Long itemId = Long.valueOf(paramMap.get(itemIdKey));
            Integer quantity = Integer.valueOf(paramMap.get(quantityKey));
            BigDecimal price = new BigDecimal(paramMap.get(priceKey));

            SalesData salesData = SalesData.builder()
                    .sales(sales)
                    .item(itemService.getItemById(itemId))
                    .unitPrice(price)
                    .quantity(quantity)
                    .build();

            salesService.insertSalesData(salesData);
        }

        return "redirect:/m31/s01"; // 수주 목록 페이지로 리디렉션
    }

    @GetMapping("/s01/{salesId}/items")
    @ResponseBody
    public ResponseEntity<List<SalesItemResponseDTO>> getOrderItems(@PathVariable Long salesId) {
        List<SalesData> list = salesService.getSalesData(salesService.getSales(salesId));
        List<SalesItemResponseDTO> dtoList = list.stream()
            .map(SalesItemResponseDTO::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }
    
    @GetMapping("/s01/{salesId}")
    @ResponseBody
    public ResponseEntity<Sales> getOrderById(@PathVariable Long salesId) {
        Sales sales = salesService.getSales(salesId);
        Sales value = new Sales();
        
        value.setId(sales.getId());
        value.setClient(sales.getClient());
        
       return ResponseEntity.ok(value);
    }
    
    @PostMapping("/s01/update/{salesId}")
    public String updateOrder(@PathVariable Long salesId,@RequestParam("clientId") Long clientId,
            @RequestParam("orderDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate salesDate,
            @RequestParam Map<String, String> paramMap,
            Principal principal) {
    	if (principal == null) return "redirect:/login";
        // 로그인 사용자 정보
        User user = userService.getuserinfo(principal.getName());
        Company company = user.getCompany();

        Sales sales = Sales.builder()
        		.id(salesId)
                .company(company)
                .client(clientService.getClientById(clientId))
                .saleDate(salesDate)
                .isComplete(2)
                .build();

        salesService.insertSales(sales);
        salesService.deleteSalesData(sales);
        
        Set<Integer> indices = paramMap.keySet().stream()
                .filter(k -> k.matches("items\\[\\d+\\]\\.id"))
                .map(k -> Integer.parseInt(k.replaceAll("\\D", "")))
                .collect(Collectors.toCollection(TreeSet::new));
        for (Integer idx : indices) {
            String itemIdKey = String.format("items[%d].id", idx);
            String quantityKey = String.format("items[%d].quantity", idx);
            String priceKey = String.format("items[%d].price", idx);

            if (!paramMap.containsKey(itemIdKey) || !paramMap.containsKey(quantityKey) || !paramMap.containsKey(priceKey)) continue;

            Long itemId = Long.valueOf(paramMap.get(itemIdKey));
            Integer quantity = Integer.valueOf(paramMap.get(quantityKey));
            BigDecimal price = new BigDecimal(paramMap.get(priceKey));

            SalesData salesData = SalesData.builder()
                    .sales(sales)
                    .item(itemService.getItemById(itemId))
                    .unitPrice(price)
                    .quantity(quantity)
                    .build();

            salesService.insertSalesData(salesData);
        }
        
        return "redirect:/m31/s01";
    }
    
    @GetMapping("/s01/date/{date}")
    @ResponseBody
    public List<Sales> getOrdersByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, Principal principal) {
		if (principal == null) return null;
		User user = userService.getuserinfo(principal.getName());
        return salesService.getSalesByDate(user.getCompany(), date);
    }
    
    @GetMapping("/s01/delete/{salesId}")
    public String deleteOrder(@PathVariable("salesId") Long salesId) {
    	Sales sales = salesService.getSales(salesId);
    	
    	salesService.deleteSalesData(sales);
    	salesService.deleteSales(sales);
    	
    	return "redirect:/m31/s01";
    }
	
    @GetMapping("/s02")
	public String move_s02(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
	                       Model model, Principal principal) {

	    if (principal == null) return "redirect:/login";
	    User user = userService.getuserinfo(principal.getName());

	    LocalDate targetDate = (date != null) ? date : LocalDate.now();

	    List<Map<String, Object>> simpleSellable = new ArrayList<>();
	    for (Item item : itemService.SellableItemList(user.getCompany())) {
	        Map<String, Object> map = new HashMap<>();
	        map.put("id", item.getId());
	        map.put("name", item.getItemName());
	        map.put("unit", item.getUnit());
	        map.put("price", item.getPrice());
	        simpleSellable.add(map);
	    }

	    model.addAttribute("clients", clientService.OrderClientList(user.getCompany()));
	    model.addAttribute("salesList", salesService.findByCompanyAndIsComplete(user.getCompany(), 2));
	    model.addAttribute("items", simpleSellable);
	    model.addAttribute("selectedDate", targetDate);

	    return "user/m31/s02";
	}
    
	@PostMapping("/s02/insert")
	public String insertReceipt(@RequestParam("receiptId") Long salesId,
	                            @RequestParam Map<String, String> paramMap,
	                            @RequestParam("isComplete") Boolean isComplete,
	                            Principal principal) {
	    if (principal == null) return "redirect:/login";
	    User user = userService.getuserinfo(principal.getName());
	    // 로그인 사용자 정보

	    Sales sales = salesService.getSales(salesId);
	    
	    if (isComplete) {
	    	sales.setIsComplete(1);
	    	sales.setDeliveryDate(LocalDate.now());
	    	salesService.insertSales(sales);
	    }	    
	    // 2. 품목 반복 등록
	    Set<Integer> indices = paramMap.keySet().stream()
	            .filter(k -> k.matches("items\\[\\d+\\]\\.itemId"))
	            .map(k -> Integer.parseInt(k.replaceAll("\\D", "")))
	            .collect(Collectors.toCollection(TreeSet::new));

	    for (Integer idx : indices) {
	        String itemIdKey = String.format("items[%d].itemId", idx);
	        String quantityKey = String.format("items[%d].receiptQuantity", idx);
	        
	        if (!paramMap.containsKey(itemIdKey)) continue;

	        Long itemId = Long.valueOf(paramMap.get(itemIdKey));
	        Integer quantity = paramMap.containsKey(quantityKey) ? Integer.parseInt(paramMap.get(quantityKey)) : 0;
	        
	        SalesData salesData = salesService.findBySalesAndItem(sales, itemService.getItemById(itemId));
	        salesData.setShippedQuantity(quantity);
	        
	        salesService.insertSalesData(salesData);


	        Item salesItem = itemService.getItemById(itemId);	        
	        
	        inventoryService.inventoryMinus(user.getCompany(), salesItem, new BigDecimal(quantity), sales);

	    }

	    return "redirect:/m31/s02"; // 입고 관리 페이지로 리디렉션
	}

	@GetMapping("/s02/{orderId}/{itemId}")
	@ResponseBody
	public Map<String, Object> getReceiptData(@PathVariable("orderId") Long salesId,
	                                          @PathVariable("itemId") Long itemId,Principal principal) {
		
	    if (principal == null) return null;
	    User user = userService.getuserinfo(principal.getName());
	    
	    Sales sales = salesService.getSales(salesId);
	    Item item = itemService.getItemById(itemId);
		SalesData salesData = salesService.findBySalesAndItem(sales,item);

	    Map<String, Object> result = new HashMap<>();
	    if (salesData != null) {
	        result.put("receiptQuantity", salesData.getShippedQuantity());
	        result.put("receiptPrice", salesData.getUnitPrice());
	        result.put("inventory", inventoryService.getItemQuantity(user.getCompany(),itemService.getItemById(itemId)));
	    }

	    return result;
	}
    
	@GetMapping("/s03")
	public String move_s03(
	        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
	        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
	        @RequestParam(required = false) Long clientId,
	        Model model,
	        Principal principal) {

	    if (principal == null) return "redirect:/login";
	    User user = userService.getuserinfo(principal.getName());

	    // 기본 날짜 설정: 오늘 포함 최근 30일
	    if (startDate == null) startDate = LocalDate.now().minusDays(30);
	    if (endDate == null) endDate = LocalDate.now();

	    // 거래처 목록
	    List<Client> clients = clientService.SalesClientList(user.getCompany());
	    model.addAttribute("clients", clients);

	    // 조건에 맞는 발주 내역 조회
	    List<Sales> sales = salesService.findSales(user.getCompany().getId(), startDate, endDate, clientId);
	    model.addAttribute("sales", sales);

	    int totalAmount = 0;
	    for(Sales sale : sales) {
	    	for(SalesData salesData : sale.getSalesDataList()) {
	    		int Amount = 0; 		
	    		if (salesData.getShippedQuantity() != null) Amount = salesData.getShippedQuantity();
	    		totalAmount += Amount * salesData.getUnitPrice().intValue();
	    	}
	    }
	    // 현재 필터값을 View에 전달
	    model.addAttribute("startDate", startDate);
	    model.addAttribute("endDate", endDate);
	    model.addAttribute("selectedClientId", clientId);
	    model.addAttribute("totalAmount", totalAmount);
	    return "user/m31/s03";
	}

    
    @GetMapping("/s04")
	public String move_s04(
	        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
	        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
	        @RequestParam(required = false) Long clientId,
	        Model model,
	        Principal principal) {	    	
    	
		if (principal == null) return "redirect:/login";
		User user = userService.getuserinfo(principal.getName());
		
		// 기본값 처리 (예: 이번 달)
	    if (startDate == null) startDate = LocalDate.now().withDayOfMonth(1);
	    if (endDate == null) endDate = LocalDate.now();
	    
	    // 발주 목록 조회
	    List<Sales> sales = salesService.findSales(user.getCompany().getId(), startDate, endDate, clientId);
	    List<Client> clients = clientService.SalesClientList(user.getCompany());

	    model.addAttribute("sales", sales);
	    model.addAttribute("clients", clients);
	    model.addAttribute("startDate", startDate);
	    model.addAttribute("endDate", endDate);
	    model.addAttribute("selectedClientId", clientId);
				
    	return "user/m31/s04";
    }
    
    @GetMapping("/s04/{orderId}/report")
    public String getOrderReport(@PathVariable Long orderId, Model model) {
        Sales sales = salesService.getSales(orderId);  // 해당 발주서 조회
        
        // 총 금액 계산 (자바에서 처리)
        long totalAmount = sales.getSalesDataList().stream()
        	    .mapToLong(d -> {
        	        long qty = d.getShippedQuantity() != null ? d.getShippedQuantity().longValue() : 0L;
        	        long price = d.getUnitPrice() != null ? d.getUnitPrice().longValue() : 0L;
        	        return qty * price;
        	    })
        	    .sum();
        
        model.addAttribute("sales", sales);
        model.addAttribute("totalAmount", totalAmount);
        return "fragments/m31s04 :: report";
    }
}
