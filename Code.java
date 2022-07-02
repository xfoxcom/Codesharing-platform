package platform;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Code {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int id;
    private String code;
    private LocalDateTime date;
}
