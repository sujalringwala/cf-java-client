/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cloudfoundry.client.v2.spaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.v2.FilterParameter;
import org.cloudfoundry.client.v2.PaginatedRequest;

import java.util.List;

/**
 * The request payload for the deprecated List all Domains for the Space operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListSpaceDomainsRequest extends PaginatedRequest implements Validatable {

    /**
     * The id
     *
     * @param id the id
     * @return the id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String id;

    /**
     * The names
     *
     * @param names the names
     * @return the names
     */
    @Getter(onMethod = @__(@FilterParameter("name")))
    private final List<String> names;

    /**
     * The owning organization ids
     *
     * @param owningOrganizationIds the owning organization ids
     * @return the owning organization ids
     */
    @Getter(onMethod = @__(@FilterParameter("owning_organization_guid")))
    private final List<String> owningOrganizationIds;

    /**
     * The space ids
     *
     * @param spaceIds the space ids
     * @return the space ids
     */
    @Getter(onMethod = @__(@FilterParameter("space_guid")))
    private final List<String> spaceIds;

    @Builder
    ListSpaceDomainsRequest(OrderDirection orderDirection, Integer page, Integer resultsPerPage,
                            String id,
                            @Singular List<String> names,
                            @Singular List<String> owningOrganizationIds,
                            @Singular List<String> spaceIds) {
        super(orderDirection, page, resultsPerPage);
        this.id = id;
        this.names = names;
        this.owningOrganizationIds = owningOrganizationIds;
        this.spaceIds = spaceIds;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.id == null) {
            builder.message("id must be specified");
        }

        return builder.build();
    }

}
