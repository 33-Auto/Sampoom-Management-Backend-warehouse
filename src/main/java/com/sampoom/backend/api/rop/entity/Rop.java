package com.sampoom.backend.api.rop.entity;

import com.sampoom.backend.api.branch.entity.Branch;
import com.sampoom.backend.api.part.entity.Part;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "part_id")
    private Part part;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    private Integer rop;
}
