package com.gitmes.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gitmes.model.Defect;
import com.gitmes.model.Inventory;
import com.gitmes.model.InventoryLog;
import com.gitmes.model.Production;
import com.gitmes.model.User;
import com.gitmes.service.DefectService;
import com.gitmes.service.InventoryService;
import com.gitmes.service.ItemService;
import com.gitmes.service.OrderService;
import com.gitmes.service.ProductionService;
import com.gitmes.service.SalesService;
import com.gitmes.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/m41")
public class M41Controller {

	private final UserService userService;
	private final OrderService orderService;
	private final ItemService itemService;
	private final InventoryService inventoryService;
	private final DefectService defectService;
	private final ProductionService productionService;
	private final SalesService salesService;

	@GetMapping("/s01")
	public String move_s01(@RequestParam(required = false) String categoryFilter, Model model, Principal principal) {

		if (principal == null)
			return "redirect:/login";
		User user = userService.getuserinfo(principal.getName());
		List<Inventory> list = inventoryService
				.findByCompanyAndQuantityGreaterThanOrderByUpdatedAtDesc(user.getCompany(), BigDecimal.ZERO);
		List<Inventory> value = new ArrayList<Inventory>();
		if (categoryFilter == null || categoryFilter == "" || categoryFilter.equals("전체")) {
			categoryFilter = "전체";
			value = list;
		} else {
			for (Inventory i : list) {
				if (i.getItem().getCategory().equals(categoryFilter))
					value.add(i);
			}
		}

		model.addAttribute("inventorys", value);
		model.addAttribute("category", itemService.findCategory(user.getCompany()));
		model.addAttribute("selectedcategory", categoryFilter);

		return "user/m41/s01";
	}

	@GetMapping("/s02")
	public String move_s02(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
			@RequestParam(required = false) String logType, Model model, Principal principal) {

		if (principal == null)
			return "redirect:/login";
		User user = userService.getuserinfo(principal.getName());

		// 기본 날짜 설정: 오늘 포함 최근 30일
		if (startDate == null)
			startDate = LocalDate.now().minusDays(30);
		if (endDate == null)
			endDate = LocalDate.now();
		if (logType == null)
			logType = "전체";

		// 현재 필터값을 View에 전달
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("selectedlogType", logType);
		model.addAttribute("inventoryLogList",
				inventoryService.findInventoryLog(user.getCompany().getId(), startDate, endDate, logType));

		return "user/m41/s02";
	}

