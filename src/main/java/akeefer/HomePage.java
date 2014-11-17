package akeefer;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptContentHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.WebPage;

public class HomePage extends WebPage {
	private static final long serialVersionUID = 1L;

	public HomePage(final PageParameters parameters) {
		super(parameters);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        StringBuilder builder = new StringBuilder();
        builder.append("var personen = [\n" +
                "        {id: 'andi', distance: 1500000, done: false},\n" +
                "        {id: 'sabine', distance: 500000, done: false},\n" +
                "//        {id: 'uli_hans', distance: 1000000, done: false},\n" +
                "        {id: 'roland', distance: 2500000, done: false},\n" +
                "        {id: 'norbert', distance: 2000000, done: false}\n" +
                "    ];");
        response.render(JavaScriptContentHeaderItem.forScript(builder.toString(), "scriptId"));
    }
}
