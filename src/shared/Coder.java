package shared;

import nu.xom.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.io.*;
import java.awt.Point;

import shared.*;

/**
* Encodes and decodes nets, create requests and responses.
* @author Vojtěch Šimša
* @author Ondřej Šlampa
*/
public class Coder
{
	private List<Place> places;
	private List<Transition> transs;
	private List<Arc> arcs;
	private String name;
	private String author;
	private String description;
	
	/**Failure flag.*/
	public boolean failed;
	
	/**
	* Creates coder.
	*/
	public Coder()
	{
		failed=false;
	}
	
	////////////////////////////////////////////////////////////////////////////
	/*
	* Requests.
	*/
	
	/**
	* Creates connection request.
	* @return connection request
	*/
	public String getConnectionRequest()
	{
		failed=false;
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<net request=\"connect\"/>\n";
	}
	
	/**
	* Creates disconnection request. This request ends all simulations and server thread.
	* @return disconnection request
	*/
	public String getDisconnectionRequest()
	{
		failed=false;
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<net request=\"disconnect\"/>\n";
	}
	
	/**
	* Creates nets request. Response to this request is a list of nets stored on sever.
	* @return nets request
	*/
	public String getNetsRequest()
	{
		failed=false;
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<net request=\"getNets\"/>\n";
	}
	
	/**
	* Creates net request. Response to this request is requested net.
	* @param name net name
	* @param version number
	* @return net request
	*/
	public String getNetRequest(String name, int version)
	{
		failed=false;
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<net request=\"getNet\" name=\""+name+"\" version=\""+version+"\" />\n";
	}
	
	/**
	* Creates registration request.
	* @param username username
	* @param password password
	* @return registration request
	*/
	public String getRegistrationRequest(String username, String password)
	{
		failed=false;
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<net request=\"register\" username=\""+username+"\" password=\""+password+"\" />\n";
	}
	
	/**
	* Creates log in request.
	* @param username username
	* @param password password
	* @return log in request
	*/
	public String getLoginRequest(String username, String password)
	{
		failed=false;
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<net request=\"login\" username=\""+username+"\" password=\""+password+"\" />\n";
	}
	
	/**
	* Creates logoff request.
	* @param username username
	* @return log off request
	*/
	public String getLogoffRequest(String username)
	{
		failed=false;
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<net request=\"logoff\" username=\""+username+"\" />\n";
	}
	
	/**
	* Creates save request. This request saves net on a server. The net is part of the reequest.
	* @param username username
	* @param doc net
	* @return save request
	*/
	public String getSaveRequest(String username, Document doc)
	{
		Element net=doc.getRootElement();
		net.addAttribute(new Attribute("request", "save"));
		net.addAttribute(new Attribute("username", username));
		failed=false;
		return doc.toXML();	
	}
	
	/**
	* Creates simulation request. This request starts simulation on sever.
	* @param name net name
	* @param version net version
	* @return simulation request
	*/
	public String getSimulateRequest(String name, int version)
	{
		failed=false;
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<net request=\"simulate\" name=\""+name+"\" version=\""+version+"\" />\n";
	}
	
	/**
	* Creates step request. This request steps simulation.
	* @param name net name
	* @param version net version
	* @return step request
	*/
	public String getStepRequest(String name, int version)
	{
		failed=false;
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<net request=\"step\" name=\""+name+"\" version=\""+version+"\" />\n";
	}
	
	/**
	* Creates finish request. This request finishes a simulation.
	* @param name net name
	* @param version net version
	* @return finish request
	*/
	public String getFinishRequest(String name, int version)
	{
		failed=false;
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<net request=\"finish\" name=\""+name+"\" version=\""+version+"\" />\n";
	}
	
	/**
	* Creates end request. This request ends simulation.
	* @param name net name
	* @param version net version
	* @return end request
	*/
	public String getEndRequest(String name, int version)
	{
		failed=false;
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<net request=\"end\" name=\""+name+"\" version=\""+version+"\" />\n";
	}
	
