package com.steel.product.application.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "product_instruction_group")
public class InstructionGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "groupid")
    private int groupId;

    @Column(name = "instructioncount")
    private int instructionCount;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getInstructionCount() {
        return instructionCount;
    }

    public void setInstructionCount(int instructionCount) {
        this.instructionCount = instructionCount;
    }
}
