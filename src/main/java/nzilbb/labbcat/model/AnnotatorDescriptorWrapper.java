//
// Copyright 2023 New Zealand Institute of Language, Brain and Behaviour, 
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
package nzilbb.labbcat.model;

import nzilbb.util.ClonedProperty;
import nzilbb.ag.automation.util.AnnotatorDescriptor;

/**
 * Client-side annotator descriptor that can be deserialized from JSON.
 * @author Robert Fromont robert@fromont.net.nz
 */
public class AnnotatorDescriptorWrapper extends AnnotatorDescriptor {
  
  /**
   * Default constructor.
   */
  public AnnotatorDescriptorWrapper() {
  } // end of constructor  
  
  /**
   * Annotator ID.
   * @see #getAnnotatorId()
   * @see #setAnnotatorId(String)
   */
  protected String annotatorId;
  /**
   * Getter for {@link #annotatorId}: Annotator ID.
   * @return Annotator ID.
   */
  @ClonedProperty
  @Override public String getAnnotatorId() { return annotatorId; }
  /**
   * Setter for {@link #annotatorId}: Annotator ID.
   * @param newAnnotatorId Annotator ID.
   */
  public AnnotatorDescriptorWrapper setAnnotatorId(String newAnnotatorId) { annotatorId = newAnnotatorId; return this; }
  
  /**
   * Version of this implementation; versions will typically be numeric, but this is not a
   * requirement. 
   * @see #getVersion()
   * @see #setVersion(String)
   */
   protected String version;
  /**
   * Getter for {@link #version}: Version of this implementation; versions will typically
   * be numeric, but this is not a requirement. 
   * @return Version of this implementation; versions will typically be numeric, but this
   * is not a requirement. 
   */
  @ClonedProperty
  @Override public String getVersion() { return version; }
  /**
   * Setter for {@link #version}: Version of this implementation; versions will typically
   * be numeric, but this is not a requirement. 
   * @param newVersion Version of this implementation; versions will typically be numeric,
   * but this is not a requirement. 
   */
  public AnnotatorDescriptorWrapper setVersion(String newVersion) { version = newVersion; return this; }
  
  /**
   * Get the minimum version of the nzilbb.ag API supported by the serializer. 
   * @see #getMinimumApiVersion()
   * @see #setMinimumApiVersion(String)
   */
  protected String minimumApiVersion;
  /**
   * Getter for {@link #minimumApiVersion}: Get the minimum version of the nzilbb.ag API
   * supported by the serializer.  
   * @return Get the minimum version of the nzilbb.ag API supported by the serializer. 
   */
  @ClonedProperty
  @Override public String getMinimumApiVersion() { return minimumApiVersion; }
  /**
   * Setter for {@link #minimumApiVersion}: Get the minimum version of the nzilbb.ag API
   * supported by the serializer.  
   * @param newMinimumApiVersion Get the minimum version of the nzilbb.ag API supported by
   * the serializer.  
   */
  public AnnotatorDescriptorWrapper setMinimumApiVersion(String newMinimumApiVersion) { minimumApiVersion = newMinimumApiVersion; return this; }
  
  /**
   * HTML-encoded description of the function of the annotator, for displaying to the user.
   * @see #getInfo()
   * @see #setInfo(String)
   */
  protected String info;
  /**
   * Getter for {@link #info}: HTML-encoded description of the function of the annotator,
   * for displaying to the user. 
   * @return HTML-encoded description of the function of the annotator, for displaying to the user.
   */
  @ClonedProperty
  @Override public String getInfo() { return info; }
  /**
   * Setter for {@link #info}: HTML-encoded description of the function of the annotator,
   * for displaying to the user. 
   * @param newInfo HTML-encoded description of the function of the annotator, for
   * displaying to the user.
   */
  public AnnotatorDescriptorWrapper setInfo(String newInfo) { info = newInfo; return this; }
  
  /**
   * HTML-encoded definition of the task parameters, including a list of all parameters,
   * and the encoding of the parameter string, for displaying to users who wish to
   * configure annotator tasks programmatically. 
   * @see #getTaskParameterInfo()
   * @see #setTaskParameterInfo(String)
   */
  protected String taskParameterInfo;
  /**
   * Getter for {@link #taskParameterInfo}: HTML-encoded definition of the task
   * parameters, including a list of all parameters, and the encoding of the parameter
   * string, for displaying to users who wish to configure annotator tasks
   * programmatically. 
   * @return HTML-encoded definition of the task parameters, including a list of all
   * parameters, and the encoding of the parameter string, for displaying to users who
   * wish to configure annotator tasks programmatically. 
   */
  @ClonedProperty
  @Override public String getTaskParameterInfo() { return taskParameterInfo; }
  /**
   * Setter for {@link #taskParameterInfo}: HTML-encoded definition of the task
   * parameters, including a list of all parameters, and the encoding of the parameter
   * string, for displaying to users who wish to configure annotator tasks
   * programmatically. 
   * @param newTaskParameterInfo HTML-encoded definition of the task parameters, including
   * a list of all parameters, and the encoding of the parameter string, for displaying to
   * users who wish to configure annotator tasks programmatically. 
   */
  public AnnotatorDescriptorWrapper setTaskParameterInfo(String newTaskParameterInfo) { taskParameterInfo = newTaskParameterInfo; return this; }
  
