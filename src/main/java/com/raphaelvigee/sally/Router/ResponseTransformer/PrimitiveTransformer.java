package com.raphaelvigee.sally.Router.ResponseTransformer;

import com.raphaelvigee.sally.Router.Response;
import com.raphaelvigee.sally.Router.ResponseTransformerInterface;
import com.raphaelvigee.sally.Server.RuntimeBag;

public class PrimitiveTransformer implements ResponseTransformerInterface
{
    @Override
    public boolean supports(RuntimeBag runtimeBag, Object response)
    {
        return response instanceof String || response instanceof Number;
    }

    @Override
    public Response resolve(RuntimeBag runtimeBag, Object response)
    {
        return new Response(response.toString());
    }
}