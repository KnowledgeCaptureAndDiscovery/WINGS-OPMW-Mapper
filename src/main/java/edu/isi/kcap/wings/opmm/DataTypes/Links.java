
package edu.isi.kcap.wings.opmm.DataTypes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Store the path to the provenance pointers: filePath and url
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "filePath",
    "fileUrl",
    "graphUrl"
})
public class Links {

    /**
     * The filePath where the provenance file is stored locally
     * <p>
     * 
     * 
     */
    @JsonProperty("filePath")
    private String filePath = "";
    /**
     * A HTTP URL where the provenance file can be downloaded
     * <p>
     * 
     * 
     */
    @JsonProperty("fileUrl")
    private String fileUrl = "";
    /**
     * The graphURL (rdf) can be queried to get the provenance
     * <p>
     * 
     * 
     */
    @JsonProperty("graphUrl")
    private String graphUrl = "";

    /**
     * The filePath where the provenance file is stored locally
     * <p>
     * 
     * 
     */
    @JsonProperty("filePath")
    public String getFilePath() {
        return filePath;
    }

    /**
     * The filePath where the provenance file is stored locally
     * <p>
     * 
     * 
     */
    @JsonProperty("filePath")
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * A HTTP URL where the provenance file can be downloaded
     * <p>
     * 
     * 
     */
    @JsonProperty("fileUrl")
    public String getFileUrl() {
        return fileUrl;
    }

    /**
     * A HTTP URL where the provenance file can be downloaded
     * <p>
     * 
     * 
     */
    @JsonProperty("fileUrl")
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    /**
     * The graphURL (rdf) can be queried to get the provenance
     * <p>
     * 
     * 
     */
    @JsonProperty("graphUrl")
    public String getGraphUrl() {
        return graphUrl;
    }

    /**
     * The graphURL (rdf) can be queried to get the provenance
     * <p>
     * 
     * 
     */
    @JsonProperty("graphUrl")
    public void setGraphUrl(String graphUrl) {
        this.graphUrl = graphUrl;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Links.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("filePath");
        sb.append('=');
        sb.append(((this.filePath == null)?"<null>":this.filePath));
        sb.append(',');
        sb.append("fileUrl");
        sb.append('=');
        sb.append(((this.fileUrl == null)?"<null>":this.fileUrl));
        sb.append(',');
        sb.append("graphUrl");
        sb.append('=');
        sb.append(((this.graphUrl == null)?"<null>":this.graphUrl));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.graphUrl == null)? 0 :this.graphUrl.hashCode()));
        result = ((result* 31)+((this.fileUrl == null)? 0 :this.fileUrl.hashCode()));
        result = ((result* 31)+((this.filePath == null)? 0 :this.filePath.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Links) == false) {
            return false;
        }
        Links rhs = ((Links) other);
        return ((((this.graphUrl == rhs.graphUrl)||((this.graphUrl!= null)&&this.graphUrl.equals(rhs.graphUrl)))&&((this.fileUrl == rhs.fileUrl)||((this.fileUrl!= null)&&this.fileUrl.equals(rhs.fileUrl))))&&((this.filePath == rhs.filePath)||((this.filePath!= null)&&this.filePath.equals(rhs.filePath))));
    }

}
