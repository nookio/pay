package utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import play.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nookio on 15/8/1.
 */
public class XmlUtil {

    private static Logger.ALogger logger = Logger.of(XmlUtil.class);

    /**
     * 将map数据转换为xml数据
     * @param map
     * @param rootValue 根节点
     * @return
     */
    public static String mapToXmlString(Map<?,?> map,String rootValue){
        StringBuilder result = new StringBuilder();
        result.append("<"+rootValue+">");
        for (Map.Entry<?,?> entry : map.entrySet()){
            result.append("<"+entry.getKey()+">");
            result.append(entry.getValue());
            result.append("</"+entry.getKey()+">");
        }
        result.append("</"+rootValue+">");
        logger.info(result.toString());
        return result.toString();
    }

    /**
     * 对String的xml串解析成为map
     * @param xml
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static Map<String,String> decodeXmlToMap(String xml) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf =DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        InputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        Document doc = docBuilder.parse(in);
        NodeList nodes = doc.getElementsByTagName("xml");
        if (nodes.getLength() != 1){
            throw new RuntimeException("转换失败");
        }
        Node node = nodes.item(0);
        Map<String,String> result = new HashMap<>();
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++){
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE){
                result.put(childNode.getNodeName(),childNode.getNodeValue());
            }
        }

        return result;
    }

    /**
     * 解析document
     * @param document
     * @return
     */
    public static Map<String,String> decodeDocumentToMap(Document document){
        NodeList nodes = document.getElementsByTagName("xml");
        if (nodes.getLength() != 1){
            throw new RuntimeException("转换失败");
        }
        Node node = nodes.item(0);
        Map<String,String> result = new HashMap<>();
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++){
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE){
                result.put(childNode.getNodeName(),childNode.getFirstChild().getNodeValue());
            }
        }

        return result;
    }

}
