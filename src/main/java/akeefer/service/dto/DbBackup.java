package akeefer.service.dto;

import akeefer.model.Aktivitaet;
import akeefer.model.User;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * @author Andreas Keefer
 */
public class DbBackup implements Serializable {
    private List<User> users;
    private List<Aktivitaet> aktivitaeten;

    public DbBackup() {
    }

    private DbBackup(Builder builder) {
        setUsers(builder.users);
        setAktivitaeten(builder.aktivitaeten);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(DbBackup copy) {
        Builder builder = new Builder();
        builder.users = copy.users;
        builder.aktivitaeten = copy.aktivitaeten;
        return builder;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Aktivitaet> getAktivitaeten() {
        return aktivitaeten;
    }

    public void setAktivitaeten(List<Aktivitaet> aktivitaeten) {
        this.aktivitaeten = aktivitaeten;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)//
                .append("users", users)//
                .append("aktivitaeten", aktivitaeten)//
                .toString();
    }

    public static final class Builder {
        private List<User> users;
        private List<Aktivitaet> aktivitaeten;

        private Builder() {
        }

        public Builder withUsers(List<User> val) {
            users = val;
            return this;
        }

        public Builder withAktivitaeten(List<Aktivitaet> val) {
            aktivitaeten = val;
            return this;
        }

        public DbBackup build() {
            return new DbBackup(this);
        }
    }

}
