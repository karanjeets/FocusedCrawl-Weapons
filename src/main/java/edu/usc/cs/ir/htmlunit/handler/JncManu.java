/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.usc.cs.ir.htmlunit.handler;

import java.io.UnsupportedEncodingException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import edu.usc.cs.ir.htmlunit.model.InteractiveHtmlUnitHandler;

/**
 * HtmlUnit Handler for http://www.sturmgewehr.com/ to extract weapon related posts
 * 
 * Command to extract URLs from this host:
 * curl -s "http://www.sturmgewehr.com/forums/" | grep -ioE 'href="[-A-Za-z0-9+&@#/%?=~_|!:,.;]*"' | grep -i "forum/" | awk -F "href=" '{print $2}'| grep -v "#" | cut -d'"' -f2
 * 
 * Seed list is as below:
 * 
 * @author karanjeets
 *
 */
public class JncManu implements InteractiveHtmlUnitHandler {

  public String processDriver(final WebDriver driver) {
        
           return driver.getPageSource().replaceAll("&amp;", "&");
    }

       public boolean shouldProcessURL(String URL) {
           return true;
       }
       
       public static void main(String[] args) {
               JncManu glocktalk = new JncManu();
               WebDriver driver = null;
               try {
                       driver = HtmlUnitWebDriver.getDriverForPage("http://www.jncmanufacturing.com");
                       System.out.println(new String(glocktalk.processDriver(driver).getBytes("UTF-8")));
               } 
               catch(Exception e) {
            	   if(e instanceof TimeoutException) {
            		   System.out.println("Timeout Exception");
            		   
            		   try {
       					System.out.println(new String(glocktalk.processDriver(driver).getBytes("UTF-8")));
       				} catch (UnsupportedEncodingException e1) {
       					// TODO Auto-generated catch block
       					e1.printStackTrace();
       				}
       				
            	   }
            	   if(driver != null)
            	     HtmlUnitWebDriver.cleanUpDriver(driver);
                   //e.printStackTrace();
               }
       }
       
}

