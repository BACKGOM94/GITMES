package com.gitmes.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gitmes.dto.BomDto;
import com.gitmes.dto.ProductionDto;
import com.gitmes.dto.StockInfoDto;
import com.gitmes.model.Client;
import com.gitmes.model.Company;
import com.gitmes.model.Inventory;
import com.gitmes.model.Item;
import com.gitmes.model.Order;
import com.gitmes.model.OrderData;
import com.gitmes.model.Production;
import com.gitmes.model.User;
import com.gitmes.service.BomService;
import com.gitmes.service.InventoryService;
import com.gitmes.service.ItemService;
import com.gitmes.service.ProductionService;
import com.gitmes.service.TaskProcessService;
import com.gitmes.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequiredArgsConstructor
@RequestMapping("/m11")
public class M11Controller {

	private final UserService userService;
	private final ItemService itemService;
	private final ProductionService productionService;
	private final BomService bomService;
	private final InventoryService inventoryService;
	private final TaskProcessService taskProcessService;
	
	@GetMapping("/s01")
	public String move_s01(Model model, Principal principal) {
		if (principal == null)
			return null;
		User user = userService.getuserinfo(principal.getName());

		List<Map<String, Object>> simpleProducible = new ArrayList<>();
		for (Item item : itemService.ProducibleItemList(user.getCompany())) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", item.getId());
			map.put("name", item.getItemName());
			map.put("unit", item.getUnit());
			map.put("price", item.getPrice());
			simpleProducible.add(map);
		}
		model.addAttribute("items", simpleProducible);
		model.addAttribute("productionList", productionService.findproductionDate(user.getCompany().getId()));

