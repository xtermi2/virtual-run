package akeefer.service.dto;

import akeefer.model.mongo.Aktivitaet;
import akeefer.model.mongo.User;
import lombok.*;

import java.util.List;

/**
 * @author Andreas Keefer
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DbBackupMongo {
    @Singular
    List<User> users;
    @Singular("aktivitaet")
    List<Aktivitaet> aktivitaeten;
}
