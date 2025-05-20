package com.gitmes.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitmes.dto.BomDto;
import com.gitmes.model.Bom;
import com.gitmes.model.Company;
import com.gitmes.model.Item;
import com.gitmes.repository.BomRepository;
import com.gitmes.repository.CompanyRepository;
import com.gitmes.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BomService {

    private final BomRepository bomRepository;
    private final ItemRepository itemRepository;
    private final CompanyRepository companyRepository;

    public List<Bom> findAll(Company company) {
        return bomRepository.findByCompany(company);
    }
    
    public List<BomDto> findByParentItemId(Long parentItemId) {
        List<Bom> boms = bomRepository.findByParentItem_Id(parentItemId);

        return boms.stream().map(bom -> new BomDto(
            bom.getId(),
            bom.getParentItem().getId(),
            bom.getParentItem().getItemName(),
            bom.getParentItem().getUnit(),
            bom.getChildItem().getId(),
            bom.getChildItem().getItemName(),
            bom.getChildItem().getUnit(),
            bom.getQuantity(),
            null
        )).toList();
    }

    public void saveMultipleBoms(Long parentItemId, Long companyId, List<Long> childItemIds, List<BigDecimal> quantities, List<String> notes, Boolean isActive) {
        Item parent = itemRepository.findById(parentItemId).orElseThrow();
        Company company = companyRepository.findById(companyId).orElseThrow();

        for (int i = 0; i < childItemIds.size(); i++) {
            Item child = itemRepository.findById(childItemIds.get(i)).orElseThrow();

            Bom bom = Bom.builder()
                    .parentItem(parent)
                    .childItem(child)
                    .company(company)
                    .quantity(quantities.get(i))
                    .isActive(isActive)
                    .build();

            bomRepository.save(bom);
        }
    }

	public void bomSave(Bom bom) {
		bomRepository.save(bom);
	}
	
    @Transactional
    public void deleteByParentItemId(Long parentItemId) {
        bomRepository.deleteByParentItemId(parentItemId);
    }
}
