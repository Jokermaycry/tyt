package com.clkj.micglmusicmixer.util;

import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 解析xml工具类
 * @author json_data
 *
 */
public class ParseXmlUtil {
	
	/**
	 * 解析简单xml，返回hashMap
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	public static  HashMap<String, String> parseXml(InputStream inStream)
			throws Exception {
		HashMap<String, String> hashMap = new HashMap<String, String>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		DocumentBuilder builder = factory.newDocumentBuilder();

		Document document = builder.parse(inStream);
		Element root = document.getDocumentElement();

		NodeList childNodes = root.getChildNodes();
		for (int j = 0; j < childNodes.getLength(); j++) {

			Node childNode = (Node) childNodes.item(j);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;

				String nodeName=childElement.getNodeName();
				String nodeValue=childElement.getFirstChild().getNodeValue();
				

				LogUtil.d("nodeName=="
						+ nodeName+"    nodeValue=="+nodeValue);
				hashMap.put(nodeName, nodeValue);
				
			}
		}
		return hashMap;
	}
}
