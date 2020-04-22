package akeefer.service.rest;

import akeefer.model.BenachrichtigunsIntervall;
import akeefer.model.SecurityRole;
import akeefer.model.mongo.User;
import akeefer.service.PersonService;
import akeefer.service.dto.DbBackupMongo;
import akeefer.service.impl.ImportService;
import akeefer.web.VRSession;
import akeefer.web.WicketApplication;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.ResourcePath;
import org.wicketstuff.rest.annotations.parameters.HeaderParam;
import org.wicketstuff.rest.annotations.parameters.RequestBody;
import org.wicketstuff.rest.contenthandling.json.objserialdeserial.JacksonObjectSerialDeserial;
import org.wicketstuff.rest.contenthandling.json.webserialdeserial.JsonWebSerialDeserial;
import org.wicketstuff.rest.resource.AbstractRestResource;
import org.wicketstuff.restutils.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;

@ResourcePath("/rest")
public class StatisticRestService extends AbstractRestResource<JsonWebSerialDeserial> {

    private static final Logger logger = LoggerFactory.getLogger(StatisticRestService.class);

    @SpringBean
    private PersonService personService;
    @SpringBean
    private ImportService importService;

    public StatisticRestService() {
        super(new JsonWebSerialDeserial(new JacksonObjectSerialDeserial(OBJECT_MAPPER)));
        Injector.get().inject(this);
    }

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        OBJECT_MAPPER.setDateFormat(df);

        SimpleModule module = new SimpleModule();
        module.addSerializer(Key.class, new KeySerializer());
        module.addDeserializer(Key.class, new KeyDeserializer());
        OBJECT_MAPPER.registerModule(module);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
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
    public DbBackupMongo backupExport(@HeaderParam(value = "Authorization", required = false) String authorizationHeader) {
        try {
            User currentUser = VRSession.get().getUser();
            if (null == currentUser) {
                currentUser = getUserFromHeader(authorizationHeader);
            }
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
    public DbBackupMongo backupExport(String username,
                                      @HeaderParam(value = "Authorization", required = false) String authorizationHeader) {
        try {
            User currentUser = VRSession.get().getUser();
            if (null == currentUser) {
                currentUser = getUserFromHeader(authorizationHeader);
            }
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
            return personService.createBackup(username);
        } catch (Exception e) {
            setResponseStatusCode(500);
            logger.warn("error while creating backup", e);
            return null;
        }
    }

    @MethodMapping(value = "/backup/import", httpMethod = HttpMethod.POST)
    public void backupImport(@RequestBody DbBackupMongo data,
                             @HeaderParam(value = "Authorization", required = false) String authorizationHeader) {
        try {
            User currentUser = VRSession.get().getUser();
            if (null == currentUser) {
                currentUser = getUserFromHeader(authorizationHeader);
            }
            if (null == currentUser) {
                setResponseStatusCode(401);
                logger.warn("unauthorized users are not allowed to create a backup");
                return;
            }
            if (!currentUser.getRoles().contains(SecurityRole.ADMIN)) {
                setResponseStatusCode(403);
                logger.warn("currentUser({}) not in required role", currentUser.getUsername());
                return;
            }
            logger.debug("import backup: {}", data);
            setResponseStatusCode(importService.importData(data));
        } catch (Exception e) {
            setResponseStatusCode(500);
            logger.warn("error while importing backup", e);
        }
    }

    private User getUserFromHeader(String authorizationHeader) {
        if (null != authorizationHeader && authorizationHeader.toLowerCase().startsWith("basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorizationHeader.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            String username = values[0];
            String password = values[1];
            if (VRSession.get().authenticate(username, password)) {
                return VRSession.get().getUser();
            }
        }
        return null;
    }

    @Deprecated
    public static class KeySerializer extends StdSerializer<Key> {
        public KeySerializer() {
            super(Key.class);
        }

        @Override
        public void serialize(Key value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeString(KeyFactory.keyToString(value));
        }
    }

    @Deprecated
    public static class KeyDeserializer extends StdDeserializer<Key> {
        public KeyDeserializer() {
            super(Key.class);
        }

        @Override
        public Key deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            String valueAsString = p.getValueAsString();
            return KeyFactory.stringToKey(valueAsString);
        }
    }
}
