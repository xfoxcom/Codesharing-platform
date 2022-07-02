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
import java.util.stream.Collectors;


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
       // snippet.setCode(code.getCode());
       // snippet.setDate(LocalDateTime.now().withNano(0));
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
        /*"<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Create</title>\n" +
                "    <link href=\"https://fonts.googleapis.com/css2?family=Lato&display=swap\" rel=\"stylesheet\">\n" +
                "</head>\n" +
                "<body>\n" +
                "<textarea style=\"color:red; border-style:dotted; width:500px; height:200px;\" id=\"code_snippet\"> //write your code here </textarea>\n" +
                "<br>\n" + "<script>\n" +
                "    function send() {\n" +
                "    let object = {\n" +
                "        \"code\": document.getElementById(\"code_snippet\").value\n" +
                "    };\n" +
                "\n" +
                "    let json = JSON.stringify(object);\n" +
                "\n" +
                "    let xhr = new XMLHttpRequest();\n" +
                "    xhr.open(\"POST\", '/api/code/new', false)\n" +
                "    xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');\n" +
                "    xhr.send(json);\n" +
                "\n" +
                "    if (xhr.status == 200) {\n" +
                "      alert(\"Success!\");\n" +
                "    }\n" +
                "}\n" +
                "    </script>\n" +
                "<button style=\"width: 130px;\n" +
                "  height: 40px;\n" +
                "  line-height: 42px;\" id=\"send_snippet\" type=\"submit\" onclick=\"send()\">Submit</button>\n" +
                "</body>\n" +
                "</html>";*/
    }

    @GetMapping("/api/code/latest")
    @ResponseBody
    public List<Code> getLatest() {
      //  codes.sort(Comparator.comparing(Code::getDate).reversed());
        List<Code> latest = new ArrayList<>();
        for (int i = codes.size() - 1; i > 0; i--) { // TODO: 02.07.2022 Last changes 
            latest.add(codes.get(i));
        }
        return latest;
    }
    @GetMapping("/code/latest")
    public String getLatestHTML (Model model) {
        model.addAttribute("codes", codes.stream().sorted(Comparator.comparing(Code::getDate)).limit(10).collect(Collectors.toList()));
        return "latest";
    }
}
