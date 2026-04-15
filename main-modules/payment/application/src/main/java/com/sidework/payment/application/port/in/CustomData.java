package com.sidework.payment.application.port.in;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomData {
    private String item;
    private int usedCredit;
}
