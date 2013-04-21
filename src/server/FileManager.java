package server;

import nu.xom.*;
import java.io.*;
import java.net.*;

import server.*;

/**
* Manages files.
* @author Vojtěch Šimša
* @author Ondřej Šlampa
*/
public class FileManager
{
	private File main_directory;
	private File nets_directory;
	private File passwd;
	
	public boolean failed;
	
	/**
	* Creates file manager.
	*/
	public FileManager()
	{
		//check default paths
		if((new File("./examples/server")).exists())
		{
			main_directory=new File("./examples/server");
		}
		else if((new File("../examples/server")).exists())
		{
			main_directory=new File("../examples/server");
		}
		else
		{
			main_directory=new File("./examples/server");
			main_directory.mkdirs();
		}
		
		//directory to store nets in
		nets_directory=new File(main_directory.getAbsolutePath()+"/nets");
			
		if(!nets_directory.exists())
		{
			nets_directory.mkdir();
		}
		
		if(nets_directory.isFile())
		{
			nets_directory.delete();
			nets_directory.mkdir();
		}
		
		passwd=new File(main_directory.getAbsolutePath()+"/passwd");
		
		//create passwd if it doesn't exist
		if(!passwd.exists())
		{
			try
			{
				passwd.createNewFile();
				FileWriter writer=new FileWriter(passwd);
				writer.write("admin:admin\n");
				writer.flush();
				writer.close();
			}
			catch(IOException ex)
			{
				failed=true;
				return;
			}
		}
		failed=false;
	}
	
	/**
	* Registers user.
	* @param username user's name
	* @param password user's password
	*/
	synchronized public void register(String username, String password)
	{
		try
		{
			FileWriter writer=new FileWriter(passwd,true);
			writer.write(username+":"+password+"\n");
			writer.flush();
			writer.close();
		}
		catch(IOException ex)
		{
			failed=true;
			return;
		}
	}
	
	/**
	* Checks if user with this password exists.
	* @param username user's name
	* @param password user's password
	* @return true if user with this passwod exits false otherwise
	*/
	synchronized public boolean login(String username, String password)
	{
		//open passwd
		BufferedReader reader=null;
		try
		{
			reader=new BufferedReader(new FileReader(passwd));
		}
		catch(FileNotFoundException ex)
		{
			failed=true;
			return false;
		}
		
		String line=null;
		boolean result=false;
		
		//check username and password
		try
		{
			while((line=reader.readLine())!=null)
			{
				if(line.equals(username+":"+password))
				{
					result=true;
					break;
				}
			}
			reader.close();
		}
		catch(IOException ex)
		{
			failed=true;
			return false;
		}
		
		failed=false;
		return result;
	}
	
	/**
	* Save one net to a file.
	* @param doc net in document
	*/
	synchronized public void saveOneNet(Document doc)
	{
		//get net name = directory name
		Element net=doc.getRootElement();
		String name=net.getAttributeValue("name");
		File dir=new File(nets_directory.getAbsolutePath()+"/"+name);
		
		if(!dir.exists())
		{
			dir.mkdir();
		}
		
		//get net version = file name
		int version=dir.listFiles(new VersionFilter()).length+1;
		net.addAttribute(new Attribute("version", Integer.toString(version)));
		
		//write net
		try
		{
			FileWriter writer=new FileWriter(new File(dir.getAbsolutePath()+"/"+Integer.toString(version)+".xml"),true);
			writer.write(doc.toXML());
			writer.flush();
			writer.close();
			failed=false;
			return;
		}
		catch(IOException ex)
		{
			failed=true;
			return;
		}	
	}
	
	/**
	* Gets net.
	* @param name net name
	* @param version net version
	*/
	synchronized public Document openOneNet(String name, int version)
	{
		//get file
		File file=new File(nets_directory.getAbsolutePath()+"/"+name+"/"+Integer.toString(version)+".xml");
		
		//parse file
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
			return null;
		}
		catch (IOException ex)
		{
			failed=true;
			return null;
		}
		
		return doc;
	}
	
	/**
	* Gets information about nets stored on server in form of XML.
	* @return information about nets stored on server in form of XML.
	*/
	synchronized public String getNets()
	{
		//nets directory
		File[] dirs=nets_directory.listFiles(new NetFilter());
		
		Element nets=new Element("nets");
		
		//for every net in nets direcory
		for(File dir: dirs)
		{
			Element net=new Element("net");
			
			File[] versions=dir.listFiles(new VersionFilter());
			
			net.addAttribute(new Attribute("name", dir.getName()));
			net.addAttribute(new Attribute("versions", Integer.toString(versions.length)));
			
			//for every version of current net
			for(File file: versions)
			{
				//parse net version
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
					return null;
				}
				catch (IOException ex)
				{
					failed=true;
					return null;
				}
				
				//get net version information
				Element root=doc.getRootElement();
				Element version=new Element("version");
				version.addAttribute(new Attribute("author",root.getAttributeValue("author")));
				version.addAttribute(new Attribute("description",root.getAttributeValue("description")));
				version.addAttribute(new Attribute("no",root.getAttributeValue("version")));
				net.appendChild(version);
			}
			
			nets.appendChild(net);
		}
		
		nets.addAttribute(new Attribute("response", "OK"));
		
		failed=false;
		return new Document(nets).toXML();
	}
}

/**
* Net filter - only nets go through
*/
class NetFilter implements FileFilter
{
	public boolean accept(File file)
	{
		return file.getName().matches("[_a-zA-Z][_a-zA-Z0-9]*") && file.isDirectory();
	}
}

/**
* Version filter - only versions go through
*/
class VersionFilter implements FileFilter
{
	public boolean accept(File file)
	{
		return file.getName().matches("[0-9]+\\.xml") && file.isFile();
	}
}

