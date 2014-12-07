package akeefer.model;

import com.google.appengine.api.datastore.Key;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Parent implements Serializable {

    private static final long serialVersionUID = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // hier muss man einen Key verwenden, da ein Eingebetteter Typ (User#aktivitaeten) nicht mit einem Long als PK funktioniert
    private Key id;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<User>();

    public Key getId() {
        return id;
    }

    public void setId(Key id) {
        this.id = id;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
