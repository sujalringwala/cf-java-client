/*

 * Copyright 2009-2013 the original author or authors.
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

package org.cloudfoundry.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.cloudfoundry.client.lib.domain.CloudService;

/**
 * Delete Services
 *
 * @author Ali Moghadam
 * @author Scott Frederick
 * @since 1.0.0
 *
 * @goal delete-services
 * @phase process-sources
 */

public class DeleteServices extends AbstractApplicationAwareCloudFoundryMojo {

	@Override
	protected void doExecute() throws MojoExecutionException {
		if (null != getServices()) {
			for (CloudService service : getServices()) {
				try {
					getLog().info(String.format("Deleting service '%s'", service.getName()));
					getClient().deleteService(service.getName());
				}
				catch (NullPointerException e) {
					getLog().info(String.format("Service '%s' does not exist", service.getName()));
				}
			}
		} else {
			getLog().info("No services to delete.");
		}
	}
}
