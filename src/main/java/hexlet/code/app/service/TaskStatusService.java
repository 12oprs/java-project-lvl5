package hexlet.code.app.service;

import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public final class TaskStatusService {

    @Autowired
    private TaskStatusRepository repository;

    public List<TaskStatus> getStatuses() {
        return (List<TaskStatus>) repository.findAll();
    }

    public TaskStatus getTaskStatus(long id) throws Exception {
        return repository.findById(id).orElseThrow(() -> new Exception("Status not found"));
    }

    public TaskStatus createTaskStatus(String name) {
        TaskStatus newStatus = new TaskStatus(name);
        return repository.save(newStatus);
    }
    public TaskStatus updateTaskStatus(long id, String newName) throws Exception {
        TaskStatus updatedStatus = repository.findById(id)
                .orElseThrow(() -> new Exception("Can't update. Status not found"));
        updatedStatus.setName(newName);
        return repository.save(updatedStatus);
    }

    public String deleteTaskStatus(long id) throws Exception {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return "Status deleted";
        }
        throw new Exception("Can't delete. Status not exist");
    }
}
