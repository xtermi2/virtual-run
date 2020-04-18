package akeefer.model.mongo;

import akeefer.model.BenachrichtigunsIntervall;
import akeefer.model.SecurityRole;
import lombok.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Document(collection = "users")
@Data
@ToString(exclude = "password")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable, Comparable<User> {
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
    private Set<SecurityRole> roles = new HashSet<>(1);

    @Size(min = 1)
    private String nickname;

    @Email
    private String email;

    @NotNull
    @NonNull
    @Builder.Default
    private BenachrichtigunsIntervall benachrichtigunsIntervall = BenachrichtigunsIntervall.deaktiviert;

    private boolean includeMeInStatisticMail;

    public User(String id) {
        this.id = id;
    }

    public User(UUID id) {
        this.id = id.toString();
    }

    @Transient
    public String getAnzeigename() {
        return null == nickname ? username : nickname;
    }

    @Override
    public int compareTo(User o) {
        return id.compareTo(o.getId());
    }

}
