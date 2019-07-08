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
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    public static void main(String[] args) {
        String fileName = "companies.txt";
        File csvOutputFile = new File("Clutch_report_" + sdf.format(new Date()));
        try (Stream<String> stream = Files.lines(Paths.get(fileName));
             PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.print("Link,Project,Project Summary,Reviewer Title,Reviewer Name");
            List<Reviewer> reviewers = stream.map(Main::parseReviewersNew).flatMap(Collection::stream)
                    .peek(r -> pw.print(Stream.of(r.getUrl(), r.getProjectSummary(), r.getTitle(), r.getName()).collect(Collectors.joining(","))))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
