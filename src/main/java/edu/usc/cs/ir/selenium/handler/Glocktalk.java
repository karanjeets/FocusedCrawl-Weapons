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
 * 
 * Command to extract URLs from this host:
 * curl -s "http://www.glocktalk.com" | grep -ioE 'href="[-A-Za-z0-9+&@#/%?=~_|!:,.;]*"' | awk -F "href=" '{print $2}'| grep -v "#"
 * 
 * Seed list is as below:
 * http://www.glocktalk.com/forum/general-firearms-forum.82/
 * http://www.glocktalk.com/forum/tactical-shotguns.180/
 * http://www.glocktalk.com/forum/black-rifle-forum.93/
 * http://www.glocktalk.com/forum/1911-forums.77/
 * http://www.glocktalk.com/forum/the-kalashnikov-klub.94/
 * http://www.glocktalk.com/forum/rimfire-forum.61/
 * http://www.glocktalk.com/forum/gun-related-clubs.164/
 * http://www.glocktalk.com/forum/general-competition.25/
 * http://www.glocktalk.com/forum/heckler-koch-forum.129/
 * http://www.glocktalk.com/forum/gate-info-announcements.248/
 * http://www.glocktalk.com/forum/gate-self-defense-forum.256/
 * http://www.glocktalk.com/forum/gate-ar-15-forum.257/
 * http://www.glocktalk.com/forum/gate-nfa-class-iii.251/
 * http://www.glocktalk.com/forum/gate-long-range-shooting.252/
 * http://www.glocktalk.com/forum/gate-survival-preparedness.255/
 * http://www.glocktalk.com/forum/gate-night-vision-forum.259/
 * http://www.glocktalk.com/forum/firearms-listings.39/
 * http://www.glocktalk.com/forum/gun-parts-accessories.40/
 * http://www.glocktalk.com/forum/edged-weapons-related-items.46/
 * http://www.glocktalk.com/forum/holsters-related-items.41/
 * http://www.glocktalk.com/forum/misc-gun-stuff.42/
 * http://www.glocktalk.com/forum/wanted-to-buy.45/
 * http://www.glocktalk.com/forum/gun-parts-access.22/
 * 
 * @author karanjeets
 *
 */
public class Glocktalk implements InteractiveSeleniumHandler {


       public String processDriver(WebDriver driver) {
               StringBuffer buffer = new StringBuffer();
               
               // Extract content - getText doesn't return any links
               String content = driver.findElement(By.tagName("body")).getText();
               buffer.append(content).append("\n");
               
               // Extract threads from a forum
               if(driver.getCurrentUrl().startsWith("http://www.glocktalk.com/forum")) {
                       List<WebElement> threadTopics = driver.findElements(By.xpath("//h3[@class='title']"));
                       
                       for(WebElement element : threadTopics) {
                               WebElement anchor = element.findElement(By.tagName("a"));
                               //System.out.println(anchor.getAttribute("href"));
                               buffer.append("<a href=\"").append(anchor.getAttribute("href")).append("\" />").append("\n");
                       }
                       
               }
               
               
               // Extract Next Page Link
               List<WebElement> nav = driver.findElements(By.xpath("//div[@class='PageNav']//nav//*[@class='text']"));
               //System.out.println(nav.get(nav.size() - 1).getAttribute("href"));
               if(nav.size() > 0)
            	   buffer.append("<a href=\"").append(nav.get(nav.size() - 1).getAttribute("href")).append("\" />").append("\n");
               
               
               // Extract all links from a thread
               if(driver.getCurrentUrl().startsWith("http://www.glocktalk.com/threads")) {
                       List<WebElement> links = driver.findElements(By.xpath("//ol[@id='messageList']//*[@href]"));
                       Set<String> filteredLinks = new HashSet<String>();
                       for(WebElement link : links) {
                               String linkValue = "<a href=\"" + link.getAttribute("href") + "\" />";
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
                       if(filteredLinks.size() > 0)
                    	   buffer.append(filteredLinks).append("\n");
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
                       driver.get("http://www.glocktalk.com/forum/general-firearms-forum.82/");
                       System.out.println(new String(glocktalk.processDriver(driver).getBytes("UTF-8")));
               } catch(Exception e) {
                       e.printStackTrace();
               }
               finally {
                       if(driver != null)
                               driver.quit();
               }
       }
       
}

