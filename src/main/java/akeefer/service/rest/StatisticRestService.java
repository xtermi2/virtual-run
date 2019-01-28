package akeefer.service.rest;

import akeefer.model.Aktivitaet;
import akeefer.model.BenachrichtigunsIntervall;
import akeefer.model.SecurityRole;
import akeefer.model.User;
import akeefer.service.PersonService;
import akeefer.service.dto.DbBackup;
import akeefer.web.VRSession;
import akeefer.web.WicketApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.crypt.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.ResourcePath;
import org.wicketstuff.rest.annotations.parameters.RequestBody;
import org.wicketstuff.rest.contenthandling.RestMimeTypes;
import org.wicketstuff.rest.resource.gson.GsonRestResource;
import org.wicketstuff.rest.utils.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@ResourcePath("/rest")
public class StatisticRestService extends GsonRestResource {

    private static final Logger logger = LoggerFactory.getLogger(StatisticRestService.class);

    @SpringBean
    private PersonService personService;

    public StatisticRestService() {
        Injector.get().inject(this);
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        DateFormat df = new SimpleDateFormat("MMM d, yyyy h:mm:ss a");
        OBJECT_MAPPER.setDateFormat(df);
    }


    @MethodMapping(value = "/statistic/{intervall}", httpMethod = HttpMethod.GET)
    public void get(String intervall) {
        try {
            boolean isLocalMode = WicketApplication.isLocalMode();
            HttpServletRequest request = ((HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest());

            String appEngineCronFlag = request.getHeader("X-AppEngine-Cron");
            logger.info("X-AppEngine-Cron=" + appEngineCronFlag);

            if (isLocalMode || "true".equalsIgnoreCase(appEngineCronFlag)) {
                personService.sendStatisticMail(BenachrichtigunsIntervall.valueOf(intervall));
            } else {
                logger.warn("Request wird nicht ausgefuehrt, da er nicht lokal oder vom GAE-Cron kommt");
            }
        } catch (Exception e) {
            logger.warn("Fehler bei sendStatisticMail: " + e.getMessage(), e);
        }
    }

    @MethodMapping(value = "/ping", httpMethod = HttpMethod.GET)
    public String ping() {
        logger.debug("ping");
        return "pong";
    }

    @MethodMapping(value = "/backup/export", httpMethod = HttpMethod.GET)
    public DbBackup backupExport() {
        try {
            User currentUser = VRSession.get().getUser();
            if (null == currentUser) {
                setResponseStatusCode(401);
                logger.warn("unauthorized users are not allowed to create a backup");
                return null;
            }
            if (!currentUser.getRoles().contains(SecurityRole.ADMIN)) {
                setResponseStatusCode(403);
                logger.warn("currentUser({}) not in required role", currentUser.getUsername());
                return null;
            }
            logger.info("create backup [currentUser={}]", currentUser.getUsername());
            return personService.createBackup();
        } catch (Exception e) {
            setResponseStatusCode(500);
            logger.warn("error while creating backup", e);
            return null;
        }
    }

    @MethodMapping(value = "/backup/export/{username}", httpMethod = HttpMethod.GET)
    public DbBackup backupExport(String username) {
        try {
            User currentUser = VRSession.get().getUser();
            if (null == currentUser) {
                setResponseStatusCode(401);
                logger.warn("unauthorized users are not allowed to create a backup");
                return null;
            }
            if (!currentUser.getRoles().contains(SecurityRole.ADMIN)) {
                setResponseStatusCode(403);
                logger.warn("currentUser({}) not in required role", currentUser.getUsername());
                return null;
            }
            logger.info("create backup [currentUser={}]", currentUser.getUsername());
            return personService.createBackup();
        } catch (Exception e) {
            setResponseStatusCode(500);
            logger.warn("error while creating backup", e);
            return null;
        }
    }

    @MethodMapping(value = "/backup/import", httpMethod = HttpMethod.POST, consumes = RestMimeTypes.TEXT_PLAIN)
    public void backupImport(@RequestBody String text) {
        try {
            logger.info("import backup...");
            logger.info(text);

            byte[] decodedBytes = Base64.decodeBase64(text + "=");
            String decoded = StringUtils.newStringUtf8(decodedBytes);
            logger.info("decoded=" + decoded);

            setResponseStatusCode(importBackup(parse(decoded)));
        } catch (Exception e) {
            setResponseStatusCode(500);
            logger.warn("error while importing backup", e);
        }
    }

    private int importBackup(DbBackup dbBackup) {
        int res = HttpStatus.OK.value();
        if (dbBackup != null) {
            Collection<String> existingUsernames = Collections2.transform(personService.getAllUser(), new Function<User, String>() {
                @Override
                public String apply(User input) {
                    return input.getUsername();
                }
            });
            final Map<String, User> usersInDbMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(dbBackup.getUsers())) {
                logger.info("importing users...");
                for (User user : dbBackup.getUsers()) {
                    if (!existingUsernames.contains(user.getUsername())) {
                        user.setId(null);
                        User userInDb = personService.createUserIfAbsent(user, true);
                        usersInDbMap.put(userInDb.getUsername(), userInDb);
                        res = HttpStatus.CREATED.value();
                    }
                }
                logger.info("{} users imported", usersInDbMap.size());
            }
            if (CollectionUtils.isNotEmpty(dbBackup.getAktivitaeten())) {
                int importCounter = 0;
                logger.info("importing activities...");
                for (Aktivitaet akt : dbBackup.getAktivitaeten()) {
                    if (!existingUsernames.contains(akt.getOwner())) {
                        User userInDb = usersInDbMap.containsKey(akt.getOwner()) //
                                ? usersInDbMap.get(akt.getOwner())//
                                : personService.getUserByUsername(akt.getOwner());
                        Assert.notNull(userInDb, "no user found in DB with username '" + akt.getOwner() + "' [" + akt + "]");
                        akt.setId(null);
                        personService.createAktivitaet(akt, userInDb, false);
                        res = HttpStatus.CREATED.value();
                        importCounter++;
                    }
                }
                logger.info("{} activities imported", importCounter);
            }
        }

        return res;
    }

    static DbBackup parse(String json) throws IOException {
        return OBJECT_MAPPER.readValue(json, DbBackup.class);
    }
}
