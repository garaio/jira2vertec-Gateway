package com.garaio.jira.plugins.vertec.soap;

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

