package com.example.taskmanager.service;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;

import java.util.List;
import java.util.Map;

public interface TaskService {
    List<Task> getAll(Status status, Priority priority, String assignee);
    Task getById(String id);
    Task save(Task task);
    void delete(String id);

    List<String> getAllAssignees();// for dropdown population

    // New method for filter dependency data
    List<Priority> getAvailablePriorities(Status status);
    List<String> getAvailableAssignees(Status status, Priority priority);
    Map<String, List<String>> getFilterOptions(Status status, Priority priority, String assignee);


}