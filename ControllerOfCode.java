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
import java.util.*;



@Controller
public class ControllerOfCode {
    private List<Code> codes = new ArrayList<>();
    private final String code = "public static void main(String[] args) {SpringApplication.run(CodeSharingPlatform.class, args);}";
    private static Code snippet= new Code(1,"public static void main(String[] args) {SpringApplication.run(CodeSharingPlatform.class, args);}", LocalDateTime.now().withNano(0));

    @GetMapping(value = "/code/{N}", produces = MediaType.TEXT_HTML_VALUE)
    public String getCode(HttpServletResponse response, @PathVariable int N, Model model) {
        response.addHeader("Content-Type", "text/html");

        for (Code code1 : codes) {
            if (code1.getId() == N) {
                snippet.setCode(code1.getCode());
                snippet.setDate(code1.getDate());
            }
        }
        model.addAttribute("code", snippet);
        return "getSnippet";
    }

    @GetMapping(value = "/api/code/{N}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Code getField(HttpServletResponse response, @PathVariable int N) {
        response.addHeader("Content-Type", "application/json");
        for (Code code1 : codes) {
            if (code1.getId() == N) {
                return code1;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/api/code/new")
    @ResponseBody
    public ResponseEntity<Map<String, String>> postNewCode(@RequestBody Code code) {
        int id;
        code.setDate(LocalDateTime.now().withNano(0));
        if (codes.isEmpty()) {
            id = 1;
        } else {
            id = codes.size() + 1;
        }
        code.setId(id);
        codes.add(code);
        return ResponseEntity.ok(Map.of("id", String.valueOf(id)));
    }

    @GetMapping("/code/new")
    public String createCode() {
        return "create";
    }

    @GetMapping("/api/code/latest")
    @ResponseBody
    public List<Code> getLatest() {
        List<Code> latest = new ArrayList<>();
        if (codes.size() <=10) {
            for (int i = codes.size() - 1; i >= 0; i--) {
                latest.add(codes.get(i));
            }
        } else {
            for (int i = codes.size() - 1; i >= codes.size() - 10; i--) {
                latest.add(codes.get(i));
            }
        }
        return latest;
    }
    @GetMapping("/code/latest")
    public String getLatestHTML (Model model) {
        List<Code> latest = new ArrayList<>();
        if (codes.size() <=10) {
            for (int i = codes.size() - 1; i >= 0; i--) {
                latest.add(codes.get(i));
            }
        } else {
            for (int i = codes.size() - 1; i >= codes.size() - 10; i--) {
                latest.add(codes.get(i));
            }
        }
        model.addAttribute("codes", latest);
        return "latest";
    }
}
