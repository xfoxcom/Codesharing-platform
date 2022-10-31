package platform;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Controller;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Controller
public class ControllerOfCode {


   private final CodeRepository repository;

    public ControllerOfCode(CodeRepository codeRepository) {
        this.repository = codeRepository;
    }

    @GetMapping(value = "/code/{N}", produces = MediaType.TEXT_HTML_VALUE)
    public String getCode(HttpServletResponse response, @PathVariable String N, Model model) {

        response.addHeader("Content-Type", "text/html");

        Code code = repository.findById(N).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (code.isSecret()) {
            long timeToExp = code.getTimeOfExpire().toSecondOfDay() - LocalTime.now().withNano(0).toSecondOfDay();

            if (code.isAllRest()) {
                code.setViews(code.getViews() - 1);
                code.setTime(timeToExp);
            }
            if (code.isOnlyViewsRest()) {
                code.setViews(code.getViews() - 1);
            }
            if (code.isOnlyTimeRest()) {
                code.setTime(timeToExp);
            }

            repository.save(code);

            if (((code.getViews() < 0 | code.isExpired()) & code.isAllRest()) | (code.isOnlyTimeRest() & code.isExpired()) | (code.isOnlyViewsRest() & code.getViews() < 0)) {
                repository.deleteById(N);
            }

        }

        code = repository.findById(N).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("code", code);
        if (code.isAllRest()) return "getSnippet";
        if (code.isOnlyViewsRest()) return "getSnippetWithViewsRestrict";
        if (code.isOnlyTimeRest()) return "getSnippetWithTimeRestriction";
        return "getSnippetWithoutRestrict";
    }

    @GetMapping(value = "/api/code/{N}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
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

    @PostMapping("/api/code/new")
    @ResponseBody
    public ResponseEntity<Map<String, String>> postNewCode(@RequestBody Code code) {

        code.setId();
        code.setDate(LocalDateTime.now());
        code.setExpired(false);
        if (code.getTime() <= 0 & code.getViews() <=0) {
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

    @GetMapping("/code/new")
    public String createCode() {
        return "create";
    }

    @GetMapping("/api/code/latest")
    @ResponseBody
    public List<Code> getLatest() {
       return repository.findAll()
               .stream().
               filter(c -> !c.isSecret())
               .sorted(Comparator.comparing(Code::getDate).reversed())
               .limit(10)
               .collect(Collectors.toList());
    }
    @GetMapping("/code/latest")
    public String getLatestHTML (Model model) {

        List<Code> codes = repository.findAll()
                .stream()
                .filter(c -> !c.isSecret())
                .sorted(Comparator.comparing(Code::getDate).reversed())
                .limit(10)
                .collect(Collectors.toList());

        model.addAttribute("codes", codes);
        return "latest";
    }
}
