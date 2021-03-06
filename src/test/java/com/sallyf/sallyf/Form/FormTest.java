package com.sallyf.sallyf.Form;

import com.sallyf.sallyf.Container.Container;
import com.sallyf.sallyf.Container.ServiceDefinition;
import com.sallyf.sallyf.Form.Type.*;
import com.sallyf.sallyf.Utils.MapUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashSet;

import static org.junit.Assert.assertEquals;

public class FormTest
{
    private FormManager formManager;

    @Before
    public void setUp() throws Exception
    {
        Container c = new Container();

        c.add(new ServiceDefinition<>(FormManager.class));

        c.instantiate();

        formManager = c.get(FormManager.class);
    }

    @Test
    public void createFormTest()
    {
        FormBuilder<FormType, FormType.FormOptions, Object> builder = new FormBuilder<>(new Container(), "", FormType.class);

        builder.setData(MapUtils.parse("{submit: 'Hello !'}"));

        Form form = builder
                .add("foo", TextType.class, (options) -> {
                    options.setDisabled(true);
                })
                .add("pass", PasswordType.class, (options) -> {

                })
                .add("textarea", TextareaType.class, (options) -> {

                })
                .add("choices", ChoiceType.class, (options) -> {
                    options.setMultiple(false);
                    options.setExpanded(false);

                    options.setChoices(new LinkedHashSet<String>(){{
                        add(null);
                        add("one");
                        add("two");
                        add("three");
                    }});
                })
                .add("submit", SubmitType.class, (options) -> {

                })
                .getForm();

        String formView = formManager.render(form.createView());

        String expected = "<form method=\"post\"><div class=\"row\"><label>foo</label><input name=\"foo\" disabled=\"disabled\" type=\"text\" value=\"\"></div><div class=\"row\"><label>pass</label><input name=\"pass\" type=\"password\" value=\"\"></div><div class=\"row\"><label>textarea</label><textarea name=\"textarea\"></textarea></div><div class=\"row\"><label>choices</label><select name=\"choices\"><option value=\"null\" selected=\"selected\">null</option><option value=\"one\">one</option><option value=\"two\">two</option><option value=\"three\">three</option></select></div><div class=\"row\"><input name=\"submit\" type=\"submit\" value=\"Hello !\"></div></form>";

        assertEquals(expected, formView);
    }
}
