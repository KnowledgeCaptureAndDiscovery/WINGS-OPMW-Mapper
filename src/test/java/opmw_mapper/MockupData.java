package opmw_mapper;

import java.util.ArrayList;
import java.util.List;

public class MockupData {
  static List<String> metaAnalysisWorkflowExecution() {
    List<String> entitiesSearched = new ArrayList<String>();
    entitiesSearched.add(
        "http://www.opmw.org/exportTest/resource/WorkflowExecutionArtifact/Meta-Analysis-57-52f5b3c2-a970-42ed-a503-fa4dfdd62ecd_p_value");
    entitiesSearched.add(
        "http://www.opmw.org/exportTest/resource/WorkflowExecutionArtifact/Meta-Analysis-57-52f5b3c2-a970-42ed-a503-fa4dfdd62ecd_cohortData_0001");
    entitiesSearched.add("http://www.opmw.org/exportTest/resource/Agent/WINGS");
    entitiesSearched.add(
        "http://www.opmw.org/exportTest/resource/SoftwareConfiguration/Meta-Analysis-57-52f5b3c2-a970-42ed-a503-fa4dfdd62ecd_AddDemographicMergeAndFilterNode_config");
    entitiesSearched.add(
        "http://www.opmw.org/exportTest/resource/WorkflowExecutionAccount/Meta-Analysis-57-52f5b3c2-a970-42ed-a503-fa4dfdd62ecd");
    return entitiesSearched;
  }
}
