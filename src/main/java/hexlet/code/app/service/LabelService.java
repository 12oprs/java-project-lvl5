package hexlet.code.app.service;

import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@AllArgsConstructor
public final class LabelService {

    @Autowired
    private LabelRepository repository;

    public Iterable<Label> getLabels() {
        return repository.findAll();
    }
    public Set<Label> getLabels(Set<Long> labelIds) {
        return repository.findAllByIdIn(labelIds);
    }

    public Label getLabel(long id) throws Exception {
        return repository.findById(id).orElseThrow(() -> new Exception("Label not found"));
    }

    public Label createLabel(String name) {
        Label newLabel = new Label(name);
        return repository.save(newLabel);
    }

    public Label updateLabel(long id, String newName) throws Exception {
        Label updatedLabel = repository.findById(id)
                .orElseThrow(() -> new Exception("Can't update. Label not found"));
        updatedLabel.setName(newName);
        return repository.save(updatedLabel);
    }

    public String deleteLabel(long id) throws Exception {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return "Label deleted";
        }
        throw new Exception("Can't delete. Label not exist");
    }
}
