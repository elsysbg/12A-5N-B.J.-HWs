package org.elsys.rest;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

@Path("/rest")
public class Rest {
	static List<Team> teams = null;
	
	static {
		loadTeams();
	}
	
	@GET
	@Path("/teams")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Team> getTeams() {
		return teams;
	}
	
	@POST
	@Path("/teams")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addTeam(Team team) {
		teams.add(team);
		
		saveTeams();
	}
	
	@DELETE
	@Path("/teams/{name}")
	public void removeTeam(@PathParam("name") String name) {
		int index = findTeamByName(name);
		
		if(index == -1){
			System.out.println("error: team not found: " + name);
			return;
		}
		
		teams.remove(index);
		
		saveTeams();
	}
	
	@POST
	@Path("/teams/{name}/moveup")
	public void moveUp(@PathParam("name") String name) {
		int ix = findTeamByName(name);
		
		if(ix == -1){
			System.out.println("error: team not found: " + name);
			return;
		}
		
		if(ix > 0){
			
			Team tempTeam = teams.get(ix);
			teams.set(ix, teams.get(ix - 1));
			teams.set(ix - 1, tempTeam);
		}
		
		saveTeams();
	}
	
	@POST
	@Path("/teams/{name}/movedown")
	public void moveDown(@PathParam("name") String name) {
		int teamIndex = findTeamByName(name);
		
		if(teamIndex == -1){
			System.out.println("error: team not found: " + name);
			return;
		}
		
		if(teamIndex < teams.size() - 1){
			
			Team tempTeam = teams.get(teamIndex);
			teams.set(teamIndex, teams.get(teamIndex + 1));
			teams.set(teamIndex + 1, tempTeam);
		}
		
		saveTeams();
	}
	
	int findTeamByName(String name){
		
		for(int i = 0; i < teams.size(); i++) {
			if(teams.get(i).name.equals(name)){
				return i;
			}
		}
		
		return -1;
	}
	
	static void loadTeams() {
		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document = builder.parse(new File("data.xml"));
			
			teams = new ArrayList<Team>();
			
			Element root = document.getDocumentElement();
			NodeList children = root.getChildNodes();
			
			for(int i = 0; i < children.getLength(); i++){
				Node node = children.item(i);
				
				if(node.getNodeType() == Node.ELEMENT_NODE){
					Element element = (Element)node;
					

					Team team = new Team();
					team.name = element.getAttribute("name");
					teams.add(team);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	static void saveTeams(){
		try {
			final DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = documentFactory.newDocumentBuilder();
			final Document document = builder.newDocument();
			
			Element root = document.createElement("root");
			
			for(int i = 0; i < teams.size(); i++){
				Element child = document.createElement("team");
				child.setAttribute("name", teams.get(i).name);
				root.appendChild(child);
			}
			
			document.appendChild(root);
			
			final TransformerFactory factory = TransformerFactory.newInstance();
			factory.setAttribute("indent-number", 2);
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			final StreamResult out = new StreamResult(new FileOutputStream(new File("data.xml")));
			transformer.transform(new DOMSource(document), out);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
