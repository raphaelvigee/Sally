package com.sallyf.sallyf.Container;

import com.sallyf.sallyf.Exception.FrameworkException;
import com.sallyf.sallyf.Exception.ServiceInstanciationException;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

class MyAlias extends ContainerAware
{
    public MyAlias(Container container)
    {
        super(container);
    }
}

public class ContainerTest
{
    @Test
    public void testExtends() throws FrameworkException
    {
        Class c = ExampleServiceExtend.class;

        Container container = new Container();
        container.add(c);

        assertNotNull(container.get(c));
    }

    @Test
    public void testImplements() throws FrameworkException
    {
        Class c = ExampleServiceImplements.class;

        Container container = new Container();
        container.add(c);

        assertNotNull(container.get(c));
    }

    @Test
    public void testAliasExtends() throws FrameworkException
    {
        Class c = MyAlias.class;

        Container container = new Container();
        container.add(c, ExampleServiceExtend.class);

        assertNotNull(container.get(c));
    }

    @Test
    public void testAliasImplements() throws FrameworkException
    {
        Class c = MyAlias.class;

        Container container = new Container();
        container.add(c, ExampleServiceImplements.class);

        assertNotNull(container.get(c));
    }

    @Test(expected = ServiceInstanciationException.class)
    public void testInvalid() throws FrameworkException
    {
        Container container = new Container();
        container.add(ExampleServiceInvalid.class);
    }
}