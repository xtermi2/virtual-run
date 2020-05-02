package akeefer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Hilfklasse um das Query Performance Logging zu vereinfachen
 *
 * @author Andreas Keefer
 */
public class MethodProfilingLogger {
    private static final Logger logger = LoggerFactory.getLogger(MethodProfilingLogger.class);

    /**
     * Startet das Methoden Profiling indem die aktuelle Zeit in ms zurueckgegeben wird.
     *
     * @return der aktuelle Zeitpunkt in ms
     */
    public static long startMethodProfiling() {
        return System.currentTimeMillis();
    }

    /**
     * Erzeugt einen Logging Aufruf fuer das Profiling einer Methode
     *
     * @param methode            der Name der aufrufenden Methode
     * @param startZeitpunktInMs der Startzeitpunkt in ms
     */
    public static void endMethodProfiling(String methode, long startZeitpunktInMs) {
        notNull(methode, "methode must not be null");

        final long now = System.currentTimeMillis();
        if (logger.isInfoEnabled()) {
            logger.info("{} runtime: {} ms", methode, (now - startZeitpunktInMs));
        }
    }
}
