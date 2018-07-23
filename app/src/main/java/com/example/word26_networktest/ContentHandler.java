package com.example.word26_networktest;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ContentHandler extends DefaultHandler{//使用SAX解析
    private static final String TAG = "ContentHandler";
    private String nodeName;
    private StringBuilder icon;
    private StringBuilder title;
    private StringBuilder content;
    private StringBuilder type;
    private StringBuilder comment;


    @Override
    public void startDocument() throws SAXException {//开始解析xml时调用
        icon=new StringBuilder();
        title=new StringBuilder();
        content=new StringBuilder();
        type=new StringBuilder();
        comment=new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {//开始解析某个节点时调用
        nodeName=localName;//记录当前节点名
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {//获取节点的内容时调用
        //判断当前节点内容添加到哪个节点里面
        if("icon".equals(nodeName)){
            icon.append(ch,start,length);
        }
        else if("title".equals(nodeName)){
            title.append(ch,start,length);
        }
        else if("content".equals(nodeName)){
            content.append(ch,start,length);
        }
        else if("type".equals(nodeName)){
            type.append(ch,start,length);
        }
        else if("comment".equals(nodeName)){
            comment.append(ch,start,length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {//完成解析某个节点时调用
        if("newsInfo".equals(localName)){
            Log.d("aaa", "endElement: icon:"+icon.toString().trim());
            icon.setLength(0);
            title.setLength(0);
            content.setLength(0);
            type.setLength(0);
            content.setLength(0);
        }
    }

    @Override
    public void endDocument() throws SAXException {//完成整个XML解析的时候调用
        super.endDocument();
    }
}
