package com.example.taskmanager.repository;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Task;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

	// List<Task> findByCompleted(boolean completed); - No longer in use

	// List<Task> findByDeadline(LocalDate deadline); - No longer in use

	// List<Task> findByDeadlineBefore(LocalDate datum); - No longer in use

	List<Task> findByUserUserid(Long userid);

	List<Task> findByUserUseridAndCompleted(Long userid, boolean completed);

	List<Task> findByUserUseridAndDeadlineBefore(Long userid, LocalDate deadline);

	List<Task> findByUserUserid(Long userid, Sort sort);

	List<Task> findByUserUseridAndPriority(Long userid, Priority priority);

	Optional<Task> findByIdAndUserUserid(Long id, Long userid);

}