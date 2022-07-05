package platform;


import org.springframework.beans.factory.annotation.Autowired;
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
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Controller
public class ControllerOfCode {
    private List<Code> codes = new ArrayList<>();
    private static final String DATE_FORMATTER= "yyyy-MM-dd HH:mm:ss";

    @Autowired
    CodeRepository repository;
    public ControllerOfCode(CodeRepository codeRepository) {
        this.repository = codeRepository;
    }
    @GetMapping(value = "/code/{N}", produces = MediaType.TEXT_HTML_VALUE)
    public String getCode(HttpServletResponse response, @PathVariable String N, Model model) {
        response.addHeader("Content-Type", "text/html");
        if (!repository.existsById(N)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Code code = repository.findById(N).get();
        LocalTime time = LocalTime.parse(code.getDate().split("\\s+")[1]);

        if (code.isSecret()) {
            long timeToExp = code.getTimeOfExpire().toSecondOfDay() - LocalTime.now().toSecondOfDay();
            code.setViews(code.getViews() - 1);
            code.setTime(timeToExp);
            repository.save(code);
            if (code.getViews() < 1 | code.isExpired()) {
                repository.deleteById(N);
            }

        }
        if (!repository.existsById(N)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        model.addAttribute("code", repository.findById(N).get());
        return "getSnippet";
    }

    @GetMapping(value = "/api/code/{N}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Code getField(HttpServletResponse response, @PathVariable String N) {
        response.addHeader("Content-Type", "application/json");
        if (repository.existsById(N)) {
            Code code = repository.findById(N).get();
            LocalTime time = LocalTime.parse(code.getDate().split("\\s+")[1]); // time of creation

            if (code.isSecret()) {
                long timeToExp = code.getTimeOfExpire().toSecondOfDay() - LocalTime.now().toSecondOfDay();
                code.setViews(code.getViews() - 1);
                code.setTime(timeToExp);
                repository.save(code);
                if (code.getViews() < 1 | code.isExpired()) {
                    repository.deleteById(N);
                }
            }
            if (repository.existsById(N)) {
                return repository.findById(N).get();
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/api/code/new")
    @ResponseBody
    public ResponseEntity<Map<String, String>> postNewCode(@RequestBody Code code) {

        // TODO: 03.07.2022 delete after test
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }

        code.setId();
        code.setDate(LocalDateTime.now());
        code.setExpired(false);
        if (code.getTime() <= 0 & code.getViews() <=0) {
            code.setSecret(false);
        } else {
            String[] t = code.getDate().split("\\s+");
            code.setSecret(true);
            code.setTimeOfExpire(LocalTime.parse(t[1]).plusSeconds(code.getTime()));
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
       return repository.findAll().stream().filter(c -> !c.isSecret()).sorted(Comparator.comparing(Code::getDate).reversed()).limit(10).collect(Collectors.toList());
    }
    @GetMapping("/code/latest")
    public String getLatestHTML (Model model) {
        model.addAttribute("codes", repository.findAll().stream().filter(c -> !c.isSecret()).sorted(Comparator.comparing(Code::getDate).reversed()).limit(10).collect(Collectors.toList()));
        return "latest";
    }
}
