package com.example.taskmanager.service;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskService {
    List<Task> getAll(Status status, Priority priority, String assignee);
    List<Task> getAll(Status status, Priority priority, String assignee, String search, String sortField, String sortDir);
    Task getById(String id);
    Task save(Task task);
    void delete(String id);

    Page<Task> getTasks(Pageable pageable);
    Page<Task> searchTasks(String keyword, Pageable pageable);
    Page<Task> getArchivedTasks(Pageable pageable);
    Page<Task> searchArchivedTasks(String keyword, Pageable pageable);

    List<String> getAllAssignees();

    List<Priority> getAvailablePriorities(Status status);
    List<String> getAvailableAssignees(Status status, Priority priority);

    List<Task> getArchivedTasks();
    List<Task> searchArchivedTasks(String keyword);

    void unarchiveTask(String id);

    // NEW method for filtered, paginated, sorted task retrieval
    Page<Task> getFilteredTasks(Status status, Priority priority, String assignee, String search, String sortField, String sortDir, Pageable pageable);
}
