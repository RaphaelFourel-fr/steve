package de.rwth.idsg.steve;

import de.rwth.idsg.steve.ocpp.ws.custom.WsSessionSelectStrategyEnum;
import de.rwth.idsg.steve.utils.PropertiesFileLoader;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.util.TimeZone;

import static de.rwth.idsg.steve.SteveConfiguration.Auth;
import static de.rwth.idsg.steve.SteveConfiguration.DB;
import static de.rwth.idsg.steve.SteveConfiguration.Jetty;
import static de.rwth.idsg.steve.SteveConfiguration.Ocpp;
import static de.rwth.idsg.steve.SteveConfiguration.STEVE_VERSION;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 14.01.2015
 */
@Slf4j
public class Application {

    public static void main(String[] args) throws IOException {

        // For Hibernate validator
        System.setProperty("org.jboss.logging.provider", "slf4j");

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        DateTimeZone.setDefault(DateTimeZone.UTC);
        log.info("Date/time zone of the application is set to UTC. Current date/time: {}", new DateTime());

        loadProperties();

        JettyServer jettyServer = new JettyServer();
        jettyServer.start();
    }

    private static void loadProperties() throws IOException {
        PropertiesFileLoader prop = new PropertiesFileLoader("main.properties");

        STEVE_VERSION = prop.getString("steve.version");

        DB.URL = new StringBuilder("jdbc:mysql://").append(prop.getString("db.ip"))
                                                   .append(":")
                                                   .append(prop.getInt("db.port"))
                                                   .append("/")
                                                   .append(prop.getString("db.schema"))
                                                   .toString();

        DB.USERNAME     = prop.getString("db.user");
        DB.PASSWORD     = prop.getString("db.password");
        DB.SQL_LOGGING  = prop.getBoolean("db.sql.logging");

        Auth.USERNAME   = prop.getString("auth.user");
        Auth.PASSWORD   = prop.getString("auth.password");

        Jetty.SERVER_HOST           = prop.getString("server.host");
        Jetty.HTTP_ENABLED          = prop.getBoolean("http.enabled");
        Jetty.HTTP_PORT             = prop.getInt("http.port");
        Jetty.HTTPS_ENABLED         = prop.getBoolean("https.enabled");
        Jetty.HTTPS_PORT            = prop.getInt("https.port");
        Jetty.KEY_STORE_PATH        = prop.getString("keystore.path");
        Jetty.KEY_STORE_PASSWORD    = prop.getString("keystore.password");

        Ocpp.WS_SESSION_SELECT_STRATEGY = WsSessionSelectStrategyEnum.fromName(prop.getString("ws.session.select.strategy"));

        if (!(Jetty.HTTP_ENABLED || Jetty.HTTPS_ENABLED)) {
            throw new IllegalArgumentException(
                    "HTTP and HTTPS are both disabled. Well, how do you want to access the server, then?");
        }
    }
}
