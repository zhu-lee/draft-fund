package lee.fund.util.xml;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/27 8:46
 * Desc:
 */
public class XmlUtils {
    private XmlUtils() {
    }

    public static Map<String, Object> parseMap(String filePath) {
        SAXReader reader = new SAXReader();
        Map<String, Object> rootMap = null;
        try {
            Document document = reader.read(new File(filePath));
            Element el = document.getRootElement();
            if (el.elements().size() > 0) {
                rootMap = (Map<String, Object>) el.elements().stream().collect(Collectors.toMap(t -> ((Element) t).attribute(0).getValue(), t -> ((Element) t).attribute(1).getValue()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rootMap;
    }

    public static Map<String, Object> parseMultiMap(String filePath) {
        SAXReader reader = new SAXReader();
        Map<String, Object> rootMap = null;
        try {
            Document document = reader.read(new File(filePath));
            Element element = document.getRootElement();
            int i = 1;
            if (element.elements().size() > 0) {
                rootMap = new HashMap<>();
                for (Iterator it = element.elementIterator(); it.hasNext(); ) {
                    Element el = (Element) it.next();
                    Map<String, Object> elMap = (Map<String, Object>) el.attributes().stream().collect(Collectors.toMap(t -> ((Attribute) t).getName(), t -> ((Attribute) t).getValue()));
                    Map<String, Object> iteMap = (Map<String, Object>) el.elements().stream().collect(Collectors.toMap(t -> ((Element) t).attribute(0).getValue(), t -> ((Element) t).attribute(1).getValue()));
                    if (elMap.size() > 0) {
                        if (iteMap.size() > 0) {
                            elMap.put("option", iteMap);
                        }
                    } else if (iteMap.size() > 0) {
                        elMap = iteMap;
                    }
                    if (rootMap.containsKey(el.getName())) {
                        rootMap.put(el.getName() + (i++), elMap);
                    } else {
                        rootMap.put(el.getName(), elMap);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rootMap;
    }
}