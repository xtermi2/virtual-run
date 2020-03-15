![Java CI](https://github.com/xtermi2/virtual-run/workflows/Java%20CI/badge.svg)

## GAE Spezifika

### GAE Tutorials
- http://www.vogella.com/tutorials/GoogleAppEngineJava/article.html

### GAE + SpringData
- http://tommysiu.blogspot.de/2014/01/spring-data-on-gae-part-1.html
- http://tommysiu.blogspot.de/2014/02/spring-data-on-gae-part-2-datastore-key.html
- http://tommysiu.blogspot.de/2014/02/spring-data-on-gae-part-3-custom.html

### GAE und e-Mail
- https://cloud.google.com/appengine/docs/java/mail/



## Wicket
 
### Wicket + BeanValidation
- http://www.effectivetrainings.de/blog/2012/12/15/wicket-6-und-jsr-303-beanvalidation/ 

### Wicket Erweiterungen
- http://wicket.visural.net/examples

### Wicket + Spring Security
- http://www.jdev.it/integrating-wicket-with-wicket-authroles-and-spring-security/
- https://github.com/thombergs/wicket-spring-security-example
- http://javajeedevelopment.blogspot.nl/2011/03/integrating-spring-security-3-with.html
- http://apache-wicket.1842946.n4.nabble.com/Spring-Security-SecurityContext-changes-td3217947.html


## CSS / Design)

### Button Designer
- http://www.bestcssbuttongenerator.com/

# Developement

## Run GAE dev server

```bash
./mvnw appengine:devserver
```
- open [http://localhost:8080/init](http://localhost:8080/init) to create initial users:
  - andi:andi
  - frank:frank
  - sabine:sabine
  - roland:roland
  - norbert:norbert
  - uli-hans:uli-hans

## Deployment on GAE

NOTE: This will use the version from `appengine-web.xml`, so check the current version before you deploy to prevent overwriting the current deployment.
```bash
./mvnw appengine:update
```