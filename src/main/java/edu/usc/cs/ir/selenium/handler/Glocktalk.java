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

package edu.usc.cs.ir.selenium.handler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import edu.usc.cs.ir.selenium.model.InteractiveSeleniumHandler;

/**
 * Selenium Handler for http://www.glocktalk.com/ to extract weapon related posts
 * Seedlist is as below:
 * 
 * @author karanjeets
 *
 */
public class Glocktalk implements InteractiveSeleniumHandler {


       public String processDriver(WebDriver driver) {
               StringBuffer buffer = new StringBuffer();
               
               // Extract threads from a forum
               if(driver.getCurrentUrl().startsWith("http://www.glocktalk.com/forum")) {
                       List<WebElement> threadTopics = driver.findElements(By.xpath("//h3[@class='title']"));
                       
                       for(WebElement element : threadTopics) {
                               WebElement anchor = element.findElement(By.tagName("a"));
                               //System.out.println(anchor.getAttribute("href"));
                               buffer.append(anchor.getAttribute("href")).append("\n");
                       }
                       
               }
               
               
               // Extract Next Page Link
               List<WebElement> nav = driver.findElements(By.xpath("//div[@class='PageNav']//nav//*[@class='text']"));
               //System.out.println(nav.get(nav.size() - 1).getAttribute("href"));
               buffer.append(nav.get(nav.size() - 1).getAttribute("href")).append("\n");
               
               
               // Extract all links from a thread
               if(driver.getCurrentUrl().startsWith("http://www.glocktalk.com/threads")) {
                       List<WebElement> links = driver.findElements(By.xpath("//ol[@id='messageList']//*[@href]"));
                       Set<String> filteredLinks = new HashSet<String>();
                       for(WebElement link : links) {
                               String linkValue = link.getAttribute("href");
                               if (linkValue.startsWith("http://www.glocktalk.com/account")
                                               || linkValue.startsWith("http://www.glocktalk.com/misc") 
                                               || linkValue.startsWith("http://www.glocktalk.com/goto")
                                               || linkValue.startsWith("http://www.glocktalk.com/social-forums")
                                               || linkValue.startsWith("http://www.glocktalk.com/search")
                                               || linkValue.contains("#"))
                                       continue;
                               //System.out.println(linkValue);
                               filteredLinks.add(linkValue);
                       }
                       buffer.append(filteredLinks);
               }
               //System.out.println();
               return buffer.toString();
       }

       public boolean shouldProcessURL(String URL) {
               
               return true;
       }
       
       public static void main(String[] args) {
               Glocktalk glocktalk = new Glocktalk();
               WebDriver driver = new FirefoxDriver();
               try {
                       driver.get("http://www.glocktalk.com/threads/beretta-m9a3.1613812/");
                       System.out.println(glocktalk.processDriver(driver));
               } catch(Exception e) {
                       e.printStackTrace();
               }
               finally {
                       if(driver != null)
                               driver.quit();
               }
       }
       
}

