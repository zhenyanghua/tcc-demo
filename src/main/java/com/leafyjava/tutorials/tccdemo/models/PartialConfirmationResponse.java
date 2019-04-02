package com.leafyjava.tutorials.tccdemo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartialConfirmationResponse {
    private List<Participant> participantLinks;
}
