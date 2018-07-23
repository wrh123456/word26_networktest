package com.example.word26_networktest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private TextView textView;
    private String[] data=new String[10];
    int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                action();//直接输出获得的数据
                okhttpsendRequest();
            }
        });
        textView=findViewById(R.id.text);
    }
    private void action(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                BufferedReader reader=null;
                try {
                    URL url=new URL("https://www.baidu.com");
                    connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(8000);
                    InputStream is=connection.getInputStream();
                    reader=new BufferedReader(new InputStreamReader(is));
                    final StringBuilder stringBuilder=new StringBuilder();
                    String line;
                    while((line=reader.readLine())!=null){
                        stringBuilder.append(line);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(stringBuilder.toString());
                        }
                    });
                    reader.close();
                    is.close();
                    connection.disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                        try {
                            if(reader!=null) {
                                reader.close();
                            }
                            if(connection!=null){
                                connection.disconnect();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }
        }).start();
    }
    private void okhttpsendRequest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient=new OkHttpClient();
                    Request request=new Request.Builder()
                            .url("http://192.168.3.102:9090/newsinfo.xml")
                            .build();
                    Response respons=okHttpClient.newCall(request).execute();
                    final String responBuider=respons.body().string();
//                    parseXMLWithPull(responBuider);//使用Pull解析
                    parseXMLWithSAX(responBuider);//使用SAX解析
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void parseXMLWithSAX(String xmldata){//使用SAX解析
        try {
            SAXParserFactory factory=SAXParserFactory.newInstance();
            XMLReader xmlReader=factory.newSAXParser().getXMLReader();
            ContentHandler handler=new ContentHandler();
            //将ContentHandler的实例放进XMLReader中
            xmlReader.setContentHandler(handler);
            //开始解析
            xmlReader.parse(new InputSource(new StringReader(xmldata)));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void parseXMLWithPull(String xmldata){//使用Pull解析
        try {
            XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser=factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int evenType=xmlPullParser.getEventType();
            String icon="";
            String title="";
            String content="";
            int type=-1;
            int comment=-1;
            while(evenType!=XmlPullParser.END_DOCUMENT) {
                String nodename=xmlPullParser.getName();
                switch (evenType){
                    case XmlPullParser.START_TAG:
                        if("icon".equals(nodename)){
                            icon=xmlPullParser.nextText();
                        }else if("title".equals(nodename)){
                            title=xmlPullParser.nextText();
                        }else if("content".equals(nodename)){
                            content=xmlPullParser.nextText();
                        }else if("type".equals(nodename)){
                            type=Integer.parseInt(xmlPullParser.nextText());
                        }else if("comment".equals(nodename)){
                            comment=Integer.parseInt(xmlPullParser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if("newsInfo".equals(nodename)){
                            final String bb="newsInfo内容："+"icon:"+icon+",title:"+title+",content:"+content+",type:"+type+",comment:"+comment;
                            data[i]=bb;
                            i++;
                        }
                        break;
                        default:break;
                }
                evenType = xmlPullParser.next();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String finaldata=null;
                        for(int k=0;k<i;k++){
                            if(finaldata!=null){
                                finaldata=finaldata+"\n"+data[k]+"\n";
                            }else{
                                finaldata=data[k]+"\n";
                            }

                        }
                        textView.setText(finaldata);
                    }
                });
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
