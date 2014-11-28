package akeefer.web.pages;

import akeefer.service.PersonService;
import akeefer.web.VRSession;
import org.apache.wicket.markup.head.*;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class MapPage extends AbstractAuthenticatedBasePage {
    private static final long serialVersionUID = 1L;

    private static final JavaScriptResourceReference MAPPAGE_JS = new JavaScriptResourceReference(MapPage.class, "MapPage.js");
    private static final String GOOGLE_MAPS_API_URL = "https://maps.googleapis.com/maps/api/js?key=AIzaSyBSrQWErgp4-ylAKMQ8tSi_kM8a3Go3g84&sensor=false&libraries=geometry&signed_in=false";

    @SpringBean
    private PersonService personService;

    public MapPage(final PageParameters parameters) {
        super(parameters, true, false);

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        StringBuilder builder = new StringBuilder();
        builder.append(personService.createPersonScript(VRSession.get().getUser().getId()));
        response.render(JavaScriptHeaderItem.forUrl(GOOGLE_MAPS_API_URL));
        response.render(JavaScriptContentHeaderItem.forScript(builder.toString(), "scriptId"));
        response.render(JavaScriptReferenceHeaderItem.forReference(MAPPAGE_JS));
        response.render(MetaDataHeaderItem.forMetaTag("viewport", "initial-scale=1.0, user-scalable=no"));
    }
}
