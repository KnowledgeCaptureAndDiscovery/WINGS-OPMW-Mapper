package edu.isi.kcap.wings.opmm;

import java.io.FileNotFoundException;
import java.io.IOException;
import edu.isi.kcap.wings.opmm.DataTypes.Links;
import edu.isi.kcap.wings.opmm.DataTypes.ProvenanceResponseSchema;
import edu.isi.kcap.wings.opmm.Publisher.TriplesPublisher;

/**
 *
 * @author Daniel Garijo
 */
public class Mapper {
        private static final String EXECUTION = "execution";
        private static final String EXPANDED_TEMPLATE = "expandedTemplate";
        private static final String ABSTRACT_TEMPLATE = "abstractTemplate";

        public static ProvenanceResponseSchema main(String domain,
                        String catalogRepository,
                        String componentLibraryFilePath, String planFilePath,
                        String executionDestinationFilePath, String expandedTemplateDestinationFilePath,
                        String abstractFilePath,
                        FilePublisher filePublisher, TriplesPublisher workflowPublisher,
                        TriplesPublisher catalogPublisher)
                        throws IOException {
                // Response
                ProvenanceResponseSchema response = new ProvenanceResponseSchema();
                // Create the catalog
                Catalog catalog = new Catalog(domain, catalogPublisher.exportUrl,
                                catalogRepository, componentLibraryFilePath);
                // Create the workflow execution export
                WorkflowExecutionExport executionExport = new WorkflowExecutionExport(planFilePath, catalog,
                                workflowPublisher.exportUrl,
                                domain,
                                filePublisher, workflowPublisher);

                exportExecution(executionDestinationFilePath, workflowPublisher.serialization, response,
                                executionExport);
                        exportExpandedTemplate(expandedTemplateDestinationFilePath, workflowPublisher.serialization,
                                        response,
                                        executionExport);
                        exportAbstractTemplate(abstractFilePath, workflowPublisher.serialization, response,
                                        executionExport);
                exportCatalog(catalogRepository, catalogPublisher.serialization, response, catalog);
                return response;
        }

        private static void exportCatalog(String catalogRepository, String serialization,
                        ProvenanceResponseSchema response,
                        Catalog catalog) throws IOException {
                // Export the catalog
                String domainPath = catalog.exportCatalog(catalogRepository, serialization);
                // this.publishFile(tstoreurl, catalog.getDomainGraphURI(),
                // new File(domainPath).getAbsolutePath());
                Links links = new Links();
                links.setFilePath(domainPath);
                links.setFileUrl(catalog.getDomainGraphURI());
                links.setGraphUrl(catalog.getDomainGraphURI());
                response.setCatalog(links);
        }

        private static void exportExpandedTemplate(String expandedTemplateDestinationFilePath, String serialization,
                        ProvenanceResponseSchema response, WorkflowExecutionExport executionExport) throws IOException {
                String expandedTemplateGraphUri = executionExport.getConcreteTemplateExport().exportAsOPMW(
                                expandedTemplateDestinationFilePath,
                                serialization);
                String concreteTemplateGraphUri = executionExport.getConcreteTemplateExport().exportAsOPMW(
                                expandedTemplateDestinationFilePath,
                                serialization);
                Links links = new Links();
                links.setFilePath(expandedTemplateDestinationFilePath);
                links.setFileUrl(expandedTemplateGraphUri);
                links.setGraphUrl(concreteTemplateGraphUri);
                response.setWorkflowExpandedTemplate(links);
        }

        private static void exportExecution(String executionDestinationFilePath, String serialization,
                        ProvenanceResponseSchema response, WorkflowExecutionExport executionExport)
                        throws IOException, FileNotFoundException {
                String executionGraphUri = executionExport.exportAsOPMW(executionDestinationFilePath, serialization);
                Links links = new Links();
                links.setFilePath(executionDestinationFilePath);
                links.setFileUrl(executionGraphUri);
                links.setGraphUrl(executionGraphUri);
                response.setWorkflowExecution(links);
        }

        private static void exportAbstractTemplate(String abstractFilePath, String serialization,
                        ProvenanceResponseSchema response,
                        WorkflowExecutionExport executionExport) throws IOException {
                WorkflowTemplateExport abstractTemplateExport = executionExport.getConcreteTemplateExport()
                                .getAbstractTemplateExport();
                if (abstractTemplateExport != null) {
                        String abstractGraphUri = abstractTemplateExport.exportAsOPMW(abstractFilePath, serialization);
                        // if (!abstractTemplateExport.isTemplatePublished())
                        // this.publishFile(tstoreurl, abstractGraphUri, abstractFilePath);
                        Links links = new Links();
                        links.setFilePath(abstractFilePath);
                        links.setFileUrl(abstractGraphUri);
                        links.setGraphUrl(abstractGraphUri);
                        response.setWorkflowAbstractTemplate(links);
                }
        }

}
