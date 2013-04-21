package server;

import nu.xom.*;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

import server.*;
import server.simulator.Simulator;
import shared.*;

/**
* One server thread.
* @author Vojtěch Šimša
* @author Ondřej Šlampa
*/
public class ServerThread extends Thread
{
	private Socket socket;
	private Coder coder;
	
	private BufferedReader in;
	private PrintWriter out;
	
	private boolean ready;
	private String username;
	
	private Map<String, Simulator> simulators;
	
	/**
	* Create server thread.
	* @param socket communication socket
	*/
	public ServerThread(Socket socket)
	{
		super();
		this.socket=socket;
		coder=new Coder();
		
		//create input reader and output writer
		try
		{
			in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out=new PrintWriter(new PrintStream(socket.getOutputStream()));
			ready=true;
		}
		catch(UnknownHostException ex)
		{
			ready=false;
			return;
		}
		catch(IOException ex)
		{
			ready=false;			
			return;
		}
	}
	
	/**
	* Closes input, output and communication socket.
	*/
	private void end()
	{
		try
		{
			out.flush();
			out.close();
			in.close();
			socket.close();
		}
		catch(UnknownHostException ex)
		{
			return;
		}
		catch(IOException ex)
		{
			return;
		}
	}
	
	/**
	* Gets request from client.
	* @return request from client
	*/
	private String getRequest()
	{
		try
		{
			String s=in.readLine();
			
			if(s==null)
			{
				return null;
			}
			
			s+=in.readLine();
			s+=in.readLine();
			//System.out.println("Server:"+s);
			//System.out.flush();
			return s;
		}
		catch(IOException ex)
		{
			return null;
		}
	}
	
	/**
	* Sends response to the client.
	* @param response response
	*/
	private void sendResponse(String response)
	{
		out.println(response);
		out.flush();
	}
	
