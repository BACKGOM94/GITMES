package com.gitmes.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gitmes.dto.BomDto;
import com.gitmes.model.Bom;
import com.gitmes.model.Client;
import com.gitmes.model.Company;
import com.gitmes.model.Item;
import com.gitmes.model.Process;
import com.gitmes.model.Task;
import com.gitmes.model.TaskProcessRequest;
import com.gitmes.model.User;
import com.gitmes.service.BomService;
import com.gitmes.service.ClientService;
import com.gitmes.service.ItemService;
import com.gitmes.service.TaskProcessService;
import com.gitmes.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/m97")
public class M97Controller {
	
	private final UserService userService;
	private final TaskProcessService taskProcessService;
	private final ItemService itemService;
	private final BomService bomService;
	private final ClientService clientService;
	
    @GetMapping("/s01")
    public String move_itemMaster(Model model, Principal principal) {    	    	
		if (principal == null) return null;
		User user = userService.getuserinfo(principal.getName());
				
		model.addAttribute("processes", taskProcessService.listProcees(user.getCompany()));
		model.addAttribute("items", itemService.allItemList(user.getCompany()));
    	return "user/masterPage/itemMaster";
    }
    
    @PostMapping("/s01/itemInsert")
    public String insert_item(Principal principal,
                              @ModelAttribute Item item,
                              @RequestParam(defaultValue = "false") boolean isPurchasable,
                              @RequestParam(defaultValue = "false") boolean isProducible,
                              @RequestParam(defaultValue = "false") boolean isSellable) {
        if (principal == null) return null;
        User user = userService.getuserinfo(principal.getName());

        item.setCompany(user.getCompany());
        item.setIsPurchasable(isPurchasable);
        item.setIsProducible(isProducible);
        item.setIsSellable(isSellable);

        itemService.itemSave(item);
        return "redirect:/m97/s01";
    }
    
    @GetMapping("/s01/items/{id}")
    @ResponseBody
    public Item getItemById(@PathVariable Long id) {
    	System.out.println(id);
        return itemService.getItemById(id);
    }
    
    @PostMapping("/s01/itemUpdate")
    public String update_item(Principal principal,
                              @ModelAttribute Item item,
                              @RequestParam(defaultValue = "false") boolean isPurchasable,
                              @RequestParam(defaultValue = "false") boolean isProducible,
                              @RequestParam(defaultValue = "false") boolean isSellable) {
        if (principal == null) return null;
        User user = userService.getuserinfo(principal.getName());

        Item existingItem = itemService.getItemById(item.getId());
        if (existingItem != null) {
            existingItem.setItemName(item.getItemName());
            existingItem.setCategory(item.getCategory());
            existingItem.setUnit(item.getUnit());
            existingItem.setPrice(item.getPrice());
            existingItem.setConversionFactor(item.getConversionFactor());
            existingItem.setProcess(item.getProcess());
            existingItem.setOrderItem(item.getOrderItem());
            existingItem.setIsPurchasable(isPurchasable);
            existingItem.setIsProducible(isProducible);
            existingItem.setIsSellable(isSellable);

            itemService.itemSave(existingItem);
        }

        return "redirect:/m97/s01";
    }
    
    @DeleteMapping("/s01/itemDelete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/s02")
    public String move_taskProcess(Model model, Principal principal) {    	    	
		if (principal == null) return null;
		User user = userService.getuserinfo(principal.getName());
    	model.addAttribute("tasks", taskProcessService.listTask(user.getCompany()));
    	model.addAttribute("processs", taskProcessService.listProcees(user.getCompany()));    	
    	return "user/masterPage/taskProcess";
    }
    
    @PostMapping("/s02/taskInsert")
    public String insert_task(Principal principal,
    		@RequestParam("task-name") String name,
    		@RequestParam("task-desc") String description) {
		if (principal == null) return null;
		User user = userService.getuserinfo(principal.getName());
		Task task = new Task();
				
		task.setCompany(user.getCompany());
		task.setName(name);
		task.setDescription(description);		
		
		taskProcessService.save_task(task);
		
    	return "redirect:/m97/s02";
    }
    
    @PostMapping("/s02/processInsert")
    public String insert_process(Principal principal,
    		@RequestParam("process-name") String name,
    		@RequestParam("process-desc") String description) {
		if (principal == null) return null;
		User user = userService.getuserinfo(principal.getName());
		Process process = new Process();
				
		process.setCompany(user.getCompany());
		process.setName(name);
		process.setDescription(description);
		
		taskProcessService.save_process(process);
		
    	return "redirect:/m97/s02";
    }
    
