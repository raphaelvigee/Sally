package com.sallyf.sallyf.Form.Renderer;

import com.sallyf.sallyf.Form.FormTypeInterface;
import com.sallyf.sallyf.Form.FormView;
import com.sallyf.sallyf.Form.Options;
import com.sallyf.sallyf.Form.Type.SubmitType;

public class SubmitRenderer extends InputRenderer<SubmitType, Options>
{
    @Override
    public boolean supports(FormTypeInterface form)
    {
        return form.getClass().equals(SubmitType.class);
    }

    @Override
    public String renderRow(FormView<SubmitType, Options, ?> formView)
    {
        String s = "<div class=\"row\">";

        s += renderErrors(formView);
        s += renderWidget(formView);

        s += "</div>";

        return s;
    }

    @Override
    public String renderLabel(FormView<SubmitType, Options, ?> formView)
    {
        throw new RuntimeException("Not Implemented");
    }
}
