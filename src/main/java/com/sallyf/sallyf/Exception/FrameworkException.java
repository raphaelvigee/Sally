package com.sallyf.sallyf.Exception;

public class FrameworkException extends RuntimeException
{
    public FrameworkException()
    {
    }

    public FrameworkException(String s)
    {
        super(s);
    }

    public FrameworkException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public FrameworkException(Throwable throwable)
    {
        super(throwable);
    }

    public FrameworkException(String s, Throwable throwable, boolean b, boolean b1)
    {
        super(s, throwable, b, b1);
    }
}
