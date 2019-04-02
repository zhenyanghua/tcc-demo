package com.leafyjava.tutorials.tccdemo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TccRequest {
    private List<Participant> participantLinks;
}
