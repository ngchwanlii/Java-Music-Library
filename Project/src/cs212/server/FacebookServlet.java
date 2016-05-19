package cs212.server;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import facebook4j.Account;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.ResponseList;
import facebook4j.User;
import facebook4j.conf.Configuration;
import facebook4j.conf.ConfigurationBuilder;

public class FacebookServlet extends MusicLibraryBaseServlet {
	
		private static final String APP_ID = "228334240883505";
		private static final String APP_SECRET_KEY = "00ea36f208739bab42c765f4115f5042";
		private static final String ACCESS_TOKEN = "EAACEdEose0cBAL5Hb3UC46UjGWxvQUllZCrUBdrqFzdUKA0nfdGsMZC324epMufsrtSh3nrSi9BFe75ctGLR5E5MF896sIUkHZCJQaG2p3jAxMyIAasOmAwzJd2Ed8FljXdnkkZClIZBlOAUZBXKKDGolgZBQOdtiZBMHZC0klUrsawZDZD";
	
		// both GET and POST is acceptable
		protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			processRequest(request, response);
		}

		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			processRequest(request, response);
		}
		
		protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
			
			// using fb4j api to integrate with facebook api 
			ConfigurationBuilder cf = new ConfigurationBuilder();
			cf.setDebugEnabled(true);

			// set facebook app id, secret key + access token (Get from facebook when registered with them)
			cf.setOAuthAppId(APP_ID);
			cf.setOAuthAppSecret(APP_SECRET_KEY);
			cf.setOAuthAccessToken(ACCESS_TOKEN);
			
			// set get permission
			cf.setOAuthPermissions("email, name, first_name, last_name");
			// use secure socket layer
			cf.setUseSSL(true);
			// for storing data
			cf.setJSONStoreEnabled(true);
			
			// create configuration obj
			Configuration config = cf.build();
			
			// create fb factory to process these request with fb (integrating..)
			FacebookFactory facebookFactory = new FacebookFactory(config);
			// facebook obj	
			Facebook fb = facebookFactory.getInstance();
			
			try {
				
				// since we only do facebook login - we get username only
				User fbUser = getFacebookLoginUsername(fb);
				System.out.println(fbUser.getName());
				
			}
			catch (FacebookException e){
				e.printStackTrace();
			}
			
			
			
		}
		
		public static User getFacebookLoginUsername(Facebook fb) throws FacebookException{
			
			// get username from facebook
			User user= fb.getMe(); 
		
			return user;

			
		}
	
}
