package com.sidework.payment.application.port.in;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Item {
    private final String id;
    private final String name;
    private final int price;
    private final String currency;
}
