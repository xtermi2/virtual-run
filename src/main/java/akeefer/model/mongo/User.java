package akeefer.model.mongo;

import akeefer.model.BenachrichtigunsIntervall;
import akeefer.model.SecurityRole;
import lombok.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Document(collection = "users")
@Data
@ToString(exclude = "password")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @EqualsAndHashCode.Include
    private String id;

    @NotNull
    @NonNull
    @NotEmpty
    private String username;

    @NotNull
    @NonNull
    @NotEmpty
    private String password;

    @NotNull
    @NonNull
    @NotEmpty
    @Singular
    private Set<SecurityRole> roles;

    @Size(min = 1)
    private String nickname;

    @Email
    private String email;

    @NotNull
    @NonNull
    @Builder.Default
    private BenachrichtigunsIntervall benachrichtigunsIntervall = BenachrichtigunsIntervall.deaktiviert;

    private boolean includeMeInStatisticMail;

    @Transient
    public String getAnzeigename() {
        return null == nickname ? username : nickname;
    }
}
