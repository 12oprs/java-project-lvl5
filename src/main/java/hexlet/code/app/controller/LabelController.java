package hexlet.code.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping(value = "${base-url}" + "/labels")
public class LabelController {

    @Autowired
    private LabelService service;

//    @Autowired
//    private LabelRepository repository;

    @Autowired
    private ObjectMapper mapper;

    private static final String ONLY_AUTHORIZED = """
                authentication.isAuthenticated()
            """;

    @Operation(summary = "Get labels")
    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Labels found",
            content = @Content(schema = @Schema(implementation = Label.class)))
    )
    @GetMapping
    public Iterable<Label> getLabels() {
        return service.getLabels();
    }

    @Operation(summary = "Get label by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label found"),
            @ApiResponse(responseCode = "404", description = "Label not found")
    })
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

    @Operation(summary = "Create label")
    @ApiResponse(responseCode = "201", description = "Label created")
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

    @Operation(summary = "Update label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label updated"),
            @ApiResponse(responseCode = "404", description = "Can't update. Label not found")
    })
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

    @Operation(summary = "Delete label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label deleted"),
            @ApiResponse(responseCode = "404", description = "Can't delete. Label not exist")
    })
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
