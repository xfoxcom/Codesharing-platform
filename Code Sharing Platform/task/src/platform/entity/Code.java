package platform.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Code {
    private static final String DATE_FORMATTER = "yyyy-MM-dd HH:mm:ss";
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Id
    private String id;
    private String code;
    private String date;
    private long time;
    private int views;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean isSecret;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean isExpired;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LocalTime timeOfExpire;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean onlyTimeRest;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean onlyViewsRest;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean allRest;

    public void setDate(LocalDateTime time) {
        this.date = time.format(DateTimeFormatter.ofPattern(DATE_FORMATTER));
    }

    public void setId() {
        UUID uuid = UUID.randomUUID();
        this.id = uuid.toString();
    }

    public boolean isExpired() {
        if (isSecret) {
            return time <= 0;
        }
        return false;
    }
}
