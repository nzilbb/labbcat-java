//
// Copyright 2020 New Zealand Institute of Language, Brain and Behaviour, 
// University of Canterbury
// Written by Robert Fromont - robert.fromont@canterbury.ac.nz
//
//    This file is part of LaBB-CAT.
//
//    LaBB-CAT is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    LaBB-CAT is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with LaBB-CAT; if not, write to the Free Software
//    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//

package nzilbb.labbcat.http;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.jar.JarFile;

/**
 * GET HTTP request, using ordinary default request encoding, and encoding all arguments
 * in the URL string
 * @author Robert Fromont robert@fromont.net.nz
 */

public class HttpRequestGet {

   static Object cookieHandlerSynchronizer = new Object();
   static String UserAgent = null;
   
   // Attributes:
   
   /**
    * Base URL for the request
    * @see #getBaseUrl()
    * @see #setBaseUrl(URL)
    */
   protected URL urlBaseUrl;
   /**
    * Getter for {@link #urlBaseUrl}: Base URL for the request
    * @return Base URL for the request
    */
   public URL getBaseUrl() { return urlBaseUrl; }
   /**
    * Setter for {@link #urlBaseUrl}: Base URL for the request
    * @param urlNewBaseUrl Base URL for the request
    */
   public HttpRequestGet setBaseUrl(URL urlNewBaseUrl) {
      
      urlBaseUrl = urlNewBaseUrl; 
      // double-check there's a cookie handler
      synchronized (cookieHandlerSynchronizer) {
         if (CookieHandler.getDefault() == null)
         {
            CookieHandler.setDefault(new ListCookieHandler());
         }
      }
      return this;
   }
    
   /**
    * Request parameters
    * @see #getParameters()
    * @see #setParameters(HashMap)
    */
   protected HashMap<String,Object> mParameters = new HashMap<String,Object>();
   /**
    * Getter for {@link #mParameters}: Resuest parameters
    * @return Resuest parameters
    */
   public HashMap<String,Object> getParameters() { return mParameters; }
   /**
    * Setter for {@link #mParameters}: Resuest parameters
    * @param mNewParameters Resuest parameters
    */
   public HttpRequestGet setParameters(HashMap<String,Object> mNewParameters) { mParameters = mNewParameters; return this; }

   /**
    * HTTP request headers.
    * @see #getHeaders()
    * @see #setHeaders(HashMap)
    */
   protected HashMap<String,String> mHeaders = new HashMap<String,String>();
   /**
    * Getter for {@link #mHeaders}: HTTP request headers.
    * @return HTTP request headers.
    */
   public HashMap<String,String> getHeaders() { return mHeaders; }
   /**
    * Setter for {@link #mHeaders}: HTTP request headers.
    * @param mNewHeaders HTTP request headers.
    */
   public HttpRequestGet setHeaders(HashMap<String,String> mNewHeaders) { mHeaders = mNewHeaders; return this; }   
   
   /**
    * The HTTP authorization string, or null if not required.
    * @see #getAuthorization()
    * @see #setAuthorization(String)
    */
   protected String sAuthorization;
   /**
    * Getter for {@link #sAuthorization}: The HTTP authorization string, or null if not required.
    * @return The HTTP authorization string, or null if not required.
    */
   public String getAuthorization() { return sAuthorization; }
   /**
    * Setter for {@link #sAuthorization}: The HTTP authorization string, or null if not required.
    * @param sNewAuthorization The HTTP authorization string, or null if not required.
    */
   public HttpRequestGet setAuthorization(String sNewAuthorization) { sAuthorization = sNewAuthorization; return this; }
  
  /**
   * HTTP method.
   * @see #getMethod()
   * @see #setMethod(String)
   */
  protected String method = "GET";
  /**
   * Getter for {@link #method}: HTTP method.
   * @return HTTP method.
   */
  public String getMethod() { return method; }
  /**
   * Setter for {@link #method}: HTTP method.
   * @param newMethod HTTP method.
   */
  public HttpRequestGet setMethod(String newMethod) { method = newMethod; return this; }
  
  // Methods:
  
   /**
    * Constructor
    */
   public HttpRequestGet(URL baseUrl) {
      setBaseUrl(baseUrl);
   } // end of constructor
   
   /**
    * Constructor
    */
   public HttpRequestGet(String baseUrl)
      throws MalformedURLException {
      setBaseUrl(new URL(baseUrl));
   } // end of constructor
   
