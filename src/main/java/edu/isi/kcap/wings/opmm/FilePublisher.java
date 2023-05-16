package edu.isi.kcap.wings.opmm;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;

import org.apache.commons.io.FileUtils;

public class FilePublisher {
    private HttpURLConnection httpConn;
    private DataOutputStream request;
    private final String boundary = "*****";
    private final String crlf = "\r\n";
    private final String twoHyphens = "--";
    public String server = null;
    public String username = null;
    public String password = null;
    // Use it when you need to copy (not upload) a file to a specific directory
    // This directory must be accessible by the user through WebServer (nginx,
    // apache, etc.)
    // For example: webServerDirectory: /var/www/html
    // For example: webServerDomain: http://localhost
    private Boolean local = true;
    public String webServerDirectory = null;
    private String webServerDomain = null;

    public FilePublisher(String server, String username, String password) {
        this.server = server;
        this.username = username;
        this.password = password;
        this.local = false;
    }

    public FilePublisher(String webServerDirectory, String webServerDomain) {
        this.webServerDirectory = webServerDirectory;
        this.webServerDomain = webServerDomain;
        this.local = true;
    }

    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     *
     * @param requestURL
     * @throws IOException
     */
    public void upload(String requestURL, String username, String password)
            throws IOException {

        // creates a unique boundary based on time stamp
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestMethod("POST");

        httpConn.setRequestProperty("Connection", "Keep-Alive");
        httpConn.setRequestProperty("Cache-Control", "no-cache");
        httpConn.setRequestProperty(
                "Content-Type", "multipart/form-data;boundary=" + this.boundary);
        String usernameColonPassword = username + ':' + password;
        String basicAuthPayload = "Basic " + Base64.getEncoder().encodeToString(usernameColonPassword.getBytes());
        httpConn.setRequestProperty("Authorization", basicAuthPayload);
        request = new DataOutputStream(httpConn.getOutputStream());
    }

    /**
     * Adds a upload file section to the request
     *
     * @param fieldName  name attribute in {@literal /} input type="file" name="..."
     *                   {@literal />}
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"" +
                fieldName + "\";filename=\"" +
                fileName + "\"" + this.crlf);
        request.writeBytes(this.crlf);

        byte[] bytes = Files.readAllBytes(uploadFile.toPath());
        request.write(bytes);
    }

    /**
     * Upload a file to publisher
     *
     * @param filePath the path of the file
     * @return a string with URL
     * @throws IOException
     */
    public String publishFile(String filePath) throws IOException {
        if (this.local) {
            File mainScriptFile = new File(filePath.replaceAll("\\s", ""));
            File toFile = new File(this.webServerDirectory + File.separator + mainScriptFile.getName());
            FileUtils.copyFile(mainScriptFile, toFile);
            return this.webServerDirectory + "/" + mainScriptFile.getName();
        } else {
            throw new IOException("Server returned non-OK status: " + 500);
        }
    }
    // } else {
    // Publisher upload = new Publisher(this.uploadURL, this.uploadUsername,
    // this.uploadPassword);
    // File mainScriptFile = new File(filePath);
    // if (this.uploadMaxSize != 0 && mainScriptFile.length() > this.uploadMaxSize)
    // {
    // return mainScriptFile.getAbsolutePath();
    // }
    // upload.addFilePart("file_param_1", mainScriptFile);
    // return upload.finish().replaceAll("\n", "");
    // }

    /**
     * Completes the request and receives response from the server.
     *
     * @return a list of Strings as response in case the server returned
     *         status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public String finish() throws IOException {
        String response = "";

        request.writeBytes(this.crlf);
        request.writeBytes(this.twoHyphens + this.boundary +
                this.twoHyphens + this.crlf);

        request.flush();
        request.close();

        // checks server's status code first
        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            InputStream responseStream = new BufferedInputStream(httpConn.getInputStream());

            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));

            String line = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();

            response = stringBuilder.toString();
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }

        return response;
    }
}