package hexlet.code.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.service.LabelService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping(value = "${base-url}" + "/labels")
public class LabelController {

    @Autowired
    private LabelService service;

    @Autowired
    private LabelRepository repository;

    @Autowired
    private ObjectMapper mapper;

    private static final String ONLY_AUTHORIZED = """
                authentication.isAuthenticated()
            """;

    @GetMapping
    public List<Label> getLabels() {
        return service.getLabels();
    }

    @GetMapping("/{id}")
    public Label getLabel(@PathVariable long id) {
        Label label = null;
        try {
            label = service.getLabel(id);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        }
        return label;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(ONLY_AUTHORIZED)
    public Label createLabel(@RequestBody String json) {
        String name = null;
        try {
            name = (String) mapper.readValue(json, Map.class).get("name");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return service.createLabel(name);
    }

    @PutMapping("/{id}")
    @PreAuthorize(ONLY_AUTHORIZED)
    public Label updateLabel(@PathVariable long id, @RequestBody String json) throws Exception {
        String name = null;
        try {
            name = (String) mapper.readValue(json, Map.class).get("name");
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        }
        return service.updateLabel(id, name);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(ONLY_AUTHORIZED)
    public String deleteLabel(@PathVariable long id) {
        try {
            return service.deleteLabel(id);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, e.getMessage(), e);
        }
    }
}
