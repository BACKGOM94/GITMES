package com.gitmes.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.gitmes.model.Company;
import com.gitmes.model.Item;
import com.gitmes.repository.ItemRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {

	private final ItemRepository itemRepository;
	
	public List<Item> allItemList(Company company){
		return itemRepository.findByCompany(company);
	}
	
	public List<String> findCategory(Company company){
		List<Item> list = allItemList(company);
		List<String> value = new ArrayList<String>();
		
		for(Item i : list) {
			value.add(i.getCategory());
		}
		
		return value.stream().distinct().collect(Collectors.toList());		
	}
	
	public List<Item> ProducibleItemList(Company company){
		return itemRepository.findByCompanyAndIsProducibleTrue(company);
	}
	public List<Item> PurchasableItemList(Company company){
		return itemRepository.findByCompanyAndIsPurchasableTrue(company);
	}
	
	public List<Item> SellableItemList(Company company){
		return itemRepository.findByCompanyAndIsSellableTrue(company);
	}
	
	public void itemSave(Item item) {
		itemRepository.save(item);
	}
	
    // 특정 아이템 조회
    public Item getItemById(Long id) {
        return itemRepository.findById(id)
        		.orElseThrow(() -> new IllegalArgumentException("해당 ID의 제품이 존재하지 않습니다."));
    }
    
    @Transactional
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}