    @PostMapping("/s02/insertTaskProcess")
    @ResponseBody
    public ResponseEntity<?> mapTasksToProcess(@RequestBody TaskProcessRequest request) {
        taskProcessService.save_taskProcess(request.getProcessId(), request.getTaskIds());
        
        // 처리 결과에 따라 적절한 응답 반환
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "작업실 매핑이 성공적으로 저장되었습니다.");
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/s02/deleteProcess/{processId}")
    @ResponseBody
    public ResponseEntity<?> deleteProcess(@PathVariable Long processId) {
        return taskProcessService.delete_process(processId);
    }

    @GetMapping("/s03")
    public String move_BomMaster(Model model, Principal principal) {    	    	
		if (principal == null) return null;
		User user = userService.getuserinfo(principal.getName());	
	    
        List<Item> producibleItems = itemService.ProducibleItemList(user.getCompany());

        List<Map<String, Object>> simpleMaterials = new ArrayList<>();
        for (Item item : itemService.allItemList(user.getCompany())) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("name", item.getItemName());
            map.put("unit", item.getUnit());
            simpleMaterials.add(map);
        }

        model.addAttribute("items", producibleItems);
        model.addAttribute("materials", simpleMaterials);
	        
    	return "user/masterPage/bomMaster";
    }
    
    @GetMapping("/s03/by-parent/{parentId}")
    @ResponseBody
    public List<BomDto> getBomsByParent(@PathVariable Long parentId) {
        return bomService.findByParentItemId(parentId);
    }
    
    @PostMapping("/s03/insertItem")
    public String addBom(@RequestParam("parentItemId") Long parentItemId,
                         @RequestParam("isActive") boolean isActive,
                         @RequestParam Map<String, String> paramMap,
                         Principal principal) {
        if (principal == null) return null;

        User user = userService.getuserinfo(principal.getName());	
        Company company = user.getCompany();
        Item parentItem = itemService.getItemById(parentItemId);

        // 기존 BOM 삭제
        bomService.deleteByParentItemId(parentItemId);

        // 존재하는 materials 인덱스를 찾아서 반복
        Set<Integer> indices = paramMap.keySet().stream()
                .filter(k -> k.matches("materials\\[\\d+\\]\\.materialId"))
                .map(k -> Integer.parseInt(k.replaceAll("\\D", "")))
                .collect(Collectors.toCollection(TreeSet::new)); // TreeSet = 자동 정렬됨

        for (Integer idx : indices) {
            String materialIdKey = String.format("materials[%d].materialId", idx);
            String quantityKey = String.format("materials[%d].quantity", idx);

            if (!paramMap.containsKey(materialIdKey) || !paramMap.containsKey(quantityKey)) continue;

            Long childItemId = Long.valueOf(paramMap.get(materialIdKey));
            BigDecimal quantity = new BigDecimal(paramMap.get(quantityKey));

            Item childItem = itemService.getItemById(childItemId);

            Bom bom = Bom.builder()
                    .company(company)
                    .parentItem(parentItem)
                    .childItem(childItem)
                    .quantity(quantity)
                    .isActive(isActive)
                    .build();

            bomService.bomSave(bom);
        }

        return "redirect:/m97/s03";
    }
    
    @GetMapping("/s04")
    public String move_ClientMaster(Model model, Principal principal) {    	    	
		if (principal == null) return null;
		User user = userService.getuserinfo(principal.getName());
		model.addAttribute("clients", clientService.allClientList(user.getCompany()));
    	return "user/masterPage/clientMaster";
    }
    
    @PostMapping("/s04/clinetInsert")
    public String insert_item(Principal principal,@ModelAttribute Client client) {
        if (principal == null) return null;
        User user = userService.getuserinfo(principal.getName());
        client.setCompany(user.getCompany());
        clientService.ClientSave(client);
        return "redirect:/m97/s04";
    }
    
    @GetMapping("/s04/clients/{id}")
    @ResponseBody
    public Client getclinetById(@PathVariable Long id) {
        return clientService.getClientById(id);
    }
    
    @PostMapping("/s04/clientUpdate")
    public String update_clinet(Principal principal,@ModelAttribute Client client) {
        if (principal == null) return null;
        User user = userService.getuserinfo(principal.getName());

        Client existingClient = clientService.getClientById(client.getId());
        if (existingClient != null) {
        	existingClient.setName(client.getName());
        	existingClient.setRepresentative(client.getRepresentative());
        	existingClient.setPhone(client.getPhone());
        	existingClient.setEmail(client.getEmail());
        	existingClient.setAddress(client.getAddress());
        	existingClient.setType(client.getType());
        	existingClient.setNote(client.getNote());

            clientService.ClientSave(existingClient);
        }

        return "redirect:/m97/s04";
    }
    
    @DeleteMapping("/s04/clientDelete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteClient(@PathVariable Long id) {
        clientService.deleteItem(id);
        return ResponseEntity.ok().build();
    }
}
