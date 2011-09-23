<%@ page import="java.util.*" %>
<html>
        <head><title>Hello World JSP Page.</title></head>
        <body>
          Your Url was <%= request.getRequestURI() %> <br />
          <% String newUrl = request.getRequestURI().replaceAll(".Hello",".json"); %>
           <jsp:include page="<%= newUrl %>"/>
         
          <h2>HTTP Request Headers Received</h2>
				<table>
				<%
					Enumeration enumeration = request.getHeaderNames();
					while (enumeration.hasMoreElements()) {
						String name = (String) enumeration.nextElement();
						String value = request.getHeader(name);
					    %>
	  					<tr><td><%= name %></td><td><%= value %></td></tr>
					    <%
					   }
			
				   System.out.println("SoftwareFm Header: "+ request.getHeader("SoftwareFm")); 
				%>
				       
			   
        </body>
</html>