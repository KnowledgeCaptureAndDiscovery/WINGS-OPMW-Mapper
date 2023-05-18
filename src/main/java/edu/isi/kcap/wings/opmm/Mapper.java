package edu.isi.kcap.wings.opmm;

import java.io.File;
import java.io.IOException;

import org.apache.jena.dboe.base.file.FileException;

/**
 *
 * @author Daniel Garijo
 */
public class Mapper {
    /**
     * Most of these will be reused from the old code, because it works.
     * The mapper initializes the catalog and calls to the template exporter.
     *
     * @param domain
     * @param filePublisher
     * @param abstractFilePath
     * @param exportPrefix:                        Used to generate the URIs of the
     *                                             exported
     *                                             files
     * @param exportUrl:                           ??
     * @param catalogRepository:                   The directory where the catalog
     *                                             will be
     *                                             stored
     * @param componentLibraryFilePath:            The path to the component library
     *                                             file
     * @param rplanfilePath:                       The path to the Workflow plan
     *                                             file
     * @param endpointQueryURI:                    The URI of the endpoint where the
     *                                             queries will be sent
     * @param endpointPostURI:                     The URI of the endpoint where the
     *                                             triples will be sent
     * @param executionDestinationFilePath:        The path to the execution file
     *                                             that will be generated by the
     *                                             mapper
     * @param expandedTemplateDestinationFilePath: The path to the expanded template
     *                                             file that will be generated by
     *                                             the mapper
     * @throws IOException
     */
    public static void main(String domain, String exportPrefix, String exportUrl, String catalogRepository,
            String componentLibraryFilePath, String planFilePath, String endpointQueryURI, String endpointPostURI,
            String executionDestinationFilePath, String expandedTemplateDestinationFilePath, String abstractFilePath,
            FilePublisher filePublisher, String serialization)
            throws IOException, FileException {
        // Create the catalog
        Catalog catalog = new Catalog(domain, exportPrefix,
                catalogRepository, componentLibraryFilePath);
        WorkflowExecutionExport executionExport = new WorkflowExecutionExport(planFilePath, catalog, exportUrl,
                exportPrefix,
                endpointQueryURI, domain,
                filePublisher);
        // Export the catalog
        String domainPath = catalog.exportCatalog(catalogRepository, serialization);
        // this.publishFile(tstoreurl, catalog.getDomainGraphURI(),
        // new File(domainPath).getAbsolutePath());

        String abstractGraphUri = null;
        String expandedTemplateGraphUri = null;

        // execution
        String graphUri = executionExport.exportAsOPMW(executionDestinationFilePath, serialization);
        if (!executionExport.isExecPublished()) {
            // TODO: enable publishing of execution
            // this.publishFile(endpointPostURI, graphUri, executionFilePath);
            expandedTemplateGraphUri = executionExport.getConcreteTemplateExport().exportAsOPMW(
                    expandedTemplateDestinationFilePath,
                    serialization);
            // TODO: enable publishing of expanded template
            // if (!executionExport.getConcreteTemplateExport().isTemplatePublished()){
            // this.publishFile(endpointPostURI, expandedTemplateGraphUri,
            // expandedTemplateFilePath);
            // }

            // abstract
            WorkflowTemplateExport abstractTemplateExport = executionExport.getConcreteTemplateExport()
                    .getAbstractTemplateExport();
            if (abstractTemplateExport != null) {
                abstractGraphUri = abstractTemplateExport.exportAsOPMW(abstractFilePath, serialization);
                // if (!abstractTemplateExport.isTemplatePublished())
                // this.publishFile(tstoreurl, abstractGraphUri, abstractFilePath);
            }
        }
        System.out.println(domainPath);
        System.out.println(graphUri);
        System.out.println(expandedTemplateGraphUri);
        System.out.println(abstractGraphUri);
    }

}
