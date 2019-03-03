package com.clkj.micglmusicmixer.update;

import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author coolszy
 * @date 2012-4-26
 * @blog http://blog.92coding.com
 */
public class ParseXmlService {
	public HashMap<String, String> parseXml(InputStream inStream)
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

				if ("version".equals(childElement.getNodeName())) {
					hashMap.put("version", childElement.getFirstChild()
							.getNodeValue());
				}

				else if (("name".equals(childElement.getNodeName()))) {
					hashMap.put("name", childElement.getFirstChild()
							.getNodeValue());
				}

				else if (("check_url".equals(childElement.getNodeName()))) {
					hashMap.put("check_url", childElement.getFirstChild()
							.getNodeValue());
				}

				else if (("download_url".equals(childElement.getNodeName()))) {
					hashMap.put("download_url", childElement.getFirstChild()
							.getNodeValue());
				} else if (("ks1_check_sound_version_cfg_url"
						.equals(childElement.getNodeName()))) {
					hashMap.put("ks1_check_sound_version_cfg_url", childElement   
							.getFirstChild().getNodeValue());
				} else if (("cokv15_check_sound_version_cfg_url"
						.equals(childElement.getNodeName()))) {
					hashMap.put("cokv15_check_sound_version_cfg_url",
							childElement.getFirstChild().getNodeValue());//
				}else if (("tgv14_check_sound_version_cfg_url"
						.equals(childElement.getNodeName()))) {
					hashMap.put("tgv14_check_sound_version_cfg_url",
							childElement.getFirstChild().getNodeValue());
				}else if (("tgmx_check_sound_version_cfg_url"
						.equals(childElement.getNodeName()))) {
					hashMap.put("tgmx_check_sound_version_cfg_url",
							childElement.getFirstChild().getNodeValue());
				}
			}
		}
		return hashMap;
	}
}
