/**
 * Copyright 2013 GARAIO AG <www.garaio.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jira.plugins.vertec.soap;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Body")
public class VertecSoapBody<T>
{
    @XmlElement(name = "Fault")
    private VertecSoapFault fault;

    private T content;

    public VertecSoapBody()
    {
    }

    public VertecSoapFault getFault()
    {
        return fault;
    }

    public T getContent()
    {
        return content;
    }

    @XmlElementRefs({
            @XmlElementRef(name = "QueryResponse", type = VertecSoapQueryResponse.class),
            @XmlElementRef(name = "Query", type = VertecSoapQuery.class),
            @XmlElementRef(name = "Update", type = VertecSoapUpdate.class),
            @XmlElementRef(name = "UpdateResponse", type = VertecSoapUpdateResponse.class),
            @XmlElementRef(name = "Create", type = VertecSoapCreate.class),
            @XmlElementRef(name = "CreateResponse", type = VertecSoapCreateResponse.class),
            @XmlElementRef(name = "Delete", type = VertecSoapDelete.class),
            @XmlElementRef(name = "DeleteResponse", type = VertecSoapDeleteResponse.class)})
    @XmlAnyElement
    public void setContent(T content)
    {
        this.content = content;
    }
}

