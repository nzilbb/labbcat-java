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
package nzilbb.labbcat.model;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

/**
 * Corpus record.
 * @author Robert Fromont robert@fromont.net.nz
 */
public class Corpus {
   
   // Attributes:

   /**
    * Database key value.
    * @see #getCorpusId()
    * @see #setCorpusId(int)
    */
   protected int corpusId;
   /**
    * Getter for {@link #corpusId}: Database key value.
    * @return Database key value.
    */
   public int getCorpusId() { return corpusId; }
   /**
    * Setter for {@link #corpusId}: Database key value.
    * @param newCorpusId Database key value.
    * @return A reference to this object, so that setters can be chained.
    */
   public Corpus setCorpusId(int newCorpusId) { corpusId = newCorpusId; return this; }

   /**
    * Corpus name.
    * @see #getName()
    * @see #setName(String)
    */
   protected String name;
   /**
    * Getter for {@link #name}: Corpus name.
    * @return Corpus name.
    */
   public String getName() { return name; }
   /**
    * Setter for {@link #name}: Corpus name.
    * @param newName Corpus name.
    * @return A reference to this object, so that setters can be chained.
    */
   public Corpus setName(String newName) { name = newName; return this; }

   /**
    * The ISO 639-1 code for the default language.
    * @see #getLanguage()
    * @see #setLanguage(String)
    */
   protected String language;
   /**
    * Getter for {@link #language}: The ISO 639-1 code for the default language.
    * @return The ISO 639-1 code for the default language.
    */
   public String getLanguage() { return language; }
   /**
    * Setter for {@link #language}: The ISO 639-1 code for the default language.
    * @param newLanguage The ISO 639-1 code for the default language.
    * @return A reference to this object, so that setters can be chained.
    */
   public Corpus setLanguage(String newLanguage) { language = newLanguage; return this; }

   /**
    * Description of corpus.
    * @see #getDescription()
    * @see #setDescription(String)
    */
   protected String description;
   /**
    * Getter for {@link #description}: Description of corpus.
    * @return Description of corpus.
    */
   public String getDescription() { return description; }
   /**
    * Setter for {@link #description}: Description of corpus.
    * @param newDescription Description of corpus.
    * @return A reference to this object, so that setters can be chained.
    */
   public Corpus setDescription(String newDescription) { description = newDescription; return this; }
   
   // Methods:
   
   /**
    * Default constructor.
    */
   public Corpus() {      
   } // end of constructor
   
   /**
    * Constructor from JSON.
    * @param json A JSON-encoded version of the corpus.
    */
   public Corpus(JsonObject json) {
      if (json.containsKey("corpus_id")) {
        corpusId = json.getInt("corpus_id", -1);
      }
      name = json.getString("corpus_name");
      language = json.getString("corpus_language");
      description = json.getString("corpus_description");
   } // end of constructor
   
   /**
    * Serializes the object to JSON.
    * @return A JSON serialization of the object.
    */
   public JsonObject toJson() {
      JsonObjectBuilder json = Json.createObjectBuilder()
         .add("corpus_id", corpusId);
      if (name != null) json = json.add("corpus_name", name);
      if (language != null) json = json.add("corpus_language", language);
      if (description != null) json = json.add("corpus_description", description);
      return json.build();
   } // end of toJSON()

} // end of class Corpus
