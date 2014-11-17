package akeefer;

import akeefer.service.PersonService;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptContentHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class HomePage extends WebPage {
    private static final long serialVersionUID = 1L;

    @SpringBean
    private PersonService personService;

    public HomePage(final PageParameters parameters) {
        super(parameters);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        StringBuilder builder = new StringBuilder();
        builder.append(personService.createPersonScript());
        response.render(JavaScriptContentHeaderItem.forScript(builder.toString(), "scriptId"));
    }
}