		return "user/m11/s01";
	}

	@GetMapping("/s01/{id}")
	@ResponseBody
	public Production getProductionOrder(@PathVariable Long id) {
		return productionService.findById(id);
	}

	@GetMapping("/s01/{productionDate}/items")
	@ResponseBody
	public ResponseEntity<List<ProductionDto>> getProductionOrderItems(@PathVariable LocalDate productionDate,
			Principal principal) {
		if (principal == null)
			return null;
		User user = userService.getuserinfo(principal.getName());
		List<Production> list = productionService.findByCompanyAndProductionDate(user.getCompany(), productionDate);		
		List<ProductionDto> dtoList = list.stream().map(ProductionDto::new).collect(Collectors.toList());
		return ResponseEntity.ok(dtoList);
	}

	@PostMapping("/s01/insert")
	public String insert(
			@RequestParam("productionDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate productionDate,
			@RequestParam Map<String, String> paramMap, Principal principal) {

		// 로그인 사용자 정보
		User user = userService.getuserinfo(principal.getName());
		Company company = user.getCompany();

		Set<Integer> indices = paramMap.keySet().stream().filter(k -> k.matches("items\\[\\d+\\]\\.id"))
				.map(k -> Integer.parseInt(k.replaceAll("\\D", ""))).collect(Collectors.toCollection(TreeSet::new));
		for (Integer idx : indices) {
			String itemIdKey = String.format("items[%d].id", idx);
			String quantityKey = String.format("items[%d].quantity", idx);
			String memoKey = String.format("items[%d].memo", idx);

			if (!paramMap.containsKey(itemIdKey) || !paramMap.containsKey(quantityKey)
					|| !paramMap.containsKey(memoKey))
				continue;

			Long itemId = Long.valueOf(paramMap.get(itemIdKey));
			Integer quantity = Integer.valueOf(paramMap.get(quantityKey));
			String memo = paramMap.get(memoKey);

			Production production = Production.builder().company(company).item(itemService.getItemById(itemId))
					.quantity(quantity).productionDate(productionDate).memo(memo).build();

			productionService.save(production);
		}
		return "redirect:/m11/s01";
	}

	@PostMapping("/s01/update/{id}")
	public String update(
			@RequestParam("productionDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate productionDate,
			@RequestParam Map<String, String> paramMap, Principal principal) {

		// 로그인 사용자 정보
		User user = userService.getuserinfo(principal.getName());
		Company company = user.getCompany();

		Set<Integer> indices = paramMap.keySet().stream().filter(k -> k.matches("items\\[\\d+\\]\\.id"))
				.map(k -> Integer.parseInt(k.replaceAll("\\D", ""))).collect(Collectors.toCollection(TreeSet::new));

		productionService.deleteByCompanyAndProductionDate(user.getCompany(), productionDate);

		for (Integer idx : indices) {
			String itemIdKey = String.format("items[%d].id", idx);
			String quantityKey = String.format("items[%d].quantity", idx);
			String memoKey = String.format("items[%d].memo", idx);

			if (!paramMap.containsKey(itemIdKey) || !paramMap.containsKey(quantityKey)
					|| !paramMap.containsKey(memoKey))
				continue;

			Long itemId = Long.valueOf(paramMap.get(itemIdKey));
			Integer quantity = Integer.valueOf(paramMap.get(quantityKey));
			String memo = paramMap.get(memoKey);

			Production production = Production.builder().company(company).item(itemService.getItemById(itemId))
					.quantity(quantity).productionDate(productionDate).memo(memo).build();

			productionService.save(production);
		}
		return "redirect:/m11/s01";
	}

	@GetMapping("/s01/delete/{productionDate}")
	@ResponseBody
	public void delete(@PathVariable LocalDate productionDate, Principal principal) {
		User user = userService.getuserinfo(principal.getName());

		productionService.deleteByCompanyAndProductionDate(user.getCompany(), productionDate);
	}

	@GetMapping("/s02")
	public String move_s02(Model model, Principal principal) {
		if (principal == null)
			return null;
		User user = userService.getuserinfo(principal.getName());

		List<Map<String, Object>> simpleProducible = new ArrayList<>();
		for (Item item : itemService.ProducibleItemList(user.getCompany())) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", item.getId());
			map.put("name", item.getItemName());
			map.put("unit", item.getUnit());
			map.put("price", item.getPrice());
			simpleProducible.add(map);
		}
		model.addAttribute("items", simpleProducible);
		model.addAttribute("productionList", productionService.findByCompanyAndProductionSequence(user.getCompany()));

		return "user/m11/s02";
	}

	@GetMapping("/s02/materials")
	@ResponseBody
	public ResponseEntity<List<BomDto>> getMaterials(@RequestParam("ids") List<Long> productionIds,
			Principal principal) {

		if (principal == null)
			return null;
		User user = userService.getuserinfo(principal.getName());

		Map<Long, BomDto> materialMap = new HashMap<>();

		for (Long prodId : productionIds) {
			Production production = productionService.findById(prodId);

			Long parentItemId = production.getItem().getId();
			BigDecimal productionQty = BigDecimal.valueOf(production.getQuantity());

			List<BomDto> bomList = bomService.findByParentItemId(parentItemId);

			for (BomDto bom : bomList) {
				Long childItemId = bom.getChildItemId(); // 자재투입 ID 기준으로 중복 체크
				BigDecimal requiredQty = bom.getQuantity().multiply(productionQty);

				if (materialMap.containsKey(childItemId)) {
					// 기존에 있으면 수량 합산
					BomDto existing = materialMap.get(childItemId);
					existing.setQuantity(existing.getQuantity().add(requiredQty));
				} else {
					// 새 항목이면 복사해서 추가
					BomDto newBom = new BomDto();
					newBom.setChildItemId(childItemId);
					newBom.setChildItemName(bom.getChildItemName());
					newBom.setChildItemUnit(bom.getChildItemUnit());
					newBom.setQuantity(requiredQty);

					List<Inventory> list = inventoryService.getInventory(user.getCompany(),
							itemService.getItemById(bom.getChildItemId()));
					List<StockInfoDto> StockInfoList = new ArrayList<StockInfoDto>();
					for (Inventory inventory : list) {
						StockInfoDto stockInfoDto = new StockInfoDto(inventory.getCreatedAt().toLocalDate(),
								inventory.getQuantity());
						StockInfoList.add(stockInfoDto);
					}
					newBom.setStockInfos(StockInfoList);

					materialMap.put(childItemId, newBom);
				}
			}
		}

		return ResponseEntity.ok(new ArrayList<>(materialMap.values()));
	}

	@PostMapping("/s02/submit")
	public String submitMaterialInput(@RequestParam Map<String, String> paramMap, Model model, Principal principal) {

		for (String key : paramMap.keySet()) {
			System.out.println(key + " : " + paramMap.get(key));
		}
		// 로그인 사용자 정보
		User user = userService.getuserinfo(principal.getName());
		Company company = user.getCompany();

		List<Long> productionIdList = Arrays.stream(paramMap.get("productionOrderIds").split(",")).map(Long::parseLong)
				.collect(Collectors.toList());

		for (Long productionId : productionIdList) {
			Production production = productionService.findById(productionId);
			if (production.getItem().getProcess() == null)
				production.setProductionSequence(0);
			else
				production.setProductionSequence(1);

			productionService.save(production);
		}
		
		// 2. 품목 반복 등록
		Set<Integer> indices = paramMap.keySet().stream()
			    .filter(k -> k.matches("items\\[\\d+\\]\\.itemId"))
			    .map(k -> Integer.parseInt(k.replaceAll("\\D", "")))  // 숫자만 추출하여 Integer로 변환
			    .collect(Collectors.toCollection(TreeSet::new));

			for (Integer idx : indices) {
			    String itemIdKey = String.format("items[%d].itemId", idx);
			    String quantityKey = String.format("items[%d].inputQuantities", idx);

			    if (!paramMap.containsKey(itemIdKey) || !paramMap.containsKey(quantityKey)) {
			        continue;  // 둘 중 하나라도 없으면 건너뜀
			    }

			    try {
			        Long itemId = Long.valueOf(paramMap.get(itemIdKey));  // itemId 파싱
			        BigDecimal quantity = new BigDecimal(paramMap.get(quantityKey));  // quantity 파싱

			        inventoryService.inventoryMinus(company, itemService.getItemById(itemId), quantity, paramMap.get("productionOrderIds"));
			    } catch (NumberFormatException e) {
			        // 숫자 변환 오류 시 에러 로그 출력
			        System.err.println("Invalid number format for itemId or quantity: " + e.getMessage());
			    }
			}
			
			
		return "redirect:/m11/s02"; // 목록으로 이동
	}
	
	@GetMapping("/s03")
	public String move_s03(@RequestParam(required = false) Long selectedTaskId, Model model, Principal principal) {
		if (principal == null)
			return null;
		User user = userService.getuserinfo(principal.getName());
		
		if (selectedTaskId == null) selectedTaskId = taskProcessService.findTop1ByCompany(user.getCompany()).getId();
		
		model.addAttribute("selectedTaskId", selectedTaskId);
		model.addAttribute("tasks",taskProcessService.listTask(user.getCompany()));
		model.addAttribute("taskDescription",taskProcessService.findById(selectedTaskId).getDescription());
		model.addAttribute("productions", productionService.findByCompanyIdAndTaskId(user.getCompany(), selectedTaskId));
		
		return "user/m11/s03";
	}
	
	@PostMapping("/s03/next")
	public String s03_next(@RequestParam("productionId") Long productionId, Principal principal) {
		
		if (principal == null) return null;
		User user = userService.getuserinfo(principal.getName());
		
		Production production = productionService.findById(productionId);
		int ProductionSequence = production.getProductionSequence() -1;
		Long selectedTaskId = production.getItem().getProcess().getTaskMappings().get(ProductionSequence).getTask().getId();
		
		if (production.getItem().getProcess().getTaskMappings().size() != ProductionSequence + 1) {
			production.setProductionSequence(ProductionSequence + 2);
			productionService.save(production);
		}else {
			production.setProductionSequence(0);
			Inventory inventory = new Inventory();
			inventory.setCompany(user.getCompany());
			inventory.setItem(production.getItem());
			inventory.setQuantity(new BigDecimal(production.getQuantity()));
			
			productionService.save(production);
			inventoryService.productSave(inventory,production);
		}
		
		return "redirect:/m11/s03?selectedTaskId=" + selectedTaskId;
	}
	
	@GetMapping("/s04")
	public String move_s04(
	        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
	        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
	        @RequestParam(required = false) Long itemId,
	        Model model,
	        Principal principal) {

	    if (principal == null) return "redirect:/login";
	    User user = userService.getuserinfo(principal.getName());

	    // 기본 날짜 설정: 오늘 포함 최근 30일
	    if (startDate == null) startDate = LocalDate.now().minusDays(30);
	    if (endDate == null) endDate = LocalDate.now();

	    // 아이템 목록
	    List<Item> items = itemService.ProducibleItemList(user.getCompany());
	    model.addAttribute("items", items);

	    // 조건에 맞는 발주 내역 조회
	    List<Production> productions = productionService.findproductions(user.getCompany().getId(), startDate, endDate, itemId);
	    model.addAttribute("productions", productions);
	    
	    // 현재 필터값을 View에 전달
	    model.addAttribute("startDate", startDate);
	    model.addAttribute("endDate", endDate);
	    model.addAttribute("selectedItemId", itemId);
	    
	    return "user/m11/s04";
	}
	
    @GetMapping("/s05")
	public String move_s05(
	        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
	        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
	        Model model,
	        Principal principal) {	    	
    	
		if (principal == null) return "redirect:/login";
		User user = userService.getuserinfo(principal.getName());
		
		// 기본값 처리 (예: 이번 달)
	    if (startDate == null) startDate = LocalDate.now().withDayOfMonth(1);
	    if (endDate == null) endDate = LocalDate.now();
	    
	    // 생산 목록 조회
	   	model.addAttribute("productionDate", productionService.findproductionDates(user.getCompany().getId(),startDate,endDate));
	    model.addAttribute("startDate", startDate);
	    model.addAttribute("endDate", endDate);
				
    	return "user/m11/s05";
    }
    
    @GetMapping("/s05/report")
    public String getOrderReport(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, Model model,Principal principal) {
		if (principal == null) return "redirect:/login";
		User user = userService.getuserinfo(principal.getName());
		
		model.addAttribute("selectedDate",date);
    	model.addAttribute("productions", productionService.findByCompanyAndProductionDate(user.getCompany(), date));
    	
        return "fragments/m11s05 :: report";
    }
}