	@GetMapping("/s03")
	public String move_s03(@RequestParam(required = false) Long inventoryId, Model model, Principal principal) {

		if (principal == null)
			return "redirect:/login";
		User user = userService.getuserinfo(principal.getName());

		model.addAttribute("inventoryList", inventoryService
				.findByCompanyAndQuantityGreaterThanOrderByUpdatedAtDesc(user.getCompany(), new BigDecimal(-1)));
		if (inventoryId != null) {
			model.addAttribute("selectedInventoryId", inventoryId);

			Inventory inventory = inventoryService.findById(inventoryId);
			model.addAttribute("selectedInventory", inventory);

			List<InventoryLog> inventoryLogs = inventoryService.findInventoryLog(inventory);

			List<Map<String, Object>> orderLogs = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> materialInputs = new ArrayList<Map<String, Object>>();
			List<Production> productionLogs = new ArrayList<Production>();
			List<Defect> defectLogs = new ArrayList<Defect>();
			List<Map<String, Object>> salesLogs = new ArrayList<Map<String, Object>>();

			for (InventoryLog i : inventoryLogs) {

				switch (i.getLogType()) {

				case "입고":
					HashMap<String, Object> orderLogsMap = new HashMap<String, Object>();
					orderLogsMap.put("client", i.getOrder().getClient());
					orderLogsMap.put("deliveryDate", i.getOrder().getDeliveryDate());
					orderLogsMap.put("orderDatas", orderService.getOrderData(i.getOrder()));
					orderLogs.add(orderLogsMap);
					break;
				case "자재투입":
					HashMap<String, Object> materialInputsMap = new HashMap<String, Object>();
					materialInputsMap.put("inputQuantity", i.getQuantity());
					materialInputsMap.put("unit", i.getInventory().getItem().getUnit());
					List<String> productionIds = Arrays.asList(i.getMaterialInputProductionId().split(","));
					List<Production> ProductionList = new ArrayList<Production>();
					for (String productionId : productionIds) {
						ProductionList.add(productionService.findById(Long.parseLong(productionId)));
					}
					materialInputsMap.put("ProductionList", ProductionList);
					materialInputs.add(materialInputsMap);
					break;
				case "생산":
					productionLogs.add(i.getProduction());
					break;
				case "불량":
					defectLogs.add(defectService.findByProduction(i.getProduction()));
					break;
				case "출고":
					HashMap<String, Object> salesLogsMap = new HashMap<String, Object>();
					salesLogsMap.put("client", i.getSales().getClient());
					salesLogsMap.put("saleDate", i.getSales().getSaleDate());
					salesLogsMap.put("deliveryDate", i.getSales().getDeliveryDate());
					salesLogsMap.put("salesDatas", salesService.getSalesData(i.getSales()));
					salesLogs.add(salesLogsMap);
					break;
				case "재고조정":
					
					break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + i.getLogType());
				}
			}

			model.addAttribute("orderLogs", orderLogs);
			model.addAttribute("materialInputs", materialInputs);
			model.addAttribute("productionLogs", productionLogs);
			model.addAttribute("defectLogs", defectLogs);
			model.addAttribute("salesLogs", salesLogs);

		}

		return "user/m41/s03";
	}

	@GetMapping("/s04")
	public String move_s04(Model model, Principal principal) {

		if (principal == null)
			return "redirect:/login";
		User user = userService.getuserinfo(principal.getName());

		model.addAttribute("inventoryList", inventoryService
				.findByCompanyAndQuantityGreaterThanOrderByUpdatedAtDesc(user.getCompany(), BigDecimal.ZERO));
		model.addAttribute("items", itemService.allItemList(user.getCompany()));
		return "user/m41/s04";
	}

	@PostMapping("/s04/inventoryInsert")
	@ResponseBody
	public String addInventory(@RequestParam("itemId") Long itemId, @RequestParam("adjustQty") int adjustQty,
			Model model, Principal principal) {
		if (principal == null)
			return "redirect:/login";
		User user = userService.getuserinfo(principal.getName());
		
		Inventory inventory = new Inventory();
		
		inventory.setCompany(user.getCompany());
		inventory.setItem(itemService.getItemById(itemId));
		inventory.setQuantity(new BigDecimal(adjustQty));
		
		InventoryLog inventoryLog = new InventoryLog();
		
		inventoryLog.setInventory(inventory);
		inventoryLog.setLogType("재고조정");
		inventoryLog.setQuantity(new BigDecimal(adjustQty));
		
		inventoryService.save(inventory, inventoryLog);
		
		
		return "redirect:/m41/s04"; // 재고 목록 페이지로 리다이렉트
	}
	
	@PostMapping("/s04/inventoryUpdate")
	@ResponseBody
	public String updateInventory(@RequestParam("inventoryId") Long inventoryId, @RequestParam("adjustQty") int adjustQty,
			Model model, Principal principal) {
		if (principal == null)
			return "redirect:/login";
		User user = userService.getuserinfo(principal.getName());
		
		Inventory inventory = inventoryService.findById(inventoryId);
		
		BigDecimal afterQty = new BigDecimal(adjustQty);

		inventory.setQuantity(afterQty);
		
		InventoryLog inventoryLog = new InventoryLog();
		
		inventoryLog.setInventory(inventory);
		inventoryLog.setLogType("재고조정");
		inventoryLog.setQuantity(afterQty);

		inventoryService.save(inventory, inventoryLog);
		
		return "redirect:/m41/s04"; // 재고 목록 페이지로 리다이렉트
	}

}