   /**
    * Constructor
    */
   public HttpRequestGet(URL baseUrl, String authorization) {
      setBaseUrl(baseUrl);
      setAuthorization(authorization);
   } // end of constructor
   
   /**
    * Constructor
    */
   public HttpRequestGet(String baseUrl, String authorization)
      throws MalformedURLException {
      setBaseUrl(new URL(baseUrl));
      setAuthorization(authorization);
   } // end of constructor
   
   /**
    * Sets the user-agent header to indicate the name/version of the library.
    */
   public HttpRequestGet setUserAgent() { // TODO use pom.xml Implementation-Title/Version
      if (UserAgent == null) {
        UserAgent = "Java "
          +Optional.ofNullable(getClass().getPackage().getImplementationTitle())
          .orElse("nzilbb.labbcat")
          + " "
          + Optional.ofNullable(getClass().getPackage().getImplementationVersion())
          .orElse("?");
      }
      setHeader("user-agent", UserAgent);
      return this;
   } // end of setUserAgent()
   
   /**
    * Set the Accept-Language header with the given language code, if any.,
    * @param language The language code, e.g. "es-AR", or null.
    * @return A reference to this object.
    */
   public HttpRequestGet setLanguage(String language) {
      if (language != null) setHeader("Accept-Language", language);
      return this;
   } // end of setLanguage()
     
   /**
    * Sets a request parameter value
    * @param sParameter
    * @param oValue
    */
   public HttpRequestGet setParameter(String sParameter, Object oValue) {
      
      mParameters.put(sParameter, oValue);
      return this;
   } // end of setParameter()
   
   /**
    * Sets a request parameter value
    * @param sKey
    * @param sValue
    */
   public HttpRequestGet setHeader(String sKey, String sValue) {
      
      mHeaders.put(sKey, sValue);
      return this;
   } // end of setParameter()   
   
   /**
    * Fetches the request response
    * @return Connection for the response
    * @throws IOException
    * @throws MalformedURLException
    */
   public HttpURLConnection getConnection() throws IOException, MalformedURLException {
      
      String sBase = getBaseUrl().toString();
      String sQueryString = getQueryString();
      
      URLConnection connection = new URL(sBase + sQueryString).openConnection();
      ((HttpURLConnection)connection).setRequestMethod(method);
      connection.setUseCaches(false);
      for (String sKey: mHeaders.keySet()) {
         connection.setRequestProperty(sKey, mHeaders.get(sKey));
      } // next header
      if (sAuthorization != null) {
         connection.setRequestProperty("Authorization", sAuthorization);
      }
      return (HttpURLConnection)connection;
   } // end of getConnection()
   
   /**
    * Generates the query string.
    * @return The query string.
    */
   public String getQueryString() throws UnsupportedEncodingException {
      
      String sBase = getBaseUrl().toString();
      StringBuilder sQueryString = new StringBuilder();
      String sParameterPrefix = "?";
      if (sBase.indexOf('?') >= 0) sParameterPrefix = "&";
      for (String sParameter : mParameters.keySet()) {
         Object o = mParameters.get(sParameter);
         if (o.getClass().isArray()) {
            o = Arrays.asList((Object[])o);
         }
         if (o instanceof Iterable) {
            @SuppressWarnings("rawtypes")
	       Iterator i = ((Iterable)o).iterator();
            while (i.hasNext()) {
               sQueryString.append(sParameterPrefix)
                  .append(URLEncoder.encode(sParameter, "UTF8"))
                  .append("=")
                  .append(URLEncoder.encode(i.next().toString(), "UTF8"));
               sParameterPrefix = "&";
            }
         } else {
            sQueryString.append(sParameterPrefix)
               .append(URLEncoder.encode(sParameter, "UTF8"))
               .append("=")
               .append(URLEncoder.encode(o.toString(), "UTF8"));
         }
         
         sParameterPrefix = "&";
      } // next parameter
      return sQueryString.toString();
   } // end of getQueryString()
   
   /**
    * Fetches the request response
    * @return Input stream of the response
    * @throws IOException
    * @throws MalformedURLException
    */
   public HttpURLConnection get()
      throws IOException, MalformedURLException {
      
      return getConnection();
   } // end of get()
   
   /**
    * String representation of the request, for logging.
    * @return A String representation of the request, for logging.
    */
   public String toString() {
      
      try {
         return "GET " + getBaseUrl() + getQueryString();
      } catch(UnsupportedEncodingException exception) {
         return "GET " + getBaseUrl() + " " + mParameters;
      }
   } // end of toString()

} // end of class HttpRequestGet
