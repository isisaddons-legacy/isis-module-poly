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
package org.isisaddons.module.poly.fixture.dom.modules.casemgmt;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;
import com.google.common.base.Function;
import org.isisaddons.module.poly.dom.PolymorphicAssociationLink;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;

@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByCase", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.isisaddons.module.poly.fixture.dom.modules.casemgmt.CaseContentLink "
                        + "WHERE case == :case"),
        @javax.jdo.annotations.Query(
                name = "findByContent", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.isisaddons.module.poly.fixture.dom.modules.casemgmt.CaseContentLink "
                        + "WHERE contentObjectType == :contentObjectType "
                        + "   && contentIdentifier == :contentIdentifier ")
})
@javax.jdo.annotations.Unique(name="CaseContentLink_case_content_UNQ", members = {"case","contentObjectType","contentIdentifier"})
@DomainObject(
        objectType = "casemgmt.CaseContentLink"
)
public abstract class CaseContentLink extends PolymorphicAssociationLink<Case, CaseContent, CaseContentLink> {

    public static class InstantiateEvent
            extends PolymorphicAssociationLink.InstantiateEvent<Case, CaseContent, CaseContentLink> {

        public InstantiateEvent(final Object source, final Case aCase, final CaseContent content) {
            super(CaseContentLink.class, source, aCase, content);
        }
    }

    //region > constructor
    public CaseContentLink() {
        super("{subject} contains {polymorphicReference}");
    }
    //endregion

    //region > SubjectPolymorphicReferenceLink API
    @Override
    @Programmatic
    public Case getSubject() {
        return getCase();
    }

    @Override
    @Programmatic
    public void setSubject(final Case subject) {
        setCase(subject);
    }

    @Override
    @Programmatic
    public String getPolymorphicObjectType() {
        return getContentObjectType();
    }

    @Override
    @Programmatic
    public void setPolymorphicObjectType(final String polymorphicObjectType) {
        setContentObjectType(polymorphicObjectType);
    }

    @Override
    @Programmatic
    public String getPolymorphicIdentifier() {
        return getContentIdentifier();
    }

    @Override
    @Programmatic
    public void setPolymorphicIdentifier(final String polymorphicIdentifier) {
        setContentIdentifier(polymorphicIdentifier);
    }
    //endregion

    //region > case (property)
    @NotPersistent // because we (have to) have a non-standard name for this field
    private Case _case;
    @Column(
            allowsNull = "false",
            name = "case_id"
    )
    public Case getCase() {
        return _case;
    }

    public void setCase(final Case aCase) {
        this._case = aCase;
    }
    //endregion

    //region > contentObjectType (property)
    private String contentObjectType;

    @Column(allowsNull = "false", length = 255)
    public String getContentObjectType() {
        return contentObjectType;
    }

    public void setContentObjectType(final String contentObjectType) {
        this.contentObjectType = contentObjectType;
    }
    //endregion

    //region > contentIdentifier (property)
    private String contentIdentifier;

    @Column(allowsNull = "false", length = 255)
    public String getContentIdentifier() {
        return contentIdentifier;
    }

    public void setContentIdentifier(final String contentIdentifier) {
        this.contentIdentifier = contentIdentifier;
    }
    //endregion

    public static class Functions {
        public static Function<CaseContentLink, Case> GET_CASE = new Function<CaseContentLink, Case>() {
            @Override
            public Case apply(final CaseContentLink input) {
                return input.getSubject();
            }
        };
        public static Function<CaseContentLink, CaseContent> GET_CONTENT = new Function<CaseContentLink, CaseContent>() {
            @Override
            public CaseContent apply(final CaseContentLink input) {
                return input.getPolymorphicReference();
            }
        };
    }
}
