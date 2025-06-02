package com.example.taskmanager.controller;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskRestController {

    private static final Logger logger = LoggerFactory.getLogger(TaskRestController.class);

    @Autowired
    private TaskService taskService;

    @GetMapping
    public List<Task> getAllTasks(@RequestParam(required = false) Status status,
                                  @RequestParam(required = false) Priority priority,
                                  @RequestParam(required = false) String assignee){
        return taskService.getAll(status, priority, assignee);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable String id) {
        try {
            Task task = taskService.getById(id);
            if (task == null) {
                logger.warn("Task with ID {} not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
            }
            return ResponseEntity.ok(task);
        } catch (Exception e) {
            logger.error("Failed to retrieve task with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving task");
        }
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskService.save(task);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable String id, @RequestBody Task task) {
        task.setId(id);
        return taskService.save(task);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable String id) {
        taskService.delete(id);
    }

    // New Endpoint: Dynamic filter options based on current selection
    @GetMapping("/filters/options")
    public Map<String, Set<String>> getFilteredOptions(@RequestParam(required = false) Status status,
                                                       @RequestParam(required = false) Priority priority,
                                                       @RequestParam(required = false) String assignee) {
        List<Task> filteredTasks = taskService.getAll(status, priority, assignee);

        Set<String> statuses = filteredTasks.stream().map(t -> t.getStatus().name()).collect(Collectors.toSet());
        Set<String> priorities = filteredTasks.stream().map(t -> t.getPriority().name()).collect(Collectors.toSet());
        Set<String> assignees = filteredTasks.stream().map(Task::getAssignee)
                .filter(Objects::nonNull).filter(s -> !s.isBlank()).collect(Collectors.toSet());

        Map<String, Set<String>> options = new HashMap<>();
        options.put("statuses", statuses);
        options.put("priorities", priorities);
        options.put("assignees", assignees);

        return options;
    }

    @GetMapping("/filters/priorities")
    public List<Priority> getAvailablePriorities(@RequestParam(required = false) Status status) {
        return taskService.getAvailablePriorities(status);
    }

    @GetMapping("/filters/assignees")
    public List<String> getAvailableAssignees(@RequestParam(required = false) Status status,
                                              @RequestParam(required = false) Priority priority) {
        return taskService.getAvailableAssignees(status, priority);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<Task>> getFilteredTasks(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String assignee,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Task> tasksPage = taskService.getFilteredTasks(status, priority, assignee, search, sortField, sortDir, pageable);
            return ResponseEntity.ok(tasksPage);
        } catch (Exception e) {
            logger.error("Error fetching paged filtered tasks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

} 