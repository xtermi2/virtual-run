package akeefer.config;

import akeefer.model.Aktivitaet;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import net.eusashead.spring.gaecache.*;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

/**
 * @author Andreas Keefer
 */
@Configuration
public class SpringJavaConfig {

    /**
     * Set up the {@link CacheManager}
     * with an memcached based cache
     *
     * @return
     */
    @Bean(name = "gaeCacheManager")
    public CacheManager cacheManager() {
        GaeCacheManager cacheManager = new GaeCacheManager();
        // default Cache 24 hours
        cacheManager.addCache(new GaeCache("default",
                MemcacheServiceFactory.getMemcacheService(),
                Expiration.byDeltaSeconds(60 * 60 * 24)));
        return cacheManager;
    }

    @Bean(name = "gaeKeyGenerator")
    public KeyGenerator keyGenerator() {
        GaeCacheKeyGenerator generator = new GaeCacheKeyGenerator();
        generator.registerStrategy(Aktivitaet.class, new ArgumentHashStrategy<Aktivitaet>() {
            @Override
            public ArgumentHash hash(Object keySource) {
                Aktivitaet akt = (Aktivitaet) keySource;
                Assert.notNull(akt.getOwner());
                return new ArgumentHash(akt.getOwner());
            }
        });
        return generator;
    }
}
