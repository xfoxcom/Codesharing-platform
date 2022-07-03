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
import java.time.format.DateTimeFormatter;
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
    public String getCode(HttpServletResponse response, @PathVariable int N, Model model) {
        response.addHeader("Content-Type", "text/html");

       /* for (Code code1 : codes) {
            if (code1.getId() == N) {
                snippet.setCode(code1.getCode());
                snippet.setDate(code1.getDate());
            }
        }*/

       // model.addAttribute("date", repository.findById(N).get().getDate().format(DateTimeFormatter.ofPattern(DATE_FORMATTER)));
        model.addAttribute("code", repository.findById(N).get());
        return "getSnippet";
    }

    @GetMapping(value = "/api/code/{N}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Code getField(HttpServletResponse response, @PathVariable int N) {
        response.addHeader("Content-Type", "application/json");
      /*  for (Code code1 : codes) {
            if (code1.getId() == N) {
                return code1;
            }
        }*/
        if (repository.existsById(N)) {
            return repository.findById(N).get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/api/code/new")
    @ResponseBody
    public ResponseEntity<Map<String, String>> postNewCode(@RequestBody Code code) {

        code.setDate(LocalDateTime.now());
        repository.save(code);
        return ResponseEntity.ok(Map.of("id", String.valueOf(code.getId())));
    }

    @GetMapping("/code/new")
    public String createCode() {
        return "create";
    }

    @GetMapping("/api/code/latest")
    @ResponseBody
    public List<Code> getLatest() {
        List<Code> latest = new ArrayList<>();

      /*  if (codes.size() <=10) {
            for (int i = codes.size() - 1; i >= 0; i--) {
                latest.add(codes.get(i));
            }
        } else {
            for (int i = codes.size() - 1; i >= codes.size() - 10; i--) {
                latest.add(codes.get(i));
            }
        }
        return latest;*/
       return repository.findAll().stream().sorted(Comparator.comparing(Code::getDate).reversed()).limit(10).collect(Collectors.toList());
    }
    @GetMapping("/code/latest")
    public String getLatestHTML (Model model) {
        List<Code> latest = new ArrayList<>();
      /*  if (codes.size() <=10) {
            for (int i = codes.size() - 1; i >= 0; i--) {
                latest.add(codes.get(i));
            }
        } else {
            for (int i = codes.size() - 1; i >= codes.size() - 10; i--) {
                latest.add(codes.get(i));
            }
        }*/
        //model.addAttribute("date", repository.findAll().stream().sorted(Comparator.comparing(Code::getDate).reversed()).limit(10).map(Code::getDate).map(d -> d.format(DateTimeFormatter.ofPattern(DATE_FORMATTER))).collect(Collectors.toList()));
        model.addAttribute("codes", repository.findAll().stream().sorted(Comparator.comparing(Code::getDate).reversed()).limit(10).collect(Collectors.toList()));
        return "latest";
    }
}
