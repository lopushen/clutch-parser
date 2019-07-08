package net.omisoft.clutch.domain;

import lombok.Data;
import lombok.ToString;

@Data @ToString
public class Reviewer {
    private String name;
    private String title;
    private String verified;
    private String url;
    private String projectSummary;
}
