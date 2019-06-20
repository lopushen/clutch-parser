package net.omisoft.clutch;

import net.omisoft.clutch.domain.Company;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        parseSingleCategory("https://clutch.co/web-developers");
    }

    private static void parseReviewers(String url) {
        Document doc = createPageDocument(url);
        Elements reviewers = doc.select("div.review-mobile-reviewer2-text");
        reviewers.forEach(e -> System.out.println(e.text()));
    }

    private static Document createPageDocument(String url) {
        try {
            return Jsoup
                    .connect(url)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                    .get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void parseCatgories() {
        Document doc = createPageDocument("https://clutch.co");
        Elements categories = doc.select("li[id^=clutchmenu-mlid]");
        categories.forEach(e -> {
            Elements allElements = e.getAllElements();
            if (allElements.size() == 2) {
                String link = allElements.get(1).attr("href");
                System.out.println(link);
            }
        });
    }

    private static void parseSingleCategory(String categoryUrl) {
        Document doc = createPageDocument(categoryUrl);
        String pager = doc.select("li[class=pager-current]").text();
        String[] split = pager.split(" ");
        Integer currentPage = Integer.parseInt(split[0]);
        Integer totalPages = Integer.parseInt(split[2]);
        System.out.println(currentPage);
        System.out.println(totalPages);

        // let's go through pages

        List<Company> companies = IntStream.range(1, 3/*totalPages + 1*/).boxed().map(i -> {
            Document categoryDoc = createPageDocument(categoryUrl + "?page=" + String.valueOf(i));
            Elements companyNameElements = categoryDoc.select("h3[class=company-name]");
            return companyNameElements.stream().map(e -> {
                Company company = new Company();
                company.setName(e.text());
                company.setUrl("https://clutch.co" + e.getAllElements().get(1).getAllElements().get(1).attr("href"));
                //System.out.println(company);
                return company;
            });
        }).flatMap(x -> x).collect(Collectors.toList());
        companies.forEach(c->parseReviewers(c.getUrl()));
    }

}

