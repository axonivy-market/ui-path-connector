package com.axonivy.connector.uipath.test.Utils;

import java.util.List;

import com.axonivy.connector.uipath.TenantHeaderFeature;
import com.axonivy.connector.uipath.test.UiPathMock;
import com.axonivy.connector.uipath.test.constants.UiPathTestConstants;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClientFeature;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.rest.client.mapper.JsonFeature;
import ch.ivyteam.ivy.rest.client.security.CsrfHeaderFeature;

public class UiPathTestUtils {
  private static final String UI_PATH_REST_CLIENT = "UIPathRPA (UiPath.WebApi 18.0)";

  public static void setUpConfigForContext(String contextName, AppFixture fixture, IApplication app) {
    switch (contextName) {
      case UiPathTestConstants.REAL_CALL_CONTEXT_DISPLAY_NAME:
        setUpConfigForApiTest(fixture);
        break;
      case UiPathTestConstants.MOCK_SERVER_CONTEXT_DISPLAY_NAME:
        setUpConfigForMockServer(fixture, app);
        break;
      default:
        break;
    }
  }

  private static void setUpConfigForApiTest(AppFixture fixture) {
    String organization = System.getProperty(UiPathTestConstants.ORGANIZATION);
    String tenant = System.getProperty(UiPathTestConstants.TENANT);
    String clientId = System.getProperty(UiPathTestConstants.CLIENT_ID);
    String userKey = System.getProperty(UiPathTestConstants.USER_KEY);

    fixture.var("uiPathConnector.organization", organization);
    fixture.var("uiPathConnector.tenant", tenant);
    fixture.var("uiPathConnector.clientId", clientId);
    fixture.var("uiPathConnector.userKey", userKey);
  }

  private static void setUpConfigForMockServer(AppFixture fixture, IApplication app) {
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