  /**
   * HTML-encoded definition of the installation config parameters, including a list of
   * all parameters, and the encoding of the parameter string, for displaying to users who
   * wish to configure the annotator programmatically.  
   * @see #getConfigParameterInfo()
   * @see #setConfigParameterInfo(String)
   */
  protected String configParameterInfo;
  /**
   * Getter for {@link #configParameterInfo}: HTML-encoded definition of the installation
   * config parameters, including a list of all parameters, and the encoding of the
   * parameter string, for displaying to users who wish to configure the annotator
   * programmatically.  
   * @return HTML-encoded definition of the installation config parameters, including a
   * list of all parameters, and the encoding of the parameter string, for displaying to
   * users who wish to configure the annotator programmatically.  
   */
  @ClonedProperty
  @Override public String getConfigParameterInfo() { return configParameterInfo; }
  /**
   * Setter for {@link #configParameterInfo}: HTML-encoded definition of the installation
   * config parameters, including a list of all parameters, and the encoding of the
   * parameter string, for displaying to users who wish to configure the annotator
   * programmatically.  
   * @param newConfigParameterInfo HTML-encoded definition of the installation config
   * parameters, including a list of all parameters, and the encoding of the parameter
   * string, for displaying to users who wish to configure the annotator programmatically.  
   */
  public AnnotatorDescriptorWrapper setConfigParameterInfo(String newConfigParameterInfo) { configParameterInfo = newConfigParameterInfo; return this; }
  
  /**
   * HTML-encoded document containing information about what endpoints are published by
   * the ext web-app, for displaying to users who wish to use it programmatically. 
   * @see #getExtApiInfo()
   * @see #setExtApiInfo(String)
   */
  protected String extApiInfo;
  /**
   * Getter for {@link #extApiInfo}: HTML-encoded document containing information about
   * what endpoints are published by the ext web-app, for displaying to users who wish to
   * use it programmatically. 
   * @return HTML-encoded document containing information about what endpoints are
   * published by the ext web-app, for displaying to users who wish to use it
   * programmatically. 
   */
  @ClonedProperty
  @Override public String getExtApiInfo() { return extApiInfo; }
  /**
   * Setter for {@link #extApiInfo}: HTML-encoded document containing information about
   * what endpoints are published by the ext web-app, for displaying to users who wish to
   * use it programmatically. 
   * @param newExtApiInfo HTML-encoded document containing information about what
   * endpoints are published by the ext web-app, for displaying to users who wish to use
   * it programmatically. 
   */
  public AnnotatorDescriptorWrapper setExtApiInfo(String newExtApiInfo) { extApiInfo = newExtApiInfo; return this; }
  
  /**
   * Whether the annotator includes a web-app for installation or general configuration.
   * @see #getHasConfigWebapp()
   * @see #setHasConfigWebapp(Boolean)
   */
  protected Boolean hasConfigWebapp;
  /**
   * Determines whether the annotator includes a web-app for installation or general
   * configuration.
   * @return true if the class includes a web-app at config/index.html, false otherwise.
   */
  @Override public boolean hasConfigWebapp() {
    return hasConfigWebapp;
  }
  /**
   * Getter for {@link #hasConfigWebapp}: Whether the annotator includes a web-app for
   * installation or general configuration. 
   * @return Whether the annotator includes a web-app for installation or general configuration.
   */
  @ClonedProperty
  public Boolean getHasConfigWebapp() { return hasConfigWebapp; }
  /**
   * Setter for {@link #hasConfigWebapp}: Whether the annotator includes a web-app for
   * installation or general configuration. 
   * @param newHasConfigWebapp Whether the annotator includes a web-app for installation
   * or general configuration. 
   */
  public AnnotatorDescriptorWrapper setHasConfigWebapp(Boolean newHasConfigWebapp) { hasConfigWebapp = newHasConfigWebapp; return this; }
  
  /**
   * Whether the annotator includes a web-app for task parameter configuration.
   * @see #getHasTaskWebapp()
   * @see #setHasTaskWebapp(Boolean)
   */
  protected Boolean hasTaskWebapp;
  /**
   * Determines whether the annotator includes a web-app for task parameter configuration.
   * @return true if the class includes a web-app at task/index.html, false otherwise.
   */
  @Override public boolean hasTaskWebapp() {
    return hasTaskWebapp;
  }
  /**
   * Getter for {@link #hasTaskWebapp}: Whether the annotator includes a web-app for task
   * parameter configuration. 
   * @return Whether the annotator includes a web-app for task parameter configuration.
   */
  @ClonedProperty
  public Boolean getHasTaskWebapp() { return hasTaskWebapp; }
  /**
   * Setter for {@link #hasTaskWebapp}: Whether the annotator includes a web-app for task
   * parameter configuration. 
   * @param newHasTaskWebapp Whether the annotator includes a web-app for task parameter
   * configuration. 
   */
  public AnnotatorDescriptorWrapper setHasTaskWebapp(Boolean newHasTaskWebapp) { hasTaskWebapp = newHasTaskWebapp; return this; }

  /**
   * Whether the annotator includes an extras web-app.
   * @see #getHasExtWebapp()
   * @see #setHasExtWebapp(Boolean)
   */
  protected Boolean hasExtWebapp;
  /**
   * Determines whether the annotator includes an extras web-app.
   * @return true if the class includes a web-app at ext/index.html, false otherwise.
   */
  @Override public boolean hasExtWebapp() {
    return hasExtWebapp;
  }
  /**
   * Getter for {@link #hasExtWebapp}: Whether the annotator includes an extras web-app.
   * @return Whether the annotator includes an extras web-app.
   */
  @ClonedProperty
  public Boolean getHasExtWebapp() { return hasExtWebapp; }
  /**
   * Setter for {@link #hasExtWebapp}: Whether the annotator includes an extras web-app.
   * @param newHasExtWebapp Whether the annotator includes an extras web-app.
   */
  public AnnotatorDescriptorWrapper setHasExtWebapp(Boolean newHasExtWebapp) { hasExtWebapp = newHasExtWebapp; return this; }

} // end of class AnnotatorDescriptorWrapper
