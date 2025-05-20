package com.gitmes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gitmes.model.TaskProcessMapping;

@Repository
public interface TaskProcessMappingRepository extends JpaRepository<TaskProcessMapping, Long> {
	List<TaskProcessMapping> findByProcessIdOrderBySequence(Long processId);
	void deleteByProcessId(Long processId);
}