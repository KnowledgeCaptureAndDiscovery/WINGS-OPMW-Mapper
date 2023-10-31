package opmw_mapper;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.sparql.function.library.e;
import org.junit.Assert;
import org.junit.Test;

import edu.isi.kcap.wings.opmm.Constants;
import edu.isi.kcap.wings.opmm.FilePublisher;
import edu.isi.kcap.wings.opmm.Mapper;
import edu.isi.kcap.wings.opmm.DataTypes.ProvenanceResponseSchema;
import edu.isi.kcap.wings.opmm.Publisher.TriplesPublisher;

public class MapperTestNew {
        private static String webServerDirectory = "tmp/";
        private static String webServerDomain = "http://localhost";
        FilePublisher filePublisher = new FilePublisher(FilePublisher.Type.FILE_SYSTEM, webServerDirectory,
                        webServerDomain);
        // Domain: it is wings domain concept: it is the name of the domain
        String domain = "neuroDisk";
        // exportPrefix: used by the catalog to create the URL:
        // w3id.org/opmw/wings/{exportName}/{domain}#Components
        // w3id.org/opmw/wings/{exportName}/{domain}#Data
        String exportPrefix = "exportTest";
        String exportUrl = "http://www.opmw.org/";
        // catalogRepositoryDirectory: the directory where the catalog will be stored
        String catalogRepositoryDirectory = "domains";
        String endpointQueryURI = "https://endpoint.mint.isi.edu/provenance/query";
        String endpointPostURI = "https://endpoint.mint.isi.edu/provenance/data";
        String componentLibraryFilePath = "src/test/resources/neuro/components.owl";
        String planFilePath = "src/test/resources/neuro/plan.owl";

        @Test
        public void testMapperTurtle() throws IOException {
                String serialization = "TTL";
                File file = new File("tmp/" + serialization);
                if (!file.exists()) {
                        file.mkdir();
                }
                String targetDirectory = "tmp/" + serialization;
                String executionFilePath = targetDirectory + "/execution.ttl";
                String expandedTemplateFilePath = targetDirectory + "/expandedTemplate.ttl";
                String abstractFilePath = targetDirectory + "/abstractTemplate.ttl";
                TriplesPublisher executionTriplesPublisher = new TriplesPublisher(endpointQueryURI, endpointPostURI,
                                exportUrl,
                                exportPrefix, serialization);
                TriplesPublisher catalogTriplesPublisher = new TriplesPublisher(endpointQueryURI, endpointPostURI,
                                Constants.CATALOG_URI,
                                exportPrefix, serialization);
                ProvenanceResponseSchema response = Mapper.main(domain, catalogRepositoryDirectory,
                                componentLibraryFilePath, planFilePath, executionFilePath,
                                expandedTemplateFilePath, abstractFilePath,
                                filePublisher, executionTriplesPublisher, catalogTriplesPublisher);

                assertTrue(response.getCatalog().getFilePath().contains("domains/neuroDisk"));
                assertTrue(response.getCatalog().getFileUrl().contains(catalogTriplesPublisher.exportUrl));
                assertTrue(response.getCatalog().getGraphUrl().contains(catalogTriplesPublisher.exportUrl));
                // Test that the execution is published
                assertTrue(response.getWorkflowExecution().getFilePath().contains("tmp/TTL/execution.ttl"));
                assertTrue(response.getWorkflowExecution().getFileUrl().contains(executionTriplesPublisher.exportUrl));
                assertTrue(response.getWorkflowExecution().getGraphUrl().contains(executionTriplesPublisher.exportUrl));
                assertTrue(response.getWorkflowExpandedTemplate().getFilePath()
                                .contains("tmp/TTL/expandedTemplate.ttl"));
                assertTrue(response.getWorkflowExpandedTemplate().getFileUrl()
                                .contains(executionTriplesPublisher.exportUrl));
                assertTrue(response.getWorkflowExpandedTemplate().getGraphUrl()
                                .contains(executionTriplesPublisher.exportUrl));
                assertTrue(response.getWorkflowAbstractTemplate().getFilePath()
                                .contains("tmp/TTL/abstractTemplate.ttl"));
                assertTrue(response.getWorkflowAbstractTemplate().getFileUrl()
                                .contains(executionTriplesPublisher.exportUrl));
                assertTrue(response.getWorkflowAbstractTemplate().getGraphUrl()
                                .contains(executionTriplesPublisher.exportUrl));

                OntModel model = Utils.loadDirectory(targetDirectory);
                validateModel(model);
        }

