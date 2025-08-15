package com.axonivy.connector.uipath.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.HttpStatus;
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
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;
import ch.ivyteam.ivy.bpm.error.BpmError;
import ch.ivyteam.ivy.bpm.exec.client.IvyProcessTest;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.rest.client.RestClients;

/**
 * Service functionality is mocked out here: {@link UiPathMock}
 */
@IvyProcessTest(enableWebServer = true)
@ExtendWith(MultiEnvironmentContextProvider.class)
public class UiPathTest {

  private static final BpmProcess UI_PATH_RPA_PROCESS = BpmProcess.path("uiPathRpa");
  private static final BpmProcess UI_PATH_JOB_PROCESS = BpmProcess.path("uiPathJob");
  private static final String REST_CLIENT_RESPONSE_STATUS_CODE = "RestClientResponseStatusCode";
  private static final String START_JOB = "startJob(String)";
  private static final String START_ALL_ACTIVE_JOBS = "startAllActiveJobs()";
  private static final String START_JOB_BY_NAME = "startJobByName(String,String)";
  private boolean isMockTest;

  @BeforeEach
  void beforeEach(ExtensionContext context, AppFixture fixture, IApplication app) {
    UiPathTestUtils.setUpConfigForContext(context.getDisplayName(), fixture, app);
    isMockTest = context.getDisplayName().equals(UiPathTestConstants.MOCK_SERVER_CONTEXT_DISPLAY_NAME);
  }

  @AfterEach
  void afterEach(IApplication app) {
    RestClients clients = RestClients.of(app);
    clients.remove("UIPathRPA (UiPath.WebApi 18.0)");
  }

  @TestTemplate
  public void rpaDemo(BpmClient bpmClient) {
    BpmElement startable = UI_PATH_RPA_PROCESS.elementName(START_JOB);
    try {
      ExecutionResult result = bpmClient.start().subProcess(startable).withParam("job", "getOrders").execute();
      UiPathRpa data = result.data().last();
      if (isMockTest) {
        assertThat(data.getLicense()).isNotNull();
        assertThat(data.getReleases()).isNotEmpty();
        assertThat(data.getRobots()).isNotEmpty();
      }
      assertThat(data.getOrganizationunitId()).isNotNull();
    } catch (BpmError e) {
      assertEquals(HttpStatus.SC_UNAUTHORIZED, e.getAttribute(REST_CLIENT_RESPONSE_STATUS_CODE));
    }
  }

  @TestTemplate
  public void startJobByNameDemo(BpmClient bpmClient) {
    BpmElement startable = UI_PATH_JOB_PROCESS.elementName(START_JOB_BY_NAME);
    try {
      ExecutionResult result = bpmClient.start().subProcess(startable).execute("jobNameTest", "jobArgumentsTest");
      UiPathJobData data = result.data().last();
      if (isMockTest) {
        assertTrue(ObjectUtils.isEmpty(data.getMachines()));
        assertTrue(ObjectUtils.isEmpty(data.getStartInfo()));
      } else {
        assertThat(data.getOrganizationunitId()).isNotNull();
      }
    } catch (BpmError e) {
      assertEquals(HttpStatus.SC_UNAUTHORIZED, e.getAttribute(REST_CLIENT_RESPONSE_STATUS_CODE));
    }
  }

  @TestTemplate
  public void startActiveJobsDemo(BpmClient bpmClient) {
    BpmElement startable = UI_PATH_JOB_PROCESS.elementName(START_ALL_ACTIVE_JOBS);
    try {
      ExecutionResult result = bpmClient.start().subProcess(startable).execute();
      UiPathJobData data = result.data().last();
      if (isMockTest) {
        assertTrue(ObjectUtils.isEmpty(data.getMachines()));
        assertTrue(ObjectUtils.isEmpty(data.getStartInfo()));
      } else {
        assertThat(data.getOrganizationunitId()).isNotNull();
      }
    } catch (BpmError e) {
      assertEquals(HttpStatus.SC_UNAUTHORIZED, e.getAttribute(REST_CLIENT_RESPONSE_STATUS_CODE));
    }
  }
}
