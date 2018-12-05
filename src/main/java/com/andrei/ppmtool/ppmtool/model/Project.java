package com.andrei.ppmtool.ppmtool.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Project Name is Required")
    private String projectName;

    @NotBlank(message = "Project Identifier is Required")
    @Size(min = 4, max = 5, message = "Please use 4 or 5 characters")
    @Column(updatable = false, unique = true)
    private String projectIdentifier;

    @NotBlank(message = "Project description is required")
    private String description;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate startDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate endDate;

    @CreatedDate
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate createDate;

    @LastModifiedDate
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate updateDate;


}
