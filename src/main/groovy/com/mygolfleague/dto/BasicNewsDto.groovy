package com.mygolfleague.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.core.annotation.Introspected

@Introspected
class BasicNewsDto {
    String id
    String title
    String summary
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "M/d/yyyy h:mm a")
    Date publishedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "M/d/yyyy h:mm a")
    Date lastUpdatedDate
    String author
    String updatedBy
}
