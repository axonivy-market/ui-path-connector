package com.axonivy.connector.uipath.test;

import static com.axonivy.utils.e2etest.enums.E2EEnvironment.MOCK_SERVER;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.axonivy.connector.uipath.TenantHeaderFeature;
import com.axonivy.connector.uipath.test.constants.UiPathTestConstants;
import com.axonivy.utils.e2etest.context.MultiEnvironmentContextProvider;
import com.axonivy.utils.e2etest.utils.E2ETestUtils;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.bpm.exec.client.IvyProcessTest;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClientFeature;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.rest.client.mapper.JsonFeature;
import ch.ivyteam.ivy.rest.client.security.CsrfHeaderFeature;

@IvyProcessTest(enableWebServer = true)
@ExtendWith(MultiEnvironmentContextProvider.class)
public class BaseSetup {
  private static final String UI_PATH_REST_CLIENT = "UIPathRPA (UiPath.WebApi 18.0)";
  protected boolean isMockTest;

  @BeforeEach
  void beforeEach(ExtensionContext context, AppFixture fixture, IApplication app) {
    E2ETestUtils.determineConfigForContext(context.getDisplayName(), runRealEnv(fixture), runMockEnv(fixture, app));
    isMockTest = context.getDisplayName().equals(MOCK_SERVER.getDisplayName());
  }

  @AfterEach
  void afterEach(IApplication app) {
    RestClients clients = RestClients.of(app);
    clients.remove("UIPathRPA (UiPath.WebApi 18.0)");
  }

  private Runnable runRealEnv(AppFixture fixture) {
    return () -> {
      String organization = System.getProperty(UiPathTestConstants.ORGANIZATION);
      String tenant = System.getProperty(UiPathTestConstants.TENANT);
      String clientId = System.getProperty(UiPathTestConstants.CLIENT_ID);
      String userKey = System.getProperty(UiPathTestConstants.USER_KEY);

      fixture.var("uiPathConnector.organization", organization);
      fixture.var("uiPathConnector.tenant", tenant);
      fixture.var("uiPathConnector.clientId", clientId);
      fixture.var("uiPathConnector.userKey", userKey);
    };
  }

  private Runnable runMockEnv(AppFixture fixture, IApplication app) {
    return () -> {
      fixture.config("RestClients." + UI_PATH_REST_CLIENT + ".Url", UiPathMock.URI);
      fixture.config("RestClients." + UI_PATH_REST_CLIENT + ".Features", "");

      RestClients clients = RestClients.of(app);
      RestClient uiPathRpa = clients.find(UI_PATH_REST_CLIENT);
      var testClient = uiPathRpa.toBuilder().features(List.of( // exclude oauth-feature
          new RestClientFeature(JsonFeature.class.getName()),
          new RestClientFeature(TenantHeaderFeature.class.getName()),
          new RestClientFeature(CsrfHeaderFeature.class.getName()))).property("AUTH.clientId", "notMyId")
          .property("AUTH.userKey", "notMyKey").property("AUTH.tenant", "notMyTenant").property("PATH.tenant", "tenant")
          .property("PATH.organization", "organization").toRestClient();
      clients.set(testClient);
    };
  }
}
