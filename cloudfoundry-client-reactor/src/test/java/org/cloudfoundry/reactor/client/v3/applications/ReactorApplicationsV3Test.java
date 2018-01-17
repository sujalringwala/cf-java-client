/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.reactor.client.v3.applications;

import org.cloudfoundry.client.v3.BuildpackData;
import org.cloudfoundry.client.v3.Checksum;
import org.cloudfoundry.client.v3.ChecksumType;
import org.cloudfoundry.client.v3.DockerData;
import org.cloudfoundry.client.v3.Lifecycle;
import org.cloudfoundry.client.v3.LifecycleType;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.Pagination;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.applications.ApplicationRelationships;
import org.cloudfoundry.client.v3.applications.ApplicationResource;
import org.cloudfoundry.client.v3.applications.ApplicationState;
import org.cloudfoundry.client.v3.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v3.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v3.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationCurrentDropletRelationshipRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationCurrentDropletRelationshipResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationCurrentDropletRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationCurrentDropletResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentVariablesRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentVariablesResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessStatisticsRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessStatisticsResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationDropletsRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationDropletsResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationPackagesRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationPackagesResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationProcessesRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationProcessesResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationTasksRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationTasksResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v3.applications.ScaleApplicationRequest;
import org.cloudfoundry.client.v3.applications.ScaleApplicationResponse;
import org.cloudfoundry.client.v3.applications.SetApplicationCurrentDropletRequest;
import org.cloudfoundry.client.v3.applications.SetApplicationCurrentDropletResponse;
import org.cloudfoundry.client.v3.applications.StartApplicationRequest;
import org.cloudfoundry.client.v3.applications.StartApplicationResponse;
import org.cloudfoundry.client.v3.applications.StopApplicationRequest;
import org.cloudfoundry.client.v3.applications.StopApplicationResponse;
import org.cloudfoundry.client.v3.applications.TerminateApplicationInstanceRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationEnvironmentVariablesRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationEnvironmentVariablesResponse;
import org.cloudfoundry.client.v3.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationResponse;
import org.cloudfoundry.client.v3.droplets.Buildpack;
import org.cloudfoundry.client.v3.droplets.DropletResource;
import org.cloudfoundry.client.v3.droplets.DropletState;
import org.cloudfoundry.client.v3.packages.BitsData;
import org.cloudfoundry.client.v3.packages.PackageResource;
import org.cloudfoundry.client.v3.packages.PackageState;
import org.cloudfoundry.client.v3.packages.PackageType;
import org.cloudfoundry.client.v3.processes.Data;
import org.cloudfoundry.client.v3.processes.HealthCheck;
import org.cloudfoundry.client.v3.processes.HealthCheckType;
import org.cloudfoundry.client.v3.processes.PortMapping;
import org.cloudfoundry.client.v3.processes.ProcessResource;
import org.cloudfoundry.client.v3.processes.ProcessState;
import org.cloudfoundry.client.v3.processes.ProcessStatisticsResource;
import org.cloudfoundry.client.v3.processes.ProcessUsage;
import org.cloudfoundry.client.v3.tasks.Result;
import org.cloudfoundry.client.v3.tasks.TaskResource;
import org.cloudfoundry.client.v3.tasks.TaskState;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.cloudfoundry.util.FluentMap;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.PATCH;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class ReactorApplicationsV3Test extends AbstractClientApiTest {

    private final ReactorApplicationsV3 applications = new ReactorApplicationsV3(CONNECTION_CONTEXT, this.root, TOKEN_PROVIDER);

    @Test
    public void create() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/apps")
                .payload("fixtures/client/v3/apps/POST_request.json")
                .build())
            .response(TestResponse.builder()
                .status(CREATED)
                .payload("fixtures/client/v3/apps/POST_response.json")
                .build())
            .build());

        this.applications
            .create(CreateApplicationRequest.builder()
                .name("my_app")
                .relationships(ApplicationRelationships.builder()
                    .space(ToOneRelationship.builder()
                        .data(Relationship.builder()
                            .id("2f35885d-0c9d-4423-83ad-fd05066f8576")
                            .build())
                        .build())
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectNext(CreateApplicationResponse.builder()
                .id("1cb006ee-fb05-47e1-b541-c34179ddc446")
                .name("my_app")
                .state(ApplicationState.STOPPED)
                .createdAt("2016-03-17T21:41:30Z")
                .updatedAt("2016-06-08T16:41:26Z")
                .lifecycle(Lifecycle.builder()
                    .type(LifecycleType.BUILDPACK)
                    .data(BuildpackData.builder()
                        .buildpack("java_buildpack")
                        .stack("cflinuxfs2")
                        .build())
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446")
                    .build())
                .link("space", Link.builder()
                    .href("https://api.example.org/v3/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                    .build())
                .link("processes", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/processes")
                    .build())
                .link("route_mappings", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/route_mappings")
                    .build())
                .link("packages", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/packages")
                    .build())
                .link("environment_variables", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/environment_variables")
                    .build())
                .link("current_droplet", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/droplets/current")
                    .build())
                .link("droplets", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/droplets")
                    .build())
                .link("tasks", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/tasks")
                    .build())
                .link("start", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/actions/start")
                    .method("POST")
                    .build())
                .link("stop", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/actions/stop")
                    .method("POST")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void delete() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/apps/test-application-id")
                .build())
            .response(TestResponse.builder()
                .status(ACCEPTED)
                .header("Location", "https://api.example.org/v3/jobs/[guid]")
                .build())
            .build());

        this.applications
            .delete(DeleteApplicationRequest.builder()
                .applicationId("test-application-id")
                .build())
            .as(StepVerifier::create)
            .expectNext("[guid]")
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void get() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/apps/test-application-id")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/apps/GET_{id}_response.json")
                .build())
            .build());

        this.applications
            .get(GetApplicationRequest.builder()
                .applicationId("test-application-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetApplicationResponse.builder()
                .id("1cb006ee-fb05-47e1-b541-c34179ddc446")
                .name("my_app")
                .state(ApplicationState.STOPPED)
                .createdAt("2016-03-17T21:41:30Z")
                .updatedAt("2016-06-08T16:41:26Z")
                .lifecycle(Lifecycle.builder()
                    .type(LifecycleType.BUILDPACK)
                    .data(BuildpackData.builder()
                        .buildpack("java_buildpack")
                        .stack("cflinuxfs2")
                        .build())
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446")
                    .build())
                .link("space", Link.builder()
                    .href("https://api.example.org/v3/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                    .build())
                .link("processes", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/processes")
                    .build())
                .link("route_mappings", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/route_mappings")
                    .build())
                .link("packages", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/packages")
                    .build())
                .link("environment_variables", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/environment_variables")
                    .build())
                .link("current_droplet", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/droplets/current")
                    .build())
                .link("droplets", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/droplets")
                    .build())
                .link("tasks", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/tasks")
                    .build())
                .link("start", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/actions/start")
                    .method("POST")
                    .build())
                .link("stop", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/actions/stop")
                    .method("POST")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getCurrentDroplet() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/apps/test-application-id/droplets/current")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/apps/GET_{id}_droplets_current_response.json")
                .build())
            .build());

        this.applications
            .getCurrentDroplet(GetApplicationCurrentDropletRequest.builder()
                .applicationId("test-application-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetApplicationCurrentDropletResponse.builder()
                .id("585bc3c1-3743-497d-88b0-403ad6b56d16")
                .state(DropletState.STAGED)
                .error(null)
                .lifecycle(Lifecycle.builder()
                    .type(LifecycleType.BUILDPACK)
                    .data(BuildpackData.builder()
                        .build())
                    .build())
                .executionMetadata("")
                .processType("rake", "bundle exec rake")
                .processType("web", "bundle exec rackup config.ru -p $PORT")
                .checksum(Checksum.builder()
                    .type(ChecksumType.SHA256)
                    .value("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")
                    .build())
                .buildpack(Buildpack.builder()
                    .name("ruby_buildpack")
                    .detectOutput("ruby 1.6.14")
                    .build())
                .stack("cflinuxfs2")
                .image(null)
                .createdAt("2016-03-28T23:39:34Z")
                .updatedAt("2016-03-28T23:39:47Z")
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/droplets/585bc3c1-3743-497d-88b0-403ad6b56d16")
                    .build())
                .link("package", Link.builder()
                    .href("https://api.example.org/v3/packages/8222f76a-9e09-4360-b3aa-1ed329945e92")
                    .build())
                .link("app", Link.builder()
                    .href("https://api.example.org/v3/apps/7b34f1cf-7e73-428a-bb5a-8a17a8058396")
                    .build())
                .link("assign_current_droplet", Link.builder()
                    .href("https://api.example.org/v3/apps/7b34f1cf-7e73-428a-bb5a-8a17a8058396/relationships/current_droplet")
                    .method("PATCH")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getCurrentDropletRelationship() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/apps/test-application-id/relationships/current_droplet")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/apps/GET_{id}_relationships_current_droplet_response.json")
                .build())
            .build());

        this.applications
            .getCurrentDropletRelationship(GetApplicationCurrentDropletRelationshipRequest.builder()
                .applicationId("test-application-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetApplicationCurrentDropletRelationshipResponse.builder()
                .data(Relationship.builder()
                    .id("9d8e007c-ce52-4ea7-8a57-f2825d2c6b39")
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/apps/d4c91047-7b29-4fda-b7f9-04033e5c9c9f/relationships/current_droplet")
                    .build())
                .link("related", Link.builder()
                    .href("https://api.example.org/v3/apps/d4c91047-7b29-4fda-b7f9-04033e5c9c9f/droplets/current")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getEnvironment() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/apps/test-application-id/env")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/apps/GET_{id}_env_response.json")
                .build())
            .build());

        this.applications
            .getEnvironment(GetApplicationEnvironmentRequest.builder()
                .applicationId("test-application-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetApplicationEnvironmentResponse.builder()
                .stagingEnvironmentVariable("GEM_CACHE", "http://gem-cache.example.org")
                .runningEnvironmentVariable("HTTP_PROXY", "http://proxy.example.org")
                .environmentVariable("RAILS_ENV", "production")
                .systemEnvironmentVariable("VCAP_SERVICES", FluentMap.builder()
                    .entry("mysql", Collections.singletonList(FluentMap.builder()
                        .entry("name", "db-for-my-app")
                        .entry("label", "mysql")
                        .entry("tags", Arrays.asList("relational", "sql"))
                        .entry("plan", "xlarge")
                        .entry("credentials", FluentMap.builder()
                            .entry("username", "user")
                            .entry("password", "top-secret")
                            .build())
                        .entry("syslog_drain_url", "https://syslog.example.org/drain")
                        .entry("provider", null)
                        .build()))
                    .build())
                .applicationEnvironmentVariable("VCAP_APPLICATION", FluentMap.builder()
                    .entry("limits", FluentMap.builder()
                        .entry("fds", 16384)
                        .build())
                    .entry("application_name", "my_app")
                    .entry("application_uris", Collections.singletonList("my_app.example.org"))
                    .entry("name", "my_app")
                    .entry("space_name", "my_space")
                    .entry("space_id", "2f35885d-0c9d-4423-83ad-fd05066f8576")
                    .entry("uris", Collections.singletonList("my_app.example.org"))
                    .entry("users", null)
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getEnvironmentVariables() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/apps/test-application-id/environment_variables")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/apps/GET_{id}_environment_variables_response.json")
                .build())
            .build());

        this.applications
            .getEnvironmentVariables(GetApplicationEnvironmentVariablesRequest.builder()
                .applicationId("test-application-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetApplicationEnvironmentVariablesResponse.builder()
                .var("RAILS_ENV", "production")
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/apps/[guid]/environment_variables")
                    .build())
                .link("app", Link.builder()
                    .href("https://api.example.org/v3/apps/[guid]")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getProcess() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/apps/test-application-id/processes/test-type")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/apps/GET_{id}_processes_{type}_response.json")
                .build())
            .build());

        this.applications
            .getProcess(GetApplicationProcessRequest.builder()
                .applicationId("test-application-id")
                .type("test-type")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetApplicationProcessResponse.builder()
                .id("6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                .type("web")
                .command("rackup")
                .instances(5)
                .memoryInMb(256)
                .diskInMb(1_024)
                .healthCheck(HealthCheck.builder()
                    .type(HealthCheckType.PORT)
                    .data(Data.builder()
                        .timeout(null)
                        .endpoint(null)
                        .build())
                    .build())
                .createdAt("2016-03-23T18:48:22Z")
                .updatedAt("2016-03-23T18:48:42Z")
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                    .build())
                .link("scale", Link.builder()
                    .href("https://api.example.org/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82/actions/scale")
                    .method("POST")
                    .build())
                .link("app", Link.builder()
                    .href("https://api.example.org/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                    .build())
                .link("space", Link.builder()
                    .href("https://api.example.org/v3/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                    .build())
                .link("stats", Link.builder()
                    .href("https://api.example.org/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82/stats")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void getProcessStatistics() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/apps/test-id/processes/test-type/stats")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/apps/GET_{id}_processes_{type}_stats_response.json")
                .build())
            .build());

        this.applications
            .getProcessStatistics(GetApplicationProcessStatisticsRequest.builder()
                .applicationId("test-id")
                .type("test-type")
                .build())
            .as(StepVerifier::create)
            .expectNext(GetApplicationProcessStatisticsResponse.builder()
                .resource(ProcessStatisticsResource.builder()
                    .type("web")
                    .index(0)
                    .state(ProcessState.RUNNING)
                    .usage(ProcessUsage.builder()
                        .time("2016-03-23T23:17:30.476314154Z")
                        .cpu(0.00038711029163348665)
                        .memory(19177472)
                        .disk(69705728)
                        .build())
                    .host("10.244.16.10")
                    .instancePort(PortMapping.builder()
                        .external(64546)
                        .internal(8080)
                        .build())
                    .uptime(9042)
                    .memoryQuota(268435456)
                    .diskQuota(1073741824)
                    .fileDescriptorQuota(16384)
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void list() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/apps")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/apps/GET_response.json")
                .build())
            .build());

        this.applications
            .list(ListApplicationsRequest.builder()
                .build())
            .as(StepVerifier::create)
            .expectNext(ListApplicationsResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(3)
                    .totalPages(2)
                    .first(Link.builder()
                        .href("https://api.example.org/v3/apps?page=1&per_page=2")
                        .build())
                    .last(Link.builder()
                        .href("https://api.example.org/v3/apps?page=2&per_page=2")
                        .build())
                    .next(Link.builder()
                        .href("https://api.example.org/v3/apps?page=2&per_page=2")
                        .build())
                    .build())
                .resource(ApplicationResource.builder()
                    .id("1cb006ee-fb05-47e1-b541-c34179ddc446")
                    .name("my_app")
                    .state(ApplicationState.STARTED)
                    .createdAt("2016-03-17T21:41:30Z")
                    .updatedAt("2016-03-18T11:32:30Z")
                    .lifecycle(Lifecycle.builder()
                        .type(LifecycleType.BUILDPACK)
                        .data(BuildpackData.builder()
                            .buildpack("java_buildpack")
                            .stack("cflinuxfs2")
                            .build())
                        .build())
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446")
                        .build())
                    .link("space", Link.builder()
                        .href("https://api.example.org/v3/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                        .build())
                    .link("processes", Link.builder()
                        .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/processes")
                        .build())
                    .link("route_mappings", Link.builder()
                        .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/route_mappings")
                        .build())
                    .link("packages", Link.builder()
                        .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/packages")
                        .build())
                    .link("environment_variables", Link.builder()
                        .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/environment_variables")
                        .build())
                    .link("current_droplet", Link.builder()
                        .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/droplets/current")
                        .build())
                    .link("droplets", Link.builder()
                        .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/droplets")
                        .build())
                    .link("tasks", Link.builder()
                        .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/tasks")
                        .build())
                    .link("start", Link.builder()
                        .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/actions/start")
                        .method("POST")
                        .build())
                    .link("stop", Link.builder()
                        .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/actions/stop")
                        .method("POST")
                        .build())
                    .build())
                .resource(ApplicationResource.builder()
                    .id("02b4ec9b-94c7-4468-9c23-4e906191a0f8")
                    .name("my_app2")
                    .state(ApplicationState.STOPPED)
                    .createdAt("1970-01-01T00:00:02Z")
                    .updatedAt("2016-06-08T16:41:26Z")
                    .lifecycle(Lifecycle.builder()
                        .type(LifecycleType.BUILDPACK)
                        .data(BuildpackData.builder()
                            .buildpack("ruby_buildpack")
                            .stack("cflinuxfs2")
                            .build())
                        .build())
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/apps/02b4ec9b-94c7-4468-9c23-4e906191a0f8")
                        .build())
                    .link("space", Link.builder()
                        .href("https://api.example.org/v3/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                        .build())
                    .link("processes", Link.builder()
                        .href("https://api.example.org/v3/apps/02b4ec9b-94c7-4468-9c23-4e906191a0f8/processes")
                        .build())
                    .link("route_mappings", Link.builder()
                        .href("https://api.example.org/v3/apps/02b4ec9b-94c7-4468-9c23-4e906191a0f8/route_mappings")
                        .build())
                    .link("packages", Link.builder()
                        .href("https://api.example.org/v3/apps/02b4ec9b-94c7-4468-9c23-4e906191a0f8/packages")
                        .build())
                    .link("environment_variables", Link.builder()
                        .href("https://api.example.org/v3/apps/02b4ec9b-94c7-4468-9c23-4e906191a0f8/environment_variables")
                        .build())
                    .link("current_droplet", Link.builder()
                        .href("https://api.example.org/v3/apps/02b4ec9b-94c7-4468-9c23-4e906191a0f8/droplets/current")
                        .build())
                    .link("droplets", Link.builder()
                        .href("https://api.example.org/v3/apps/02b4ec9b-94c7-4468-9c23-4e906191a0f8/droplets")
                        .build())
                    .link("tasks", Link.builder()
                        .href("https://api.example.org/v3/apps/02b4ec9b-94c7-4468-9c23-4e906191a0f8/tasks")
                        .build())
                    .link("start", Link.builder()
                        .href("https://api.example.org/v3/apps/02b4ec9b-94c7-4468-9c23-4e906191a0f8/actions/start")
                        .method("POST")
                        .build())
                    .link("stop", Link.builder()
                        .href("https://api.example.org/v3/apps/02b4ec9b-94c7-4468-9c23-4e906191a0f8/actions/stop")
                        .method("POST")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listDroplets() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/apps/test-application-id/droplets")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/apps/GET_{id}_droplets_response.json")
                .build())
            .build());

        this.applications
            .listDroplets(ListApplicationDropletsRequest.builder()
                .applicationId("test-application-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(ListApplicationDropletsResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(2)
                    .totalPages(1)
                    .first(Link.builder()
                        .href("https://api.example.org/v3/app/7b34f1cf-7e73-428a-bb5a-8a17a8058396/droplets?page=1&per_page=50")
                        .build())
                    .last(Link.builder()
                        .href("https://api.example.org/v3/app/7b34f1cf-7e73-428a-bb5a-8a17a8058396/droplets?page=1&per_page=50")
                        .build())
                    .build())
                .resource(DropletResource.builder()
                    .id("585bc3c1-3743-497d-88b0-403ad6b56d16")
                    .state(DropletState.STAGED)
                    .error(null)
                    .lifecycle(Lifecycle.builder()
                        .type(LifecycleType.BUILDPACK)
                        .data(BuildpackData.builder()
                            .build())
                        .build())
                    .image(null)
                    .executionMetadata("PRIVATE DATA HIDDEN")
                    .processType("redacted_message", "[PRIVATE DATA HIDDEN IN LISTS]")
                    .checksum(Checksum.builder()
                        .type(ChecksumType.SHA256)
                        .value("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")
                        .build())
                    .buildpack(Buildpack.builder()
                        .name("ruby_buildpack")
                        .detectOutput("ruby 1.6.14")
                        .build())
                    .stack("cflinuxfs2")
                    .createdAt("2016-03-28T23:39:34Z")
                    .updatedAt("2016-03-28T23:39:47Z")
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/droplets/585bc3c1-3743-497d-88b0-403ad6b56d16")
                        .build())
                    .link("package", Link.builder()
                        .href("https://api.example.org/v3/packages/8222f76a-9e09-4360-b3aa-1ed329945e92")
                        .build())
                    .link("app", Link.builder()
                        .href("https://api.example.org/v3/apps/7b34f1cf-7e73-428a-bb5a-8a17a8058396")
                        .build())
                    .link("assign_current_droplet", Link.builder()
                        .href("https://api.example.org/v3/apps/7b34f1cf-7e73-428a-bb5a-8a17a8058396/relationships/current_droplet")
                        .method("PATCH")
                        .build())
                    .build())
                .resource(DropletResource.builder()
                    .id("fdf3851c-def8-4de1-87f1-6d4543189e22")
                    .state(DropletState.STAGED)
                    .error(null)
                    .lifecycle(Lifecycle.builder()
                        .type(LifecycleType.DOCKER)
                        .data(DockerData.builder()
                            .build())
                        .build())
                    .executionMetadata("[PRIVATE DATA HIDDEN IN LISTS]")
                    .processType("redacted_message", "[PRIVATE DATA HIDDEN IN LISTS]")
                    .image("cloudfoundry/diego-docker-app-custom:latest")
                    .checksum(null)
                    .buildpacks(null)
                    .stack(null)
                    .createdAt("2016-03-17T00:00:01Z")
                    .updatedAt("2016-03-17T21:41:32Z")
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/droplets/fdf3851c-def8-4de1-87f1-6d4543189e22")
                        .build())
                    .link("package", Link.builder()
                        .href("https://api.example.org/v3/packages/c5725684-a02f-4e59-bc67-8f36ae944688")
                        .build())
                    .link("app", Link.builder()
                        .href("https://api.example.org/v3/apps/7b34f1cf-7e73-428a-bb5a-8a17a8058396")
                        .build())
                    .link("assign_current_droplet", Link.builder()
                        .href("https://api.example.org/v3/apps/7b34f1cf-7e73-428a-bb5a-8a17a8058396/relationships/current_droplet")
                        .method("PATCH")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listPackages() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/apps/test-application-id/packages")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/apps/GET_{id}_packages_response.json")
                .build())
            .build());

        this.applications
            .listPackages(ListApplicationPackagesRequest.builder()
                .applicationId("test-application-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(ListApplicationPackagesResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(1)
                    .totalPages(1)
                    .first(Link.builder()
                        .href("https://api.example.org/v3/apps/f2efe391-2b5b-4836-8518-ad93fa9ebf69/packages?states=READY&page=1&per_page=50")
                        .build())
                    .last(Link.builder()
                        .href("https://api.example.org/v3/apps/f2efe391-2b5b-4836-8518-ad93fa9ebf69/packages?states=READY&page=1&per_page=50")
                        .build())
                    .build())
                .resource(PackageResource.builder()
                    .id("752edab0-2147-4f58-9c25-cd72ad8c3561")
                    .type(PackageType.BITS)
                    .data(BitsData.builder()
                        .error(null)
                        .checksum(Checksum.builder()
                            .type(ChecksumType.SHA256)
                            .value(null)
                            .build())
                        .build())
                    .state(PackageState.READY)
                    .createdAt("2016-03-17T21:41:09Z")
                    .updatedAt("2016-06-08T16:41:26Z")
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/packages/752edab0-2147-4f58-9c25-cd72ad8c3561")
                        .build())
                    .link("upload", Link.builder()
                        .href("https://api.example.org/v3/packages/752edab0-2147-4f58-9c25-cd72ad8c3561/upload")
                        .method("POST")
                        .build())
                    .link("download", Link.builder()
                        .href("https://api.example.org/v3/packages/752edab0-2147-4f58-9c25-cd72ad8c3561/download")
                        .method("GET")
                        .build())
                    .link("app", Link.builder()
                        .href("https://api.example.org/v3/apps/f2efe391-2b5b-4836-8518-ad93fa9ebf69")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listProcesses() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/apps/test-application-id/processes")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/apps/GET_{id}_processes_response.json")
                .build())
            .build());

        this.applications
            .listProcesses(ListApplicationProcessesRequest.builder()
                .applicationId("test-application-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(ListApplicationProcessesResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(3)
                    .totalPages(2)
                    .first(Link.builder()
                        .href("https://api.example.org/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5/processes?page=1&per_page=2")
                        .build())
                    .last(Link.builder()
                        .href("https://api.example.org/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5/processes?page=2&per_page=2")
                        .build())
                    .next(Link.builder()
                        .href("https://api.example.org/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5/processes?page=2&per_page=2")
                        .build())
                    .build())
                .resource(ProcessResource.builder()
                    .id("6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                    .type("web")
                    .command("[PRIVATE DATA HIDDEN IN LISTS]")
                    .instances(5)
                    .memoryInMb(256)
                    .diskInMb(1_024)
                    .healthCheck(HealthCheck.builder()
                        .type(HealthCheckType.PORT)
                        .data(Data.builder()
                            .timeout(null)
                            .endpoint(null)
                            .build())
                        .build())
                    .createdAt("2016-03-23T18:48:22Z")
                    .updatedAt("2016-03-23T18:48:42Z")
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                        .build())
                    .link("scale", Link.builder()
                        .href("https://api.example.org/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82/actions/scale")
                        .method("POST")
                        .build())
                    .link("app", Link.builder()
                        .href("https://api.example.org/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                        .build())
                    .link("space", Link.builder()
                        .href("https://api.example.org/v3/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                        .build())
                    .link("stats", Link.builder()
                        .href("https://api.example.org/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82/stats")
                        .build())
                    .build())
                .resource(ProcessResource.builder()
                    .id("3fccacd9-4b02-4b96-8d02-8e865865e9eb")
                    .type("worker")
                    .command("[PRIVATE DATA HIDDEN IN LISTS]")
                    .instances(1)
                    .memoryInMb(256)
                    .diskInMb(1_024)
                    .healthCheck(HealthCheck.builder()
                        .type(HealthCheckType.PROCESS)
                        .data(Data.builder()
                            .timeout(null)
                            .endpoint(null)
                            .build())
                        .build())
                    .createdAt("2016-03-23T18:48:22Z")
                    .updatedAt("2016-03-23T18:48:42Z")
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/processes/3fccacd9-4b02-4b96-8d02-8e865865e9eb")
                        .build())
                    .link("scale", Link.builder()
                        .href("https://api.example.org/v3/processes/3fccacd9-4b02-4b96-8d02-8e865865e9eb/actions/scale")
                        .method("POST")
                        .build())
                    .link("app", Link.builder()
                        .href("https://api.example.org/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                        .build())
                    .link("space", Link.builder()
                        .href("https://api.example.org/v3/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                        .build())
                    .link("stats", Link.builder()
                        .href("https://api.example.org/v3/processes/3fccacd9-4b02-4b96-8d02-8e865865e9eb/stats")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void listTasks() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/apps/test-application-id/tasks")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/apps/GET_{id}_tasks_response.json")
                .build())
            .build());

        this.applications
            .listTasks(ListApplicationTasksRequest.builder()
                .applicationId("test-application-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(ListApplicationTasksResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(3)
                    .totalPages(2)
                    .first(Link.builder()
                        .href("https://api.example.org/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5/tasks?page=1&per_page=2")
                        .build())
                    .last(Link.builder()
                        .href("https://api.example.org/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5/tasks?page=2&per_page=2")
                        .build())
                    .next(Link.builder()
                        .href("https://api.example.org/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5/tasks?page=2&per_page=2")
                        .build())
                    .build())
                .resource(TaskResource.builder()
                    .id("d5cc22ec-99a3-4e6a-af91-a44b4ab7b6fa")
                    .sequenceId(1)
                    .name("hello")
                    .state(TaskState.SUCCEEDED)
                    .memoryInMb(512)
                    .diskInMb(1024)
                    .result(Result.builder()
                        .build())
                    .dropletId("740ebd2b-162b-469a-bd72-3edb96fabd9a")
                    .createdAt("2016-05-04T17:00:41Z")
                    .updatedAt("2016-05-04T17:00:42Z")
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/tasks/d5cc22ec-99a3-4e6a-af91-a44b4ab7b6fa")
                        .build())
                    .link("app", Link.builder()
                        .href("https://api.example.org/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                        .build())
                    .link("cancel", Link.builder()
                        .href("https://api.example.org/v3/tasks/d5cc22ec-99a3-4e6a-af91-a44b4ab7b6fa/actions/cancel")
                        .method("POST")
                        .build())
                    .link("droplet", Link.builder()
                        .href("https://api.example.org/v3/droplets/740ebd2b-162b-469a-bd72-3edb96fabd9a")
                        .build())
                    .build())
                .resource(TaskResource.builder()
                    .id("63b4cd89-fd8b-4bf1-a311-7174fcc907d6")
                    .sequenceId(2)
                    .name("migrate")
                    .state(TaskState.FAILED)
                    .memoryInMb(512)
                    .diskInMb(1024)
                    .result(Result.builder()
                        .failureReason("Exited with status 1")
                        .build())
                    .dropletId("740ebd2b-162b-469a-bd72-3edb96fabd9a")
                    .createdAt("2016-05-04T17:00:41Z")
                    .updatedAt("2016-05-04T17:00:42Z")
                    .link("self", Link.builder()
                        .href("https://api.example.org/v3/tasks/63b4cd89-fd8b-4bf1-a311-7174fcc907d6")
                        .build())
                    .link("app", Link.builder()
                        .href("https://api.example.org/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                        .build())
                    .link("cancel", Link.builder()
                        .href("https://api.example.org/v3/tasks/63b4cd89-fd8b-4bf1-a311-7174fcc907d6/actions/cancel")
                        .method("POST")
                        .build())
                    .link("droplet", Link.builder()
                        .href("https://api.example.org/v3/droplets/740ebd2b-162b-469a-bd72-3edb96fabd9a")
                        .build())
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void scale() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PUT).path("/apps/test-application-id/processes/test-type/actions/scale")
                .payload("fixtures/client/v3/apps/PUT_{id}_processes_{type}_actions_scale_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/apps/PUT_{id}_processes_{type}_actions_scale_response.json")
                .build())
            .build());

        this.applications
            .scale(ScaleApplicationRequest.builder()
                .applicationId("test-application-id")
                .type("test-type")
                .instances(5)
                .memoryInMb(256)
                .diskInMb(1_024)
                .build())
            .as(StepVerifier::create)
            .expectNext(ScaleApplicationResponse.builder()
                .id("6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                .type("web")
                .command("rackup")
                .instances(5)
                .memoryInMb(256)
                .diskInMb(1_024)
                .healthCheck(HealthCheck.builder()
                    .type(HealthCheckType.PORT)
                    .data(Data.builder()
                        .timeout(null)
                        .endpoint(null)
                        .build())
                    .build())
                .createdAt("2016-03-23T18:48:22Z")
                .updatedAt("2016-03-23T18:48:42Z")
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                    .build())
                .link("scale", Link.builder()
                    .href("https://api.example.org/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82/actions/scale")
                    .method("POST")
                    .build())
                .link("app", Link.builder()
                    .href("https://api.example.org/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                    .build())
                .link("space", Link.builder()
                    .href("https://api.example.org/v3/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                    .build())
                .link("stats", Link.builder()
                    .href("https://api.example.org/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82/stats")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void setCurrentDroplet() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PATCH).path("/apps/test-application-id/relationships/current_droplet")
                .payload("fixtures/client/v3/apps/PATCH_{id}_relationships_current_droplet_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/apps/PATCH_{id}_relationships_current_droplet_response.json")
                .build())
            .build());

        this.applications
            .setCurrentDroplet(SetApplicationCurrentDropletRequest.builder()
                .data(Relationship.builder()
                    .id("[droplet_guid]")
                    .build())
                .applicationId("test-application-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(SetApplicationCurrentDropletResponse.builder()
                .data(Relationship.builder()
                    .id("9d8e007c-ce52-4ea7-8a57-f2825d2c6b39")
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/apps/d4c91047-7b29-4fda-b7f9-04033e5c9c9f/relationships/current_droplet")
                    .build())
                .link("related", Link.builder()
                    .href("https://api.example.org/v3/apps/d4c91047-7b29-4fda-b7f9-04033e5c9c9f/droplets/current")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void start() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/apps/test-application-id/actions/start")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/apps/POST_{id}_actions_start_response.json")
                .build())
            .build());

        this.applications
            .start(StartApplicationRequest.builder()
                .applicationId("test-application-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(StartApplicationResponse.builder()
                .id("1cb006ee-fb05-47e1-b541-c34179ddc446")
                .name("my_app")
                .state(ApplicationState.STARTED)
                .createdAt("2016-03-17T21:41:30Z")
                .updatedAt("2016-03-18T11:32:30Z")
                .lifecycle(Lifecycle.builder()
                    .type(LifecycleType.BUILDPACK)
                    .data(BuildpackData.builder()
                        .buildpack("java_buildpack")
                        .stack("cflinuxfs2")
                        .build())
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446")
                    .build())
                .link("space", Link.builder()
                    .href("https://api.example.org/v3/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                    .build())
                .link("processes", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/processes")
                    .build())
                .link("route_mappings", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/route_mappings")
                    .build())
                .link("packages", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/packages")
                    .build())
                .link("environment_variables", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/environment_variables")
                    .build())
                .link("current_droplet", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/droplets/current")
                    .build())
                .link("droplets", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/droplets")
                    .build())
                .link("tasks", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/tasks")
                    .build())
                .link("start", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/actions/start")
                    .method("POST")
                    .build())
                .link("stop", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/actions/stop")
                    .method("POST")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void stop() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(POST).path("/apps/test-application-id/actions/stop")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/apps/POST_{id}_actions_stop_response.json")
                .build())
            .build());

        this.applications
            .stop(StopApplicationRequest.builder()
                .applicationId("test-application-id")
                .build())
            .as(StepVerifier::create)
            .expectNext(StopApplicationResponse.builder()
                .id("1cb006ee-fb05-47e1-b541-c34179ddc446")
                .name("my_app")
                .state(ApplicationState.STOPPED)
                .createdAt("2016-03-17T21:41:30Z")
                .updatedAt("2016-03-18T11:32:30Z")
                .lifecycle(Lifecycle.builder()
                    .type(LifecycleType.BUILDPACK)
                    .data(BuildpackData.builder()
                        .buildpack("java_buildpack")
                        .stack("cflinuxfs2")
                        .build())
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446")
                    .build())
                .link("space", Link.builder()
                    .href("https://api.example.org/v3/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                    .build())
                .link("processes", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/processes")
                    .build())
                .link("route_mappings", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/route_mappings")
                    .build())
                .link("packages", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/packages")
                    .build())
                .link("environment_variables", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/environment_variables")
                    .build())
                .link("current_droplet", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/droplets/current")
                    .build())
                .link("droplets", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/droplets")
                    .build())
                .link("tasks", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/tasks")
                    .build())
                .link("start", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/actions/start")
                    .method("POST")
                    .build())
                .link("stop", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/actions/stop")
                    .method("POST")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void terminateInstance() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(DELETE).path("/apps/test-application-id/processes/test-type/instances/test-index")
                .build())
            .response(TestResponse.builder()
                .status(NO_CONTENT)
                .build())
            .build());

        this.applications
            .terminateInstance(TerminateApplicationInstanceRequest.builder()
                .applicationId("test-application-id")
                .index("test-index")
                .type("test-type")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void update() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PATCH).path("/apps/test-application-id")
                .payload("fixtures/client/v3/apps/PATCH_{id}_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/apps/PATCH_{id}_response.json")
                .build())
            .build());

        this.applications
            .update(UpdateApplicationRequest.builder()
                .applicationId("test-application-id")
                .name("my_app")
                .lifecycle(Lifecycle.builder()
                    .type(LifecycleType.BUILDPACK)
                    .data(BuildpackData.builder()
                        .buildpack("java_buildpack")
                        .build())
                    .build())
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateApplicationResponse.builder()
                .id("1cb006ee-fb05-47e1-b541-c34179ddc446")
                .name("my_app")
                .state(ApplicationState.STARTED)
                .createdAt("2016-03-17T21:41:30Z")
                .updatedAt("2016-03-18T11:32:30Z")
                .lifecycle(Lifecycle.builder()
                    .type(LifecycleType.BUILDPACK)
                    .data(BuildpackData.builder()
                        .buildpack("java_buildpack")
                        .stack("cflinuxfs2")
                        .build())
                    .build())
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446")
                    .build())
                .link("space", Link.builder()
                    .href("https://api.example.org/v3/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                    .build())
                .link("processes", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/processes")
                    .build())
                .link("route_mappings", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/route_mappings")
                    .build())
                .link("packages", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/packages")
                    .build())
                .link("environment_variables", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/environment_variables")
                    .build())
                .link("current_droplet", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/droplets/current")
                    .build())
                .link("droplets", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/droplets")
                    .build())
                .link("tasks", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/tasks")
                    .build())
                .link("start", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/actions/start")
                    .method("POST")
                    .build())
                .link("stop", Link.builder()
                    .href("https://api.example.org/v3/apps/1cb006ee-fb05-47e1-b541-c34179ddc446/actions/stop")
                    .method("POST")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    public void updateEnvironmentVariables() {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(PATCH).path("/apps/test-application-id/environment_variables")
                .payload("fixtures/client/v3/apps/PATCH_{id}_environment_variables_request.json")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v3/apps/PATCH_{id}_environment_variables_response.json")
                .build())
            .build());

        this.applications
            .updateEnvironmentVariables(UpdateApplicationEnvironmentVariablesRequest.builder()
                .applicationId("test-application-id")
                .var("DEBUG", "false")
                .build())
            .as(StepVerifier::create)
            .expectNext(UpdateApplicationEnvironmentVariablesResponse.builder()
                .var("RAILS_ENV", "production")
                .var("DEBUG", "false")
                .link("self", Link.builder()
                    .href("https://api.example.org/v3/apps/[guid]/environment_variables")
                    .build())
                .link("app", Link.builder()
                    .href("https://api.example.org/v3/apps/[guid]")
                    .build())
                .build())
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
