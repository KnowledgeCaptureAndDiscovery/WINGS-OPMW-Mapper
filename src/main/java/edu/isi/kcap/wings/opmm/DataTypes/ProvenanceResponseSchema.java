
package edu.isi.kcap.wings.opmm.DataTypes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Root Schema
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "catalog",
    "workflowExecution",
    "workflowExpandedTemplate",
    "workflowAbstractTemplate"
})
public class ProvenanceResponseSchema {

    /**
     * Store the path to the provenance pointers: filePath and url
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("catalog")
    private Links catalog;
    /**
     * Store the path to the provenance pointers: filePath and url
     * <p>
     * 
     * 
     */
    @JsonProperty("workflowExecution")
    private Links workflowExecution;
    /**
     * Store the path to the provenance pointers: filePath and url
     * <p>
     * 
     * 
     */
    @JsonProperty("workflowExpandedTemplate")
    private Links workflowExpandedTemplate;
    /**
     * Store the path to the provenance pointers: filePath and url
     * <p>
     * 
     * 
     */
    @JsonProperty("workflowAbstractTemplate")
    private Links workflowAbstractTemplate;

    /**
     * Store the path to the provenance pointers: filePath and url
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("catalog")
    public Links getCatalog() {
        return catalog;
    }

    /**
     * Store the path to the provenance pointers: filePath and url
     * <p>
     * 
     * (Required)
     * 
     */
    @JsonProperty("catalog")
    public void setCatalog(Links catalog) {
        this.catalog = catalog;
    }

    /**
     * Store the path to the provenance pointers: filePath and url
     * <p>
     * 
     * 
     */
    @JsonProperty("workflowExecution")
    public Links getWorkflowExecution() {
        return workflowExecution;
    }

    /**
     * Store the path to the provenance pointers: filePath and url
     * <p>
     * 
     * 
     */
    @JsonProperty("workflowExecution")
    public void setWorkflowExecution(Links workflowExecution) {
        this.workflowExecution = workflowExecution;
    }

    /**
     * Store the path to the provenance pointers: filePath and url
     * <p>
     * 
     * 
     */
    @JsonProperty("workflowExpandedTemplate")
    public Links getWorkflowExpandedTemplate() {
        return workflowExpandedTemplate;
    }

    /**
     * Store the path to the provenance pointers: filePath and url
     * <p>
     * 
     * 
     */
    @JsonProperty("workflowExpandedTemplate")
    public void setWorkflowExpandedTemplate(Links workflowExpandedTemplate) {
        this.workflowExpandedTemplate = workflowExpandedTemplate;
    }

    /**
     * Store the path to the provenance pointers: filePath and url
     * <p>
     * 
     * 
     */
    @JsonProperty("workflowAbstractTemplate")
    public Links getWorkflowAbstractTemplate() {
        return workflowAbstractTemplate;
    }

    /**
     * Store the path to the provenance pointers: filePath and url
     * <p>
     * 
     * 
     */
    @JsonProperty("workflowAbstractTemplate")
    public void setWorkflowAbstractTemplate(Links workflowAbstractTemplate) {
        this.workflowAbstractTemplate = workflowAbstractTemplate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ProvenanceResponseSchema.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("catalog");
        sb.append('=');
        sb.append(((this.catalog == null)?"<null>":this.catalog));
        sb.append(',');
        sb.append("workflowExecution");
        sb.append('=');
        sb.append(((this.workflowExecution == null)?"<null>":this.workflowExecution));
        sb.append(',');
        sb.append("workflowExpandedTemplate");
        sb.append('=');
        sb.append(((this.workflowExpandedTemplate == null)?"<null>":this.workflowExpandedTemplate));
        sb.append(',');
        sb.append("workflowAbstractTemplate");
        sb.append('=');
        sb.append(((this.workflowAbstractTemplate == null)?"<null>":this.workflowAbstractTemplate));
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
        result = ((result* 31)+((this.workflowExecution == null)? 0 :this.workflowExecution.hashCode()));
        result = ((result* 31)+((this.workflowExpandedTemplate == null)? 0 :this.workflowExpandedTemplate.hashCode()));
        result = ((result* 31)+((this.catalog == null)? 0 :this.catalog.hashCode()));
        result = ((result* 31)+((this.workflowAbstractTemplate == null)? 0 :this.workflowAbstractTemplate.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ProvenanceResponseSchema) == false) {
            return false;
        }
        ProvenanceResponseSchema rhs = ((ProvenanceResponseSchema) other);
        return (((((this.workflowExecution == rhs.workflowExecution)||((this.workflowExecution!= null)&&this.workflowExecution.equals(rhs.workflowExecution)))&&((this.workflowExpandedTemplate == rhs.workflowExpandedTemplate)||((this.workflowExpandedTemplate!= null)&&this.workflowExpandedTemplate.equals(rhs.workflowExpandedTemplate))))&&((this.catalog == rhs.catalog)||((this.catalog!= null)&&this.catalog.equals(rhs.catalog))))&&((this.workflowAbstractTemplate == rhs.workflowAbstractTemplate)||((this.workflowAbstractTemplate!= null)&&this.workflowAbstractTemplate.equals(rhs.workflowAbstractTemplate))));
    }

}
