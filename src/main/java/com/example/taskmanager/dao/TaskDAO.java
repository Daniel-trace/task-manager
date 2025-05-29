// TaskDAO Interface
package com.example.taskmanager.dao;

import com.example.taskmanager.model.Task;
import java.util.List;

public interface TaskDAO {
    Task findById(String id);
    List<Task> findAll();
    Task save(Task task);
    void deleteById(String id);
}