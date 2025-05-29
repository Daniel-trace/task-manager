// TaskDAOImpl Implementation with Soft Delete
package com.example.taskmanager.dao;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class TaskDAOImpl implements TaskDAO {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public Task findById(String id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        return optionalTask.filter(task -> !task.isDeleted()).orElse(null);
    }

    @Override
    public List<Task> findAll() {
        return taskRepository.findAll().stream()
                .filter(task -> !task.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public Task save(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public void deleteById(String id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        optionalTask.ifPresent(task -> {
            task.setDeleted(true);
            taskRepository.save(task);
        });
    }
}

