package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(	name = "volunteering",
        uniqueConstraints = {
        })
public class Volunteering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer volunteeringId;
    @NotBlank
    @Size(max = 20)
    private String num;

    @Size(max = 50)
    private String name;
    private Integer credit;
    @ManyToOne
    @JoinColumn(name="pre_volunteering_id")
    private Volunteering preVolunteering;
    @Size(max = 12)
    private String volunteeringPath;

}
