package com.example.taskmanager.controller;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public String listTasks(@RequestParam(required = false) Status status,
                            @RequestParam(required = false) Priority priority,
                            @RequestParam(required = false) String assignee,
                            @RequestParam(required = false) String sortField,
                            @RequestParam(required = false, defaultValue = "asc") String sortDir,
                            Model model,
                            @ModelAttribute("message") String message) {

        model.addAttribute("tasks", taskService.getAll(status, priority, assignee));
        model.addAttribute("statuses", Status.values());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("assignees", taskService.getAllAssignees());
        model.addAttribute("message", message);

        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedPriority", priority);
        model.addAttribute("selectedAssignee", assignee);
        model.addAttribute("sortDir", sortDir);

        return "task-list";
    }

    @GetMapping("/new")
    public String createTaskForm(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("statuses", Status.values());
        model.addAttribute("priorities", Priority.values());
        return "task-form";
    }

    @PostMapping
    public String saveTask(@Valid @ModelAttribute Task task, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("statuses", Status.values());
            model.addAttribute("priorities", Priority.values());
            return "task-form";
        }
        taskService.save(task);
        redirectAttributes.addFlashAttribute("message", "Task created successfully.");
        return "redirect:/tasks";
    }

    @GetMapping("/{id}/edit")
    public String editTask(@PathVariable String id, Model model) {
        Task task = taskService.getById(id);
        model.addAttribute("task", task);
        model.addAttribute("statuses", Status.values());
        model.addAttribute("priorities", Priority.values());
        return "task-detail";
    }

    @PostMapping("/{id}")
    public String updateTask(@PathVariable String id, @Valid @ModelAttribute Task task,
                             BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("statuses", Status.values());
            model.addAttribute("priorities", Priority.values());
            return "task-detail";
        }
        task.setId(id);
        taskService.save(task);
        redirectAttributes.addFlashAttribute("message", "Task updated successfully.");
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable String id, RedirectAttributes redirectAttributes) {
        taskService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Task deleted successfully.");
        return "redirect:/tasks";
    }
}
