package com.andrei.ppmtool.ppmtool.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProjectTask extends BaseEntity {

    @Column(updatable = false, unique = true)
    private String projectSequence;

    @NotNull(message = "please include a project summary")
    private String summary;

    private String acceptanceCriteria;

    private String status;

    private int priority;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date dueDate;

    @Column(updatable = false)
    private String projectIdentifier;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "backlog_id", updatable = false, nullable = false)
    @JsonIgnore
    private Backlog backlog;

}