	////////////////////////////////////////////////////////////////////////////
	/*
	* Responses.
	*/
	
	/**
	* Creates OK response. This response says
	* @return OK response
	*/
	public String getOKResponse()
	{
		failed=false;
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<net response=\"OK\"/>\n";
	}
	
	/**
	* Creates not OK request.
	* @return not OK request
	*/
	public String getNotOKResponse()
	{
		failed=false;
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<net response=\"notOK\"/>\n";
	}
	
	////////////////////////////////////////////////////////////////////////////
	/*
	* Net encoding and decoding.
	*/
	
	/**
	* Encode one net into a document.
	* @param places net places
	* @param transs net transitions
	* @param arcs net arcs
	* @param name net name
	* @param author net author
	* @param description net description
	*/
	public Document encodeOneNet(List<Place> places, List<Transition> transs, List<Arc> arcs, String name, String author, String description)
	{
		if(author==null)
		{
			author="";
		}
		
		if(description==null)
		{
			description="";
		}
		
		//net attributes
		Element net=new Element("net");
		net.addAttribute(new Attribute("name", name));
		net.addAttribute(new Attribute("version", "n"));
		net.addAttribute(new Attribute("author", author));
		net.addAttribute(new Attribute("description", description));
		
		Element tmp;
		
		//encode places
		for(Place place: places)
		{
			tmp=new Element("place");
			tmp.addAttribute(new Attribute("ID", Integer.toString(place.ID)));
			tmp.addAttribute(new Attribute("x", Integer.toString(place.point.x)));
			tmp.addAttribute(new Attribute("y", Integer.toString(place.point.y)));
			tmp.addAttribute(new Attribute("token", place.token==null?"":place.token));
			net.appendChild(tmp);
		}
		
		//encode transitions
		for(Transition trans: transs)
		{
			tmp=new Element("transition");
			tmp.addAttribute(new Attribute("ID", Integer.toString(trans.ID)));
			tmp.addAttribute(new Attribute("x", Integer.toString(trans.point.x)));
			tmp.addAttribute(new Attribute("y", Integer.toString(trans.point.y)));
			tmp.addAttribute(new Attribute("active", Boolean.toString(trans.active)));
			
			String s="";
			for(String line: trans.guard)
			{
				s+=line+"\\n";
			}
			
			tmp.addAttribute(new Attribute("guard", s));
			net.appendChild(tmp);
		}
		
		//encode arcs
		for(Arc arc: arcs)
		{
			tmp=new Element("arc");
			tmp.addAttribute(new Attribute("ID", Integer.toString(arc.ID)));
			tmp.addAttribute(new Attribute("ID1", Integer.toString(arc.ID1)));
			tmp.addAttribute(new Attribute("ID2", Integer.toString(arc.ID2)));
			tmp.addAttribute(new Attribute("varname", arc.varname));
			net.appendChild(tmp);
		}
		
		failed=false;
		return new Document(net);
	}
	
	/**
	* Save one net into a file.
	* @param places net places
	* @param transs net transitions
	* @param arcs net arcs
	* @param name net name
	* @param author net author
	* @param description net description
	* @param file output file
	*/
	public void saveOneNet(List<Place> places, List<Transition> transs, List<Arc> arcs, String name, String author, String description, File file)
	{
		Document doc=encodeOneNet(places, transs, arcs, name, author, description);
		try
		{
			Serializer serializer=new Serializer(new FileOutputStream(file));
			serializer.setIndent(4);
			serializer.write(doc);
			failed=false;
		}
		catch(IOException ex)
		{
			failed=true;
			return;
		}
	}
	
