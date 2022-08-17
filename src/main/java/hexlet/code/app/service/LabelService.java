package hexlet.code.app.service;

import hexlet.code.app.dto.LabelDTO;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public final class LabelService {

    private final LabelRepository repository;

    public Iterable<Label> getLabels() {
        return repository.findAll();
    }

    public Set<Label> getLabels(final Set<Long> labelIds) {
        return repository.findAllByIdIn(labelIds);
    }

    public Label getLabel(final long id) {
        return repository.findById(id).get();
    }

    public Label createLabel(final LabelDTO dto) {
        final Label newLabel = Label.builder()
                .name(dto.getName())
                .build();
        return repository.save(newLabel);
    }

    public Label updateLabel(final long id, final LabelDTO dto) {
        final Label updatedLabel = repository.findById(id).get();
        updatedLabel.setName(dto.getName());
        return repository.save(updatedLabel);
    }

    public String deleteLabel(final long id) {
        repository.deleteById(id);
        return "Label deleted";
    }
}
