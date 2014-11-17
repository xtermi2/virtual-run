package akeefer;

import akeefer.service.PersonService;
import org.apache.wicket.authroles.authentication.pages.SignOutPage;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptContentHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

@AuthorizeInstantiation("USER")
public class HomePage extends WebPage {
    private static final long serialVersionUID = 1L;

    @SpringBean
    private PersonService personService;

    public HomePage(final PageParameters parameters) {
        super(parameters);

        add(new Link("logoutButton") {
            @Override
            public void onClick() {
                setResponsePage(SignOutPage.class);
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        StringBuilder builder = new StringBuilder();
        VRSession session = (VRSession) VRSession.get();
        builder.append(personService.createPersonScript(session.getUser()));
        response.render(JavaScriptContentHeaderItem.forScript(builder.toString(), "scriptId"));
    }
}
