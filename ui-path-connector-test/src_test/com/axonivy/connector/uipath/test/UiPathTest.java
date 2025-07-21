package com.axonivy.connector.uipath.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import com.axonivy.connector.uipath.test.Utils.UiPathTestUtils;
import com.axonivy.connector.uipath.test.constants.UiPathTestConstants;
import com.axonivy.connector.uipath.test.context.MultiEnvironmentContextProvider;
import com.axonivy.connector.uipath.ui.path.connector.UiPathJobData;
import com.axonivy.connector.uipath.ui.path.connector.UiPathRpa;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.ExecutionResult;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.exec.client.IvyProcessTest;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.security.ISession;

/**
 * Service functionality is mocked out here: {@link UiPathMock}
 */
@IvyProcessTest(enableWebServer = true)
@ExtendWith(MultiEnvironmentContextProvider.class)
public class UiPathTest {

  private static final BpmElement UI_PATH_JOB_ALL_ACTIVE_JOBS_END = BpmElement.pid("190E93ECBBC86C6F-f1");
  private static final BpmElement UI_PATH_JOB_START_JOB_END = BpmElement.pid("190E93ECBBC86C6F-f50");
  private static final BpmElement UI_PATH_RPA_END = BpmElement.pid("175F58F3612E10B1-f15");
  private static final BpmElement UI_PATH_RPA_READ_JOB = BpmElement.pid("175F58F3612E10B1-f5");

  @BeforeEach
  void beforeEach(ExtensionContext context, AppFixture fixture, IApplication app) {
    UiPathTestUtils.setUpConfigForContext(context.getDisplayName(), fixture, app);
  }

  @AfterEach
  void afterEach(AppFixture fixture, IApplication app) {
    RestClients clients = RestClients.of(app);
    clients.remove("UIPathRPA (UiPath.WebApi 18.0)");
  }

  @TestTemplate
  public void rpaDemo(ExtensionContext context, BpmClient bpmClient, ISession session) {
    ExecutionResult result = bpmClient.start().process("uiPathDemo/robotGetOrders.ivp").as().session(session).execute();
    if (context.getDisplayName().equals(UiPathTestConstants.MOCK_SERVER_CONTEXT_DISPLAY_NAME)) {
      UiPathRpa data = result.data().lastOnElement(UI_PATH_RPA_END);
      assertThat(data.getLicense()).isNotNull();
      assertThat(data.getReleases()).isNotEmpty();
      assertThat(data.getRobots()).isNotEmpty();
      assertThat(data.getOrganizationunitId()).isNotNull();
    } else {
      UiPathRpa data = result.data().lastOnElement(UI_PATH_RPA_READ_JOB);
      assertThat(data.getOrganizationunitId()).isNotNull();
    }
  }


  @TestTemplate
  public void jobDemo(BpmClient bpmClient, ISession session, ExtensionContext context) {
    ExecutionResult result =
        bpmClient.start().process("uiPathDemo/triggerAllActiveJobs.ivp").as().session(session).execute();
    UiPathJobData data = result.data().lastOnElement(UI_PATH_JOB_ALL_ACTIVE_JOBS_END);
    assertThat(data.getOrganizationunitId()).isNotNull();
    if (context.getDisplayName().equals(UiPathTestConstants.MOCK_SERVER_CONTEXT_DISPLAY_NAME)) {
      data = result.data().lastOnElement(UI_PATH_JOB_START_JOB_END);
      assertThat(data.getMachines()).isNotEmpty();
      assertThat(data.getStartInfo()).isNotNull();
    }
  }
}
