package com.example.taskmanager.service;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;

import java.util.List;

public interface TaskService {
    List<Task> getAll(Status status, Priority priority);
    Task getById(String id);
    Task save(Task task);
    void delete(String id);
}