package com.sallyf.sallyf.Form.Renderer;

import com.sallyf.sallyf.Form.FormTypeInterface;
import com.sallyf.sallyf.Form.Options;
import com.sallyf.sallyf.Form.Type.SubmitType;

public class SubmitRenderer extends InputRenderer<SubmitType, Options>
{
    @Override
    public boolean supports(FormTypeInterface form)
    {
        return form instanceof SubmitType;
    }
}