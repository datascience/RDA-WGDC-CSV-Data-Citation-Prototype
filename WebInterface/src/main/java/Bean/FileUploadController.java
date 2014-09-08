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

package Bean;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by stefan on 17.06.14.
 */

@ManagedBean
@SessionScoped
public class FileUploadController implements Serializable {
    private String tableName;
    private HashMap<String, String> filesList;
    private List<String> filesListStrings;

    public FileUploadController() {
        this.filesList = new HashMap<String, String>();
        this.filesListStrings = new ArrayList<String>();


    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getFilesListStrings() {
        return filesListStrings;
    }

    public void setFilesListStrings(List<String> filesListStrings) {
        this.filesListStrings = filesListStrings;
    }

    public HashMap<String, String> getFilesList() {
        return filesList;
    }

    public void setFilesList(HashMap<String, String> filesList) {
        this.filesList = filesList;
    }

    public void handleFileUpload(FileUploadEvent event) {
        System.out.println("Upload event...");
        UploadedFile file = event.getFile();


        this.filesListStrings.add(file.getFileName());


        FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded. Textfield: "
                + tableName);
        FacesContext.getCurrentInstance().addMessage(null, msg);


        System.out.println("added to files list " + filesList + " with Summary field " + tableName);

        this.storeFiles(file);
        this.storeSessionData();

    }


    public void storeFiles(UploadedFile file) {
        System.out.println("Store event...");
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");

        String path = FacesContext.getCurrentInstance().getExternalContext()
                .getRealPath("/");


        String name = this.tableName + "_" + fmt.format(new Date())
                + file.getFileName().substring(
                file.getFileName().lastIndexOf('.')) + ".csv";

        File fileToStore = new File(path + "/" + name);
        System.out.println("Path set to: " + fileToStore.getAbsolutePath());

        InputStream is = null;
        try {
            is = file.getInputstream();
            OutputStream out = new FileOutputStream(fileToStore);
            byte buf[] = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0)
                out.write(buf, 0, len);
            is.close();
            out.close();
        } catch (IOException e) {
            System.out.println("error");
            e.printStackTrace();

        }

        this.filesList.put(this.tableName, fileToStore.getAbsolutePath());


    }

    private void storeSessionData() {
        System.out.println("Session data function");

        // schreiben
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        session.put("fileListHashMap", this.filesList);

        // schreiben

        session.put("currentTableName", this.tableName);


        // lesen
        Map<String, Object> sessionMAP = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        HashMap<String, String> filesList = (HashMap<String, String>) sessionMAP.get("fileListHashMap");


        System.out.println("SEEEEEEEEEEEEEESION " + filesList.size() + " table name " + this.tableName);
    }

}