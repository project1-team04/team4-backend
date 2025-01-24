package com.elice.team04backend.dto.search;

import com.elice.team04backend.common.constant.IssueStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IssueSearchCondition {
    private String condition;
}
