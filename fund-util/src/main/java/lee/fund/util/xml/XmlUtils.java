package lee.fund.util.xml;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/27 8:46
 * Desc:
 */
public class XmlUtils {
    private XmlUtils(){}

    public static Map<String, String> parseXml2Map(String filePath){
        SAXReader reader = new SAXReader();
        Map<String, String> xmlMap;
        try {
            Document document = reader.read(new File(filePath));
            Element element = document.getRootElement();
            xmlMap=(Map<String, String>)element.elements().stream().collect(Collectors.toMap(t -> ((Element) t).getName(), t -> ((Element) t).getText()));
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
        return xmlMap;
    }

    public static List<Map<String, String>> parseXml2MapList(String filePath){
        SAXReader reader = new SAXReader();
        List<Map<String, String>> xmlMapList = new ArrayList<>();
        try {
            Document document = reader.read(new File(filePath));
            Element element = document.getRootElement();
            for(Iterator it = element.elementIterator(); it.hasNext();) {
                Element el = (Element) it.next();
                Map<String, String> xmlMap=(Map<String, String>)el.elements().stream().collect(Collectors.toMap(t -> ((Element) t).getName(), t -> ((Element) t).getText()));
                if (!xmlMap.isEmpty()) {
                    xmlMapList.add(xmlMap);
                }
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
        return xmlMapList;
    }

    public static String getXmlString(Map<String,String> xmlMap,String key) {
        if (xmlMap.containsKey(key)) {
            return xmlMap.get(key).toString();
        }
        return null;
    }

    public static Integer getXmlInt(Map<String,String> map,String key) {
        if (map.containsKey(key)) {
            return Integer.parseInt(map.get(key).toString());
        }
        return null;
    }

    public static Boolean getXmlBoolean(Map<String,String> map,String key) {
        if (map.containsKey(key)) {
            return Boolean.parseBoolean (map.get(key).toString());
        }
        return null;
    }
}