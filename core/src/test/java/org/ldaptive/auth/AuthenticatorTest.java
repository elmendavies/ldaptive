/* See LICENSE for licensing and NOTICE for copyright. */
package org.ldaptive.auth;

import java.util.Collections;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.MockConnectionFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit test for {@link Authenticator}.
 *
 * @author  Middleware Services
 */
public class AuthenticatorTest
{


  /**
   * Unit test for {@link Authenticator#close()}.
   */
  @Test(groups = "auth")
  public void close()
  {
    final Authenticator auth = new Authenticator();
    auth.setDnResolver(new SearchDnResolver(new MockConnectionFactory(new ConnectionConfig())));
    auth.setAuthenticationHandler(
      new SimpleBindAuthenticationHandler(new MockConnectionFactory(new ConnectionConfig())));
    auth.setEntryResolver(new SearchEntryResolver(new MockConnectionFactory(new ConnectionConfig())));
    auth.close();

    Assert.assertFalse(
      ((MockConnectionFactory)
        ((SearchDnResolver) auth.getDnResolver()).getConnectionFactory()).isOpen());
    Assert.assertFalse(
      ((MockConnectionFactory)
        ((SimpleBindAuthenticationHandler) auth.getAuthenticationHandler()).getConnectionFactory()).isOpen());
    Assert.assertFalse(
      ((MockConnectionFactory)
        ((SearchEntryResolver) auth.getEntryResolver()).getConnectionFactory()).isOpen());
  }


  /**
   * Unit test for {@link Authenticator#close()} wired with aggregate components.
   */
  @Test(groups = "auth")
  public void closeAggregate()
  {
    final Authenticator auth = new Authenticator();
    auth.setDnResolver(
      new AggregateDnResolver(
        Collections.singletonMap("1", new SearchDnResolver(new MockConnectionFactory(new ConnectionConfig())))));
    auth.setAuthenticationHandler(
      new AggregateAuthenticationHandler(
        Collections.singletonMap(
          "1", new SimpleBindAuthenticationHandler(new MockConnectionFactory(new ConnectionConfig())))));
    auth.setEntryResolver(
      new AggregateEntryResolver(
        Collections.singletonMap("1", new SearchEntryResolver(new MockConnectionFactory(new ConnectionConfig())))));
    auth.close();

    Assert.assertFalse(
      ((MockConnectionFactory)
        ((SearchDnResolver)
          ((AggregateDnResolver)
            auth.getDnResolver()).getDnResolvers()
              .values().iterator().next()).getConnectionFactory()).isOpen());
    Assert.assertFalse(
      ((MockConnectionFactory)
        ((SimpleBindAuthenticationHandler)
          ((AggregateAuthenticationHandler)
            auth.getAuthenticationHandler()).getAuthenticationHandlers()
              .values().iterator().next()).getConnectionFactory()).isOpen());
    Assert.assertFalse(
      ((MockConnectionFactory)
        ((SearchEntryResolver)
          ((AggregateEntryResolver) auth.getEntryResolver()).getEntryResolvers()
              .values().iterator().next()).getConnectionFactory()).isOpen());
  }
}
