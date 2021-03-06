/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.isisaddons.module.poly.fixture.scripts.modules.casemgmt;

import org.isisaddons.module.poly.fixture.dom.modules.casemgmt.Case;
import org.isisaddons.module.poly.fixture.dom.modules.casemgmt.Cases;

import org.apache.isis.applib.fixturescripts.FixtureScript;

public class CaseCreate extends FixtureScript {

    //region > name (input)
    private String name;
    public String getName() {
        return name;
    }

    public CaseCreate setName(final String name) {
        this.name = name;
        return this;
    }
    //endregion

    //region > party (output)
    private Case aCase;

    /**
     * The created case (output).
     */
    public Case getCase() {
        return aCase;
    }
    //endregion

    @Override
    protected void execute(final ExecutionContext ec) {

        final String name = checkParam("name", ec, String.class);

        this.aCase = cases.create(name);

        // also make available to UI
        ec.addResult(this, aCase);
    }

    @javax.inject.Inject
    private Cases cases;

}
