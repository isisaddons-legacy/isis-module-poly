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
package org.isisaddons.module.poly.fixture.dom.modules.party;

import java.util.List;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.isisaddons.module.poly.fixture.dom.modules.casemgmt.CaseContent;
import org.isisaddons.module.poly.fixture.dom.modules.comms.CommunicationChannel;
import org.isisaddons.module.poly.fixture.dom.modules.comms.CommunicationChannelOwner;
import org.isisaddons.module.poly.fixture.dom.modules.comms.CommunicationChannelOwnerLink;
import org.isisaddons.module.poly.fixture.dom.modules.comms.CommunicationChannelOwnerLinks;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.util.ObjectContracts;

@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
         column="id")
@javax.jdo.annotations.Version(
        strategy=VersionStrategy.VERSION_NUMBER, 
        column="version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "find", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.isisaddons.module.poly.fixture.dom.modules.party.Party ")
})
@javax.jdo.annotations.Unique(name="Party_name_UNQ", members = {"name"})
@DomainObject(
        objectType = "PARTY",
        bounded = true
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class Party implements CommunicationChannelOwner, CaseContent, Comparable<Party> {

    //region > identification
    public TranslatableString title() {
        return TranslatableString.tr("{name}", "name", getName());
    }

    //endregion

    //region > name (property)

    private String name;

    @Column(allowsNull="false", length = 40)
    @Title(sequence="1")
    @Property(
            editing = Editing.DISABLED
    )
    @PropertyLayout(
            // contributed 'title' property (from CaseContentContributions) is shown instead on tables
            hidden = Where.ALL_TABLES
    )
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // endregion

    //region > updateName (action)

    public Party updateName(
            @Parameter(maxLength = 40)
            @ParameterLayout(named = "New name")
            final String name) {
        setName(name);
        return this;
    }

    public String default0UpdateName() {
        return getName();
    }

    //endregion


    //region > communicationChannels (derived collection)
    @CollectionLayout(
            render = RenderType.EAGERLY
    )
    public List<CommunicationChannel> getCommunicationChannels() {
        final List<CommunicationChannelOwnerLink> ownerLinks =
                communicationChannelOwnerLinks.findByOwner(this);
        return Lists.newArrayList(
                Iterables.transform(ownerLinks, CommunicationChannelOwnerLink.Functions.GET_COMMUNICATION_CHANNEL)
        );
    }
    //endregion

    //region > createCommunicationChannel (action)
    @MemberOrder(name="CommunicationChannel",sequence = "3")
    public CommunicationChannelOwner addCommunicationChannel(
            @ParameterLayout(named = "Details")
            final String details) {

        final CommunicationChannel communicationChannel = container.newTransientInstance(CommunicationChannel.class);
        communicationChannel.setDetails(details);
        container.persist(communicationChannel);
        container.flush();

        communicationChannelOwnerLinks.createLink(communicationChannel, this);
        return this;
    }

    public String validate0AddCommunicationChannel(
            final String details) {
        final List<CommunicationChannel> communicationChannels = getCommunicationChannels();
        for (CommunicationChannel communicationChannel : communicationChannels) {
            if(communicationChannel.getDetails().equals(details)) {
                return "Already have a communication channel with those details";
            }
        }
        return null;
    }

    //endregion

    //region > removeCommunicationChannel (action)
    @MemberOrder(name="CommunicationChannel", sequence = "4")
    public CommunicationChannelOwner removeCommunicationChannel(
            final CommunicationChannel communicationChannel) {

        final List<CommunicationChannelOwnerLink> ownerLinks =
                communicationChannelOwnerLinks.findByOwner(this);
        final CommunicationChannelOwnerLink ownerLink = communicationChannelOwnerLinks.findByCommunicationChannel(communicationChannel);

        if(ownerLinks.contains(ownerLink)) {
            container.removeIfNotAlready(ownerLink);
            container.removeIfNotAlready(communicationChannel);
        }

        return this;
    }

    public String disableRemoveCommunicationChannel(final CommunicationChannel communicationChannel) {
        final List<CommunicationChannelOwnerLink> ownerLinks =
                communicationChannelOwnerLinks.findByOwner(this);
        return ownerLinks.isEmpty()? "Does not own a communication channel": null;
    }
    public String validate0RemoveCommunicationChannel(final CommunicationChannel communicationChannel) {
        final List<CommunicationChannelOwnerLink> ownerLinks =
                communicationChannelOwnerLinks.findByOwner(this);
        final CommunicationChannelOwnerLink ownerLink = communicationChannelOwnerLinks.findByCommunicationChannel(communicationChannel);
        return ownerLinks.contains(ownerLink)? null: "Not a communication channel of this party";
    }

    public List<CommunicationChannel> choices0RemoveCommunicationChannel() {
        final List<CommunicationChannelOwnerLink> ownerLinks =
                communicationChannelOwnerLinks.findByOwner(this);
        return Lists.newArrayList(
                Iterables.transform(ownerLinks, CommunicationChannelOwnerLink.Functions.GET_COMMUNICATION_CHANNEL)
        );
    }

    //endregion



    //region > compareTo

    @Override
    public int compareTo(final Party other) {
        return ObjectContracts.compare(this, other, "name");
    }

    //endregion

    //region > injected services

    @javax.inject.Inject
    private DomainObjectContainer container;

    @javax.inject.Inject
    CommunicationChannelOwnerLinks communicationChannelOwnerLinks;

    //endregion

}
