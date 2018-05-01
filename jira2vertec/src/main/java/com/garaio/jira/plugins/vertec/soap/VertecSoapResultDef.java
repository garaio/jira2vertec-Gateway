package com.garaio.jira.plugins.vertec.soap;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mjr0177_2
 * Date: Sep 22, 2011
 * Time: 12:29:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class VertecSoapResultDef {
    public VertecSoapResultDef() {
        member = new ArrayList<String>();
        expressions = new ArrayList<VertecSoapExpression>();
    }

    @XmlElement(name = "member")
    private List<String> member;

    @XmlElement(name = "expression")
    private List<VertecSoapExpression> expressions;

    public List<String> getMembers() {
        return member;
    }

    public List<VertecSoapExpression> getExpressions() {
        return expressions;
    }
}
