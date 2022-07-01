package platform;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;


@RestController
public class Controller {
    private List<Code> codes = List.of(new Code("public static void main(String[] args) {SpringApplication.run(CodeSharingPlatform.class, args);}", LocalDateTime.now().withNano(0)));
    private final String code = "public static void main(String[] args) {SpringApplication.run(CodeSharingPlatform.class, args);}";
    private static Code snippet= new Code("public static void main(String[] args) {SpringApplication.run(CodeSharingPlatform.class, args);}", LocalDateTime.now().withNano(0));

    @GetMapping(value = "/code", produces = MediaType.TEXT_HTML_VALUE)
    public String getCode(HttpServletResponse response) {
        response.addHeader("Content-Type", "text/html");
        return "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Code</title>\n" +
                "\n" +
                "</head>\n" +
                "<body>\n" + "<span id=\"load_date\"> " + snippet.getDate() + "</span><br>" +
                "<pre id=\"code_snippet\">" + snippet.getCode() + "</pre>" + "</body>\n</html>";
    }

    @GetMapping(value = "/api/code", produces = MediaType.APPLICATION_JSON_VALUE)
    public Code getField(HttpServletResponse response) {
        response.addHeader("Content-Type", "application/json");
        return snippet;
    }

    @PostMapping("/api/code/new")
    public ResponseEntity<EmptyJson> postNewCode(@RequestBody Code code) {
        snippet.setCode(code.getCode());
        snippet.setDate(LocalDateTime.now().withNano(0));
        return ResponseEntity.ok(new EmptyJson());
    }

    @GetMapping("/code/new")
    public String createCode() {
        return "<!DOCTYPE html>\n" +
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
                "</html>";
    }
}
