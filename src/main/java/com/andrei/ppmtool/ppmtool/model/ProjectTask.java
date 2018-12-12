package com.andrei.ppmtool.ppmtool.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
public class ProjectTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false)
    private String projectSequence;

    @NotNull(message = "please include a project summaty")
    private String summary;

    private String acceptanceCriteria;

    private String status;

    private Integer priority;

    @Column(updatable = false)
    private String projectIdentifier;

    private Date createAt;

    private Date updateAt;

}
