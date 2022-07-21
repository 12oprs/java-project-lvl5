package hexlet.code.app.service;

import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.model.Task;
import hexlet.code.app.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public final class TaskService {

    @Autowired
    private TaskRepository repository;

    public List<Task> getTasks() {
        return (List<Task>) repository.findAll();
    }

    public Task getTask(long id) throws Exception {
        return repository.findById(id).orElseThrow(() -> new Exception("Task not found"));
    }

    public Task createTask(TaskDTO dto) {
        Task newTask = new Task(dto);
        return repository.save(newTask);
    }
    public Task updateTask(long id, TaskDTO dto) throws Exception {
        Task updatedTask = repository.findById(id).orElseThrow(() -> new Exception("Can't update. Task not found"));
        updatedTask.setName(dto.getName());
        updatedTask.setDescription(dto.getDescription());
        updatedTask.setTaskStatus(dto.getStatus());
        updatedTask.setAuthor(dto.getAuthor());
        updatedTask.setExecutor(dto.getExecutor());
        return repository.save(updatedTask);
    }

    public String deleteTask(long id) throws Exception {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return "Task deleted";
        }
        throw new Exception("Can't delete. Task not exist");
    }
}