        @Test
        public void testXMLTurtle() throws IOException {
                String serialization = "RDF/XML";
                File file = new File("tmp/" + serialization);
                if (!file.exists()) {
                        file.mkdirs();
                }
                String executionFilePath = "tmp/" + serialization + "/execution.xml";
                String expandedTemplateFilePath = "tmp/" + serialization + "/expandedTemplate.xml";
                String abstractFilePath = "tmp/" + serialization + "/abstractTemplate.xml";
                TriplesPublisher executionTriplesPublisher = new TriplesPublisher(endpointQueryURI, endpointPostURI,
                                exportUrl,
                                exportPrefix, serialization);
                TriplesPublisher catalogTriplesPublisher = new TriplesPublisher(endpointQueryURI, endpointPostURI,
                                Constants.CATALOG_URI,
                                exportPrefix, serialization);
                try {
                        Mapper.main(domain, catalogRepositoryDirectory, componentLibraryFilePath, planFilePath,
                                        executionFilePath,
                                        expandedTemplateFilePath, abstractFilePath,
                                        filePublisher, executionTriplesPublisher, catalogTriplesPublisher);
                } catch (Exception e) {
                        e.printStackTrace();
                        Assert.assertTrue(false);
                }
                List<String> entities = MockupData.metaAnalysisWorkflowExecution();
                for (String entity : entities) {
                        Utils.checkExecutionXML(entity, executionFilePath);
                }

        }

