package com.sallyf.sallyf.Authentication;

import com.sallyf.sallyf.Authentication.DataSource.InMemoryDataSource;
import com.sallyf.sallyf.Authentication.Exception.AuthenticationException;
import com.sallyf.sallyf.BaseFrameworkTest;
import com.sallyf.sallyf.Container.Container;
import com.sallyf.sallyf.EventDispatcher.EventDispatcher;
import com.sallyf.sallyf.ExpressionLanguage.ExpressionLanguage;
import com.sallyf.sallyf.Router.Router;
import org.junit.Test;

import java.util.ArrayList;

public class AuthenticationTest extends BaseFrameworkTest
{
    public AuthenticationManager newInstance(Configuration configuration)
    {
        Container c = new Container();
        EventDispatcher ed = new EventDispatcher();
        Router r = new Router(c, ed);
        ExpressionLanguage el = new ExpressionLanguage(r);

        return new AuthenticationManager(c, configuration, r, ed, el);
    }

    @Test(expected = AuthenticationException.class)
    public void unknownDataSourceTest() throws Exception
    {
        AuthenticationManager authenticationManager = newInstance(new Configuration());

        authenticationManager.getDataSource(InMemoryDataSource.class);
    }

    @Test(expected = AuthenticationException.class)
    public void noDataSourceTest() throws Exception
    {
        AuthenticationManager authenticationManager = newInstance(new Configuration());

        authenticationManager.authenticate(null, null);
        authenticationManager.authenticate(null, null, null);

    }

    @Test(expected = AuthenticationException.class)
    public void ambiguousDataSourceTest() throws Exception
    {
        Configuration configuration = new Configuration()
        {
            @Override
            public ArrayList<UserDataSourceInterface> getDataSources()
            {
                ArrayList<UserDataSourceInterface> dataSources = new ArrayList<>();

                InMemoryDataSource<User> ds1 = new InMemoryDataSource<>();
                InMemoryDataSource<User> ds2 = new InMemoryDataSource<>();

                dataSources.add(ds1);
                dataSources.add(ds2);

                return dataSources;
            }
        };

        AuthenticationManager authenticationManager = newInstance(configuration);

        authenticationManager.authenticate(null, null);
        authenticationManager.authenticate(null, null, null);
    }
}
