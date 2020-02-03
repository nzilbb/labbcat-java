//
// Copyright 2020 New Zealand Institute of Language, Brain and Behaviour, 
// University of Canterbury
// Written by Robert Fromont - robert.fromont@canterbury.ac.nz
//
//    This file is part of LaBB-CAT.
//
//    LaBB-CAT is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 3 of the License, or
//    (at your option) any later version.
//
//    LaBB-CAT is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with nzilbb.ag; if not, write to the Free Software
//    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//

package nzilbb.labbcat.test;
	      
import org.junit.*;
import static org.junit.Assert.*;

import nzilbb.labbcat.*;
import org.json.JSONObject;

/**
 * Unit tests for PatternBuilder.
 */
public class TestPatternBuilder 
{
   @Test public void basicPattern()
      throws Exception
   {
      JSONObject pattern = new PatternBuilder().addMatchLayer("orthography", "and").build();
      assertEquals("{\"columns\":[{\"layers\":{\"orthography\":{\"pattern\":\"and\"}}}]}",
                   pattern.toString());
   } 

   @Test public void multiLayerPattern()
      throws Exception
   {
      JSONObject pattern = new PatternBuilder()
         .addColumn()
         .addMatchLayer("orthography", "the")
         .addColumn()
         .addNotMatchLayer("phonemes", "[cCEFHiIPqQuUV0123456789~#\\$@].*")
         .addMaxLayer("frequency", 2)
         .build();
      assertEquals("{\"columns\":["
                   +"{\"layers\":{"
                   +"\"orthography\":{\"pattern\":\"the\"}},"
                   +"\"adj\":1},"
                   +"{\"layers\":{"
                   +"\"phonemes\":{\"not\":true,\"pattern\":\"[cCEFHiIPqQuUV0123456789~#\\\\$@].*\"},"
                   +"\"frequency\":{\"max\":\"2\"}}}"
                   +"]}",
                   pattern.toString());
   }

   public static void main(String args[]) 
   {
      org.junit.runner.JUnitCore.main("nzilbb.labbcat.test.TestPatternBuilder");
   }
}
