package akeefer.web.components.interplay.conditional;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import static akeefer.web.components.interplay.conditional.SimpleConditional.not;

@SuppressWarnings("serial")
public class RolesConditional extends AbstractConditional<Roles> {

    /**
     * Flag, ob im OR oder AND Modus geprüft wird
     */
    private final boolean mode;

    /**
     * zu prüfende Rollen
     */
    private final IModel<Roles> rolesToCheck;

    /**
     * Konstruktor für ein Conditional, das prüft, ob die angegebenen Rollen in {@code rolesToCheck} in {@code roles}
     * hinterlegt sind.
     *
     * @param rolesToCheck Model der zu prüfenden Rollen
     * @param roles        Model mit vorhandenen Rollen
     * @param all          Flag, ob alle oder nur eine der Rollen hinterlegt sein muss
     */
    public RolesConditional(final IModel<Roles> roles, final IModel<Roles> rolesToCheck, boolean all) {
        super(roles);
        this.rolesToCheck = rolesToCheck;
        this.mode = all;
    }

    @Override
    public boolean isFulfilled(Roles roles) {
        if (null != roles) {
            if (null != rolesToCheck) {
                if (!mode) {
                    return roles.hasAnyRole(rolesToCheck.getObject());
                } else {
                    return roles.hasAllRoles(rolesToCheck.getObject());
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void detach() {
        super.detach();
        if (rolesToCheck != null) {
            rolesToCheck.detach();
        }
    }

    /**
     * @param roles Model der zu prüfenden Rollen
     * @return Conditional, das prüft, ob eine der angegebenen Rollen in der Session hinterlegt ist.
     */
    public static IConditional<Roles> hasAnyRole(final IModel<Roles> roles) {
        return hasAnyRole(new SessionRolesModel(), roles);
    }

    /**
     * @param roles        Model mit vorhandenen Rollen
     * @param rolesToCheck Model der zu prüfenden Rollen
     * @return Conditional, das prüft, ob eine der angegebenen Rollen in {@code rolesToCheck} in {@code roles} hinterlegt ist.
     */
    public static IConditional<Roles> hasAnyRole(final IModel<Roles> roles, final IModel<Roles> rolesToCheck) {
        return new RolesConditional(roles, rolesToCheck, false);
    }

    /**
     * @param roles Model mit vorhandenen Rollen
     * @param role  zu prüfende Rolle
     * @return Conditional, das prüft, ob die angegebene Rolle in {@code roles} hinterlegt ist.
     */
    public static IConditional<Roles> hasRole(IModel<Roles> roles, String role) {
        return hasAllRoles(roles, new Roles(role));
    }

    /**
     * @param role zu prüfende Rolle
     * @return Conditional, das prüft, ob die angegebene Rolle in der Session hinterlegt ist.
     */
    public static IConditional<Roles> hasRole(IModel<String> role) {
        return hasRole(new SessionRolesModel(), role);
    }

    /**
     * @param role zu prüfende Rolle
     * @return Conditional, das prüft, ob die angegebene Rolle in der Session hinterlegt ist.
     */
    public static IConditional<Roles> hasRole(String role) {
        return hasAllRoles(new SessionRolesModel(), new Roles(role));
    }

    /**
     * @param roles Model mit vorhandenen Rollen
     * @param role  zu prüfende Rolle
     * @return Conditional, das prüft, ob die angegebene Rolle in {@code roles} hinterlegt ist.
     */
    public static IConditional<Roles> hasRole(IModel<Roles> roles, IModel<String> role) {
        return hasAllRoles(roles, new RolesFromStringModel(role));
    }

    /**
     * @param roles Model der zu prüfenden Rollen
     * @return Conditional, das prüft, ob alle der angegebenen Rollen in der Session hinterlegt sind.
     */
    public static IConditional<Roles> hasAllRoles(final IModel<Roles> roles) {
        return hasAllRoles(new SessionRolesModel(), roles);
    }

    /**
     * @param roles        Model mit vorhandenen Rollen
     * @param rolesToCheck Model der zu prüfenden Rollen
     * @return Conditional, das prüft, ob alle der angegebenen Rollen in {@code rolesToCheck} in {@code roles} hinterlegt sind.
     */
    public static IConditional<Roles> hasAllRoles(final IModel<Roles> roles, final IModel<Roles> rolesToCheck) {
        return new RolesConditional(roles, rolesToCheck, true);
    }

    /**
     * @param roles Model der zu prüfenden Rollen
     * @return Conditional, das prüft, ob keine der angegebenen Rollen in der Session hinterlegt ist.
     */
    public static IConditional<Roles> hasNoRoles(final IModel<Roles> roles) {
        return hasNoRoles(new SessionRolesModel(), roles);
    }

    /**
     * @param rolesToCheck Model der zu prüfenden Rollen
     * @param roles        Model mit vorhandenen Rollen
     * @return Conditional, das prüft, ob keine der angegebenen Rollen in {@code rolesToCheck} in {@code roles} hinterlegt ist.
     */
    public static IConditional<Roles> hasNoRoles(final IModel<Roles> roles, final IModel<Roles> rolesToCheck) {
        return not(new RolesConditional(roles, rolesToCheck, false));
    }

    /**
     * @param roles Model der zu prüfenden Rollen
     * @return Conditional, das prüft, ob nicht alle der angegebenen Rollen in der Session hinterlegt sind.
     */
    public static IConditional<Roles> hasNotAllRoles(final IModel<Roles> roles) {
        return hasNotAllRoles(new SessionRolesModel(), roles);
    }

    /**
     * @param rolesToCheck Model der zu prüfenden Rollen
     * @param roles        Model mit vorhandenen Rollen
     * @return Conditional, das prüft, ob nicht alle der angegebenen Rollen in {@code rolesToCheck} in {@code roles} hinterlegt sind.
     */
    public static IConditional<Roles> hasNotAllRoles(final IModel<Roles> roles, final IModel<Roles> rolesToCheck) {
        return not(new RolesConditional(roles, rolesToCheck, true));
    }

    /**
     * @param roles zu prüfende Rollen
     * @return Conditional, das prüft, ob eine der angegebenen Rollen in der Session hinterlegt ist.
     */
    public static IConditional<Roles> hasAnyRole(String... roles) {
        return hasAnyRole(new Roles(roles));
    }

    /**
     * @param roles zu prüfende Rollen
     * @return Conditional, das prüft, ob eine der angegebenen Rollen in der Session hinterlegt ist.
     */
    public static IConditional<Roles> hasAnyRole(Roles roles) {
        return hasAnyRole(new Model<Roles>(roles));
    }

    /**
     * @param rolesToCheck zu prüfende Rollen
     * @param roles        Model mit vorhandenen Rollen
     * @return Conditional, das prüft, ob eine der angegebenen Rollen in {@code rolesToCheck} in {@code roles} hinterlegt ist.
     */
    public static IConditional<Roles> hasAnyRole(IModel<Roles> roles, String... rolesToCheck) {
        return hasAnyRole(roles, new Roles(rolesToCheck));
    }

    /**
     * @param rolesToCheck zu prüfende Rollen
     * @param roles        Model mit vorhandenen Rollen
     * @return Conditional, das prüft, ob eine der angegebenen Rollen in {@code rolesToCheck} in {@code roles} hinterlegt ist.
     */
    public static IConditional<Roles> hasAnyRole(IModel<Roles> roles, Roles rolesToCheck) {
        return hasAnyRole(roles, new Model<Roles>(rolesToCheck));
    }

    /**
     * @param roles zu prüfende Rollen
     * @return Conditional, das prüft, ob alle der angegebenen Rollen in der Session hinterlegt sind.
     */
    public static IConditional<Roles> hasAllRoles(String... roles) {
        return hasAllRoles(new Roles(roles));
    }

    /**
     * @param roles zu prüfende Rollen
     * @return Conditional, das prüft, ob alle der angegebenen Rollen in der Session hinterlegt sind.
     */
    public static IConditional<Roles> hasAllRoles(Roles roles) {
        return hasAllRoles(new Model<Roles>(roles));
    }

    /**
     * @param rolesToCheck zu prüfende Rollen
     * @param roles        Model mit vorhandenen Rollen
     * @return Conditional, das prüft, ob alle der angegebenen Rollen in {@code rolesToCheck} in {@code roles} hinterlegt sind.
     */
    public static IConditional<Roles> hasAllRoles(IModel<Roles> roles, String... rolesToCheck) {
        return hasAllRoles(roles, new Roles(rolesToCheck));
    }

    /**
     * @param rolesToCheck zu prüfende Rollen
     * @param roles        Model mit vorhandenen Rollen
     * @return Conditional, das prüft, ob alle der angegebenen Rollen in {@code rolesToCheck} in {@code roles} hinterlegt sind.
     */
    public static IConditional<Roles> hasAllRoles(IModel<Roles> roles, Roles rolesToCheck) {
        return hasAllRoles(roles, new Model<Roles>(rolesToCheck));
    }

    /**
     * @param rolesToCheck zu prüfende Rollen
     * @param roles        Model mit vorhandenen Rollen
     * @return Conditional, das prüft, dass keine der angegebenen Rollen in {@code rolesToCheck} in {@code roles} hinterlegt ist.
     */
    public static IConditional<Roles> hasNoRoles(IModel<Roles> roles, String... rolesToCheck) {
        return hasNoRoles(roles, new Roles(rolesToCheck));
    }

    /**
     * @param rolesToCheck zu prüfende Rollen
     * @param roles        Model mit vorhandenen Rollen
     * @return Conditional, das prüft, dass keine der angegebenen Rollen in {@code rolesToCheck} in {@code roles} hinterlegt ist.
     */
    public static IConditional<Roles> hasNoRoles(IModel<Roles> roles, Roles rolesToCheck) {
        return hasNoRoles(roles, new Model<Roles>(rolesToCheck));
    }

    /**
     * @param roles zu prüfende Rollen
     * @return Conditional, das prüft, dass keine der angegebenen Rollen in der Session hinterlegt ist.
     */
    public static IConditional<Roles> hasNoRoles(String... roles) {
        return hasNoRoles(new Roles(roles));
    }

    /**
     * @param roles zu prüfende Rollen
     * @return Conditional, das prüft, dass keine der angegebenen Rollen in der Session hinterlegt ist.
     */
    public static IConditional<Roles> hasNoRoles(Roles roles) {
        return hasNoRoles(new Model<Roles>(roles));
    }

    /**
     * @param roles zu prüfende Rollen
     * @return Conditional, das prüft, ob nicht alle der angegebenen Rollen in der Session hinterlegt sind.
     */
    public static IConditional<Roles> hasNotAllRoles(String... roles) {
        return hasNotAllRoles(new Roles(roles));
    }

    /**
     * @param roles zu prüfende Rollen
     * @return Conditional, das prüft, ob nicht alle der angegebenen Rollen in der Session hinterlegt sind.
     */
    public static IConditional<Roles> hasNotAllRoles(Roles roles) {
        return hasNotAllRoles(new Model<Roles>(roles));
    }

    /**
     * @param rolesToCheck zu prüfende Rollen
     * @param roles        Model mit vorhandenen Rollen
     * @return Conditional, das prüft, ob nicht alle der angegebenen Rollen in {@code rolesToCheck} in {@code roles} hinterlegt sind.
     */
    public static IConditional<Roles> hasNotAllRoles(IModel<Roles> roles, String... rolesToCheck) {
        return hasNotAllRoles(roles, new Roles(rolesToCheck));
    }

    /**
     * @param rolesToCheck zu prüfende Rollen
     * @param roles        Model mit vorhandenen Rollen
     * @return Conditional, das prüft, ob nicht alle der angegebenen Rollen in {@code rolesToCheck} in {@code roles} hinterlegt sind.
     */
    public static IConditional<Roles> hasNotAllRoles(IModel<Roles> roles, Roles rolesToCheck) {
        return hasNotAllRoles(roles, new Model<Roles>(rolesToCheck));
    }

    private static final class SessionRolesModel extends AbstractReadOnlyModel<Roles> {
        @Override
        public Roles getObject() {
            return AuthenticatedWebSession.get().getRoles();
        }
    }

    private static final class RolesFromStringModel extends LoadableDetachableModel<Roles> {

        private final IModel<String> model;

        public RolesFromStringModel(IModel<String> model) {
            this.model = model;
        }

        @Override
        public Roles load() {
            if (model != null) {
                return new Roles(model.getObject());
            } else {
                return null;
            }
        }

        @Override
        public void setObject(Roles object) {
            if (model != null) {
                model.setObject(object.toString());
            }
        }

        @Override
        public void detach() {
            super.detach();
            if (model != null) {
                model.detach();
            }
        }
    }
}
