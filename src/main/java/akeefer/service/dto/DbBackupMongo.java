package akeefer.service.dto;

import akeefer.model.mongo.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

/**
 * @author Andreas Keefer
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties("aktivitaeten")
public class DbBackupMongo {
    @Singular
    List<User> users;
}
