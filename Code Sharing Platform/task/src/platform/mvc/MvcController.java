package platform.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import platform.entity.Code;
import platform.repository.CodeRepository;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MvcController {

    private final CodeRepository repository;

    @GetMapping(value = "/code/{N}", produces = MediaType.TEXT_HTML_VALUE)
    public String getCode(HttpServletResponse response, @PathVariable String N, Model model) {

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

    @GetMapping("/code/new")
    public String createCode() {
        return "create";
    }

    @GetMapping("/code/latest")
    public String getLatestHTML(Model model) {

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
