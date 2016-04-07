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
public class Sturmgewehr implements InteractiveHtmlUnitHandler {

	   // Number of pages to navigation pages to fetch in one round of crawl 
	   private static final Integer PAGE_FETCH_LIMIT = 5;
		   
       public String processDriver(WebDriver driver) {
    	   	  StringBuffer buffer = new StringBuffer();
            
    	   	  //System.out.println("I am here");
    	   	  
    	   	 WebElement posts = driver.findElement(By.xpath("//div[@class='ipsBox']//ol"));
    	   	 buffer.append((String)((JavascriptExecutor)driver).executeScript("return arguments[0].innerHTML;", posts)).append("\n\n"); 
    	   	  
    	   	  int lastPage = Integer.parseInt(driver.findElement(By.xpath("//li[@class='ipsPagination_last']//a")).getAttribute("data-page"));
    	   	  
    	   	  for(int i = 2; i <= lastPage; i++)
    	   	    buffer.append("<a href=\"").append(driver.getCurrentUrl()).append("&page=").append(i).append("\" />\n");
    	   	  
    	   	  //System.out.println(lastPage);
    	   	  
            //System.out.println(buffer.toString());
            return buffer.toString();
       }

       public boolean shouldProcessURL(String URL) {
           if (URL.startsWith("http://www.sturmgewehr.com/forums") && !URL.contains("&page=") && URL.contains("/forum/"))    
        	   return true;
           return false;
       }
       
       public static void main(String[] args) {
               Sturmgewehr glocktalk = new Sturmgewehr();
               WebDriver driver = null;
               try {
                       driver = HtmlUnitWebDriver.getDriverForPage("http://www.sturmgewehr.com/forums/index.php?/forum/8-parts-and-accessories-market-board/");
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

