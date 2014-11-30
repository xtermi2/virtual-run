package akeefer.model;

import com.google.appengine.api.datastore.Key;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class User implements Serializable, Comparable<User> {

    private static final long serialVersionUID = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Key id;
    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private Set<SecurityRole> roles = new HashSet<>(1);
    @ManyToOne
    private Parent parent;
    @NotNull
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Aktivitaet> aktivitaeten = new ArrayList<Aktivitaet>();

    public User() {
    }

    public User(Key id) {
        this.id = id;
    }

    public Key getId() {
        return id;
    }

    public void setId(Key id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<SecurityRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<SecurityRole> role) {
        this.roles = role;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public List<Aktivitaet> getAktivitaeten() {
        return aktivitaeten;
    }

    public void setAktivitaeten(List<Aktivitaet> aktivitaeten) {
        this.aktivitaeten = aktivitaeten;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        User rhs = (User) obj;
        return new EqualsBuilder()
                .append(this.id, rhs.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .toHashCode();
    }

    @Override
    public int compareTo(User o) {
        return id.compareTo(o.getId());
    }

    public User addRole(SecurityRole role) {
        getRoles().add(role);
        return this;
    }
}
