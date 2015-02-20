/*
 * Copyright [2015] [Stefan Pröll]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package Filters;

import Bean.LoginBean;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Filter checks if LoginBean has loginIn property set to true.
 * If it is not set then request is being redirected to the login.xhml page.
 * The login.xhtml page is excplicitly not required to have a logged in user
 *
 * @author itcuties
 */


public class LoginFilter implements Filter {
    Logger logger;

    public LoginFilter() {
        this.logger = Logger.getLogger(this.getClass().getName());

    }

    /**
     * Checks if user is logged in. If not it redirects to the login.xhtml page.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, 
            ServletException {

        // Get the loginBean from session attribute
        LoginBean loginBean = (LoginBean) ((HttpServletRequest) request).getSession().getAttribute("loginBean");

        // For the first application request there is no loginBean in the session so user needs to log in
        // For other requests loginBean is present but we need to check if user has logged in successfully
        if (loginBean == null || !loginBean.isLoggedIn()) {
            String contextPath = ((HttpServletRequest) request).getContextPath();
            String path = ((HttpServletRequest) request).getRequestURI();
            //this.logger.info("Path is: " + path);

            if (path.endsWith("login.xhtml") == true) {
                //this.logger.info("Login page detected");

            } else if (path.contains("/cite/javax.faces.resource")) {
                //this.logger.info("Resource detected");

            } else if (path.endsWith(".xhtml")) {
                this.logger.warning("User was not logged in. Filter triggered");
                ((HttpServletResponse) response).sendRedirect(contextPath + "/login.xhtml");
            }
        }

        chain.doFilter(request, response);


    }

    public void init(FilterConfig config) throws ServletException {
        // Nothing to do here!
    }

    public void destroy() {
        // Nothing to do here!
    }

}