        public void validateModel(OntModel m) {
                /*
                 * Check that artifact is connected to a:
                 * 1. Account
                 * 2. Location or value
                 * 3. Template artifact
                 * 4. Process
                 */
                int problems = Integer
                                .parseInt(Utils.getCountOf(Queries.COUNT_ARTIFACTS_WITHOUT_ACCOUNT, m, "countArt"));
                assertTrue(problems <= 0);
                problems = Integer.parseInt(
                                Utils.getCountOf(Queries.COUNT_ARTIFACTS_WITHOUT_LOCATION_OR_VALUE, m, "countArt"));
                assertTrue(problems <= 0);
                problems = Integer.parseInt(
                                Utils.getCountOf(Queries.COUNT_ARTIFACTS_WITHOUT_BINDING_TO_TEMPLATE_ARTIFACT, m,
                                                "countArt"));
                assertTrue(problems <= 0);
                problems = Integer
                                .parseInt(Utils.getCountOf(Queries.COUNT_ARTIFACTS_WITHOUT_BINDING_TO_PROCESS, m,
                                                "countArt"));
                assertTrue(problems <= 0);
                /**
                 * Check that execution process is connected to a:
                 * 1. Account
                 * 2. Artifact
                 * 3. Execution code
                 * 4. Template process
                 */
                problems = Integer.parseInt(Utils.getCountOf(Queries.COUNT_PROCESSES_WITHOUT_ACCOUNT, m, "countProc"));
                assertTrue(problems <= 0);
                problems = Integer.parseInt(
                                Utils.getCountOf(Queries.COUNT_PROCESSES_NOT_BOUND_TO_ARTIFACT, m, "countProc"));
                assertTrue(problems <= 0);
                problems = Integer.parseInt(Utils.getCountOf(Queries.COUNT_PROCESSES_WITHOUT_CODE, m, "countProc"));
                assertTrue(problems <= 0);
                problems = Integer
                                .parseInt(Utils.getCountOf(Queries.COUNT_PROCESSES_WITHOUT_CORRECT_TEMPLATE_BINDING, m,
                                                "countProc"));
                assertTrue(problems <= 0);
                // execution accounts
                /**
                 * Check that execution account is connected to a:
                 * 1. Template account
                 * 2. Time or status
                 */
                problems = Integer.parseInt(Utils.getCountOf(Queries.COUNT_EXECUTIONS_WITHOUT_TEMPLATE, m, "countAcc"));
                assertTrue(problems <= 0);
                problems = Integer.parseInt(
                                Utils.getCountOf(Queries.COUNT_EXECUTIONS_WITHOUT_TIME_OR_STATUS, m, "countAcc"));
                assertTrue(problems <= 0);
                /**
                 * Check that template account is connected to a:
                 * 1. Template
                 * 2. Process
                 * 3. Plan
                 */
                problems = Integer.parseInt(
                                Utils.getCountOf(Queries.COUNT_TEMPL_ARTIFACTS_WITHOUT_TEMPLATE, m, "countArt"));
                assertTrue(problems <= 0);
                problems = Integer
                                .parseInt(Utils.getCountOf(Queries.COUNT_TEMPL_ARTIFACTS_WITHOUT_PROCESS_OPMW, m,
                                                "countArt"));
                assertTrue(problems <= 0);
                problems = Integer
                                .parseInt(Utils.getCountOf(Queries.COUNT_TEMPL_ARTIFACTS_WITHOUT_PROCESS_P_PLAN, m,
                                                "countArt"));
                assertTrue(problems <= 0);
                /*
                 * Check that template process is connected to a:
                 * 1. Artifact (OPMW)
                 * 2. Template (OPMW)
                 * 3. Template (P-PLAN)
                 */
                System.out.println(
                                "Validating: ALL TEMPLATE PROCESSES MUST USE OR GENERATE A TEMPLATE ARTIFACT (test in P-PLAN).");
                problems = Integer.parseInt(
                                Utils.getCountOf(Queries.COUNT_TEMPL_PROCESS_WITHOUT_BINDING_TO_ARTIFACT_OPMW, m,
                                                "countProc"));
                assertTrue(problems <= 0);
                System.out.println("Validating: ALL TEMPLATE PROCESSES MUST BELONG TO A TEMPLATE (test in OPMW).");
                problems = Integer
                                .parseInt(Utils.getCountOf(Queries.COUNT_TEMPL_PROCESSES_WITHOUT_TEMPLATE_OPMW, m,
                                                "countProc"));
                assertTrue(problems <= 0);
                System.out.println("Validating: ALL TEMPLATE PROCESSES MUST BELONG TO A TEMPLATE (test in PPlan).");
                problems = Integer
                                .parseInt(Utils.getCountOf(Queries.COUNT_TEMPL_PROCESSES_WITHOUT_TEMPLATE_PPLAN, m,
                                                "countProc"));
                assertTrue(problems <= 0);
                /**
                 * Check if there are any undeclared workflow template processes
                 */
                problems = Integer.parseInt(Utils.getCountOf(Queries.COUNT_UNDECLARED_PROCESSES, m, "countProc"));
                assertTrue(problems <= 0);
                /**
                 * Check that all template processes must use or generate a template artifact
                 * (OPMW)
                 */
                problems = Integer.parseInt(
                                Utils.getCountOf(Queries.COUNT_TEMPL_PROCESS_WITHOUT_BINDING_TO_ARTIFACT_OPMW, m,
                                                "countProc"));
                assertTrue(problems <= 0);

                /**
                 * Check that all template processes must use or generate a template artifact
                 * (P-PLAN)
                 */
                problems = Integer.parseInt(
                                Utils.getCountOf(Queries.COUNT_TEMPL_PROCESS_WITHOUT_BINDING_TO_ARTIFACT_PPLAN, m,
                                                "countProc"));
                assertTrue(problems <= 0);

                // TODO: Test | All expanded template processes should link to a template
                // process.
                // TODO: Test | All expanded template should link to a template process.
                // TODO: Test | All expanded template should like to a template.
                // TODO: Test | All expanded template variables should link to a template
                // variable that belong to a template (and and execution).
                // TODO: Test | All expanded parameter variables should link to a parameter
                // variable that belong to a template (and and execution).
                // TODO: Test | All w3id component classes should have a label (otherwise they
                // are not correctly linked).
                // TODO: Test | All w3id data classes should have a label (otherwise they are
                // not correctly linked).

        }
}
