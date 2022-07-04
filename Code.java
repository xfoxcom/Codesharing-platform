package platform;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Code {
    private static final String DATE_FORMATTER= "yyyy-MM-dd HH:mm:ss";
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

    public void setDate(LocalDateTime time) {
        this.date = time.format(DateTimeFormatter.ofPattern(DATE_FORMATTER));
    }

    public void setId() {
        UUID uuid = UUID.randomUUID();
        this.id = uuid.toString();
    }


    public boolean isExpired() {
        if (isSecret) {
           String[] t = date.split("\\s+");
           LocalTime localTime = LocalTime.parse(t[1]);
            if (LocalTime.now().isAfter(localTime.plusSeconds(time))) {
                return true;
            }
        }
        return false;
    }
}
