package com.axonivy.connector.uipath;

import java.io.IOException;

import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

import ch.ivyteam.ivy.rest.client.feature.FeatureConfig;


public class TenantHeaderFeature implements Feature
{
  public static final String PROPERTY_NAME = "AUTH.tenant";

  @Override
  public boolean configure(FeatureContext context)
  {
    var config = FeatureConfig.of(context.getConfiguration(), TenantHeaderFeature.class);
    String tenant = config.readMandatory(PROPERTY_NAME);
    context.register(new OrechstratorTenantFilter(tenant), Priorities.HEADER_DECORATOR);
    return true;
  }

  private static class OrechstratorTenantFilter implements ClientRequestFilter
  {
    private final String tenant;

    private OrechstratorTenantFilter(String tenant)
    {
      this.tenant = tenant;
    }

    @Override
    public void filter(ClientRequestContext context) throws IOException 
    {
      context.getHeaders().add("X-UIPATH-TenantName", tenant);
    }
  }

}
