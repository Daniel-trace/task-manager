package com.example.taskmanager.model;

import com.example.taskmanager.validation.DueDateNotPast; // import your annotation
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tasks")
public class Task {
    @Id
    private String id;

    @NotBlank(message = "Title is required")
    private String title;

    @Size(max = 500, message = "Description should not exceed 500 characters")
    private String description;

    @NotBlank(message = "Assignee is required")
    private String assignee;

    @DueDateNotPast         // custom  annotation for validation
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @Field("status")
    private Status status;

    @Field("priority")
    private Priority priority;

    @Field("isDeleted")
    private boolean isDeleted = false;

    @Field("createdAt")
    private LocalDateTime createdAt;


    private boolean archived = false;
}
