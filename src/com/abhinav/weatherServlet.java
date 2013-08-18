package com.abhinav;

import java.io.IOException;
import javax.servlet.http.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@SuppressWarnings("serial")
public class weatherServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/html");
		resp.getWriter().println("<!DOCTYPE html><html><head><meta name='viewport' content='initial-scale=1.0, user-scalable=no' /><style type='text/css'>html { height: 100% } body { height: 100%; margin: 0px; padding: 0px }</style><script type='text/javascript' src='http://maps.google.com/maps/api/js?sensor=false'></script><script type='text/javascript'> function initialize(lat,lng) {var latlng = new google.maps.LatLng(lat, lng); var myOptions = {zoom: 11, center: latlng, mapTypeId: google.maps.MapTypeId.ROADMAP }; var map = new google.maps.Map(document.getElementById('map_canvas'), myOptions); var marker = new google.maps.Marker({position: latlng}); marker.setMap(map);} </script></head><body>");
		
		resp.getWriter().println("<center><img src='/weather.jpg'></center><br><br><br>");
		String place=req.getParameter("p");
		String country=req.getParameter("c");
		Document dom=null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		NodeList nl;
		Element el;
		try {
			db = dbf.newDocumentBuilder();
			dom = db.parse("http://query.yahooapis.com/v1/public/yql?q=select%20*from%20geo.places%20where%20text%3D%22"+place+"%20"+country+"%22&format=xml");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nl= dom.getDocumentElement().getElementsByTagName("woeid");
		el = (Element) nl.item(0);
		String woeid = el.getFirstChild().getNodeValue();
		
		try {
			db = dbf.newDocumentBuilder();
			dom=db.parse("http://weather.yahooapis.com/forecastrss?w="+woeid+"&u=c");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nl= dom.getDocumentElement().getElementsByTagName("title");
		el = (Element) nl.item(0);
		String title = el.getFirstChild().getNodeValue();
		if(title.indexOf("Error")!=-1||title.indexOf("error")!=-1){
			resp.getWriter().println("<h1>Error: City not found</h1>");
		}
		else{
			resp.getWriter().println("<div id='map_canvas' style='width:600px; height:400px; float: right;'></div>");
			resp.getWriter().println("<p style='font-size: 30px;'><u>Current Weather :</u></p>");
			nl= dom.getDocumentElement().getElementsByTagName("yweather:condition");
			el = (Element) nl.item(0);
			resp.getWriter().println("<img src='http://l.yimg.com/a/i/us/we/52/"+el.getAttribute("code")+".gif' style='float: left;'>");
			
			nl= dom.getDocumentElement().getElementsByTagName("yweather:location");
			el = (Element) nl.item(0);
			resp.getWriter().println(" <b>"+el.getAttribute("city")+"</b>");
			resp.getWriter().println("<br> "+el.getAttribute("country")+"<br><br>");
			nl= dom.getDocumentElement().getElementsByTagName("yweather:condition");
			el = (Element) nl.item(0);
			resp.getWriter().println("<span style='color: blue; font-size: 35px;'>"+el.getAttribute("temp")+"<sup>0</sup>C, "+el.getAttribute("text")+"</span><br>");
			nl= dom.getDocumentElement().getElementsByTagName("yweather:atmosphere");
			el = (Element) nl.item(0);
			resp.getWriter().println("Humidity : "+el.getAttribute("humidity")+"%<br>");
			nl= dom.getDocumentElement().getElementsByTagName("yweather:condition");
			el = (Element) nl.item(0);
			resp.getWriter().println(el.getAttribute("date")+"<br><br>");
			nl= dom.getDocumentElement().getElementsByTagName("yweather:forecast");
			el = (Element) nl.item(1);
			resp.getWriter().println("<b>Tomorrow's Forecast :</b><br>Low="+el.getAttribute("low")+"<sup>0</sup>C, High="+el.getAttribute("high")+"<sup>0</sup>C<br>"+el.getAttribute("day")+", "+el.getAttribute("date")+"<br><br>");
			
			nl= dom.getDocumentElement().getElementsByTagName("geo:lat");
			el = (Element) nl.item(0);
			double lat=Double.parseDouble(el.getFirstChild().getNodeValue());
			nl= dom.getDocumentElement().getElementsByTagName("geo:long");
			el = (Element) nl.item(0);
			double lng=Double.parseDouble(el.getFirstChild().getNodeValue());
			
			resp.getWriter().println("<input type='button' value='Show Map' onclick='initialize("+lat+", "+lng+")' style='font-size: 20px;'><br>");
			resp.getWriter().println("<br><br><h2><a href='/weatherdetails.html'>How I Made This Project....</a></h2>");
			resp.getWriter().println("</body></html>");
		}

	}
}
