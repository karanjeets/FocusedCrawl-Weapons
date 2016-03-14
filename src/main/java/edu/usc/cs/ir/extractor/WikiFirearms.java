package edu.usc.cs.ir.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class WikiFirearms {

       private String getFirearms() throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException, XPathExpressionException {
               URL url = new URL("https://en.wikipedia.org/wiki/List_of_firearms");
               URLConnection connection = url.openConnection();
        InputStream is = connection.getInputStream();
        /*
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null)
            sb.append(line);
        reader.close();

        System.out.println(sb.toString());
        */
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder;
        Document doc;
        
        docBuilder = docFactory.newDocumentBuilder();
        doc = docBuilder.parse(is);
        
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        
        XPathExpression expression = xpath.compile("//li");
        NodeList a = (NodeList) expression.evaluate(doc, XPathConstants.NODESET);
        
        Set<String> set = new LinkedHashSet<String>();
        
        for(int i = 0; i < a.getLength(); i++) {
               Node node = a.item(i);
               if(node.getTextContent().trim().length() == 0 || !node.hasChildNodes() || !node.getFirstChild().hasAttributes() || node.getFirstChild().getAttributes().getNamedItem("title") == null)
                       continue;
               String str = node.getTextContent().trim();
               str = str.replaceAll("\\(.*?-", "");
               String[] arr = str.split("\\n");
               for(String s: arr) {
                       s = s.replaceAll("(\\r|\\n)", " ");
               set.add(s);
               }
               
               //expression = xpath.compile("//a[@title != '']");
               //NodeList b = (NodeList) expression.evaluate(node, XPathConstants.NODESET);
               //System.out.println(b.item(0).getTextContent());
        }
        
        for (String s: set) {
               System.out.println(s);
        }
        
        //System.out.println(a.getLength());
        //System.out.println(a.item(0).getNodeValue());
        
        //System.out.println(doc.getElementsByTagName("li").item(0).getTextContent());
               return null;
       }
       
       public static void main(String args[]) {
               WikiFirearms obj = new WikiFirearms();
               try {
                       obj.getFirearms();
               } catch (UnsupportedEncodingException e) {
                       // TODO Auto-generated catch block
                       e.printStackTrace();
               } catch (IOException e) {
                       // TODO Auto-generated catch block
                       e.printStackTrace();
               } catch (ParserConfigurationException e) {
                       // TODO Auto-generated catch block
                       e.printStackTrace();
               } catch (SAXException e) {
                       // TODO Auto-generated catch block
                       e.printStackTrace();
               } catch (XPathExpressionException e) {
                       // TODO Auto-generated catch block
                       e.printStackTrace();
               }
       }
       
}

