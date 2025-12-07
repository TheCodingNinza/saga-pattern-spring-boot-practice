package com.saurabhsameer.payments.service;

import com.saurabhsameer.core.dto.Payment;

import java.util.List;

public interface PaymentService {
    List<Payment> findAll();

    Payment process(Payment payment);
}
