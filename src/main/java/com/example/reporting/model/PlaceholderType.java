package com.example.reporting.model;

public enum PlaceholderType {
    TEXT,      // Полная замена текста {{key}} -> value
    IMAGE,     // Замена на изображение
    INSERT     // Вставка значения в существующий текст $key -> value
}