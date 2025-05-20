package com.gitmes.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.gitmes.dto.GraphDto;
import com.gitmes.model.Defect;
import com.gitmes.model.Inventory;
import com.gitmes.model.InventoryLog;
import com.gitmes.model.Item;
import com.gitmes.model.Production;
import com.gitmes.model.Task;
import com.gitmes.model.TaskProcessMapping;
import com.gitmes.model.User;
import com.gitmes.service.DefectService;
import com.gitmes.service.InventoryService;
import com.gitmes.service.ItemService;
import com.gitmes.service.ProductionService;
import com.gitmes.service.TaskProcessService;
import com.gitmes.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/m21")
public class M21Controller {

	private final UserService userService;
	private final ProductionService productionService;
	private final DefectService defectService;
	private final TaskProcessService taskProcessService;
	private final ItemService itemService;
	private final InventoryService inventoryService;
	
	@GetMapping("/s01")
	public String move_s01(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
	                       Model model, Principal principal) {
		
	    if (principal == null) return "redirect:/login";
	    User user = userService.getuserinfo(principal.getName());

	    LocalDate targetDate = (date != null) ? date : LocalDate.now();
	    
	    model.addAttribute("selectedDate", targetDate);
	    model.addAttribute("productionList", productionService.findByCompanyAndProductionDateAndProductionSequence(user.getCompany(), date,0));
	    
		return "user/m21/s01";
	}
	
    @GetMapping("/s01/{ProductionId}")
    @ResponseBody
    public ResponseEntity<HashMap<String, Object>> getOrderItems(@PathVariable Long ProductionId) {
    	HashMap<String, Object> map = new HashMap<>();
    	Production production = productionService.findById(ProductionId);
    	Defect defect = defectService.findByProduction(production);
    	Item defectItem = production.getItem();
    	if (defect != null) defectItem = defect.getProduction().getItem();
    	
    	List<TaskProcessMapping> taskProcessMappingList = defectItem.getProcess().getTaskMappings();
    	List<HashMap<String, Object>> taskList = new ArrayList<HashMap<String, Object>>();
    	
    	for(TaskProcessMapping taskProcessMapping : taskProcessMappingList) {
    		Task task = taskProcessMapping.getTask();
    		HashMap<String, Object> taskMap = new HashMap<>();
    		taskMap.put("id",task.getId());
    		taskMap.put("name",task.getName());
    		taskList.add(taskMap);
    	}
    	
    	map.put("taskList", taskList);
    	map.put("selectedProductionInfo", production.getProductionDate() + " - " 
    	+ production.getItem().getItemName() + " - " + production.getQuantity() + " " + production.getItem().getUnit() + " - " + production.getMemo());
    	map.put("discrepantDate",production.getProductionDate());
    	map.put("defectItemId", defectItem.getId());
    	map.put("defectItem", defectItem.getItemName());
    	map.put("productionQuantity",production.getQuantity());
    	
    	if (defect != null) {
    		map.put("defectId", defect.getId());
    		map.put("defectQuantity", defect.getQuantity());
			map.put("action", defect.getAction());
			map.put("description", defect.getDescription());
			map.put("selectedTaskId", defect.getTask().getId());
		}
    	
        return ResponseEntity.ok(map);
    }

