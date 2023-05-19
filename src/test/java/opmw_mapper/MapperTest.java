/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opmw_mapper;

import org.apache.jena.ontology.OntModel;
import static org.junit.Assert.*;

/**
 * This class performs different tests with different templates and executions
 *
 * @author Daniel Garijo
 */
public class MapperTest {

        /**
         * Function to validate an OPMW Model. It includes a set of tests for both
         * executions and templates
         *
         * @param m
         */
        public void validateModel(OntModel m) {
                // execution artifacts
                System.out.println("Validating:  ALL EXECUTION ARTIFACTS SHOULD BELONG TO AN ACCOUNT");
                int problems = Integer
                                .parseInt(Utils.getCountOf(Queries.COUNT_ARTIFACTS_WITHOUT_ACCOUNT, m, "countArt"));
                assertTrue(problems <= 0);
                System.out.println(
                                "Validating: ALL EXECUTION ARTIFACTS SHOULD HAVE A LOCATION (VARIABLES) OR VALUE (PARAMETERS).");
                problems = Integer.parseInt(
                                Utils.getCountOf(Queries.COUNT_ARTIFACTS_WITHOUT_LOCATION_OR_VALUE, m, "countArt"));
                assertTrue(problems <= 0);
                System.out.println(
                                "Validating: ALL EXECUTION ARTIFACTS SHOULD BELONG TO A TEMPLATE VARIABLE OR PARAMETER THAT BELONGS TO A TEMPLATE.");
                problems = Integer.parseInt(
                                Utils.getCountOf(Queries.COUNT_ARTIFACTS_WITHOUT_BINDING_TO_TEMPLATE_ARTIFACT, m,
                                                "countArt"));
                assertTrue(problems <= 0);
                System.out.println("Validating: ALL EXECUTION ARTIFACTS SHOULD BE USED OR GENERATED BY A PROCESS.");
                problems = Integer
                                .parseInt(Utils.getCountOf(Queries.COUNT_ARTIFACTS_WITHOUT_BINDING_TO_PROCESS, m,
                                                "countArt"));
                assertTrue(problems <= 0);
                // execution processes
                System.out.println("Validating: ALL EXECUTION PROCESSES SHOULD BELONG TO AN ACCOUNT.");
                problems = Integer.parseInt(Utils.getCountOf(Queries.COUNT_PROCESSES_WITHOUT_ACCOUNT, m, "countProc"));
                assertTrue(problems <= 0);
                System.out.println("Validating: ALL EXECUTION PROCESSES SHOULD USE OR GENERATE SOME ARTIFACT.");
                problems = Integer.parseInt(
                                Utils.getCountOf(Queries.COUNT_PROCESSES_NOT_BOUND_TO_ARTIFACT, m, "countProc"));
                assertTrue(problems <= 0);
                System.out.println(
                                "Validating: ALL EXECUTION PROCESSES SHOULD HAVE AN EXECUTION CODE ASSOCIATED TO THEM.");
                problems = Integer.parseInt(Utils.getCountOf(Queries.COUNT_PROCESSES_WITHOUT_CODE, m, "countProc"));
                assertTrue(problems <= 0);
                System.out.println(
                                "Validating: ALL EXECUTION PROCESSES SHOULD CORRESPOND TO A TEMPLATE PROCESS THAT BELONGS TO A TEMPLATE.");
                problems = Integer
                                .parseInt(Utils.getCountOf(Queries.COUNT_PROCESSES_WITHOUT_CORRECT_TEMPLATE_BINDING, m,
                                                "countProc"));
                assertTrue(problems <= 0);
                // execution accounts
                System.out.println("Validating:  ALL EXECUTIONS MUST BELONG TO A TEMPLATE OR EXPANDED TEMPLATE.");
                problems = Integer.parseInt(Utils.getCountOf(Queries.COUNT_EXECUTIONS_WITHOUT_TEMPLATE, m, "countAcc"));
                assertTrue(problems <= 0);
                System.out.println("Validating:  ALL EXECUTIONS MUST HAVE AN END TIME, A START TIME AND A STATUS.");
                problems = Integer.parseInt(
                                Utils.getCountOf(Queries.COUNT_EXECUTIONS_WITHOUT_TIME_OR_STATUS, m, "countAcc"));
                assertTrue(problems <= 0);
                // template artifacts
                System.out.println("Validating:  ALL TEMPLATE ARTIFCATS MUST BELONG TO A TEMPLATE.");
                problems = Integer.parseInt(
                                Utils.getCountOf(Queries.COUNT_TEMPL_ARTIFACTS_WITHOUT_TEMPLATE, m, "countArt"));
                assertTrue(problems <= 0);
                System.out.println(
                                "Validating:  ALL TEMPLATE ARTIFACTS MUST BE CONNECTED TO A TEMPLATE PROCESS (testing in  OPMW).");
                problems = Integer
                                .parseInt(Utils.getCountOf(Queries.COUNT_TEMPL_ARTIFACTS_WITHOUT_PROCESS_OPMW, m,
                                                "countArt"));
                assertTrue(problems <= 0);
                System.out.println(
                                "Validating:  ALL TEMPLATE ARTIFACTS MUST BE CONNECTED TO A TEMPLATE PROCESS (testing in P-PLAN).");
                problems = Integer
                                .parseInt(Utils.getCountOf(Queries.COUNT_TEMPL_ARTIFACTS_WITHOUT_PROCESS_P_PLAN, m,
                                                "countArt"));
                assertTrue(problems <= 0);
                // template processes
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
                System.out.println("Validating: ARE THERE ANY UNDECLARED WORKFLOW TEMPLATE PROCESSES?.");
                problems = Integer.parseInt(Utils.getCountOf(Queries.COUNT_UNDECLARED_PROCESSES, m, "countProc"));
                assertTrue(problems <= 0);
                System.out.println(
                                "Validating:  ALL TEMPLATE PROCESSES MUST USE OR GENERATE A TEMPLATE ARTIFACT (test in OPMW).");
                problems = Integer.parseInt(
                                Utils.getCountOf(Queries.COUNT_TEMPL_PROCESS_WITHOUT_BINDING_TO_ARTIFACT_OPMW, m,
                                                "countProc"));
                assertTrue(problems <= 0);
                System.out.println(
                                "Validating:  ALL TEMPLATE PROCESSES MUST USE OR GENERATE A TEMPLATE ARTIFACT (test in P-PLAN).");
                problems = Integer.parseInt(
                                Utils.getCountOf(Queries.COUNT_TEMPL_PROCESS_WITHOUT_BINDING_TO_ARTIFACT_PPLAN, m,
                                                "countProc"));
                assertTrue(problems <= 0);

                // expanded template (TO DO)
                System.out.println("Validating:  ALL EXPANDED TEMPLATE PROCESSES SHOULD LINK TO A TEMPLATE.");
                // TO DO
                System.out.println(
                                "Validating:  ALL EXPANDED TEMPLATE PROCESSES SHOULD BE IMPLEMENTATIONS OF A TEMPLATE PROCESS.");
                // TO DO
                System.out.println("Validating:  ALL EXPANDED TEMPLATE SHOULD LINK TO A TEMPLATE.");
                // TO DO
                System.out.println(
                                "Validating:  ALL EXPANDED TEMPLATE VARIABLES SHOULD LINK TO A TEMPLATE VARIABLE THAT BELONG TO A TEMPLATE (AND AND EXECUTION).");
                // TO DO
                System.out.println(
                                "Validating:  ALL EXPANDED PARAMETER VARIABLES SHOULD LINK TO A PARAMETER VARIABLE THAT BELONG TO A TEMPLATE (AND AND EXECUTION).");
                // TO DO
                // the following won't work at the moment
                System.out.println(
                                "Validating:  ALL W3ID COMPONENT CLASSES SHOULD HAVE A LABEL (OTHERWISE THEY ARE NOT CORRECTLY LINKED).");
                // TO DO
                // the following won't work at the moment
                System.out.println(
                                "Validating:  ALL W3ID DATA CLASSES SHOULD HAVE A LABEL (OTHERWISE THEY ARE NOT CORRECTLY LINKED).");
                // TO DO

                // Optional tests not included
                // result+="#TEST"+(++n)+": (OPTIONAL TEST) AN ACCOUNT MAY HAVE A POINTER TO THE
                // ORIGINAL LOG FILE.\n";
                // result+="\t"+isTestFailed(Integer.parseInt(Utils.getCountOf(Queries.COUNT_EXECUTIONS_WITHOUT_LOG_FILE,
                // m, "countAcc")))+"\n";
                // result+="#TEST"+(++n)+": (OPTIONAL TEST) TEMPLATES SHOULD HAVE A VERSION
                // NUMBER.\n";
                // result+="\t"+isTestFailed(Integer.parseInt(Utils.getCountOf(Queries.COUNT_TEMPL_WITHOUT_VERSION_NUMBER,
                // m, "countT")))+"\n";
                // result+="#TEST"+(++n)+": (OPTIONAL TEST) TEMPLATES SHOULD HAVE A POINTER TO
                // THE NATIVE SYSTEM TEMPLATE.\n";
                // result+="\t"+isTestFailed(Integer.parseInt(Utils.getCountOf(Queries.COUNT_TEMPL_WITHOUT_NATIVE_SYS_TEMPL,
                // m, "countT")))+"\n";

        }

}
