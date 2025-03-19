package org.desp.sapphireMarket.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class PlayerIndividualPurchaseDto {
    private String user_id;
    private String uuid;
    private String MMOItem_id;
    private int bought;
}
