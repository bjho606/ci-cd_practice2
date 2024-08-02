package com.ssafy.meshroom.backend.domain.topic.game.trueorfalse.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Array;
import java.util.ArrayList;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TFInfoCreateRequest {
    String ovToken;

    ArrayList<String> truths;
    String false1;
}
