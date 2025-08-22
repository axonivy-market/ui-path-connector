package com.axonivy.connector.uipath.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.TestTemplate;
import com.axonivy.connector.uipath.ui.path.connector.UiPathJobData;
import com.axonivy.connector.uipath.ui.path.connector.UiPathRpa;

import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.ExecutionResult;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;
import ch.ivyteam.ivy.bpm.error.BpmError;

/**
 * Service functionality is mocked out here: {@link UiPathMock}
 */
public class UiPathTest extends BaseSetup {
  private static final BpmProcess UI_PATH_RPA_PROCESS = BpmProcess.path("uiPathRpa");
  private static final BpmProcess UI_PATH_JOB_PROCESS = BpmProcess.path("uiPathJob");
  private static final String REST_CLIENT_RESPONSE_STATUS_CODE = "RestClientResponseStatusCode";
  private static final String START_JOB = "startJob(String)";
  private static final String START_ALL_ACTIVE_JOBS = "startAllActiveJobs()";
  private static final String START_JOB_BY_NAME = "startJobByName(String,String)";
  private static final BpmElement UI_PATH_JOB_START_JOB_END = BpmElement.pid("190E93ECBBC86C6F-f50");

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
      UiPathJobData data = result.data().lastOnElement(UI_PATH_JOB_START_JOB_END);
      if (isMockTest) {
        assertThat(data.getMachines()).isNotEmpty();
        assertThat(data.getStartInfo()).isNotNull();
      } else {
        assertThat(data.getOrganizationunitId()).isNotNull();
      }
    } catch (BpmError e) {
      assertEquals(HttpStatus.SC_UNAUTHORIZED, e.getAttribute(REST_CLIENT_RESPONSE_STATUS_CODE));
    }
  }
}
