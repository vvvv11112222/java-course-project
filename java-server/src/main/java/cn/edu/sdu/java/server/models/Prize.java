package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(	name = "prize",
        uniqueConstraints = {
        })
public class Prize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer prizeId;
    @NotBlank
    @Size(max = 20)
    private String num;

    @Size(max = 50)
    private String name;
    @Size(max = 20)
    private String prizeLevel;

    @ManyToOne
    @JoinColumn(name="pre_prize_id")
    private Prize prePrize;
    @Size(max = 12)
    private String prizePath;

}
