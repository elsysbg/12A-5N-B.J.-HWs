package org.elsys.rest;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Team {
	public String name;
	public Date created;
}