    @PostMapping("/s01/insert")
    public String insertOrder(@RequestParam("discrepantDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate discrepantDate,
                              @RequestParam Map<String, String> paramMap,Principal principal) {
    	
    	Defect defect = new Defect();
    	
    	if (paramMap.get("defectId") != "") defect = defectService.findById(Long.parseLong(paramMap.get("defectId")));
    	
    	Production production = productionService.findById(Long.parseLong(paramMap.get("productionId")));
    	InventoryLog inventoryLog = inventoryService.findByProductionAndLogType(production,"생산");
    	Inventory inventory = inventoryLog.getInventory();
    	
    	BigDecimal quantity = inventory.getQuantity().subtract(new BigDecimal(paramMap.get("defectQuantity")));
    	
    	if(paramMap.get("defectId") != "") quantity = quantity.add(new BigDecimal(defect.getQuantity()));
    	
    	inventory.setQuantity(quantity);
    	inventoryLog = inventoryService.findByProductionAndLogType(production,"불량");
    	
    	if (inventoryLog == null) {
			inventoryLog = new InventoryLog();
			inventoryLog.setInventory(inventory);
			inventoryLog.setLogType("불량");
			inventoryLog.setProduction(production);
		}
    	
    	inventoryLog.setQuantity(new BigDecimal(paramMap.get("defectQuantity")));
    	
    	inventoryService.save(inventory, inventoryLog);
    	
    	defect.setProduction(production);
    	defect.setTask(taskProcessService.findById(Long.parseLong(paramMap.get("taskId"))));
    	defect.setQuantity(Integer.parseInt(paramMap.get("defectQuantity")));
    	defect.setDescription(paramMap.get("description"));
    	defect.setAction(paramMap.get("action"));
    	
    	defectService.save(defect);
		
    	return "redirect:/m21/s01?date=" + discrepantDate;
    }
    
	@GetMapping("/s02")
	public String move_s04(
	        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
	        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
	        @RequestParam(required = false) Long itemId,
	        @RequestParam(required = false) Long taskId,
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
	    
	    // 작업 목록
	    List<Task> tasks = taskProcessService.listTask(user.getCompany());
	    model.addAttribute("tasks", tasks);

	    // 조건에 맞는 발주 내역 조회
	    List<Defect> defects = defectService.findDefects(user.getCompany().getId(), startDate, endDate, itemId, taskId);
	    model.addAttribute("defects", defects);
	    
	    // 현재 필터값을 View에 전달
	    model.addAttribute("startDate", startDate);
	    model.addAttribute("endDate", endDate);
	    model.addAttribute("selectedItemId", itemId);
	    model.addAttribute("selectedTaskId", taskId);
	    
		return "user/m21/s02";
	}
	
	@GetMapping("/s03")
	public String move_s03(@RequestParam(required = false) String date,
						            @RequestParam(required = false) Long taskId,
						            @RequestParam(required = false) Long itemId,
						            Model model, Principal principal) {
		
	    if (principal == null) return "redirect:/login";
	    User user = userService.getuserinfo(principal.getName());

	    if(date == null) {
	        // 오늘 날짜를 가져오기
	        LocalDate today = LocalDate.now();
	        
	        // 원하는 형식으로 포맷팅
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
	        date = today.format(formatter);
	    }
	    
	    List<Task> taskList = taskProcessService.listTask(user.getCompany());
	    List<Item> itemList = itemService.ProducibleItemList(user.getCompany());
	    
	    List<GraphDto> dailyGraph = new ArrayList<GraphDto>();
	    List<GraphDto> monthlyGraph = new ArrayList<GraphDto>();
	    List<GraphDto> dailyCircleGraph = new ArrayList<GraphDto>();
	    List<GraphDto> monthlyCircleGraph = new ArrayList<GraphDto>();
	    
	    HashMap<Long, Integer> dailyCircleGraphMap = new HashMap<Long, Integer>();
	    HashMap<Long, Integer> monthlyCircleGraphMap = new HashMap<Long, Integer>();
	    
	    YearMonth yearMonth = YearMonth.parse(date);
	    
	    for(int i = 0 ; i < yearMonth.lengthOfMonth() ; i++) {
	    	int totalProductionCount = 0;
	    	int totalDefectCount = 0;
	    	
	    	for(Defect d : defectService.findDefects(user.getCompany().getId(), yearMonth.atDay(i+1), yearMonth.atDay(i+1), itemId, taskId)) {
	    		totalProductionCount += d.getProduction().getQuantity();
	    		totalDefectCount += d.getQuantity();
	    		
	    		if (dailyCircleGraphMap.get(d.getTask().getId()) == null) dailyCircleGraphMap.put(d.getTask().getId(), d.getQuantity());
	    		else dailyCircleGraphMap.put(d.getTask().getId(), dailyCircleGraphMap.get(d.getTask().getId()) + d.getQuantity());
	    		
	    	}	    	
	    	
	    	GraphDto barGraphDto = new GraphDto();
	    	barGraphDto.setLabels(i+1 + "일");
	    	if (totalProductionCount == 0) barGraphDto.setData(new BigDecimal(0));
	    	else barGraphDto.setData(new BigDecimal(totalDefectCount).divide(new BigDecimal(totalProductionCount),3,RoundingMode.UP).multiply(new BigDecimal(100)));
	    	dailyGraph.add(barGraphDto);	
	    	 	
	    }	    
    	
	    for(int i = 0 ; i < 12 ; i++) {
	    	int totalProductionCount = 0;
	    	int totalDefectCount = 0;
	    	YearMonth month = YearMonth.parse("2025-"+String.format("%2s", Integer.toString(i+1)).replace(" ","0"));
	    	for(Defect d : defectService.findDefects(user.getCompany().getId(), month.atDay(1), month.atDay(month.lengthOfMonth()), itemId, taskId)) {
	    		totalProductionCount += d.getProduction().getQuantity();
	    		totalDefectCount += d.getQuantity();
	    		
	    		if (monthlyCircleGraphMap.get(d.getTask().getId()) == null) monthlyCircleGraphMap.put(d.getTask().getId(), d.getQuantity());
	    		else monthlyCircleGraphMap.put(d.getTask().getId(), monthlyCircleGraphMap.get(d.getTask().getId()) + d.getQuantity());
	    	}
	    	
	    	
	    	GraphDto barGraphDto = new GraphDto();
	    	barGraphDto.setLabels(i+1 + "월");
	    	if (totalProductionCount == 0) barGraphDto.setData(new BigDecimal(0));
	    	else barGraphDto.setData(new BigDecimal(totalDefectCount).divide(new BigDecimal(totalProductionCount),3,RoundingMode.UP).multiply(new BigDecimal(100)));
	    	monthlyGraph.add(barGraphDto);
	    }
	    
    	for (Long key : dailyCircleGraphMap.keySet()) {
    		GraphDto circleGraphDto = new GraphDto();
    		circleGraphDto.setLabels(taskProcessService.findById(key).getName());
    		circleGraphDto.setData(new BigDecimal(dailyCircleGraphMap.get(key)));
    		dailyCircleGraph.add(circleGraphDto);
    	}	   
    	
    	for (Long key : monthlyCircleGraphMap.keySet()) {
    		GraphDto circleGraphDto = new GraphDto();
    		circleGraphDto.setLabels(taskProcessService.findById(key).getName());
    		circleGraphDto.setData(new BigDecimal(monthlyCircleGraphMap.get(key)));
    		monthlyCircleGraph.add(circleGraphDto);
    	}	   
    	
        // 선택된 기준월, 공정, 품목을 모델에 추가 (필요에 따라 활용)
        model.addAttribute("selectedDate", date);
        model.addAttribute("selectedTaskId", taskId);
        model.addAttribute("selectedItemId", itemId);
        model.addAttribute("taskList", taskList);
        model.addAttribute("itemList", itemList);
        
        model.addAttribute("dailyGraph", dailyGraph);
        model.addAttribute("monthlyGraph", monthlyGraph);
        model.addAttribute("dailyCircleGraph", dailyCircleGraph);
        model.addAttribute("monthlyCircleGraph", monthlyCircleGraph);
        
		return "user/m21/s03";
	}
    
}
