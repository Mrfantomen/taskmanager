package com.example.taskmanager.repository;

import com.example.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

   List<Task> findByCompleted(boolean completed);
   List<Task> findByDeadline(LocalDate deadline);
   List<Task> findByDeadlineBefore(LocalDate datum);
   
   List<Task>findByUserUserid(Long userid);
   

}