	/**
	* Opens and decodes one net.
	* @param file input file
	*/
	public void openOneNet(File file)
	{
		//non existing net is empty
		if(!file.exists())
		{
			places=new ArrayList<Place>();
			transs=new ArrayList<Transition>();
			arcs=new ArrayList<Arc>();
			name="";
			description="";
			return;
		}
		
		//open file and parse it into a document
		Document doc=null;
		try
		{
			Builder parser=new Builder(false);
			doc=parser.build(file);
		}
		catch (ValidityException ex)
		{
			doc=ex.getDocument();
		}
		catch (ParsingException ex)
		{
			failed=true;
			return;
		}
		catch (IOException ex)
		{
			failed=true;
			return;
		}
		
		failed=false;
		//parse the document
		parseDocument(doc);
	}
	
	/**
	* Opens and decodes one net.
	* @param net_string input string
	*/
	public void parseOneNet(String net_string)
	{
		//parse string into a document
		Document doc=null;
		try
		{
			Builder parser=new Builder(false);
			doc=parser.build(new ByteArrayInputStream(net_string.getBytes("UTF-8")));
		}
		catch (ValidityException ex)
		{
			doc=ex.getDocument();
		}
		catch (ParsingException ex)
		{
			failed=true;
			return;
		}
		catch (IOException ex)
		{
			failed=true;
			return;
		}
		
		failed=false;
		//parse the document
		parseDocument(doc);
	}
	
	/**
	* Parses documentinto a net.
	* @param doc input document
	*/
	public void parseDocument(Document doc)
	{
		Element net=doc.getRootElement();
		
		if(!net.getQualifiedName().equals("net"))
		{
			failed=true;
			return;
		}
		
		places=new ArrayList<Place>();
		transs=new ArrayList<Transition>();
		arcs=new ArrayList<Arc>();
		
		//get net attributes
		this.name=net.getAttributeValue("name");
		this.author=net.getAttributeValue("author");
		this.description=net.getAttributeValue("description");
		
		//iterate through net components
		for(int i=0;i<net.getChildCount();i++)
		{
			Element child;
			
			try
			{
				child=(Element)net.getChild(i);
			}
			//not a net element - skip it
			catch(ClassCastException ex)
			{
				continue;
			}
			
			String name=child.getQualifiedName();
			
			if(name.equals("place"))
			{
				places.add(new Place(new Point(Integer.parseInt(child.getAttributeValue("x")), Integer.parseInt(child.getAttributeValue("y"))), Integer.parseInt(child.getAttributeValue("ID")), child.getAttributeValue("token")));
			}
			else if(name.equals("transition"))
			{
				transs.add(new Transition(new Point(Integer.parseInt(child.getAttributeValue("x")), Integer.parseInt(child.getAttributeValue("y"))), Integer.parseInt(child.getAttributeValue("ID")), child.getAttributeValue("guard"), Boolean.parseBoolean(child.getAttributeValue("active"))));
			}
			else if(name.equals("arc"))
			{
				arcs.add(new Arc(Integer.parseInt(child.getAttributeValue("ID")), child.getAttributeValue("varname"), Integer.parseInt(child.getAttributeValue("ID1")), Integer.parseInt(child.getAttributeValue("ID2"))));
			}
		}
		
		failed=false;
	}
	
	/**
	* Get places of parsed net.
	* @return net places
	*/
	public List<Place> getPlaces()
	{
		return places;
	}
	
	/**
	* Get transitions of parsed net.
	* @return net transitions
	*/
	public List<Transition> getTransitions()
	{
		return transs;
	}
	
	/**
	* Get arcs of parsed net.
	* @return net arcs
	*/
	public List<Arc> getArcs()
	{
		return arcs;
	}
	
	/**
	* Get name of parsed net.
	* @return name
	*/
	public String getName()
	{
		return name;
	}
	
	/**
	* Get author of parsed net.
	* @return author
	*/
	public String getAuthor()
	{
		return author;
	}
	
	/**
	* Get description of parsed net.
	* @return description
	*/
	public String getDescription()
	{
		return description;
	}
}

