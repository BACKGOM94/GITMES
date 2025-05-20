package com.gitmes.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gitmes.model.Company;
import com.gitmes.model.Process;
import com.gitmes.model.Task;
import com.gitmes.model.TaskProcessMapping;
import com.gitmes.repository.ProcessRepository;
import com.gitmes.repository.TaskProcessMappingRepository;
import com.gitmes.repository.TaskRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskProcessService {
	
	private final TaskRepository taskRepository;
	private final ProcessRepository processRepository;
	private final TaskProcessMappingRepository taskProcessMappingRepository;
	
	public void save_task(Task task) {
		taskRepository.save(task);
	}
	
	public List<Task> listTask(Company company){
		return taskRepository.findByCompany(company);
	}
	
	public Task findById(Long taskId) {
		return taskRepository.findById(taskId)
				.orElseThrow(() -> new UsernameNotFoundException("작업을 찾을 수 없습니다."));
	}
	
	public Task findTop1ByCompany(Company company) {
		return taskRepository.findTop1ByCompany(company);
	}
	
	public void save_process(Process process) {
		processRepository.save(process);
	}
	
	public List<Process> listProcees(Company company){
		return processRepository.findByCompany(company);
	}
	
	@Transactional
	public void save_taskProcess(Long processId, List<Long> taskIds) {
        // 공정과 작업실을 가져옵니다.
        Process process = processRepository.findById(processId)
                                           .orElseThrow(() -> new RuntimeException("공정을 찾을 수 없습니다."));
        
        taskProcessMappingRepository.deleteByProcessId(processId);
        taskProcessMappingRepository.flush();
        
        List<Task> tasks = new ArrayList<>();
        for (Long taskId : taskIds) {
            taskRepository.findById(taskId).ifPresent(tasks::add);
        }
        
        int sequence = 1;
        // 작업실-공정 매핑 정보를 저장합니다.
        for (Task task : tasks) {
            TaskProcessMapping taskProcessMapping = new TaskProcessMapping();
            taskProcessMapping.setProcess(process);
            taskProcessMapping.setTask(task);
            taskProcessMapping.setSequence(sequence);
            taskProcessMappingRepository.save(taskProcessMapping);
            sequence++;
        }
	}
	
	public ResponseEntity<?> delete_process(Long processId) {
        try {
            // 해당 공정을 찾고 삭제
            Process process = processRepository.findById(processId)
                                               .orElseThrow(() -> new RuntimeException("공정을 찾을 수 없습니다."));
            processRepository.delete(process);
            return ResponseEntity.ok().build(); // 성공적으로 삭제
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("공정 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
	}
}
