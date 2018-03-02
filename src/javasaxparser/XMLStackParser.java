package javasaxparser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLStackParser extends DefaultHandler
{
    
    public static XMLNode load(File xmlFile) throws Exception
    {
        final XMLNode root = new XMLNode();
        List<XMLNode> stack = new ArrayList<XMLNode>();
        
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            
            DefaultHandler handler = new DefaultHandler()
            {
                XMLNode currentNode = null;
                
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
                {
                    XMLNode node = new XMLNode();
                    node.name = qName;
                    
                    for (int i = 0; i < attributes.getLength(); i++)
                    {
                        node.attributes.put(attributes.getQName(i), attributes.getValue(i));
                    } 
                    
                    stack.add(0, node);
                    
                    if (currentNode != null)
                    {
                        if (currentNode.properties.get(qName) != null)
                        {
                            currentNode.properties.get(qName).add(node);
                        } 
                        else
                        {
                            List<XMLNode> newProperty = new ArrayList<XMLNode>();
                            newProperty.add(node);
                            currentNode.properties.put(qName, newProperty);
                        } 
                    } 
                    
                    currentNode = node;
                } 
                
                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException
                {
                    if (stack.size() > 0)
                    {
                        XMLNode poppedNode = stack.remove(0);
                        
                        if (poppedNode != null)
                        {
                            poppedNode.content = poppedNode.content.replace("\n", "").replace("\r", "");
                            poppedNode.content = poppedNode.content.trim();
                            
                            if (stack.isEmpty())
                            {
                                root.name = poppedNode.name;
                                root.content = poppedNode.content;
                                root.properties = poppedNode.properties;
                                root.attributes = poppedNode.attributes;
                                currentNode = null;
                            } 
                            else
                            {
                                currentNode = stack.get(0);
                            } 
                        } 
                    } 
                } 
                
                @Override
                public void characters(char ch[], int start, int length) throws SAXException
                {
                    if (currentNode != null)
                    {
                        currentNode.content = new String (ch, start, length);
                    } 
                } 
            };
            
            saxParser.parse(xmlFile.getAbsoluteFile(), handler);
            
        } 
        catch (Exception e)
        {
            throw e;
        } 
        
        return root;
    } 
    
} 
