package com.axonivy.connector.uipath.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.connector.uipath.TenantHeaderFeature;
import com.axonivy.connector.uipath.ui.path.connector.UiPathRpa;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.ExecutionResult;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.exec.client.IvyProcessTest;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.rest.client.mapper.JsonFeature;
import ch.ivyteam.ivy.rest.client.security.CsrfHeaderFeature;
import ch.ivyteam.ivy.security.ISession;

/**
 * Service functionality is mocked out here: {@link UiPathMock}
 */
@IvyProcessTest
public class TestUiPathRPA {

  private static final BpmElement UI_PATH_END = BpmElement.pid("175F58F3612E10B1-f15");

  @BeforeEach
  void beforeEach(AppFixture fixture, IApplication app) {
    fixture.config("RestClients.UIPathRPA.Url", UiPathMock.URI);
    fixture.config("RestClients.UIPathRPA.Features", "");

    RestClients clients = RestClients.of(app);
    RestClient uiPathRpa = clients.find("UIPathRPA");
    var testClient = uiPathRpa.toBuilder()
      .features(List.of( // exclude oauth-feature
        JsonFeature.class.getName(),
        TenantHeaderFeature.class.getName(),
        CsrfHeaderFeature.class.getName()))
      .property("AUTH.clientId", "notMyId")
      .property("AUTH.userKey", "notMyKey")
      .toRestClient();
    clients.set(testClient);
  }

  @Test
  public void rpaDemo(BpmClient bpmClient, ISession session) {
    ExecutionResult result = bpmClient.start()
      .process("uiPathDemo/robotGetOrders.ivp")
      .as().session(session)
      .execute();
    UiPathRpa data = result.data().lastOnElement(UI_PATH_END);
    assertThat(data.getLicense()).isNotNull();
    assertThat(data.getReleases()).isNotEmpty();
    assertThat(data.getRobots()).isNotEmpty();
  }
}
