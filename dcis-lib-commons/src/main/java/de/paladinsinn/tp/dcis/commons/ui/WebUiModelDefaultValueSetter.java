/*
 * Copyright (c) 2025. Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.commons.ui;


import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;


/**
 * Fills the default values needed in our template.
 *
 * @author klenkes74
 * @since 09.04.25
 */
@Service
@XSlf4j
public class WebUiModelDefaultValueSetter {
  
  @Value("${server.servlet.contextPath}:/scs")
  private String contextPath;

  /**
   * Adds the context path to the model to be used there.
   *
   * @param result The result to return.
   * @param authentication The authentication to read the name and id from.
   * @param model The model the contextPath should be added to.
   * @return The result string.
   */
  public String addContextPath(final String result, final Authentication authentication, Model model) {
    log.entry(result, authentication, model);
    
    model.addAttribute("id", ((DefaultOidcUser)authentication.getPrincipal()).getSubject());
    model.addAttribute("name", authentication.getName());
    model.addAttribute("contextPath", contextPath);
    
    return log.exit(result);
  }
  
  public String fullUrl(final String url) {
    log.entry(url);
    
    String result = contextPath + (url.startsWith("/") ? "" : "/") + url;
    
    return log.exit(result);
  }
}
