package com.sallyf.sallyf.Form;

import com.sallyf.sallyf.Container.Container;
import com.sallyf.sallyf.Container.ServiceDefinition;
import com.sallyf.sallyf.Form.Type.FormType;
import com.sallyf.sallyf.Form.Type.SubmitType;
import com.sallyf.sallyf.Form.Type.TextType;
import com.sallyf.sallyf.Utils.MapUtils;
import org.junit.Before;
import org.junit.Test;

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
        FormBuilder<FormType, FormType.FormOptions, Object, Object> builder = new FormBuilder<>(new Container(), "", FormType.class);

        builder.setData(MapUtils.parse("{submit: 'Hello !'}"));

        Form form = builder
                .add("foo", TextType.class, (options) -> {

                })
                .add("submit", SubmitType.class, (options) -> {

                })
                .getForm();

        String formView = formManager.render(form.createView());

        String expected = "<form method=\"post\"><input name=\"foo\" type=\"text\"><input name=\"submit\" type=\"submit\" value=\"Hello !\"></form>";

        assertEquals(expected, formView);
    }
}