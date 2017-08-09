package com.wec.resume.model;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by Majfrendmartin on 2017-08-05.
 */

@Data
@Accessors(chain = true)
public abstract class Section<T> {

    public enum SectionType {
        EDUCATION, JOBS, SKILLS
    }

    private String title;
    private String cover;
    private List<T> items;

    public abstract SectionType getType();
}