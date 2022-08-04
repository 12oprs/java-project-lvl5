package hexlet.code.app.repository;

import hexlet.code.app.model.Label;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface LabelRepository extends CrudRepository<Label, Long> {
    Optional<Label> findByName(String name);

    Set<Label> findAllByIdIn(Set<Long> labelIds);
}
