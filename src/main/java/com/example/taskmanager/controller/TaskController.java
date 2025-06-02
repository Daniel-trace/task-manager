package com.example.taskmanager.controller;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public String listTasks(@RequestParam(required = false) Status status,
                            @RequestParam(required = false) Priority priority,
                            @RequestParam(required = false) String assignee,
                            @RequestParam(required = false) String search,
                            @RequestParam(required = false, defaultValue = "newlyAdded") String sortField,
                            @RequestParam(required = false) String sortDir,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            Model model,
                            @ModelAttribute("message") String message) {

        // Default to 'desc' for newlyAdded if sortDir is not specified
        if ("newlyAdded".equalsIgnoreCase(sortField) && (sortDir == null || sortDir.isBlank())) {
            sortDir = "desc";
        }

        Pageable pageable = PageRequest.of(page, 10); // 10 tasks per page
        Page<Task> taskPage;

        if (search != null && !search.isEmpty()) {
            taskPage = taskService.searchTasks(search, pageable);
        } else {
            taskPage = taskService.getFilteredTasks( status, priority, assignee, search, sortField, sortDir, pageable);
        }

        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("statuses", Status.values());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("assignees", taskService.getAllAssignees());
        model.addAttribute("message", message);

        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedPriority", priority);
        model.addAttribute("selectedAssignee", assignee);
        model.addAttribute("search", search);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortField", sortField);
        model.addAttribute("currentPage", taskPage.getNumber());
        model.addAttribute("totalPages", taskPage.getTotalPages());

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
        if (task.getCreatedAt() == null) {
            task.setCreatedAt(LocalDateTime.now());
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

    @PostMapping("/{id}/archive")
    public String archiveTask(@PathVariable String id, RedirectAttributes redirectAttributes) {
        Task task = taskService.getById(id);
        task.setArchived(true);
        taskService.save(task);
        redirectAttributes.addFlashAttribute("message", "Task archived successfully.");
        return "redirect:/tasks";
    }

    @GetMapping("/archived")
    public String viewArchivedTasks(@RequestParam(value = "keyword", required = false) String keyword,
                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                    Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Task> taskPage = (keyword != null && !keyword.isEmpty())
                ? taskService.searchArchivedTasks(keyword, pageable)
                : taskService.getArchivedTasks(pageable);

        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("currentPage", taskPage.getNumber());
        model.addAttribute("totalPages", taskPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "task-archived-list";
    }

    @GetMapping("/unarchive/{id}")
    public String unarchiveTask(@PathVariable String id, RedirectAttributes redirectAttributes) {
        taskService.unarchiveTask(id);
        redirectAttributes.addFlashAttribute("message", "Task unarchived successfully.");
        return "redirect:/tasks/archived";
    }
}