	/**
	* Main method.
	*/
	public void run()
	{
		//is this thread ready
		if(!ready)
		{
			System.err.println("Server thread is not ready.");
			return;
		}
		
		String request_string=null;
		simulators=new HashMap<String, Simulator>();
		
		//get request and do it
		for(;;)
		{
			//get request
			request_string=getRequest();
			
			if(request_string==null)
			{
				break;
			}
			
			//parse request
			Document request=null;
			try
			{
				Builder parser=new Builder(false);
				request=parser.build(new ByteArrayInputStream(request_string.getBytes("UTF-8")));
			}
			catch(ParsingException ex)
			{
				sendResponse(coder.getNotOKResponse());
				continue;
			}
			catch(IOException ex)
			{
				sendResponse(coder.getNotOKResponse());
				continue;
			}
			
			//get request type
			Element net=(Element)request.getRootElement();
			String request_type=net.getAttributeValue("request");
			
			//identify request
			//connection request
			if(request_type.equals("connect"))
			{
				sendResponse(coder.getOKResponse());
			}
			//disconnection request
			else if(request_type.equals("disconnect"))
			{
				break;
			}
			//login request
			else if(username==null && !request_type.equals("login"))
			{
				sendResponse(coder.getNotOKResponse());
			}
			//log in request
			else if(request_type.equals("login"))
			{
				if(server.Main.fm.login(net.getAttributeValue("username"), net.getAttributeValue("password")) && !server.Main.fm.failed)
				{
					username=net.getAttributeValue("username");
					sendResponse(coder.getOKResponse());
				}
				else
				{
					sendResponse(coder.getNotOKResponse());
				}
			}
			//log off request
			else if(request_type.equals("logoff"))
			{
				username=null;
				sendResponse(coder.getOKResponse());
			}
			//registration request
			else if(request_type.equals("register"))
			{
				//only admin can register new users
				if(username.equals("admin"))
				{
					server.Main.fm.register(net.getAttributeValue("username"), net.getAttributeValue("password"));
					
					if(server.Main.fm.failed)
					{
						sendResponse(coder.getNotOKResponse());
					}
					else
					{
						sendResponse(coder.getOKResponse());
					}
				}
				else
				{
					sendResponse(coder.getNotOKResponse());
				}
			}
			//save net request
			else if(request_type.equals("save"))
			{
				server.Main.fm.saveOneNet(request);
				if(!server.Main.fm.failed)
				{
					sendResponse(coder.getOKResponse());
				}
				else
				{
					sendResponse(coder.getNotOKResponse());
				}
			}
			//get nets information request
			else if(request_type.equals("getNets"))
			{
				sendResponse(server.Main.fm.getNets());
			}
			//get net request
			else if(request_type.equals("getNet"))
			{
				//get net
				Document doc=server.Main.fm.openOneNet(net.getAttributeValue("name"), Integer.parseInt(net.getAttributeValue("version")));
				
				//sent net
				if(doc==null)
				{
					sendResponse(coder.getNotOKResponse());
				}
				else
				{
					doc.getRootElement().addAttribute(new Attribute("response", "OK"));
					sendResponse(doc.toXML());
				}
			}
			//simulation request
			else if(request_type.equals("simulate"))
			{
				//get net
				Document net_doc=server.Main.fm.openOneNet(net.getAttributeValue("name"), Integer.parseInt(net.getAttributeValue("version")));
				
				if(server.Main.fm.failed)
				{
					sendResponse(coder.getNotOKResponse());
					continue;
				}
				
				//parse net
				coder.parseDocument(net_doc);
				if(coder.failed)
				{
					sendResponse(coder.getNotOKResponse());
					continue;
				}
				
				//simulate net
				Simulator s=new Simulator(coder.getPlaces(), coder.getTransitions(), coder.getArcs());
				simulators.put(net.getAttributeValue("name")+"_"+Integer.parseInt(net.getAttributeValue("version")), s);
				Document doc=coder.encodeOneNet(s.getPlaces(), s.getTransitions(), s.getArcs(), net.getAttributeValue("name"), net.getAttributeValue("author"), net.getAttributeValue("description"));
				doc.getRootElement().addAttribute(new Attribute("response", "OK"));
				sendResponse(doc.toXML());
			}
			//step request
			else if(request_type.equals("step"))
			{
				//get simulator
				Simulator s=simulators.get(net.getAttributeValue("name")+"_"+Integer.parseInt(net.getAttributeValue("version")));
				
				//step it
				if(s==null)
				{
					sendResponse(coder.getNotOKResponse());
				}
				else
				{
					try
					{
						s.step();
						Document doc=coder.encodeOneNet(s.getPlaces(), s.getTransitions(), s.getArcs(), net.getAttributeValue("name"), net.getAttributeValue("author"), net.getAttributeValue("description"));
						doc.getRootElement().addAttribute(new Attribute("response", "OK"));
						sendResponse(doc.toXML());
					}
					catch(TransitionComputationException e)
					{
						sendResponse(coder.getNotOKResponse());
					}
				}
			}
			//finish request
			else if(request_type.equals("finish"))
			{
				//get simulator
				Simulator s=simulators.get(net.getAttributeValue("name")+"_"+Integer.parseInt(net.getAttributeValue("version")));
				//end it
				if(s==null)
				{
					sendResponse(coder.getNotOKResponse());
				}
				else
				{
					try
					{
						s.finish();
						Document doc=coder.encodeOneNet(s.getPlaces(), s.getTransitions(), s.getArcs(), net.getAttributeValue("name"), net.getAttributeValue("author"), net.getAttributeValue("description"));
						doc.getRootElement().addAttribute(new Attribute("response", "OK"));
						sendResponse(doc.toXML());
					}
					catch(TransitionComputationException e)
					{
						sendResponse(coder.getNotOKResponse());
					}
				}
			}
			//end request
			else if(request_type.equals("end"))
			{
				simulators.put(net.getAttributeValue("name")+"_"+Integer.parseInt(net.getAttributeValue("version")), null);
				sendResponse(coder.getOKResponse());
			}
		}
		
		//end thread
		end();
	}
}

