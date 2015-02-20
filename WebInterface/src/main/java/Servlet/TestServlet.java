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

/*
 * Copyright [2014] [Stefan Pröll]
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

/*
 * Copyright [2014] [Stefan Pröll]
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

package Servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * Created by stefan on 21.06.14.
 */

@WebServlet("/test")
public class TestServlet extends HttpServlet {
    private Logger logger;

    public TestServlet() {
        this.logger = Logger.getLogger(this.getClass().getName());
    }


    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) {


        System.out.println("GET sent");

        this.printAllRequestParameters(request);

    }

    public void addHandler(Handler handler) throws SecurityException {
        logger.addHandler(handler);
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        System.out.println("POST received");

        doGet(request, response);

    }

    private void printAllRequestParameters(HttpServletRequest request) {

        if (request == null) {
            System.out.println("REQUEST is Null");
        } else {
            System.out.println("Request NOT NULL");
            if (request.getParameter("submitSelection") != null) {
                this.logger.info("Button wurde geklickt: " + request.getParameter("submitSelection:"));

            }

        }


        Enumeration enAttr = request.getAttributeNames();

        while (enAttr.hasMoreElements()) {
            String attributeName = (String) enAttr.nextElement();
            System.out.println("Attribute Name - " + attributeName + ", Value - " + (request.getAttribute
                    (attributeName)).toString());
        }

        System.out.println("To out-put All the request parameters received from request - ");

        Enumeration enParams = request.getParameterNames();
        while (enParams.hasMoreElements()) {
            String paramName = (String) enParams.nextElement();
            System.out.println("Attribute Name - " + paramName + ", Value - " + request.getParameter(paramName));
        }

    }

}
