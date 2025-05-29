package com.example.taskmanager.service;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public List<Task> getAll(Status status, Priority priority, String assignee) {
        return taskRepository.findAll().stream()
                .filter(task -> status == null || task.getStatus() == status)
                .filter(task -> priority == null || task.getPriority() == priority)
                .filter(task -> assignee == null || assignee.isEmpty() || assignee.equals(task.getAssignee()))
                .collect(Collectors.toList());
    }

    @Override
    public Task getById(String id) {
        return taskRepository.findById(id).orElse(null);
    }

    @Override
    public Task save(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public void delete(String id) {
        taskRepository.deleteById(id);
    }

    @Override
    public List<String> getAllAssignees() {
        return taskRepository.findAll().stream()
                .map(Task::getAssignee)
                .filter(assignee -> assignee != null && !assignee.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<String>> getFilterOptions(Status status, Priority priority, String assignee) {
        List<Task> filteredTasks = getAll(status, priority, assignee);

        List<String> priorities = filteredTasks.stream()
                .map(task -> task.getPriority().name())
                .distinct()
                .collect(Collectors.toList());

        List<String> assignees = filteredTasks.stream()
                .map(Task::getAssignee)
                .filter(a -> a != null && !a.isBlank())
                .distinct()
                .collect(Collectors.toList());

        Map<String, List<String>> options = new HashMap<>();
        options.put("priorities", priorities);
        options.put("assignees", assignees);
        return options;
    }

    @Override
    public List<Priority> getAvailablePriorities(Status status) {
        return taskRepository.findAll().stream()
                .filter(task -> status == null || task.getStatus() == status)
                .map(Task::getPriority)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAvailableAssignees(Status status, Priority priority) {
        return taskRepository.findAll().stream()
                .filter(task -> status == null || task.getStatus() == status)
                .filter(task -> priority == null || task.getPriority() == priority)
                .map(Task::getAssignee)
                .filter(assignee -> assignee != null && !assignee.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }
}
