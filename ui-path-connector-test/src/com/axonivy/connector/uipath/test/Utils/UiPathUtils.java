package com.axonivy.connector.uipath.test.Utils;

import java.util.List;

import com.axonivy.connector.uipath.TenantHeaderFeature;
import com.axonivy.connector.uipath.test.UiPathMock;
import com.axonivy.connector.uipath.test.constants.UiPathConstants;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClientFeature;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.rest.client.mapper.JsonFeature;
import ch.ivyteam.ivy.rest.client.security.CsrfHeaderFeature;

public class UiPathUtils {
  private static final String UI_PATH_REST_CLIENT = "UIPathRPA (UiPath.WebApi 18.0)";

  public static void setUpConfigForContext(String contextName, AppFixture fixture, IApplication app) {
    switch (contextName) {
      case UiPathConstants.REAL_CALL_CONTEXT_DISPLAY_NAME:
        setUpConfigForApiTest(fixture);
        break;
      case UiPathConstants.MOCK_SERVER_CONTEXT_DISPLAY_NAME:
        setUpConfigForMockServer(fixture, app);
        break;
      default:
        break;
    }
  }

  public static void setUpConfigForApiTest(AppFixture fixture) {
    String organization = System.getProperty(UiPathConstants.ORGANIZATION);
    String tenant = System.getProperty(UiPathConstants.TENANT);
    String clientId = System.getProperty(UiPathConstants.CLIENT_ID);
    String userKey = System.getProperty(UiPathConstants.USER_KEY);

    fixture.var("uiPathConnector.organization", organization);
    fixture.var("uiPathConnector.tenant", tenant);
    fixture.var("uiPathConnector.clientId", clientId);
    fixture.var("uiPathConnector.userKey", userKey);
  }

  public static void setUpConfigForMockServer(AppFixture fixture, IApplication app) {
    fixture.config("RestClients." + UI_PATH_REST_CLIENT + ".Url", UiPathMock.URI);
    fixture.config("RestClients." + UI_PATH_REST_CLIENT + ".Features", "");

    RestClients clients = RestClients.of(app);
    RestClient uiPathRpa = clients.find(UI_PATH_REST_CLIENT);
    var testClient = uiPathRpa.toBuilder().features(List.of( // exclude oauth-feature
        new RestClientFeature(JsonFeature.class.getName()), new RestClientFeature(TenantHeaderFeature.class.getName()),
        new RestClientFeature(CsrfHeaderFeature.class.getName()))).property("AUTH.clientId", "notMyId")
        .property("AUTH.userKey", "notMyKey").property("AUTH.tenant", "notMyTenant").property("PATH.tenant", "tenant")
        .property("PATH.organization", "organization").toRestClient();
    clients.set(testClient);
  }
}
