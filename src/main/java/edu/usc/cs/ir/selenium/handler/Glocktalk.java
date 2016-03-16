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

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

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

	   // Number of pages to navigation pages to fetch in one round of crawl 
	   private static final Integer PAGE_FETCH_LIMIT = 5;
	
	   private Set<String> getThreadsFromForum(WebDriver driver) {
		   Set<String> threads = new HashSet<String>();
		   List<WebElement> threadTopics = driver.findElements(By.xpath("//h3[@class='title']"));
           
           for(WebElement element : threadTopics) {
                   WebElement anchor = element.findElement(By.tagName("a"));
                   //System.out.println(anchor.getAttribute("href"));
                   threads.add("<a href=\"" + anchor.getAttribute("href") + "\" />");
           }
		   return threads;
	   }
	   
	   private Set<String> getPostsFromThread(WebDriver driver) {
		   List<WebElement> links = driver.findElements(By.xpath("//ol[@id='messageList']//*[@href|@src]"));
           Set<String> filteredLinks = new HashSet<String>();
           for(WebElement link : links) {
        	   	   String linkValue = null;
        	   	   if(link.getAttribute("src") != null && !link.getTagName().equals("img"))
        	   		   continue;
        	   	   if(link.getAttribute("src") != null)
        	   		   linkValue = link.getAttribute("src");
        	   	   else
        	   		   linkValue = link.getAttribute("href");
                   if (linkValue.startsWith("http://www.glocktalk.com/account")
                                   || linkValue.startsWith("http://www.glocktalk.com/misc") 
                                   || linkValue.startsWith("http://www.glocktalk.com/goto")
                                   || linkValue.startsWith("http://www.glocktalk.com/social-forums")
                                   || linkValue.startsWith("http://www.glocktalk.com/search")
                                   || linkValue.contains("#"))
                           continue;
                   //System.out.println(linkValue);
                   filteredLinks.add("<a href=\"" + linkValue + "\" />");
           }
           return filteredLinks;
	   }
	   
       public String processDriver(WebDriver driver) {
    	   	   StringBuffer buffer = new StringBuffer();
               
               // Extract content - getText doesn't return any links
               String content = driver.findElement(By.tagName("body")).getText();
               buffer.append(content).append("\n");
               
               // Extract threads from a forum through all of its navigation
               if(driver.getCurrentUrl().startsWith("http://www.glocktalk.com/forum")) {
            	   	   // Extract the last page number
            	       List<WebElement> pages = driver.findElements(By.xpath("//div[@class='PageNav']//nav//a[@class='']"));
            	       buffer.append(getThreadsFromForum(driver)).append("\n");
            	       
            	       if(pages != null && !pages.isEmpty()) {
            	    	   
            	    	   WebElement currentPage = driver.findElement(By.xpath("//div[@class='PageNav']//nav//a[@class='currentPage ']"));
            	    	   Integer currentPageNo = Integer.parseInt(currentPage.getText());
            	    	   if(currentPageNo == 1 || currentPageNo % PAGE_FETCH_LIMIT == 0) {
	            	    	   Integer lastPage = Integer.parseInt(pages.get(pages.size() - 1).getText());
	            	    	   String currentUrl = driver.getCurrentUrl().substring(0, driver.getCurrentUrl().lastIndexOf("/") + 1);
	            	    	   int i = currentPageNo + 1;
	            	    	   for(; i < (currentPageNo + PAGE_FETCH_LIMIT) && i <= lastPage; i++) {
	            	    		   try {
		            	    		   driver.get(currentUrl + "page-" + i);
	            	    		   } catch(TimeoutException e) {
	            	    			   System.out.println("Timeout Exception for page = " + i);
	            	    		   } finally {
	            	    			   buffer.append(getThreadsFromForum(driver)).append("\n");
	            	    		   }
	            	    	   }
	            	    	   
	            	    	   if(i <= lastPage)
	            	    		   buffer.append("<a href=\"").append(currentUrl + "page-" + i).append("\" />").append("\n");
            	    	   }
            	       }
               }
               
               // Extract all links from a thread
               else if(driver.getCurrentUrl().startsWith("http://www.glocktalk.com/threads")) {
            	       
            	       // Extract the last page number
            	       List<WebElement> pages = driver.findElements(By.xpath("//div[@class='PageNav']//nav//a[@class='']"));
            	       buffer.append(getPostsFromThread(driver)).append("\n");
            	       
            	       if(pages != null && !pages.isEmpty()) {
            	    	   
            	    	   WebElement currentPage = driver.findElement(By.xpath("//div[@class='PageNav']//nav//a[@class='currentPage ']"));
            	    	   Integer currentPageNo = Integer.parseInt(currentPage.getText());
            	    	   if(currentPageNo == 1 || currentPageNo % PAGE_FETCH_LIMIT == 0) {
	            	    	   Integer lastPage = Integer.parseInt(pages.get(pages.size() - 1).getText());
	            	    	   String currentUrl = driver.getCurrentUrl().substring(0, driver.getCurrentUrl().lastIndexOf("/") + 1);
	            	    	   int i = currentPageNo + 1;
	            	    	   for(; i < (currentPageNo + PAGE_FETCH_LIMIT) && i <= lastPage; i++) {
	            	    		   try {
		            	    		   driver.get(currentUrl + "page-" + i);
	            	    		   } catch(TimeoutException e) {
	            	    			   System.out.println("Timeout Exception for page = " + i);
	            	    		   } finally {
	            	    			   buffer.append(getPostsFromThread(driver)).append("\n");
	            	    		   }
	            	    	   }
	            	    	   
	            	    	   if(i <= lastPage)
	            	    		   buffer.append("<a href=\"").append(currentUrl + "page-" + i).append("\" />").append("\n");
            	    	   }
            	       }
            	       
               }
               
               // Extract User Images
               else if(driver.getCurrentUrl().startsWith("http://www.glocktalk.com/members")) {
            	   List<WebElement> srcLinks = driver.findElements(By.xpath("//div[@class='profilePage']//*[@src]"));
                   Set<String> images = new HashSet<String>();
                   for(WebElement link : srcLinks) {
                	   	   String linkValue = null;
                	   	   if(!link.getTagName().equals("img"))
                	   		   continue;
                	   	   linkValue = link.getAttribute("src");
                           System.out.println(linkValue);
                           images.add("<img src=\"" + linkValue + "\" />");
                   }
                   buffer.append(images);
               }
               
               //System.out.println();
               return buffer.toString();
       }

       public boolean shouldProcessURL(String URL) {
           if(URL.startsWith("http://www.glocktalk.com/forum") || URL.startsWith("http://www.glocktalk.com/threads") || URL.startsWith("http://www.glocktalk.com/members"))    
        	   return true;
           return false;
       }
       
       public static void main(String[] args) {
               Glocktalk glocktalk = new Glocktalk();
               FirefoxProfile profile = new FirefoxProfile();
               profile.setPreference(FirefoxProfile.ALLOWED_HOSTS_PREFERENCE, "localhost");
               profile.setPreference("dom.ipc.plugins.enabled.libflashplayer.so", "false");
               profile.setPreference("permissions.default.stylesheet", 1);
               profile.setPreference("permissions.default.image", 1);
               
               FirefoxBinary binary = new FirefoxBinary();
               binary.setTimeout(TimeUnit.SECONDS.toMillis(180));
               
               WebDriver driver = new FirefoxDriver(binary, profile);
               driver.manage().timeouts().pageLoadTimeout(10000, TimeUnit.MILLISECONDS);
               
               try {
                       driver.get("http://www.glocktalk.com/threads/rimfire-pics.726737/page-10");
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
                   //e.printStackTrace();
               }
               finally {
                       if(driver != null) {
                    	   	driver.close();
                            driver.quit();
                       }
               }
       }
       
}

