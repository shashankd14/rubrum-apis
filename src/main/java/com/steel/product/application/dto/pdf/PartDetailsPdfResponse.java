package com.steel.product.application.dto.pdf;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
public class PartDetailsPdfResponse {

    private Long id;
    private String partDetailsId;
    private Float targetWeight;
    private Float length;
    private Set<InstructionResponsePdfDto> instructions;
    private BigDecimal plannedYieldLossRatio;
    private BigDecimal actualYieldLossRatio;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartDetailsPdfResponse that = (PartDetailsPdfResponse) o;
        return id.equals(that.id) && partDetailsId.equals(that.partDetailsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, partDetailsId);
    }
}
