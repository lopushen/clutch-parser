package net.omisoft.clutch;

import net.omisoft.clutch.domain.Reviewer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MainPerCompany {
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");

    public static void main(String[] args) {
        String fileName = "companies.txt";
        File csvOutputFile = new File("Clutch_report_" + sdf.format(new Date()));
        try (Stream<String> stream = Files.lines(Paths.get(fileName));
             PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println("Link,Project,Project Summary,Reviewer Title,Reviewer Name,Feedback Summary");
            List<Reviewer> reviewers = stream.map(Main::parseReviewersNew).flatMap(Collection::stream)
                    .peek(r -> pw.println(Stream.of(r.getUrl(),r.getProject(), r.getProjectSummary(), r.getTitle(),
                            r.getName(), r.getFeedBackSummary()).collect(Collectors.joining(","))))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
