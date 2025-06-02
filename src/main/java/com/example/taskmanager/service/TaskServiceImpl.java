package com.example.taskmanager.service;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Task> getAll(Status status, Priority priority, String assignee) {
        return getAll(status, priority, assignee, null, null, null);
    }

    @Override
    public List<Task> getAll(Status status, Priority priority, String assignee, String search, String sortField, String sortDir) {
        Comparator<Task> comparator;

        if ("dueDate".equalsIgnoreCase(sortField)) {
            comparator = Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()));
            if ("desc".equalsIgnoreCase(sortDir)) {
                comparator = comparator.reversed();
            }
        } else if ("newlyAdded".equalsIgnoreCase(sortField)) {
            comparator = Comparator.comparing(Task::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            if (!"asc".equalsIgnoreCase(sortDir)) {
                comparator = comparator.reversed();
            }
        } else {
            comparator = Comparator.comparing(Task::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed();
        }

        return taskRepository.findAll().stream()
                .filter(task -> !task.isArchived())
                .filter(task -> status == null || task.getStatus() == status)
                .filter(task -> priority == null || task.getPriority() == priority)
                .filter(task -> assignee == null || assignee.isEmpty() || assignee.equalsIgnoreCase(task.getAssignee()))
                .filter(task -> search == null || search.isBlank() || task.getTitle().toLowerCase().contains(search.toLowerCase()))
                .sorted(comparator)
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
    public List<Priority> getAvailablePriorities(Status status) {
        return taskRepository.findAll().stream()
                .filter(task -> !task.isArchived())
                .filter(task -> status == null || task.getStatus() == status)
                .map(Task::getPriority)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAvailableAssignees(Status status, Priority priority) {
        return taskRepository.findAll().stream()
                .filter(task -> !task.isArchived())
                .filter(task -> status == null || task.getStatus() == status)
                .filter(task -> priority == null || task.getPriority() == priority)
                .map(Task::getAssignee)
                .filter(assignee -> assignee != null && !assignee.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getArchivedTasks() {
        return taskRepository.findAll().stream()
                .filter(Task::isArchived)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> searchArchivedTasks(String keyword) {
        return taskRepository.findAll().stream()
                .filter(Task::isArchived)
                .filter(task -> keyword != null && task.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public void unarchiveTask(String id) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setArchived(false);
        taskRepository.save(task);
    }

    @Override
    public Page<Task> getTasks(Pageable pageable) {
        return taskRepository.findByArchived(false, pageable);
    }

    @Override
    public Page<Task> searchTasks(String keyword, Pageable pageable) {
        return taskRepository.findByArchivedAndTitleContainingIgnoreCase(false, keyword, pageable);
    }

    @Override
    public Page<Task> getArchivedTasks(Pageable pageable) {
        return taskRepository.findByArchived(true, pageable);
    }

    @Override
    public Page<Task> searchArchivedTasks(String keyword, Pageable pageable) {
        return taskRepository.findByArchivedAndTitleContainingIgnoreCase(true, keyword, pageable);
    }

    // New method implementation
    @Override
    public Page<Task> getFilteredTasks(Status status, Priority priority, String assignee, String search, String sortField, String sortDir, Pageable pageable) {
        Query query = new Query();

        // Only non-archived tasks
        query.addCriteria(Criteria.where("archived").is(false));

        if (status != null) {
            query.addCriteria(Criteria.where("status").is(status));
        }

        if (priority != null) {
            query.addCriteria(Criteria.where("priority").is(priority));
        }

        if (assignee != null && !assignee.isBlank()) {
            query.addCriteria(Criteria.where("assignee").regex("^" + assignee + "$", "i"));
        }

        if (search != null && !search.isBlank()) {
            query.addCriteria(Criteria.where("title").regex(search, "i"));
        }

        // Sorting logic
        if (sortField != null && !sortField.isBlank()) {
            Direction direction = "desc".equalsIgnoreCase(sortDir) ? Direction.DESC : Direction.ASC;
            query.with(Sort.by(direction, sortField));
        } else {
            query.with(Sort.by(Direction.DESC, "createdAt"));
        }

        // Pagination
        long total = mongoTemplate.count(query, Task.class);
        query.with(pageable);

        List<Task> tasks = mongoTemplate.find(query, Task.class);

        return new PageImpl<>(tasks, pageable, total);
    }
}
