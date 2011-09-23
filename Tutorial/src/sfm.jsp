<%@ page import="java.util.*" %>
          <%
             String newUrl = request.getRequestURI().replaceAll(".sfm",".json");
             String userId = request.getHeader("SoftwareFm"); 
	         System.out.println("User: "+ request.getHeader("SoftwareFm") +", Url: " + newUrl);  
	         org.softwareFm.tutorial.Store.store(userId, newUrl);
	      %>
           <jsp:forward page="<%= newUrl %>"/>
			   
        </body>
</html>