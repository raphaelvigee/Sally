package com.raphaelvigee.sally.Event;

import com.raphaelvigee.sally.EventDispatcher.EventInterface;
import com.raphaelvigee.sally.Router.Response;
import com.raphaelvigee.sally.Server.RuntimeBag;

public class ResponseEvent implements EventInterface
{
    private RuntimeBag runtimeBag;

    private Response response;

    public ResponseEvent(RuntimeBag runtimeBag, Response response)
    {
        this.runtimeBag = runtimeBag;
        this.response = response;
    }

    public RuntimeBag getRuntimeBag()
    {
        return runtimeBag;
    }

    public void setRuntimeBag(RuntimeBag runtimeBag)
    {
        this.runtimeBag = runtimeBag;
    }

    public Response getResponse()
    {
        return response;
    }

    public void setResponse(Response response)
    {
        this.response = response;
    }
}