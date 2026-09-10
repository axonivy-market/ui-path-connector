package com.axonivy.connector.uipath.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.axonivy.connector.uipath.TenantHeaderFeature;
import com.axonivy.connector.uipath.ui.path.connector.UiPathJobData;
import com.axonivy.connector.uipath.ui.path.connector.UiPathRpa;

import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.ExecutionResult;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.exec.client.IvyProcessTest;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.rest.client.mapper.JsonFeature;
import ch.ivyteam.ivy.rest.client.security.CsrfHeaderFeature;
import ch.ivyteam.ivy.security.ISession;

/**
 * Service functionality is mocked out here: {@link UiPathMock}
 */
@IvyProcessTest(enableWebServer = true)
public class UiPathTest {

  private static final String UI_PATH_REST_CLIENT = "UIPathRPA (UiPath.WebApi 18.0)";
  private static final BpmElement UI_PATH_JOB_ALL_ACTIVE_JOBS_END = BpmElement.pid("190E93ECBBC86C6F-f1");
  private static final BpmElement UI_PATH_JOB_START_JOB_END = BpmElement.pid("190E93ECBBC86C6F-f50");
  private static final BpmElement UI_PATH_RPA_END = BpmElement.pid("175F58F3612E10B1-f15");

  @BeforeAll
  static void beforeAll(AppFixture fixture) {
    fixture.config("RestClients." + UI_PATH_REST_CLIENT + ".Url", UiPathMock.URI);
    fixture.config("RestClients." + UI_PATH_REST_CLIENT + ".Features", List.of(
        JsonFeature.class.getName(),
        TenantHeaderFeature.class.getName(),
        CsrfHeaderFeature.class.getName()));
    fixture.config("RestClients." + UI_PATH_REST_CLIENT + ".Properties.AUTH.clientId", "notMyId");
    fixture.config("RestClients." + UI_PATH_REST_CLIENT + ".Properties.AUTH.userKey", "notMyKey");
    fixture.config("RestClients." + UI_PATH_REST_CLIENT + ".Properties.AUTH.tenant", "notMyTenant");
    fixture.config("RestClients." + UI_PATH_REST_CLIENT + ".Properties.PATH.tenant", "tenant");
    fixture.config("RestClients." + UI_PATH_REST_CLIENT + ".Properties.PATH.organization", "organization");
  }

  @Test
  public void rpaDemo(BpmClient bpmClient, ISession session) {
    ExecutionResult result = bpmClient.start()
      .process("uiPathDemo/robotGetOrders.ivp")
      .as().session(session)
      .execute();
    UiPathRpa data = result.data().lastOnElement(UI_PATH_RPA_END);
    assertThat(data.getLicense()).isNotNull();
    assertThat(data.getReleases()).isNotEmpty();
    assertThat(data.getRobots()).isNotEmpty();
  }

  @Test
  public void jobDemo(BpmClient bpmClient, ISession session) {
    ExecutionResult result = bpmClient.start().process("uiPathDemo/triggerAllActiveJobs.ivp").as().session(session)
        .execute();
    UiPathJobData data = result.data().lastOnElement(UI_PATH_JOB_ALL_ACTIVE_JOBS_END);
    assertThat(data.getOrganizationunitId()).isNotNull();
    data = result.data().lastOnElement(UI_PATH_JOB_START_JOB_END);
    assertThat(data.getMachines()).isNotEmpty();
    assertThat(data.getStartInfo()).isNotNull();
  }
}
