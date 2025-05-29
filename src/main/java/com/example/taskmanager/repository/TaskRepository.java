package com.example.taskmanager.repository;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByStatus(Status status);
    List<Task> findByPriority(Priority priority);

    // Custom query: filter by optional status, priority, and assignee, and ignore deleted tasks
    @Query("{ $and: [" +
            "  { $or: [ { 'status': ?0 }, { ?0: null } ] }," +
            "  { $or: [ { 'priority': ?1 }, { ?1: null } ] }," +
            "  { $or: [ { 'assignee': ?2 }, { ?2: null } ] }," +
            "  { 'deleted': false }" +
            "]}")
    List<Task> findByStatusAndPriorityAndAssignee(Status status, Priority priority, String assignee);

}