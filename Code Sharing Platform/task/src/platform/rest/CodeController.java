package platform.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import platform.entity.Code;
import platform.repository.CodeRepository;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/code")
public class CodeController {

    private final CodeRepository repository;

    @GetMapping(value = "/{N}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Code getField(HttpServletResponse response, @PathVariable String N) {
        response.addHeader("Content-Type", "application/json");

        Code code = repository.findById(N).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (code.isSecret()) {
            long timeToExp = code.getTimeOfExpire().toSecondOfDay() - LocalTime.now().withNano(0).toSecondOfDay();
            if (code.getViews() > 0) code.setViews(code.getViews() - 1);
            if (code.getTime() > 0) code.setTime(timeToExp);
            repository.save(code);
            if ((code.getViews() < 1 & code.isExpired() & code.isAllRest()) | (code.isOnlyTimeRest() & code.isExpired()) | (code.isOnlyViewsRest() & code.getViews() < 1)) {
                repository.deleteById(N);
            }
        }
        return repository.findById(N).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/latest")
    public List<Code> getLatest() {
        return repository.findAll()
                .stream().
                filter(c -> !c.isSecret())
                .sorted(Comparator.comparing(Code::getDate).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    @PostMapping("/new")
    public ResponseEntity<Map<String, String>> postNewCode(@RequestBody Code code) {

        code.setId();
        code.setDate(LocalDateTime.now());
        code.setExpired(false);
        if (code.getTime() <= 0 & code.getViews() <= 0) {
            code.setSecret(false);
        } else {
            String[] t = code.getDate().split("\\s+");
            code.setSecret(true);
            code.setTimeOfExpire(LocalTime.parse(t[1]).plusSeconds(code.getTime()));
            if (code.getViews() > 0 & code.getTime() > 0) code.setAllRest(true);
            if (code.getTime() == 0 & code.getViews() > 0) code.setOnlyViewsRest(true);
            if (code.getViews() == 0 & code.getTime() > 0) code.setOnlyTimeRest(true);
        }
        repository.save(code);
        return ResponseEntity.ok(Map.of("id", code.getId()));
    }
}
