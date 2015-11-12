package edu.jsu.mcis;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

public class LoadXML {

  private String fileName;
  public String output = "";

  public void setFileName(String fileName){
    this.fileName = fileName;
  }

  protected String getFileName(){
    return fileName;
  }

  public String loadXML() {
    try {
      File xmlFile = new File(getFileName());
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(xmlFile);
      doc.getDocumentElement().normalize();
      NodeList nList = doc.getElementsByTagName("positional");
      for (int temp = 0; temp < nList.getLength(); temp++) {
        Node nNode = nList.item(temp);
        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
          Element eElement = (Element) nNode;
          output = output + ("\n||" + "Arguments : " + eElement.getElementsByTagName("name").item(0).getTextContent() +
          "\ntype : " + eElement.getElementsByTagName("type").item(0).getTextContent() +
          "\ndescription : " + eElement.getElementsByTagName("description").item(0).getTextContent() +
          "\nvalue : " + eElement.getElementsByTagName("position").item(0).getTextContent() + "||\n");
        }
      }
    }
    catch (Exception e) {
      output = output + "NOTHING HERE!" + e;
    }
    return output;
  }

  public String returnLoad(){
    return loadXML();
  }

  public void saveXML() {
    try{

    }
    catch (Exception e) {

    }
  }
}
