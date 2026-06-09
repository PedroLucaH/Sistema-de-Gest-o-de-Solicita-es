package com.sgs.sgs_pagamentos.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Solicitante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // [cite: 17]

    @Column(nullable = false)
    private String nome; // [cite: 18]

    @Column(name = "cpf_cnpj", unique = true, nullable = false)
    private String cpfCnpj; // [cite: 19]
}