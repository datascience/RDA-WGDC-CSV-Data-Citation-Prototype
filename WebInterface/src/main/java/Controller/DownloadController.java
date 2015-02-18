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

package Controller;


import java.io.*;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean
public class DownloadController implements Serializable {

    private StreamedContent file;

    public DownloadController() {
        
        //InputStream stream = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("images/optimus.jpg");
        
        InputStream stream = null;
        try {
            stream = new FileInputStream(new File("/tmp/test.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        file = new DefaultStreamedContent(stream, "image/jpg", "downloaded_optimus.jpg");
    }

    public StreamedContent getFile() {
        return file;
    }
}
