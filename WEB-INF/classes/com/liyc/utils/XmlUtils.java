package com.liyc.utils;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;

/**
 * XML处理
 */
public class XmlUtils {
	/**
	 * 字符串转成Document文档
	 * @param xml
	 * @return
	 */
	public static Document genDoc(String xml) {
		StringReader reader = new StringReader(xml);
		InputSource source = new InputSource(reader);
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		try {
			doc = builder.build(source);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * File to Xml Doc
	 */
	public static Document getDoc(File file) {
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		try {
			doc = builder.build(file);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * 生成XML文件
	 * 
	 * @param doc
	 * @param file
	 */
	public static void genXmlFile(Document doc, File file) {
		XMLOutputter out = new XMLOutputter(formatXml());
		try {
			out.output(doc, new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 格式化XML
	 * 
	 * @return
	 */
	private static Format formatXml() {
		Format format = Format.getCompactFormat();
		format.setEncoding("UTF-8");
		format.setIndent("  ");
		return format;

	}

	/**
	 * 打印XML
	 * 
	 * @param doc
	 */
	public static void printXml(Document doc) {
		XMLOutputter out = new XMLOutputter(formatXml());
		System.out.println(out.outputString(doc));
	}

	/**
	 * 修改或添加document里的节点并设置节点值
	 * 
	 * @param doc
	 * @param e
	 * @param text
	 * @return
	 */
	public static Document setText(Document doc, String e, String text) {
		Element root = doc.getRootElement();
		Element target = root.getChild(e);
		if (target != null) {
			target.setText(text);
		} else {
			root.addContent(new Element(e).setText(text));
		}
		return doc;
	}

	/**
	 * 修改或添加节点
	 * 
	 * @param doc
	 * @param e
	 * @param v
	 * @return
	 */
	public static Document setText(Document doc, String e, Integer v) {
		Element root = doc.getRootElement();
		Element target = root.getChild(e);
		if (target != null) {
			target.setText(String.valueOf(v));
		} else {
			root.addContent(new Element(e).setText(String.valueOf(v)));
		}
		return doc;
	}

	/**
	 * 添加或修改节点及节点属性
	 * 
	 * @param doc
	 * @param e
	 * @param v
	 * @param a
	 * @param attr
	 * @return
	 */
	public static Document setElement(Document doc, String e, Integer v,
			String a, String attr) {
		Element root = doc.getRootElement();
		Element target = root.getChild(e);
		if (target != null) {
			target.setText(String.valueOf(v));
			Attribute attribute = target.getAttribute(a).setValue(attr);
			if (attribute != null) {
				attribute.setValue(attr);
			} else {
				target.setAttribute(new Attribute(a, attr));
			}
		} else {
			root.addContent(new Element(e).setText(String.valueOf(v))
					.setAttribute(new Attribute(a, attr)));
		}
		return doc;
	}
	/**
	 * 批量设置节点
	 * @param doc
	 * @param e
	 * @return
	 */
	public static Document setElement(Document doc, Map<String, String> e) {
		Element root = doc.getRootElement();
		for (String node : e.keySet()) {
			if (root.getChild(node) != null) {
				root.getChild(node).setText(e.get(node));
			} else {
				root.addContent(new Element(node).setText(e.get(node)));
			}
		}
		return doc;
	}
	/**
	 * 批量设置节点属性
	 * @param doc
	 * @param node
	 * @param attrs
	 * @return
	 */
	public static Document setAttribute(Document doc, String node,
			Map<String, String> attrs) {
		Element root = doc.getRootElement();
		Element target = root.getChild(node);
		if (target != null) {
			for (String name : attrs.keySet()) {
				Attribute attribute = target.getAttribute(name);
				if (attribute != null) {
					attribute.setValue(attrs.get(name));
				} else {
					target.setAttribute(new Attribute(name, attrs.get(name)));
				}
			}
		}
		return doc;
	}

	/**
	 * 修改或添加属性
	 * 
	 * @param doc
	 * @param e
	 * @param attribute
	 * @param v
	 * @return
	 */
	public static Document setAttribute(Document doc, String e,
			String attribute, String v) {
		Element root = doc.getRootElement();
		Element target = root.getChild(e);
		if (target != null) {
			target.getAttribute(attribute).setValue(v);
		} else {
			root.addContent(new Element(e).setAttribute(attribute, v));
		}
		return doc;
	}

	/**
	 * XML转String
	 * 
	 * @param doc
	 * @return
	 */
	public static String docToString(Document doc) {
		String xmlStr = "";
		XMLOutputter out = new XMLOutputter(formatXml());
		xmlStr = out.outputString(doc);
		return xmlStr;
	}
	/**
	 * 将对象转成xml，并输出
	 */
	public static String marshaller(Object o, Class<?> T) {
		JAXBContext jc;
		Marshaller marshaller;
		StringWriter writer = new StringWriter();
		try {
			jc = JAXBContext.newInstance(T);
			marshaller = jc.createMarshaller();
			marshaller.marshal(o, writer);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return writer.toString();

	}
	/**
	 * 将字符串xml转成对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T unmarshaller(String xml, Class<?> T) {
		JAXBContext jc;
		Unmarshaller unmarshaller;
		Object o = null;
		try {
			jc = JAXBContext.newInstance(T);
			unmarshaller = jc.createUnmarshaller();
			o = unmarshaller.unmarshal(new StreamSource(new StringReader(xml)));
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return (T) o;
	}
}
