<%@ page session="false" %>
<%@ page import="javax.jcr.*,org.apache.sling.api.resource.*"%>
<%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %>
<sling:defineObjects />
<%        ValueMap properties = resource.adaptTo(ValueMap.class); %>


<html>
        <body style="background-color:#ffffcc;">
           <h1><%= properties.get("title") %></h1>
           <pre>
<%= properties.get("content") %>
		  </pre>
        </body>
</html>