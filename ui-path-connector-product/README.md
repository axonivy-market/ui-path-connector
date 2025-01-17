# UiPath Connector
Axon Ivy's [UiPath](https://www.uipath.com/) connector helps you to automate repetitive manual work steps in legacy IT systems and integrate them into your business processes.

This connector:

- enables you to steer robotic work from your workflow process. 
- makes the UiPath RPA API features easy and attractive to use in business
  processes.

## Demo

With this connector a demo process will be installed to demonstrate prominent
use cases for UiPath orchestrator interactions. E.g., 'startAJob' on an
unattended robot.

 ![copy-properties](images/startJobSample.png)

## Setup

Security matters! Data flows between the UiPath Orchestrator and the Axon Ivy
Engine are encrypted and protected by OAUTH access tokens.

However, the systems must be introduced to each other. The Axon Ivy Engine needs
a valid clientId and userKey that are allowed to connect to UiPath as substitute
for your user account.

Proceed as follows:
1. Start any process that contains an UiPath activity interaction.
1. If OAUTH credentials are not present, the browser will redirect you to the UiPath cloud login page.
  ![cloud-navigate](images/authErrorHandling.png)
1. Login and navigate to: Account > Preferences > Privacy & Security > View API Access
  ![cloud-access](images/cloudApiAccess.png)
1. Copy the shown `clientId`, `userKey` and `tenant` to your Rest Client definition.
  ![copy-properties](images/copyAuth_idKeyTenant.png)
1. Start the process again. Now the AccessToken will be sent automatically within UiPath requests.


### Unlimited

Although the included demos focus on prominent use cases, be aware that our REST
Client activities can use the full featured APIs that are provided by UiPath. In
fact, it all can be done in low-code manner. Just select any service method to
call and use the intuitive mapping tables to define your data flows.

Use the REST Client API browser to get a first outline on the UiPath orchestrator capabilities..
 ![api-browser](images/apiBrowserUiPath.png)

