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
package nzilbb.labbcat;

import nzilbb.ag.StoreException;

/**
 * An object representing an error returned by the server.
 * @author Robert Fromont robert@fromont.net.nz
 */

public class ResponseException
  extends StoreException
{
   // Attributes:

   /**
    * The response.
    * @see #getResponse()
    */
   protected Response response;
   /**
    * Getter for {@link #response}: The response.
    * @return The response.
    */
   public Response getResponse() { return response; }
   
   // Methods:
   
   /**
    * Constructor.
    */
   public ResponseException(Response response)
   {
      super(GenerateMessage(response));
      this.response = response;      
   } // end of constructor
   
   /**
    * Generates the exception message.
    * @param response
    * @return The message.
    */
   static private String GenerateMessage(Response response)
   {
      if (response.getErrors() != null && response.getErrors().size() > 0)
      {
         StringBuilder allErrors = new StringBuilder();
         for (String error : response.getErrors())
         {
            if (allErrors.length() > 0) allErrors.append("\n");
            allErrors.append(error);
         }
         return allErrors.toString();
      }
      if (response.getCode() > 0)
      {
         return "Response code " + response.getCode();
      }
      if (response.getHttpStatus() > 0)
      {
         return "HTTP status " + response.getHttpStatus();
      }
      return null;
   } // end of generateMessage()

   private static final long serialVersionUID = 1;
} // end of class ResponseException
