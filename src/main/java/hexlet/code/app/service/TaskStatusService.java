package hexlet.code.app.service;

import hexlet.code.app.dto.TaskStatusDTO;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public final class TaskStatusService {

    private  final TaskStatusRepository repository;

    public List<TaskStatus> getStatuses() {
        return (List<TaskStatus>) repository.findAll();
    }

    public TaskStatus getTaskStatus(final long id) {
        return repository.findById(id).get();
    }

    public TaskStatus createTaskStatus(final TaskStatusDTO dto) {
        final TaskStatus newStatus = TaskStatus
                .builder()
                .name(dto.getName())
                .build();
        return repository.save(newStatus);
    }

    public TaskStatus updateTaskStatus(final long id, final TaskStatusDTO dto) {
        final TaskStatus updatedStatus = repository.findById(id).get();
        updatedStatus.setName(dto.getName());
        return repository.save(updatedStatus);
    }

    public void deleteTaskStatus(final long id) {
        repository.deleteById(id);
    }
}
